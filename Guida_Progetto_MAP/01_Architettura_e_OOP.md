# Capitolo 1: Architettura Software e OOP
### Obiettivo: Progettare un motore di gioco modulare ed estensibile basato sui sani principi della programmazione orientata agli oggetti (OOP) e sul recupero dei requisiti didattici.

Questo capitolo affronta i primi due criteri di valutazione d'esame: la **Qualità dell'Avventura** e la **Qualità della Programmazione ad Oggetti** `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`, integrando le funzionalità avanzate del framework delle collezioni `[Lezioni/7 - JAVA - Le Collection.pdf]` e delle espressioni lambda `[Lezioni/16 - JAVA - Lambda Expressions.pdf / Lezioni/17 - JAVA - Lambda Expressions.pdf]`.

---

## 1. Il Modello di un'Avventura Testuale secondo la Didattica
Nelle slide di laboratorio del Prof. Basile, un'avventura testuale viene definita come:
> *"dei programmi che simulano un ambiente nel quale i giocatori usano comandi testuali per istruire il personaggio della storia a interagire con l'ambiente circostante"* `[Lab 1 - Introduzione.pdf, p. 3]`.

Per creare un'avventura di ottima qualità, le slide indicano che la trama deve essere accuratamente definita attraverso quattro elementi fondamentali:
1. **La Mappa**: Una struttura logica che rappresenta i luoghi (stanze) in cui si svolge l'avventura `[Lab 1 - Introduzione.pdf, p. 34, 37]`.
2. **Il Dizionario**: L'elenco di parole e sinonimi conosciuti dal programma `[Lab 1 - Introduzione.pdf, p. 34]`.
3. **Gli Oggetti e i Personaggi (NPC)**: Elementi interattivi distribuiti nella mappa dotati di specifiche proprietà `[Lab 1 - Introduzione.pdf, p. 34, 38]`.
4. **Le Azioni**: I comandi (sotto forma di frasi o verbi come "prendi chiave", "vai nord") che hanno effetto sul gioco e ne modificano le proprietà `[Lab 1 - Introduzione.pdf, p. 6, 34, 40]`.

---

## 2. Architettura dei Package in NetBeans
Nel corso viene insegnata la strutturazione del codice in package al fine di applicare il principio dell'**Information Hiding** (incapsulamento) e della coesione modulare:
> *"I package raggruppano classi correlate... controllando l'accesso tramite i modificatori"* `[Lezioni/2 - Paradigma OO.pdf, Slide 5]`, `[Corso.pdf, p. 2]`.

In NetBeans, organizzeremo l'applicazione in package logici:
* `it.uniba.map.gioco.core`: Contiene la logica di controllo (il Game Loop, il Parser sintattico, le classi di comando).
* `it.uniba.map.gioco.model`: Contiene le classi di dati (*Entity*) che modellano il mondo virtuale (`Stanza`, `Oggetto`, `Personaggio`).
* `it.uniba.map.gioco.ui`: Contiene l'interfaccia utente Swing.
* `it.uniba.map.gioco.impl`: Contiene l'avventura specifica concreta da te creata.

---

## 3. Implementazione del Modello dei Dati (Package `model`)

### A. La classe `Oggetto.java`
Rappresenta un elemento interattivo presente nell'avventura `[Lab 1 - Introduzione.pdf, p. 27]`.
* **Perché questa struttura?** Usiamo l'incapsulamento (`private` con getter/setter pubblici) per proteggere lo stato interno dell'oggetto da modifiche impreviste `[Lezioni/2 - Paradigma OO.pdf, Slide 37]`.
* **Utilizzo delle Collezioni**: Per i sinonimi dell'oggetto (es: "chiave", "chiavetta", "chiave d'ottone"), usiamo un `Set<String>` (precisamente un `HashSet`) poiché i sinonimi sono elementi unici non ordinati e un `Set` ottimizza la ricerca `[Lezioni/7 - JAVA - Le Collection.pdf, Slide 25-28]`.

