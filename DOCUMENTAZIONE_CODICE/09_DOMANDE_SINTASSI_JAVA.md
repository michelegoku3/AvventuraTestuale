# 09 — Domande sulla sintassi Java usata nel progetto

Questo documento risponde ad alcune domande puntuali sulla sintassi Java presente nel progetto **Trapped Virus**. L'obiettivo è capire non solo “cosa fa” il codice, ma anche a quale argomento del corso MAP si collega.

---

# 1. `java.awt.EventQueue.invokeLater(() -> { ... })`

Esempio:

```java
java.awt.EventQueue.invokeLater(() -> {
    InterfacciaGioco frame = new InterfacciaGioco();
    frame.setVisible(true);
});
```

## Cosa fa

`EventQueue.invokeLater(...)` dice a Java:

> “Esegui questo codice sul thread degli eventi grafici di Swing.”

Swing usa un thread speciale chiamato **Event Dispatch Thread**, spesso abbreviato in **EDT**. Tutte le operazioni che creano, modificano o aggiornano componenti Swing dovrebbero avvenire su quel thread.

## Perché serve

Swing **non è thread-safe**. Questo significa che se più thread modificano contemporaneamente la GUI, possono verificarsi comportamenti imprevedibili.

Per questo si scrive:

```java
EventQueue.invokeLater(...)
```

oppure:

```java
SwingUtilities.invokeLater(...)
```

## Cosa significa `() -> { ... }`

Questa è una **lambda expression**.

Significa:

```text
una funzione anonima senza parametri che esegue il blocco tra parentesi graffe
```

Equivale a scrivere una classe anonima che implementa `Runnable`, ma in forma più compatta.

## Argomento del prof

Sì, è collegato a:

- Swing;
- gestione eventi;
- thread;
- lambda expression.

---

# 2. `public class Comando implements Serializable`

Esempio:

```java
public class Comando implements Serializable {
```

## Cosa significa

La classe `Comando` implementa l'interfaccia `Serializable`.

Questo significa che gli oggetti di tipo `Comando` possono essere **serializzati**, cioè trasformati in una sequenza di byte e salvati su file.

Nel progetto serve perché lo stato del gioco viene salvato tramite:

```java
ObjectOutputStream
```

Quando salvi una partita, Java salva l'oggetto `Gioco`, e con esso tutti gli oggetti collegati che implementano `Serializable`.

## Cosa viene serializzato davvero

Nel caso di `Comando`, vengono salvati i suoi campi:

```java
private final TipoComando tipo;
private final Set<String> sinonimi;
```

Quindi vengono salvati:

- il tipo del comando;
- i sinonimi associati.

## Argomento del prof

Sì, è collegato alla parte su:

- file;
- I/O;
- serializzazione degli oggetti.

---

# 3. `private static final long serialVersionUID = 1L;`

Esempio:

```java
private static final long serialVersionUID = 1L;
```

## Spiegazione dei vari elementi

### `private`

Il campo è visibile solo dentro la classe.

### `static`

Il campo appartiene alla classe, non ai singoli oggetti.

Quindi non esiste una copia per ogni oggetto, ma una sola copia associata alla classe.

### `final`

Il valore non può essere modificato dopo l'inizializzazione.

### `long`

È un tipo numerico intero a 64 bit.

### `serialVersionUID`

È un identificativo di versione usato dalla serializzazione Java.

Quando Java carica da file un oggetto serializzato, confronta il `serialVersionUID` salvato con quello della classe attuale. Se non corrispondono, può sollevare un errore di incompatibilità.

### `1L`

La `L` indica che il numero `1` è di tipo `long`.

## Perché si usa

Serve a rendere più stabile la serializzazione nel tempo.

## Argomento del prof

Sì, rientra nella parte su:

- classi;
- modificatori;
- tipi primitivi;
- serializzazione.

---

# 4. `private final Set<String> sinonimi;`

Esempio:

```java
private final Set<String> sinonimi;
```

## Cosa significa

È un campo privato e finale che contiene un insieme di stringhe.

### `Set<String>`

`Set` è una collection Java che rappresenta un insieme senza duplicati.

`String` indica che l'insieme contiene solo stringhe.

Esempio:

```java
Set<String> sinonimi = new HashSet<>();
```

può contenere:

```text
prendi
raccogli
take
get
```

ma non può contenere duplicati.

## Perché si usa `Set` e non `List`

