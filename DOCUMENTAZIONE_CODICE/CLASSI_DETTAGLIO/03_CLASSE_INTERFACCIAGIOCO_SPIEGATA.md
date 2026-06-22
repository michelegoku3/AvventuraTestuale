# Classe `InterfacciaGioco` spiegata per intero

File:

```text
src/main/java/com/mycompany/avventuratestuale/ui/InterfacciaGioco.java
```

`InterfacciaGioco` è la classe che gestisce la finestra Swing principale del gioco.

Si occupa di:

- mostrare il terminale testuale;
- leggere input dell'utente;
- inviare input al parser;
- aggiornare inventario;
- gestire timer;
- gestire menu;
- gestire socket spettatore;
- gestire salvataggio/caricamento dal punto di vista grafico.

---

# 1. Package e import

```java
package com.mycompany.avventuratestuale.ui;
```

La classe appartiene al package `ui`, cioè interfaccia utente.

Import importanti:

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
```

Servono per Swing e gestione eventi.

Import per socket:

```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
```

Servono per la modalità spettatore.

---

# 2. Dichiarazione della classe

```java
public class InterfacciaGioco extends javax.swing.JFrame
```

`JFrame` è la finestra principale Swing.

Estendendo `JFrame`, `InterfacciaGioco` diventa una finestra con:

- titolo;
- dimensione;
- menu;
- componenti grafici;
- comportamento di chiusura.

---

# 3. Campi principali

```java
private Gioco gioco;
private Parser parser;
```

`gioco` contiene lo stato dell'avventura.

`parser` interpreta i comandi testuali.

---

## Timer

```java
private static final int TEMPO_DECONTAMINAZIONE_TOTALE_SECONDI = 120;
private int tempoImpiegatoDecontaminazione = -1;
private transient ThreadTimer timerRunnable;
private transient Thread threadTimer;
```

- totale timer: 120 secondi;
- tempo impiegato: usato per punteggio;
- `timerRunnable`: logica del timer;
- `threadTimer`: thread che esegue il timer.

`transient` indica che questi campi non vanno serializzati.

---

## Memoria contestuale

```java
private TipoComando comandoInAttesaDiTarget = null;
```

Serve per comandi incompleti.

Esempio:

```text
prendi
```

Il gioco chiede:

```text
Cosa vuoi prendere?
```

Poi se scrivi:

```text
tessera
```

la GUI ricostruisce:

```text
prendi tessera
```

---

## Componenti Swing

```java
private JTextArea txtConsole;
private JTextField txtInput;
private JButton btnInvia;
private JList<String> listInventario;
private DefaultListModel<String> modelInventario;
private JLabel lblTimer;
```

- `txtConsole`: terminale del gioco;
- `txtInput`: campo input;
- `btnInvia`: bottone invio;
- `listInventario`: lista grafica inventario;
- `modelInventario`: modello dati della lista;
- `lblTimer`: label timer.

---

# 4. Costruttore

```java
public InterfacciaGioco() {
    initComponents();
    personalizzaInizializzazione();
}
```

Prima costruisce i componenti grafici.

Poi inizializza il gioco.

---

# 5. Metodo `personalizzaInizializzazione`

Fa partire il gioco.

Passaggi principali:

1. crea il parser;
2. crea `LaMiaAvventura`;
3. inizializza inventario grafico;
4. chiama `gioco.inizializza()`;
5. stampa titolo e descrizione iniziale;
6. inizializza database;
7. avvia server socket locale;
8. collega listener input e bottone.

---

## Avvio database

```java
connettiAlDatabase();
```

Il database viene inizializzato automaticamente.

---

## Avvio socket

```java
avviaServerSocketLocale(8888);
```

Prova ad aprire la porta 8888. Se è occupata, prova la successiva.

---

# 6. Metodo `elaboraInputUtente`

È il metodo centrale della GUI.

Legge il comando:

```java
String input = txtInput.getText().trim();
```

Se è vuoto, termina.

Poi pulisce il campo:

```java
txtInput.setText("");
```

E stampa:

```java
stampaTesto("> " + input);
```

---

## 6.1 Dialogo attivo

```java
if (((LaMiaAvventura) gioco).isDialogoAttivo()) {
    String risDialogo = ((LaMiaAvventura) gioco).elaboraDialogo(input);
    ...
    return;
}
```

Se è attivo un dialogo con Prometeo o Rancido, l'input non viene interpretato come comando normale ma come scelta numerica del dialogo.

---

## 6.2 Finale attivo

```java
if (((LaMiaAvventura) gioco).isFinaleAttivo()) {
    String risFinale = ((LaMiaAvventura) gioco).elaboraSceltaFinale(input);
    ...
}
```

Se la console finale è aperta, l'input viene interpretato come scelta finale.

Se la scelta conclude il gioco, calcola e salva il punteggio.

---

## 6.3 Comandi in attesa di target

Se il giocatore prima ha scritto:

```text
prendi
```

la GUI aspetta il target.

Se poi scrive:

```text
tessera
```

ricostruisce:

```java
input = "prendi " + input;
```

---

## 6.4 Codice cassaforte

```java
if (input.contains("2041") && gioco.getStanzaCorrente().getId() == 6) {
    String ris = ((LaMiaAvventura) gioco).digitaCodiceCassaforte("2041");
    ...
}
```

Se sei nell'Ufficio e scrivi `2041`, il gioco prova ad aprire la cassaforte.

---

## 6.5 Parsing

```java
ParserOutput output = parser.parse(input, gioco.getComandi(), gioco.getStanzaCorrente().getOggetti(), gioco.getInventario().getElementi());
```

La GUI passa al parser:

- input;
- comandi;
- oggetti della stanza;
- inventario.

---

## 6.6 Fuzzy matching

Se il comando non è riconosciuto:

```java
String sconosciuto = output.getInputInvalido();
String suggerimento = parser.suggerisciComando(sconosciuto, gioco.getComandi());
```

Esempio:

```text
guada
```

può suggerire:

```text
guarda
```

---

## 6.7 Esecuzione comando

Prima di salvare, sincronizza il timer nel gioco:

```java
if (output.getComando().getTipo() == TipoComando.SALVA) {
    sincronizzaTimerNelGioco();
}
```

Poi esegue:

```java
String risposta = gioco.elaboraComando(output);
```

Dopo il caricamento:

```java
ripristinaTimerDaGioco();
```

---

# 7. Metodo `calcolaPunteggioFinale`

```java
private int calcolaPunteggioFinale(String sceltaFinale)
```

Calcola il punteggio in base al finale e al tempo impiegato.

Basi:

```text
1 -> 500
2 -> 400
3 -> 100
```

Penalità:

```java
int penalitaTempo = tempoImpiegato * 2;
```

Punteggio:

```java
Math.max(0, base - penalitaTempo)
```

`Math.max` evita punteggi negativi.

---

# 8. Metodo `stampaTesto`

```java
public void stampaTesto(String testo)
```

Aggiunge testo alla console:

```java
txtConsole.append(testo + "\n");
```

Poi porta il cursore in fondo:

```java
txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
```

Infine trasmette il testo ai client socket:

```java
serverSocket.trasmettiAClient(testo);
```

---

# 9. Metodo `aggiornaInventarioGrafico`

```java
private void aggiornaInventarioGrafico()
```

Aggiorna la `JList` dell'inventario.

Usa stream:

```java
gioco.getInventario().getElementi().stream()
    .map(Oggetto::getNome)
    .forEach(modelInventario::addElement);
