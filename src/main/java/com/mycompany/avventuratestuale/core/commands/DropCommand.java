package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.model.Oggetto;

public class DropCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        Oggetto obj = output.getOggetto();
        if (obj == null) return "Cosa vuoi lasciare?";
        if (!game.getInventario().contiene(obj)) return "Non hai '" + obj.getNome() + "' nell'inventario.";
        game.rimuoviDaInventario(obj);
        game.getStanzaCorrente().aggiungiOggetto(obj);
        return "Hai lasciato '" + obj.getNome() + "' sul pavimento di questa stanza.";
    }
}
