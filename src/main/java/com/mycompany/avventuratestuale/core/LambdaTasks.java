package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.model.Oggetto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Dimostrazione completa di tutti gli 8 task dell'Esercizio Lambda
 * Expressions (Esercizi/Esercizio Lambda Expressions.pdf, p. 1-3),
 * adattati al dominio del progetto (Oggetto invece di Candidato).
 */
public class LambdaTasks {

    // === Task 1: Predicate ===
    public static <T> List<T> filtra(List<T> lista, Predicate<T> criterio) {
        return lista.stream().filter(criterio).collect(Collectors.toList());
    }

    public static List<Oggetto> trovaOggettiPrendibiliVisibili(List<Oggetto> lista) {
        return filtra(lista, o -> o.isPrendibile() && o.isVisibile());
    }

    // === Task 2: Function ===
    public static double sommaValoriTassati(List<Oggetto> lista) {
        Function<Oggetto, Double> tassa = o -> o.isPrendibile() ? 1.0 : 1.5;
        return lista.stream().map(tassa).mapToDouble(Double::doubleValue).sum();
    }

    // === Task 3: Consumer ===
    public static void gridoEco(List<Oggetto> lista) {
        Consumer<Oggetto> eco = o -> {
            String nome = o.getNome();
            String out = (o.getId() % 2 == 0) ? nome.toUpperCase() : nome.toLowerCase();
            System.out.println("[ECO] " + out);
        };
        lista.forEach(eco);
    }

    // === Task 4: Comparator multi-campo ===
    public static void ordinaPerIdPoiNome(List<Oggetto> lista) {
        lista.sort((a, b) -> {
            int compId = Integer.compare(b.getId(), a.getId());
            if (compId != 0) return compId;
            return a.getNome().compareToIgnoreCase(b.getNome());
        });
    }

    // === Task 5: Pipeline filter + map + limit ===
    public static List<String> primiTrePrendibili(List<Oggetto> lista) {
        return lista.stream()
                .filter(Oggetto::isPrendibile)
                .map(o -> o.getId() + ":" + o.getNome())
                .limit(3)
                .collect(Collectors.toList());
    }

    // === Task 6: Pipeline filter + map + collect ===
    public static List<String> nomiRaccoglibili(List<Oggetto> lista) {
        return lista.stream()
                .filter(o -> o.getId() >= 100 && o.getId() < 200)
                .map(Oggetto::getNome)
                .collect(Collectors.toList());
    }

    // === Task 7: Reduction ===
    public static double sommaIdRaccoglibili(List<Oggetto> lista) {
        return lista.stream()
                .filter(Oggetto::isPrendibile)
                .mapToInt(Oggetto::getId)
                .sum();
    }

    public static Oggetto oggettoConIdMassimo(List<Oggetto> lista) {
        return lista.stream()
                .max(Comparator.comparingInt(Oggetto::getId))
                .orElse(null);
    }

    public static double mediaLunghezzaNomi(List<Oggetto> lista) {
        return lista.stream()
                .mapToInt(o -> o.getNome().length())
                .average()
                .orElse(0.0);
    }

    // === Task 8: Collectors.groupingBy ===
    public static <T> Map<String, List<T>> raggruppaPer(List<T> lista,
                                                       Function<T, String> criterio) {
        return lista.stream().collect(Collectors.groupingBy(criterio));
    }

    public static Map<String, Long> contaPer(List<Oggetto> lista,
                                              Function<Oggetto, String> criterio) {
        return lista.stream().collect(Collectors.groupingBy(criterio, Collectors.counting()));
    }

    public static Map<String, Oggetto> minimoPerStanza(List<Oggetto> lista) {
        return lista.stream().collect(Collectors.groupingBy(
                o -> String.valueOf(o.getId() / 100),
                Collectors.minBy(Comparator.comparingInt(Oggetto::getId))
        )).entrySet().stream()
                .filter(e -> e.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
    }

    // === Main di test ===
    public static void main(String[] args) {
        Random rnd = new Random(42);
        List<Oggetto> oggetti = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            final boolean prendibile = rnd.nextBoolean();
            final int id = 100 + rnd.nextInt(50);
            Oggetto o = new Oggetto(id, "oggetto_" + i, "desc_" + i);
            o.setPrendibile(prendibile);
            oggetti.add(o);
        }

        System.out.println("=== Task 1: Predicate ===");
        System.out.println("Prendibili visibili: " + trovaOggettiPrendibiliVisibili(oggetti).size());

        System.out.println("\n=== Task 2: Function (somma tassata) ===");
        System.out.println("Somma valori tassati: " + sommaValoriTassati(oggetti));

        System.out.println("\n=== Task 3: Consumer (eco primi 5) ===");
        gridoEco(oggetti.subList(0, 5));

        System.out.println("\n=== Task 4: Comparator ===");
        List<Oggetto> copia4 = new ArrayList<>(oggetti);
        ordinaPerIdPoiNome(copia4);
        System.out.println("Primi 3 dopo sort: " +
                copia4.subList(0, 3).stream().map(Oggetto::getNome).collect(Collectors.toList()));

        System.out.println("\n=== Task 5: Pipeline filter+map+limit ===");
        System.out.println("Primi 3 raccoglibili: " + primiTrePrendibili(oggetti));

        System.out.println("\n=== Task 6: Pipeline raccoglibili ===");
        List<String> nomiRac = nomiRaccoglibili(oggetti);
        System.out.println("Nomi raccoglibili (primi 5): " + nomiRac.subList(0, Math.min(5, nomiRac.size())));

        System.out.println("\n=== Task 7: Reduction ===");
        System.out.println("Somma id raccoglibili: " + sommaIdRaccoglibili(oggetti));
        System.out.println("Media lunghezza nomi: " + mediaLunghezzaNomi(oggetti));
        System.out.println("Id massimo: " + oggettoConIdMassimo(oggetti).getId());

        System.out.println("\n=== Task 8: Collectors.groupingBy ===");
        System.out.println("Conteggio prendibili/non: " +
                contaPer(oggetti, o -> o.isPrendibile() ? "prendibili" : "scenici"));
        System.out.println("Minimo per gruppo id/100 keys: " + minimoPerStanza(oggetti).keySet());
    }
}
