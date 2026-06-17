# Capitolo 2: Interfaccia Grafica con Swing
### Obiettivo: Progettare un'interfaccia utente grafica avanzata e conforme alle lezioni ed esercitazioni del corso, integrando JMenuBar, JMenu, JDialogs modali di inserimento/ricerca, JCheckBox, JList con DefaultListModel e AbstractAction.

Il criterio 7 di valutazione d'esame prevede l'**utilizzo delle SWING** `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`. Le lezioni del corso `[Lezioni/16 - JAVA - Swing.pdf]` e l'esercitazione pratica di laboratorio `[Esercizi/Esercizio Lab.pdf, p. 2-5]` mostrano in modo chiaro come strutturare un'interfaccia Swing professionale e ricca di funzionalità.

---

## 1. Architettura dei Componenti Swing della Nostra GUI
In conformità con la teoria del corso `[Lezioni/16 - JAVA - Swing.pdf, Slide 16-24]` e con i requisiti dell'esercitazione `[Esercizi/Esercizio Lab.pdf, p. 4-5]`, progetteremo un'interfaccia grafica composta da:
1. **JFrame principale (Top-Level Container)** `[Lezioni/16 - JAVA - Swing.pdf, Slide 16]`: La finestra principale che ospita la console di gioco.
2. **JMenuBar e JMenu** `[Lezioni/16 - JAVA - Swing.pdf, Slide 19]`: Una barra dei menu in alto con i menu `Gioco`, `Inserimento` e `Ricerca`.
3. **JDialog Modale** `[Lezioni/16 - JAVA - Swing.pdf, Slide 49-51]`: Finestre di dialogo temporanee dipendenti dal frame principale per inserire un record giocatore o cercare punteggi nel database relazionale.
4. **JList e DefaultListModel** `[Lezioni/16 - JAVA - Swing.pdf, Slide 44-48]`: Componenti integrati per mostrare in modo interattivo e con scorrimento (`JScrollPane`) l'inventario del giocatore e i risultati delle ricerche.
5. **JCheckBox** `[Lezioni/16 - JAVA - Swing.pdf, Slide 24]`: Utilizzato nei form per selezionare opzioni booleane (es: attivazione di bonus o modalità di gioco).
6. **AbstractAction** `[Lezioni/16 - JAVA - Swing.pdf, Slide 68]`: Per centralizzare la logica di controllo (es. "Esci" o "Connetti") associandola contemporaneamente sia alle voci di menu sia alla pressione di combinazioni di tasti.

---

## 2. Implementazione della Classe Principale `InterfacciaGioco.java`

Ecco il codice del frame principale integrato con la barra dei menu e la disabilitazione automatica delle funzionalità se il database non è connesso, esattamente come richiesto nell'esercitazione `[Esercizi/Esercizio Lab.pdf, p. 4]`.

