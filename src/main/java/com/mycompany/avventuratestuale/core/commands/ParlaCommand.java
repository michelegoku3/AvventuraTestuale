package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

/**
 * Comando che avvia i dialoghi con NPC e terminali.
 */
public class ParlaCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {


        return game.elaboraComandoTalk(output);
    }
}
