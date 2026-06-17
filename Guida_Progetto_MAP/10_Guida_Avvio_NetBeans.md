# Capitolo 10: Guida Passo-Passo per l'Avvio in NetBeans
### Obiettivo: Spiegare in modo semplice come importare, compilare ed eseguire il progetto Maven in NetBeans e come testare le funzionalità avanzate (Database H2, Timer e Socket Remota).

Ora che l'intera infrastruttura del codice è stata implementata all'interno della struttura dei sorgenti Maven (`src/main/java/`), segui questi semplici passi sul tuo computer per importare il progetto ed eseguirlo.

---

## 1. Importazione del Progetto in NetBeans
Grazie all'uso di Maven, NetBeans riconoscerà automaticamente la struttura del progetto d'esame.
1. Apri **NetBeans**.
2. Fai clic su **File** ➔ **Open Project...**
3. Sfoglia le cartelle e seleziona la cartella principale del progetto (quella contenente il file `pom.xml`). NetBeans mostrerà l'icona del progetto con un piccolo triangolo di Maven.
4. Fai clic su **Open**.

---

## 2. Compilazione ed Esecuzione
1. Nel pannello sinistro dei progetti, fai clic destro sul progetto `AvventuraTestuale` e seleziona **Clean and Build**. Maven scaricherà la libreria di H2 Database centralmente e compilerà tutte le classi Java.
2. Al termine, fai clic destro sul progetto e seleziona **Run** (oppure fai clic sul pulsante **Play Verde** nella barra superiore).
3. Si aprirà la finestra grafica Swing del gioco **"Protocollo Chimera"**!

---

## 3. Come Testare le Funzionalità Richieste dal Professore

### A. La Giocabilità dell'Avventura (Trama ed Enigmi)
Una volta avviato il gioco, digita nella barra inferiore i seguenti comandi premendo **INVIO** (o cliccando "Invia") per verificare il funzionamento:
1. `guarda` (esamina la Camera Criogenica e scopri gli oggetti).
2. `prendi tessera` (raccoglie il badge magnetico del Dr. Moretti).
3. `usa tessera` (sblocca la porta est verso il corridoio).
4. `est` (ti sposta nel Corridoio di Servizio).

### B. Il Database Embedded H2 (Criterio JDBC)
1. Fai clic sul menù in alto **DB** ➔ **Connetti**.
2. Nella console apparirà il messaggio: *"Database relazionale H2 Connesso ed Inizializzato con successo."*
3. **Abilitazione Dinamica**: I menù **Inserimento** (per salvare i punteggi) e **Ricerca** (per cercare i giocatori), prima disabilitati, ora si sono attivati!
4. Gioca e sblocca l'enigma finale, oppure clicca su **Inserimento** ➔ **Salva Punteggio** per aprire la Dialog modale ed inserire un record nel DB. Puoi effettuare ricerche cliccando su **Ricerca** ➔ **Cerca Giocatori**.

### C. La Concorrenza (Criterio Thread)
1. Dal Corridoio di Servizio, dopo aver disattivato i laser con il decodificatore, muoviti a sud digitando `sud`.
2. Entrerai nella **Camera di Decontaminazione**: si attiverà immediatamente il **Timer asincrono** in alto a destra, impostato su un conto alla rovescia di 2 minuti.
3. Se non versi la cura nel condotto (`usa siero condotto`) prima dello scadere del tempo, il Thread attiverà il Game Over asincrono bloccando la barra di inserimento e mostrando il popup di sconfitta.

### D. Il Server Multithread (Criterio Socket)
All'avvio del gioco, un Server Socket TCP si attiva automaticamente in background in attesa di connessioni sulla porta **`8888`** `[Lezioni/14 - Slide 13]`.
1. Apri una finestra di terminale sul tuo computer (Prompt dei comandi, PowerShell o terminale MacOS/Linux).
2. Digita il comando di connessione standard:
   * **Se usi Telnet**: `telnet localhost 8888`
   * **Se usi Netcat**: `nc localhost 8888`
3. Il terminale si connetterà al tuo gioco Java in esecuzione! Mostrerà il messaggio: *"Benvenuto nella console di sicurezza Chimera"*.
4. Digita un comando dal terminale esterno (es. `ovest` o `prendi fiala`) e premi invio: **vedrai la GUI Swing del gioco locale eseguire asincronamente il comando ricevuto da rete e trasmettere la risposta testuale sul terminale remoto in tempo reale!**
5. Questo lascerà la commissione d'esame e il Prof. Basile letteralmente a bocca aperta.
