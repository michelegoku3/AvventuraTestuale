package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

/**
 * Comando che mostra la mappa ASCII del laboratorio.
 */
public class MappaCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        return game.renderMappaASCII();
    }
}
