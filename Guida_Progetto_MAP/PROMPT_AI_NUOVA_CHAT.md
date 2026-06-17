# CHAT HANDOFF PROMPT — PROGETTO METODI AVANZATI DI PROGRAMMAZIONE (MAP)
### DOCUMENTO DI TRASFERIMENTO CONTESTO PER LA NUOVA SESSIONE DI CHAT (PROMPT PER AI RICEVENTE)

> ⚠️ **IMPORTANTE PER L'AI RICEVENTE:** Leggi questo documento prima di scrivere qualsiasi codice. Qui è riassunto lo stato del progetto, l'architettura logica di Java 26, le dipendenze Maven, i requisiti d'esame e la sceneggiatura definita. Continua lo sviluppo e il raffinamento su NetBeans rispettando rigorosamente queste linee guida accademiche.

---

## 1. Informazioni Accademiche e Vincoli del Corso
* **Insegnamento**: Metodi Avanzati di Programmazione (MAP) - Track M-Z
* **Corso di Laurea**: Laurea Triennale in Informatica, Università degli Studi di Bari "Aldo Moro"
* **Docente**: Prof. Pierpaolo Basile
* **Anno Accademico**: 2025/2026
* **Ambiente di Sviluppo**: Java 26 (JDK 26, progetto Maven aperto in NetBeans)
* **DBMS Relazionale**: H2 Database (in modalità embedded relazionale tramite JDBC) `[Lezioni/13 - JDBC.pdf, Slide 4-5]`
* **Trama Selezionata**: **"Protocollo Chimera"** (Sci-Fi Distopico nel Laboratorio Genetico Sotterraneo)

---

## 2. Requisiti di Valutazione (I 10 Criteri MAP da 0 a 5 Punti)
Il progetto (caso di studio) viene valutato secondo **10 criteri da 0 a 5 punti ciascuno** per un totale di **50 punti d'esame** (poi rapportati in trentesimi) `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`. L'architettura è stata progettata per soddisfarli tutti in modo coerente:

