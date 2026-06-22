package com.mycompany.avventuratestuale.core;

import java.io.Serializable;
import java.util.Set;

/**
 * Rappresenta un comando del parser con tipo e sinonimi riconosciuti.
 */
public class Comando implements Serializable {
    private static final long serialVersionUID = 1L;
    private final TipoComando tipo;
    private final Set<String> sinonimi;

    public Comando(TipoComando tipo, Set<String> sinonimi) {
        this.tipo = tipo;
        this.sinonimi = sinonimi;
    }

    public TipoComando getTipo() { return tipo; }
    public Set<String> getSinonimi() { return sinonimi; }
}
