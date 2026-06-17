package com.mycompany.avventuratestuale.core;

import java.io.*;

public class SalvataggioManager {

    // Salva lo stato corrente del gioco su un file binario (Serializzazione) [Lezioni/10 - Input Output.pdf, Slide 31-33]
    public static void salvaPartita(Gioco gioco, String nomeFile) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeFile))) {
            oos.writeObject(gioco);
        }
    }

    // Carica lo stato del gioco da un file binario (Deserializzazione) [Lezioni/10 - Input Output.pdf, Slide 31-33]
    public static Gioco caricaPartita(String nomeFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeFile))) {
            return (Gioco) ois.readObject();
        }
    }
}
