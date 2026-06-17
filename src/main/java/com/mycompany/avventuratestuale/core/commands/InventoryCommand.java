package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

public class InventoryCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        if (game.getInventario().vuoto()) return "Il tuo inventario e' desolatamente vuoto.";
        StringBuilder invStr = new StringBuilder("Nel tuo zaino ci sono:\n");
        game.getInventario().getElementi().forEach(o -> invStr.append("- ").append(o.getNome()).append("\n"));
        return invStr.toString();
    }
}
