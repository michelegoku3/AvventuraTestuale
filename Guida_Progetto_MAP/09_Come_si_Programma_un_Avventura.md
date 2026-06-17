# Capitolo 9: Come si Programma un'Avventura Testuale?
### Obiettivo: Spiegare in modo semplice e chiaro la logica di programmazione dietro un'avventura testuale, il concetto di "Ciclo di Gioco" (Game Loop), la "Macchina a Stati" e come configurare il progetto in NetBeans con Java 26 e Maven.

Se non hai mai sviluppato un'avventura testuale, l'idea di programmarla può sembrare complessa. In realtà, la logica di base è estremamente lineare ed elegante. Questo capitolo ti guiderà passo-passo nella comprensione di come "pensa" il tuo programma Java.

---

## 1. La Configurazione di NetBeans con Maven e Java 26
Sì, **la tua configurazione è assolutamente corretta ed è la migliore possibile!**
* **Maven**: Gestisce automaticamente le librerie esterne. Non dovrai scaricare manualmente i file `.jar` per connetterti al database H2; ci penserà Maven scaricandoli dai server centrali.
* **Java 26**: Essendo la versione più moderna e aggiornata, è fantastica! È completamente retrocompatibile con tutto il codice Java standard e ti permette di utilizzare tutte le funzionalità avanzate del corso (Stream, Lambda, e persino le recenti ottimizzazioni del compilatore).

### Il file `pom.xml` per Java 26 e H2
Nel tuo progetto Maven appena creato in NetBeans, apri il file `pom.xml` e assicurati che le proprietà del compilatore siano impostate per Java 26 e che la dipendenza per H2 sia presente. Sostituisci la sezione `<properties>` e aggiungi `<dependencies>` in questo modo:

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>26</maven.compiler.source>
    <maven.compiler.target>26</maven.compiler.target>
</properties>

<dependencies>
    <!-- Driver JDBC ufficiale di H2 Database per la persistenza relazionale [Lezioni/13 - JDBC.pdf, Slide 5] -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.200</version>
    </dependency>
</dependencies>
```

---

## 2. Il Cuore del Gioco: La Macchina a Stati
Un'avventura testuale non è altro che una **Macchina a Stati**. Lo "Stato" del gioco in un determinato istante è definito da tre elementi:
1. **La Stanza Corrente (`stanzaCorrente`)**: Dove si trova fisicamente il giocatore in questo momento (es: Camera Criogenica o Sala Server).
2. **L'Inventario (`inventario`)**: Quali oggetti il giocatore ha in tasca in questo momento (es: la tessera magnetica o il siero).
3. **Le Variabili di Stato / Flag booleani**: Interruttori logici in memoria che tengono traccia di cosa è successo nel mondo (es: `isBarrieraLaserAttiva = true`, `isSieroSintetizzato = false`).

Ogni comando dell'utente (es: "prendi tessera") ha il solo scopo di **leggere** lo stato corrente, verificare se l'azione è lecita e, in caso positivo, **modificare lo stato** fornendo una risposta testuale.

---

## 3. Il Ciclo di Gioco (Game Loop)
Un gioco testuale funziona come un ciclo continuo formato da 4 fasi ripetute all'infinito fino alla vittoria o alla sconfitta:

```text
 ┌────────────────────────────────────────────────────────┐
 │ 1. PRESENTAZIONE DELLO STATO                           │
 │    - Stampa descrizione stanza corrente e oggetti.     │
 └──────────────────────────┬─────────────────────────────┘
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │ 2. LETTURA DELL'INPUT                                  │
 │    - Attende che il giocatore scriva un comando.       │
 └──────────────────────────┬─────────────────────────────┘
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │ 3. PARSING DELL'INPUT                                  │
 │    - Pulisce il testo e identifica Verbo ed Oggetti.    │
 └──────────────────────────┬─────────────────────────────┘
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │ 4. AGGIORNAMENTO DELLO STATO (LOGICA DEL GIOCO)         │
 │    - Esegue l'azione e modifica le variabili di stato. │
 └──────────────────────────┬─────────────────────────────┘
                            ▲
                            └─────────────────────────────┘ (Ripeti)
