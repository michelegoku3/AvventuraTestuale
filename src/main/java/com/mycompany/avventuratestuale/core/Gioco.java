package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.model.Stanza;
import com.mycompany.avventuratestuale.model.Oggetto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe astratta che modella lo stato comune di un'avventura testuale.
 */
public abstract class Gioco implements Serializable {
    private static final long serialVersionUID = 1L;

    private Stanza stanzaCorrente;


    private Inventario inventario;

    private List<Comando> comandi = new ArrayList<>();

    public abstract void inizializza() throws Exception;

    public abstract String elaboraComando(ParserOutput output);

    protected void inizializzaInventario() {
        this.inventario = Inventario.creaInv();
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public void aggiungiAInventario(Oggetto o) {
        this.inventario = this.inventario.inserisci(o);
    }

    public void rimuoviDaInventario(Oggetto o) {
        this.inventario = this.inventario.rimuovi(o);
    }

    public Stanza getStanzaCorrente() { return stanzaCorrente; }
    public void setStanzaCorrente(Stanza stanzaCorrente) { this.stanzaCorrente = stanzaCorrente; }

    public List<Comando> getComandi() { return comandi; }
}
