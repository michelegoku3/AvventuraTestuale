# 10 — Domande di approfondimento sul codice

Questo documento risponde in modo dettagliato alle domande poste, partendo dai punti reali del codice di **Trapped Virus**. Per ogni domanda viene spiegato:

- dove si trova il codice;
- cosa fa;
- perché è scritto così;
- se è necessario al funzionamento del progetto;
- a quale argomento Java/MAP si collega.

---

# 1. Se elimino `LambdaTasks`, il progetto funziona lo stesso?

## Stringa/classe nel codice

File:

```text
core/LambdaTasks.java
```

Classe:

```java
public class LambdaTasks
```

## Risposta breve

Sì, **il gioco funziona anche se elimini `LambdaTasks.java`**, perché questa classe non è richiamata dal flusso principale del programma.

Il gioco parte da:

```text
AvventuraTestuale.java
```

che apre:

```text
InterfacciaGioco
```

Da lì vengono usati:

- `Parser`;
- `LaMiaAvventura`;
- `Command` e classi comando;
- `DatabaseManager`;
- `ThreadTimer`;
- `ServerComandi`;
- classi del modello.

`LambdaTasks` non è necessaria per muoversi, raccogliere oggetti, usare oggetti, salvare, caricare, usare il DB o completare il gioco.

## Allora a cosa serve?

`LambdaTasks` è una classe **dimostrativa**. Serve a mostrare in modo esplicito diversi argomenti dell'esercizio sulle lambda expression:

- `Predicate`;
- `Function`;
- `Consumer`;
- `Comparator`;
- `map`;
- `filter`;
- `collect`;
- `sum`;
- `max`;
- `average`;
- `groupingBy`;
- `minBy`.

Quindi è utile per dimostrare competenza sugli argomenti del corso, ma **non è indispensabile al gameplay**.

## Se il professore chiede perché c'è

Risposta possibile:

> “La classe `LambdaTasks` è una classe dimostrativa che raccoglie esempi espliciti di lambda, functional interfaces, stream e collectors applicati agli oggetti del gioco. Il gameplay usa già stream e lambda in vari punti, ma questa classe rende più evidente la copertura dell'esercizio sulle lambda expression.”

## Se l'hai tolta

Se la togli e il progetto compila ancora, va bene dal punto di vista funzionale. Però perdi una classe utile a mostrare esplicitamente alcuni argomenti sulle lambda.

---

# 2. Perché in `ParserOutput` c'è scritto il nome completo della classe `Oggetto`?

## Stringa nel codice

File:

```text
core/ParserOutput.java
```

Campi:

```java
private final com.mycompany.avventuratestuale.model.Oggetto oggetto;
private final com.mycompany.avventuratestuale.model.Oggetto oggettoSecondario;
```

## Perché è scritto così?

Qui il codice usa il **nome completamente qualificato** della classe.

Invece di scrivere:

```java
import com.mycompany.avventuratestuale.model.Oggetto;

private final Oggetto oggetto;
```

scrive direttamente:

```java
private final com.mycompany.avventuratestuale.model.Oggetto oggetto;
```

## Cosa significa

Significa:

> “Usa la classe `Oggetto` che si trova esattamente nel package `com.mycompany.avventuratestuale.model`.”

È equivalente a usare l'import.

## Perché si può fare

In Java una classe può essere indicata in due modi:

### Con import

```java
import com.mycompany.avventuratestuale.model.Oggetto;

private Oggetto oggetto;
```

### Con nome completo

```java
private com.mycompany.avventuratestuale.model.Oggetto oggetto;
```

## Quale forma è migliore?

Di solito è più leggibile usare l'import:

```java
import com.mycompany.avventuratestuale.model.Oggetto;
```

Però la forma attuale è corretta e compila.

## Cosa rappresentano i due campi

```java
oggetto
```

è l'oggetto principale del comando.

Esempio:

```text
prendi tessera
```

qui `oggetto = tessera`.

```java
oggettoSecondario
```

è il secondo oggetto coinvolto.

Esempio:

```text
usa siero condotto
```

qui:

```text
oggetto = siero
oggettoSecondario = condotto
```

---

# 3. A cosa serve `getInputInvalido()` e come funziona?

## Stringa nel codice

File:

```text
core/ParserOutput.java
```

Metodo:

```java
public String getInputInvalido() {
    if (rawInput == null || rawInput.trim().isEmpty()) {
        return null;
    }
    String[] tokens = rawInput.toLowerCase().trim().split("\\s+");
    return tokens.length == 0 ? null : tokens[0];
}
```

