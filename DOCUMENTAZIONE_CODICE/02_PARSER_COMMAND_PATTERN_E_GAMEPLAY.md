# 02 — Parser, Command Pattern e logica di gameplay

## 1. Flusso generale di un comando

Quando il giocatore scrive un comando, il programma segue questa catena:

```text
Input utente
    ↓
InterfacciaGioco.elaboraInputUtente()
    ↓
Parser.parse(...)
    ↓
ParserOutput
    ↓
LaMiaAvventura.elaboraComando(...)
    ↓
Command.execute(...)
    ↓
Risposta testuale nella console Swing
```

Esempio:

```text
prendi tessera
```

Il parser riconosce:

```text
TipoComando.PRENDI
Oggetto: tessera
```

Poi `LaMiaAvventura` trova nella mappa dei comandi:

```java
TipoComando.PRENDI -> PrendiCommand
```

Infine esegue:

```java
PrendiCommand.execute(...)
```

---

# 2. `TipoComando`

`TipoComando` è un enum:

```java
public enum TipoComando {
    NORD, SUD, EST, OVEST,
    PRENDI, LASCIA, APRI, USA,
    GUARDA, INVENTARIO, AIUTO,
    CLASSIFICA, SALVA, CARICA,
    ESCI, MAPPA, PARLA
}
```

## Cosa significa `enum`

Un `enum` definisce un insieme chiuso di valori costanti.

Invece di usare stringhe sparse nel codice, il progetto usa valori forti e controllati:

```java
TipoComando.PRENDI
TipoComando.USA
TipoComando.GUARDA
```

Questo evita errori di battitura e rende il codice più sicuro.

---

# 3. `Comando`

`Comando` associa un `TipoComando` a un insieme di sinonimi.

Esempio tratto dalla logica di inizializzazione:

```java
new Comando(
    TipoComando.PRENDI,
    new HashSet<>(Arrays.asList("prendi", "raccogli", "afferra", "get", "take", "grab"))
)
```

Questo significa che tutti questi input:

```text
prendi tessera
raccogli tessera
take tessera
get tessera
```

vengono interpretati come:

```text
TipoComando.PRENDI
```

---

# 4. `Parser`

Il parser prende:

```java
String input
List<Comando> comandi
List<Oggetto> oggettiStanza
List<Oggetto> inventario
```

Restituisce un `ParserOutput`.

## Pulizia dell'input

Il parser trasforma l'input in minuscolo:

```java
String pulito = input.toLowerCase().trim();
```

- `toLowerCase()` evita differenze tra maiuscole e minuscole;
- `trim()` rimuove spazi iniziali e finali.

Poi divide la frase in token:

```java
String[] tokens = pulito.split("\\s+");
```

La regex `\\s+` indica uno o più spazi.

Esempio:

```text
"prendi la tessera"
```

diventa:

```text
["prendi", "la", "tessera"]
```

---

## Stopword

Il parser ignora parole come:

```text
il, lo, la, i, gli, le, un, uno, una, di, a, da, in, con, su
```

Così:

```text
prendi la tessera
```

viene interpretato come:

```text
prendi tessera
```

Questo rende il parser più naturale.

---

## Ricerca del comando

Il parser cerca il primo token tra i sinonimi dei comandi:

```java
Comando comandoRilevato = comandi.stream()
    .filter(c -> c.getSinonimi().contains(primoToken))
    .findFirst()
    .orElse(null);
```

Questa è una pipeline Stream:

- `stream()` crea un flusso di comandi;
- `filter(...)` tiene solo quelli che contengono il token;
- `findFirst()` prende il primo;
- `orElse(null)` restituisce `null` se non trova nulla.

---

## Ricerca degli oggetti

Il parser cerca gli oggetti nella stanza e nell'inventario.

Controlla:

```java
nome oggetto
sinonimi oggetto
visibilità
```

Gli oggetti invisibili nella stanza non vengono riconosciuti, così il giocatore non può prendere oggetti non ancora scoperti.

---

# 5. `ParserOutput`

`ParserOutput` contiene il risultato del parser:

```java
private final Comando comando;
private final Oggetto oggetto;
private final Oggetto oggettoSecondario;
private final String rawInput;
```

Esempio:

```text
usa fiala macchinario
```

può produrre:

```text
comando = USA
oggetto = fiala
oggettoSecondario = macchinario
rawInput = "usa fiala macchinario"
```

---

# 6. Suggerimenti fuzzy con Levenshtein

Se il comando non viene riconosciuto, il parser può suggerire la parola più simile.

Esempio:

```text
guada
```

viene confrontato con i sinonimi dei comandi. Se la distanza è bassa, suggerisce:

```text
guarda
```

Questo viene fatto con l'algoritmo di Levenshtein.

## Cosa misura Levenshtein

Misura quante modifiche servono per trasformare una parola in un'altra:

- inserimento;
- cancellazione;
- sostituzione.

Esempio:

```text
guada -> guarda
```

richiede poche modifiche, quindi viene suggerito.

---

# 7. Command Pattern

Il progetto usa il Command Pattern per separare:

```text
riconoscimento del comando
```

da:

```text
esecuzione del comando
```

La mappa è:

```java
Map<TipoComando, Command> commandMap
```

Esempio:

```java
commandMap.put(TipoComando.PRENDI, new PrendiCommand());
commandMap.put(TipoComando.USA, new UsaCommand());
commandMap.put(TipoComando.GUARDA, new GuardaCommand());
```

Quando il parser riconosce `PRENDI`, il gioco esegue `PrendiCommand`.

---

# 8. Esempio: `PrendiCommand`

`PrendiCommand` controlla:

1. che l'oggetto esista;
2. che sia nella stanza corrente;
3. che sia visibile;
4. che sia prendibile.

Se tutto è valido:

```java
game.getStanzaCorrente().rimuoviOggetto(obj);
game.aggiungiAInventario(obj);
```

L'oggetto viene rimosso dalla stanza e inserito nell'inventario.

---

# 9. Esempio: `UsaCommand`

`UsaCommand` delega a:

```java
game.gestisciUsoOggetto(output)
```

Questo metodo in `LaMiaAvventura` gestisce gli enigmi principali:

- usare tessera sulla porta;
- usare cacciavite su Rancido;
- usare decodificatore sulla barriera;
- usare fiala nel macchinario;
- usare siero nel condotto;
- usare console nel Nucleo.

---

# 10. Gameplay e flag

Gli enigmi sono controllati da flag booleani.

Esempio:

```java
private boolean isPortaCrioAperta = false;
```

Quando il giocatore usa la tessera:

```java
isPortaCrioAperta = true;
```

Da quel momento può andare a est dalla Camera Criogenica.

Altri flag importanti:

```text
isBarrieraLaserAttiva
isSieroSintetizzato
isCondottoPurificato
isCassaforteAperta
isDroideRiparato
```

---

# 11. Collegamento con gli argomenti del corso

Questa parte del codice mostra:

- enum;
- collection;
- stream;
- lambda;
- classi e oggetti;
- interfacce;
- polimorfismo;
- Command Pattern;
- information hiding;
- gestione dello stato;
- progettazione modulare.