```java
package it.uniba.map.gioco.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Oggetto implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private String nome;
    private String descrizione;
    private Set<String> sinonimi;
    private boolean prendibile = true;
    private boolean visibile = true;
    private boolean aperto = false;
    private boolean contenitore = false;

    public Oggetto(int id, String nome, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.sinonimi = new HashSet<>();
    }

    public Oggetto(int id, String nome, String descrizione, Set<String> sinonimi) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.sinonimi = sinonimi;
    }

    // Getter e Setter (Incapsulamento)
    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Set<String> getSinonimi() { return sinonimi; }
    public void setSinonimi(Set<String> sinonimi) { this.sinonimi = sinonimi; }
    public boolean isPrendibile() { return prendibile; }
    public void setPrendibile(boolean prendibile) { this.prendibile = prendibile; }
    public boolean isVisibile() { return visibile; }
    public void setVisibile(boolean visibile) { this.visibile = visibile; }
    public boolean isAperto() { return aperto; }
    public void setAperto(boolean aperto) { this.aperto = aperto; }
    public boolean isContenitore() { return contenitore; }
    public void setContenitore(boolean contenitore) { this.contenitore = contenitore; }
}
```

### B. La classe `Stanza.java`
Rappresenta una locazione della mappa `[Lab 1 - Introduzione.pdf, p. 24, 37]`.
* **Perché questa struttura?** Applichiamo la **Composizione** (relazione *has-a*) inserendo una lista di oggetti (`List<Oggetto>`) all'interno della classe `Stanza` `[Lezioni/2 - Paradigma OO.pdf, Slide 45]`. La composizione è da preferire all'ereditarietà quando non c'è una relazione di tipo "is-a".
* **Direzioni**: Colleghiamo le stanze tra di loro tramite riferimenti diretti alle quattro direzioni cardinali (Nord, Sud, Est, Ovest), conformemente allo schema della griglia di gioco spiegata in laboratorio `[Lab 1 - Introduzione.pdf, p. 23, 37]`.

```java
package it.uniba.map.gioco.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stanza implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private String nome;
    private String descrizione;
    
    // Riferimenti alle stanze adiacenti
    private Stanza nord = null;
    private Stanza sud = null;
    private Stanza est = null;
    private Stanza ovest = null;
    
    // Composizione: una stanza contiene oggetti
    private List<Oggetto> oggetti = new ArrayList<>();

    public Stanza(int id, String nome, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    
    public Stanza getNord() { return nord; }
    public void setNord(Stanza nord) { this.nord = nord; }
    public Stanza getSud() { return sud; }
    public void setSud(Stanza sud) { this.sud = sud; }
    public Stanza getEst() { return est; }
    public void setEst(Stanza est) { this.est = est; }
    public Stanza getOvest() { return ovest; }
    public void setOvest(Stanza ovest) { this.ovest = ovest; }
    
    public List<Oggetto> getOggetti() { return oggetti; }
    public void aggiungiOggetto(Oggetto obj) { this.oggetti.add(obj); }
    public void rimuoviOggetto(Oggetto obj) { this.oggetti.remove(obj); }
}
```

---

## 4. Gestione dei Comandi ed Engine con Enumerativi ed Espressoini Lambda

### A. Tipo di Comando ed Enumerazione
Per evitare errori di battitura e facilitare il controllo logico, usiamo un `Enum` (`TipoComando.java`), che è un costrutto nativo Java ideale per insiemi di costanti predefinite `[Lezioni/4 - JAVA - Elementi del Linguaggio.pdf, Slide 42]`.

```java
package it.uniba.map.gioco.core;

public enum TipoComando {
    NORD, SUD, EST, OVEST, 
    PRENDI, LASCIA, APRI, USA, GUARDA, INVENTARIO, 
    AIUTO, CLASSIFICA, SALVA, CARICA, ESCI
}
```

La classe `Comando.java` associa ogni istanza di `TipoComando` all'elenco dei suoi sinonimi testuali:
```java
package it.uniba.map.gioco.core;

import java.io.Serializable;
import java.util.Set;

public class Comando implements Serializable {
    private static final long serialVersionUID = 1L;
    private final TipoComando tipo;
    private final Set<String> sinonimi;

    public Comando(TipoComando tipo, Set<String> sinonimi) {
        this.tipo = tipo;
        this.sinonimi = sinonimi;
    }

    public TipoComando getTipo() { return tipo; }
    public Set<String> getSinonimi() { return sinonimi; }
}
```

---

## 5. Implementazione delle Funzionalità Lambda & Stream (Criterio 8)
In conformità con quanto insegnato e richiesto dal docente nell'**`Esercizio Lambda Expressions`** `[Esercizi/Esercizio Lambda Expressions.pdf, p. 1-3]`, il progetto d'esame deve far uso di:
* **`java.util.function.Predicate`** per il filtraggio degli elementi.
* **`java.util.function.Consumer`** per compiere azioni sistematiche sugli elementi.
* **`java.util.function.Comparator`** per ordinare le collezioni sulla base di molteplici campi logici.
* **Pipeline Stream complete** con operatori intermedi e terminali complessi.

