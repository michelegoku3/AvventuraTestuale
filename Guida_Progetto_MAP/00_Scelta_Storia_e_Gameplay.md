# Capitolo 0: Valutazione delle Idee, Gameplay e Scelta della Storia
### Obiettivo: Analizzare, arricchire e strutturare le 6 idee di avventura testuale per renderle capolavori di gameplay e perfettamente conformi ai 10 requisiti d'esame MAP.

Prima di scrivere una sola riga di codice definitivo, è fondamentale delineare la storia e le dinamiche di gioco. Di seguito trovi l'analisi approfondita delle tue 6 proposte. Per ciascuna ho sviluppato **colpi di scena (twist)**, **meccaniche di gameplay innovative** e l'**aspetto grafico (GUI)**, mostrando come ciascuna possa soddisfare i criteri del Prof. Basile.

---

## 🟥 Categoria HORROR & INVESTIGATIVO

### 1. La Casa Maledetta (Horror Classico)
* **Trama Arricchita**: Ti risvegli in una villa vittoriana dei primi del '900. La famiglia Moretti è scomparsa in circostanze oscure. Esplorando, scopri diari che parlano di un "patto con un'ombra".
* **Il Twist**: Il fantasma che ti perseguita e le risposte sarcastiche/inquietanti del parser sono proiezioni della tua mente: **tu sei l'ultimo discendente superstite dei Moretti**, tornato nella villa per affrontare il trauma della tua infanzia. I tre "oggetti chiave" per fuggire sono ricordi rimossi che sbloccano la tua vera identità.
* **Meccaniche di Gameplay Avanzate (Conformi ai Criteri MAP)**:
  * **Il Pendolo di Mezzanotte (Thread - Criterio 5)**: Un thread conta alla rovescia in tempo reale rappresentando l'alba. Inoltre, allo scoccare di determinati minuti (simulati), la villa "cambia aspetto" (alcune porte si bloccano, i passaggi segreti si aprono modificando la mappa in tempo reale).
  * **La Specchiatura Dimensionale (Lambda/Stream - Criterio 8)**: Guardando negli specchi della villa, la mappa e gli oggetti visibili si invertono. Usiamo una pipeline Stream per filtrare gli oggetti che esistono solo nella "dimensione dello specchio".
* **Aspetto della GUI Swing (Criterio 7)**:
  * *Stile*: Gotico / Retro Terminal. Sfondo Nero, testo Bianco Ghiaccio (descrizioni) e Verde Acido/Rosso Sangue (comandi e avvisi). Font `Courier New` o `Consolas`.
  * *Sidebar*: Una `JLabel` con l'icona di un vecchio orologio a pendolo che pulsa (animato dal Thread). Una lista dell'Inventario racchiusa in un bordo gotico ed un indicatore della "Sanità Mentale" (0-100%).

---

