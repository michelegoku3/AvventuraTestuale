# Guida Universitaria Completa al Progetto MAP (M-Z)
### Corso di Metodi Avanzati di Programmazione — Università degli Studi di Bari "Aldo Moro"
**Docente: Prof. Pierpaolo Basile**

Questa risorsa è una guida di riferimento accademica completa e aggiornata per la progettazione, lo sviluppo e la documentazione del caso di studio dell'esame di **Metodi Avanzati di Programmazione (MAP)**.

Questa versione della guida include una revisione totale basata su **tutti i file e gli esercizi aggiornati nella repository**, comprendendo le nuove lezioni su **Java SWING** `[Lezioni/16 - JAVA - Swing.pdf]`, i nuovi esercizi pratici di laboratorio **Esercizio Lab** `[Esercizi/Esercizio Lab.pdf]` e **Esercizio Lambda Expressions** `[Esercizi/Esercizio Lambda Expressions.pdf]`.

Ogni frammento di codice, scelta architetturale e modello teorico presentati in questa guida sono ricavati **esclusivamente** dai materiali didattici ufficiali distribuiti dal docente. Ogni capitolo include **citazioni puntuali alle fonti** (con indicazione esatta del file e del numero di pagina o slide) per garantirti la massima conformità logica e formale ai requisiti di valutazione del docente.

---

## 📋 Requisiti d'Esame e Criteri di Valutazione (Da 0 a 50 Punti)
Come specificato nel documento d'esame ufficiale `MAP M-Z - Documentazione Progetto - Template.pdf (p. 3)` e nella scheda di presentazione del corso `Corso.pdf (p. 1-2, 9-10)`, il progetto (caso di studio) è valutato su una scala da **0 a 50 punti** (poi rapportato in trentesimi) secondo **10 criteri specifici** (da 0 a 5 punti ciascuno). 

Di seguito viene mostrato come ciascun criterio di valutazione sia direttamente affrontato in questa guida:

1. **Qualità dell'avventura (Trama, Mappa e Logica)**
   * *Cosa richiede il prof*: Un'avventura coerente con trama ben definita, dizionario/sinonimi, gestione di ambienti comunicanti e risoluzione di enigmi `[Lab 1 - Introduzione.pdf, p. 17-18, 34, 37]`.
   * *Dove trovarlo*: ➔ [**Capitolo 1: Architettura Software e OOP**](./01_Architettura_e_OOP.md)
2. **Qualità della programmazione ad oggetti (Principi di Astrazione e OOP)**
   * *Cosa richiede il prof*: Saper applicare l'astrazione dati tramite classi, incapsulamento (information hiding), ereditarietà, interfacce, polimorfismo e composizione `[Corso.pdf, p. 1-2]`, `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf]`, `[Lezioni/2 - Paradigma OO.pdf]`.
   * *Dove trovarlo*: ➔ [**Capitolo 1: Architettura Software e OOP**](./01_Architettura_e_OOP.md)
3. **Utilizzo dei File**
   * *Cosa richiede il prof*: Saper gestire l'input/output tramite byte stream o character stream e la persistenza degli oggetti (serializzazione/deserializzazione) `[Lezioni/10 - JAVA - Input Output.pdf]`, `[Esercizio Input Output.pdf, p. 1]`.
   * *Dove trovarlo*: ➔ [**Capitolo 3: Persistenza e Gestione File**](./03_File_e_Salvataggi.md)
4. **Utilizzo di Database/JDBC**
   * *Cosa richiede il prof*: Connessione JDBC ad un database relazionale embedded (H2 consigliato), creazione automatica di tabelle, esecuzione di query SQL e inserimenti tramite DAO `[Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 4-5]`, `[Esercizio JDBC.pdf, p. 1]`.
   * *Dove trovarlo*: ➔ [**Capitolo 4: Database e Connettività JDBC**](./04_Database_JDBC.md)
5. **Utilizzo dei Thread e della Programmazione Concorrente**
   * *Cosa richiede il prof*: Creazione di classi attive tramite `Thread` o `Runnable`, gestione della sincronizzazione per prevenire *Thread Interference* e *Memory Consistency Errors* nell'accesso a risorse condivise `[Lezioni/14 - JAVA - Programmazione Concorrente.pdf / Lezioni/15 - JAVA - Programmazione Concorrente.pdf, Slide 4, 20-24]`.
   * *Dove trovarlo*: ➔ [**Capitolo 5: Thread e Programmazione Concorrente**](./05_Thread_e_Timer.md)
