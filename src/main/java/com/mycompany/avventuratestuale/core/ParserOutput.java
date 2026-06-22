package com.mycompany.avventuratestuale.core;

import java.io.Serializable;

/**
 * Risultato del parsing di un comando utente.
 */
public class ParserOutput implements Serializable {
    private final Comando comando;
    private final com.mycompany.avventuratestuale.model.Oggetto oggetto;
    private final com.mycompany.avventuratestuale.model.Oggetto oggettoSecondario;
    private final String rawInput;

    public ParserOutput(Comando comando, com.mycompany.avventuratestuale.model.Oggetto oggetto,
                        com.mycompany.avventuratestuale.model.Oggetto oggettoSecondario, String rawInput) {
        this.comando = comando;
        this.oggetto = oggetto;
        this.oggettoSecondario = oggettoSecondario;
        this.rawInput = rawInput;
    }

    public Comando getComando() { return comando; }
    public com.mycompany.avventuratestuale.model.Oggetto getOggetto() { return oggetto; }
    public com.mycompany.avventuratestuale.model.Oggetto getOggettoSecondario() { return oggettoSecondario; }
    public String getRawInput() { return rawInput; }


    public String getInputInvalido() {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return null;
        }
        String[] tokens = rawInput.toLowerCase().trim().split("\\s+");
        return tokens.length == 0 ? null : tokens[0];
    }
}