Andiamo a integrare queste tre interfacce e pipeline all'interno del nostro motore di gioco.

### A. Il Parser dei Comandi con Stream e Lambda (Criterio 8 - Predicate)
Il parser implementa la logica di pulizia della stringa (eliminando le *stopwords* o parole vuote) e mappa l'input sui comandi e sugli oggetti del gioco.
* **Perché questa scelta?** Implementiamo la ricerca dell'oggetto nella stanza e nell'inventario usando le **Lambda Expressions** e la **Stream API** `[Lezioni/16 - JAVA - Lambda Expressions.pdf / Lezioni/17 - JAVA - Lambda Expressions.pdf, Slide 3]`. 
* **Uso del Predicate**: Il metodo `filtraOggetti(List<Oggetto> lista, java.util.function.Predicate<Oggetto> criterio)` rispecchia esattamente il Task 1 dell'esercitazione d'esame `[Esercizi/Esercizio Lambda Expressions.pdf, p. 1]`.

```java
package it.uniba.map.gioco.core;

import it.uniba.map.gioco.model.Oggetto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Predicate;

public class Parser {

    private final Set<String> stopwords = Set.of(
        "il", "lo", "la", "i", "gli", "le", "un", "uno", "una", 
        "di", "a", "da", "in", "con", "su", "per", "tra", "fra"
    );

    // Metodo generico che accetta un'interfaccia funzionale Predicate, conforme a [Esercizi/Esercizio Lambda Expressions.pdf, p. 1]
    public List<Oggetto> filtraOggetti(List<Oggetto> lista, Predicate<Oggetto> criterio) {
        return lista.stream()
                .filter(criterio)
                .collect(Collectors.toList());
    }

    public ParserOutput parse(String input, List<Comando> comandi, List<Oggetto> oggettiStanza, List<Oggetto> inventario) {
        String pulito = input.toLowerCase().trim();
        String[] tokens = pulito.split("\\s+");
        
        if (tokens.length == 0 || tokens[0].isEmpty()) {
            return null;
        }

        // Cerca il comando (verbo) usando la pipeline Stream
        Comando comandoRilevato = comandi.stream()
                .filter(c -> c.getSinonimi().contains(tokens[0]))
                .findFirst()
                .orElse(null);

        if (comandoRilevato == null) {
            return new ParserOutput(null, null, null);
        }

        Oggetto oggettoRilevato = null;
        Oggetto oggettoSecRilevato = null;

        // Cerca oggetti nell'input filtrando le stopwords
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            if (stopwords.contains(token)) {
                continue;
            }

            final String t = token;
            // Utilizzo del metodo di filtraggio tramite Predicate Lambda
            List<Oggetto> trovati = filtraOggetti(oggettiStanza, o -> o.getNome().equalsIgnoreCase(t) || o.getSinonimi().contains(t));
            if (trovati.isEmpty()) {
                trovati = filtraOggetti(inventario, o -> o.getNome().equalsIgnoreCase(t) || o.getSinonimi().contains(t));
            }

            if (!trovati.isEmpty()) {
                if (oggettoRilevato == null) {
                    oggettoRilevato = trovati.get(0);
                } else if (oggettoSecRilevato == null) {
                    oggettoSecRilevato = trovati.get(0);
                }
            }
        }

        return new ParserOutput(comandoRilevato, oggettoRilevato, oggettoSecRilevato);
    }
}
```

### B. Uso di `Consumer` per Azioni sugli Oggetti (Criterio 8 - Consumer)
In conformità con il Task 3 dell'esercitazione `[Esercizi/Esercizio Lambda Expressions.pdf, p. 2]`, possiamo definire un metodo che applichi un side-effect (`Consumer`) sugli oggetti della stanza (ad esempio, per nasconderli o rivelarli sistematicamente in seguito ad un'esplosione o magia):

```java
// Metodo conforme al Task 3 di [Esercizi/Esercizio Lambda Expressions.pdf, p. 2]
public void eseguiAzioneSuOggetti(List<Oggetto> lista, java.util.function.Consumer<Oggetto> azione) {
    lista.forEach(azione);
}

// Esempio d'uso pratico nell'elaborazione di un comando:
// eseguiAzioneSuOggetti(stanza.getOggetti(), obj -> obj.setVisibile(true));
```

