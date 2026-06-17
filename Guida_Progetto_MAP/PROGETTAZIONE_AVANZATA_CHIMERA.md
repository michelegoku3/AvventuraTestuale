# Progettazione Avanzata — "Protocollo Chimera"
### Documento di Specifica Narrativa, Gameplay e Roadmap delle Funzionalità da Aggiungere

Grazie al log della tua prima sessione di gioco, abbiamo individuato alcuni punti chiave del design che possiamo migliorare per rendere il gioco un'esperienza di altissimo livello (Criterio 1 - Qualità dell'Avventura) ed evitare incongruenze nell'immersione di gioco (come il fallback della descrizione quando l'utente guarda elementi decorativi della stanza).

---

## 1. Analisi dei Bug e dei Miglioramenti dal tuo Playtest

Durante il tuo test, sono emerse tre situazioni tipiche dei giochi testuali che risolveremo nelle prossime iterazioni di codice:

### A. Il "Bug" degli Oggetti Scenici (`guarda silos`, `guarda macchinario`)
* **Problema rilevato**: Nel Laboratorio di Genetica hai digitato `guarda silos` e `guarda macchinario`, ma il gioco ha ristampato l'intera descrizione della stanza.
* **Perché succede?** Il parser e la stanza non sanno cosa sia il "silos" o il "macchinario". Trattandosi di elementi descrittivi immobili, il parser restituisce `null` come oggetto, e la logica del comando `GUARDA` ripiega sulla descrizione globale della stanza.
* **Soluzione**: Registreremo degli **Oggetti Scenici (Scenery Objects)** non prendibili (`setPrendibile(false)`) ma esaminabili, posizionandoli nelle stanze. In questo modo, digitando `guarda silos`, riceverai una descrizione ad hoc (es. *"Un silos cilindrico riempito di liquido conservativo torbido. Sul fondo noti un'etichetta lacerata."*).

### B. Comando `mappa` non riconosciuto
* **Problema rilevato**: Hai digitato `mappa` ricevendo un messaggio di errore.
* **Soluzione**: Aggiungeremo il comando `MAPPA` (e sinonimi "mappa", "m", "radar") per stampare a schermo un fantastico schema ASCII dell'intera planimetria del laboratorio, evidenziando graficamente con una crocetta `[X]` la stanza in cui ti trovi in quel momento.

---

## 2. Il Lore e la Storia Delineata

