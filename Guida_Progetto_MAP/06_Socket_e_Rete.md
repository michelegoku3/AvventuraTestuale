# Capitolo 6: Programmazione in Rete con Socket
### Obiettivo: Sviluppare un Server Socket Multithread TCP/IP per la gestione remota del gioco o per la modalità spettatore remota, conformemente alle esercitazioni didattiche e ai requisiti di rete.

Questo capitolo affronta il criterio 6 di valutazione d'esame (**Utilizzo delle socket e/o delle REST**) e si appoggia sul criterio 5 per la gestione del multithreading in rete `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`.

---

## 1. Socket e Programmazione Multiclient in Rete
Nelle lezioni del corso viene spiegato che:
> *"In Java si usa un socket per creare la connessione ad un'altra macchina... Ci sono due classi socket basate su stream: ServerSocket che il server usa per ascoltare una richiesta di connessione, Socket usata dal client per inizializzare la connessione... accept() restituisce un Socket corrispondente attraverso il quale la comunicazione può avvenire"* `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 9-10]`.

Inoltre, l'esercitazione pratica di rete assegna il compito di realizzare un server di comunicazione multiclient basato su comandi interpretati ed eseguiti lato server `[Esercizio Socket.pdf, p. 1]`.

Per implementare con successo questo criterio nel progetto d'esame, implementiamo un **Server Socket Multithread** che permette a spettatori remoti di connettersi in tempo reale (sulla porta TCP `8888`) per assistere alla partita o inoltrare comandi di aiuto al giocatore locale `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 13]`.

---

## 2. Architettura Multithread del Server (`ServerComandi.java`)
Come spiegato a lezione, la connessione di rete deve essere gestita asincronamente per evitare di bloccare la GUI Swing:
> *"Un problema di non poca rilevanza è la necessità di manipolare più connessioni contemporaneamente. Per servire più client contemporaneamente si ricorre al multithreading... Quando la connessione è attiva e accept() termina... si utilizza il Socket ottenuto in un nuovo thread"* `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 13]`.

```java
package it.uniba.map.gioco.socket;

import it.uniba.map.gioco.ui.InterfacciaGioco;
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

    // Colleziona i client gestiti asincronamente per l'invio broadcast (eco dei comandi della partita)
    private final List<ClientHandler> clientConnessi = new ArrayList<>();

    public ServerComandi(int porta, InterfacciaGioco gui) {
        this.porta = porta;
        this.gui = gui;
    }

    public synchronized void fermaServer() {
        this.inEsecuzione = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Forza la chiusura sbloccando la accept() [Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 10]
            }
        } catch (IOException e) {
            System.err.println("Errore di chiusura del ServerSocket: " + e.getMessage());
        }
    }

    // Trasmette un messaggio a tutti i client spettatori attivi (Broadcast)
    public synchronized void trasmettiTutti(String messaggio) {
        // Rimuove spettatori disconnessi e inoltra l'aggiornamento
        clientConnessi.removeIf(client -> !client.isConnesso());
        for (ClientHandler client : clientConnessi) {
            client.inviaMessaggio(messaggio);
        }
    }

    @Override
    public void run() {
        try {
            // Quando si crea un ServerSocket si specifica solo il port [Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 11]
            serverSocket = new ServerSocket(porta);
            gui.stampaTesto("🌐 Server Spettatore TCP avviato sulla porta " + porta);

            while (inEsecuzione) {
                // accept() è bloccante e attende la richiesta del client [Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 10]
                Socket clientSocket = serverSocket.accept();
                gui.stampaTesto("🔌 Nuovo spettatore connesso da IP: " + clientSocket.getRemoteSocketAddress());

                // Crea un thread dedicato per servire il client specifico senza bloccare accept() [Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 13]
                ClientHandler handler = new ClientHandler(clientSocket, gui);
                synchronized (this) {
                    clientConnessi.add(handler);
                }
                handler.start(); // Avvia il thread concorrente
            }
        } catch (IOException e) {
            if (inEsecuzione) {
                System.err.println("Errore di rete sul Server: " + e.getMessage());
            }
        }
    }
}
```

---

## 3. Gestore del Singolo Client (`ClientHandler.java`)
Questa classe gestisce i canali di I/O (InputStream / OutputStream) del socket avvolgendoli in classi bufferizzate e formattate per la trasmissione di testo `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 12]`.