Perché per i sinonimi non serve l'ordine e non vogliamo duplicati.

## Argomento del prof

Sì, rientra in:

- Collections;
- Generics;
- classi e oggetti.

---

# 5. `String execute(LaMiaAvventura game, ParserOutput output);`

Esempio:

```java
String execute(LaMiaAvventura game, ParserOutput output);
```

## Dove si trova

Si trova nell'interfaccia:

```java
Command
```

## Cosa fa

È il metodo che ogni comando deve implementare.

Esempio:

```java
public class PrendiCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        ...
    }
}
```

Quando il giocatore scrive un comando, il parser lo interpreta e poi il motore chiama `execute` sulla classe comando corretta.

## Parametri

### `LaMiaAvventura game`

È lo stato corrente del gioco.

Serve per sapere:

- stanza corrente;
- inventario;
- flag narrativi;
- oggetti disponibili.

### `ParserOutput output`

Contiene il risultato del parser:

- comando riconosciuto;
- oggetto primario;
- oggetto secondario;
- input originale.

## Ritorno

Il metodo restituisce una `String`, cioè il testo da mostrare nella console del gioco.

## Argomento del prof

Sì, rientra in:

- interfacce;
- polimorfismo;
- OOP;
- Command Pattern.

---

# 6. `public abstract void inizializza() throws Exception;`

Esempio:

```java
public abstract void inizializza() throws Exception;
```

## Cosa significa `abstract`

Il metodo è dichiarato ma non implementato nella classe astratta.

La sottoclasse concreta deve implementarlo.

Nel progetto:

```java
Gioco
```

dichiara il metodo astratto, mentre:

```java
LaMiaAvventura
```

lo implementa.

## Cosa significa `throws Exception`

Significa che il metodo può lanciare un'eccezione.

Chi chiama il metodo deve:

- gestire l'eccezione con `try/catch`;
- oppure dichiarare a sua volta `throws`.

Esempio:

```java
try {
    gioco.inizializza();
} catch (Exception e) {
    ...
}
```

## Perché si usa

Perché durante l'inizializzazione potrebbero verificarsi errori, ad esempio legati a risorse, database, file o costruzione dello stato.

## Argomento del prof

Sì, rientra in:

- classi astratte;
- eccezioni;
- gestione degli errori.

---

# 7. `throw new IllegalArgumentException(...)`

Esempio:

```java
throw new IllegalArgumentException("inserisci: x non puo' essere null");
```

## Cosa fa

Lancia manualmente un'eccezione.

In questo caso l'eccezione indica che è stato passato un argomento non valido al metodo.

Esempio:

```java
inventario.inserisci(null);
```

non ha senso, quindi il metodo blocca l'operazione e segnala l'errore.

## Cos'è `IllegalArgumentException`

È un'eccezione standard di Java usata quando un metodo riceve un parametro non accettabile.

## È checked o unchecked?

È una eccezione unchecked, perché deriva da `RuntimeException`.

Quindi non è obbligatorio dichiararla con `throws`.

## Argomento del prof

Sì, rientra nella parte su:

- eccezioni;
- controllo degli errori;
- robustezza del codice.

---

# 8. `this.elementi.stream().anyMatch(o -> o.getId() == y.getId())`

Esempio:

```java
return this.elementi.stream().anyMatch(o -> o.getId() == y.getId());
```

## È una lambda?

Sì.

La parte:

```java
o -> o.getId() == y.getId()
```

è una lambda expression.

## Cosa fa tutto il codice

`this.elementi` è la lista degli oggetti nell'inventario.

`stream()` crea un flusso di elementi.

`anyMatch(...)` controlla se almeno un elemento soddisfa una condizione.

La condizione è:

```java
o.getId() == y.getId()
```

Quindi il codice significa:

> “Restituisci true se nell'inventario esiste almeno un oggetto con lo stesso id dell'oggetto y.”

## Esempio

Se l'inventario contiene:

```text
tessera id 101
fiala id 102
```

allora:

```java
contiene(fiala)
```

restituisce `true`.

## Argomento del prof

Sì, rientra in:

- lambda;
- stream;
- collection.

---

# 9. `Collections.unmodifiableList(this.elementi)`

Esempio:

```java
return java.util.Collections.unmodifiableList(this.elementi);
```

## Cosa fa

Restituisce una vista non modificabile della lista.

Chi riceve questa lista può leggerla, ma non può modificarla.

Esempio:

