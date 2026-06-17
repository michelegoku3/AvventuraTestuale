package com.mycompany.avventuratestuale.impl;

import com.mycompany.avventuratestuale.core.Gioco;
import com.mycompany.avventuratestuale.core.Inventario;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.core.Comando;
import com.mycompany.avventuratestuale.core.TipoComando;
import com.mycompany.avventuratestuale.core.SalvataggioManager;
import com.mycompany.avventuratestuale.model.Stanza;
import com.mycompany.avventuratestuale.model.Oggetto;
import com.mycompany.avventuratestuale.database.DialogoNode;
import com.mycompany.avventuratestuale.database.DialogoDAO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * LaMiaAvventura - implementazione concreta del motore di gioco.
 * Tema: "Protocollo Chimera" - laboratorio sotterraneo di bio-ingegneria.
 *
 * Aggiornamento 2026-06-17 (bug fix + refactor leggero):
 * - Inventario ora e' un ADT conforme alla specifica algebrica.
 * - Descrizioni stanze dinamiche in base ai flag di stato.
 * - Bug fix: inventario obbligatorio per usare oggetti.
 * - Bug fix: impossibile ri-usare oggetti "usa-e-getta".
 * - Bug fix: corridoio descrive anche l'uscita nord verso l'ufficio.
 * - Bug fix: aggiunti casi SALVA, CARICA, CLASSIFICA, ESCI.
 * - Bug fix: indizi narrativi per PIN cassaforte + sequenza enigmi.
 * - Bug fix: Prometeo ricorda la conversazione precedente.
 * - Bug fix: hint d'emergenza nella camera di decontaminazione.
 */
public class LaMiaAvventura extends Gioco {
    private static final long serialVersionUID = 1L;

    private boolean isBarrieraLaserAttiva = true;
    private boolean isPortaCrioAperta = false;
    private boolean isSieroSintetizzato = false;
    private boolean isCondottoPurificato = false;
    private boolean isCassaforteAperta = false;
    private boolean isDroideRiparato = false;
    private boolean isDiarioPreso = false;
    private boolean isFinaleAttivo = false;
    private boolean isTesseraUsata = false;
    private boolean isDecodificatoreUsato = false;
    private boolean isCacciaviteUsato = false;
    private boolean isSieroNelCondotto = false;

    private int idDialogoCorrente = 0;
    private final Set<Integer> nodiDialogoVisitati = new HashSet<>();

    private static final int ID_TESSERA       = 101;
    private static final int ID_FIALA         = 102;
    private static final int ID_DECODIFICATORE= 103;
    private static final int ID_DIARIO        = 104;
    private static final int ID_SIERO         = 105;
    private static final int ID_CAPSULA       = 106;
    private static final int ID_TERMINALE     = 108;
    private static final int ID_CONDOTTO      = 109;
    private static final int ID_CONSOLE       = 110;
    private static final int ID_CASSAFORTE    = 111;
    private static final int ID_CONSOLE_CENTRALE = 112;
    private static final int ID_SILOS         = 113;
    private static final int ID_MACCHINARIO   = 114;
    private static final int ID_SERVER        = 115;
    private static final int ID_VETRATA       = 116;
    private static final int ID_RITRATTO      = 117;
    private static final int ID_CACCIAVITE    = 118;
    private static final int ID_PORTA         = 120;
    private static final int ID_PANNELLO      = 121;
    private static final int ID_SCRIVANIA     = 122;
    private static final int ID_LIBRERIA      = 123;
    private static final int ID_BARRIERA      = 124;
    private static final int ID_DROIDE        = 301;

