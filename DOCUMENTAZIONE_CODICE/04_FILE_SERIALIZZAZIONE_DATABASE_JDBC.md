# 04 — File, serializzazione, database e JDBC

# 1. Persistenza su file

Il progetto permette di salvare e caricare lo stato della partita tramite serializzazione Java.

La classe principale è:

```text
core/SalvataggioManager.java
```

---

## 1.1 Salvataggio

Metodo:

```java
public static void salvaPartita(Gioco gioco, String nomeFile) throws IOException
```

Codice essenziale:

```java
try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeFile))) {
    oos.writeObject(gioco);
}
```

## Spiegazione sintattica

### `FileOutputStream`

Apre un flusso di byte verso un file.

### `ObjectOutputStream`

Permette di scrivere oggetti Java serializzabili.

### `try (...)`

È un try-with-resources: chiude automaticamente il flusso alla fine.

### `writeObject(gioco)`

Scrive l'intero oggetto `Gioco`, con tutti gli oggetti raggiungibili e serializzabili.

---

## 1.2 Caricamento

Metodo:

```java
public static Gioco caricaPartita(String nomeFile)
```

Codice essenziale:

```java
try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeFile))) {
    return (Gioco) ois.readObject();
}
```

`readObject()` legge l'oggetto dal file. Il cast a `Gioco` è necessario perché `readObject()` restituisce `Object`.

---

## 1.3 Cosa viene salvato

Vengono salvati:

- stanza corrente;
- inventario;
- oggetti nelle stanze;
- flag narrativi;
- stato enigmi;
- stato timer della decontaminazione;
- dialoghi attivi;
- stato della cassaforte, barriera, siero, droide.

Non viene salvato direttamente il thread del timer, perché un thread è una risorsa di runtime. Vengono salvati invece:

```text
isTimerDecontaminazioneAttivo
secondiDecontaminazioneRimanenti
tempoImpiegatoDecontaminazione
```

Dopo il caricamento, la GUI ricostruisce il timer se necessario.

---

# 2. Database H2 e JDBC

Il progetto usa un database H2 embedded.

La classe principale è:

```text
database/DatabaseManager.java
```

---

## 2.1 Connessione JDBC

Metodo:

```java
public static Connection getConnessione() throws SQLException
```

Restituisce:

```java
DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)
```

Dove:

```java
private static final String CONNECTION_URL = "jdbc:h2:./avventuradb";
private static final String USER = "sa";
private static final String PASSWORD = "";
```

## Spiegazione

- `jdbc:h2:` indica il driver H2;
- `./avventuradb` indica un database locale nella cartella del progetto;
- `sa` è l'utente standard H2.

---

## 2.2 Tabelle

Il database contiene due tabelle principali:

```text
punteggi
dialoghi
```

### Tabella `punteggi`

Contiene:

```text
id
nome_giocatore
punti
data_partita
```

Serve per la classifica.

### Tabella `dialoghi`

Contiene:

```text
id
testo_ia
opzione1
dest1
opzione2
dest2
```

Serve per i dialoghi a scelta multipla di Prometeo.

---

# 3. DAO Pattern

DAO significa **Data Access Object**.

L'idea è separare il codice SQL dalla logica del gioco.

Nel progetto ci sono:

```text
PunteggioDAO
DialogoDAO
```

---

## 3.1 `PunteggioDAO`

Gestisce i punteggi.

Metodo per inserire:

```java
aggiungiPunteggio(String nomeGiocatore, int punti)
```

Usa `PreparedStatement`:

```java
String sql = "INSERT INTO punteggi(nome_giocatore, punti) VALUES (?, ?)";
PreparedStatement pstm = conn.prepareStatement(sql);
pstm.setString(1, nomeGiocatore);
pstm.setInt(2, punti);
pstm.executeUpdate();
```

## Perché `PreparedStatement`

È più sicuro di concatenare stringhe SQL perché:

- evita problemi con apici e caratteri speciali;
- protegge da SQL injection;
- separa query e parametri.

---

## 3.2 Lettura punteggi

Metodo:

```java
getMiglioriPunteggi()
```

Esegue:

```sql
SELECT id, nome_giocatore, punti, data_partita
FROM punteggi
ORDER BY punti DESC
LIMIT 5
```

Poi legge il `ResultSet`:

```java
while (rs.next()) {
    ...
}
```

Ogni riga viene convertita in un oggetto `Punteggio`.

La data viene formattata come:

```text
GG-MM-AAAA alle HH:MM
```

---

## 3.3 `DialogoDAO`

Legge un nodo di dialogo dal database:

```java
getDialogoNode(int id)
```

Esegue una query con parametro:

```sql
SELECT id, testo_ia, opzione1, dest1, opzione2, dest2
FROM dialoghi
WHERE id = ?
```

Restituisce un `DialogoNode`.

---

# 4. Dialoghi Prometeo su database

Prometeo usa dialoghi caricati da H2.

Ogni nodo ha due opzioni. Esempio concettuale:

```text
Nodo 1:
Prometeo parla.
1 -> vai al nodo 2
2 -> vai al nodo 3
```

Se la destinazione è `0`, il dialogo termina.

Questo mostra un uso concreto del database nel gameplay, non solo una classifica accessoria.

---

# 5. Collegamento con gli argomenti del corso

Questa parte copre:

- file;
- stream di byte;
- serializzazione;
- deserializzazione;
- eccezioni I/O;
- JDBC;
- H2 embedded;
- SQL;
- `Connection`;
- `Statement`;
- `PreparedStatement`;
- `ResultSet`;
- DAO Pattern.
