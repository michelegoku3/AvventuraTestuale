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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Finestra Swing principale che collega GUI, parser, gioco, database, timer e socket.
 */
public class InterfacciaGioco extends javax.swing.JFrame {

    private Gioco gioco;
    private Parser parser;
    private boolean dbConnesso = false;
    private static final int TEMPO_DECONTAMINAZIONE_TOTALE_SECONDI = 120;
    private int tempoImpiegatoDecontaminazione = -1;


    private TipoComando comandoInAttesaDiTarget = null;

    private static final Set<String> STOPWORDS_TARGET = new HashSet<>(Arrays.asList(
            "il", "lo", "la", "i", "gli", "le", "un", "uno", "una",
            "di", "a", "da", "in", "con", "su", "per", "tra", "fra"));


    private JTextArea txtConsole;
    private JTextField txtInput;
    private JButton btnInvia;
    private JList<String> listInventario;
    private DefaultListModel<String> modelInventario;
    private JLabel lblTimer;


    private JMenuBar menuBar;
    private JMenu menuPartita, menuRicerca, menuSocket;
    private JMenuItem itemNuovaPartita, itemSalvaPartita, itemCaricaPartita, itemCercaPunteggio, itemApriSpettatore, itemInfoSocket;


    private transient ThreadTimer timerRunnable;
    private transient Thread threadTimer;
    private transient ServerComandi serverSocket;
    private int portaSocketLocale = 8888;

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
            stampaTesto("DIARIO DI BORDO CHIMERA — SOGGETTO #12");
            stampaTesto("==================================================");


            stampaTesto(((LaMiaAvventura) gioco).getStanzaDescrizioneCompleta(gioco.getStanzaCorrente()));
            stampaTesto("");
            aggiornaInventarioGrafico();


            connettiAlDatabase();