## A cosa serve

Serve per recuperare la prima parola scritta dall'utente quando il parser non riconosce un comando.

Esempio:

```text
guada tessera
```

Il parser non riconosce `guada`, quindi `getInputInvalido()` restituisce:

```text
guada
```

Questa parola viene poi usata dal fuzzy matching per suggerire:

```text
guarda
```

## Come funziona passo per passo

### 1. Controlla input nullo o vuoto

```java
if (rawInput == null || rawInput.trim().isEmpty()) {
    return null;
}
```

Se non c'è input, restituisce `null`.

### 2. Normalizza la stringa

```java
rawInput.toLowerCase().trim()
```

- `toLowerCase()` porta tutto in minuscolo;
- `trim()` elimina spazi iniziali e finali.

### 3. Divide in parole

```java
split("\\s+")
```

Divide la stringa in base agli spazi.

Esempio:

```text
guada tessera
```

diventa:

```text
["guada", "tessera"]
```

### 4. Restituisce il primo token

```java
return tokens.length == 0 ? null : tokens[0];
```

Quindi restituisce:

```text
guada
```

## Collegamento col fuzzy matching

In `InterfacciaGioco`, quando il comando non è riconosciuto, viene fatto:

```java
String sconosciuto = output.getInputInvalido();
String suggerimento = parser.suggerisciComando(sconosciuto, gioco.getComandi());
```

Quindi `getInputInvalido()` fornisce la parola da correggere.

---

# 4. `public class ThreadTimer implements Runnable`

## Stringa nel codice

File:

```text
core/ThreadTimer.java
```

Dichiarazione:

```java
public class ThreadTimer implements Runnable
```

## Cosa significa

Significa che `ThreadTimer` è una classe che implementa l'interfaccia `Runnable`.

`Runnable` rappresenta un compito eseguibile da un thread.

L'interfaccia `Runnable` richiede di implementare il metodo:

```java
public void run()
```

Nel progetto, `run()` contiene il ciclo del timer.

## Perché non estende direttamente `Thread`?

In Java puoi creare un thread in due modi principali:

### Estendere `Thread`

```java
class MioThread extends Thread
```

### Implementare `Runnable`

```java
class MioTask implements Runnable
```

Nel progetto è stata scelta la seconda soluzione.

Vantaggi:

- separa il compito dal thread che lo esegue;
- è più flessibile;
- la classe resta libera di estendere eventualmente un'altra classe;
- è una pratica generalmente consigliata.

## Come viene avviato

In `InterfacciaGioco`:

```java
timerRunnable = ThreadTimer.daSecondi(secondi, this);
threadTimer = new Thread(timerRunnable, "Thread-Timer-Decontaminazione");
threadTimer.start();
```

`start()` avvia un nuovo thread. La JVM chiamerà automaticamente:

```java
timerRunnable.run()
```

ma su un thread separato.

## Cosa fa `run()`

Il metodo `run()`:

1. controlla se il timer è attivo;
2. formatta il tempo;
3. aggiorna la GUI;
4. aspetta un secondo;
5. decrementa il tempo;
6. se arriva a zero, causa il game over.

---

# 5. `String.format("%02d:%02d", minuti, secondi)`

## Stringa nel codice

File:

```text
core/ThreadTimer.java
```

Codice:

```java
String tempoFormattato = String.format("%02d:%02d", minuti, secondi);
```

## Cosa fa

Crea una stringa nel formato:

```text
MM:SS
```

Esempio:

```java
minuti = 1
secondi = 5
```

risultato:

```text
01:05
```

## Come funziona la sintassi

```java
String.format(formato, valori...)
```

Il primo parametro è una stringa con segnaposto.

```java
"%02d:%02d"
```

contiene due segnaposto:

```text
%02d
%02d
```

### `%d`

Significa: inserisci un numero intero.

### `02`

Significa: usa almeno 2 cifre e, se manca una cifra, riempi con zero.

Esempi:

```text
5  -> 05
12 -> 12
```

### `:`

È un carattere normale inserito nella stringa.

## Esempi

```java
String.format("%02d:%02d", 0, 9)
```

produce:

```text
00:09
```

```java
String.format("%02d:%02d", 1, 30)
```

produce:

```text
01:30
```

## Collegamento al corso

È una funzione standard di `String`, coerente con la parte su stringhe e formattazione.

---

# 6. Perché ci sono 3 `ThreadTimer`?

Nel codice non ci sono tre classi `ThreadTimer`, ma ci sono diversi elementi collegati al timer.