### C. Ordinamento Multi-campo tramite `Comparator` (Criterio 8 - Comparator)
In conformità con il Task 4 dell'esercitazione `[Esercizi/Esercizio Lambda Expressions.pdf, p. 2]`, che richiede di ordinare una lista di elementi per un livello di forza decrescente e, a parità di forza, per costo d'ingaggio crescente, implementiamo un ordinamento multi-campo per i punteggi di gioco (es: per punteggio decrescente e, a parità di punteggio, per nome alfabetico crescente):

```java
// Metodo conforme al Task 4 di [Esercizi/Esercizio Lambda Expressions.pdf, p. 2]
public void ordinaClassifica(List<it.uniba.map.gioco.database.Punteggio> lista) {
    lista.sort((p1, p2) -> {
        // Confronto primario decrescente sui punti
        int compPunti = Integer.compare(p2.getPunti(), p1.getPunti());
        if (compPunti != 0) {
            return compPunti;
        }
        // Confronto secondario alfabetico crescente sul nome (tie-breaker)
        return p1.getNomeGiocatore().compareToIgnoreCase(p2.getNomeGiocatore());
    });
}
```

---

## 6. Il Motore di Gioco Astratto `Gioco.java`
Utilizziamo una **classe astratta** `Gioco.java` come superclasse per definire la struttura generale dello stato del gioco `[Lezioni/2 - Paradigma OO.pdf, Slide 50-53]`.
* **Perché la classe astratta?** Una classe astratta consente di definire parzialmente l'implementazione (condividendo attributi e getter/setter comuni) e di delegare i dettagli specifici dell'avventura alle sottoclassi concrete che la estenderanno tramite il polimorfismo `[Lezioni/2 - Paradigma OO.pdf, Slide 52]`.

```java
package it.uniba.map.gioco.core;

import it.uniba.map.gioco.model.Stanza;
import it.uniba.map.gioco.model.Oggetto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Gioco implements Serializable {
    private static final long serialVersionUID = 1L;

    private Stanza stanzaCorrente;
    private List<Oggetto> inventario = new ArrayList<>();
    private List<Comando> comandi = new ArrayList<>();

    // Metodo astratto che definisce la storia e la mappa
    public abstract void inizializza() throws Exception;
    
    // Gestisce lo smistamento logico dei comandi
    public abstract String elaboraComando(ParserOutput output);

    // Getters e Setters
    public Stanza getStanzaCorrente() { return stanzaCorrente; }
    public void setStanzaCorrente(Stanza stanzaCorrente) { this.stanzaCorrente = stanzaCorrente; }
    public List<Oggetto> getInventario() { return inventario; }
    public void setInventario(List<Oggetto> inventario) { this.inventario = inventario; }
    public List<Comando> getComandi() { return comandi; }
}
```

---

## 7. Perché questa architettura è conforme al corso MAP?
Durante l'esame orale potresti dover giustificare le tue scelte architetturali. Ecco i punti chiave da citare:
* **Separazione dei Package**: Conforme con la lezione sull'**Information Hiding** e l'uso del controllo degli accessi `[Lezioni/2 - Paradigma OO.pdf, Slide 37-38]`.
* **Uso di Collezioni Generiche**: Conforme alla lezione sulle Collezioni in Java `[Lezioni/7 - JAVA - Le Collection.pdf, Slide 2]`. Abbiamo preferito interfacce generiche come `List` e `Set` anziché tipi concreti come `ArrayList` o `HashSet` per favorire il disaccoppiamento del codice (Polimorfismo delle Collezioni).
* **Integrazione Totale dei Task Lambda**: Le metodologie di filtraggio tramite `Predicate`, manipolazione tramite `Consumer` e ordinamento dinamico composito tramite `Comparator` rispecchiano fedelmente i concetti di programmazione funzionale dichiarativa assegnati nell'**Esercizio Lambda Expressions** `[Esercizi/Esercizio Lambda Expressions.pdf, p. 1-3]`.

*Passa al [**Capitolo 2: Interfaccia Grafica con Swing**](./02_Swing_GUI.md) per scoprire come integrare la finestra di gioco Swing con barre dei menu e JDialogs di inserimento e ricerca!*