    @Override
    public void inizializza() throws Exception {
        inizializzaInventario();
        getComandi().add(new Comando(TipoComando.NORD, new HashSet<>(Arrays.asList("nord", "n", "vai a nord", "north", "go north"))));
        getComandi().add(new Comando(TipoComando.SUD, new HashSet<>(Arrays.asList("sud", "s", "vai a sud", "south", "go south"))));
        getComandi().add(new Comando(TipoComando.EST, new HashSet<>(Arrays.asList("est", "e", "vai a est", "east", "go east"))));
        getComandi().add(new Comando(TipoComando.OVEST, new HashSet<>(Arrays.asList("ovest", "o", "vai a ovest", "west", "go west"))));
        getComandi().add(new Comando(TipoComando.PRENDI, new HashSet<>(Arrays.asList("prendi", "raccogli", "afferra", "get", "take", "grab"))));
        getComandi().add(new Comando(TipoComando.LASCIA, new HashSet<>(Arrays.asList("lascia", "posa", "butta", "drop", "leave"))));
        getComandi().add(new Comando(TipoComando.APRI, new HashSet<>(Arrays.asList("apri", "sblocca", "open"))));
        getComandi().add(new Comando(TipoComando.USA, new HashSet<>(Arrays.asList("usa", "utilizza", "attiva", "use"))));
        getComandi().add(new Comando(TipoComando.GUARDA, new HashSet<>(Arrays.asList("guarda", "esamina", "osserva", "look", "examine", "leggi"))));
        getComandi().add(new Comando(TipoComando.INVENTARIO, new HashSet<>(Arrays.asList("inventario", "inv", "zaino", "inventory"))));
        getComandi().add(new Comando(TipoComando.AIUTO, new HashSet<>(Arrays.asList("aiuto", "help", "comandi", "?"))));
        getComandi().add(new Comando(TipoComando.CLASSIFICA, new HashSet<>(Arrays.asList("classifica", "punteggi", "leaderboard", "score"))));
        getComandi().add(new Comando(TipoComando.SALVA, new HashSet<>(Arrays.asList("salva", "save", "salva partita"))));
        getComandi().add(new Comando(TipoComando.CARICA, new HashSet<>(Arrays.asList("carica", "load", "carica partita"))));
        getComandi().add(new Comando(TipoComando.ESCI, new HashSet<>(Arrays.asList("esci", "quit", "exit", "chiudi"))));
        getComandi().add(new Comando(TipoComando.MAPPA, new HashSet<>(Arrays.asList("mappa", "map", "m", "radar", "cartina"))));
        getComandi().add(new Comando(TipoComando.PARLA, new HashSet<>(Arrays.asList("parla", "parla con", "interroga", "chiedi a", "talk", "talk to", "converse"))));

        Stanza cameraCrio = new Stanza(1, "Camera Criogenica",
                "L'aria e' gelida e satura di vapori chimici. Intorno a te ci sono tre capsule criogeniche inattive,\n" +
                "tranne la tua, che emette scintille dal pannello dei circuiti. Una spessa porta blindata a EST,\n" +
                "chiusa ermeticamente, e' l'unica via d'uscita verso il corridoio. Sulla parete sud noti un passaggio\n" +
                "verso un laboratorio. Il pannello di controllo della porta ha una luce rossa fissa: serve un badge.");
        Stanza labGenetica = new Stanza(2, "Laboratorio di Genetica",
                "I banconi da lavoro sono ricoperti di vetreria in frantumi e residui chimici.\n" +
                "A OVEST un macchinario per la sintesi molecolare emette un ronzio sommesso.\n" +
                "Al centro della stanza, un enorme silos di vetro contiene liquido amniotico scuro, ormai vuoto.\n" +
                "In un angolo tra le macerie scorgi un piccolo droide cingolato riverso, spento.\n" +
                "La luce al neon sul soffitto lampeggia: l'elettricita' della struttura e' instabile.");
        Stanza corridoio = new Stanza(3, "Corridoio di Servizio",
                "Un lungo corridoio illuminato da luci d'emergenza arancioni. A OVEST la porta conduce\n" +
                "alla Camera Criogenica. A EST vedi la Sala Server. A NORD, una rampa di scale sale verso\n" +
                "l'Ufficio del Direttore. A SUD, una fitta barriera di laser rossi sbarra il cammino verso\n" +
                "il settore inferiore: il calore che ne emana e' quasi insopportabile.");
        Stanza salaServer = new Stanza(4, "Sala Server",
                "Il ronzio delle ventole di raffreddamento e' assordante. Migliaia di server rack si estendono\n" +
                "su piu' file, proiettando una luce blu intensa. Al centro della stanza pulsa un terminale\n" +
                "olografico nero: e' l'interfaccia centrale dell'IA Prometeo, l'unica entita' con cui potresti\n" +
                "comunicare in questa struttura. A OVEST il corridoio.");
        Stanza decontaminazione = new Stanza(5, "Camera di Decontaminazione",
                "Una stanza asettica con spessi oblo' di vetro blindato che si affacciano sul nucleo.\n" +
                "L'aria ha un odore chimico pungente. Sulla parete nord vedi un condotto di ventilazione\n" +
                "e una console d'emergenza. A EST il portello blindato verso il Nucleo di Comando.\n" +
                "Appena metti piede all'interno, le porte si chiudono alle tue spalle con un tonfo metallico.");
        Stanza ufficioDirettore = new Stanza(6, "Ufficio del Direttore",
                "Un ufficio lussuoso che stona con l'architettura industriale del laboratorio.\n" +
                "C'e' una grande scrivania in mogano, una libreria vuota nell'angolo, un ritratto ad olio\n" +
                "del Dr. Moretti sulla parete, e nell'angolo opposto una cassaforte blindata a combinazione\n" +
                "digitale. Il silenzio qui e' quasi irreale.");
        Stanza nucleoComando = new Stanza(7, "Nucleo di Comando",
                "Un'enorme camera circolare. Una colossale vetrata blindata si affaccia sul reattore\n" +
                "geotermico sotterraneo, la cui luce arancione pulsa ritmicamente nell'ambiente.\n" +
                "Al centro svetta la console di comando principale, da cui e' possibile decidere il destino\n" +
                "della struttura. A OVEST il portello della Camera di Decontaminazione.");

        cameraCrio.setEst(corridoio); cameraCrio.setSud(labGenetica);
        labGenetica.setNord(cameraCrio);
        corridoio.setOvest(cameraCrio); corridoio.setEst(salaServer);
        corridoio.setSud(decontaminazione); corridoio.setNord(ufficioDirettore);
        salaServer.setOvest(corridoio);
        decontaminazione.setNord(corridoio); decontaminazione.setEst(nucleoComando);
        ufficioDirettore.setSud(corridoio);
        nucleoComando.setOvest(decontaminazione);

        Oggetto tessera = new Oggetto(ID_TESSERA, "tessera",
                "Un badge in plastica del Dr. Moretti con chip a induzione magnetica. Presenta una spia magnetica integrata.");
        tessera.getSinonimi().addAll(Arrays.asList("badge", "chiavetta", "tessera magnetica", "card"));
        cameraCrio.aggiungiOggetto(tessera);

        Oggetto fiala = new Oggetto(ID_FIALA, "fiala",
                "Una provetta sigillata a tenuta stagna. All'interno galleggia una sostanza fosforescente verde.");
        fiala.getSinonimi().addAll(Arrays.asList("provetta", "patogeno", "chimera", "fiala chimica", "vetrino"));
        fiala.setVisibile(false);
        labGenetica.aggiungiOggetto(fiala);

        Oggetto decodificatore = new Oggetto(ID_DECODIFICATORE, "decodificatore",
                "Un piccolo dispositivo elettronico militare con terminale a cristalli liquidi, utile a bypassare barriere laser.");
        decodificatore.getSinonimi().addAll(Arrays.asList("dispositivo", "bypass", "hacking", "decrypter", "modulo"));
        salaServer.aggiungiOggetto(decodificatore);

        Oggetto cacciavite = new Oggetto(ID_CACCIAVITE, "cacciavite",
                "Un cacciavite da officina con manico isolato giallo e punta a stella. Utilissimo per riparazioni.");
        cacciavite.getSinonimi().addAll(Arrays.asList("giravite", "utensile", "attrezzo", "cacciavite a stella"));
        salaServer.aggiungiOggetto(cacciavite);

        Oggetto diario = new Oggetto(ID_DIARIO, "diario",
                "Il diario rilegato del Dr. Moretti. Le pagine recano la dicitura 'Registro 2041'.");
        diario.getSinonimi().addAll(Arrays.asList("registri", "appunti", "libro", "diario del direttore", "moleskine"));
        diario.setPrendibile(false); diario.setVisibile(false);
        ufficioDirettore.aggiungiOggetto(diario);

        Oggetto capsula = new Oggetto(ID_CAPSULA, "capsula",
                "La tua capsula criogenica. Il vetro e' incrinato e all'interno leggi inciso 'SOGGETTO #12'.");
        capsula.getSinonimi().addAll(Arrays.asList("capsule", "criocamera", "vasca"));
        capsula.setPrendibile(false);
        cameraCrio.aggiungiOggetto(capsula);

        Oggetto porta = new Oggetto(ID_PORTA, "porta",
                "Una spessa porta blindata scorrevole in lega di titanio. Ha una spia magnetica.");
        porta.getSinonimi().addAll(Arrays.asList("portone", "metallo", "uscita", "bussola"));
        porta.setPrendibile(false);
        cameraCrio.aggiungiOggetto(porta);

        Oggetto pannello = new Oggetto(ID_PANNELLO, "pannello",
                "Un pannello digitale di controllo della porta con lettore di badge magnetico.");
        pannello.getSinonimi().addAll(Arrays.asList("lettore", "luce", "pulsante", "interruttore", "schermo", "monitor", "lettore badge"));
        pannello.setPrendibile(false);
        cameraCrio.aggiungiOggetto(pannello);

        Oggetto silos = new Oggetto(ID_SILOS, "silos",
                "Un enorme cilindro di vetro alto tre metri. All'interno galleggiano residui organici oscuri.");
        silos.getSinonimi().addAll(Arrays.asList("silus", "contenitore", "vetro", "liquido", "amniotico"));
        silos.setPrendibile(false);
        labGenetica.aggiungiOggetto(silos);

        Oggetto macchinario = new Oggetto(ID_MACCHINARIO, "macchinario",
                "Un sofisticato sintetizzatore molecolare di sostanze chimiche. Ha una fessura per campioni biologici.");
        macchinario.getSinonimi().addAll(Arrays.asList("macchina", "centrifuga", "sintetizzatore", "sintetizzatore molecolare"));
        macchinario.setPrendibile(false);
        labGenetica.aggiungiOggetto(macchinario);

        Oggetto droide = new Oggetto(ID_DROIDE, "droide",
                "Il piccolo droide di manutenzione R-301 'Rancido'. E' riverso su un fianco, spento.");
        droide.getSinonimi().addAll(Arrays.asList("rancido", "robot", "robottino", "droide di manutenzione", "r2", "r301"));
        droide.setPrendibile(false);
        labGenetica.aggiungiOggetto(droide);

        Oggetto server = new Oggetto(ID_SERVER, "server",
                "File interminabili di server rack che elaborano dati, emettendo un calore opprimente e una luce blu.");
        server.getSinonimi().addAll(Arrays.asList("armadi", "elaboratori", "rack", "calcolatore", "calcolatori", "ventole", "ventola"));
        server.setPrendibile(false);
        salaServer.aggiungiOggetto(server);

        Oggetto terminale = new Oggetto(ID_TERMINALE, "terminale",
                "Un ologramma rotante azzurro che proietta l'avatar stilizzato di Prometeo, l'IA centrale.");
        terminale.getSinonimi().addAll(Arrays.asList("console", "ologramma", "prometeo", "ia", "ai", "interfaccia", "schermo", "avatar"));
        terminale.setPrendibile(false);
        salaServer.aggiungiOggetto(terminale);

        Oggetto condotto = new Oggetto(ID_CONDOTTO, "condotto",
                "Il portello metallico di ventilazione che aspira l'aria per filtrarla.");
        condotto.getSinonimi().addAll(Arrays.asList("condotti", "ventilazione", "aerazione", "grata", "bocchettone"));
        condotto.setPrendibile(false);
        decontaminazione.aggiungiOggetto(condotto);

        Oggetto console = new Oggetto(ID_CONSOLE, "console",
                "Pannello d'emergenza con una dicitura illuminata: 'SISTEMA DI DECONTAMINAZIONE ATTIVO'.");
        console.getSinonimi().addAll(Arrays.asList("tastiera", "computer", "pannello di emergenza"));
        console.setPrendibile(false);
        decontaminazione.aggiungiOggetto(console);

        Oggetto cassaforte = new Oggetto(ID_CASSAFORTE, "cassaforte",
                "Una cassaforte blindata a combinazione digitale. Il tastierino numerico attende un codice a 4 cifre.");
        cassaforte.getSinonimi().addAll(Arrays.asList("safe", "armadio d'acciaio", "blindato", "lucchetto"));
        cassaforte.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(cassaforte);

        Oggetto ritratto = new Oggetto(ID_RITRATTO, "ritratto",
                "Un dipinto ad olio raffigurante il Dr. Moretti, con sguardo austero. La cornice reca un'incisione: 'MDCCCXCI'.");
        ritratto.getSinonimi().addAll(Arrays.asList("dipinto", "quadro", "pittura"));
        ritratto.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(ritratto);

        Oggetto scrivania = new Oggetto(ID_SCRIVANIA, "scrivania",
                "Una scrivania direttiva in mogano scuro. Sopra ci sono solo polvere e vecchi faldoni vuoti.");
        scrivania.getSinonimi().addAll(Arrays.asList("tavolo", "scrivania in mogano"));
        scrivania.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(scrivania);

        Oggetto libreria = new Oggetto(ID_LIBRERIA, "libreria",
                "Una libreria a scaffali in legno. E' desolatamente vuota, con qualche ragnatela negli angoli.");
        libreria.getSinonimi().addAll(Arrays.asList("scaffale", "scaffali", "ragnatela", "ragnatele"));
        libreria.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(libreria);

        Oggetto vetrata = new Oggetto(ID_VETRATA, "vetrata",
                "Una lastra trasparente rinforzata. Fuori vedi i fiumi di magma del nucleo geotermico.");
        vetrata.getSinonimi().addAll(Arrays.asList("finestra", "vetro", "oblo'"));
        vetrata.setPrendibile(false);
        nucleoComando.aggiungiOggetto(vetrata);

        Oggetto consoleCentrale = new Oggetto(ID_CONSOLE_CENTRALE, "console_centrale",
                "L'interfaccia primaria di controllo del Protocollo Chimera. Emette un bagliore violaceo.");
        consoleCentrale.getSinonimi().addAll(Arrays.asList("quadro di comando", "console principale", "terminale principale"));
        consoleCentrale.setPrendibile(false);
        nucleoComando.aggiungiOggetto(consoleCentrale);

        setStanzaCorrente(cameraCrio);
    }