            avviaServerSocketLocale(8888);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore avvio gioco: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }


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


        if (((LaMiaAvventura) gioco).isDialogoAttivo()) {
            String risDialogo = ((LaMiaAvventura) gioco).elaboraDialogo(input);
            stampaTesto(risDialogo);
            stampaTesto("");
            return;
        }


        if (((LaMiaAvventura) gioco).isFinaleAttivo()) {
            String risFinale = ((LaMiaAvventura) gioco).elaboraSceltaFinale(input);
            stampaTesto(risFinale);
            stampaTesto("");


            if ((input.equals("1") || input.equals("2") || input.equals("3")) && !((LaMiaAvventura) gioco).isFinaleAttivo()) {
                final int puntiFinali = calcolaPunteggioFinale(input);


                txtInput.setEnabled(false);
                btnInvia.setEnabled(false);


                SwingUtilities.invokeLater(() -> {
                    String nome = JOptionPane.showInputDialog(this,
                            "Hai completato 'Protocollo Chimera'!\nPunteggio finale: " + puntiFinali + " pt\nInserisci il tuo nome per registrarlo nella classifica H2:",
                            "Hall of Fame H2", JOptionPane.QUESTION_MESSAGE);

                    if (nome != null && !nome.trim().isEmpty()) {
                        try {
                            PunteggioDAO dao = new PunteggioDAO();
                            dao.aggiungiPunteggio(nome.trim(), puntiFinali);
                            JOptionPane.showMessageDialog(this, "Punteggio di " + puntiFinali + " punti salvato con successo!", "DB Aggiornato", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            System.err.println("Errore salvataggio punteggio finale: " + ex.getMessage());
                        }
                    }
                });
            }
            return;
        }


        if (comandoInAttesaDiTarget != null) {
            String inputLower = input.toLowerCase();

            if (inputLower.contains("annulla") || inputLower.contains("nessuno") || inputLower.contains("niente")) {
                comandoInAttesaDiTarget = null;
                stampaTesto("Azione annullata.");
                stampaTesto("");
                return;
            }


            ParserOutput testOutput = parser.parse(input, gioco.getComandi(), gioco.getStanzaCorrente().getOggetti(), gioco.getInventario().getElementi());
            if (testOutput != null && testOutput.getComando() != null &&
                (testOutput.getComando().getTipo() == TipoComando.NORD ||
                 testOutput.getComando().getTipo() == TipoComando.SUD ||
                 testOutput.getComando().getTipo() == TipoComando.EST ||
                 testOutput.getComando().getTipo() == TipoComando.OVEST)) {
                comandoInAttesaDiTarget = null;
            } else {
                String verboStr = getVerboString(comandoInAttesaDiTarget);
                input = verboStr + " " + input;
                comandoInAttesaDiTarget = null;
            }
        }


        if (input.contains("2041") && gioco.getStanzaCorrente().getId() == 6) {
            String ris = ((LaMiaAvventura) gioco).digitaCodiceCassaforte("2041");
            stampaTesto(ris);
            stampaTesto("");
            return;
        }

        ParserOutput output = parser.parse(input, gioco.getComandi(), gioco.getStanzaCorrente().getOggetti(), gioco.getInventario().getElementi());


        if (output == null) {
            stampaTesto("Richiesta non riconosciuta. Digita 'aiuto' per i comandi.");
            stampaTesto("");
            return;
        }


        if (output.getComando() != null && output.getOggetto() == null) {
            TipoComando tipoCmd = output.getComando().getTipo();
            String targetScritto = estraiTargetScritto(input);

            if (tipoCmd == TipoComando.PRENDI) {
                if (targetScritto != null) {
                    stampaTesto("Non vedo '" + targetScritto + "' in questa stanza.");
                    stampaTesto("");
                    return;
                }
                comandoInAttesaDiTarget = TipoComando.PRENDI;
                stampaTesto("Cosa vuoi prendere?");
                stampaTesto("");
                return;
            } else if (tipoCmd == TipoComando.LASCIA) {
                if (targetScritto != null) {
                    stampaTesto("Non hai '" + targetScritto + "' nell'inventario.");
                    stampaTesto("");
                    return;
                }
                comandoInAttesaDiTarget = TipoComando.LASCIA;
                stampaTesto("Cosa vuoi lasciare?");
                stampaTesto("");
                return;
            } else if (tipoCmd == TipoComando.USA) {
                if (targetScritto != null) {
                    stampaTesto("Non vedo '" + targetScritto + "' qui e non ce l'hai nell'inventario.");
                    stampaTesto("");
                    return;
                }
                comandoInAttesaDiTarget = TipoComando.USA;
                stampaTesto("Cosa vuoi usare?");
                stampaTesto("");
                return;
            } else if (tipoCmd == TipoComando.PARLA) {
                if (targetScritto != null) {
                    stampaTesto("Non vedo '" + targetScritto + "' in questa stanza.");
                    stampaTesto("");
                    return;
                }
                comandoInAttesaDiTarget = TipoComando.PARLA;
                stampaTesto("Con chi vorresti parlare?");
                stampaTesto("");
                return;
            }
        }


        if (comandoInAttesaDiTarget != null) {
            String inputLower = input.toLowerCase();
            if (inputLower.contains("annulla") || inputLower.contains("nessuno") || inputLower.contains("niente")) {
                comandoInAttesaDiTarget = null;
                stampaTesto("Azione annullata.");
                stampaTesto("");
                return;
            }
        }


        if (output.getComando() == null && output.getOggetto() != null) {
            stampaTesto("Capisco che vuoi interagire con '" + output.getOggetto().getNome() +
                        "', ma non so cosa fare. Prova a scrivere 'guarda " + output.getOggetto().getNome() + "' o 'usa " + output.getOggetto().getNome() + "'.");
            stampaTesto("");
            return;
        }


        if (output.getComando() == null && output.getInputInvalido() != null) {
            String sconosciuto = output.getInputInvalido();


            String suggerimento = parser.suggerisciComando(sconosciuto, gioco.getComandi());

            if (suggerimento != null) {
                stampaTesto("Non conosco il comando '" + sconosciuto + "'. Forse intendevi '" + suggerimento + "'?");
            } else {

                if (sconosciuto.equalsIgnoreCase("pin") || sconosciuto.equalsIgnoreCase("codice") || sconosciuto.equalsIgnoreCase("combinazione")) {
                    stampaTesto("Vuoi inserire un codice di sicurezza? Per sbloccare la cassaforte, usa il comando 'usa <codice> cassaforte' (es. 'usa 2041 cassaforte').");
                } else if (sconosciuto.equalsIgnoreCase("ragnatela") || sconosciuto.equalsIgnoreCase("ragnateòa") || sconosciuto.equalsIgnoreCase("polvere")) {
                    stampaTesto("È semplice polvere accumulata negli anni di lockdown. Non contiene indizi utili.");
                } else if (sconosciuto.equalsIgnoreCase("ciao") || sconosciuto.equalsIgnoreCase("salve")) {
                    stampaTesto("Non c'è tempo per i convenevoli. Sei intrappolato! Cerca un modo per fuggire o digita 'aiuto'.");
                } else {
                    stampaTesto("Non so come compiere l'azione '" + sconosciuto + "'. Digita 'aiuto' per l'elenco dei comandi disponibili.");
                }
            }
            stampaTesto("");
            return;
        }


        if (output.getComando() != null && output.getComando().getTipo() == TipoComando.SALVA) {
            sincronizzaTimerNelGioco();
        }

        String risposta = gioco.elaboraComando(output);
        stampaTesto(risposta);
        aggiornaInventarioGrafico();

        if (output.getComando() != null && output.getComando().getTipo() == TipoComando.CARICA) {
            txtInput.setEnabled(true);
            btnInvia.setEnabled(true);
            ripristinaTimerDaGioco();
            aggiornaInventarioGrafico();
        }


        if (gioco.getStanzaCorrente().getId() == 5 && timerRunnable == null && !((LaMiaAvventura) gioco).isCondottoPurificato()) {
            avviaTimer();
        }


        if (((LaMiaAvventura) gioco).isCondottoPurificato() && timerRunnable != null && !timerSpegneteNotificato) {
            int secondiRimasti = timerRunnable.getSecondiRimanenti();
            tempoImpiegatoDecontaminazione = Math.max(0, TEMPO_DECONTAMINAZIONE_TOTALE_SECONDI - secondiRimasti);
            ((LaMiaAvventura) gioco).aggiornaStatoTimerDecontaminazione(false, secondiRimasti, tempoImpiegatoDecontaminazione);
            timerRunnable.fermaTimer();
            stampaTesto("[TIMER] GAS NEUTRALIZZATO. Tempo impiegato: " + tempoImpiegatoDecontaminazione + " secondi. Aria purificata correttamente.");
            timerSpegneteNotificato = true;
        }

        stampaTesto("");
    }

    private String getVerboString(TipoComando tipo) {
        if (tipo == TipoComando.PRENDI) return "prendi";
        if (tipo == TipoComando.LASCIA) return "lascia";
        if (tipo == TipoComando.USA) return "usa";
        if (tipo == TipoComando.PARLA) return "parla";
        return "";
    }

    private int calcolaPunteggioFinale(String sceltaFinale) {
        int base;
        if (sceltaFinale.equals("1")) base = 500;
        else if (sceltaFinale.equals("2")) base = 400;
        else base = 100;

        int tempoImpiegato = tempoImpiegatoDecontaminazione >= 0
                ? tempoImpiegatoDecontaminazione
                : TEMPO_DECONTAMINAZIONE_TOTALE_SECONDI;
        int penalitaTempo = tempoImpiegato * 2;
        int punti = Math.max(0, base - penalitaTempo);

        stampaTesto("[PUNTEGGIO] Base finale: " + base
                + " | Tempo gas: " + tempoImpiegato + "s"
                + " | Penalita' tempo: -" + penalitaTempo
                + " | Totale: " + punti + " pt");
        return punti;
    }


    private String estraiTargetScritto(String input) {
        if (input == null) return null;
        String[] tokens = input.toLowerCase().trim().split("\\s+");
        if (tokens.length <= 1) return null;

        StringBuilder target = new StringBuilder();
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty() || STOPWORDS_TARGET.contains(token)) {
                continue;
            }
            if (target.length() > 0) target.append(' ');
            target.append(token);
        }
        return target.length() == 0 ? null : target.toString();
    }

    public void stampaTesto(String testo) {
        if (testo != null && testo.startsWith("[CLEAR_CHAT]")) {
            txtConsole.setText("");
            testo = testo.replace("[CLEAR_CHAT]", "");
        }
        txtConsole.append(testo + "\n");
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());


        if (serverSocket != null) {
            serverSocket.trasmettiAClient(testo);
        }
    }

    private void aggiornaInventarioGrafico() {
        modelInventario.clear();

        gioco.getInventario().getElementi().stream()
                .map(Oggetto::getNome)
                .forEach(modelInventario::addElement);
    }

    private void connettiAlDatabase() {
        try {
            DatabaseManager.inizializzaDatabase();
            dbConnesso = true;
            System.out.println("[DB] Database relazionale H2 connesso ed inizializzato con successo.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore connessione DB: " + e.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void apriDialogNuovoGiocatore() {
        DialogInserimento dialog = new DialogInserimento(this);
        dialog.setVisible(true);
    }

    private void apriDialogCercaPunteggio() {
        DialogRicerca dialog = new DialogRicerca(this);
        dialog.setVisible(true);
    }

    private int trovaPortaDisponibile(int portaBase) {
        int porta = Math.max(1, portaBase);
        while (porta <= 65535) {
            try (ServerSocket test = new ServerSocket(porta)) {
                return porta;
            } catch (IOException ex) {
                porta++;
            }
        }
        throw new IllegalStateException("Nessuna porta socket disponibile da " + portaBase + " in poi.");
    }

    private void avviaServerSocketLocale(int portaBase) {
        portaSocketLocale = trovaPortaDisponibile(portaBase);
        serverSocket = new ServerComandi(portaSocketLocale, this);
        serverSocket.start();
        System.out.println("[Socket] Server spettatore locale attivo sulla porta " + portaSocketLocale + ".");
    }

    private void mostraInfoSocket() {
        JOptionPane.showMessageDialog(this,
                "Server spettatore locale attivo sulla porta " + portaSocketLocale + ".\n" +
                "Per vedere questa partita da un'altra istanza: Socket > Apri spettatore e inserisci questa porta.",
                "Socket", JOptionPane.INFORMATION_MESSAGE);
    }

    private void apriSpettatoreSocket() {
        String inputPorta = JOptionPane.showInputDialog(this,
                "Inserisci la porta della partita da osservare:",
                String.valueOf(8888));
        if (inputPorta == null || inputPorta.trim().isEmpty()) {
            return;
        }
        int porta;
        try {
            porta = Integer.parseInt(inputPorta.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Porta non valida.", "Socket", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Spettatore Socket - porta " + porta, false);
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(Color.BLACK);
        area.setForeground(new Color(255, 128, 0));
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        dialog.add(new JScrollPane(area), BorderLayout.CENTER);
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        Thread viewerThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", porta);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                SwingUtilities.invokeLater(() -> area.append("[Socket] Connesso come spettatore alla porta " + porta + "\n"));
                String line;
                while ((line = in.readLine()) != null) {
                    final String msg = line;
                    SwingUtilities.invokeLater(() -> {
                        area.append(msg + "\n");
                        area.setCaretPosition(area.getDocument().getLength());
                    });
                }
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> area.append("[Socket] Connessione chiusa o non disponibile: " + ex.getMessage() + "\n"));
            }
        }, "Socket-Viewer-" + porta);
        viewerThread.setDaemon(true);
        viewerThread.start();
    }

    private void nuovaPartita() {
        if (timerRunnable != null) {
            timerRunnable.fermaTimer();
            timerRunnable = null;
            threadTimer = null;
        }
        try {
            gioco = new LaMiaAvventura();
            gioco.inizializza();
            comandoInAttesaDiTarget = null;
            tempoImpiegatoDecontaminazione = -1;
            timerSpegneteNotificato = false;
            lblTimer.setText("Timer: Spento");
            txtInput.setEnabled(true);
            btnInvia.setEnabled(true);
            txtConsole.setText("");
            stampaTesto("DIARIO DI BORDO CHIMERA — SOGGETTO #12");
            stampaTesto("==================================================");
            stampaTesto(((LaMiaAvventura) gioco).getStanzaDescrizioneCompleta(gioco.getStanzaCorrente()));
            stampaTesto("");
            aggiornaInventarioGrafico();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore nuova partita: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eseguiComandoDaMenu(String comando) {
        txtInput.setText(comando);
        elaboraInputUtente();
    }

    private void sincronizzaTimerNelGioco() {
        LaMiaAvventura avventura = (LaMiaAvventura) gioco;
        if (avventura.isCondottoPurificato()) {
            int rimasti = timerRunnable != null ? timerRunnable.getSecondiRimanenti() : Math.max(0, TEMPO_DECONTAMINAZIONE_TOTALE_SECONDI - Math.max(0, tempoImpiegatoDecontaminazione));
            avventura.aggiornaStatoTimerDecontaminazione(false, rimasti, tempoImpiegatoDecontaminazione);
        } else if (timerRunnable != null) {
            avventura.aggiornaStatoTimerDecontaminazione(true, timerRunnable.getSecondiRimanenti(), -1);
        } else {
            avventura.aggiornaStatoTimerDecontaminazione(false, TEMPO_DECONTAMINAZIONE_TOTALE_SECONDI, -1);
        }
    }

    private void ripristinaTimerDaGioco() {
        if (timerRunnable != null) {
            timerRunnable.fermaTimer();
            timerRunnable = null;
            threadTimer = null;
        }

        LaMiaAvventura avventura = (LaMiaAvventura) gioco;
        timerSpegneteNotificato = avventura.isCondottoPurificato();
        tempoImpiegatoDecontaminazione = avventura.getTempoImpiegatoDecontaminazione();

        if (avventura.isTimerDecontaminazioneAttivo() && !avventura.isCondottoPurificato()) {
            int secondi = avventura.getSecondiDecontaminazioneRimanenti();
            avviaTimerDaSecondi(secondi);
        } else if (avventura.isCondottoPurificato() && tempoImpiegatoDecontaminazione >= 0) {
            lblTimer.setText("Timer: Gas neutralizzato in " + tempoImpiegatoDecontaminazione + "s");
        } else {
            lblTimer.setText("Timer: Spento");
        }
    }

    private boolean timerSpegneteNotificato = false;

    public void avviaTimer() {
        avviaTimerDaSecondi(TEMPO_DECONTAMINAZIONE_TOTALE_SECONDI);
    }

    private void avviaTimerDaSecondi(int secondi) {
        if (timerRunnable != null) {
            timerRunnable.fermaTimer();
        }
        timerRunnable = ThreadTimer.daSecondi(secondi, this);
        threadTimer = new Thread(timerRunnable, "Thread-Timer-Decontaminazione");
        threadTimer.start();
        ((LaMiaAvventura) gioco).aggiornaStatoTimerDecontaminazione(true, secondi, -1);
        aggiornaLabelTimer(formattaTempo(secondi));
    }

    private String formattaTempo(int secondiTotali) {
        int secondiSicuri = Math.max(0, secondiTotali);
        return String.format("%02d:%02d", secondiSicuri / 60, secondiSicuri % 60);
    }

    public void aggiornaLabelTimer(String tempo) {
        lblTimer.setText("Timer: " + tempo);
    }

    public void gestisciScadenzaTempo() {


        if (((LaMiaAvventura) gioco).isCondottoPurificato()) {
            return;
        }
        stampaTesto("\n[GAME OVER] BOOM! Le fiamme del sistema di decontaminazione termica divampano nella stanza! Sei stato incenerito.");
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


        menuBar = new JMenuBar();

        menuPartita = new JMenu("Partita");
        itemNuovaPartita = new JMenuItem("Nuova");
        itemNuovaPartita.addActionListener(e -> nuovaPartita());
        itemSalvaPartita = new JMenuItem("Salva");
        itemSalvaPartita.addActionListener(e -> eseguiComandoDaMenu("salva"));
        itemCaricaPartita = new JMenuItem("Carica");
        itemCaricaPartita.addActionListener(e -> eseguiComandoDaMenu("carica"));
        menuPartita.add(itemNuovaPartita);
        menuPartita.addSeparator();
        menuPartita.add(itemSalvaPartita);
        menuPartita.add(itemCaricaPartita);

        menuRicerca = new JMenu("Classifica");
        itemCercaPunteggio = new JMenuItem("Cerca Punteggi");
        itemCercaPunteggio.addActionListener(e -> apriDialogCercaPunteggio());
        menuRicerca.add(itemCercaPunteggio);

        menuSocket = new JMenu("Socket");
        itemApriSpettatore = new JMenuItem("Apri spettatore");
        itemApriSpettatore.addActionListener(e -> apriSpettatoreSocket());
        itemInfoSocket = new JMenuItem("Info porta locale");
        itemInfoSocket.addActionListener(e -> mostraInfoSocket());
        menuSocket.add(itemApriSpettatore);
        menuSocket.add(itemInfoSocket);

        menuBar.add(menuPartita);
        menuBar.add(menuRicerca);
        menuBar.add(menuSocket);
        setJMenuBar(menuBar);


        txtConsole = new JTextArea();
        txtConsole.setEditable(false);
        txtConsole.setBackground(Color.BLACK);
        txtConsole.setForeground(new Color(255, 128, 0));
        txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtConsole.setLineWrap(true);
        txtConsole.setWrapStyleWord(true);
        JScrollPane scrollConsole = new JScrollPane(txtConsole);


        JPanel panelInput = new JPanel(new BorderLayout());
        txtInput = new JTextField();
        txtInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        btnInvia = new JButton("Invia");
        panelInput.add(txtInput, BorderLayout.CENTER);
        panelInput.add(btnInvia, BorderLayout.EAST);


        JPanel panelLaterale = new JPanel(new BorderLayout());
        panelLaterale.setPreferredSize(new Dimension(220, 0));

        lblTimer = new JLabel("Timer: Spento", SwingConstants.CENTER);
        lblTimer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblTimer.setFont(new Font("Arial", Font.BOLD, 14));
        lblTimer.setOpaque(true);
        lblTimer.setBackground(new Color(240, 240, 240));

        listInventario = new JList<>();
        JScrollPane scrollInventario = new JScrollPane(listInventario);
        scrollInventario.setBorder(BorderFactory.createTitledBorder("Inventario Clone"));

        panelLaterale.add(lblTimer, BorderLayout.NORTH);
        panelLaterale.add(scrollInventario, BorderLayout.CENTER);

        JPanel panelTerminale = new JPanel(new BorderLayout());
        panelTerminale.add(scrollConsole, BorderLayout.CENTER);
        panelTerminale.add(panelInput, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelTerminale, BorderLayout.CENTER);
        getContentPane().add(panelLaterale, BorderLayout.EAST);
    }
}
