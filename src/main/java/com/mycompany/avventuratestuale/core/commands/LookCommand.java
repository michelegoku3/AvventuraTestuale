package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.model.Oggetto;

public class LookCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        Oggetto obj = output.getOggetto();
        if (obj != null) {
            if (obj.getId() == 104) { // ID_DIARIO
                if (!game.getInventario().contiene(obj)) {
                    return "Il diario non e' nel tuo inventario. Non puoi esaminarlo se non lo hai con te.";
                }
                return game.testoCompletoDiario();
            }
            return game.descrizioneOggetto(obj);
        }
        return game.getStanzaDescrizioneCompleta(game.getStanzaCorrente());
    }
}
