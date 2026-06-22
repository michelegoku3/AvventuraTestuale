# 06 — Lambda, Stream, Collection e Generics

# 1. Collection e Generics

Il progetto usa molte collection Java parametrizzate con generics.

Esempi:

```java
List<Oggetto>
Set<String>
Map<TipoComando, Command>
Set<Integer>
```

## Cosa sono i generics

I generics permettono di indicare il tipo degli elementi contenuti in una struttura.

Esempio:

```java
List<Oggetto>
```

significa che la lista contiene oggetti di tipo `Oggetto`.

Questo evita cast manuali e rende il codice più sicuro.

---

# 2. `List`

Usata per:

- oggetti in una stanza;
- inventario;
- comandi disponibili;
- risultati della classifica.

Esempio:

```java
private List<Oggetto> oggetti;
```

Una `List` conserva ordine e permette duplicati.

---

# 3. `Set`

Usato per:

- sinonimi dei comandi;
- sinonimi degli oggetti;
- nodi dialogo visitati.

Esempio:

```java
private Set<String> sinonimi;
```

Un `Set` non contiene duplicati.

---

# 4. `Map`

Usata per associare un tipo comando alla relativa classe eseguibile:

```java
Map<TipoComando, Command> commandMap
```

Esempio:

```java
commandMap.put(TipoComando.PRENDI, new PrendiCommand());
```

Poi:

```java
Command cmd = commandMap.get(tipo);
```

Una `Map` è una struttura chiave-valore.

---

# 5. Lambda expression

Una lambda è una funzione anonima compatta.

Esempio:

```java
e -> elaboraInputUtente()
```

Significa:

```text
quando accade l'evento e, esegui elaboraInputUtente()
```

Nel progetto le lambda vengono usate per:

- listener Swing;
- filtri sugli oggetti;
- trasformazioni stream;
- ricerca punteggi;
- aggiornamento inventario.

---

# 6. Stream API

Gli stream permettono di elaborare collection in modo dichiarativo.

Esempio:

```java
stanza.getOggetti().stream()
    .filter(o -> o.isVisibile() && o.isPrendibile())
    .collect(Collectors.toList())
```

Significato:

1. prendi gli oggetti della stanza;
2. crea uno stream;
3. tieni solo quelli visibili e prendibili;
4. raccogli il risultato in una lista.

---

# 7. Uso nel parser

Nel parser:

```java
comandi.stream()
    .filter(c -> c.getSinonimi().contains(primoToken))
    .findFirst()
    .orElse(null)
```

Significato:

- scorri i comandi disponibili;
- filtra quello che contiene il token scritto;
- prendi il primo risultato;
- se non esiste, restituisci `null`.

---

# 8. Uso nell'inventario grafico

Nella GUI:

```java
gioco.getInventario().getElementi().stream()
    .map(Oggetto::getNome)
    .forEach(modelInventario::addElement);
```

Significato:

- prendi gli oggetti dell'inventario;
- trasformali nel loro nome;
- aggiungi ogni nome alla `JList`.

`Oggetto::getNome` è un method reference, cioè una forma compatta di:

```java
o -> o.getNome()
```

---

# 9. Uso nella ricerca punteggi

In `DialogRicerca`:

```java
tutti.stream()
    .filter(p -> p.getNomeGiocatore().toLowerCase().contains(queryStr.toLowerCase()))
    .map(p -> p.getNomeGiocatore() + " - " + p.getPunti() + " punti (...)")
    .forEach(modelRisultati::addElement);
```

Pipeline:

1. filtra i punteggi per nome;
2. trasforma ogni punteggio in stringa;
3. aggiunge la stringa alla lista grafica.

---

# 10. Classe `LambdaTasks`

La classe `LambdaTasks` contiene esempi più diretti degli argomenti dell'esercizio lambda:

- `Predicate`
- `Function`
- `Consumer`
- `Comparator`
- `map`
- `filter`
- `limit`
- `collect`
- `mapToInt`
- `sum`
- `average`
- `groupingBy`

Serve come dimostrazione esplicita degli argomenti del corso applicati al dominio degli oggetti di gioco.

---

# 11. Collegamento con gli argomenti del corso

Questa parte copre:

- collection;
- generics;
- lambda expression;
- functional interfaces;
- stream;
- pipeline;
- method reference;
- `Collectors`;
- ordinamenti e riduzioni.
