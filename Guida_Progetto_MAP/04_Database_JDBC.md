# Capitolo 4: Database e Connettività JDBC
### Obiettivo: Progettare un'infrastruttura di persistenza dei dati relazionale (come gli High Scores dei giocatori) integrando il DBMS embedded H2 e l'architettura di design pattern DAO (Data Access Object).

Questo capitolo risponde al criterio 4 di valutazione (**Utilizzo di database/JDBC**) `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`.

---

## 1. Il DBMS Embedded H2 secondo la Didattica
Nelle lezioni del corso viene spiegato che:
> *"Per svolgere gli esercizi useremo il Database Engine H2 poiché può essere utilizzato in modo embedded senza necessità di installare un server (la modalità server è comunque disponibile). Implementa nativamente (100% Java) il protocollo JDBC"* `[Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 4]`.

Se utilizzi Maven nel tuo progetto NetBeans, la dipendenza ufficiale insegnata a lezione da aggiungere al file `pom.xml` è `[Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 5]`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
</dependency>
```

---

## 2. Il Design Pattern DAO (Data Access Object)
In conformità con quanto richiesto dal docente nelle prove di laboratorio `[Esercizio JDBC.pdf, p. 1]`, la gestione del database deve essere strutturata in modo pulito seguendo il pattern **DAO** (Data Access Object) e separando la configurazione della connessione (classe `DatabaseManager`) dalle query di dominio.

Andiamo a salvare una classifica dei migliori punteggi raggiunti dai giocatori (la Tabella `punteggi`).

### A. La Classe Entity `Punteggio.java`
Rappresenta il modello logico di un record di punteggio.
```java
package it.uniba.map.gioco.database;

public class Punteggio {
    private final int id;
    private final String nomeGiocatore;
    private final int punti;
    private final String dataPartita;

    public Punteggio(int id, String nomeGiocatore, int punti, String dataPartita) {
        this.id = id;
        this.nomeGiocatore = nomeGiocatore;
        this.punti = punti;
        this.dataPartita = dataPartita;
    }

    public int getId() { return id; }
    public String getNomeGiocatore() { return nomeGiocatore; }
    public int getPunti() { return punti; }
    public String getDataPartita() { return dataPartita; }
}
```

### B. Gestore della Connessione `DatabaseManager.java`
Gestisce la stringa di connessione in modalità embedded locale (il database scriverà su un file `avventuradb` locale) `[Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 7-8]`.

```java
package it.uniba.map.gioco.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // Stringa di connessione H2 per creare un file di db locale chiamato "avventuradb"
    private static final String CONNECTION_URL = "jdbc:h2:./avventuradb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    // Restituisce una connessione aperta sul database
    public static Connection getConnessione() throws SQLException {
        // Connessione con parametri di username e password [Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 8]
        return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
    }

    // Inizializza le tabelle necessarie all'inizio del gioco
    public static void inizializzaDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS punteggi (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "nome_giocatore VARCHAR(255) NOT NULL, " +
                     "punti INT NOT NULL, " +
                     "data_partita TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";

        try (Connection conn = getConnessione();
             Statement stmt = conn.createStatement()) {
            
            // Esecuzione della query di creazione della tabella tramite executeUpdate [Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 11-12]
            stmt.executeUpdate(sql);
            System.out.println("Tabella punteggi H2 verificata/creata con successo.");
            
        } catch (SQLException e) {
            System.err.println("Errore nell'inizializzazione del database: " + e.getMessage());
        }
    }
}
```

### C. La Classe DAO `PunteggioDAO.java`
Contiene la logica per le query SQL di inserimento e selezione, in modo analogo alla classe `RobotDAO` assegnata nell'esercitazione di laboratorio `[Esercizio JDBC.pdf, p. 1]`.

```java
package it.uniba.map.gioco.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PunteggioDAO {

    // Inserisce un nuovo record nel database (PreparedStatement sicuro)
    public void aggiungiPunteggio(String nomeGiocatore, int punti) {
        String sql = "INSERT INTO punteggi(nome_giocatore, punti) VALUES (?, ?)";

        // try-with-resources per auto-chiusura di Connection e PreparedStatement [Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 13]
        try (Connection conn = DatabaseManager.getConnessione();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            
            pstm.setString(1, nomeGiocatore); // I parametri partono dall'indice 1 [Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 13]
            pstm.setInt(2, punti);
            
            pstm.executeUpdate(); // Esegue la query di modifica [Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 11]
            
        } catch (SQLException e) {
            // Gestione dell'eccezione SQLException [Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 9]
            System.err.println("Errore inserimento punteggio: " + e.getMessage() + " [Codice d'errore: " + e.getErrorCode() + "]");
        }
    }

    // Estrae i migliori 5 punteggi (Statement di interrogazione ResultSet)
    public List<Punteggio> getMiglioriPunteggi() {
        List<Punteggio> classifica = new ArrayList<>();
        String sql = "SELECT id, nome_giocatore, punti, data_partita FROM punteggi ORDER BY punti DESC LIMIT 5";

        try (Connection conn = DatabaseManager.getConnessione();
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) { // executeQuery restituisce un ResultSet [Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 14]

            // Naviga tra le tuple restituite [Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 14-15]
            while (rs.next()) {
                classifica.add(new Punteggio(
                        rs.getInt("id"),
                        rs.getString("nome_giocatore"),
                        rs.getInt("punti"),
                        rs.getString("data_partita")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero classifica: " + e.getMessage());
        }
        return classifica;
    }
}
```

---

## 3. Conformità Didattica e Chiarimenti per l'Orale d'Esame
Ecco gli argomenti teorici esposti nelle slide di JDBC da sapere assolutamente per superare brillantemente l'interrogazione:

* **Perché usare `PreparedStatement` per l'inserimento?**
  Nelle slide viene evidenziato l'uso di `PreparedStatement` per rimpiazzare i segnaposto `?` con valori tipizzati `[Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 13]`. Oltre a garantire la corretta conversione dei tipi (es. escape automatico delle stringhe con apici), questa tecnica è di importanza critica a livello di sicurezza informatica poiché **impedisce attacchi di SQL Injection** neutralizzando i tentativi dell'utente di iniettare comandi dannosi tramite il proprio nome di gioco.
* **Gestione delle Risorse e `try-with-resources`**:
  > *"Gli Statement vanno sempre chiusi tramite il metodo close() per liberare risorse"* `[Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 11]`.
  Utilizzando i blocchi `try-with-resources`, la JVM assicura l'invocazione automatica di `close()` su connessioni, statements e risultati, evitando leak di memoria o blocchi persistenti dei file del database H2 (problema del file "locked").
* **Analisi di `SQLException`**:
  Nelle slide `[Lezioni/13 - JAVA - Database Connectivity (JDBC).pdf, Slide 9]` vengono indicati tre metodi fondamentali per analizzare i crash del DB:
  * `getMessage()`: per la descrizione testuale dell'errore.
  * `getSQLState()`: per i codici standard ISO/ANSI.
  * `getErrorCode()`: per ottenere l'errore numerico proprietario del driver H2.

*Passa al [**Capitolo 5: Thread e Programmazione Concorrente**](./05_Thread_e_Timer.md) per scoprire come inserire la programmazione concorrente e un conto alla rovescia nel gioco!*
