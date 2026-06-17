package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.model.Oggetto;

public class LoadCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        try {
            LaMiaAvventura caricata = (LaMiaAvventura) com.mycompany.avventuratestuale.core.SalvataggioManager.caricaPartita("partita_chemera.sav");
            return "[CLEAR_CHAT]Partita caricata da file.\n" +
                   "Stanza attuale: " + caricata.getStanzaCorrente().getNome() + "\n" +
                   "Inventario: " + (caricata.getInventario().vuoto() ? "vuoto" :
                           caricata.getInventario().getElementi().size() + " oggetti.");
        } catch (Exception ex) {
            return "Errore durante il caricamento: " + ex.getMessage();
        }
    }
}
