package com.mycompany.avventuratestuale.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO per leggere i nodi di dialogo dal database H2.
 */
public class DialogoDAO {


    public DialogoNode getDialogoNode(int id) {
        DatabaseManager.inizializzaDatabase();
        String sql = "SELECT id, testo_ia, opzione1, dest1, opzione2, dest2 FROM dialoghi WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnessione();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new DialogoNode(
                            rs.getInt("id"),
                            rs.getString("testo_ia"),
                            rs.getString("opzione1"),
                            rs.getInt("dest1"),
                            rs.getString("opzione2"),
                            rs.getInt("dest2")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore di caricamento dialogo dal DB: " + e.getMessage());
        }
        return null;
    }
}
