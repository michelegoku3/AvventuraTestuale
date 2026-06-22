package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.model.Oggetto;

/**
 * Comando per raccogliere oggetti visibili e prendibili.
 */
public class PrendiCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        Oggetto obj = output.getOggetto();
        if (obj == null) return "Cosa vuoi prendere?";
        if (!game.getStanzaCorrente().getOggetti().contains(obj) || !obj.isVisibile()) {
            return "Non vedo '" + obj.getNome() + "' in questa stanza.";
        }
        if (!obj.isPrendibile()) return "Non puoi prendere '" + obj.getNome() + "': e' saldato o fissato.";

        game.getStanzaCorrente().rimuoviOggetto(obj);
        game.aggiungiAInventario(obj);

        return "Hai raccolto l'oggetto: " + obj.getNome() +
               ". Digita 'inventario' per vederlo o 'guarda " + obj.getNome() + "' per esaminarlo.";
    }
}
