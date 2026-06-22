# 01 — Architettura generale e programmazione orientata agli oggetti

## 1. Obiettivo architetturale

Il progetto **Trapped Virus** è organizzato per separare le responsabilità principali:

- rappresentazione del mondo di gioco;
- interpretazione dei comandi;
- esecuzione delle azioni;
- interfaccia grafica;
- persistenza su file;
- persistenza su database;
- timer concorrente;
- socket spettatore.

Questa separazione rende il codice più leggibile, modificabile e coerente con i principi della programmazione orientata agli oggetti.

---

## 2. Struttura dei package

```text
com.mycompany.avventuratestuale
│
├── core
├── core.commands
├── model
├── impl
├── database
├── socket
└── ui
```

### `core`

Contiene le classi generali del motore:

- `Gioco`
- `Parser`
- `ParserOutput`
- `Comando`
- `Command`
- `TipoComando`
- `Inventario`
- `SalvataggioManager`
- `ThreadTimer`

### `core.commands`

Contiene le classi che eseguono i comandi:

- `PrendiCommand`
- `UsaCommand`
- `GuardaCommand`
- `MovimentoCommand`
- `SalvaCommand`
- `CaricaCommand`
- ecc.

### `model`

Contiene il modello dati:

- `Stanza`
- `Oggetto`

### `impl`

Contiene l'avventura concreta:

- `LaMiaAvventura`

### `database`

Contiene la parte JDBC/H2:

- `DatabaseManager`
- `PunteggioDAO`
- `DialogoDAO`
- `Punteggio`
- `DialogoNode`

### `socket`

Contiene la parte di rete:

- `ServerComandi`
- `ClientHandler`

### `ui`

Contiene l'interfaccia Swing:

- `InterfacciaGioco`
- `DialogRicerca`
- `DialogInserimento`

---

# 3. Classe astratta `Gioco`

La classe `Gioco` è una classe astratta.

```java
public abstract class Gioco implements Serializable {
    private Stanza stanzaCorrente;
    private Inventario inventario;
    private List<Comando> comandi = new ArrayList<>();

    public abstract void inizializza() throws Exception;
    public abstract String elaboraComando(ParserOutput output);
}
```

## Cosa significa `abstract`

Una classe astratta non viene istanziata direttamente. Serve come modello comune per classi concrete.

Nel progetto:

```java
Gioco gioco = new LaMiaAvventura();
```

La variabile è di tipo generale `Gioco`, ma l'oggetto reale è `LaMiaAvventura`.

Questo dimostra il polimorfismo: il codice usa un riferimento generale, ma a runtime viene eseguito il comportamento della sottoclasse concreta.

---

# 4. Classe concreta `LaMiaAvventura`

`LaMiaAvventura` estende `Gioco`:

```java
public class LaMiaAvventura extends Gioco {
```

Questa classe contiene:

- mappa delle stanze;
- oggetti;
- enigmi;
- dialoghi;
- flag narrativi;
- finali;
- gestione uso oggetti;
- gestione spostamenti;
- rendering della mappa ASCII.

Esempi di flag:

```java
private boolean isBarrieraLaserAttiva = true;
private boolean isPortaCrioAperta = false;
private boolean isSieroSintetizzato = false;
private boolean isCondottoPurificato = false;
```

## Cosa sono i flag

Un flag è una variabile booleana che indica se una condizione del gioco è vera o falsa.

Esempio:

```java
isBarrieraLaserAttiva = true;
```

significa che la barriera blocca ancora il passaggio.

Quando il giocatore usa il decodificatore:

```java
isBarrieraLaserAttiva = false;
```

e quindi il passaggio diventa libero.

---

# 5. Incapsulamento

L'incapsulamento consiste nel tenere nascosti i dati interni di una classe e accedervi tramite metodi.

Esempio in `Oggetto`:

```java
private final int id;
private String nome;
private String descrizione;
private Set<String> sinonimi;
private boolean prendibile = true;
private boolean visibile = true;
```

I campi sono `private`, quindi non sono modificabili direttamente dall'esterno.

Si accede tramite metodi:

```java
public int getId()
public String getNome()
public boolean isPrendibile()
public void setVisibile(boolean visibile)
```

Questo protegge lo stato interno e rende più controllabile il comportamento degli oggetti.

---

# 6. Composizione

La composizione è il rapporto "ha un".

Esempi nel progetto:

```text
Gioco ha un Inventario
Gioco ha una Stanza corrente
Stanza ha molti Oggetti
InterfacciaGioco ha un Parser
InterfacciaGioco ha un ThreadTimer
InterfacciaGioco ha un ServerComandi
```

Nel codice:

```java
private Inventario inventario;
private Stanza stanzaCorrente;
```

oppure:

```java
private List<Oggetto> oggetti;
```

La composizione è preferibile all'ereditarietà quando una classe deve contenere o usare oggetti di un'altra classe.

---

# 7. Interfacce e polimorfismo: `Command`

L'interfaccia `Command` definisce un comportamento comune:

```java
public interface Command {
    String execute(LaMiaAvventura game, ParserOutput output);
}
```

Ogni classe comando implementa questa interfaccia:

```java
public class PrendiCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        ...
    }
}
```

Il motore può trattare tutti i comandi allo stesso modo:

```java
private transient Map<TipoComando, Command> commandMap = new HashMap<>();
```

E poi:

```java
Command cmd = commandMap.get(tipo);
return cmd.execute(this, output);
```

Questo è polimorfismo: `cmd` è di tipo `Command`, ma l'oggetto reale può essere `PrendiCommand`, `UsaCommand`, `GuardaCommand`, ecc.

---

# 8. Perché esistono sia `Comando` sia `Command`

I nomi sono simili, ma i ruoli sono diversi.

## `Comando`

È la definizione sintattica usata dal parser:

```java
public class Comando implements Serializable {
    private final TipoComando tipo;
    private final Set<String> sinonimi;
}
```

Serve a riconoscere che parole come:

```text
prendi, raccogli, take
```

corrispondono a:

```text
TipoComando.PRENDI
```

## `Command`

È l'interfaccia del Command Pattern. Serve a eseguire l'azione.

```java
String execute(LaMiaAvventura game, ParserOutput output);
```

Quindi:

```text
Comando = riconoscimento sintattico
Command = esecuzione logica
```

---

# 9. Collegamento con gli argomenti del corso

Questa parte del codice implementa:

- astrazione;
- classi astratte;
- interfacce;
- incapsulamento;
- composizione;
- polimorfismo;
- package;
- riuso del codice;
- separazione delle responsabilità.
