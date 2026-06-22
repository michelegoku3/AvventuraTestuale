package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

/**
 * Comando informativo per uscire dal gioco.
 */
public class EsciCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        return "Per uscire dal gioco chiudi la finestra con la X.";
    }
}