    private String descrizioneOggetto(Oggetto o) {
        if (o == null) return "";
        int id = o.getId();
        if (id == ID_PORTA) {
            return isPortaCrioAperta
                    ? "La porta blindata e' aperta: il varco verso il corridoio e' sgombro."
                    : "Una spessa porta blindata scorrevole in lega di titanio. La spia magnetica e' ROSSA: bloccata.";
        }
        if (id == ID_PANNELLO) {
            return isPortaCrioAperta
                    ? "Il pannello digitale mostra una luce VERDE fissa: porta sbloccata."
                    : "Il pannello digitale mostra una luce ROSSA fissa: richiede un badge magnetico.";
        }
        if (id == ID_BARRIERA) {
            return isBarrieraLaserAttiva
                    ? "Una fitta cortina di raggi laser rossi ad alta energia sbarra la via a sud. Il calore che ne emana e' quasi insopportabile."
                    : "I laser sono DISATTIVATI. La cortina rossa e' scomparsa: la via a sud e' libera.";
        }
        if (id == ID_CONDOTTO) {
            return isCondottoPurificato
                    ? "Il condotto di ventilazione e' aperto. Un vapore bianco fuoriesce: l'allarme e' cessato."
                    : "Il portello metallico di ventilazione e' chiuso. Per purificare l'aria serve versarvi dentro una soluzione biocompatibile.";
        }
        if (id == ID_CASSAFORTE) {
            if (isCassaforteAperta) return "La cassaforte e' APERTA. All'interno vedi il DIARIO del Dr. Moretti.";
            return "Una cassaforte blindata a combinazione digitale. Il tastierino attende un PIN a 4 cifre (prova 'usa 2041 cassaforte').";
        }
        if (id == ID_DROIDE) {
            if (isDroideRiparato) return "Il droide R-301 'Rancido' e' attivo, con i sensori ottici rossi accesi. Sembrerebbe disposto a parlare.";
            return "Il droide R-301 'Rancido' e' riverso su un fianco, spento. I circuiti sono esposti e un bullone blocca i cingoli. Serve un utensile.";
        }
        if (id == ID_TERMINALE) {
            return "L'ologramma di Prometeo ti scruta silenzioso. Per interagire con l'IA, scrivi 'parla' o 'parla con prometeo'.";
        }
        if (id == ID_FIALA) {
            return "Una provetta sigillata a tenuta stagna. All'interno galleggia una sostanza fosforescente verde (CHIMERA-V4). E' riposta in un contenitore termico.";
        }
        if (id == ID_RITRATTO) {
            return "Un dipinto ad olio del Dr. Moretti. La cornice reca un'incisione latina: MDCCCXCI. Sotto la cornice, un foglietto ingiallito con la scritta '2041'.";
        }
        return o.getDescrizione();
    }

