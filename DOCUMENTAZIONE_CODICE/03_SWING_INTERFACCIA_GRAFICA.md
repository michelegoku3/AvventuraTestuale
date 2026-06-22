# 03 — Interfaccia grafica Swing

## 1. Classe principale della GUI

La GUI è implementata nella classe:

```text
ui/InterfacciaGioco.java
```

La classe estende `JFrame`:

```java
public class InterfacciaGioco extends javax.swing.JFrame
```

`JFrame` rappresenta una finestra principale Swing.

---

# 2. Componenti principali

La finestra contiene:

```java
private JTextArea txtConsole;
private JTextField txtInput;
private JButton btnInvia;
private JList<String> listInventario;
private DefaultListModel<String> modelInventario;
private JLabel lblTimer;
```

## `JTextArea txtConsole`

È il terminale testuale del gioco. Mostra descrizioni, comandi digitati, dialoghi e messaggi.

È configurato con:

```java
txtConsole.setEditable(false);
txtConsole.setLineWrap(true);
txtConsole.setWrapStyleWord(true);
```

- `setEditable(false)` impedisce all'utente di modificare il testo della console;
- `setLineWrap(true)` manda automaticamente a capo;
- `setWrapStyleWord(true)` evita di spezzare le parole.

## `JTextField txtInput`

È la barra dove il giocatore scrive comandi.

## `JButton btnInvia`

Permette di inviare il comando anche senza premere Invio.

## `JList listInventario`

Mostra gli oggetti attualmente posseduti dal giocatore.

## `JLabel lblTimer`

Mostra lo stato del timer della decontaminazione.

---

# 3. Layout

La GUI usa `BorderLayout`.

Il pannello centrale contiene:

```text
console testuale
input + bottone invia
```

La sidebar destra contiene:

```text
timer
inventario
```

Schema:

```text
+-----------------------------------+------------------+
|                                   | Timer            |
|           Console gioco           |                  |
|                                   | Inventario       |
+-----------------------------------+                  |
| Input comando       | Invia       |                  |
+-----------------------------------+------------------+
```

Questo evita che la barra input occupi anche lo spazio sotto l'inventario.

---

# 4. Gestione eventi

## Invio da tastiera

Il campo input ha un `KeyListener`:

```java
txtInput.addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            elaboraInputUtente();
        }
    }
});
```

Quando il giocatore preme Invio, viene chiamato:

```java
elaboraInputUtente()
```

## Bottone invia

Il bottone usa un `ActionListener`:

```java
btnInvia.addActionListener(e -> elaboraInputUtente());
```

`e -> ...` è una lambda expression.

---

# 5. Metodo `elaboraInputUtente`

Questo metodo è il cuore della GUI.

Passaggi principali:

1. legge il testo da `txtInput`;
2. lo pulisce con `trim()`;
3. lo stampa nella console con prefisso `>`;
4. controlla se c'è un dialogo attivo;
5. controlla se c'è una scelta finale attiva;
6. gestisce comandi incompleti;
7. chiama il parser;
8. invia il comando al motore di gioco;
9. aggiorna inventario e timer.

Esempio:

```java
String input = txtInput.getText().trim();
```

Poi:

```java
ParserOutput output = parser.parse(...);
String risposta = gioco.elaboraComando(output);
stampaTesto(risposta);
```

---

# 6. Memoria contestuale dei comandi

Se il giocatore scrive:

```text
prendi
```

il gioco risponde:

```text
Cosa vuoi prendere?
```

Questo è gestito da:

```java
private TipoComando comandoInAttesaDiTarget = null;
```

Se il comando è incompleto, la GUI memorizza il tipo del comando e aspetta l'oggetto.

Esempio:

```text
Utente: prendi
Gioco: Cosa vuoi prendere?
Utente: tessera
```

La GUI ricostruisce:

```text
prendi tessera
```

---

# 7. Menu

La GUI ha una `JMenuBar` con menu:

```text
Partita
Classifica
Socket
```

## Menu Partita

Contiene:

```text
Nuova
Salva
Carica
```

- `Nuova` crea una nuova partita;
- `Salva` esegue il comando `salva`;
- `Carica` esegue il comando `carica`.

## Menu Classifica

Apre una dialog per cercare punteggi.

## Menu Socket

Permette di:

- vedere la porta socket locale;
- aprire una finestra spettatore collegata a un'altra istanza.

---

# 8. Aggiornamento inventario grafico

Il metodo:

```java
aggiornaInventarioGrafico()
```

svuota il modello della lista e inserisce i nomi degli oggetti posseduti.

Usa stream:

```java
gioco.getInventario().getElementi().stream()
    .map(Oggetto::getNome)
    .forEach(modelInventario::addElement);
```

Significato:

- prendi gli oggetti dell'inventario;
- trasformali nei loro nomi;
- aggiungi ogni nome alla lista Swing.

---

# 9. Swing e thread

Swing non è thread-safe. Gli aggiornamenti grafici devono avvenire sull'Event Dispatch Thread.

Per questo il timer usa:

```java
SwingUtilities.invokeLater(...)
```

Questo evita aggiornamenti pericolosi della GUI da thread secondari.

---

# 10. Collegamento con gli argomenti del corso

Questa parte implementa:

- Swing;
- gestione eventi;
- listener;
- lambda expression;
- `JFrame`;
- `JDialog`;
- `JMenuBar`;
- `JList` con modello;
- thread-safe GUI update tramite `SwingUtilities.invokeLater`.