6. **Utilizzo delle Socket e/o REST**
   * *Cosa richiede il prof*: Programmazione in rete client-server tramite Socket TCP/IP basate su flussi di input/output `[Lezioni/14 - JAVA - Programmazione in Rete.pdf / Lezioni/15 - JAVA - Programmazione in Rete.pdf]`, `[Esercizio Socket.pdf, p. 1]`.
   * *Dove trovarlo*: ➔ [**Capitolo 6: Programmazione in Rete con Socket**](./06_Socket_e_Rete.md)
7. **Utilizzo delle SWING**
   * *Cosa richiede il prof*: Progettazione e creazione di interfacce grafiche utente tramite componenti grafici Swing (`JFrame`, `JDialog` modali, `JMenuBar`, `JMenuItem`, `JList` con scroll, `JCheckBox` e `AbstractAction`) `[Lezioni/16 - JAVA - Swing.pdf]`, `[Esercizi/Esercizio Lab.pdf, p. 2-5]`.
   * *Dove trovarlo*: ➔ [**Capitolo 2: Interfaccia Grafica con Swing**](./02_Swing_GUI.md)
8. **Utilizzo delle Lambda Expression, Stream e Pipeline**
   * *Cosa richiede il prof*: Utilizzo di espressioni lambda, interfacce funzionali (`Predicate`, `Consumer`, `Comparator`) e pipeline di elaborazione dichiarativa delle collezioni/file (`map`, `filter`, `mapToDouble`, `sum`, `max`, `Collectors.groupingBy`) `[Lezioni/16 - JAVA - Lambda Expressions.pdf / Lezioni/17 - JAVA - Lambda Expressions.pdf]`, `[Esercizi/Esercizio Lambda Expressions.pdf, p. 1-3]`.
   * *Dove trovarlo*: ➔ [**Capitolo 1, 2, 3, 4, 5, 6** (Integrate in tutti i capitoli di codice)]
9. **Qualità della documentazione (Relazione + Specifica Algebrica Non Assiomatica)**
   * *Cosa richiede il prof*: Relazione tecnica basata sul template d'esame. Deve contenere una **specifica algebrica non assiomatica** (con costruttori, osservatori ed equazioni minimali di riduzione) di una struttura dati d'appoggio `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 2]`, `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 104-108]`.
   * *Dove trovarlo*: ➔ [**Capitolo 7: Specifica Algebrica (Non Assiomatica)**](./07_Specifica_Algebrica.md)
10. **Punteggio Bonus**
    * *Cosa richiede il prof*: Complessità architetturale globale ed elementi innovativi implementati con intelligenza ed eleganza `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`.

---

## 📖 Indice dei Capitoli

* [**Capitolo 1: Architettura Software e OOP**](./01_Architettura_e_OOP.md) — Definizione di mappa, stanze, oggetti, parser sintattico, e integrazione delle Lambda Expressions avanzate.
* [**Capitolo 2: Interfaccia Grafica con Swing**](./02_Swing_GUI.md) — Sviluppo del JFrame, JDialogs modali di ricerca/inserimento, barre di menu e JList integrate con H2 Database.
* [**Capitolo 3: Persistenza e Gestione File**](./03_File_e_Salvataggi.md) — Serializzazione dello stato di gioco e lettura file tramite Stream.
* [**Capitolo 4: Database e Connettività JDBC**](./04_Database_JDBC.md) — Integrazione di H2 Database con architettura DAO completa.
* [**Capitolo 5: Thread e Programmazione Concorrente**](./05_Thread_e_Timer.md) — Sviluppo di un timer real-time asincrono, thread-safe e sincronizzato.
* [**Capitolo 6: Programmazione in Rete con Socket**](./06_Socket_e_Rete.md) — Connessione Socket TCP multi-client per controllo remoto e spettatori.
* [**Capitolo 7: Specifica Algebrica (Non Assiomatica)**](./07_Specifica_Algebrica.md) — La teoria algebrica formale, la matrice degli operatori e la dimostrazione per l'inventario di gioco.

---

## 🎓 Note sullo svolgimento dell'Esame Orale
Ricordati che, come indicato in `MAP M-Z - Documentazione Progetto - Template.pdf (p. 3)`, la presentazione del caso di studio e la demo live dureranno al massimo **20 minuti**. Durante la presentazione, ogni membro del gruppo sarà interrogato su tutti gli argomenti teorici del corso (es. astrazione dati, polimorfismo, gestione della memoria dei thread, funzionamento interno del driver JDBC). Questa guida ti fornisce gli strumenti concettuali per rispondere in modo impeccabile.