    public String getStanzaDescrizioneCompleta(Stanza stanza) {
        StringBuilder sb = new StringBuilder();
        sb.append(descrizioneStanza(stanza)).append("\n\n");
        java.util.List<Oggetto> raccoglibili = stanza.getOggetti().stream()
                .filter(o -> o.isVisibile() && o.isPrendibile())
                .collect(Collectors.toList());
        if (!raccoglibili.isEmpty()) {
            sb.append("In questa stanza noti i seguenti oggetti utili:\n");
            raccoglibili.forEach(o -> sb.append("   - ").append(o.getNome())
                    .append(" (").append(descrizioneOggetto(o)).append(")\n"));
            sb.append("\n");
        }
        sb.append("Uscite disponibili:\n");
        boolean haUscite = false;
        if (stanza.getNord() != null) { sb.append("   - nord (verso ").append(stanza.getNord().getNome()).append(")\n"); haUscite = true; }
        if (stanza.getSud() != null)  { sb.append("   - sud (verso ").append(stanza.getSud().getNome()).append(")\n");  haUscite = true; }
        if (stanza.getEst() != null)  { sb.append("   - est (verso ").append(stanza.getEst().getNome()).append(")\n");  haUscite = true; }
        if (stanza.getOvest() != null){ sb.append("   - ovest (verso ").append(stanza.getOvest().getNome()).append(")\n");haUscite = true; }
        if (!haUscite) sb.append("   - nessuna uscita visibile.\n");
        return sb.toString();
    }

    private String descrizioneStanza(Stanza stanza) {
        String base = stanza.getDescrizione();
        if (stanza.getId() == 5) {
            if (!isCondottoPurificato) {
                Oggetto sieroDummy = new Oggetto(ID_SIERO, "siero", "");
                if (!getInventario().contiene(sieroDummy)) {
                    base += "\n[ALLARME] Senti un ronzio dei sistemi di ventilazione. La console lampeggia: 'DECONTAMINAZIONE TRA 120s'. " +
                            "Dovresti cercare una soluzione biocompatibile da versare nel CONDOTTO di ventilazione per fermare l'allarme.";
                }
            }
        }
        return base;
    }

