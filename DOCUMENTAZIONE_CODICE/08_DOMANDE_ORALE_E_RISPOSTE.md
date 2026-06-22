# 08 — Domande possibili all'orale e risposte

## 1. Che tipo di progetto hai realizzato?

Ho realizzato un'avventura testuale con interfaccia grafica Swing chiamata **Trapped Virus**. Il giocatore esplora un laboratorio sotterraneo, risolve enigmi con oggetti, dialoga con NPC, usa un inventario, salva/carica la partita, interagisce con un database e affronta un timer concorrente nella Camera di Decontaminazione.

---

## 2. Quali principi OOP hai usato?

Ho usato:

- incapsulamento;
- classi e oggetti;
- composizione;
- classe astratta `Gioco`;
- interfaccia `Command`;
- polimorfismo tramite le classi comando;
- package per separare responsabilità.

---

## 3. A cosa serve la classe astratta `Gioco`?

`Gioco` definisce lo stato comune di una generica avventura testuale: stanza corrente, inventario e lista dei comandi. Lascia alla sottoclasse concreta `LaMiaAvventura` la definizione della mappa, degli oggetti e della logica specifica del gioco.

---

## 4. Perché hai usato il Command Pattern?

Per separare il riconoscimento del comando dalla sua esecuzione. Il parser riconosce il tipo di comando, mentre la classe concreta, ad esempio `PrendiCommand` o `UsaCommand`, esegue la logica. Questo rende il codice più modulare ed estensibile.

---

## 5. Differenza tra `Comando` e `Command`?

`Comando` rappresenta il comando riconosciuto dal parser, con tipo e sinonimi.

`Command` è l'interfaccia delle classi che eseguono concretamente un comando tramite il metodo `execute`.

Quindi:

```text
Comando = riconoscimento sintattico
Command = esecuzione operativa
```

---

## 6. Come funziona il parser?

Il parser prende l'input utente, lo porta in minuscolo, lo divide in token, elimina stopword e cerca il comando tra i sinonimi disponibili. Poi cerca gli oggetti nella stanza e nell'inventario. Restituisce un `ParserOutput` con comando, oggetto primario, oggetto secondario e input originale.

---

## 7. Come funziona il fuzzy matching?

Se il comando non viene riconosciuto, il parser usa la distanza di Levenshtein per trovare il sinonimo più vicino. Se la distanza è piccola, suggerisce il comando corretto. Per esempio `guada` può suggerire `guarda`.

---

## 8. Come hai usato i file?

Ho usato la serializzazione Java con `ObjectOutputStream` e `ObjectInputStream` nella classe `SalvataggioManager`. La partita viene salvata su file e poi deserializzata quando il giocatore carica.

---

## 9. Cosa viene serializzato?

Viene serializzato lo stato del gioco: stanza corrente, inventario, stanze, oggetti, flag narrativi, stato enigmi e stato serializzabile del timer. Non viene serializzato il thread reale del timer, ma i dati necessari per ricostruirlo.

---

## 10. Come hai usato JDBC?

Ho usato H2 embedded con JDBC. `DatabaseManager` crea le tabelle, `PunteggioDAO` salva e legge i punteggi, `DialogoDAO` legge i nodi di dialogo di Prometeo.

---

## 11. Perché usare DAO?

Il DAO separa il codice SQL dalla logica del gioco. In questo modo la logica dell'avventura non contiene direttamente query SQL e l'accesso ai dati è più ordinato.

---

## 12. Come hai usato Swing?

Ho usato `JFrame` per la finestra principale, `JTextArea` per il terminale, `JTextField` per l'input, `JButton` per inviare, `JList` per l'inventario, `JMenuBar` per i menu e `JDialog` per la ricerca punteggi.

---

## 13. Come hai gestito gli eventi Swing?

Ho usato listener. Il tasto Invio è gestito con un `KeyAdapter`, mentre il bottone `Invia` usa un `ActionListener` con lambda expression.

---

## 14. Come hai usato i thread?

Il timer della decontaminazione è implementato con `ThreadTimer`, che implementa `Runnable`. Viene eseguito da un thread separato e aggiorna il tempo ogni secondo.

---

## 15. Perché usi `SwingUtilities.invokeLater`?

Perché Swing non è thread-safe. Il timer gira in un thread secondario, quindi per aggiornare la GUI deve inserire l'aggiornamento nella coda dell'Event Dispatch Thread tramite `invokeLater`.

---

## 16. A cosa serve `synchronized` nel timer?

Serve a proteggere l'accesso a variabili condivise come `secondiRimanenti` e `inEsecuzione`. Senza sincronizzazione, GUI e thread del timer potrebbero leggere o modificare lo stato contemporaneamente.

---

## 17. Come hai usato le socket?

Ogni istanza del gioco avvia un server socket spettatore. Un'altra istanza può collegarsi inserendo la porta e vedere in tempo reale il testo della partita. Il server usa `ServerSocket`, ogni client è gestito da un `ClientHandler` su thread separato.

---

## 18. Come funziona la porta automatica delle socket?

Il gioco prova ad aprire la porta 8888. Se è occupata, prova 8889, poi 8890 e così via, finché trova una porta libera. Questo permette di aprire due istanze contemporaneamente.

---

## 19. Come hai usato lambda e stream?

Li ho usati per filtrare oggetti, cercare comandi, aggiornare la lista dell'inventario, cercare punteggi e implementare esempi nella classe `LambdaTasks`.

---

## 20. Qual è la specifica algebrica scelta?

Ho scelto l'ADT `Inventario`, con operazioni `creaInv`, `inserisci`, `rimuovi`, `vuoto`, `quanti` e `contiene`. La specifica è data tramite sorts, operations ed equazioni di riduzione.

---

## 21. Come funziona il punteggio?

Il punteggio dipende dal finale scelto e dal tempo impiegato a neutralizzare il gas.

```text
penalita = tempoImpiegato * 2
punteggio = baseFinale - penalita
```

Basi:

```text
Finale 1 = 500
Finale 2 = 400
Finale 3 = 100
```

Il punteggio minimo è 0.

---

## 22. Qual è la parte più importante del gameplay?

La progressione principale è: trovare la tessera, riparare Rancido, ottenere il decodificatore, aprire la cassaforte, prendere fiala e diario, sintetizzare il siero, neutralizzare il gas e scegliere il finale dal Nucleo.

---

## 23. Perché il progetto è conforme ai requisiti?

Perché implementa tutti gli argomenti richiesti: OOP, file, JDBC, thread, socket, Swing, lambda/stream, documentazione, specifica algebrica e un'avventura completa con mappa, inventario, oggetti, enigmi e finali.