## 1. La classe `ThreadTimer`

File:

```text
core/ThreadTimer.java
```

È la classe che definisce il comportamento del timer.

## 2. Il campo `timerRunnable`

In `InterfacciaGioco`:

```java
private transient ThreadTimer timerRunnable;
```

È l'oggetto che contiene la logica del timer.

## 3. Il campo `threadTimer`

In `InterfacciaGioco`:

```java
private transient Thread threadTimer;
```

È il vero thread Java che esegue il `Runnable`.

## 4. I dati serializzabili del timer

In `LaMiaAvventura` ci sono variabili come:

```java
isTimerDecontaminazioneAttivo
secondiDecontaminazioneRimanenti
tempoImpiegatoDecontaminazione
```

Questi non sono thread. Sono solo dati salvabili su file.

## Quindi perché sembrano tanti?

Perché il timer ha tre livelli:

```text
ThreadTimer = logica del conto alla rovescia
Thread = esecutore concorrente
Campi in LaMiaAvventura = stato salvabile del timer
```

Sono tutti necessari per gestire correttamente:

- esecuzione concorrente;
- aggiornamento GUI;
- salvataggio/caricamento della partita.

---

# 7. `public class CaricaCommand implements Command`

## Stringa nel codice

File:

```text
core/commands/CaricaCommand.java
```

Dichiarazione:

```java
public class CaricaCommand implements Command
```

## Come funziona `implements`

`implements` significa che la classe si impegna a implementare i metodi definiti da un'interfaccia.

L'interfaccia `Command` richiede:

```java
String execute(LaMiaAvventura game, ParserOutput output);
```

Quindi `CaricaCommand` deve fornire quel metodo.

## Cosa fa `@Override`

Esempio:

```java
@Override
public String execute(LaMiaAvventura game, ParserOutput output) {
    ...
}
```

`@Override` indica che il metodo sta sovrascrivendo o implementando un metodo già dichiarato in una superclasse o interfaccia.

## Perché è utile

Se sbagli nome o parametri del metodo, il compilatore segnala errore.

Esempio sbagliato:

```java
public String execut(...)
```

Con `@Override`, Java si accorge che non stai implementando davvero `execute`.

## Cosa fa `CaricaCommand`

Carica una partita da file:

1. usa `SalvataggioManager.caricaPartita(...)`;
2. ottiene una `LaMiaAvventura` deserializzata;
3. copia lo stato caricato nell'istanza attuale tramite `caricaStatoDa`;
4. restituisce una stringa con descrizione della stanza caricata.

---

# 8. `tutti.forEach(p -> sb.append(...))`

## Stringa nel codice

File:

```text
core/commands/ClassificaCommand.java
```

Codice:

```java
tutti.forEach(p -> sb.append("- ").append(p.getNomeGiocatore())
        .append(" - ").append(p.getPunti()).append(" punti (")
        .append(p.getDataPartita()).append(")\n"));
```

## Cosa fa

Costruisce una stringa con la classifica dei punteggi.

`tutti` è una lista di oggetti `Punteggio`.

Per ogni punteggio `p`, aggiunge al `StringBuilder` una riga.

## Esempio

Se `p` contiene:

```text
nome = Roberto
punti = 420
data = 21-06-2026 alle 22:10
```

viene aggiunta questa riga:

```text
- Roberto - 420 punti (21-06-2026 alle 22:10)
```

## Cos'è `StringBuilder`

`StringBuilder` serve a costruire stringhe in modo efficiente facendo molte concatenazioni.

Invece di fare:

```java
stringa = stringa + altro
```

si fa:

```java
sb.append(...)
```

## È una lambda?

Sì:

```java
p -> sb.append(...)
```

è una lambda che prende un `Punteggio` e aggiunge testo allo `StringBuilder`.

---

# 9. `game.getInventario().getElementi().forEach(...)`

## Stringa nel codice

File:

```text
core/commands/InventarioCommand.java
```

Codice:

```java
game.getInventario().getElementi().forEach(o -> invStr.append("- ").append(o.getNome()).append("\n"));
```

## Cosa fa

Costruisce la stringa dell'inventario.

Per ogni oggetto `o` nell'inventario, aggiunge una riga:

```text
- nomeOggetto
```

## Esempio

Se l'inventario contiene:

```text
tessera
cacciavite
fiala
```

la stringa diventa:

```text
Nel tuo zaino ci sono:
- tessera
- cacciavite
- fiala
```

## Come funziona