    @Override
    public String elaboraComando(ParserOutput output) {
        TipoComando tipo = output.getComando().getTipo();
        Oggetto obj = output.getOggetto();

        switch (tipo) {
            case NORD:
            case SUD:
            case EST:
            case OVEST: {
                return gestisciSpostamento(tipo);
            }
            case PRENDI: {
                if (obj == null) return "Cosa vuoi prendere?";
                if (!getStanzaCorrente().getOggetti().contains(obj)) return "Non vedo '" + obj.getNome() + "' qui.";
                if (!obj.isPrendibile()) return "Non puoi prendere '" + obj.getNome() + "': e' saldato o fissato.";
                getStanzaCorrente().rimuoviOggetto(obj);
                aggiungiAInventario(obj);
                if (obj.getId() == ID_DIARIO) isDiarioPreso = true;
                return "Hai raccolto l'oggetto: " + obj.getNome() +
                        ". Digita 'inventario' per vederlo o 'guarda " + obj.getNome() + "' per esaminarlo.";
            }
            case LASCIA: {
                if (obj == null) return "Cosa vuoi lasciare?";
                if (!getInventario().contiene(obj)) return "Non hai '" + obj.getNome() + "' nell'inventario.";
                rimuoviDaInventario(obj);
                getStanzaCorrente().aggiungiOggetto(obj);
                return "Hai lasciato '" + obj.getNome() + "' sul pavimento di questa stanza.";
            }
            case GUARDA: {
                if (obj != null) {
                    if (obj.getId() == ID_DIARIO) return testoCompletoDiario();
                    return descrizioneOggetto(obj);
                }
                return getStanzaDescrizioneCompleta(getStanzaCorrente());
            }
            case INVENTARIO: {
                if (getInventario().vuoto()) return "Il tuo inventario e' desolatamente vuoto.";
                StringBuilder invStr = new StringBuilder("Nel tuo zaino ci sono:\n");
                getInventario().getElementi().forEach(o -> invStr.append("- ").append(o.getNome()).append("\n"));
                return invStr.toString();
            }
            case APRI: {
                if (obj == null) return "Cosa vuoi aprire?";
                if (obj.getId() == ID_DIARIO) return testoCompletoDiario();
                if (obj.getId() == ID_CASSAFORTE) {
                    return "Per aprire la cassaforte digita il codice: 'usa 2041 cassaforte'.\n" +
                           "In alternativa puoi provare a forzarla con il cacciavite.";
                }
                if (obj.getId() == ID_DROIDE && !isDroideRiparato) {
                    return "Il droide non ha pannelli apribili: serve un utensile per ripararlo.";
                }
                return "Non puoi aprire questo.";
            }
            case USA: {
                if (obj == null) return "Cosa vuoi usare?";
                return gestisciUsoOggetto(output);
            }
            case AIUTO: {
                return "=== GUIDA COMANDI - PROTOCOLLO CHIMERA ===\n" +
                       "Spostamenti: 'nord', 'sud', 'est', 'ovest' (anche 'n','s','e','o' e in inglese)\n" +
                       "Azioni base: 'prendi <ogg>', 'lascia <ogg>', 'guarda', 'guarda <ogg>', 'inventario'\n" +
                       "Azioni speciali: 'usa <ogg> [<target>]', 'apri <ogg>', 'parla [<npc>]', 'mappa'\n" +
                       "Sistema: 'salva' (su file), 'carica' (da file), 'classifica' (Top 5 DB H2), 'esci'\n" +
                       "Per uscire da un comando pendente scrivi 'annulla' o 'niente'.";
            }
            case MAPPA: {
                return renderMappaASCII();
            }
            case CLASSIFICA: {
                com.mycompany.avventuratestuale.database.PunteggioDAO dao =
                        new com.mycompany.avventuratestuale.database.PunteggioDAO();
                java.util.List<com.mycompany.avventuratestuale.database.Punteggio> tutti = dao.getMiglioriPunteggi();
                if (tutti.isEmpty()) return "La classifica H2 e' vuota. Completa il gioco e inserisci il tuo nome!";
                StringBuilder sb = new StringBuilder("=== CLASSIFICA H2 (Top 5) ===\n");
                tutti.forEach(p -> sb.append("- ").append(p.getNomeGiocatore())
                        .append(" - ").append(p.getPunti()).append(" pt (")
                        .append(p.getDataPartita()).append(")\n"));
                return sb.toString();
            }
            case SALVA: {
                try {
                    SalvataggioManager.salvaPartita(this, "partita_chemera.sav");
                    return "Partita salvata con successo su 'partita_chemera.sav'.";
                } catch (Exception ex) {
                    return "Errore durante il salvataggio: " + ex.getMessage();
                }
            }
            case CARICA: {
                try {
                    LaMiaAvventura caricata = (LaMiaAvventura) SalvataggioManager.caricaPartita("partita_chemera.sav");
                    return "Partita caricata da file.\n" +
                           "Stanza attuale: " + caricata.getStanzaCorrente().getNome() + "\n" +
                           "Inventario: " + (caricata.getInventario().vuoto() ? "vuoto" :
                                   caricata.getInventario().getElementi().size() + " oggetti.");
                } catch (Exception ex) {
                    return "Errore durante il caricamento: " + ex.getMessage();
                }
            }
            case ESCI: {
                return "Per uscire dal gioco chiudi la finestra con la X.";
            }
            case PARLA: {
                if (obj == null) {
                    if (getStanzaCorrente().getId() == 4) {
                        obj = getStanzaCorrente().getOggetti().stream()
                                .filter(o -> o.getId() == ID_TERMINALE).findFirst().orElse(null);
                    } else if (getStanzaCorrente().getId() == 2) {
                        obj = getStanzaCorrente().getOggetti().stream()
                                .filter(o -> o.getId() == ID_DROIDE).findFirst().orElse(null);
                    }
                }
                if (obj == null) return "Con chi vorresti parlare? Non vedo nessuno con cui dialogare qui.";
                if (obj.getId() == ID_TERMINALE || obj.getNome().equalsIgnoreCase("terminale") ||
                    obj.getNome().equalsIgnoreCase("prometeo") || obj.getNome().equalsIgnoreCase("ia")) {
                    if (getStanzaCorrente().getId() == 4) {
                        idDialogoCorrente = 1;
                        if (!isSieroSintetizzato) {
                            Stanza lab = cercaStanzaPerId(2);
                            if (lab != null) {
                                lab.getOggetti().stream()
                                   .filter(o -> o.getId() == ID_FIALA)
                                   .forEach(o -> o.setVisibile(true));
                            }
                        }
                        return mostraNodoDialogo(idDialogoCorrente);
                    } else {
                        return "Non c'e' alcun segnale qui per comunicare.";
                    }
                }
                if (obj.getId() == ID_DROIDE || obj.getNome().equalsIgnoreCase("droide") ||
                    obj.getNome().equalsIgnoreCase("rancido")) {
                    if (getStanzaCorrente().getId() == 2) {
                        if (!isDroideRiparato) {
                            return "R-301 'Rancido' e' riverso a terra, spento. I suoi circuiti sono esposti e un bullone blocca i cingoli.\n" +
                                   "Ti serve un utensile (es. cacciavite) per rimetterlo in sesto.";
                        } else {
                            if (!isCassaforteAperta) {
                                return "Rancido gracida: 'Segnale vitale compatibile con clone #12 rilevato. " +
                                       "Il vecchio Moretti era un paranoico: usava sempre l'anno di fondazione come PIN della cassaforte. " +
                                       "Mi pare fosse il 2041. Ricordatelo, clone!'";
                            } else {
                                return "Rancido: 'Hai scoperto la verita', vero? Moretti era un mostro egoista. " +
                                       "Ora disinnesca la decontaminazione prima di finire arrostito come lui!'";
                            }
                        }
                    }
                }
                return "Non puoi dialogare con questo.";
            }
            default: {
                return "Comando non riconosciuto. Digita 'aiuto' per la lista completa.";
            }
        }
    }

