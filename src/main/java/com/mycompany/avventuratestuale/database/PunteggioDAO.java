package com.mycompany.avventuratestuale.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PunteggioDAO {

    // Inserimento con PreparedStatement (previene SQL Injection) [Lezioni/13 - Database Connectivity (JDBC).pdf, Slide 13]
    public void aggiungiPunteggio(String nomeGiocatore, int punti) {
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

    // Lettura con Statement e ResultSet [Lezioni/13 - Database Connectivity (JDBC).pdf, Slide 14-16]
    public List<Punteggio> getMiglioriPunteggi() {
        List<Punteggio> classifica = new ArrayList<>();
        String sql = "SELECT id, nome_giocatore, punti, data_partita FROM punteggi ORDER BY punti DESC LIMIT 5";

        try (Connection conn = DatabaseManager.getConnessione();
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

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
