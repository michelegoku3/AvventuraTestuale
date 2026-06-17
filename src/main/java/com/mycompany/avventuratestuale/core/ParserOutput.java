package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.model.Oggetto;

public class ParserOutput {
    private final Comando comando;
    private final Oggetto oggetto;
    private final Oggetto oggettoSecondario;
    private final String inputInvalido;

    public ParserOutput(Comando comando, Oggetto oggetto, Oggetto oggettoSecondario) {
        this.comando = comando;
        this.oggetto = oggetto;
        this.oggettoSecondario = oggettoSecondario;
        this.inputInvalido = null;
    }

    public ParserOutput(Comando comando, Oggetto oggetto, Oggetto oggettoSecondario, String inputInvalido) {
        this.comando = comando;
        this.oggetto = oggetto;
        this.oggettoSecondario = oggettoSecondario;
        this.inputInvalido = inputInvalido;
    }

    public Comando getComando() { return comando; }
    public Oggetto getOggetto() { return oggetto; }
    public Oggetto getOggettoSecondario() { return oggettoSecondario; }
    public String getInputInvalido() { return inputInvalido; }
}
