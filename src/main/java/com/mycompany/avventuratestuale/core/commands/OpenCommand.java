package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.model.Oggetto;

public class OpenCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        Oggetto obj = output.getOggetto();
        if (obj == null) return "Cosa vuoi aprire?";
        
        if (obj.getId() == 104) { // ID_DIARIO
            if (!game.getInventario().contiene(obj)) {
                return "Il diario non e' nel tuo inventario. Non puoi aprirlo se non lo hai con te.";
            }
            return game.testoCompletoDiario();
        }
        if (obj.getId() == 111) { // ID_CASSAFORTE
            return "Per aprire la cassaforte digita il codice o usa un oggetto appropriato.";
        }
        if (obj.getId() == 301 && !game.isDroideRiparato()) {
            return "Il droide non ha pannelli apribili: serve un utensile per ripararlo.";
        }
        return "Non puoi aprire questo.";
    }
}
