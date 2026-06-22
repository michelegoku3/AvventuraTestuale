# Classe `Parser` spiegata per intero

File:

```text
src/main/java/com/mycompany/avventuratestuale/core/Parser.java
```

La classe `Parser` ha il compito di trasformare il testo scritto dal giocatore in un oggetto strutturato, cioè un `ParserOutput`. In pratica prende una frase come:

```text
usa siero condotto
```

e cerca di capire:

```text
comando = USA
oggetto principale = siero
oggetto secondario = condotto
```

---

# 1. Package e import

```java
package com.mycompany.avventuratestuale.core;
```

Indica che la classe appartiene al package `core`, cioè alla parte centrale del motore di gioco.

```java
import com.mycompany.avventuratestuale.model.Oggetto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Predicate;
```

## Significato degli import

- `Oggetto`: serve perché il parser deve riconoscere oggetti di gioco.
- `List`: lista ordinata di elementi.
- `Set`: insieme senza duplicati.
- `Collectors`: serve per raccogliere risultati degli stream.
- `Predicate`: interfaccia funzionale che rappresenta una condizione booleana.

---

# 2. Dichiarazione della classe

```java
public class Parser {
```

`public` significa che la classe è accessibile anche da altri package.

La classe non estende altre classi e non implementa interfacce. È una classe di servizio usata dalla GUI.

---

# 3. Stopword

```java
private final Set<String> stopwords = Set.of(
    "il", "lo", "la", "i", "gli", "le", "un", "uno", "una",
    "di", "a", "da", "in", "con", "su", "per", "tra", "fra"
);
```

## Cosa sono le stopword

Sono parole grammaticali che il parser ignora.

Esempio:

```text
prendi la tessera
```

La parola `la` non è importante per capire l'azione. Il parser la salta.

## Perché `Set<String>`

`Set<String>` è un insieme di stringhe senza duplicati.

È adatto perché:

- non importa l'ordine;
- basta sapere se una parola è presente o no;
- `contains` è l'operazione principale.

## Cosa significa `final`

Il riferimento `stopwords` non può essere riassegnato dopo l'inizializzazione.

---

# 4. Metodo `filtraOggetti`

```java
public List<Oggetto> filtraOggetti(List<Oggetto> lista, Predicate<Oggetto> criterio) {
    return lista.stream()
            .filter(criterio)
            .collect(Collectors.toList());
}
```

## Cosa fa

Prende una lista di oggetti e una condizione. Restituisce una nuova lista contenente solo gli oggetti che soddisfano quella condizione.

## Spiegazione sintassi

### `Predicate<Oggetto> criterio`

`Predicate` è una interfaccia funzionale. Rappresenta una funzione che prende un `Oggetto` e restituisce `true` o `false`.

Esempio di predicato:

```java
o -> o.isVisibile()
```

significa:

```text
un oggetto passa il filtro se è visibile
```

### `lista.stream()`

Crea uno stream dalla lista.

### `.filter(criterio)`

Tiene solo gli oggetti per cui il predicato restituisce `true`.

### `.collect(Collectors.toList())`

Raccoglie il risultato in una nuova lista.

## Esempio

```java
filtraOggetti(oggettiStanza, o -> o.isVisibile() && o.isPrendibile())
```

restituisce solo gli oggetti visibili e prendibili.

---

# 5. Metodo `parse`

Firma:

```java
public ParserOutput parse(String input,
                          List<Comando> comandi,
                          List<Oggetto> oggettiStanza,
                          List<Oggetto> inventario)
```

## Parametri

### `String input`

Testo scritto dal giocatore.

Esempio:

```text
usa siero condotto
```

### `List<Comando> comandi`

Lista dei comandi conosciuti dal gioco, con sinonimi.

### `List<Oggetto> oggettiStanza`

Oggetti presenti nella stanza corrente.

### `List<Oggetto> inventario`

Oggetti posseduti dal giocatore.

## Ritorno

Restituisce un `ParserOutput`, cioè il risultato del parsing.

---

## 5.1 Pulizia dell'input

```java
String pulito = input.toLowerCase().trim();
String[] tokens = pulito.split("\\s+");
```

### `toLowerCase()`

Trasforma tutto in minuscolo.

