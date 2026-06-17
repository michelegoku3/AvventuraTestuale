package com.mycompany.avventuratestuale.ui;

import com.mycompany.avventuratestuale.core.Gioco;
import com.mycompany.avventuratestuale.core.Parser;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.core.ThreadTimer;
import com.mycompany.avventuratestuale.core.TipoComando;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.model.Oggetto;
import com.mycompany.avventuratestuale.database.DatabaseManager;
import com.mycompany.avventuratestuale.database.PunteggioDAO;
import com.mycompany.avventuratestuale.socket.ServerComandi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InterfacciaGioco extends javax.swing.JFrame {

    private Gioco gioco;
    private Parser parser;
    private boolean dbConnesso = false;

    // Memoria contestuale per comandi in attesa di bersaglio [Risolve il problema del "prendi -> tessera"]
    private TipoComando comandoInAttesaDiTarget = null;

    // Componenti Swing [Lezioni/16 - Swing.pdf, Slide 16-24]
    private JTextArea txtConsole;
    private JTextField txtInput;
    private JButton btnInvia;
    private JList<String> listInventario;
    private DefaultListModel<String> modelInventario;
    private JLabel lblTimer;
    
    // Elementi dei Menu
    private JMenuBar menuBar;
    private JMenu menuDB, menuInserimento, menuRicerca;
    private JMenuItem itemConnetti, itemNuovoGiocatore, itemCercaPunteggio;

    // Thread di supporto (marcati transient per non essere serializzati!) [Lezioni/10 - Slide 37-38]
    private transient ThreadTimer timerRunnable;
    private transient Thread threadTimer;
    private transient ServerComandi serverSocket;

    public InterfacciaGioco() {
        initComponents();
        personalizzaInizializzazione();
    }

    private void personalizzaInizializzazione() {
        parser = new Parser();
        gioco = new LaMiaAvventura();
        modelInventario = new DefaultListModel<>();
        listInventario.setModel(modelInventario);

        try {
            gioco.inizializza();
            stampaTesto("🎮 DIARIO DI BORDO CHIMERA — SOGGETTO #12");
            stampaTesto("==================================================");
            
            // Stampa la descrizione estesa completa con oggetti e uscite disponibili all'avvio!
            stampaTesto(((LaMiaAvventura) gioco).getStanzaDescrizioneCompleta(gioco.getStanzaCorrente()));
            stampaTesto("");
            aggiornaInventarioGrafico();
            
            // Avvia il server socket in background sulla porta 8888 [Lezioni/14 / Slide 13]
            serverSocket = new ServerComandi(8888, this);
            serverSocket.start();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore avvio gioco: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }

        // Gestione INVIO conforme a [Esercizi/Esercizio Lab.pdf, p. 3 - Messenger]
        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    elaboraInputUtente();
                }
            }
        });

        btnInvia.addActionListener(e -> elaboraInputUtente());
    }

    private void elaboraInputUtente() {
        String input = txtInput.getText().trim();
        if (input.isEmpty()) return;

        txtInput.setText("");
        stampaTesto("> " + input);

        // 1. SE C'È UN DIALOGO ATTIVO CON PROMETEO, INTERCETTA E ELABORA
        if (((LaMiaAvventura) gioco).isDialogoAttivo()) {
            String risDialogo = ((LaMiaAvventura) gioco).elaboraDialogo(input);
            stampaTesto(risDialogo);
            stampaTesto("");
            return;
        }

        // 2. SE C'È IL FINALE ATTIVO, GESTISCI LA SCELTA DELL'UTENTE
        if (((LaMiaAvventura) gioco).isFinaleAttivo()) {
            String risFinale = ((LaMiaAvventura) gioco).elaboraSceltaFinale(input);
            stampaTesto(risFinale);
            stampaTesto("");
            
            // Se l'input corrisponde ad un finale valido (1-4)
            if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")) {
                int puntiLog = 0;
                switch (input) {
                    case "1" -> puntiLog = 500;
                    case "2" -> puntiLog = 400;
                    case "3" -> puntiLog = 300;
                    case "4" -> puntiLog = 100;
                }
                
                final int puntiFinali = puntiLog;
                
                // Disabilita gli input per fine partita
                txtInput.setEnabled(false);
                btnInvia.setEnabled(false);
                
                // Chiede il nome del giocatore in modo visibile Swing per registrarlo su DB H2 [Esercizio Lab.pdf, p. 4]
                SwingUtilities.invokeLater(() -> {
                    String nome = JOptionPane.showInputDialog(this, 
                            "Hai completato 'Protocollo Chimera'!\nInserisci il tuo nome per registrare il tuo punteggio nella classifica H2:", 
                            "Hall of Fame H2", JOptionPane.QUESTION_MESSAGE);
                    
                    if (nome != null && !nome.trim().isEmpty()) {
                        try {
                            PunteggioDAO dao = new PunteggioDAO();
                            dao.aggiungiPunteggio(nome.trim(), puntiFinali);
                            JOptionPane.showMessageDialog(this, "Punteggio di " + puntiFinali + "pt salvato con successo!", "DB Aggiornato", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            System.err.println("Errore salvataggio punteggio finale: " + ex.getMessage());
                        }
                    }
                });
            }
            return;
        }

        // 3. SE C'È UN COMANDO IN ATTESA DI TARGET (Memoria contestuale sblocca clunkiness!)
        if (comandoInAttesaDiTarget != null) {
            // Se l'input inserito è un comando di direzione, annulliamo la memoria contestuale e ci spostiamo normalmente
            ParserOutput testOutput = parser.parse(input, gioco.getComandi(), gioco.getStanzaCorrente().getOggetti(), gioco.getInventario());
            if (testOutput != null && testOutput.getComando() != null && 
                (testOutput.getComando().getTipo() == TipoComando.NORD ||
                 testOutput.getComando().getTipo() == TipoComando.SUD ||
                 testOutput.getComando().getTipo() == TipoComando.EST ||
                 testOutput.getComando().getTipo() == TipoComando.OVEST)) {
                comandoInAttesaDiTarget = null; // Resetta memoria
            } else {
                // Riscrive l'input accorpando il verbo in attesa col bersaglio (es: "tessera" -> "prendi tessera")
                String verboStr = getVerboString(comandoInAttesaDiTarget);
                input = verboStr + " " + input;
                comandoInAttesaDiTarget = null; // Resetta memoria
            }
        }

        // 4. Se l'utente digita il codice cassaforte (es. "usa 2041 cassaforte" o "2041")
        if (input.contains("2041") && gioco.getStanzaCorrente().getId() == 6) {
            String ris = ((LaMiaAvventura) gioco).digitaCodiceCassaforte("2041");
            stampaTesto(ris);
            stampaTesto("");
            return;
        }

        ParserOutput output = parser.parse(input, gioco.getComandi(), gioco.getStanzaCorrente().getOggetti(), gioco.getInventario());

        // 5. SE IL PARSER NON HA TROVATO NULLA (INPUT VUOTO O GRAVE ERRORE)
        if (output == null) {
            stampaTesto("Richiesta non riconosciuta. Digita 'aiuto' per i comandi.");
            stampaTesto("");
            return;
        }

        // 6. GESTIONE DEI VERBI COMPLETI MA INCOMPLETI (Innescano la memoria contestuale!)
        if (output.getComando() != null && output.getOggetto() == null) {
            TipoComando tipoCmd = output.getComando().getTipo();
            if (tipoCmd == TipoComando.PRENDI) {
                comandoInAttesaDiTarget = TipoComando.PRENDI;
                stampaTesto("Cosa vuoi prendere?");
                stampaTesto("");
                return;
            } else if (tipoCmd == TipoComando.LASCIA) {
                comandoInAttesaDiTarget = TipoComando.LASCIA;
                stampaTesto("Cosa vuoi lasciare?");
                stampaTesto("");
                return;
            } else if (tipoCmd == TipoComando.USA) {
                comandoInAttesaDiTarget = TipoComando.USA;
                stampaTesto("Cosa vuoi usare?");
                stampaTesto("");
                return;
            } else if (tipoCmd == TipoComando.PARLA) {
                comandoInAttesaDiTarget = TipoComando.PARLA;
                stampaTesto("Con chi vorresti parlare?");
                stampaTesto("");
                return;
            }
        }

        // 7. SE IL COMANDO (VERBO) È NULLO MA È STATO RICONOSCIUTO UN OGGETTO (es: "tessera", "silos", "libreria")
        if (output.getComando() == null && output.getOggetto() != null) {
            stampaTesto("Capisco che vuoi interagire con '" + output.getOggetto().getNome() + 
                        "', ma non so cosa fare. Prova a scrivere 'guarda " + output.getOggetto().getNome() + "' o 'usa " + output.getOggetto().getNome() + "'.");
            stampaTesto("");
            return;
        }

        // 8. SE IL VERBO È NULLO E SI TRATTA DI UN TYPO SCONOSCIUTO (Algoritmo Levenshtein Distance dinamico d'esame!)
        if (output.getComando() == null && output.getInputInvalido() != null) {
            String sconosciuto = output.getInputInvalido();
            
            // Interroga l'algoritmo di Levenshtein del parser per cercare il sinonimo più vicino in modo del tutto dinamico!
            String suggerimento = parser.suggerisciComando(sconosciuto, gioco.getComandi());
            
            if (suggerimento != null) {
                stampaTesto("Non conosco il comando '" + sconosciuto + "'. Forse intendevi '" + suggerimento + "'?");
            } else {
                // Fallback statici per commenti o oggetti estranei
                if (sconosciuto.equalsIgnoreCase("pin") || sconosciuto.equalsIgnoreCase("codice") || sconosciuto.equalsIgnoreCase("combinazione")) {
                    stampaTesto("Vuoi inserire un codice di sicurezza? Per sbloccare la cassaforte, usa il comando 'usa <codice> cassaforte' (es. 'usa 2041 cassaforte').");
                } else if (sconosciuto.equalsIgnoreCase("ragnatela") || sconosciuto.equalsIgnoreCase("ragnateòa") || sconosciuto.equalsIgnoreCase("polvere")) {
                    stampaTesto("È semplice polvere accumulata negli anni di lockdown. Non contiene indizi utili.");
                } else if (sconosciuto.equalsIgnoreCase("ciao") || sconosciuto.equalsIgnoreCase("salve")) {
                    stampaTesto("Non c'è tempo per i convenevoli. Sei intrappolato! Cerca un modo per fuggire o digita 'aiuto'.");
                } else {
                    stampaTesto("Non so come compiere l'action '" + sconosciuto + "'. Digita 'aiuto' per l'elenco dei comandi disponibili.");
                }
            }
            stampaTesto("");
            return;
        }

        // 9. Elaborazione Standard del Comando
        String risposta = gioco.elaboraComando(output);
        stampaTesto(risposta);
        aggiornaInventarioGrafico();
        
        // Attivazione asincrona del timer se si entra nella stanza di decontaminazione (5)
        if (gioco.getStanzaCorrente().getId() == 5 && timerRunnable == null && !((LaMiaAvventura) gioco).isCondottoPurificato()) {
            avviaTimer();
        }

        // Arresta il timer se il condotto è purificato
        if (((LaMiaAvventura) gioco).isCondottoPurificato() && timerRunnable != null) {
            timerRunnable.fermaTimer();
            stampaTesto("⏰ SISTEMA DI RISCALDAMENTO DISATTIVATO. Aria purificata correttamente.");
        }
        
        stampaTesto("");
    }

    private String getVerboString(TipoComando tipo) {
        switch (tipo) {
            case PRENDI -> { return "prendi"; }
            case LASCIA -> { return "lascia"; }
            case USA -> { return "usa"; }
            case PARLA -> { return "parla"; }
            default -> { return ""; }
        }
    }

    public void stampaTesto(String testo) {
        txtConsole.append(testo + "\n");
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
        
        // Trasmette l'eco del testo in broadcast a tutti i terminali remoti connessi [Lezioni/14 - Slide 13]
        if (serverSocket != null) {
            serverSocket.trasmettiAClient(testo);
        }
    }

    private void aggiornaInventarioGrafico() {
        modelInventario.clear();
        // Uso di Stream Pipeline e Lambda per aggiornare la JList [Lezioni/16 - Swing, Slide 48]
        gioco.getInventario().stream()
                .map(Oggetto::getNome)
                .forEach(modelInventario::addElement);
    }

    private void connettiAlDatabase() {
        try {
            DatabaseManager.inizializzaDatabase();
            dbConnesso = true;
            stampaTesto("🗄️ Database relazionale H2 Connesso ed Inizializzato con successo.");
            
            // Abilita i menu dopo la connessione avvenuta [Esercizi/Esercizio Lab.pdf, p. 4]
            menuInserimento.setEnabled(true);
            menuRicerca.setEnabled(true);
            itemConnetti.setEnabled(false); // Disabilita riconnessioni
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore connessione DB: " + e.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void apriDialogNuovoGiocatore() {
        DialogInserimento dialog = new DialogInserimento(this);
        dialog.setVisible(true); // Modale
    }

    private void apriDialogCercaPunteggio() {
        DialogRicerca dialog = new DialogRicerca(this);
        dialog.setVisible(true); // Modale
    }

    public void avviaTimer() {
        timerRunnable = new ThreadTimer(2, this); // 2 minuti di tempo
        threadTimer = new Thread(timerRunnable, "Thread-Timer-Decontaminazione");
        threadTimer.start(); // Avvia thread concorrente [Lezioni/15 - Slide 7]
    }

    public void aggiornaLabelTimer(String tempo) {
        lblTimer.setText("⏰ Timer: " + tempo);
    }

    public void gestisciScadenzaTempo() {
        stampaTesto("\n💥💥💥 BOOM! Le fiamme del sistema di decontaminazione termica divampano nella stanza! Sei stato incenerito. 💥💥💥");
        txtInput.setEnabled(false);
        btnInvia.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Game Over! Sei stato incenerito!", "Sconfitta", JOptionPane.ERROR_MESSAGE);
    }

    public void eseguiComandoDaRemoto(String comando) {
        txtInput.setText(comando);
        elaboraInputUtente();
    }

    private void initComponents() {
        setTitle("Protocollo Chimera — Avventura Testuale MAP");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Barra dei Menu conforme a [Esercizi/Esercizio Lab.pdf, p. 4]
        menuBar = new JMenuBar();
        
        menuDB = new JMenu("DB");
        itemConnetti = new JMenuItem("Connetti");
        itemConnetti.addActionListener(e -> connettiAlDatabase());
        menuDB.add(itemConnetti);

        menuInserimento = new JMenu("Inserimento");
        menuInserimento.setEnabled(false); // Disabilitato fino a connessione DB [Esercizi/Esercizio Lab.pdf, p. 4]
        itemNuovoGiocatore = new JMenuItem("Salva Punteggio");
        itemNuovoGiocatore.addActionListener(e -> apriDialogNuovoGiocatore());
        menuInserimento.add(itemNuovoGiocatore);

        menuRicerca = new JMenu("Ricerca");
        menuRicerca.setEnabled(false); // Disabilitato fino a connessione DB [Esercizi/Esercizio Lab.pdf, p. 4]
        itemCercaPunteggio = new JMenuItem("Cerca Giocatori");
        itemCercaPunteggio.addActionListener(e -> apriDialogCercaPunteggio());
        menuRicerca.add(itemCercaPunteggio);

        menuBar.add(menuDB);
        menuBar.add(menuInserimento);
        menuBar.add(menuRicerca);
        setJMenuBar(menuBar);

        // JTextArea console
        txtConsole = new JTextArea();
        txtConsole.setEditable(false);
        txtConsole.setBackground(Color.BLACK);
        txtConsole.setForeground(new Color(255, 128, 0)); // Colore Arancio Ambra di un vecchio terminale
        txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtConsole.setLineWrap(true);
        txtConsole.setWrapStyleWord(true);
        JScrollPane scrollConsole = new JScrollPane(txtConsole);

        // JTextField input e bottone
        JPanel panelInput = new JPanel(new BorderLayout());
        txtInput = new JTextField();
        txtInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        btnInvia = new JButton("Invia");
        panelInput.add(txtInput, BorderLayout.CENTER);
        panelInput.add(btnInvia, BorderLayout.EAST);

        // Pannello Laterale (Inventario JList e Timer)
        JPanel panelLaterale = new JPanel(new BorderLayout());
        panelLaterale.setPreferredSize(new Dimension(220, 0));
        
        lblTimer = new JLabel("⏰ Timer: Spento", SwingConstants.CENTER);
        lblTimer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblTimer.setFont(new Font("Arial", Font.BOLD, 14));
        lblTimer.setOpaque(true);
        lblTimer.setBackground(new Color(240, 240, 240));
        
        listInventario = new JList<>();
        JScrollPane scrollInventario = new JScrollPane(listInventario);
        scrollInventario.setBorder(BorderFactory.createTitledBorder("Inventario Clone"));

        panelLaterale.add(lblTimer, BorderLayout.NORTH);
        panelLaterale.add(scrollInventario, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollConsole, BorderLayout.CENTER);
        getContentPane().add(panelInput, BorderLayout.SOUTH);
        getContentPane().add(panelLaterale, BorderLayout.EAST);
    }
}
