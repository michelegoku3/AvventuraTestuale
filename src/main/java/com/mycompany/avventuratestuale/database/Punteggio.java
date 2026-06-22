package com.mycompany.avventuratestuale.database;

/**
 * DTO immutabile per un punteggio salvato.
 */
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