```text
Prendi Tessera -> prendi tessera
```

### `trim()`

Rimuove spazi all'inizio e alla fine.

### `split("\\s+")`

Divide la stringa in parole usando uno o più spazi come separatore.

Esempio:

```text
usa siero condotto
```

diventa:

```text
["usa", "siero", "condotto"]
```

---

## 5.2 Controllo input vuoto

```java
if (tokens.length == 0 || tokens[0].isEmpty()) {
    return null;
}
```

Se non ci sono parole, il parser restituisce `null`.

---

## 5.3 Primo token

```java
String primoToken = tokens[0];
```

Il parser considera la prima parola come possibile comando.

Esempio:

```text
prendi tessera
```

primo token:

```text
prendi
```

---

## 5.4 Ricerca del comando

```java
Comando comandoRilevato = comandi.stream()
        .filter(c -> c.getSinonimi().contains(primoToken))
        .findFirst()
        .orElse(null);
```

## Cosa fa

Cerca tra tutti i comandi quello che contiene il primo token tra i suoi sinonimi.

## Spiegazione della pipeline

### `comandi.stream()`

Crea uno stream dalla lista dei comandi.

### `.filter(c -> c.getSinonimi().contains(primoToken))`

Tiene solo i comandi il cui insieme di sinonimi contiene la parola scritta.

Questa è una lambda.

```java
c -> c.getSinonimi().contains(primoToken)
```

significa:

```text
per ogni comando c, controlla se i suoi sinonimi contengono primoToken
```

### `.findFirst()`

Prende il primo comando trovato.

### `.orElse(null)`

Se non trova niente, restituisce `null`.

---

## 5.5 Se il comando non viene trovato

```java
if (comandoRilevato == null) {
    Oggetto objSolo = cercaOggetto(primoToken, oggettiStanza, inventario);
    if (objSolo != null) {
        return new ParserOutput(null, objSolo, null, input);
    }
    return new ParserOutput(null, null, null, input);
}
```

## Caso 1: l'utente scrive solo un oggetto

Esempio:

```text
tessera
```

Il parser non trova un comando, ma trova un oggetto. Restituisce:

```text
comando = null
oggetto = tessera
```

La GUI userà questa informazione per dire:

```text
Capisco che vuoi interagire con tessera, ma non so cosa fare.
```

## Caso 2: input sconosciuto

Esempio:

```text
guda
```

Il parser non trova né comando né oggetto. Restituisce un `ParserOutput` vuoto ma con `rawInput`, così la GUI può usare il fuzzy matching.

---

## 5.6 Ricerca degli oggetti dopo il verbo

```java
Oggetto oggettoRilevato = null;
Oggetto oggettoSecRilevato = null;
```

Il parser può trovare al massimo:

- un oggetto principale;
- un oggetto secondario.

Esempio:

```text
usa siero condotto
```

```text
oggettoRilevato = siero
oggettoSecRilevato = condotto
```

---

## 5.7 Ciclo sui token successivi

```java
for (int i = 1; i < tokens.length; i++) {
```

Parte da `i = 1` perché `tokens[0]` è il comando.

---

## 5.8 Salto delle stopword

```java
if (stopwords.contains(token)) {
    continue;
}
```

Se la parola è una stopword, il ciclo passa al token successivo.

`continue` significa:

```text
salta il resto del ciclo e passa alla prossima iterazione
```

---

## 5.9 Variabile `final String t = token`

```java
final String t = token;
```

Serve perché `t` viene usata dentro una lambda.

In Java, le variabili usate dentro una lambda devono essere finali o effectively final, cioè non modificate dopo l'assegnazione.

---

## 5.10 Ricerca negli oggetti della stanza

```java
List<Oggetto> trovati = filtraOggetti(oggettiStanza,
        o -> o.isVisibile() && (o.getNome().equalsIgnoreCase(t) || o.getSinonimi().contains(t)));
```

Cerca oggetti nella stanza che:

1. siano visibili;
2. abbiano nome uguale al token;
3. oppure abbiano il token tra i sinonimi.

### `equalsIgnoreCase`

Confronta stringhe ignorando maiuscole/minuscole.

### `||`

È OR logico.

