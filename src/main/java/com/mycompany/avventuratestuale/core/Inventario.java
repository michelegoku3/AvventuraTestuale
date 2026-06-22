package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.model.Oggetto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * ADT serializzabile dell'inventario, coerente con la specifica algebrica del progetto.
 */
public class Inventario implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Oggetto> elementi;

    public Inventario() {
        this.elementi = new ArrayList<>();
    }

    public static Inventario creaInv() {
        return new Inventario();
    }

    public Inventario inserisci(Oggetto x) {
        if (x == null) {
            throw new IllegalArgumentException("inserisci: x non puo' essere null");
        }
        Inventario nuovo = new Inventario();
        nuovo.elementi.addAll(this.elementi);
        nuovo.elementi.add(x);
        return nuovo;
    }

    public boolean vuoto() {
        return this.elementi.isEmpty();
    }

    public int quanti() {
        return this.elementi.size();
    }

    public boolean contiene(Oggetto y) {
        if (y == null) return false;
        return this.elementi.stream().anyMatch(o -> o.getId() == y.getId());
    }

    public Inventario rimuovi(Oggetto y) {
        Inventario nuovo = new Inventario();
        boolean rimosso = false;
        for (Oggetto o : this.elementi) {
            if (!rimosso && o.getId() == y.getId()) {
                rimosso = true;
            } else {
                nuovo.elementi.add(o);
            }
        }
        return nuovo;
    }

    public List<Oggetto> getElementi() {
        return java.util.Collections.unmodifiableList(this.elementi);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Inventario)) return false;
        Inventario other = (Inventario) obj;
        Set<Integer> ids1 = this.elementi.stream().map(Oggetto::getId).collect(Collectors.toSet());
        Set<Integer> ids2 = other.elementi.stream().map(Oggetto::getId).collect(Collectors.toSet());
        return ids1.equals(ids2);
    }

    @Override
    public int hashCode() {
        return new HashSet<>(this.elementi).hashCode();
    }

    public List<Oggetto> ordinatiPerNomePoiId() {
        List<Oggetto> copia = new ArrayList<>(this.elementi);
        copia.sort(Comparator
                .comparing(Oggetto::getNome, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Oggetto::getId));
        return copia;
    }
}
