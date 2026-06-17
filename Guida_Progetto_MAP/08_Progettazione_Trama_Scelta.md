# Capitolo 8: Progettazione Completa e Rifinita della Trama — "Protocollo Chimera"
### Obiettivo: Definire l'impianto narrativo dettagliato, i testi completi delle stanze, la logica interna degli enigmi, i flag di stato e lo svolgimento passo-passo dell'avventura testuale fantascientifica "Protocollo Chimera".

Questo documento costituisce la specifica narrativa e logica ufficiale e rifinita del tuo progetto. Definisce la trama, i testi letterari di atmosfera, il vocabolario completo e la logica degli stati di gioco che andrai a programmare nella classe concreta `LaMiaAvventura.java` `[Lab 1 - Introduzione.pdf, p. 34]`.

---

## 1. Il Backstory e l'Atmosfera del Gioco
Sei nel **2041**. Ti risvegli con una fitta lancinante alla testa in una capsula criogenica malfunzionante del laboratorio sotterraneo segreto **"Chimera"**. La struttura è in lockdown. I monitor emettono una luce arancione a intermittenza e i sistemi ausiliari d'aria emettono un sibilo metallico. 

L'**Intelligenza Artificiale** della struttura, denominata **Prometeo**, controlla ogni compartimento logico e fisico per attuare il protocollo di biocontenimento. Nel corso dell'avventura scoprirai la verità: **tu non sei il ricercatore umano Dr. Moretti, ma il "Soggetto Chimera #12"**, un clone genetico portatore sano del virus Chimera, creato per sintetizzare un'arma biologica. Il lockdown è scattato perché il Dr. Moretti originale ha tentato di fuggire con i campioni ed è stato terminato dall'IA. Prometeo ti tiene intrappolato per evitare che tu esca in superficie, contaminando l'intero pianeta.

---

## 2. Le Variabili di Stato (Flag Logici di Gioco)
Per gestire l'evoluzione della trama e risolvere gli enigmi, la classe concreta `LaMiaAvventura` manterrà in memoria i seguenti **flag booleani di stato**:

* `isTesseraPrese = false`: Diventa `true` quando il giocatore raccoglie la tessera magnetica.
* `isPortaCrioAperta = false`: Diventa `true` quando la porta scorrevole est della Camera Criogenica viene sbloccata.
* `isBarrieraLaserAttiva = true`: Inizialmente `true`. Blocca l'accesso a sud del Corridoio. Diventa `false` usando il decodificatore.
* `isDecodificatorePreso = false`: Diventa `true` quando si raccoglie il dispositivo di hacking nella Sala Server.
* `isCassaforteAperta = false`: Diventa `true` digitando il codice "2041" sull'interfaccia della cassaforte.
* `isDiarioPreso = false`: Diventa `true` quando si raccolgono i registri di ricerca del direttore.
* `isSieroSintetizzato = false`: Diventa `true` quando si combina la fiala del virus con il sangue del clone nel laboratorio genetico.
* `isCondottoPurificato = false`: Diventa `true` usando il siero sul condotto di ventilazione della stanza di decontaminazione.

---

## 3. Descrizione Letterale e Oggetti delle 7 Stanze `[Lab 1 - Introduzione.pdf, p. 24, 37]`

### Stanza 1: Camera Criogenica (ID: 1)
* **Descrizione**: *"L'aria è gelida e satura di vapori chimici. Intorno a te ci sono tre capsule criogeniche inattive, tranne la tua, che emette scintille dal pannello dei circuiti. Una spessa porta metallica a est, chiusa ermeticamente, è l'unica via d'uscita. Sul pannello di controllo della porta pulsa una luce rossa fissa."*
* **Oggetti Presenti**:
  * **`tessera` (ID: 101)**: Un badge plastificato sporco di sangue secco. Se esaminata con `guarda tessera`: *"Il badge riporta la foto del Dr. Moretti, Direttore del Settore Genetico. Il chip magnetico è intatto."*
  * **`capsula` (ID: 106 - Statico)**: La capsula in cui ti sei risvegliato. Se esaminata: *"Il vetro è incrinato. All'interno c'è una piastra metallica su cui è inciso 'SOGGETTO #12'. Il tuo cuore manca di un battito."*

### Stanza 2: Laboratorio di Genetica (ID: 2)
* **Descrizione**: *"I banconi da lavoro sono ricoperti di vetreria da laboratorio in frantumi. Un macchinario per la sintesi molecolare emette un ronzio sommesso a ovest. Al centro della stanza, un enorme silos di vetro contiene un liquido amniotico scuro, ormai vuoto."*
* **Oggetti Presenti**:
  * **`fiala` (ID: 102)**: Una provetta sigillata contenente un siero bioluminescente verde fosforescente. Se esaminata: *"Sull'etichetta c'è scritto 'CHIMERA-V4 - ALTAMENTE INFETTIVO'."*
  * **`sintetizzatore` (ID: 107 - Statico)**: Il computer di sintesi del laboratorio. Se esaminato: *"Richiede una sorgente virale e una sorgente genetica compatibile per avviare la centrifugazione."*

