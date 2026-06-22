package com.mycompany.avventuratestuale.ui;

import com.mycompany.avventuratestuale.database.Punteggio;
import com.mycompany.avventuratestuale.database.PunteggioDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog Swing per cercare punteggi nella classifica.
 */
public class DialogRicerca extends JDialog {

    private JTextField txtCerca;
    private JButton btnAvviaRicerca;
    private JList<String> listRisultati;
    private DefaultListModel<String> modelRisultati;

    public DialogRicerca(JFrame parent) {
        super(parent, "Cerca Giocatori", true);
        initComponents();
    }

    private void initComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperiore = new JPanel(new BorderLayout(5, 5));
        panelSuperiore.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        txtCerca = new JTextField();
        btnAvviaRicerca = new JButton("Avvia Ricerca");
        btnAvviaRicerca.addActionListener(e -> eseguiRicerca());

        panelSuperiore.add(new JLabel("Filtra Nome: "), BorderLayout.WEST);
        panelSuperiore.add(txtCerca, BorderLayout.CENTER);
        panelSuperiore.add(btnAvviaRicerca, BorderLayout.EAST);
        add(panelSuperiore, BorderLayout.NORTH);


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


        tutti.stream()
                .filter(p -> p.getNomeGiocatore().toLowerCase().contains(queryStr.toLowerCase()))
                .map(p -> p.getNomeGiocatore() + " - " + p.getPunti() + " punti (" + p.getDataPartita() + ")")
                .forEach(modelRisultati::addElement);

        if (modelRisultati.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nessun risultato trovato.", "Ricerca", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
