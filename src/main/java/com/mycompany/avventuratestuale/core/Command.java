package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.impl.LaMiaAvventura;

/**
 * Interfaccia per il Command Pattern.
 * Incapsula una richiesta come un oggetto, permettendo di parametrizzare 
 * i client con diverse richieste.
 */
public interface Command {
    String execute(LaMiaAvventura game, ParserOutput output);
}
