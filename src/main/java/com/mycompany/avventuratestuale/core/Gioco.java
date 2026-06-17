package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.model.Stanza;
import com.mycompany.avventuratestuale.model.Oggetto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Gioco implements Serializable {
    private static final long serialVersionUID = 1L;

    private Stanza stanzaCorrente;
    private List<Oggetto> inventario = new ArrayList<>();
    private List<Comando> comandi = new ArrayList<>();

    // Metodo astratto per inizializzare mappa ed elementi [Lezioni/2 - Paradigma OO.pdf, Slide 52]
    public abstract void inizializza() throws Exception;
    
    // Gestisce lo smistamento logico dei comandi
    public abstract String elaboraComando(ParserOutput output);

    // Getters e Setters (Incapsulamento)
    public Stanza getStanzaCorrente() { return stanzaCorrente; }
    public void setStanzaCorrente(Stanza stanzaCorrente) { this.stanzaCorrente = stanzaCorrente; }
    public List<Oggetto> getInventario() { return inventario; }
    public void setInventario(List<Oggetto> inventario) { this.inventario = inventario; }
    public List<Comando> getComandi() { return comandi; }
}
