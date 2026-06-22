package com.mycompany.avventuratestuale.core;

import java.io.*;

/**
 * Gestisce salvataggio e caricamento della partita tramite serializzazione Java.
 */
public class SalvataggioManager {


    /**
     * Serializza su file lo stato corrente dell'avventura.
     *
     * @param gioco oggetto gioco da salvare
     * @param nomeFile percorso del file di salvataggio
     * @throws IOException se la scrittura fallisce
     */
    public static void salvaPartita(Gioco gioco, String nomeFile) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeFile))) {
            oos.writeObject(gioco);
        }
    }


    /**
     * Deserializza da file una partita salvata.
     *
     * @param nomeFile percorso del file di salvataggio
     * @return gioco caricato
     * @throws IOException se la lettura fallisce
     * @throws ClassNotFoundException se il file contiene classi non disponibili
     */
    public static Gioco caricaPartita(String nomeFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeFile))) {
            return (Gioco) ois.readObject();
        }
    }
}