```java
inventario.getElementi().add(oggetto);
```

lancerebbe un'eccezione.

## Perché serve

Serve a proteggere l'incapsulamento.

La classe `Inventario` non vuole che altre classi modifichino direttamente la lista interna. Le modifiche devono passare da metodi controllati come:

```java
inserisci
rimuovi
```

## È presente negli argomenti del prof?

Il concetto sì:

- incapsulamento;
- information hiding;
- collection.

Il metodo specifico `unmodifiableList` potrebbe non essere stato spiegato in dettaglio, ma è una normale API Java coerente con quegli argomenti.

---

# 10. `if (!(obj instanceof Inventario)) return false;`

Esempio:

```java
if (!(obj instanceof Inventario)) return false;
```

## Cosa fa `instanceof`

Controlla se un oggetto è istanza di una certa classe.

```java
obj instanceof Inventario
```

significa:

> “obj è un Inventario?”

Restituisce `true` o `false`.

## Come funzionano le parentesi

La parte:

```java
(obj instanceof Inventario)
```

viene valutata come booleano.

Il simbolo `!` nega il risultato.

Quindi:

```java
!(obj instanceof Inventario)
```

significa:

> “obj NON è un Inventario”

Se non è un inventario, `equals` restituisce subito `false`.

## Argomento del prof

Sì, rientra in:

- identificazione di tipo a runtime;
- ereditarietà;
- polimorfismo;
- overriding di `equals`.

---

# 11. `Inventario other = (Inventario) obj;`

Esempio:

```java
Inventario other = (Inventario) obj;
```

## Cosa fanno le parentesi

Questa è un'operazione di cast.

`obj` è di tipo generale `Object`.

Dopo aver verificato:

```java
obj instanceof Inventario
```

possiamo convertirlo a `Inventario`:

```java
(Inventario) obj
```

Quindi:

```java
Inventario other = (Inventario) obj;
```

significa:

> “Tratta obj come un Inventario e assegnalo alla variabile other.”

## Perché prima si usa `instanceof`

Per evitare un errore a runtime chiamato `ClassCastException`.

## È presente nelle spiegazioni del prof?

Sì, è collegato a:

- identificazione di tipo a runtime;
- cast;
- polimorfismo;
- metodo `equals`.

---

# 12. `Collectors.toSet()`

Esempio:

```java
Set<Integer> ids1 = this.elementi.stream()
    .map(Oggetto::getId)
    .collect(Collectors.toSet());
```

## Cosa fa

Parte da una lista di oggetti.

```java
this.elementi.stream()
```

Poi trasforma ogni oggetto nel suo id:

```java
.map(Oggetto::getId)
```

Poi raccoglie gli id in un `Set`:

```java
.collect(Collectors.toSet())
```

## Cos'è `Collectors.toSet()`

È un collector che raccoglie gli elementi di uno stream dentro un `Set`.

Quindi da:

```text
Oggetto tessera id 101
Oggetto fiala id 102
```

ottieni:

```text
Set<Integer> = {101, 102}
```

## Argomento del prof

Sì, rientra in:

- Stream API;
- Collectors;
- `collect`;
- collection;
- generics.

---

# 13. `Oggetto::getNome` e i due `::`

Esempio:

```java
Comparator.comparing(Oggetto::getNome, String.CASE_INSENSITIVE_ORDER)
```

## Cosa significa `::`

`::` è un **method reference**.

```java
Oggetto::getNome
```

è una forma compatta di:

```java
o -> o.getNome()
```

Significa:

> “Per ogni Oggetto, usa il suo metodo getNome.”

## Cosa fa `Comparator.comparing`

Crea un comparatore che ordina gli oggetti in base a una proprietà.

Qui ordina gli oggetti in base al nome.

## Cosa fa `String.CASE_INSENSITIVE_ORDER`

È un comparatore predefinito di Java per confrontare stringhe ignorando maiuscole/minuscole.

Esempio:

```text
Fiala
fiala
```

vengono considerate equivalenti nell'ordinamento.

## Argomento del prof

Sì per:

- lambda;
- method reference;
- Comparator;
- ordinamento;
- stream/pipeline.

Il dettaglio specifico `CASE_INSENSITIVE_ORDER` potrebbe non essere stato approfondito, ma è una costante standard della classe `String`.

---

# 14. Metodo generico `filtra`

Esempio:

```java
public static <T> List<T> filtra(List<T> lista, Predicate<T> criterio) {
    return lista.stream().filter(criterio).collect(Collectors.toList());
}
```

## Spiegazione sintassi

### `public`

Metodo accessibile da altre classi.

### `static`

Il metodo appartiene alla classe, non a un oggetto specifico.

Si può chiamare così:

```java
LambdaTasks.filtra(...)
```

### `<T>`

Indica un tipo generico.

Il metodo può lavorare con liste di qualsiasi tipo:

```java
List<Oggetto>
List<String>
List<Integer>
```

### `List<T>`

Lista di elementi di tipo `T`.

### `Predicate<T>`

Interfaccia funzionale che rappresenta una condizione booleana su un elemento di tipo `T`.

Esempio:

```java
o -> o.isPrendibile()
```

### `.filter(criterio)`

Tiene solo gli elementi che soddisfano il predicato.

### `.collect(Collectors.toList())`

Raccoglie il risultato in una nuova lista.

## In parole semplici

Il metodo significa:

> “Data una lista e una condizione, restituisci una nuova lista con solo gli elementi che rispettano la condizione.”

## Argomento del prof

Sì, copre:

- generics;
- functional interfaces;
- `Predicate`;
- lambda;
- stream;
- collect.

---

# 15. `sommaValoriTassati`

Esempio:

```java
public static double sommaValoriTassati(List<Oggetto> lista) {
    Function<Oggetto, Double> tassa = o -> o.isPrendibile() ? 1.0 : 1.5;
    return lista.stream().map(tassa).mapToDouble(Double::doubleValue).sum();
}
```

## Cosa è

È un metodo dimostrativo nella classe `LambdaTasks`.

Non è fondamentale per il gameplay. Serve a mostrare l'uso di:

- `Function`;
- lambda;
- stream;
- trasformazione;
- `mapToDouble`;
- `sum`.

## Spiegazione

```java
Function<Oggetto, Double> tassa
```

è una funzione che prende un `Oggetto` e restituisce un `Double`.

La lambda:

```java
o -> o.isPrendibile() ? 1.0 : 1.5
```

significa:

> “Se l'oggetto è prendibile restituisci 1.0, altrimenti 1.5.”

L'operatore:

```java
condizione ? valoreSeVero : valoreSeFalso
```

è l'operatore ternario.

Poi:

```java
lista.stream().map(tassa)
```

trasforma ogni oggetto in un valore numerico.

```java
.mapToDouble(Double::doubleValue)
```

converte da `Double` oggetto a `double` primitivo.

```java
.sum()
```

somma tutti i valori.

## Argomento del prof

Sì, è collegato agli esercizi su lambda e functional interfaces.

---

# 16. `gridoEco`

Esempio:

```java
public static void gridoEco(List<Oggetto> lista)
```

## Cosa fa

È un metodo dimostrativo in `LambdaTasks`.

Usa un `Consumer<Oggetto>` per stampare in console il nome degli oggetti in modo diverso.

Un `Consumer<T>` è una funzione che prende un elemento e non restituisce nulla.

Esempio concettuale:

```java
Consumer<Oggetto> eco = o -> {
    System.out.println(o.getNome());
};
```

Serve a dimostrare l'uso dell'interfaccia funzionale `Consumer`.

## Argomento del prof

Sì, è collegato a:

- lambda expression;
- `Consumer`;
- functional interfaces.

---

# 17. `Integer.compare(b.getId(), a.getId())`

Esempio:

```java
int compId = Integer.compare(b.getId(), a.getId());
```

## Cos'è `Integer`

`Integer` è la classe wrapper del tipo primitivo `int`.

Java ha:

```text
int      tipo primitivo
Integer  classe oggetto corrispondente
```

`Integer.compare(x, y)` confronta due interi.

Restituisce:

- valore negativo se `x < y`;
- zero se `x == y`;
- valore positivo se `x > y`.

## Perché qui l'ordine è `b, a`

```java
Integer.compare(b.getId(), a.getId())
```

ordina in modo decrescente.

Se fosse:

```java
Integer.compare(a.getId(), b.getId())
```

sarebbe crescente.

## L'ha spiegato il prof?

Il concetto di wrapper, tipi numerici e comparator rientra negli argomenti Java. Anche se `Integer.compare` specifico potrebbe non essere stato mostrato esattamente, è una normale API Java usata per implementare un `Comparator`.

---

# 18. `compareToIgnoreCase`

