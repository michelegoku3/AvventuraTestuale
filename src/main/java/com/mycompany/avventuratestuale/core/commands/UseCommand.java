package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.model.Oggetto;

public class UseCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        return game.gestisciUsoOggetto(output);
    }
}