```java
package it.uniba.map.gioco.ui;

import it.uniba.map.gioco.core.Gioco;
import it.uniba.map.gioco.core.Parser;
import it.uniba.map.gioco.core.ParserOutput;
import it.uniba.map.gioco.impl.LaMiaAvventura;
import it.uniba.map.gioco.model.Oggetto;
import it.uniba.map.gioco.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InterfacciaGioco extends javax.swing.JFrame {

    private Gioco gioco;
    private Parser parser;
    private boolean dbConnesso = false;

    // Componenti Swing [Lezioni/16 - JAVA - Swing.pdf, Slide 16-24]
    private JTextArea txtConsole;
    private JTextField txtInput;
    private JButton btnInvia;
    private JList<String> listInventario;
    private DefaultListModel<String> modelInventario;
    private JLabel lblTimer;
    
    // Elementi dei Menu
    private JMenuBar menuBar;
    private JMenu menuGioco, menuInserimento, menuRicerca;
    private JMenuItem itemConnetti, itemNuovoGiocatore, itemCercaPunteggio;

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
            stampaTesto("🎮 BENVENUTO NEL GIOCO!");
            stampaTesto("==================================================");
            stampaTesto(gioco.getStanzaCorrente().getDescrizione());
            stampaTesto("");
            aggiornaInventarioGrafico();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore avvio gioco: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }

        // Criterio 7 Opzione 1: gestione della pressione del tasto INVIO [Esercizi/Esercizio Lab.pdf, p. 3]
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

        ParserOutput output = parser.parse(input, gioco.getComandi(), gioco.getStanzaCorrente().getOggetti(), gioco.getInventario());

        if (output == null || output.getComando() == null) {
            stampaTesto("Non ho capito questo comando. Digita 'aiuto' per la lista comandi.");
        } else {
            String risposta = gioco.elaboraComando(output);
            stampaTesto(risposta);
            aggiornaInventarioGrafico();
        }
        stampaTesto("");
    }

    public void stampaTesto(String testo) {
        txtConsole.append(testo + "\n");
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }

    private void aggiornaInventarioGrafico() {
        modelInventario.clear();
        // Uso di defaultListModel conforme a [Lezioni/16 - JAVA - Swing.pdf, Slide 48]
        gioco.getInventario().stream()
                .map(Oggetto::getNome)
                .forEach(modelInventario::addElement);
    }

    // Gestione della connessione al DB tramite menù conforme a [Esercizi/Esercizio Lab.pdf, p. 4]
    private void connettiAlDatabase() {
        try {
            DatabaseManager.inizializzaDatabase();
            dbConnesso = true;
            stampaTesto("🗄️ Database H2 Connesso con successo.");
            
            // Abilita i menù dopo la connessione avvenuta [Esercizi/Esercizio Lab.pdf, p. 4]
            menuInserimento.setEnabled(true);
            menuRicerca.setEnabled(true);
            itemConnetti.setEnabled(false); // Disabilita per evitare riconnessioni
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore connessione DB: " + e.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Apre la Dialog modale per inserire un record utente [Esercizi/Esercizio Lab.pdf, p. 4]
    private void apriDialogNuovoGiocatore() {
        DialogInserimento dialog = new DialogInserimento(this);
        dialog.setVisible(true); // Bloccante fino alla chiusura (Modale)
    }

    // Apre la Dialog modale per cercare un punteggio [Esercizi/Esercizio Lab.pdf, p. 4-5]
    private void apriDialogCercaPunteggio() {
        DialogRicerca dialog = new DialogRicerca(this);
        dialog.setVisible(true);
    }

    public void aggiornaLabelTimer(String tempo) {
        lblTimer.setText("⏰ Tempo Rimanente: " + tempo);
    }

    public void gestisciScadenzaTempo() {
        stampaTesto("\n💥 TEMPO SCADUTO! Hai perso!");
        txtInput.setEnabled(false);
        btnInvia.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Game Over!", "Sconfitta", JOptionPane.ERROR_MESSAGE);
    }

    public void eseguiComandoDaRemoto(String comando) {
        txtInput.setText(comando);
        elaboraInputUtente();
    }

    private void initComponents() {
        // Layout, console, input
        setTitle("Fuga Dal Labirinto - MAP");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Barra dei Menu [Lezioni/16 - JAVA - Swing.pdf, Slide 19]
        menuBar = new JMenuBar();
        
        menuGioco = new JMenu("DB"); // Voce di menù DB
        itemConnetti = new JMenuItem("Connetti");
        itemConnetti.addActionListener(e -> connettiAlDatabase());
        menuGioco.add(itemConnetti);

        menuInserimento = new JMenu("Inserimento");
        menuInserimento.setEnabled(false); // Disabilitato fino a connessione DB [Esercizi/Esercizio Lab.pdf, p. 4]
        itemNuovoGiocatore = new JMenuItem("Nuovo Giocatore");
        itemNuovoGiocatore.addActionListener(e -> apriDialogNuovoGiocatore());
        menuInserimento.add(itemNuovoGiocatore);

        menuRicerca = new JMenu("Ricerca");
        menuRicerca.setEnabled(false); // Disabilitato fino a connessione DB [Esercizi/Esercizio Lab.pdf, p. 4]
        itemCercaPunteggio = new JMenuItem("Cerca Punteggio");
        itemCercaPunteggio.addActionListener(e -> apriDialogCercaPunteggio());
        menuRicerca.add(itemCercaPunteggio);

        menuBar.add(menuGioco);
        menuBar.add(menuInserimento);
        menuBar.add(menuRicerca);
        setJMenuBar(menuBar); // Associa la barra dei menu [Lezioni/16 - JAVA - Swing.pdf, Slide 19]

        // JTextArea console
        txtConsole = new JTextArea();
        txtConsole.setEditable(false);
        txtConsole.setBackground(Color.BLACK);
        txtConsole.setForeground(Color.GREEN);
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
        panelLaterale.setPreferredSize(new Dimension(200, 0));
        
        lblTimer = new JLabel("⏰ Tempo Rimanente: 05:00", SwingConstants.CENTER);
        lblTimer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblTimer.setFont(new Font("Arial", Font.BOLD, 12));
        
        listInventario = new JList<>();
        JScrollPane scrollInventario = new JScrollPane(listInventario); // JList inserita nello ScrollPane [Lezioni/16 - JAVA - Swing.pdf, Slide 44-45]
        scrollInventario.setBorder(BorderFactory.createTitledBorder("Inventario"));

        panelLaterale.add(lblTimer, BorderLayout.NORTH);
        panelLaterale.add(scrollInventario, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollConsole, BorderLayout.CENTER);
        getContentPane().add(panelInput, BorderLayout.SOUTH);
        getContentPane().add(panelLaterale, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new InterfacciaGioco().setVisible(true);
        });
    }
}
```

