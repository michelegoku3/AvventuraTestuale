package com.mycompany.avventuratestuale.core;

import com.mycompany.avventuratestuale.model.Oggetto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Predicate;

public class Parser {

    private final Set<String> stopwords = Set.of(
        "il", "lo", "la", "i", "gli", "le", "un", "uno", "una", 
        "di", "a", "da", "in", "con", "su", "per", "tra", "fra"
    );

    // Metodo generico che accetta un Predicate (Lambda) [Esercizi/Esercizio Lambda Expressions.pdf, p. 1]
    public List<Oggetto> filtraOggetti(List<Oggetto> lista, Predicate<Oggetto> criterio) {
        return lista.stream()
                .filter(criterio)
                .collect(Collectors.toList());
    }

    public ParserOutput parse(String input, List<Comando> comandi, List<Oggetto> oggettiStanza, List<Oggetto> inventario) {
        String pulito = input.toLowerCase().trim();
        String[] tokens = pulito.split("\\s+");
        
        if (tokens.length == 0 || tokens[0].isEmpty()) {
            return null;
        }

        String primoToken = tokens[0];

        // 1. Cerca il comando (verbo) tramite Stream Pipeline [Lezioni/16 - JAVA - Lambda Expressions.pdf]
        Comando comandoRilevato = comandi.stream()
                .filter(c -> c.getSinonimi().contains(primoToken))
                .findFirst()
                .orElse(null);

        // 2. Se il primo token NON è un comando conosciuto, potrebbe essere solo il nome di un oggetto?
        if (comandoRilevato == null) {
            Oggetto objSolo = cercaOggetto(primoToken, oggettiStanza, inventario);
            if (objSolo != null) {
                // Ritorna un output con oggetto riconosciuto ma verbo nullo
                return new ParserOutput(null, objSolo, null, null);
            }
            // Altrimenti, è un comando del tutto sconosciuto (es: "ciao", "aiuo")
            return new ParserOutput(null, null, null, primoToken);
        }

        Oggetto oggettoRilevato = null;
        Oggetto oggettoSecRilevato = null;

        // 3. Cerca oggetti nell'input filtrando le stopwords
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            if (stopwords.contains(token)) {
                continue;
            }

            final String t = token;
            List<Oggetto> trovati = filtraOggetti(oggettiStanza, o -> o.getNome().equalsIgnoreCase(t) || o.getSinonimi().contains(t));
            if (trovati.isEmpty()) {
                trovati = filtraOggetti(inventario, o -> o.getNome().equalsIgnoreCase(t) || o.getSinonimi().contains(t));
            }

            if (!trovati.isEmpty()) {
                if (oggettoRilevato == null) {
                    oggettoRilevato = trovati.get(0);
                } else if (oggettoSecRilevato == null) {
                    oggettoSecRilevato = trovati.get(0);
                }
            }
        }

        return new ParserOutput(comandoRilevato, oggettoRilevato, oggettoSecRilevato);
    }

    private Oggetto cercaOggetto(String token, List<Oggetto> oggettiStanza, List<Oggetto> inventario) {
        Oggetto trovato = oggettiStanza.stream()
                .filter(o -> o.getNome().equalsIgnoreCase(token) || o.getSinonimi().contains(token))
                .findFirst()
                .orElse(null);
        
        if (trovato == null) {
            trovato = inventario.stream()
                    .filter(o -> o.getNome().equalsIgnoreCase(token) || o.getSinonimi().contains(token))
                    .findFirst()
                    .orElse(null);
        }
        return trovato;
    }

    // Algoritmo dinamico di Levenshtein Distance per suggerimenti fuzzy automatici dei typos d'esame [Novità Algoritmica!]
    public String suggerisciComando(String parola, List<Comando> comandi) {
        String miglioreSuggerimento = null;
        int minDistanza = Integer.MAX_VALUE;

        for (Comando cmd : comandi) {
            for (String sinonimo : cmd.getSinonimi()) {
                int dist = calcolaLevenshtein(parola, sinonimo);
                if (dist < minDistanza) {
                    minDistanza = dist;
                    miglioreSuggerimento = sinonimo;
                }
            }
        }

        // Suggeriamo solo se la distanza di modifica è estremamente vicina (1 o 2 edits max)
        if (minDistanza <= 2) {
            return miglioreSuggerimento;
        }
        return null;
    }

    private int calcolaLevenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
