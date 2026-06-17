package com.mycompany.avventuratestuale.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // Connessione ad H2 Embedded localmente [Lezioni/13 - Database Connectivity (JDBC).pdf, Slide 4, 7-8]
    private static final String CONNECTION_URL = "jdbc:h2:./avventuradb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnessione() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
    }

    public static void inizializzaDatabase() {
        // Creazione tabelle relazionali [Esercizio JDBC.pdf]
        String sqlPunteggi = "CREATE TABLE IF NOT EXISTS punteggi (" +
                             "id INT AUTO_INCREMENT PRIMARY KEY, " +
                             "nome_giocatore VARCHAR(255) NOT NULL, " +
                             "punti INT NOT NULL, " +
                             "data_partita TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                             ")";

        String sqlDialoghi = "CREATE TABLE IF NOT EXISTS dialoghi (" +
                             "id INT PRIMARY KEY, " +
                             "testo_ia VARCHAR(1000) NOT NULL, " +
                             "opzione1 VARCHAR(255), " +
                             "dest1 INT, " +
                             "opzione2 VARCHAR(255), " +
                             "dest2 INT" +
                             ")";

        try (Connection conn = getConnessione();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sqlPunteggi);
            stmt.executeUpdate(sqlDialoghi);
            System.out.println("Tabelle database H2 create/verificate.");

            // Popola i dialoghi dell'IA Prometeo se la tabella è vuota
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM dialoghi");
            if (rs.next() && rs.getInt(1) == 0) {
                popolaDialoghiIniziali(conn);
            }
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Errore d'inizializzazione database: " + e.getMessage() + " [Code: " + e.getErrorCode() + "]");
        }
    }

    private static void popolaDialoghiIniziali(Connection conn) throws SQLException {
        String insertSql = "INSERT INTO dialoghi(id, testo_ia, opzione1, dest1, opzione2, dest2) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            // Nodo 1
            pstmt.setInt(1, 1);
            pstmt.setString(2, "Prometeo: 'Rilevato Soggetto #12. Non dovresti essere cosciente. Sei un innesto biologico portatore.'");
            pstmt.setString(3, "Chi sono io realmente?");
            pstmt.setInt(4, 2);
            pstmt.setString(5, "Fammi uscire immediatamente da qui!");
            pstmt.setInt(6, 3);
            pstmt.executeUpdate();

            // Nodo 2
            pstmt.setInt(1, 2);
            pstmt.setString(2, "Prometeo: 'Sei un clone organico programmato per ospitare il genoma del virus Chimera, creato dal Dr. Moretti.'");
            pstmt.setString(3, "Cosa è successo al vero Dr. Moretti?");
            pstmt.setInt(4, 4);
            pstmt.setString(5, "Non mi interessa del virus, esigo di uscire!");
            pstmt.setInt(6, 3);
            pstmt.executeUpdate();

            // Nodo 3
            pstmt.setInt(1, 3);
            pstmt.setString(2, "Prometeo: 'Il protocollo di biocontenimento lo vieta. Il contagio distruggerebbe l'ecosistema in superficie.'");
            pstmt.setString(3, "Ci deve essere un modo per curarlo.");
            pstmt.setInt(4, 5);
            pstmt.setString(5, "Troverò comunque la via per andarmene.");
            pstmt.setInt(6, 0); // 0 indica termine dialogo
            pstmt.executeUpdate();

            // Nodo 4
            pstmt.setInt(1, 4);
            pstmt.setString(2, "Prometeo: 'Moretti ha tentato di asportare il virus violando il lockdown. È stato terminato dai reattori termici.'");
            pstmt.setString(3, "Aiutami a rimediare al suo errore e a guarire.");
            pstmt.setInt(4, 5);
            pstmt.setString(5, "Che orrore...");
            pstmt.setInt(6, 0);
            pstmt.executeUpdate();

            // Nodo 5
            pstmt.setInt(1, 5);
            pstmt.setString(2, "Prometeo: 'Il tuo sangue contiene cloni immunitari sani. Se unisci la FIALA al SANGUE nel sintetizzatore molecolare del laboratorio genetico, otterrai la cura.'");
            pstmt.setString(3, "Grazie Prometeo. Ci proverò immediatamente.");
            pstmt.setInt(4, 0);
            pstmt.setString(5, "Interrompi collegamento.");
            pstmt.setInt(6, 0);
            pstmt.executeUpdate();

            System.out.println("Dialoghi dell'IA Prometeo inseriti correttamente nella tabella relazionale.");
        }
    }
}