In conformità con la traccia `[Esercizio Socket.pdf, p. 1]`, il protocollo prevede l'invio di messaggi ed il comando di uscita `#exit` per chiudere la connessione.

```java
package it.uniba.map.gioco.socket;

import it.uniba.map.gioco.ui.InterfacciaGioco;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;

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

    // Invia un messaggio nello stream di output del client
    public void inviaMessaggio(String messaggio) {
        if (out != null) {
            out.println(messaggio);
            out.flush();
        }
    }

    @Override
    public void run() {
        try {
            // Ottiene gli stream a partire dai singoli Socket [Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 12]
            // PrintWriter e BufferedReader consentono un I/O di riga [Lezioni/10 - JAVA - Input Output.pdf, Slide 9-10]
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("--- BENVENUTO NEL SERVER SPETTATORE ---");
            out.println("Puoi inviare un comando da eseguire sul gioco locale.");
            out.println("Digita '#exit' per disconnetterti.");
            out.println("----------------------------------------");

            String rigaInput;
            while (connesso && (rigaInput = in.readLine()) != null) {
                String cmdPuro = rigaInput.trim();

                // Comando di disconnessione conforme alla traccia d'esercizio [Esercizio Socket.pdf, p. 1]
                if (cmdPuro.equalsIgnoreCase("#exit")) {
                    out.println("#ok Arrivederci!");
                    break;
                }

                // Inoltra il comando asincrono sul thread della GUI in modo thread-safe
                final String cmdInvia = cmdPuro;
                SwingUtilities.invokeLater(() -> {
                    gui.stampaTesto("💬 [Remoto] Inoltrato: " + cmdInvia);
                    gui.eseguiComandoDaRemoto(cmdInvia);
                });
                
                out.println("#ok Comando ricevuto ed elaborato");
            }
        } catch (IOException e) {
            System.err.println("Errore comunicazione client: " + e.getMessage());
        } finally {
            try {
                connesso = false;
                socket.close(); // Chiude il socket rilasciando le porte [Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 10]
                gui.stampaTesto("🔌 Uno spettatore si è disconnesso.");
            } catch (IOException e) {
                System.err.println("Errore di chiusura socket: " + e.getMessage());
            }
        }
    }
}
```

---

## 4. Teoria delle Sockets: Domande Tipiche del Colloquio Orale
Durante l'esame orale il Prof. Basile interrogherà approfonditamente sui meccanismi di basso livello gestiti dalla rete:

* **Modello a File (Wrapping degli Stream)**:
  > *"I progettisti di Java hanno reso la programmazione in rete molto simile alla lettura e scrittura di file... si fa il wrapping di una connessione di rete (un socket) in un flusso (stream) di oggetti... per scambiare informazioni"* `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 3-4]`.
  Nel nostro codice, infatti, gli oggetti `PrintWriter` e `BufferedReader` operano esattamente come farebbero su file di testo locali (Vedi Capitolo 3), nascondendo la complessità di pacchettizzazione TCP e di frammentazione IP sottostanti gestite interamente dalla JVM `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 4]`.
* **Identificazione del Server e della Porta**:
  > *"Quando si crea un ServerSocket, si specifica solo un numero di port... quando si crea un Socket lato client, occorre specificare tanto l'indirizzo IP quanto il numero di port"* `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 11]`.
  Il server non deve specificare l'IP perché è in ascolto sulla scheda di rete della macchina su cui gira. Il numero di porta (es: `8888`) è un'astrazione software necessaria per instradare i pacchetti di rete TCP verso la nostra specifica applicazione Java, evitando collisioni con altre applicazioni `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 7-8]`.
* **Multithreading di Rete**:
  La chiamata `serverSocket.accept()` si blocca in attesa indefinita di pacchetti (handshake SYN/ACK) da parte di un client. Senza un thread separato (`ServerComandi`), l'EDT di Swing si bloccherebbe impedendo al giocatore di interagire con la finestra. Inoltre, se il server non lanciasse ogni client in un proprio thread (`ClientHandler`), un secondo client non potrebbe connettersi finché il primo non ha chiuso la sessione `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf, Slide 13]`.

*Passa al [**Capitolo 7: Specifica Algebrica**](./07_Specifica_Algebrica.md) per scoprire come redigere la specifica formale del tuo Inventario di gioco!*
