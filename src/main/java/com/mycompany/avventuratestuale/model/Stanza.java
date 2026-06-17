package com.mycompany.avventuratestuale.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stanza implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private String nome;
    private String descrizione;
    
    // Riferimenti alle stanze adiacenti per lo spostamento geografico [Lab 1 - Introduzione.pdf, p. 23]
    private Stanza nord = null;
    private Stanza sud = null;
    private Stanza est = null;
    private Stanza ovest = null;
    
    // Composizione: una stanza possiede oggetti interattivi [Lezioni/2 - Paradigma OO.pdf, Slide 45]
    private List<Oggetto> oggetti = new ArrayList<>();

    public Stanza(int id, String nome, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // Getters e Setters (Incapsulamento)
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    
    public Stanza getNord() { return nord; }
    public void setNord(Stanza nord) { this.nord = nord; }
    public Stanza getSud() { return sud; }
    public void setSud(Stanza sud) { this.sud = sud; }
    public Stanza getEst() { return est; }
    public void setEst(Stanza est) { this.est = est; }
    public Stanza getOvest() { return ovest; }
    public void setOvest(Stanza ovest) { this.ovest = ovest; }
    
    public List<Oggetto> getOggetti() { return oggetti; }
    public void aggiungiOggetto(Oggetto obj) { this.oggetti.add(obj); }
    public void rimuoviOggetto(Oggetto obj) { this.oggetti.remove(obj); }
}
