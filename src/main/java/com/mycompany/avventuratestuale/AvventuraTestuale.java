package com.mycompany.avventuratestuale;

import com.mycompany.avventuratestuale.ui.InterfacciaGioco;

public class AvventuraTestuale {
    public static void main(String[] args) {
        // Avvia l'interfaccia di gioco Swing nel corretto thread di coda degli eventi (EDT) [Lezioni/16 - Swing.pdf]
        java.awt.EventQueue.invokeLater(() -> {
            InterfacciaGioco frame = new InterfacciaGioco();
            frame.setVisible(true);
        });
    }
}
