package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.core.SalvataggioManager;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

/**
 * Comando per caricare una partita salvata.
 */
public class CaricaCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        try {
            LaMiaAvventura caricata = (LaMiaAvventura) SalvataggioManager.caricaPartita("partita_chemera.sav");
            game.caricaStatoDa(caricata);

            return "[CLEAR_CHAT]Partita caricata correttamente da 'partita_chemera.sav'.\n" +
                   "Stanza attuale: " + game.getStanzaCorrente().getNome() + "\n" +
                   "Inventario: " + (game.getInventario().vuoto() ? "vuoto" :
                           game.getInventario().getElementi().size() + " oggetti.") + "\n\n" +
                   game.getStanzaDescrizioneCompleta(game.getStanzaCorrente());
        } catch (Exception ex) {
            return "Errore durante il caricamento: " + ex.getMessage();
        }
    }
}