La condizione è vera se almeno una delle due parti è vera.

---

## 5.11 Ricerca nell'inventario

```java
if (trovati.isEmpty()) {
    trovati = filtraOggetti(inventario, o -> o.getNome().equalsIgnoreCase(t) || o.getSinonimi().contains(t));
}
```

Se non trova l'oggetto nella stanza, cerca nell'inventario.

Nell'inventario non controlla `isVisibile()` perché un oggetto posseduto è già noto al giocatore.

---

## 5.12 Assegnazione oggetto primario e secondario

```java
if (!trovati.isEmpty()) {
    if (oggettoRilevato == null) {
        oggettoRilevato = trovati.get(0);
    } else if (oggettoSecRilevato == null) {
        oggettoSecRilevato = trovati.get(0);
    }
}
```

Se trova un oggetto:

- se il primo slot è vuoto, lo mette come oggetto principale;
- altrimenti lo mette come oggetto secondario.

---

## 5.13 Ritorno finale

```java
return new ParserOutput(comandoRilevato, oggettoRilevato, oggettoSecRilevato, input);
```

Crea un oggetto risultato con:

- comando;
- oggetto principale;
- oggetto secondario;
- input originale.

---

# 6. Metodo `cercaOggetto`

```java
private Oggetto cercaOggetto(String token, List<Oggetto> oggettiStanza, List<Oggetto> inventario)
```

È un metodo privato usato quando l'utente scrive una parola che non è un comando.

Cerca prima nella stanza:

```java
oggettiStanza.stream()
    .filter(...)
    .findFirst()
    .orElse(null)
```

Poi, se non trova niente, cerca nell'inventario.

---

# 7. Metodo `suggerisciComando`

```java
public String suggerisciComando(String parola, List<Comando> comandi)
```

Serve per il fuzzy matching.

## Funzionamento

Tiene traccia di:

```java
String miglioreSuggerimento = null;
int minDistanza = Integer.MAX_VALUE;
```

Poi confronta la parola sbagliata con tutti i sinonimi dei comandi.

```java
for (Comando cmd : comandi) {
    for (String sinonimo : cmd.getSinonimi()) {
        int dist = calcolaLevenshtein(parola, sinonimo);
        ...
    }
}
```

Se trova una distanza minore, aggiorna il suggerimento.

Alla fine restituisce il suggerimento solo se la distanza è massimo 2:

```java
if (minDistanza <= 2) {
    return miglioreSuggerimento;
}
return null;
```

Questo evita suggerimenti troppo fantasiosi.

---

# 8. Metodo `calcolaLevenshtein`

```java
private int calcolaLevenshtein(String s1, String s2)
```

Calcola la distanza di Levenshtein tra due stringhe.

## Cosa misura

Misura quante operazioni servono per trasformare una parola in un'altra:

- inserimento;
- cancellazione;
- sostituzione.

Esempio:

```text
guada -> guarda
```

richiede poche modifiche, quindi viene suggerito.

## Matrice `dp`

```java
int[][] dp = new int[s1.length() + 1][s2.length() + 1];
```

Crea una matrice di programmazione dinamica.

`dp[i][j]` rappresenta la distanza tra:

```text
primi i caratteri di s1
primi j caratteri di s2
```

## Inizializzazione

```java
for (int i = 0; i <= s1.length(); i++) {
    dp[i][0] = i;
}
```

Trasformare una stringa lunga `i` in stringa vuota richiede `i` cancellazioni.

```java
for (int j = 0; j <= s2.length(); j++) {
    dp[0][j] = j;
}
```

Trasformare stringa vuota in una lunga `j` richiede `j` inserimenti.

## Ciclo principale

Se i caratteri sono uguali:

```java
dp[i][j] = dp[i - 1][j - 1];
```

Se sono diversi:

```java
dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
```

Prende il minimo tra:

- cancellazione;
- inserimento;
- sostituzione;

e aggiunge 1.

---

# 9. Argomenti MAP coperti da `Parser`

La classe `Parser` copre:

- stringhe;
- array;
- collection;
- generics;
- stream;
- lambda;
- Predicate;
- algoritmi;
- programmazione dinamica;
- separazione delle responsabilità;
- progettazione modulare.