### I Personaggi (NPC)
1. **Prometeo (L'IA Centrale)**: Sistema olografico situato nella Sala Server. Parla con frasi asettiche, sarcastiche ed estremamente dirette. Ritiene gli umani imperfetti e pericolosi vettori di contagio.
2. **R-301 "Rancido" (Il Droide di Manutenzione)**: Un piccolo droide cilindrico cingolato guasto situato nel Laboratorio di Genetica. Se riparato con un cacciavite o ricaricato con una batteria, svelerà indizi preziosi sugli esperimenti e sulla vera natura del Dr. Moretti.

### La Svolta Narrativa (Il Twist)
Tu credi di essere il Dr. Moretti, ma scoprirai che l'originale Moretti è morto settimane fa nel tentativo di vendere il virus sul mercato nero. Prometeo ha clonato Moretti immettendo i suoi vecchi ricordi digitalizzati nel cervello del **Soggetto Chimera #12** (tu). Tu sei un clone portatore sano del virus. Prometeo ha sigillato la struttura non per ucciderti, ma per salvaguardare il pianeta.

---

## 3. Elenco Completo degli Oggetti di Gioco

Mentre molti oggetti sono raccoglibili, registreremo anche oggetti statici di scenario (Scenery) per garantire un'immersione perfetta:

| Nome Oggetto | ID | Tipo | Locazione Iniziale | Descrizione per l'esame (`guarda`) |
| :--- | :---: | :---: | :---: | :--- |
| **`tessera`** | 101 | Raccoglibile | Camera Criogenica | Un badge plastificato del Dr. Moretti con chip magnetico abilitato. |
| **`fiala`** | 102 | Raccoglibile | Lab Genetica | Una provetta rinforzata contenente il virus verde fosforescente Chimera. |
| **`decodificatore`**| 103 | Raccoglibile | Sala Server | Un bypass di sicurezza hardware militare per disattivare barriere laser. |
| **`diario`** | 104 | Raccoglibile | Ufficio Direttore | I registri finali del direttore che svelano l'esperimento di clonazione del Soggetto #12. |
| **`siero`** | 105 | Raccoglibile | Lab Genetica (Sintesi) | L'antidoto sintetizzato combinando la fiala con il tuo sangue di clone. |
| **`capsula`** | 106 | Scenico (Statico)| Camera Criogenica | Una capsula criogenica in frantumi. All'interno vedi la scritta incisa "SOGGETTO #12". |
| **`silos`** | 113 | Scenico (Statico)| Lab Genetica | Un enorme cilindro di vetro contenente liquido amniotico scuro ormai inattivo. |
| **`macchinario`** | 114 | Scenico (Statico)| Lab Genetica | Centrifuga molecolare usata per sintetizzare vaccini o sieri chimici. |
| **`server`** | 115 | Scenico (Statico)| Sala Server | File interminabili di elaboratori che ronzano emettendo calore e luci blu. |
| **`cassaforte`** | 111 | Scenico (Statico)| Ufficio Direttore | Cassaforte d'acciaio blindata con un tastierino numerico retroilluminato. |
| **`vetrata`** | 116 | Scenico (Statico)| Nucleo Comando | Spessa lastra trasparente che mostra il magma arancione del reattore geotermico. |

---

## 4. Mappa e Flusso delle Cose da Fare (Walkthrough)

Ecco la timeline degli obiettivi che guiderà lo sviluppo della logica di gioco turno per turno:

```text
  [Camera Crio] ➔ Esamina capsula ➔ Prendi tessera ➔ Sblocca porta ➔ Vai a est
       │
       ▼
  [Corridoio] ➔ Tenta di andare a sud ➔ Barriera laser attiva ➔ Vai a est
       │
       ▼
  [Sala Server] ➔ Parla con Prometeo (DB) ➔ Prendi decodificatore ➔ Torna nel corridoio ➔ Disattiva laser ➔ Vai a sud
       │
       ▼
  [Camera Decontaminazione] ➔ ATTIVAZIONE TIMER (2 min) ➔ Porte bloccate!
       │
       ├─► Soluzione Veloce: Usa siero (se sintetizzato nel Lab Genetica combinando fiala + sangue) ➔ Purifica condotto
       │
       └─► Soluzione Alternativa: Hackeraggio Console via Socket da terminale esterno
       │
       ▼
  [Nucleo di Comando] ➔ Scelta del Finale ➔ Salvataggio del Punteggio nel Database H2
```

---

## 5. Roadmap delle Funzionalità da Aggiungere (Turno per Turno)

Nelle prossime iterazioni implementeremo queste estensioni modulari:

1. **PROSSIMO TURNO (Miglioramento Immediato)**:
   * Registrazione degli **Oggetti Scenici** (`silos`, `macchinario`, `server`, etc.) in `LaMiaAvventura.java` per sradicare il bug del playtest.
   * Implementazione del comando **`map` / `mappa`** che stampa un layout ASCII dinamico della planimetria, mostrando la posizione corrente del giocatore `[X]`.
   * Registrazione dei sinonimi in lingua inglese per tutti i comandi base (es: `east`, `map`, `look`) per rendere il parser robusto alle abitudini di gioco.
2. **TURNO SUCCESSIVO (NPC & Database)**:
   * Creazione della tabella `dialoghi` in H2 Database per gestire i nodi dei dialoghi olografici con Prometeo `[Lezioni/13 - JDBC.pdf]`.
   * Integrazione del comando `parla` / `interroga` per comunicare con l'IA.
3. **TURNO SUCCESSIVO (Socket avanzati & Finali)**:
   * Abilitazione di un comando speciale remoto via Socket per disattivare la decontaminazione inviando un codice di override da un computer esterno (simulando un hacking di rete cooperativo!).
   * Implementazione della fine della partita con scrittura automatica del punteggio finale nella Hall of Fame di H2 e stampa della classifica grafica.

---

### ✍️ Sei d'accordo con questa roadmap e con il design degli oggetti scenici e della mappa?
Se mi dai il via libera, procederò subito a implementare il **PROSSIMO TURNO** aggiornando `LaMiaAvventura.java` con i nuovi oggetti scenici, la mappa ASCII dinamica, i sinonimi estesi ed aggiornerò il prompt di handoff `PROMPT_AI_NUOVA_CHAT.md`! Just say yes! Let's do it!