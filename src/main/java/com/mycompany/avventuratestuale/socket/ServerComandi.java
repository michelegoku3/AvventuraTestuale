package com.mycompany.avventuratestuale.socket;

import com.mycompany.avventuratestuale.ui.InterfacciaGioco;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server socket multiclient per spettatori e terminali remoti.
 */
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
                serverSocket.close();
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

            serverSocket = new ServerSocket(porta);

            System.out.println("[Socket] Server di Hacking attivo in background sulla porta " + porta + "...");

            while (inEsecuzione) {
                Socket clientSocket = serverSocket.accept();
                gui.stampaTesto("[Socket] Terminale di sicurezza connesso da IP: " + clientSocket.getRemoteSocketAddress());


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