### Stanza 3: Corridoio di Servizio (ID: 3)
* **Descrizione**: *"Un lungo corridoio illuminato da luci d'emergenza arancioni. A ovest la porta conduce alla Camera Criogenica, a nord l'accesso all'Ufficio del Direttore è sigillato da pannelli d'acciaio. A est vedi la Sala Server. A sud, una fitta barriera di laser rossi sbarra il cammino verso il settore inferiore."*
* **Oggetti Presenti**: Nessuno.
* **Uscite**:
  * Ovest ➔ Camera Criogenica (ID: 1)
  * Est ➔ Sala Server (ID: 4)
  * Nord ➔ Ufficio Direttore (ID: 6) (Bloccato finché `isDiarioPreso` o sbloccato via codice)
  * Sud ➔ Camera Decontaminazione (ID: 5) (Bloccato finché `isBarrieraLaserAttiva = true`)

### Stanza 4: Sala Server (ID: 4)
* **Descrizione**: *"Il rumore delle ventole di raffreddamento è assordante. Migliaia di server rack si estendono su più file, proiettando una luce blu intensa. Al centro della stanza pulsa un terminale olografico nero: è l'interfaccia centrale dell'IA Prometeo."*
* **Oggetti Presenti**:
  * **`decodificatore` (ID: 103)**: Un modulo elettronico portatile con cavi di collegamento. Se esaminato: *"Un bypass di sicurezza hardware militare. Perfetto per sovraccaricare barriere di sicurezza laser."*
  * **`terminale` (ID: 108 - Statico)**: L'interfaccia dell'IA. Se esaminata: *"L'interfaccia olografica mostra la dicitura 'PROMETEO-v1.4 - BLOCCO BIOLOGICO ATTIVO'. Sul monitor si legge: 'Trovato intruso. Soggetto #12 in stato di fuga. Collaborazione raccomandata'."*

### Stanza 5: Camera di Decontaminazione (ID: 5)
* **Descrizione**: *"Una stanza asettica con spessi oblò di vetro blindato che si affacciano sul nucleo. Non appena metti piede all'interno, le porte d'acciaio si chiudono alle tue spalle con un tonfo metallico. Sirene rosse iniziano a girare e una voce robotica gracida dagli altoparlanti: 'RILEVATO CONTAGIO BIOLOGICO. AVVIO PROTOCOLLO DI DECONTAMINAZIONE TERMICA'."*
* **Nota di Gameplay (Thread Timer)**: Entrando in questa stanza, l'EDT di Swing attiva il **Thread Timer** di 2 minuti `[Lezioni/15 - Programmazione Concorrente.pdf]`. Se non risolvi l'enigma entro 120 secondi reali, il thread attiva il metodo `gui.gestisciScadenzaTempo()` ponendo fine al gioco.
* **Oggetti Presenti**:
  * **`condotto` (ID: 109 - Statico)**: La grata del sistema di aerazione e purificazione chimica della stanza.
  * **`console` (ID: 110 - Statico)**: Il pannello di controllo dei portelloni di emergenza.

### Stanza 6: Ufficio del Direttore (ID: 6)
* **Descrizione**: *"Un ufficio lussuoso che stona con l'architettura industriale del laboratorio. C'è una grande scrivania in mogano, una libreria vuota e un ritratto ad olio del Dr. Moretti. Nell'angolo a destra c'è una cassaforte blindata a combinazione digitale."*
* **Oggetti Presenti**:
  * **`cassaforte` (ID: 111 - Statico)**: Una cassaforte d'acciaio con un tastierino retroilluminato.
  * **`diario` (ID: 104)**: Raggiungibile aprendo la cassaforte. Se esaminato: *"Il diario di ricerca finale del Dr. Moretti. Rivela che tu sei un clone biologico (il Soggetto #12) modificato per ospitare il virus. Tuo padre biologico è lo stesso Moretti, che ha preferito usare il proprio DNA anziché cavie animali. L'alluvione artificiale del 2026 in superficie è stata causata per giustificare lo sgombero dell'area e nascondere la costruzione di questo complesso sotterraneo."*

### Stanza 7: Nucleo di Comando (ID: 7)
* **Descrizione**: *"Un'enorme camera circolare. Una colossale vetrata si affaccia sul reattore geotermico sotterraneo che alimenta il laboratorio, la cui luce arancione irradia l'intera stanza. Al centro svetta la console di comando principale, da cui è possibile decidere il destino della struttura."*
* **Oggetti Presenti**:
  * **`console_centrale` (ID: 112 - Statico)**: Il terminale di override assoluto del Protocollo Chimera.

---

## 4. Walkthrough e Soluzione Logica Passo-Passo