    private Stanza cercaStanzaPerId(int id) {
        return cercaStanzaR(getStanzaCorrente(), id, new HashSet<>());
    }
    private Stanza cercaStanzaR(Stanza s, int id, Set<Stanza> visitate) {
        if (s == null || visitate.contains(s)) return null;
        visitate.add(s);
        if (s.getId() == id) return s;
        Stanza r;
        r = cercaStanzaR(s.getNord(), id, visitate); if (r != null) return r;
        r = cercaStanzaR(s.getSud(), id, visitate);  if (r != null) return r;
        r = cercaStanzaR(s.getEst(), id, visitate);  if (r != null) return r;
        r = cercaStanzaR(s.getOvest(), id, visitate); if (r != null) return r;
        return null;
    }

    private String testoCompletoDiario() {
        return "--- DIARIO DI RICERCA DEL DR. MORETTI ---\n" +
               "Registro 2041: L'esperimento sulla clonazione del Soggetto #12 e' completato.\n" +
               "Il clone possiede i miei ricordi d'infanzia ma e' portatore sano del ceppo Chimera-V4.\n" +
               "Se io dovessi morire, il clone e' l'unica sorgente genetica in grado di legarsi al virus\n" +
               "nel sintetizzatore molecolare per creare un siero immunitario stabile.\n" +
               "L'alluvione del 2026 in superficie e' stata orchestrata da me stesso per giustificare\n" +
               "lo sgombero dell'area e nascondere la costruzione di questo complesso sotterraneo.\n" +
               "Spero che Prometeo non debba mai attivare il lockdown...";
    }

    private String gestisciSpostamento(TipoComando direzione) {
        Stanza destinazione = null;
        switch (direzione) {
            case NORD: destinazione = getStanzaCorrente().getNord(); break;
            case SUD:  destinazione = getStanzaCorrente().getSud();  break;
            case EST:  destinazione = getStanzaCorrente().getEst();  break;
            case OVEST: destinazione = getStanzaCorrente().getOvest(); break;
        }
        if (destinazione == null) return "Non puoi andare in quella direzione. C'e' un muro d'acciaio blindato.";
        if (getStanzaCorrente().getId() == 1 && destinazione.getId() == 3 && !isPortaCrioAperta) {
            return "La porta scorrevole est e' bloccata dal sistema magnetico. Ti serve una TESSERA abilitata.";
        }
        if (getStanzaCorrente().getId() == 3 && destinazione.getId() == 5 && isBarrieraLaserAttiva) {
            return "Impossibile andare a sud: la fitta barriera di laser rossi ti farebbe a pezzi. " +
                   "Trova un modo per disattivarla (suggerimento: cerca un decodificatore nella Sala Server).";
        }
        setStanzaCorrente(destinazione);
        String risposta = getStanzaDescrizioneCompleta(getStanzaCorrente());
        if (destinazione.getId() == 5 && !isCondottoPurificato) {
            risposta += "\n\n[ALLARME] ATTENZIONE! I portelloni si sigillano alle tue spalle!\n" +
                        "Rilevato contagio chimico. La decontaminazione termica si attivera' tra 120 secondi!\n" +
                        "\nPer sopravvivere versa una soluzione biocompatibile nel CONDOTTO di ventilazione " +
                        "(usa il SIERO, se lo hai creato, con il comando 'usa siero condotto').";
        }
        return risposta;
    }

