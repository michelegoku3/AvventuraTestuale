package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

/**
 * Comando che mostra l'elenco delle azioni disponibili.
 */
public class AiutoCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        return "=== GUIDA COMANDI - PROTOCOLLO CHIMERA ===\n" +
               "Spostamenti: 'nord', 'sud', 'est', 'ovest' (anche 'n','s','e','o' e in inglese)\n" +
               "Azioni base: 'prendi <ogg>', 'lascia <ogg>', 'guarda', 'guarda <ogg>', 'inventario'\n" +
               "Azioni speciali: 'usa <ogg> [<target>]', 'apri <ogg>', 'parla [<npc>]', 'mappa'\n" +
               "Sistema: 'salva' (su file), 'carica' (da file), 'classifica' (Top 5 DB H2), 'esci'\n" +
               "Per uscire da un comando pendente scrivi 'annulla' o 'niente'.";
    }
}
