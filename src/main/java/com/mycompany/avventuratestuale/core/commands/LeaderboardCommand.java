package com.mycompany.avventuratestuale.core.commands;

import com.mycompany.avventuratestuale.core.Command;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.impl.LaMiaAvventura;
import com.mycompany.avventuratestuale.database.PunteggioDAO;
import com.mycompany.avventuratestuale.database.Punteggio;
import java.util.List;

public class LeaderboardCommand implements Command {
    @Override
    public String execute(LaMiaAvventura game, ParserOutput output) {
        PunteggioDAO dao = new PunteggioDAO();
        List<Punteggio> tutti = dao.getMiglioriPunteggi();
        if (tutti.isEmpty()) return "La classifica H2 e' vuota. Completa il gioco e inserisci il tuo nome!";
        StringBuilder sb = new StringBuilder("=== CLASSIFICA H2 (Top 5) ===\n");
        tutti.forEach(p -> sb.append("- ").append(p.getNomeGiocatore())
                .append(" - ").append(p.getPunti()).append(" pt (")
                .append(p.getDataPartita()).append(")\n"));
        return sb.toString();
    }
}
