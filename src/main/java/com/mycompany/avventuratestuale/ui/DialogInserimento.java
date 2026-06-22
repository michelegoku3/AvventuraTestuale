package com.mycompany.avventuratestuale.ui;

import com.mycompany.avventuratestuale.database.PunteggioDAO;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog Swing per inserimento manuale di un punteggio.
 */
public class DialogInserimento extends JDialog {

    private JTextField txtNome;
    private JTextField txtPunti;
    private JCheckBox chkPrime;
    private JButton btnSalva, btnAnnulla;

    public DialogInserimento(JFrame parent) {
        super(parent, "Registra Punteggio nel Database", true);
        initComponents();
    }

    private void initComponents() {
        setSize(350, 220);
        setLocationRelativeTo(getParent());
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel(" Nome Giocatore:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel(" Punteggio Finale:"));
        txtPunti = new JTextField();
        add(txtPunti);

        add(new JLabel(" Attiva Bonus?"));
        chkPrime = new JCheckBox("Bonus d'Iscrizione (+50pt)");
        add(chkPrime);

        btnSalva = new JButton("Salva");
        btnSalva.addActionListener(e -> salvaPunteggio());
        add(btnSalva);

        btnAnnulla = new JButton("Annulla");
        btnAnnulla.addActionListener(e -> dispose());
        add(btnAnnulla);
    }

    private void salvaPunteggio() {
        String nome = txtNome.getText().trim();
        String puntiStr = txtPunti.getText().trim();

        if (nome.isEmpty() || puntiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Compilare tutti i campi!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int punti = Integer.parseInt(puntiStr);
            if (chkPrime.isSelected()) {
                punti += 50;
            }


            PunteggioDAO dao = new PunteggioDAO();
            dao.aggiungiPunteggio(nome, punti);

            JOptionPane.showMessageDialog(this, "Punteggio inserito correttamente nel DB!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Il punteggio inserito non è un intero valido!", "Errore di tipo", JOptionPane.ERROR_MESSAGE);
        }
    }
}