```java
game.getInventario()
```

prende l'inventario.

```java
.getElementi()
```

prende la lista non modificabile degli oggetti.

```java
.forEach(o -> ...)
```

ripete l'operazione per ogni oggetto.

```java
o.getNome()
```

prende il nome dell'oggetto.

---

# 10. Come funziona `getDialogoNode`? Si autochiama?

## Stringa nel codice

File:

```text
database/DialogoDAO.java
```

Metodo:

```java
public DialogoNode getDialogoNode(int id)
```

## Risposta breve

No, `getDialogoNode` **non si autochiama**.

Viene chiamato da `LaMiaAvventura` quando serve leggere un nodo di dialogo di Prometeo.

Esempio:

```java
DialogoDAO dao = new DialogoDAO();
DialogoNode nodo = dao.getDialogoNode(idDialogoCorrente);
```

## Come funziona

Il metodo riceve un id, ad esempio:

```text
1
```

Poi prepara una query SQL:

```sql
SELECT id, testo_ia, opzione1, dest1, opzione2, dest2
FROM dialoghi
WHERE id = ?
```

Il `?` viene sostituito con l'id passato.

Poi esegue la query.

Se trova una riga, crea un oggetto `DialogoNode`:

```java
return new DialogoNode(...);
```

Se non trova nulla, restituisce:

```java
null
```

## Come si passa da un nodo all'altro

In `LaMiaAvventura.elaboraDialogo`, il giocatore sceglie `1` o `2`.

Ogni nodo contiene:

```text
dest1
dest2
```

Se il giocatore sceglie `1`, il prossimo id diventa `dest1`.

Se sceglie `2`, il prossimo id diventa `dest2`.

Quindi non è ricorsione. È navigazione tra record del database.

---

# 11. A cosa servono le classi DAO?

DAO significa:

```text
Data Access Object
```

Le classi DAO servono a separare il codice SQL dalla logica principale del gioco.

Nel progetto ci sono:

```text
DialogoDAO
PunteggioDAO
```

## `DialogoDAO`

Legge i dialoghi di Prometeo dal database.

## `PunteggioDAO`

Salva e legge i punteggi.

## Perché è utile

Senza DAO, avresti query SQL sparse nel codice della GUI o dell'avventura.

Con DAO, invece:

```text
LaMiaAvventura chiede un dialogo
DialogoDAO sa come leggerlo dal DB
```

Questo rende il codice più ordinato, più modulare e più facile da spiegare.

---

# 12. Come funzionano i `PreparedStatement`?

## Esempio nel codice

File:

```text
database/PunteggioDAO.java
```

Codice:

```java
String sql = "INSERT INTO punteggi(nome_giocatore, punti) VALUES (?, ?)";
PreparedStatement pstm = conn.prepareStatement(sql);
pstm.setString(1, nomeGiocatore);
pstm.setInt(2, punti);
pstm.executeUpdate();
```

## Cosa fa

`PreparedStatement` è una query SQL con parametri.

I punti interrogativi:

```sql
?, ?
```

sono segnaposto.

Poi vengono riempiti con:

```java
pstm.setString(1, nomeGiocatore);
pstm.setInt(2, punti);
```

Il primo `?` diventa il nome.

Il secondo `?` diventa il punteggio.

## Perché si usa

È più sicuro e pulito rispetto a concatenare stringhe.

Evita problemi come:

```java
"INSERT INTO punteggi VALUES ('" + nome + "')"
```

che può rompersi se il nome contiene apici.

---

# 13. `DateTimeFormatter.ofPattern(...)`

## Stringa nel codice

File:

```text
database/PunteggioDAO.java
```

Codice:

```java
private static final DateTimeFormatter FORMATO_DATA =
    DateTimeFormatter.ofPattern("dd-MM-yyyy 'alle' HH:mm");
```

## Cosa fa `ofPattern`

Crea un formattatore di date seguendo uno schema.

Lo schema:

```text
dd-MM-yyyy 'alle' HH:mm
```

significa:

```text
dd      giorno a due cifre
MM      mese a due cifre
yyyy    anno a quattro cifre
'alle'  parola fissa
HH      ora a due cifre, formato 24 ore
mm      minuti a due cifre
```

## Esempio

Una data può diventare:

```text
21-06-2026 alle 22:45
```

## Perché `alle` è tra apici

Gli apici indicano testo letterale, cioè da stampare così com'è.

---

# 14. `Statement stm = conn.createStatement();`

## Stringa nel codice

File:

```text
database/PunteggioDAO.java
```