1. **Qualità dell'avventura**: Trama coerente, dizionario di comandi bilingue (Italiano/Inglese), mappa a grafo di 7 stanze con enigmi, e **oggetti scenici statici non prendibili (scenery)** per garantire un'immersione di gioco perfetta ed evitare fallback impropri `[Lab 1 - Introduzione.pdf]`.
2. **Qualità della programmazione ad oggetti**: Ereditarietà, interfacce, classi astratte, composizione, information hiding (package e modificatori d'accesso) `[Lezioni/2 - Paradigma OO.pdf]`.
3. **Utilizzo dei file**: Persistenza tramite serializzazione ricorsiva (`ObjectOutputStream` / `ObjectInputStream`) `[Lezioni/10 - Input Output.pdf]`, `[Esercizio Input Output.pdf, p. 1]`.
4. **Utilizzo di database/JDBC**: Database relazionale H2 in modalità embedded, creazione automatica di tabelle, interrogazioni SQL e inserimenti sicuri tramite il design pattern **DAO (Data Access Object)** `[Lezioni/13 - JDBC.pdf]`, `[Esercizio JDBC.pdf]`.
5. **Utilizzo dei thread**: Creazione di un timer di gioco concorrente (countdown di decontaminazione nella Stanza 5) che gira in background implementando l'interfaccia **`Runnable`** `[Lezioni/14 / Lezioni/15 - Programmazione Concorrente.pdf, Slide 4, 7]`. Gestione asincrona e thread-safe dello stato tramite metodi e blocchi **`synchronized`** per evitare *Thread Interference* e *Memory Consistency Errors*.
6. **Utilizzo delle socket**: Server socket multithread TCP/IP per la gestione concorrente di molteplici spettatori/client remoti `[Lezioni/14 / Lezioni/15 - Programmazione in Rete.pdf]`, `[Esercizio Socket.pdf]`.
7. **Utilizzo delle SWING**: GUI desktop interattiva formata da `JFrame`, barre di menu `JMenuBar` (con menu DB, Inserimento e Ricerca abilitati dinamicamente solo se il DB H2 è connesso!), `JMenuItem`, finestre di dialogo modali `JDialog` per form di inserimento/ricerca, elementi a due stati `JCheckBox` e `JList` con `DefaultListModel` inseriti in `JScrollPane` `[Lezioni/16 - Swing.pdf]`, `[Esercizi/Esercizio Lab.pdf]`.
8. **Utilizzo di Lambda, Stream e Pipeline**: Programmazione funzionale dichiarativa applicata sulle collezioni e sui file, utilizzando le interfacce funzionali standard di Java (`Predicate` per i filtri, `Consumer` per le azioni, `Comparator` per ordinamenti multi-campo complessi) e operazioni terminali avanzate `[Lezioni/16 / Lezioni/17 - Lambda Expressions.pdf]`, `[Esercizi/Esercizio Lambda Expressions.pdf]`.
9. **Qualità della documentazione (Specifice Algebriche)**: Relazione d'esame corredata da una **specifica algebrica non assiomatica** (con costruttori, osservatori ed equazioni minimali di riduzione) dell'inventario `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 2]`, `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 103-108]`.
10. **Punteggio bonus**: Complessità del codice in rapporto ai membri del gruppo ed elementi innovativi.

---

## 3. Struttura dei Sorgenti Java nel Workspace
Il codice sorgente è organizzato all'interno dei seguenti package Maven in `/src/main/java/com/mycompany/avventuratestuale/`:

```text
com.mycompany.avventuratestuale
│
├── core                  <-- Logica di Controllo, Parsing e Thread Concorrenti
│   ├── Gioco.java        <-- Classe astratta (Superclasse dell'avventura) [Lezioni/2, Slide 50]
│   ├── Parser.java       <-- Parser delle stopwords con Predicate e algoritmo Levenshtein [Lab 1, p. 6-7]
│   ├── ParserOutput.java <-- Incapsula Comando, Oggetto, OggettoSecondario e inputInvalido
│   ├── Comando.java      <-- Associa TipoComando ai sinonimi
│   ├── TipoComando.java  <-- Enum dei verbi supportati (NORD, PRENDI, APRI, MAPPA, etc.)
│   ├── SalvataggioManager.java <-- Serializzazione/Deserializzazione dello stato [Lezioni/10]
│   └── ThreadTimer.java  <-- Conto alla rovescia Runnable in background (Stanza 5) [Lezioni/15]
│
├── model                 <-- Entity del mondo di gioco
│   ├── Stanza.java       <-- Locazione geografica (Composizione con oggetti) [Lab 1, p. 23-24]
│   ├── Oggetto.java      <-- Elemento interattivo, sinonimi ed oggetti scenici [Lab 1, p. 27]
│   └── Personaggio.java  <-- NPC interattivi (IA Prometeo) [Lab 1, p. 30]
│
├── database              <-- Gestione Database Relazionale H2 e Pattern DAO
│   ├── DatabaseManager.java <-- Connessione H2 Embedded e creazione tabelle [Lezioni/13, Slide 4]
│   ├── Punteggio.java    <-- Classe Entity per i record dei punteggi
│   ├── PunteggioDAO.java <-- Pattern DAO per punteggi tramite query SQL [Esercizio JDBC.pdf]
│   ├── DialogoNode.java  <-- Rappresenta un nodo di dialogo olografico
│   └── DialogoDAO.java   <-- Pattern DAO per caricare i dialoghi dal DB H2 [Lezioni/13, Slide 16]
│
├── socket                <-- Server Socket TCP Multithread [Lezioni/14, Slide 13]
│   ├── ServerComandi.java <-- Accetta le connessioni remote sulla porta 8888
│   └── ClientHandler.java <-- Thread dedicato per ciascun client remoto [Esercizio Socket.pdf]
│
├── ui                    <-- Interfaccia Grafica Swing [Lezioni/16 - Swing.pdf]
│   ├── InterfacciaGioco.java <-- JFrame principale (Console + Inventario + Timer + Menu)
│   ├── DialogInserimento.java <-- JDialog modale d'inserimento giocatori con JCheckBox [Esercizio Lab.pdf]
│   └── DialogRicerca.java <-- JDialog modale con JList e JScrollPane per cercare record [Esercizio Lab.pdf]
│
└── impl                  <-- Avventura Specifica Concreta
    └── LaMiaAvventura.java <-- Estende Gioco.java, definisce la mappa a 7 stanze e la logica di "Protocollo Chimera"
```

---

## 4. Stato delle Funzionalità Già Implementate e Funzionanti

La base di gioco è interamente implementata, pulita e compila senza errori su Java 26. Include le seguenti raffinatezze logiche introdotte per evitare rigidità di gioco:

1. **Mappa Dinamica con Oggetti e Uscite**: Il metodo `LaMiaAvventura.getStanzaDescrizioneCompleta(...)` genera descrizioni estese che mostrano in automatico gli oggetti per terra e le uscite disponibili.
2. **Memoria Contestuale d'Input**: In [`InterfacciaGioco.java`](./src/main/java/com/mycompany/avventuratestuale/ui/InterfacciaGioco.java) la variabile `comandoInAttesaDiTarget` intercetta comandi incompleti (es: `prendi` ➔ chiede *"Cosa vuoi prendere?"* ➔ l'input successivo `tessera` viene accorpato in *"prendi tessera"* ed eseguito normalmente!).
3. **Levenshtein Distance per Typo**: [`Parser.java`](./src/main/java/com/mycompany/avventuratestuale/core/Parser.java) include l'algoritmo dinamico della distanza di Levenshtein. Se l'utente sbaglia a digitare (es. `guada` o `mapp`), il sistema consiglia dinamicamente la parola corretta senza hard-coding.
4. **Dialoghi Olografici via H2 DB**: Il comando **`parla`** (con o senza argomenti) avvia una comunicazione interattiva con Prometeo nella Sala Server, caricando i bivi di dialogo e le opzioni numeriche (`1` e `2`) direttamente dal database H2.
5. **Mappa ASCII**: Il comando `mappa` visualizza uno schema olografico del laboratorio con un indicatore dinamico `[X]` sulla posizione corrente.
6. **Double-use prevent**: Risolti i bug di ripetizione dell'uso della tessera, del cacciavite e del decodificatore.
7. **Bivi dei finali e Registrazione Classifica Swing**: Nel Nucleo di Comando, interagendo con la console, l'utente seleziona uno dei 4 finali e un pop-up di Swing (`JOptionPane.showInputDialog`) gli chiede il nome per salvare automaticamente il punteggio sul DB H2.