Esempio:

```java
return a.getNome().compareToIgnoreCase(b.getNome());
```

## Cosa fa

Confronta due stringhe ignorando maiuscole e minuscole.

Esempio:

```text
"fiala" e "Fiala"
```

vengono confrontate come equivalenti dal punto di vista delle lettere.

## A cosa serve

Serve per ordinare alfabeticamente i nomi degli oggetti senza distinguere maiuscole/minuscole.

## L'ha spiegato il prof?

Il corso tratta stringhe, numeri, classi e oggetti. Il metodo specifico può non essere stato spiegato parola per parola, ma è un metodo standard della classe `String`, coerente con gli argomenti del corso.

---

# 19. `primiTrePrendibili`

Esempio:

```java
public static List<String> primiTrePrendibili(List<Oggetto> lista)
```

## A cosa serve

È un metodo dimostrativo della classe `LambdaTasks`.

Serve a mostrare una pipeline stream con:

- `filter`;
- `map`;
- `limit`;
- `collect`.

Concettualmente fa:

1. prende una lista di oggetti;
2. tiene solo quelli prendibili;
3. li trasforma in stringhe;
4. prende i primi tre;
5. restituisce una lista di stringhe.

## Cosa fa `.stream()`

`.stream()` crea un flusso di dati dalla collection.

Uno stream permette di applicare operazioni dichiarative come:

```java
filter
map
sorted
limit
collect
```

## Argomento del prof

Sì, rientra pienamente in stream e pipeline.

---

# 20. A cosa serve `LambdaTasks.java`

`LambdaTasks.java` è una classe dimostrativa.

Non è centrale per la storia o il gameplay, ma serve a mostrare in modo esplicito gli argomenti dell'esercizio sulle lambda expression.

Contiene metodi che dimostrano:

- `Predicate`;
- `Function`;
- `Consumer`;
- `Comparator`;
- pipeline stream;
- riduzioni;
- raggruppamenti con `Collectors.groupingBy`;
- `minBy`;
- `max`;
- `average`.

## Perché ci sono tanti metodi diversi

Perché ogni metodo mostra una tecnica diversa.

Esempi:

```text
filtra                       -> Predicate
sommaValoriTassati           -> Function
gridoEco                     -> Consumer
ordinaPerIdPoiNome           -> Comparator
primiTrePrendibili           -> filter/map/limit
sommaIdRaccoglibili          -> riduzione con sum
oggettoConIdMassimo          -> max con Comparator
minimoPerStanza              -> groupingBy + minBy
```

---

# 21. `minBy`

Esempio concettuale:

```java
Collectors.minBy(Comparator.comparingInt(Oggetto::getId))
```

## Cosa fa

Trova l'elemento minimo di un gruppo secondo un comparatore.

Se raggruppi oggetti per categoria, `minBy` può trovare l'oggetto con id più basso in ogni gruppo.

## È stato spiegato dal prof?

Il concetto generale sì, perché rientra in:

- stream;
- collectors;
- comparator;
- grouping.

Il metodo specifico `minBy` può essere più dettagliato rispetto agli esempi base, ma è coerente con l'esercizio sulle lambda e stream.

Non è fuori programma: è un'applicazione diretta di `Collectors`.

---

# 22. Conclusione su `LambdaTasks`

`LambdaTasks` non serve a risolvere un enigma specifico, ma serve a dimostrare competenza sugli argomenti del corso.

Se il professore chiede perché esiste, la risposta è:

> “È una classe dimostrativa che applica gli esercizi su lambda, functional interfaces, stream e collectors al dominio del progetto, usando gli oggetti dell'avventura invece di esempi astratti.”

---

# 23. In sintesi: cosa è sicuramente nel programma del prof?

Sicuramente coerenti con il corso:

- classi e oggetti;
- incapsulamento;
- ereditarietà;
- classi astratte;
- interfacce;
- polimorfismo;
- eccezioni;
- serializzazione;
- collection;
- generics;
- JDBC;
- Swing;
- thread;
- socket;
- lambda expression;
- stream;
- pipeline;
- comparator;
- functional interfaces.

Alcuni metodi specifici Java, come:

```text
Collections.unmodifiableList
String.CASE_INSENSITIVE_ORDER
compareToIgnoreCase
Collectors.minBy
```

potrebbero non essere stati spiegati singolarmente, ma sono API standard coerenti con gli argomenti trattati.