```

---

# 10. Metodo `connettiAlDatabase`

```java
private void connettiAlDatabase()
```

Chiama:

```java
DatabaseManager.inizializzaDatabase();
```

Se fallisce, mostra una finestra di errore.

---

# 11. Socket spettatore

Metodi principali:

```java
trovaPortaDisponibile
avviaServerSocketLocale
mostraInfoSocket
apriSpettatoreSocket
```

## `trovaPortaDisponibile`

Prova ad aprire una porta. Se è occupata, prova la successiva.

## `avviaServerSocketLocale`

Avvia il server socket locale sulla prima porta libera.

## `apriSpettatoreSocket`

Chiede all'utente una porta, apre un socket verso `localhost` e mostra in una finestra Swing i messaggi ricevuti.

---

# 12. Nuova partita

```java
private void nuovaPartita()
```

Resetta:

- gioco;
- console;
- inventario;
- timer;
- input;
- bottone.

Non riavvia il server socket per evitare conflitti di porta.

---

# 13. Sincronizzazione timer nel salvataggio

```java
private void sincronizzaTimerNelGioco()
```

Prima di salvare, copia lo stato del timer dalla GUI dentro `LaMiaAvventura`.

Questo è necessario perché il thread reale non viene serializzato.

---

# 14. Ripristino timer dopo caricamento

```java
private void ripristinaTimerDaGioco()
```

Dopo aver caricato una partita:

- se il timer era attivo, lo riavvia dai secondi salvati;
- se il gas era neutralizzato, mostra il tempo impiegato;
- se non era partito, mostra `Timer: Spento`.

---

# 15. `initComponents`

Costruisce tutta la GUI:

- titolo finestra;
- dimensioni;
- menu;
- console;
- input;
- bottone;
- inventario;
- timer;
- layout.

## Layout finale

```text
+-----------------------------+----------------+
| Console                     | Timer          |
|                             | Inventario     |
+-----------------------------+                |
| Input + bottone             |                |
+-----------------------------+----------------+
```

---

# 16. Argomenti MAP coperti

`InterfacciaGioco` copre:

- Swing;
- eventi;
- lambda;
- thread;
- socket;
- gestione GUI thread-safe;
- integrazione parser/motore;
- JList e modello;
- JMenuBar;
- JDialog;
- serializzazione integrata con GUI;
- database integrato con GUI.
