# 05 — Thread, timer concorrente e socket

# 1. Timer concorrente

Il timer della Camera di Decontaminazione è implementato nella classe:

```text
core/ThreadTimer.java
```

La classe implementa `Runnable`:

```java
public class ThreadTimer implements Runnable
```

## Cosa significa `Runnable`

`Runnable` è un'interfaccia Java che rappresenta un'attività eseguibile da un thread.

Il metodo principale è:

```java
public void run()
```

Quando viene creato un thread:

```java
threadTimer = new Thread(timerRunnable, "Thread-Timer-Decontaminazione");
threadTimer.start();
```

la JVM esegue il metodo `run()` in parallelo rispetto alla GUI.

---

# 2. Stato del timer

Il timer contiene:

```java
private int secondiRimanenti;
private final InterfacciaGioco gui;
private boolean inEsecuzione = true;
```

- `secondiRimanenti`: tempo ancora disponibile;
- `gui`: riferimento alla finestra per aggiornare label e messaggi;
- `inEsecuzione`: flag per fermare il timer.

---

# 3. Sincronizzazione

Alcuni metodi sono `synchronized`:

```java
public synchronized void fermaTimer()
public synchronized int getSecondiRimanenti()
private synchronized boolean isAttivo()
```

## Perché `synchronized`

Il timer gira in un thread separato, mentre la GUI può leggere o modificare il suo stato. Senza sincronizzazione, due thread potrebbero accedere agli stessi dati nello stesso momento in modo incoerente.

`synchronized` garantisce accesso controllato alla sezione critica.

---

# 4. Ciclo del timer

Nel metodo `run()`:

```java
while (isAttivo()) {
    ...
    Thread.sleep(1000);
    secondiRimanenti--;
}
```

Ogni secondo:

1. calcola minuti e secondi;
2. aggiorna la label Swing;
3. dorme per 1000 millisecondi;
4. decrementa il tempo.

---

# 5. Aggiornare Swing da un thread

Swing non è thread-safe. Per aggiornare la GUI dal timer si usa:

```java
SwingUtilities.invokeLater(() -> {
    gui.aggiornaLabelTimer(tempoFormattato);
});
```

`invokeLater` mette l'aggiornamento nella coda dell'Event Dispatch Thread di Swing.

Questo evita errori dovuti all'aggiornamento della GUI da thread secondari.

---

# 6. Game over

Quando il timer arriva a zero:

```java
if (isScadutoNaturalmente()) {
    SwingUtilities.invokeLater(() -> {
        gui.gestisciScadenzaTempo();
    });
}
```

Il game over parte solo se il timer è scaduto naturalmente. Se il giocatore usa il siero, il timer viene fermato e non uccide il personaggio.

---

# 7. Salvataggio del timer

Il thread non viene serializzato direttamente. Vengono salvati nel gioco:

```text
isTimerDecontaminazioneAttivo
secondiDecontaminazioneRimanenti
tempoImpiegatoDecontaminazione
```

Quando si carica la partita, la GUI ricostruisce il timer se era attivo.

---

# 8. Socket

Il progetto usa socket per una modalità spettatore.

Classi:

```text
socket/ServerComandi.java
socket/ClientHandler.java
```

---

## 8.1 `ServerComandi`

`ServerComandi` estende `Thread`:

```java
public class ServerComandi extends Thread
```

Apre un `ServerSocket`:

```java
serverSocket = new ServerSocket(porta);
```

Poi resta in ascolto:

```java
Socket clientSocket = serverSocket.accept();
```

`accept()` è bloccante: il thread resta fermo finché un client non si collega.

---

## 8.2 Porta automatica

Ogni istanza del gioco prova ad aprire il server dalla porta 8888. Se è occupata, prova la successiva:

```text
8889, 8890, 8891, ...
```

Questo consente di aprire due istanze del gioco contemporaneamente.

---

## 8.3 `ClientHandler`

Ogni client connesso è gestito da un thread dedicato:

```java
ClientHandler handler = new ClientHandler(clientSocket, gui);
handler.start();
```

Questo permette al server di gestire più spettatori contemporaneamente.

---

## 8.4 Broadcast

Quando il gioco stampa testo nella console, lo trasmette anche ai client collegati:

```java
serverSocket.trasmettiAClient(testo);
```

In questo modo un'altra istanza può osservare la partita in tempo reale.

---

# 9. Modalità spettatore

Uso pratico:

1. aprire una prima istanza del gioco;
2. leggere la porta da `Socket > Info porta locale`;
3. aprire una seconda istanza;
4. usare `Socket > Apri spettatore`;
5. inserire la porta della prima istanza.

La seconda finestra riceverà i messaggi della prima.

---

# 10. Collegamento con gli argomenti del corso

Questa parte implementa:

- thread;
- `Runnable`;
- `Thread`;
- `sleep`;
- `synchronized`;
- gestione dell'interruzione;
- aggiornamento Swing tramite EDT;
- `ServerSocket`;
- `Socket`;
- stream di rete;
- multiclient;
- broadcast.
