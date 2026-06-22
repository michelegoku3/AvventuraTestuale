# Classe `DatabaseManager` spiegata per intero

File:

```text
src/main/java/com/mycompany/avventuratestuale/database/DatabaseManager.java
```

La classe `DatabaseManager` gestisce:

- connessione al database H2;
- creazione delle tabelle;
- inizializzazione dei dialoghi di Prometeo.

È una classe di servizio, con metodi e campi statici.

---

# 1. Package e import

```java
package com.mycompany.avventuratestuale.database;
```

La classe appartiene al package `database`.

Import:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
```

## Cosa sono

- `Connection`: rappresenta una connessione al database.
- `DriverManager`: crea connessioni JDBC.
- `PreparedStatement`: query SQL parametrica.
- `SQLException`: eccezione SQL.
- `Statement`: oggetto per eseguire query SQL semplici.

---

# 2. Dichiarazione della classe

```java
public class DatabaseManager {
```

La classe non viene istanziata nel progetto. I suoi metodi sono statici.

Questo significa che si usa così:

```java
DatabaseManager.inizializzaDatabase();
DatabaseManager.getConnessione();
```

Non così:

```java
new DatabaseManager()
```

---

# 3. Costanti di connessione

```java
private static final String CONNECTION_URL = "jdbc:h2:./avventuradb";
private static final String USER = "sa";
private static final String PASSWORD = "";
private static boolean inizializzato = false;
```

## `CONNECTION_URL`

Indica dove si trova il database.

```text
jdbc:h2:./avventuradb
```

Significa:

- usa JDBC;
- usa H2;
- crea o apre un database locale chiamato `avventuradb` nella cartella corrente.

## `USER`

Utente del database H2.

```text
sa
```

## `PASSWORD`

Password vuota.

## `inizializzato`

Flag booleano che evita di inizializzare il database più volte nella stessa esecuzione.

---

# 4. Metodo `getConnessione`

```java
public static Connection getConnessione() throws SQLException {
    return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
}
```

## Cosa fa

Apre una connessione JDBC al database H2.

## Spiegazione sintassi

### `public static`

Il metodo è accessibile dall'esterno e appartiene alla classe.

### `Connection`

È il tipo restituito.

### `throws SQLException`

Il metodo può lanciare un'eccezione SQL.

Chi lo chiama deve gestire l'errore.

### `DriverManager.getConnection(...)`

È il metodo standard JDBC per ottenere una connessione.

---

# 5. Metodo `inizializzaDatabase`

```java
public static synchronized void inizializzaDatabase()
```

## Cosa fa

Crea le tabelle se non esistono e aggiorna i dialoghi di Prometeo.

## Perché `synchronized`

Potrebbero esserci più parti del programma che provano a inizializzare il DB. `synchronized` evita che due thread entrino contemporaneamente nel metodo.

## Controllo iniziale

```java
if (inizializzato) {
    return;
}
```

Se il database è già stato inizializzato, il metodo termina subito.

---

# 6. Creazione tabella `punteggi`

```java
String sqlPunteggi = "CREATE TABLE IF NOT EXISTS punteggi (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "nome_giocatore VARCHAR(255) NOT NULL, " +
                     "punti INT NOT NULL, " +
                     "data_partita TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";
```

## Cosa fa

Crea la tabella dei punteggi, se non esiste già.

## Spiegazione SQL

### `CREATE TABLE IF NOT EXISTS`

Crea la tabella solo se non esiste.

### `id INT AUTO_INCREMENT PRIMARY KEY`

Campo numerico auto-incrementale usato come chiave primaria.

### `nome_giocatore VARCHAR(255) NOT NULL`

Nome del giocatore, stringa massima 255 caratteri, obbligatoria.

### `punti INT NOT NULL`

Punteggio numerico, obbligatorio.

### `data_partita TIMESTAMP DEFAULT CURRENT_TIMESTAMP`

Data e ora della partita. Se non viene specificata, H2 usa automaticamente il timestamp corrente.

---

# 7. Creazione tabella `dialoghi`

```java
String sqlDialoghi = "CREATE TABLE IF NOT EXISTS dialoghi (" +
                     "id INT PRIMARY KEY, " +
                     "testo_ia VARCHAR(1000) NOT NULL, " +
                     "opzione1 VARCHAR(255), " +
                     "dest1 INT, " +
                     "opzione2 VARCHAR(255), " +
                     "dest2 INT" +
                     ")";
```

## Cosa contiene

Ogni riga è un nodo di dialogo di Prometeo.

Campi:

- `id`: identificativo del nodo;
- `testo_ia`: testo pronunciato da Prometeo;
- `opzione1`: prima scelta;
- `dest1`: nodo di destinazione della prima scelta;
- `opzione2`: seconda scelta;
- `dest2`: nodo di destinazione della seconda scelta.

Se `dest1` o `dest2` vale `0`, il dialogo termina.

---

# 8. Try-with-resources

```java
try (Connection conn = getConnessione();
     Statement stmt = conn.createStatement()) {
    ...
}
```

## Cosa fa

Crea una connessione e uno statement. Entrambi vengono chiusi automaticamente alla fine del blocco.

## Perché è utile

Evita di dimenticare di chiudere risorse importanti.

---

# 9. `Statement`

```java
Statement stmt = conn.createStatement();
```

`Statement` serve per eseguire SQL senza parametri.

Nel codice:

```java
stmt.executeUpdate(sqlPunteggi);
stmt.executeUpdate(sqlDialoghi);
```

`executeUpdate` si usa per comandi SQL che modificano lo schema o i dati, come `CREATE`, `INSERT`, `UPDATE`, `DELETE`.

---

# 10. Popolamento dialoghi

```java
popolaDialoghiIniziali(conn);
inizializzato = true;
```

Dopo aver creato le tabelle, il metodo inserisce o aggiorna i dialoghi di Prometeo.

`inizializzato = true` evita ripetizioni successive.

---

# 11. Gestione errori

```java
catch (SQLException e) {
    throw new IllegalStateException("Errore d'inizializzazione database: " + e.getMessage() + " [Code: " + e.getErrorCode() + "]", e);
}
```

Se qualcosa va male nel database, viene lanciata una `IllegalStateException`.

Questo trasforma un errore SQL in un errore più generale di stato dell'applicazione.

---

# 12. Metodo `popolaDialoghiIniziali`

```java
private static void popolaDialoghiIniziali(Connection conn) throws SQLException
```

È privato perché viene usato solo dentro `DatabaseManager`.

Riceve una connessione già aperta.

---

# 13. `MERGE INTO`

```java
String mergeSql = "MERGE INTO dialoghi (id, testo_ia, opzione1, dest1, opzione2, dest2) KEY(id) VALUES (?, ?, ?, ?, ?, ?)";
```

## Cosa fa

`MERGE` inserisce o aggiorna una riga.

Se il nodo con quell'id non esiste, lo inserisce.

Se esiste già, lo aggiorna.

## Perché è utile

Se il database esiste già, i dialoghi vengono comunque aggiornati all'ultima versione senza cancellare manualmente il file del database.

---

# 14. `PreparedStatement`

```java
try (PreparedStatement pstmt = conn.prepareStatement(mergeSql)) {
    ...
}
```

`PreparedStatement` è una query SQL con parametri.

I `?` nel testo SQL vengono sostituiti da valori reali.

Vantaggi:

- più sicurezza;
- meno errori con stringhe;
- codice più ordinato.

---

# 15. Metodo `upsertDialogo`

```java
private static void upsertDialogo(PreparedStatement pstmt,
                                  int id,
                                  String testo,
                                  String opzione1,
                                  int dest1,
                                  String opzione2,
                                  int dest2) throws SQLException
```

## Cosa fa

Riempie i parametri del `PreparedStatement` e lo esegue.

---

## 15.1 Parametri numerati

```java
pstmt.setInt(1, id);
pstmt.setString(2, testo);
pstmt.setString(3, opzione1);
pstmt.setInt(4, dest1);
pstmt.setString(5, opzione2);
pstmt.setInt(6, dest2);
pstmt.executeUpdate();
```

I numeri corrispondono ai `?` nella query.

Query:

```sql
VALUES (?, ?, ?, ?, ?, ?)
```

Corrispondenza:

```text
1 -> id
2 -> testo_ia
3 -> opzione1
4 -> dest1
5 -> opzione2
6 -> dest2
```

---

# 16. Esempio di nodo dialogo

```java
upsertDialogo(pstmt, 1,
    "Prometeo: 'Soggetto #12. Il tuo risveglio non era previsto...'",
    "Chi sono io?", 2,
    "Perche' sono rinchiuso qui?", 3);
```

Significa:

```text
Nodo 1:
Testo di Prometeo
Opzione 1 -> nodo 2
Opzione 2 -> nodo 3
```

---

# 17. Come il gioco usa questi dialoghi

In `LaMiaAvventura`, quando il giocatore parla con Prometeo:

```java
idDialogoCorrente = 1;
return mostraNodoDialogo(idDialogoCorrente);
```

`mostraNodoDialogo` usa `DialogoDAO`, che legge il nodo dal DB tramite `DatabaseManager`.

---

# 18. Argomenti MAP coperti

`DatabaseManager` copre:

- JDBC;
- H2 embedded;
- SQL;
- `Connection`;
- `Statement`;
- `PreparedStatement`;
- eccezioni SQL;
- try-with-resources;
- persistenza relazionale;
- separazione delle responsabilità.
