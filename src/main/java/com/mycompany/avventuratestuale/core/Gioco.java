package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.model.Stanza;
import com.mycompany.avventuratestuale.model.Oggetto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe astratta che rappresenta il motore di gioco generico.
 * Conforme alla lezione sul Paradigma OO (Lezioni/2, Slide 50-53):
 * definisce la struttura comune a qualunque avventura testuale e delega
 * i dettagli specifici alle sottoclassi concrete.
 *
 * Lo stato del gioco (stanza corrente, inventario, comandi) e'
 * gestito qui per applicare l'information hiding (Incapsulamento).
 *
 * Aggiornamento 2026-06-17: l'inventario ora e' un'istanza dell'ADT
 * Inventario (cfr. specifica algebrica in Guida Cap. 7), non piu'
 * una List<Oggetto> nuda. Questo rende il codice conforme al
 * criterio 9 del template d'esame.
 */
public abstract class Gioco implements Serializable {
    private static final long serialVersionUID = 1L;

    private Stanza stanzaCorrente;

    /** ADT Inventario conforme alla specifica algebrica. */
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
