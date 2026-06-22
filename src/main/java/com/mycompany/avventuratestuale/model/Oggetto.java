package com.mycompany.avventuratestuale.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Modello serializzabile di un oggetto o elemento scenico del gioco.
 */
public class Oggetto implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private String nome;
    private String descrizione;
    private Set<String> sinonimi;
    private boolean prendibile = true;
    private boolean visibile = true;
    private boolean aperto = false;
    private boolean contenitore = false;

    public Oggetto(int id, String nome, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.sinonimi = new HashSet<>();
    }

    public Oggetto(int id, String nome, String descrizione, Set<String> sinonimi) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.sinonimi = sinonimi;
    }


    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Set<String> getSinonimi() { return sinonimi; }
    public void setSinonimi(Set<String> sinonimi) { this.sinonimi = sinonimi; }
    public boolean isPrendibile() { return prendibile; }
    public void setPrendibile(boolean prendibile) { this.prendibile = prendibile; }
    public boolean isVisibile() { return visibile; }
    public void setVisibile(boolean visibile) { this.visibile = visibile; }
    public boolean isAperto() { return aperto; }
    public void setAperto(boolean aperto) { this.aperto = aperto; }
    public boolean isContenitore() { return contenitore; }
    public void setContenitore(boolean contenitore) { this.contenitore = contenitore; }
}