Codice:

```java
Statement stm = conn.createStatement();
```

## Cos'è `createStatement`

È un metodo della connessione JDBC che crea un oggetto `Statement`.

Un `Statement` permette di eseguire query SQL semplici, senza parametri.

Esempio:

```java
ResultSet rs = stm.executeQuery(sql);
```

## Differenza tra `Statement` e `PreparedStatement`

### `Statement`

Usato per query statiche, senza parametri utente.

Esempio:

```sql
SELECT * FROM punteggi ORDER BY punti DESC
```

### `PreparedStatement`

Usato per query con parametri.

Esempio:

```sql
SELECT * FROM dialoghi WHERE id = ?
```

Nel progetto sono usati entrambi.

---

# 15. Come funziona `getMiglioriPunteggi`

## Stringa nel codice

File:

```text
database/PunteggioDAO.java
```

Metodo:

```java
public List<Punteggio> getMiglioriPunteggi()
```

## Cosa fa

Restituisce la top 5 dei punteggi migliori.

## Passaggi

### 1. Inizializza il DB

```java
DatabaseManager.inizializzaDatabase();
```

Così la tabella esiste sicuramente.

### 2. Crea una lista vuota

```java
List<Punteggio> classifica = new ArrayList<>();
```

### 3. Prepara la query

```java
String sql = "SELECT id, nome_giocatore, punti, data_partita FROM punteggi ORDER BY punti DESC LIMIT 5";
```

Significa:

```text
prendi id, nome, punti e data
ordina per punti dal più alto al più basso
limita il risultato a 5 righe
```

### 4. Esegue la query

```java
ResultSet rs = stm.executeQuery(sql);
```

### 5. Scorre i risultati

```java
while (rs.next()) {
    ...
}
```

`rs.next()` passa alla riga successiva. Restituisce `false` quando non ci sono più righe.

### 6. Crea oggetti `Punteggio`

Per ogni riga:

```java
classifica.add(new Punteggio(...));
```

### 7. Restituisce la lista

```java
return classifica;
```

---

# 16. Come vengono gestiti client e socket

## Classi coinvolte

```text
socket/ServerComandi.java
socket/ClientHandler.java
ui/InterfacciaGioco.java
```

---

## 16.1 Server socket

`ServerComandi` apre una porta:

```java
serverSocket = new ServerSocket(porta);
```

Poi aspetta connessioni:

```java
Socket clientSocket = serverSocket.accept();
```

Quando un client si collega, crea un handler:

```java
ClientHandler handler = new ClientHandler(clientSocket, gui);
handler.start();
```

Ogni client viene quindi gestito da un thread separato.

---

## 16.2 Lista dei client

Il server mantiene una lista:

```java
private final List<ClientHandler> clientConnessi = new ArrayList<>();
```

Ogni client connesso viene aggiunto alla lista.

---

## 16.3 Broadcast

Quando il gioco stampa un messaggio, `InterfacciaGioco.stampaTesto` manda il testo anche al server socket:

```java
serverSocket.trasmettiAClient(testo);
```

Il server lo invia a tutti i client connessi:

```java
for (ClientHandler client : clientConnessi) {
    client.inviaMessaggio(messaggio);
}
```

Questa è la modalità spettatore.

---

## 16.4 Client handler

`ClientHandler` gestisce un singolo client.

Ha:

```java
Socket socket
PrintWriter out
BufferedReader in
```

### `PrintWriter out`

Serve a inviare testo al client.

### `BufferedReader in`

Serve a leggere testo dal client.

---

## 16.5 Socket spettatore

Nel menu Socket della GUI è possibile aprire una finestra spettatore.

La finestra si collega a una porta:

```java
new Socket("localhost", porta)
```

Poi legge continuamente messaggi dal server e li mostra in una `JTextArea`.

---

## 16.6 Perché è multithread

Il server deve poter:

- continuare ad accettare nuovi client;
- gestire client già collegati;
- non bloccare la GUI.

Per questo:

- `ServerComandi` è un thread;
- ogni `ClientHandler` è un thread;
- la GUI Swing resta sul suo thread.

---

# 17. Collegamento con gli argomenti del prof

Queste parti coprono:

- JDBC;
- DAO;
- `Statement`;
- `PreparedStatement`;
- `ResultSet`;
- lambda;
- stream;
- functional interfaces;
- `Runnable`;
- thread;
- socket;
- `ServerSocket`;
- `Socket`;
- I/O stream;
- Swing;
- aggiornamento GUI.