---

## 3. Finestra di Dialogo Modale d'Inserimento (`DialogInserimento.java`)
Questa classe estende `JDialog` e implementa il form per aggiungere un giocatore, contenente una `JCheckBox`, soddisfacendo i requisiti esatti descritti nell'esercitazione `[Esercizi/Esercizio Lab.pdf, p. 4]`.

* **Perché questa scelta?** La finestra di dialogo dipende dal frame principale ed è impostata come **modale** (`true`), bloccando l'interazione con l'avventura principale finché l'utente non ha concluso l'inserimento `[Lezioni/16 - JAVA - Swing.pdf, Slide 50-51]`.

```java
package it.uniba.map.gioco.ui;

import it.uniba.map.gioco.database.PunteggioDAO;
import javax.swing.*;
import java.awt.*;

public class DialogInserimento extends JDialog {

    private JTextField txtNome;
    private JTextField txtPunti;
    private JCheckBox chkPrime; // JCheckBox conforme a [Esercizi/Esercizio Lab.pdf, p. 4]
    private JButton btnSalva, btnAnnulla;

    public DialogInserimento(JFrame parent) {
        super(parent, "Inserimento Nuovo Giocatore", true); // Terzo parametro 'true' imposta la modalità modale
        initComponents();
    }

    private void initComponents() {
        setSize(350, 220);
        setLocationRelativeTo(getParent());
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel(" Nome Giocatore:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel(" Punteggio iniziale:"));
        txtPunti = new JTextField();
        add(txtPunti);

        add(new JLabel(" Giocatore Prime?"));
        chkPrime = new JCheckBox("Attiva bonus d'iscrizione (+50pt)"); // JCheckBox [Lezioni/16 - JAVA - Swing.pdf, Slide 24]
        add(chkPrime);

        btnSalva = new JButton("Salva nel DB");
        btnSalva.addActionListener(e -> salvaDati());
        add(btnSalva);

        btnAnnulla = new JButton("Annulla");
        btnAnnulla.addActionListener(e -> dispose());
        add(btnAnnulla);
    }

    private void salvaDati() {
        String nome = txtNome.getText().trim();
        String puntiStr = txtPunti.getText().trim();

        if (nome.isEmpty() || puntiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Compilare tutti i campi!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int punti = Integer.parseInt(puntiStr);
            if (chkPrime.isSelected()) {
                punti += 50; // Applica il bonus Prime
            }

            // Richiama il DAO per l'inserimento
            PunteggioDAO dao = new PunteggioDAO();
            dao.aggiungiPunteggio(nome, punti);

            JOptionPane.showMessageDialog(this, "Giocatore inserito con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Chiude la Dialog
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Il punteggio deve essere un intero!", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
```

---

