package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.impl.LaMiaAvventura;


/**
 * Interfaccia del Command Pattern usato per separare parser e logica delle azioni.
 */
public interface Command {
    /**
     * Esegue l'azione associata al comando parsato.
     *
     * @param game stato corrente dell'avventura
     * @param output risultato del parser con comando e oggetti coinvolti
     * @return testo da mostrare nella console di gioco
     */
    String execute(LaMiaAvventura game, ParserOutput output);
}
