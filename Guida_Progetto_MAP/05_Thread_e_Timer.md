# Capitolo 5: Thread e Programmazione Concorrente
### Obiettivo: Progettare un modulo per l'introduzione del tempo reale nel gioco (es. un timer di conto alla rovescia di 5 minuti) implementando le classi attive, gestendo l'interruzione dei thread e la sincronizzazione sicura delle risorse per prevenire conflitti di memoria.

Questo capitolo affronta il criterio 5 di valutazione d'esame (**Utilizzo dei thread e della programmazione concorrente**) `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`.

---

## 1. Thread e Runnable secondo la Didattica
Nelle lezioni del corso viene spiegato che:
> *"Thread: sono unità di esecuzione meno complesse dei processi... i thread all'interno dello stesso processo condividono le stesse risorse (es. memoria e I/O)"* `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 3]`.

Java consente di creare classi attive (ovvero che girano asincronamente su un proprio thread d'esecuzione) in due modi principali `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 4]`:
1. **Estendendo la classe `Thread`**: Ereditarietà diretta.
2. **Implementando l'interfaccia `Runnable`**: Consigliata perché:
   > *"L'utilizzo di Runnable evita di far ereditare la vostra classe da Thread (in questo modo la classe è libera di ereditare da qualche altra classe)"* `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 7]`.

Nel nostro progetto d'esame, implementiamo l'interfaccia **`Runnable`** per creare il nostro timer in tempo reale. Questo ci permette di mantenere aperta la possibilità di ereditare da un'altra classe se necessario, rispettando i consigli di progettazione OO forniti a lezione.

Inoltre, in conformità con la traccia assegnata per esercizio `[Esercizio Thread.pdf, p. 1]`, quando più thread interagiscono, la risorsa centrale deve essere gestita tramite metodi **`thread-safe`**.

---

## 2. Implementazione della Classe `ThreadTimer.java`

Il timer gira in background, riducendo di un secondo il tempo rimanente ogni 1000 millisecondi tramite l'uso di `Thread.sleep` `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 8]`.

```java
package it.uniba.map.gioco.core;

import it.uniba.map.gioco.ui.InterfacciaGioco;
import javax.swing.SwingUtilities;

public class ThreadTimer implements Runnable {

    private int secondiRimanenti;
    private final InterfacciaGioco gui;
    private boolean inEsecuzione = true;

    public ThreadTimer(int minutiTotali, InterfacciaGioco gui) {
        this.secondiRimanenti = minutiTotali * 60;
        this.gui = gui;
    }

    // Metodo sincronizzato per fermare il timer dall'esterno (Thread-Safe) [Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 25]
    public synchronized void fermaTimer() {
        this.inEsecuzione = false;
    }

    // Controlla lo stato inEsecuzione in modo sincronizzato per prevenire Memory Consistency Errors
    private synchronized boolean isAttivo() {
        return inEsecuzione && secondiRimanenti > 0;
    }

    @Override
    public void run() {
        // Ciclo di monitoraggio attivo controllato periodicamente [Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 11-12]
        while (isAttivo()) {
            int minuti = secondiRimanenti / 60;
            int secondi = secondiRimanenti % 60;
            String tempoFormattato = String.format("%02d:%02d", minuti, secondi);

            // Modifica asincrona di componenti grafici Swing tramite EDT (SwingUtilities)
            SwingUtilities.invokeLater(() -> {
                gui.aggiornaLabelTimer(tempoFormattato);
                if (secondiRimanenti == 60) {
                    gui.stampaTesto("⚠️ ATTENZIONE: Manca solo un minuto alla fine della partita!");
                }
            });

            try {
                // Sospende l'esecuzione per 1 secondo [Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 8]
                Thread.sleep(1000);
                
                synchronized (this) {
                    secondiRimanenti--;
                }
            } catch (InterruptedException e) {
                // Gestione corretta dell'interruzione [Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 10-11]
                System.err.println("Il Thread Timer è stato interrotto asincronamente.");
                return; // Esce dal run() e termina l'esecuzione del thread
            }
        }

        // Se scade il tempo e non siamo stati interrotti o fermati
        if (secondiRimanenti <= 0) {
            SwingUtilities.invokeLater(() -> {
                gui.gestisciScadenzaTempo();
            });
        }
    }
}
```

---

## 3. Gestione e avvio del Thread Timer dalla GUI
Per far partire il thread, creiamo un oggetto `Thread` passando l'istanza `Runnable` e chiamiamo il metodo `start()` `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 5, 7]`.

```java
// All'interno di InterfacciaGioco.java (Capitolo 2)

private ThreadTimer timerRunnable;
private Thread threadTimer;

public void avviaTimer() {
    timerRunnable = new ThreadTimer(5, this); // Timer di 5 minuti
    threadTimer = new Thread(timerRunnable, "Thread-Timer-Gioco"); // Crea il contenitore Thread [Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 5]
    threadTimer.start(); // Innesca l'esecuzione asincrona chiamando run() [Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 7]
}

public void arrestaTimerVittoria() {
    if (timerRunnable != null) {
        timerRunnable.fermaTimer(); // Ferma in sicurezza lo stato
    }
}
```

---

## 4. Teoria della Concorrenza: Cosa Spiegare al Professore
All'orale, il Prof. Basile presterà molta attenzione alle motivazioni teoriche che hanno guidato l'implementazione:

* **Thread Interference (Interferenza)**:
  > *"Avviene quando due operazioni su due thread differenti agiscono sullo stesso dato (interleave)... Siccome le operazioni non sono atomiche il risultato che si ottiene sul dato è difficilmente prevedibile"* `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 21-23]`.
  Nel nostro codice, la variabile `secondiRimanenti` potrebbe essere decrementata dal thread del timer e contemporaneamente letta o resettata dal thread principale (ad esempio, se il giocatore risolve un enigma che regala tempo aggiuntivo). Per prevenire interferenze, l'accesso e la modifica a variabili condivise avvengono dentro blocchi o metodi marcati come **`synchronized`** `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 25-28]`.
* **Memory Consistency Errors (Inconsistenza di Memoria)**:
  > *"Si verifica quando due thread hanno una visione inconsistente dello stesso dato"* `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 24]`.
  Senza sincronizzazione, la JVM potrebbe mantenere una copia cache locale di `inEsecuzione` nel thread del timer, non accorgendosi se il thread della GUI l'ha modificata per disattivarlo. Il metodo `synchronized` forza la sincronizzazione della memoria centrale (RAM) tra i thread.
* **Arresto Corretto dei Thread via `Interrupt`**:
  Nelle lezioni viene ricordato che:
  > *"Ogni thread dovrebbe implementare il suo metodo interrupt... è buona norma che l'interrupt di un thread coincida con la sua terminazione... si può catturare l'eccezione InterruptedException e interrompere l'esecuzione"* `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 10-11]`.
  Nel nostro codice del timer, se il metodo `Thread.sleep()` lancia un `InterruptedException` (perché viene invocato `threadTimer.interrupt()`), l'eccezione viene catturata dal blocco `catch` che esegue un `return` immediato per disattivare il thread pulendo le risorse.

*Passa al [**Capitolo 6: Programmazione in Rete con Socket**](./06_Socket_e_Rete.md) per completare il comparto di rete del progetto!*
