# Capitolo 3: Persistenza e Gestione File
### Obiettivo: Implementare il salvataggio e il caricamento dello stato dell'avventura tramite la serializzazione di oggetti e la lettura strutturata dei file di testo.

Questo capitolo affronta il criterio 3 di valutazione (**Utilizzo dei file**) e integra il criterio 8 (**Utilizzo delle lambda expression, stream e pipeline**) `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`.

---

## 1. La persistenza in Java secondo le lezioni del corso
Nelle lezioni del corso viene spiegato che:
> *"Al termine della esecuzione di un programma, i dati utilizzati vengono distrutti. Per poterli preservare fra due esecuzioni consecutive è possibile ricorrere all'uso dell'I/O su file... Nel caso si desideri memorizzare strutture complesse (e.g., collezioni di oggetti):... si ricorre alla serializzazione"* `[Lezioni/10 - JAVA - Input Output.pdf, Slide 29-30]`.

Inoltre, nell'esercitazione pratica ufficiale del corso viene esplicitamente richiesto di implementare due metodi di persistenza tramite serializzazione di oggetti `[Esercizio Input Output.pdf, p. 1]`:
* `public static void save(Map<String, Integer> count, File file) throws IOException`
* `public static Map<String, Integer> load(File file) throws IOException, ClassNotFoundException`

Prendendo spunto da questa specifica, implementiamo un sistema di salvataggio dello stato globale dell'avventura testuale (rappresentata dall'oggetto `Gioco`).

---

## 2. Implementazione della persistenza tramite Serializzazione

### A. Requisito di conformità `Serializable`
Affinché il nostro oggetto `Gioco` (e tutto il grafo degli oggetti ad esso collegati, come `Stanza` e `Oggetto`) possa essere memorizzato, ciascuna di queste classi deve implementare l'interfaccia marcatore `java.io.Serializable` `[Lezioni/10 - JAVA - Input Output.pdf, Slide 31-33]`.

### B. Il Gestore di Salvataggio `SalvataggioManager.java`
* **Perché questa scelta?** Utilizziamo la classe `ObjectOutputStream` e il suo metodo `writeObject` per riversare lo stato dell'oggetto su un file, e `ObjectInputStream` con `readObject` per ripristinarlo in memoria `[Lezioni/10 - JAVA - Input Output.pdf, Slide 31]`.
* **Utilizzo del costrutto `try-with-resources`**: Tutte le operazioni di I/O utilizzano il blocco `try-with-resources` per garantire la chiusura automatica e sicura degli stream fisici del file, prevenendo la perdita di risorse di sistema (*resource leak*).

```java
package it.uniba.map.gioco.core;

import java.io.*;

public class SalvataggioManager {

    // Salva lo stato corrente del gioco su un file binario (Serializzazione)
    public static void salvaPartita(Gioco gioco, String nomeFile) throws IOException {
        // wrapping di un ObjectOutputStream su un FileOutputStream [Lezioni/10 - JAVA - Input Output.pdf, Slide 32]
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeFile))) {
            oos.writeObject(gioco);
        }
    }

    // Carica lo stato del gioco da un file binario (Deserializzazione)
    public static Gioco caricaPartita(String nomeFile) throws IOException, ClassNotFoundException {
        // wrapping di un ObjectInputStream su un FileInputStream [Lezioni/10 - JAVA - Input Output.pdf, Slide 33]
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeFile))) {
            return (Gioco) ois.readObject();
        }
    }
}
```

---

## 3. Elaborazione File di Configurazione (Stopwords) tramite Stream e Lambda (Criterio 8)
Nelle slide del corso viene spiegato l'uso del **Character Stream (Line-oriented I/O)** per leggere file riga per riga `[Lezioni/10 - JAVA - Input Output.pdf, Slide 7-10]`. 
Uniamo questo concetto alle moderne **Lambda Expressions e Stream API** `[Lezioni/16 - JAVA - Lambda Expressions.pdf / Lezioni/17 - JAVA - Lambda Expressions.pdf, Slide 3]` per caricare l'elenco delle *stopwords* da un file di testo in modo dichiarativo e compatto.

```java
package it.uniba.map.gioco.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurazioneLoader {

    // Carica le stopwords da file usando una pipeline di stream filtrati (Criterio 8)
    public static Set<String> caricaStopwords(String pathFile) throws IOException {
        // Files.lines restituisce uno Stream<String> [Lezioni/10 - JAVA - Input Output.pdf, Slide 9-10]
        try (Stream<String> streamLinee = Files.lines(Paths.get(pathFile))) {
            return streamLinee
                    .map(String::trim)                         // Operazione intermedia: pulizia spazi bianchi
                    .filter(linea -> !linea.isEmpty())         // Operazione intermedia: salta le righe vuote (Lambda Predicate)
                    .map(String::toLowerCase)                  // Operazione intermedia: conversione in minuscolo
                    .collect(Collectors.toSet());             // Operazione terminale: raccoglie in un Set
        }
    }
}
```

---

## 4. Analisi delle Proprietà Avanzate della Serializzazione (Per l'Orale)
Preparati a rispondere alle domande di approfondimento del Prof. Basile sulla serializzazione citando questi concetti:

* **Attributi Statici (`static`)**: 
  > *"Gli attributi di classe, cioè definiti como static, NON vengono serializzati. Per poterli salvare occorre provvedere in modo personalizzato"* `[Lezioni/10 - JAVA - Input Output.pdf, Slide 34]`.
  Nel nostro progetto, infatti, il campo `serialVersionUID` o eventuali contatori statici globali non faranno parte del file di salvataggio automatico.
* **Il modificatore `transient`**: 
  > *"A volte, quando si serializza un oggetto, si può desiderare di escludere delle informazioni... Per modificare la dichiarazione di una variabile può essere usata la parola chiave transient: questa indica al compilatore di non rappresentarla come parte dello stream di byte"* `[Lezioni/10 - JAVA - Input Output.pdf, Slide 37-38]`.
  Nel nostro progetto, se una classe contiene riferimenti a thread attivi (come il timer del Capitolo 5) o a connessioni socket (come nel Capitolo 6), questi riferimenti **devono essere marcati come `transient`**, poiché i thread e le connessioni socket di rete *non possono essere serializzati* e solleverebbero un'eccezione `NotSerializableException` se non esclusi.
* **Serializzazione Ricorsiva**: 
  > *"La serializzazione di un oggetto si occupa di serializzare tutti gli eventuali riferimenti ad esso collegati"* `[Lezioni/10 - JAVA - Input Output.pdf, Slide 33]`. 
  Quando salviamo l'oggetto `Gioco`, la JVM salverà automaticamente la stanza corrente, tutti i collegamenti alle altre stanze (l'intera mappa) e tutti gli oggetti in esse contenuti, poiché sono tutti legati da riferimenti e implementano `Serializable`.

*Passa al [**Capitolo 4: Database e Connettività JDBC**](./04_Database_JDBC.md) per scoprire come salvare i dati in un Database H2 con il pattern DAO!*