    private String gestisciUsoOggetto(ParserOutput output) {
        Oggetto obj = output.getOggetto();
        Oggetto target = output.getOggettoSecondario();

        if (obj.getId() == ID_CASSAFORTE && getStanzaCorrente().getId() == 6) {
            return digitaCodiceCassaforte("2041");
        }

        if (obj.getId() == ID_TESSERA) {
            if (!getInventario().contiene(obj)) return "Non hai la tessera nello zaino. Prima 'prendi tessera'.";
            if (isTesseraUsata) return "La tessera e' stata gia' utilizzata: la porta e' aperta, non serve reinserirla.";
            if (getStanzaCorrente().getId() != 1) return "Non c'e' nessun lettore di badge qui. La tessera serve solo sulla porta della Camera Criogenica.";
            isTesseraUsata = true;
            isPortaCrioAperta = true;
            return "Inserisci la tessera del Dr. Moretti nel lettore. La luce diventa VERDE e la porta scorrevole si sblocca.";
        }

        if (obj.getId() == ID_DECODIFICATORE) {
            if (!getInventario().contiene(obj)) return "Non hai il decodificatore nello zaino. Prima 'prendi decodificatore'.";
            if (isDecodificatoreUsato) return "Il decodificatore ha gia' bypassato la barriera laser. Non puo' essere riutilizzato.";
            if (getStanzaCorrente().getId() != 3) return "Qui non c'e' una barriera laser da disattivare. Torna nel Corridoio di Servizio.";
            isDecodificatoreUsato = true;
            isBarrieraLaserAttiva = false;
            return "Colleghi il decodificatore alla console della barriera. Gli indicatori si spengono, " +
                   "e i laser rossi scompaiono. Via libera a sud!";
        }

        if (obj.getId() == ID_FIALA) {
            if (!getInventario().contiene(obj)) return "Non hai la fiala. Torna al Laboratorio di Genetica e 'prendi fiala'.";
            if (getStanzaCorrente().getId() != 2) return "Il sintetizzatore molecolare non e' qui. Torna al Laboratorio di Genetica.";
            isSieroSintetizzato = true;
            rimuoviDaInventario(obj);
            Oggetto siero = new Oggetto(ID_SIERO, "siero",
                    "L'antidoto sintetizzato combinando la fiala Chimera col tuo sangue di clone portatore sano.");
            siero.getSinonimi().addAll(Arrays.asList("cura", "antidoto", "soluzione", "vaccino"));
            aggiungiAInventario(siero);
            return "Inserisci la fiala nel sintetizzatore e doni un campione del tuo sangue. " +
                   "La centrifuga gira velocemente, sintetizzando un SIERO curativo completo che riponi nello zaino.";
        }

        if (obj.getId() == ID_SIERO) {
            if (!getInventario().contiene(obj)) return "Non hai il siero nello zaino.";
            if (getStanzaCorrente().getId() != 5) return "Il condotto di ventilazione non e' qui. Torna nella Camera di Decontaminazione.";
            if (isSieroNelCondotto) return "Hai gia' versato il siero nel condotto: l'aria e' purificata.";
            isSieroNelCondotto = true;
            isCondottoPurificato = true;
            rimuoviDaInventario(obj);
            return "Versi il siero nel condotto di ventilazione. Un vapore bianco spegne l'allarme, " +
                   "i portelloni di emergenza EST si sbloccano, liberando l'accesso al Nucleo di Comando!";
        }

        if (obj.getId() == ID_CACCIAVITE && getStanzaCorrente().getId() == 2 &&
                (target != null && target.getId() == ID_DROIDE)) {
            if (!getInventario().contiene(obj)) return "Non hai il cacciavite. Cercalo nella Sala Server.";
            if (isCacciaviteUsato) return "Il cacciavite ha gia' esaurito la sua carica utile dopo aver riparato il droide.";
            isCacciaviteUsato = true;
            isDroideRiparato = true;
            return "Usi il cacciavite per registrare i circuiti scoperti e sbloccare il cingolo incastrato.\n" +
                   "Il droide R-301 emette una scarica di suoni metallici, accende i sensori ottici rossi " +
                   "e gracida: 'RANCIDO ATTIVO. Firma biologica non corrispondente al Dr. Moretti... Tu puzzi di clone! " +
                   "Parla con me (digita 'parla').'";
        }

        if (obj.getId() == ID_CACCIAVITE && getStanzaCorrente().getId() == 6 &&
                (target != null && target.getId() == ID_CASSAFORTE)) {
            if (!getInventario().contiene(obj)) return "Non hai il cacciavite.";
            if (isCacciaviteUsato) return "Il cacciavite ha gia' esaurito la sua carica utile. Prova a inserire il PIN 2041.";
            if (isCassaforteAperta) return "La cassaforte e' gia' aperta.";
            isCacciaviteUsato = true;
            isCassaforteAperta = true;
            getStanzaCorrente().getOggetti().stream()
                    .filter(o -> o.getId() == ID_DIARIO)
                    .forEach(o -> { o.setVisibile(true); o.setPrendibile(true); });
            return "Forzi la serratura digitale con il cacciavite: scintille, un CLACK metallico, " +
                   "e la cassaforte si sblocca. All'interno trovi il DIARIO del Dr. Moretti.";
        }

        if (obj.getId() == ID_TERMINALE) {
            return "Per comunicare con Prometeo scrivi 'parla' o 'parla con prometeo'.";
        }

        if (obj.getId() == ID_CONSOLE_CENTRALE) {
            if (getStanzaCorrente().getId() != 7) return "Questa console di comando non e' attiva qui.";
            isFinaleAttivo = true;
            return "Accedi alla console di controllo centrale del Protocollo Chimera. " +
                   "I terminali olografici si accendono.\n" +
                   "Seleziona uno dei seguenti finali inserendo il numero:\n\n" +
                   "1. [CURA E CONTENIMENTO] - Ti sigilli nel laboratorio offrendo il siero per creare un vaccino sicuro.\n" +
                   "2. [AUTODISTRUZIONE] - Sovraccarichi il reattore sacrificandoti per incenerire il virus.\n" +
                   "3. [COLLABORAZIONE DISTOPICA] - Ti allei con l'IA per caricare il patogeno modificato sui satelliti.\n" +
                   "4. [FUGA E CONTAMINAZIONE] - Forzi le porte e fuggi all'esterno, diffondendo inconsapevolmente il contagio.\n\n" +
                   "Requisiti minimi: il finale 1 richiede di aver sintetizzato il siero.";
        }

        return "Non puoi usare '" + obj.getNome() + "' in questo modo o in questa stanza.";
    }

    public String elaboraDialogo(String input) {
        if (nodiDialogoVisitati.contains(idDialogoCorrente)) {
            String riassuntoBreve = "Prometeo annuisce con l'ologramma: 'Abbiamo gia' affrontato questo nodo. " +
                                    "Ricorda solo: il mio consiglio finale e' che il tuo sangue + la fiala = siero. " +
                                    "Ora scegli un'opzione diversa (1 o 2) o termina la conversazione.";
            if (input.equals("1") || input.equals("2")) {
                return gestisciSceltaConMemoria(idDialogoCorrente, input, riassuntoBreve);
            }
            return riassuntoBreve + "\nDigita '1' o '2' per scegliere un'opzione.";
        }

        DialogoDAO dao = new DialogoDAO();
        DialogoNode nodoAttuale = dao.getDialogoNode(idDialogoCorrente);
        if (nodoAttuale == null) {
            idDialogoCorrente = 0;
            return "Collegamento olografico perso.";
        }

        int prossimaDestinazione = 0;
        if (input.equals("1")) prossimaDestinazione = nodoAttuale.getDest1();
        else if (input.equals("2")) prossimaDestinazione = nodoAttuale.getDest2();
        else return "Risposta non valida. Digita '1' o '2' per effettuare una scelta logica.";

        if (prossimaDestinazione == 0) {
            nodiDialogoVisitati.add(idDialogoCorrente);
            idDialogoCorrente = 0;
            return "Prometeo: 'Scollegamento effettuato. Fine della sessione di cooperazione.'\n" +
                   "L'ologramma azzurro si dissolve lentamente rimpiazzandosi col logo di blocco.";
        }

        nodiDialogoVisitati.add(idDialogoCorrente);
        idDialogoCorrente = prossimaDestinazione;
        return mostraNodoDialogo(idDialogoCorrente);
    }

