package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

public class TalkCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        // The Talk logic is slightly complex because it transitions into a dialogue state
        // In the original code, it sets idDialogoCorrente and calls mostraNodoDialogo
        // We can call a helper method in the game
        return game.elaboraComandoTalk(output);
    }
}
