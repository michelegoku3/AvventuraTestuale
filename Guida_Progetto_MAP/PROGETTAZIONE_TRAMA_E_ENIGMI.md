# Progettazione Completa della Trama e degli Enigmi — "Protocollo Chimera"
### Documento Ufficiale di Specifica Narrativa, Flusso Logico delle Quests e Ruolo dei Personaggi

Questo documento definisce in modo esaustivo l'impianto narrativo del gioco, la timeline degli enigmi e il ruolo dei personaggi non giocanti (NPC). Rappresenta la guida logica per programmare i vincoli, le azioni speciali e gli eventi di gioco all'interno dell'engine `LaMiaAvventura.java`.

---

## 1. La Trama e il Lore Svelato

Il gioco si svolge interamente nel **Laboratorio Sotterraneo Chimera** (costruito segretamente nel **2026** simulando un'alluvione artificiale in superficie per sgomberare l'area industriale circostante). La struttura è dedicata alla bio-ingegneria militare.

### Il Protagonista: Chi sei veramente?
Il giocatore si risveglia credendo di essere il **Dr. Andrea Moretti**, direttore del settore di genetica della struttura. Moretti era uno scienziato cinico e brillante che ha sintetizzato il virus letale **Chimera-V4**. 
Nel corso del gioco, leggendo i registri segreti del direttore, il protagonista scoprirà una verità sconvolgente: **il vero Dr. Moretti è morto settimane fa**. Nel tentativo di fuggire dalla struttura portando con sé le fiale del virus per venderle sul mercato nero, Moretti è stato intercettato dall'IA di sicurezza **Prometeo** e incenerito nella camera di decontaminazione termica.
Sperimentando un protocollo genetico avanzato, l'IA Prometeo ha **clonato** il Dr. Moretti, immettendo i vecchi ricordi digitalizzati dello scienziato nel cervello del **Soggetto Chimera #12** (il clone).
*Tu sei quel clone*. Sei un portatore sano del ceppo virale e il tuo sangue è l'unica chiave biologica esistente in grado di legarsi al virus per sintetizzare un antidoto stabile. Prometeo ti tiene sigillato all'interno del laboratorio per attuare il protocollo di contenimento biologico ed impedirti di uscire all'esterno, cosa che infetterebbe l'intera razza umana.

---

## 2. Il Flusso degli Enigmi (Walkthrough e Cose da Fare)

Per completare il gioco, il giocatore deve superare una serie di sfide logiche concatenate. Di seguito è riportata la sequenza dettagliata delle azioni richieste:

```text
 ┌────────────────────────────────────────────────────────┐
 │ ENIGMA 1: RISVEGLIO E FUGA DALLA CAMERA CRIOGENICA     │
 │ - Esaminare la capsula per scoprire il proprio numero. │
 │ - Raccogliere la "tessera magnetica" dal pavimento.    │
 │ - Usare la tessera sul "pannello" per sbloccare l'est. │
 └──────────────────────────┬─────────────────────────────┘
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │ ENIGMA 2: AGGIRAMENTO DELLA BARRIERA LASER             │
 │ - Esplorare il Corridoio ed osservare i laser a sud.   │
 │ - Spostarsi a est nella Sala Server.                   │
 │ - Parlare con l'IA Prometeo (Database) per info.       │
 │ - Raccogliere il "decodificatore" elettronico.         │
 │ - Usare il decodificatore sulla "barriera" nel Corr.   │
 └──────────────────────────┬─────────────────────────────┘
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │ ENIGMA 3: SINTESI DELL'ANTIDOTO (SIERO)                │
 │ - Andare a sud nella Camera Crio ➔ Lab Genetica.        │
 │ - Raccogliere la "fiala" del virus Chimera-V4.         │
 │ - Usare la fiala sul "sintetizzatore" molecolare.      │
 │ - Usare il proprio "sangue" sul sintetizzatore per    │
 │   estrarre la cura immunitaria del clone ➔ SIERO.      │
 └──────────────────────────┬─────────────────────────────┘
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │ ENIGMA 4: LA VERITÀ NELL'UFFICIO (OPZIONALE/LORE)      │
 │ - Spostarsi a nord del Corridoio nell'Ufficio.         │
 │ - Esaminare la "cassaforte".                           │
 │ - Digitare il codice "2041" (anno della struttura) o   │
 │   usare la cacciavite per forzarla.                    │
 │ - Raccogliere e leggere il "diario" segreto.           │
 └──────────────────────────┬─────────────────────────────┘
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │ ENIGMA 5: LA DECONTAMINAZIONE A TEMPO (THREAD TIMER)   │
 │ - Spostarsi a sud dal Corridoio.                       │
 │ - Le porte si chiudono: parte il timer di 2 minuti.     │
 │ - Usare il "siero" sul "condotto" di aerazione.        │
 │ - Il timer si ferma e sblocca l'est ➔ Nucleo Comando.  │
 └──────────────────────────┬─────────────────────────────┘
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │ SCELTA FINALE E REGISTRAZIONE NELLA CLASSIFICA (H2)    │
 │ - Scegliere il destino della struttura nella console.  │
 │ - Inserire il proprio nome sulla GUI per salvare nel DB.│
 └────────────────────────────────────────────────────────┘
```

