package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.core.TipoComando;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

/**
 * Comando che delega gli spostamenti al motore dell'avventura.
 */
public class MovimentoCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        return game.gestisciSpostamento(output.getComando().getTipo());
    }
}