## 4. Finestra di Dialogo Modale di Ricerca (`DialogRicerca.java`)
Questa classe estende `JDialog` e implementa la ricerca dinamica visualizzando i risultati in una `JList` inserita in un `JScrollPane`, in perfetta conformità con il layout richiesto dall'esercitazione `[Esercizi/Esercizio Lab.pdf, p. 4-5]`.

```java
package it.uniba.map.gioco.ui;

import it.uniba.map.gioco.database.Punteggio;
import it.uniba.map.gioco.database.PunteggioDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogRicerca extends JDialog {

    private JTextField txtCerca;
    private JButton btnAvviaRicerca;
    private JList<String> listRisultati;
    private DefaultListModel<String> modelRisultati;

    public DialogRicerca(JFrame parent) {
        super(parent, "Cerca Punteggi nel Database", true); // Finestra modale
        initComponents();
    }

    private void initComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Pannello di Input Superiore (TextField + Bottone) [Esercizi/Esercizio Lab.pdf, p. 5]
        JPanel panelSuperiore = new JPanel(new BorderLayout(5, 5));
        panelSuperiore.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        txtCerca = new JTextField();
        btnAvviaRicerca = new JButton("Avvia Ricerca");
        btnAvviaRicerca.addActionListener(e -> eseguiRicerca());

        panelSuperiore.add(new JLabel("Nome Giocatore: "), BorderLayout.WEST);
        panelSuperiore.add(txtCerca, BorderLayout.CENTER);
        panelSuperiore.add(btnAvviaRicerca, BorderLayout.EAST);
        add(panelSuperiore, BorderLayout.NORTH);

        // Area Centrale: List con scroll dei risultati della ricerca [Esercizi/Esercizio Lab.pdf, p. 5]
        modelRisultati = new DefaultListModel<>();
        listRisultati = new JList<>(modelRisultati);
        JScrollPane scrollRisultati = new JScrollPane(listRisultati);
        scrollRisultati.setBorder(BorderFactory.createTitledBorder("Risultati Trovati"));
        add(scrollRisultati, BorderLayout.CENTER);
    }

    private void eseguiRicerca() {
        String queryStr = txtCerca.getText().trim();
        modelRisultati.clear();

        PunteggioDAO dao = new PunteggioDAO();
        List<Punteggio> tutti = dao.getMiglioriPunteggi();

        // Filtra la lista usando gli Stream (Lambda) [Lezioni/16 - JAVA - Lambda Expressions.pdf]
        tutti.stream()
                .filter(p -> p.getNomeGiocatore().toLowerCase().contains(queryStr.toLowerCase()))
                .map(p -> p.getNomeGiocatore() + " — " + p.getPunti() + " punti (" + p.getDataPartita() + ")")
                .forEach(modelRisultati::addElement);

        if (modelRisultati.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nessun risultato trovato.", "Ricerca", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
```

---

## 5. Conformità Didattica e Regola del Singolo Thread (Orale d'Esame)
Durante la prova orale, il docente valuterà l'uso consapevole di Swing. Ecco le spiegazioni da fornire per giustificare il codice:

* **Swing Single-Thread Rule**: Swing **non è thread-safe**. Tutte le interazioni con i componenti grafici (es: aggiungere testo in `txtConsole`, modificare la label `lblTimer` o inserire elementi nell'inventario) devono essere eseguite esclusivamente sull'**Event Dispatch Thread (EDT)**. Per questo motivo, l'avvio del JFrame nel metodo `main` viene racchiuso all'interno di `java.awt.EventQueue.invokeLater(...)` (o equivalente `SwingUtilities.invokeLater`). Come studiato nei thread, questo accoda l'operazione sulla coda grafica, evitando crash asincroni o anomalie di rendering `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 3]`.
* **Disposizione dei Componenti (Layout Managers)**: Swing utilizza i layout managers per disporre i componenti in modo indipendente dalla risoluzione dello schermo. Nel codice abbiamo utilizzato `BorderLayout` e `GridLayout` per dividere lo schermo in aree logiche (Center, South, East), garantendo la responsività della finestra quando l'utente la ridimensiona `[Lezioni/16 - JAVA - Swing.pdf, Slide 2, 19]`.

*Passa al [**Capitolo 3: Persistenza e Gestione File**](./03_File_e_Salvataggi.md) per scoprire come salvare e caricare la partita da file!*