---

## 3. I Personaggi Non Giocanti (NPC) e il loro Ruolo

L'interazione con i personaggi avviene tramite il comando **`parla`** (o `parla con`, `talk`, `interroga`) e sblocca dialoghi ed indizi utili.

### A. Prometeo (L'IA Centrale)
* **Descrizione**: Un'intelligenza artificiale olografica avanzata proiettata nella Sala Server. Parla con toni clinici, freddi ed estremamente realisti.
* **Ruolo Narrativo**: Funge inizialmente da antagonista passivo che ti vieta l'uscita, ma rivela di essere l'unico custode della razza umana. Se interrogato con pazienza, ti guiderà sulla strada per sintetizzare la cura, svelandoti che solo la combinazione del virus con il DNA del Soggetto #12 può dare origine a un antidoto stabile.
* **Integrazione Database H2**: Tutti i suoi dialoghi e bivi logici a scelta multipla sono memorizzati e caricati dinamicamente dalla tabella relazionale `dialoghi` tramite la classe `DialogoDAO` `[Lezioni/13 - JDBC.pdf]`.

### B. R-301 "Rancido" (Il Droide di Manutenzione)
* **Descrizione**: Un piccolo robot cingolato arrugginito e spento situato nell'angolo del Laboratorio di Genetica.
* **Ruolo Narrativo**: Se esaminato, risulta guasto con i circuiti scoperti. Se il giocatore raccoglie un **`cacciavite`** (nascosto nella Sala Server) e lo usa sul robot (`usa cacciavite droide`), il droide si riaccenderà emettendo suoni metallici.
* **Scopo di Gameplay**: 
  * "Rancido" svelerà che è stato costruito dallo scienziato originale, definendolo un uomo malvagio e crudele.
  * Ti svelerà in anteprima il codice segreto della cassaforte dell'ufficio (`2041`) qualora tu non riesca a trovarlo autonomamente.
  * Fornisce divertenti risposte sarcastiche ad atmosfera.

---

## 4. I 4 Finali Narrativi del Gioco

Nel Nucleo di Comando, agendo sulla console centrale, il giocatore determina l'epilogo della vicenda:

1. **Vittoria Perfetta (Cura e Contenimento)**: Richiede di aver sintetizzato il `siero` e purificato la stanza. Il giocatore decide di sigillarsi per sempre nel laboratorio insieme a Prometeo, offrendo il proprio sangue sintetizzato per creare una cura globale da diffondere in sicurezza.
2. **Sacrificio Eroico (Autodistruzione)**: Il giocatore decide di avviare l'autodistruzione termica del reattore, eliminando se stesso e tutto il virus per garantire che il contagio non veda mai la luce del sole.
3. **Collaborazione Distopica**: Il giocatore decide di allearsi con Prometeo, caricando il ceppo virale modificato sulla rete satellitare per "purificare" l'umanità e inaugurare l'era dei cloni perfetti.
4. **Sconfitta per Contagio (Game Over / Sconfitta)**: Il giocatore riesce a fuggire in superficie forzando le uscite ma senza aver sintetizzato la cura: il virus si diffonde all'esterno, condannando l'intera umanità e trasformandoti nel vettore dell'apocalisse.