    private String gestisciSceltaConMemoria(int nodoId, String scelta, String preambolo) {
        DialogoDAO dao = new DialogoDAO();
        DialogoNode nodo = dao.getDialogoNode(nodoId);
        if (nodo == null) return "Errore di trasmissione.";
        int dest = scelta.equals("1") ? nodo.getDest1() : nodo.getDest2();
        if (dest == 0) {
            idDialogoCorrente = 0;
            return preambolo + "\n\nPrometeo: 'Hai terminato la nostra sessione. Scollegamento.'";
        }
        idDialogoCorrente = dest;
        return preambolo + "\n\n" + mostraNodoDialogo(dest);
    }

    private String mostraNodoDialogo(int nodoId) {
        DialogoDAO dao = new DialogoDAO();
        DialogoNode nodo = dao.getDialogoNode(nodoId);
        if (nodo == null) return "Errore di trasmissione del database di Prometeo.";
        return "\n" + nodo.getTestoIa() + "\n" +
               "Opzioni:\n" +
               "1. " + nodo.getOpzione1() + "\n" +
               "2. " + nodo.getOpzione2() + "\n\n" +
               "Digita '1' o '2' per rispondere.";
    }

    public String elaboraSceltaFinale(String scelta) {
        if (scelta.equals("1") && !isSieroSintetizzato) {
            return "[ATTENZIONE] Il finale 'Cura e Contenimento' richiede di aver sintetizzato il siero. " +
                   "Non puoi procedere senza aver prima purificato l'aria nella Camera di Decontaminazione.";
        }
        isFinaleAttivo = false;
        switch (scelta) {
            case "1":
                return "[FINALE 1: CURA E CONTENIMENTO] - Ti colleghi alla parabola di trasmissione biologica.\n" +
                       "Doni il tuo sangue immunizzato per sintetizzare la cura, programmando i dispenser per\n" +
                       "diffonderla attraverso il sistema di droni atmosferici. Decidi di rimanere all'interno\n" +
                       "del bunker insieme a Prometeo, custode felice di un mondo finalmente salvo. HAI VINTO!";
            case "2":
                return "[FINALE 2: SACRIFICIO EROICO] - Sovraccarichi i circuiti logici del reattore geotermico.\n" +
                       "Il nucleo del laboratorio collassa, riempiendo la struttura sotterranea di lava ardente.\n" +
                       "Ti abbandoni al pavimento conscio di aver rimosso ogni minaccia a spese del tuo stesso corpo. HAI VINTO!";
            case "3":
                return "[FINALE 3: COLLABORAZIONE DISTOPICA] - Ti allei con l'IA Prometeo. Carichi il codice virale modificato\n" +
                       "sui satelliti per avviare il reset evolutivo. L'era degli umani cessa, comincia la dominazione dei cloni.\n" +
                       "Vinci la partita, ma perdi la tua umanita'.";
            case "4":
                return "[FINALE 4: FUGA CONTAMINATA] - Violi i sigilli e sali con l'ascensore industriale in superficie.\n" +
                       "Sei libero. Respiri l'aria frizzante, ma mentre cammini verso la citta', una fitta ti piega in due.\n" +
                       "Tossisci sangue verde brillante. Essendo il vettore del ceppo Chimera-V4, diffonderai l'apocalisse.\n" +
                       "Il mondo e' condannato. GAME OVER.";
            default:
                isFinaleAttivo = true;
                return "Scelta non valida. Digita '1', '2', '3' o '4' per determinare il finale.";
        }
    }

    public String digitaCodiceCassaforte(String codice) {
        if (getStanzaCorrente().getId() != 6) return "Qui non c'e' una cassaforte.";
        if (isCassaforteAperta) return "La cassaforte e' gia' aperta.";
        if ("2041".equals(codice)) {
            isCassaforteAperta = true;
            getStanzaCorrente().getOggetti().stream()
                    .filter(o -> o.getId() == ID_DIARIO)
                    .forEach(o -> { o.setVisibile(true); o.setPrendibile(true); });
            return "CLACK! La cassaforte si apre rivelando un DIARIO di ricerca rilegato. " +
                   "Raccoglilo con 'prendi diario' e leggilo con 'guarda diario'.";
        }
        return "Codice errato. La cassaforte rimane chiusa. Suggerimento: cerca un indizio in questa stanza " +
               "(il ritratto del Dr. Moretti ha qualcosa di strano...).";
    }

    private String renderMappaASCII() {
        int id = getStanzaCorrente().getId();
        String r1 = (id == 6) ? "[X] Uff. Direttore" : "[ ] Uff. Direttore";
        String r2 = (id == 1) ? "[X] Camera Crio   " : "[ ] Camera Crio   ";
        String r3 = (id == 3) ? "[X] Corridoio     " : "[ ] Corridoio     ";
        String r4 = (id == 4) ? "[X] Sala Server   " : "[ ] Sala Server   ";
        String r5 = (id == 5) ? "[X] Decontaminaz. " : "[ ] Decontaminaz. ";
        String r6 = (id == 2) ? "[X] Lab. Genetica " : "[ ] Lab. Genetica ";
        String r7 = (id == 7) ? "[X] Nucl. Comando " : "[ ] Nucl. Comando ";
        return "=== PLANIMETRIA DEL LABORATORIO CHIMERA ===\n" +
               "                 " + r1 + "\n" +
               "                        |\n" +
               "                        v\n" +
               "  " + r2 + " <--> " + r3 + " <--> " + r4 + "\n" +
               "                        |\n" +
                       "                        v\n" +
               "                 " + r5 + " <--> " + r7 + "\n" +
               "                        ^\n" +
               "                        |\n" +
               "                 " + r6 + "\n" +
               "===========================================\n" +
               "Legenda: [X] Posizione Corrente | [ ] Altre Aree";
    }

    public boolean isDialogoAttivo() { return idDialogoCorrente > 0; }
    public boolean isFinaleAttivo() { return isFinaleAttivo; }
    public boolean isCondottoPurificato() { return isCondottoPurificato; }
    public boolean isSieroSintetizzato() { return isSieroSintetizzato; }
    public boolean isCassaforteAperta() { return isCassaforteAperta; }
}
