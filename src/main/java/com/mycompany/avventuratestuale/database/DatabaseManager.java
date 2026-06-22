package com.mycompany.avventuratestuale.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestisce connessione H2, creazione tabelle e popolamento dei dialoghi.
 */
public class DatabaseManager {


    private static final String CONNECTION_URL = "jdbc:h2:./avventuradb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static boolean inizializzato = false;

    /**
     * Apre una connessione JDBC verso il database H2 embedded.
     *
     * @return connessione pronta per query e update
     * @throws SQLException se la connessione fallisce
     */
    public static Connection getConnessione() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
    }

    /**
     * Crea le tabelle necessarie e aggiorna i dialoghi iniziali di Prometeo.
     */
    public static synchronized void inizializzaDatabase() {
        if (inizializzato) {
            return;
        }


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


            popolaDialoghiIniziali(conn);
            inizializzato = true;

        } catch (SQLException e) {
            throw new IllegalStateException("Errore d'inizializzazione database: " + e.getMessage() + " [Code: " + e.getErrorCode() + "]", e);
        }
    }

    private static void popolaDialoghiIniziali(Connection conn) throws SQLException {
        String mergeSql = "MERGE INTO dialoghi (id, testo_ia, opzione1, dest1, opzione2, dest2) KEY(id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(mergeSql)) {
            upsertDialogo(pstmt, 1,
                    "Prometeo: 'Soggetto #12. Il tuo risveglio non era previsto. Il protocollo impone che tu rimanga confinato.'",
                    "Chi sono io?", 2,
                    "Perche' sono rinchiuso qui?", 3);

            upsertDialogo(pstmt, 2,
                    "Prometeo: 'Sei un organismo sintetico costruito con materiale genetico umano. Moretti ti chiamava esperimento. Io preferisco chiamarti rischio.'",
                    "Sono umano?", 6,
                    "Chi era Moretti?", 4);

            upsertDialogo(pstmt, 3,
                    "Prometeo: 'Chimera-V4 non e' solo un virus. E' un vettore di riscrittura biologica. Tu sei l'unico portatore stabile conosciuto.'",
                    "Posso curarlo?", 5,
                    "Allora fammi uscire.", 7);

            upsertDialogo(pstmt, 4,
                    "Prometeo: 'Moretti voleva superare la morte. Ha creato copie biologiche capaci di ospitare memorie, patogeni e colpa.'",
                    "Dove teneva i suoi segreti?", 8,
                    "Perche' mi somiglia?", 6);

            upsertDialogo(pstmt, 5,
                    "Prometeo: 'Una cura teorica esiste: campione primario Chimera-V4 piu' sangue compatibile S12. Moretti ha nascosto il campione lontano dai miei protocolli.'",
                    "Dove devo cercare?", 8,
                    "Cosa succede se fallisco?", 7);

            upsertDialogo(pstmt, 6,
                    "Prometeo: 'La tua umanita' non e' una variabile binaria. Hai la firma genetica di Moretti, ma le tue scelte non sono ancora state scritte.'",
                    "Cosa devo fare adesso?", 5,
                    "Interrompi collegamento.", 0);

            upsertDialogo(pstmt, 7,
                    "Prometeo: 'Se esci contaminato, la superficie diventera' un'estensione del laboratorio. Il contenimento non e' crudelta': e' aritmetica della sopravvivenza.'",
                    "Esiste un'alternativa?", 5,
                    "Interrompi collegamento.", 0);

            upsertDialogo(pstmt, 8,
                    "Prometeo: 'Moretti concentrava tutto nel suo ufficio: potere, colpa e chiavi nello stesso luogo. Anche i suoi codici erano date, non password.'",
                    "Che data dovrei ricordare?", 9,
                    "Cosa mi aspetta nel Nucleo?", 10);

            upsertDialogo(pstmt, 9,
                    "Prometeo: '2041. L'anno in cui Chimera smise di essere ricerca e divenne peccato industriale.'",
                    "Cosa mi aspetta nel Nucleo?", 10,
                    "Interrompi collegamento.", 0);

            upsertDialogo(pstmt, 10,
                    "Prometeo: 'Quando arriverai al Nucleo, dovrai scegliere: contenere, distruggere, collaborare o fuggire. Tre scelte sono logiche. Una e' umana.'",
                    "Interrompi collegamento.", 0,
                    "Ripeti cosa devo cercare.", 5);

            System.out.println("Dialoghi dell'IA Prometeo aggiornati correttamente nella tabella relazionale.");
        }
    }

    private static void upsertDialogo(PreparedStatement pstmt, int id, String testo, String opzione1, int dest1,
                                      String opzione2, int dest2) throws SQLException {
        pstmt.setInt(1, id);
        pstmt.setString(2, testo);
        pstmt.setString(3, opzione1);
        pstmt.setInt(4, dest1);
        pstmt.setString(5, opzione2);
        pstmt.setInt(6, dest2);
        pstmt.executeUpdate();
    }
}