```

---

## 4. Esempio Pratico di Flusso Logico: Il comando "PRENDI TESSERA"
Vediamo come Java traduce l'azione "prendi la tessera magnetica" in codice:

1. **Input dell'utente**: Digita `"Prendi la tessera magnetica"`.
2. **Il Parser interviene**:
   * Riconosce che il primo token è `"prendi"`, che corrisponde a `TipoComando.PRENDI` `[Lab 1 - Introduzione.pdf, p. 34]`.
   * Riconosce che la parola `"tessera"` corrisponde all'oggetto con ID `101` (`tessera`) presente nella stanza corrente `[Lab 1 - Introduzione.pdf, p. 27]`.
   * Restituisce un oggetto `ParserOutput(Comando(PRENDI), Oggetto(tessera), null)`.
3. **L'Engine del Gioco (`LaMiaAvventura.java`) elabora**:
   * Riceve il `ParserOutput`.
   * Controlla se l'oggetto è effettivamente presente nella stanza:
     `if (getStanzaCorrente().getOggetti().contains(oggetto))`
   * Controlla se l'oggetto è prendibile:
     `if (oggetto.isPrendibile())`
   * Se i controlli sono superati, **modifica lo stato**:
     * Rimuove l'oggetto dalla stanza: `getStanzaCorrente().rimuoviOggetto(oggetto);`
     * Aggiunge l'oggetto all'inventario: `getInventario().add(oggetto);`
     * Restituisce il testo di risposta: `"Hai raccolto la tessera magnetica."`
4. **La GUI Swing aggiorna la vista**:
   * Appende il testo `"Hai raccolto la tessera magnetica."` nella `JTextArea` della console `[Lezioni/16 - Swing.pdf, Slide 22]`.
   * Ricarica gli elementi della `JList` laterale per mostrare visivamente la tessera nell'inventario grafico `[Lezioni/16 - Swing.pdf, Slide 48]`.

---

## 5. Come gestire gli Spostamenti ("VAI NORD", "VAI EST")
Lo spostamento tra stanze è l'azione più semplice. Consiste nel sostituire il riferimento di `stanzaCorrente` con la stanza adiacente se essa esiste:

```java
public String gestisciSpostamento(TipoComando direzione) {
    Stanza stanzaDestinazione = null;
    
    // Controlla la direzione richiesta
    switch (direzione) {
        case NORD -> stanzaDestinazione = getStanzaCorrente().getNord();
        case SUD  -> stanzaDestinazione = getStanzaCorrente().getSud();
        case EST  -> stanzaDestinazione = getStanzaCorrente().getEst();
        case OVEST -> stanzaDestinazione = getStanzaCorrente().getOvest();
    }
    
    if (stanzaDestinazione == null) {
        return "Non puoi andare in quella direzione. C'è un muro d'acciaio blindato.";
    }
    
    // SE LA STANZA DI DESTINAZIONE È BLOCCATA DA UN ENIGMA
    if (stanzaDestinazione.getId() == 5 && isBarrieraLaserAttiva) {
        return "La barriera laser del corridoio è attiva! Ti incenerirebbe se provassi a passare. Trova un modo per disattivarla.";
    }
    
    // Aggiorna lo stato: sposta fisicamente il giocatore
    setStanzaCorrente(stanzaDestinazione);
    
    // Restituisce la descrizione della nuova stanza
    return "🗺️ " + getStanzaCorrente().getNome().toUpperCase() + "\n" +
           "===================================\n" +
           getStanzaCorrente().getDescrizione();
}
```

---

## 6. Perché questo approccio è semplice ed efficace
* **Niente coordinate matematiche**: Non devi gestire coordinate X e Y o calcoli di collisione 3D complessi. La mappa è un semplice **Grafo di riferimenti** (stanze collegate tra loro).
* **Massima estensibilità**: Se domani vuoi aggiungere una nuova stanza (es. la "Stanza dei Cloni Scartati"), devi solo creare un oggetto `Stanza` e collegarlo a una stanza esistente: `stanzaCorridoio.setOvest(nuovaStanza); nuovaStanza.setEst(stanzaCorridoio);`.
* **Database & File istantanei**: Poiché lo stato del gioco è racchiuso nell'oggetto `Gioco`, quando decidi di salvare, la JVM scriverà su file l'intera gerarchia delle stanze e degli oggetti con una sola riga di codice (Serializzazione), rispettando i requisiti accademici del corso!

*Ora che hai compreso la logica di programmazione, passa al [**Capitolo 8: Progettazione Completa della Trama**](./08_Progettazione_Trama_Scelta.md) per leggere la storia di "Protocollo Chimera" rifinita in ogni dettaglio!*
