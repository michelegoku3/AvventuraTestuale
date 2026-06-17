package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.ui.InterfacciaGioco;
import javax.swing.SwingUtilities;

public class ThreadTimer implements Runnable {

    private int secondiRimanenti;
    private final InterfacciaGioco gui;
    private boolean inEsecuzione = true;

    public ThreadTimer(int minutiTotali, InterfacciaGioco gui) {
        this.secondiRimanenti = minutiTotali * 60;
        this.gui = gui;
    }

    // Metodo sincronizzato per fermare il timer (Thread-Safe) [Lezioni/14 / Lezioni/15 - Programmazione Concorrente.pdf, Slide 25-26]
    public synchronized void fermaTimer() {
        this.inEsecuzione = false;
    }

    private synchronized boolean isAttivo() {
        return inEsecuzione && secondiRimanenti > 0;
    }

    @Override
    public void run() {
        // Monitoraggio attivo conforme a [Lezioni/14 / Lezioni/15 - Programmazione Concorrente.pdf, Slide 10-12]
        while (isAttivo()) {
            int minuti = secondiRimanenti / 60;
            int secondi = secondiRimanenti % 60;
            String tempoFormattato = String.format("%02d:%02d", minuti, secondi);

            // Modifica asincrona di Swing tramite EDT
            SwingUtilities.invokeLater(() -> {
                gui.aggiornaLabelTimer(tempoFormattato);
                if (secondiRimanenti == 60) {
                    gui.stampaTesto("⚠️ ATTENZIONE: Manca solo un minuto prima della decontaminazione termica!");
                }
            });

            try {
                Thread.sleep(1000); // Sospende il thread per 1 secondo [Lezioni/14 / Lezioni/15 - Programmazione Concorrente.pdf, Slide 8]
                synchronized (this) {
                    secondiRimanenti--;
                }
            } catch (InterruptedException e) {
                // Gestione pulita dell'interrupt [Lezioni/14 / Lezioni/15 - Programmazione Concorrente.pdf, Slide 10-11]
                System.err.println("Thread Timer interrotto asincronamente.");
                return;
            }
        }

        if (secondiRimanenti <= 0) {
            SwingUtilities.invokeLater(() -> {
                gui.gestisciScadenzaTempo();
            });
        }
    }
}
