package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.ui.InterfacciaGioco;
import javax.swing.SwingUtilities;

/**
 * Timer concorrente della decontaminazione, eseguito su un thread separato.
 */
public class ThreadTimer implements Runnable {

    private int secondiRimanenti;
    private final InterfacciaGioco gui;
    private boolean inEsecuzione = true;

    public ThreadTimer(int minutiTotali, InterfacciaGioco gui) {
        this(minutiTotali * 60, gui, true);
    }

    private ThreadTimer(int secondiTotali, InterfacciaGioco gui, boolean daSecondi) {
        this.secondiRimanenti = Math.max(0, secondiTotali);
        this.gui = gui;
    }

    public static ThreadTimer daSecondi(int secondiTotali, InterfacciaGioco gui) {
        return new ThreadTimer(secondiTotali, gui, true);
    }


    /**
     * Ferma il conto alla rovescia in modo thread-safe.
     */
    public synchronized void fermaTimer() {
        this.inEsecuzione = false;
    }

    /**
     * Restituisce i secondi rimasti, usati anche per salvare la partita.
     *
     * @return secondi non negativi ancora disponibili
     */
    public synchronized int getSecondiRimanenti() {
        return Math.max(0, secondiRimanenti);
    }

    private synchronized boolean isAttivo() {
        return inEsecuzione && secondiRimanenti > 0;
    }

    private synchronized boolean isScadutoNaturalmente() {
        return inEsecuzione && secondiRimanenti <= 0;
    }

    @Override
    public void run() {

        while (isAttivo()) {
            int minuti = secondiRimanenti / 60;
            int secondi = secondiRimanenti % 60;
            String tempoFormattato = String.format("%02d:%02d", minuti, secondi);


            SwingUtilities.invokeLater(() -> {
                gui.aggiornaLabelTimer(tempoFormattato);
                if (secondiRimanenti == 60) {
                    gui.stampaTesto("[ATTENZIONE]: Manca solo un minuto prima della decontaminazione termica!");
                }
            });

            try {
                Thread.sleep(1000);
                synchronized (this) {
                    secondiRimanenti--;
                }
            } catch (InterruptedException e) {

                System.err.println("Thread Timer interrotto asincronamente.");
                return;
            }
        }


        if (isScadutoNaturalmente()) {
            SwingUtilities.invokeLater(() -> {
                gui.gestisciScadenzaTempo();
            });
        }
    }
}