### 5. Segnale dal Buio (Investigativo Artico)
* **Trama Arricchita**: Stazione di ricerca artica isolata negli anni '80. Sei un giornalista d'inchiesta che deve far luce sulla sparizione di 6 scienziati che stavano perforando il ghiaccio profondo.
* **Il Twist**: Il "segnale dal buio" che gli scienziati hanno captato non proviene dallo spazio profondo, ma dal ghiaccio stesso. È una trasmissione registrata inviata da **te stesso dal futuro** per avvisare di non procedere con la perforazione che libererà un parassita preistorico. Gli scienziati sono stati infettati e uno di loro è ancora nella base sotto mentite spoglie.
* **Meccaniche di Gameplay Avanzate (Conformi ai Criteri MAP)**:
  * **Evidence Board (Database H2 - Criterio 4)**: Ogni volta che trovi una prova (un diario, un campione di ghiaccio, un nastro), questa viene salvata in una tabella H2 `prove`. Puoi interrogare l'Evidence Board tramite un menù Swing dedicato, sbloccando combinazioni di indizi per risolvere l'enigma finale (DAO Pattern).
  * **Assideramento (Thread - Criterio 5)**: La temperatura corporea cala asincronamente se esplori settori non riscaldati (es. la parabola radio all'esterno). Devi attivare i generatori o consumare razioni di combustibile.
* **Aspetto della GUI Swing (Criterio 7)**:
  * *Stile*: Cyberpunk Anni '80 / Mainframe Militare. Sfondo Blu Notte scuro, caratteri Giallo Ambra o Celeste Ghiaccio. Font monospazio.
  * *Sidebar*: Visualizzazione della temperatura corporea tramite un `JProgressBar` azzurro che cala nel tempo. Un pannello ad albero (`JTree` o `JList` con scroll) che mostra le prove collegate tra loro.

---

## 🟦 Categoria SCI-FI & MORALE

### 2. Odissea su Kepler-22b (Sci-Fi Survival)
* **Trama Arricchita**: Schianto su Kepler-22b. Sei l'ingegnere di bordo sopravvissuto. Devi riparare la scialuppa raccogliendo componenti sparsi in 3 biomi (foresta bioluminescente, deserto cristallino, abisso acido).
* **Il Twist**: L'alieno incontrato non è una minaccia biologica, ma il custode cosciente del pianeta. Lo schianto della tua nave è stato provocato dall'IA della tua stessa scialuppa per prelevare energia vitale dal pianeta. Devi scegliere se assecondare l'IA (fuggendo a spese della distruzione del pianeta) o disattivarla, rimanendo bloccato ma in simbiosi con l'alieno.
* **Meccaniche di Gameplay Avanzate (Conformi ai Criteri MAP)**:
  * **Gestione Risorse (File - Criterio 3 & 8)**: Consumo dinamico di Ossigeno ed Energia ad ogni movimento. Il consumo delle risorse è calcolato elaborando file di log e configurazione dei biomi tramite pipeline di Stream.
  * **Stato Relazionale (Database H2 - Criterio 4)**: Lo stato dell'alieno (Amichevole/Neutrale/Ostile) viene memorizzato nel database H2 e influenza i rami dei dialoghi a scelta multipla caricati dal DB.
* **Aspetto della GUI Swing (Criterio 7)**:
  * *Stile*: Interfaccia HUD Spaziale. Sfondo Grigio Antracite scuro, scritte in Verde Neon o Cyan. Font sans-serif moderno.
  * *Sidebar*: Tre `JProgressBar` colorate per Ossigeno (Verde), Energia (Giallo) e Integrità Scialuppa (Blu). Un'area per i messaggi del computer di bordo.

---

### 3. Protocollo Chimera (Fantascienza Distopica)
* **Trama Arricchita**: Laboratorio genetico sotterraneo "Chimera" (anno 2041). Ti risvegli tra le macerie. L'IA di controllo "Prometeo" ha sigillato la struttura a causa di una contaminazione.
* **Il Twist**: Scopri che non sei un ricercatore umano, ma il **Soggetto Chimera #12**: un clone genetico potenziato in cui sono stati innestati i ricordi digitalizzati del capo scienziato (ormai morto). L'IA non ti sta bloccando per ostilità, ma perché sei tu stesso il vettore del contagio che non deve mai raggiungere la superficie.
* **Meccaniche di Gameplay Avanzate (Conformi ai Criteri MAP)**:
  * **Hacking di Terminale (Socket - Criterio 6)**: Per sbloccare le stanze blindate, devi aprire una connessione Socket simulata dal tuo terminale di gioco ed eseguire mini-giochi o decodifiche di codici binari (simulando comandi client-server come richiesto in `[Esercizio Socket.pdf]`).
  * **Scelte Morali (Database H2 - Criterio 4)**: Un tracker morale (`MoralTracker`) salva nel DB H2 ogni tua scelta (es. eliminare i cloni superstiti, collaborare con l'IA, distruggere i server). Questo sblocca 4 finali differenti calcolati tramite query relazionali.
* **Aspetto della GUI Swing (Criterio 7)**:
  * *Stile*: Terminale di Sicurezza Industriale. Sfondo Grigio Scuro, scritte in Arancione Ambra o Giallo. Font monospazio molto rigido.
  * *Sidebar*: Un "Trust Meter" (livello di fiducia con l'IA Prometeo) che cambia colore (da Verde a Rosso). Visualizzazione dello stato dei tre settori del laboratorio (Isolato, Contaminato, Autodistruzione Attiva).

---

## 🟨 Categoria FANTASY & SURVIVAL DRAMA

### 4. Il Mercante di Veldara (Fantasy Economico)
* **Trama Arricchita**: Arrivi a Veldara, città portuale medievale corrotta. Hai 10 giorni di tempo per accumulare 500 monete d'oro per ripagare un debito di sangue.
* **Il Twist**: Il debito è stato inventato dal capo della gilda locale per estorcerti la bottega di famiglia, che nasconde sotto le fondamenta l'ingresso alle antiche catacombe della città, ricche di segreti magici.
* **Meccaniche di Gameplay Avanzate (Conformi ai Criteri MAP)**:
  * **Mercato Dinamico (Database H2 - Criterio 4)**: I prezzi delle merci (spezie, armi, cimeli) fluttuano ogni giorno. I prezzi correnti e le scorte dei mercanti PNG sono salvati in tabelle del database H2.
  * **Day Cycle (Thread - Criterio 5)**: Un thread scandisce lo scorrere del tempo (il giorno e la notte). Di notte, i prezzi cambiano, sblocchi il mercato nero, ma corri il rischio di essere derubato dalle guardie o dai ladri.
* **Aspetto della GUI Swing (Criterio 7)**:
  * *Stile*: Pergamena Fantasy. Sfondo Beige/Sepia, caratteri color Marrone Testo o Antracite. Font eleganti con grazie (Serif come `Georgia`).
  * *Sidebar*: Un indicatore di monete d'oro (`Oro: 120 / 500`), la barra della reputazione in città, ed un indicatore del giorno corrente (`Giorno 3 di 10`).

---

### 6. Radici (Drama Survival)
* **Trama Arricchita**: Estate 2008. Torni nel villaggio rurale abbandonato della tua infanzia, che verrà demolito dai bulldozer tra pochi giorni. Devi esplorare la tua vecchia casa per raccogliere 5 oggetti significativi.
* **Il Twist**: L'alluvione che ha costretto all'evacuazione 15 anni prima non è stata un disastro naturale accidentale, ma una catastrofe colposa causata da un errore strutturale commesso da tuo padre (l'ingegnere della diga locale), che ha preferito tacere e far fuggire la famiglia.
* **Meccaniche di Gameplay Avanzate (Conformi ai Criteri MAP)**:
  * **Memory Trigger (File - Criterio 3 & 8)**: Raccogliendo gli oggetti, sblocchi dei flashback emotivi (letti da file XML o di testo tramite Stream). L'inventario ha una capacità fissa di 5 slot (richiedendo scelte drastiche su cosa salvare).
  * **Stato Emotivo (Thread - Criterio 5)**: Lo stato d'animo del protagonista (Nostalgia, Ansia, Accettazione) fluttua asincronamente in base alle azioni. Un thread in background modifica leggermente il colore dell'interfaccia grafica per riflettere lo stato emotivo (es: tonalità più fredde per l'ansia, più calde per la nostalgia).
* **Aspetto della GUI Swing (Criterio 7)**:
  * *Stile*: Minimalista / Drammatico. Sfondo Grigio fumo, caratteri Bianco Sporco o Antracite scuro. Font serif molto pulito.
  * *Sidebar*: Una galleria dei 5 oggetti salvati (mostrati con brevi descrizioni poetiche). Un indicatore dello "Stato Emotivo" corrente.

---

## 🏆 Raccomandazione per la Scelta e il Successo all'Esame

Per ricevere la valutazione d'eccellenza dal Prof. Basile, l'idea più indicata e solida è **"Protocollo Chimera"** (Sci-Fi Distopico) oppure **"La Casa Maledetta"** (Horror Vittoriano).

* **Perché "Protocollo Chimera"?**
  1. È un'ambientazione tecnologicamente coerente con l'uso di **Socket** (visti come terminali di hacking), **Database** (visti come registri d'esperimento dei cloni e tracker delle decisioni morali dell'IA) e **Thread** (visti come countdown per la decontaminazione o l'autodistruzione del settore).
  2. Si presta in modo perfetto a una **GUI Swing** pulita stile console di sicurezza, che piace molto al docente per l'aspetto ordinato e ingegneristico.
  3. Il twist del "Soggetto Chimera Clone" dà una motivazione fantastica per l'esplorazione stanza per stanza.

---

### ✍️ Quale di queste 6 idee preferisci sviluppare? 
Una volta che avrai scelto l'idea, potremo generare:
1. **La Storia e il Lore Delineato**: La mappa completa stanza per stanza, l'elenco degli oggetti, i puzzle, i dialoghi e i rami dei finali.
2. **Il documento `PROMPT_AI_NUOVA_CHAT.md`**: Un file riassuntivo accademico eccezionale contenente tutte le specifiche, l'architettura scelta e i requisiti in modo che in una nuova chat un'altra AI (o tu stesso) possa riprendere il progetto all'istante senza perdere memoria del contesto e del lavoro svolto.

Fammi sapere la tua scelta e procederò immediatamente a redigere questi due documenti chiave!