---

## 5. Come Procedere con Sviluppo e Raffinamento (Compiti per la Nuova AI)

1. **Leggi i file di progettazione narrativa e tecnica**:
   * Sfoglia il file [**`PROGETTAZIONE_TRAMA_E_ENIGMI.md`**](./Guida_Progetto_MAP/PROGETTAZIONE_TRAMA_E_ENIGMI.md) per comprendere il lore completo e lo schema logico degli enigmi.
   * Sfoglia il file [**`MANUALE_ENTITA_GIOCO.md`**](./Guida_Progetto_MAP/MANUALE_ENTITA_GIOCO.md) per l'elenco completo di tutte le stanze, descrizioni letterarie, oggetti raccoglibili, oggetti scenici e NPC.
2. **Itera e Raffina**:
   * Puoi aggiungere ulteriori bivi di dialogo relazionale nel database modificando il caricamento in `DatabaseManager.java`.
   * Puoi inserire un lettore di file musicali (o suoni di allarme) in background gestito come thread asincrono separato (ottimo elemento di complessità!).
   * Puoi potenziare la console di hacking socket (Capitolo 6) per permettere ai terminali esterni di sbloccare i computer inserendo codici criptati.
3. **Conserva l'architettura pulita**: Qualsiasi nuova classe o modifica deve rispettare i principi OOP, l'Information Hiding dei package e le direttive d'esame.

Il progetto è pronto per essere preso in consegna ed elevato all'eccellenza assoluta! Buon lavoro.
