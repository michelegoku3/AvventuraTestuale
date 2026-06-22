package com.mycompany.avventuratestuale;

import com.mycompany.avventuratestuale.ui.InterfacciaGioco;

/**
 * Punto di ingresso dell'applicazione Swing.
 */
public class AvventuraTestuale {
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            InterfacciaGioco frame = new InterfacciaGioco();
            frame.setVisible(true);
        });
    }
}
