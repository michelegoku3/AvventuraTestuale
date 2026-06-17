package com.mycompany.avventuratestuale.socket;

import com.mycompany.avventuratestuale.ui.InterfacciaGioco;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerComandi extends Thread {

    private final int porta;
    private final InterfacciaGioco gui;
    private ServerSocket serverSocket;
    private boolean inEsecuzione = true;
    private final List<ClientHandler> clientConnessi = new ArrayList<>();

    public ServerComandi(int porta, InterfacciaGioco gui) {
        this.porta = porta;
        this.gui = gui;
    }

    public synchronized void fermaServer() {
        inEsecuzione = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Sblocca accept() [Lezioni/14 / Lezioni/15 - Programmazione in Rete.pdf, Slide 10]
            }
        } catch (IOException e) {
            System.err.println("Errore interruzione server socket: " + e.getMessage());
        }
    }

    public synchronized void trasmettiAClient(String messaggio) {
        clientConnessi.removeIf(client -> !client.isConnesso());
        for (ClientHandler client : clientConnessi) {
            client.inviaMessaggio(messaggio);
        }
    }

    @Override
    public void run() {
        try {
            // Avvia ServerSocket [Lezioni/14 / Lezioni/15 - Programmazione in Rete.pdf, Slide 11]
            serverSocket = new ServerSocket(porta);
            // Stampa silenziosa in standard output per evitare di inquinare la console Swing d'inizio partita del giocatore
            System.out.println("🌐 Server Socket di Hacking attivo in background sulla porta " + porta + "...");
            
            while (inEsecuzione) {
                Socket clientSocket = serverSocket.accept(); // Operazione bloccante [Lezioni/14 / Lezioni/15 - Programmazione in Rete.pdf, Slide 10]
                gui.stampaTesto("🔌 Terminale di sicurezza connesso da IP: " + clientSocket.getRemoteSocketAddress());
                
                // Crea e lancia thread concorrente per il client [Lezioni/14 / Lezioni/15 - Programmazione in Rete.pdf, Slide 13]
                ClientHandler handler = new ClientHandler(clientSocket, gui);
                synchronized (this) {
                    clientConnessi.add(handler);
                }
                handler.start();
            }
        } catch (IOException e) {
            if (inEsecuzione) {
                System.err.println("Errore del server socket: " + e.getMessage());
            }
        }
    }
}
