package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.core.SalvataggioManager;

public class SaveCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        try {
            SalvataggioManager.salvaPartita(game, "partita_chemera.sav");
            return "Partita salvata con successo su 'partita_chemera.sav'.";
        } catch (Exception ex) {
            return "Errore durante il salvataggio: " + ex.getMessage();
        }
    }
}
