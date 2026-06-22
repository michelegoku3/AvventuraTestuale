package com.mycompany.avventuratestuale.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per salvare e leggere la classifica dei punteggi.
 */
public class PunteggioDAO {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy 'alle' HH:mm");


    public void aggiungiPunteggio(String nomeGiocatore, int punti) {
        DatabaseManager.inizializzaDatabase();
        String sql = "INSERT INTO punteggi(nome_giocatore, punti) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnessione();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, nomeGiocatore);
            pstm.setInt(2, punti);

            pstm.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore inserimento punteggio: " + e.getMessage());
        }
    }


    public List<Punteggio> getMiglioriPunteggi() {
        DatabaseManager.inizializzaDatabase();
        List<Punteggio> classifica = new ArrayList<>();
        String sql = "SELECT id, nome_giocatore, punti, data_partita FROM punteggi ORDER BY punti DESC LIMIT 5";

        try (Connection conn = DatabaseManager.getConnessione();
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("data_partita");
                String dataFormattata = ts != null
                        ? ts.toLocalDateTime().format(FORMATO_DATA)
                        : LocalDateTime.now().format(FORMATO_DATA);
                classifica.add(new Punteggio(
                        rs.getInt("id"),
                        rs.getString("nome_giocatore"),
                        rs.getInt("punti"),
                        dataFormattata
                ));
            }

        } catch (SQLException e) {
            System.err.println("Errore recupero classifica: " + e.getMessage());
        }
        return classifica;
    }
}