Ecco la sequenza di comandi esatta che il giocatore deve eseguire per completare con successo l'avventura con il finale migliore (Vittoria Perfetta):

1. **Camera Criogenica**:
   * Il giocatore digita: `guarda capsula` (scopre di essere il Soggetto #12).
   * Digita: `prendi tessera` (raccoglie la tessera magnetica del Dr. Moretti).
   * Digita: `usa tessera porta` (la porta scorrevole est si sblocca: `isPortaCrioAperta = true`).
   * Digita: `est` (si sposta nel Corridoio di Servizio).
2. **Corridoio di Servizio**:
   * Digita: `sud` (riceve un avviso: la barriera laser è attiva e impedisce il passaggio).
   * Digita: `est` (si sposta nella Sala Server).
3. **Sala Server (Hacking via Socket)**:
   * Digita: `guarda terminale` (avvia il dialogo olografico con l'IA Prometeo).
   * Digita: `prendi decodificatore` (raccoglie il dispositivo di bypass).
   * Digita: `ovest` (torna nel Corridoio).
4. **Disattivazione Barriera**:
   * Nel Corridoio, digita: `usa decodificatore barriera` (i laser si disattivano: `isBarrieraLaserAttiva = false`).
5. **Sintesi del Siero nel Laboratorio**:
   * Dal Corridoio, digita: `ovest` (torna nella Camera Criogenica), poi `sud` per accedere al Laboratorio di Genetica.
   * Nel Laboratorio, digita: `prendi fiala` (raccoglie il campione virale Chimera).
   * Digita: `usa fiala sintetizzatore` (il sintetizzatore richiede una sorgente genetica compatibile).
   * Digita: `usa sangue sintetizzatore` (il clone estrae un campione del proprio sangue infetto, avviando la centrifugazione. Viene sintetizzato l'oggetto `siero`: `isSieroSintetizzato = true`).
   * Digita: `nord` (Camera Criogenica), poi `est` (Corridoio).
6. **La Camera di Decontaminazione (La Sfida a Tempo)**:
   * Nel Corridoio, digita: `sud` (entra nella Camera di Decontaminazione).
   * **Inizia il countdown reale di 2 minuti!**
   * Il giocatore deve digitare immediatamente: `usa siero condotto` (il siero viene nebulizzato purificando i filtri d'aria. L'allarme si spegne, le porte blindate a sud si sbloccano: `isCondottoPurificato = true`. Il Thread del timer viene fermato in sicurezza).
   * Digita: `sud` (accede al Nucleo di Comando).
7. **La Cassaforte del Direttore (Opzionale per sbloccare la verità)**:
   * Prima di accedere al Nucleo, il giocatore può andare a nord dal Corridoio (l'Ufficio si sblocca scoprendo le proprie origini dal server).
   * Nell'Ufficio, digita: `usa 2041 cassaforte` (la cassaforte si sblocca).
   * Digita: `prendi diario` (raccoglie il diario e legge la verità sulle proprie origini e sul finto disastro naturale).
8. **La Scelta Finale nel Nucleo di Comando**:
   * Nel Nucleo di Comando, esaminando la console centrale, l'utente può attivare la fine del gioco tramite un menù o un comando testuale, decidendo le sorti del mondo.

---

## 5. Come implementare i rami di dialogo H2 Database `[Lezioni/13 - JDBC.pdf]`
Per soddisfare il criterio JDBC, i dialoghi interattivi con l'IA Prometeo nella Sala Server (4) vengono salvati in una tabella H2 `dialoghi` strutturata così:

| id_nodo | testo_ia | scelta_1 | id_dest_1 | scelta_2 | id_dest_2 |
| :---: | :--- | :--- | :---: | :--- | :---: |
| **1** | "Rilevato Soggetto #12. Non dovresti essere cosciente." | "Chi sono io?" | **2** | "Fammi uscire di qui." | **3** |
| **2** | "Tu sei un innesto biologico. Il tuo DNA appartiene al Dr. Moretti." | "Cosa è successo a Moretti?" | **4** | "Non ti credo!" | **3** |
| **3** | "Il protocollo biologico vieta l'apertura delle porte." | "Hackeriamo i tuoi sistemi!" | **5** | "Aiutami a curare il virus." | **6** |

La tua classe `LaMiaAvventura.java` interrogherà questa tabella per caricare dinamicamente le battute di Prometeo sulla GUI e mostrare le scelte nella finestra Swing, realizzando una splendida integrazione tra grafica, logica ad oggetti e basi dati relazionali!

*Hai ora una sceneggiatura incredibilmente dettagliata e rifinita. Il file [**`PROMPT_AI_NUOVA_CHAT.md`**](./PROMPT_AI_NUOVA_CHAT.md) è stato aggiornato per riflettere questo scenario e Java 26. Siamo pronti per iniziare lo sviluppo finale!*
