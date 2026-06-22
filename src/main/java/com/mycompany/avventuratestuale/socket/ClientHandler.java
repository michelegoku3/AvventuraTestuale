package com.mycompany.avventuratestuale.socket;

import com.mycompany.avventuratestuale.ui.InterfacciaGioco;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;

/**
 * Thread dedicato alla gestione di un client socket connesso.
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final InterfacciaGioco gui;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connesso = true;

    public ClientHandler(Socket socket, InterfacciaGioco gui) {
        this.socket = socket;
        this.gui = gui;
    }

    public boolean isConnesso() {
        return connesso && !socket.isClosed();
    }

    public void inviaMessaggio(String messaggio) {
        if (out != null) {
            out.println(messaggio);
            out.flush();
        }
    }

    @Override
    public void run() {
        try {

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("==================================================");
            out.println("  BENVENUTO NELLA CONSOLE DI SICUREZZA CHIMERA ");
            out.println("  Digita i comandi da remoto. Esci digitando '#exit'");
            out.println("==================================================");

            String riga;

            while (connesso && (riga = in.readLine()) != null) {
                final String cmdRemoto = riga.trim();

                if (cmdRemoto.equalsIgnoreCase("#exit")) {
                    out.println("#ok Disconnessione riuscita.");
                    break;
                }


                SwingUtilities.invokeLater(() -> {
                    gui.stampaTesto("[Terminale Remoto]: " + cmdRemoto);
                    gui.eseguiComandoDaRemoto(cmdRemoto);
                });

                out.println("#ok Comando '" + cmdRemoto + "' inviato all'elaboratore centrale.");
            }
        } catch (IOException e) {
            System.err.println("Errore comunicazione client remoto: " + e.getMessage());
        } finally {
            try {
                connesso = false;
                socket.close();
                gui.stampaTesto("[Socket] Connessione remota disattivata.");
            } catch (IOException e) {
                System.err.println("Errore chiusura socket: " + e.getMessage());
            }
        }
    }
}
