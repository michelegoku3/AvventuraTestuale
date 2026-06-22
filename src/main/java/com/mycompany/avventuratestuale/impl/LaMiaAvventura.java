package com.mycompany.avventuratestuale.impl;

import com.mycompany.avventuratestuale.core.*;
import com.mycompany.avventuratestuale.core.commands.*;
import com.mycompany.avventuratestuale.model.Stanza;
import com.mycompany.avventuratestuale.model.Oggetto;
import com.mycompany.avventuratestuale.database.DialogoNode;
import com.mycompany.avventuratestuale.database.DialogoDAO;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Implementazione concreta di Protocollo Chimera, con mappa, enigmi e flag narrativi.
 */
public class LaMiaAvventura extends Gioco {
    private static final long serialVersionUID = 1L;


    private transient Map<TipoComando, Command> commandMap = new HashMap<>();

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


    private boolean isTimerDecontaminazioneAttivo = false;
    private int secondiDecontaminazioneRimanenti = 120;
    private int tempoImpiegatoDecontaminazione = -1;

    private int idDialogoCorrente = 0;
    private int idDialogoRancidoCorrente = 0;
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
    private static final int ID_BIGLIETTO     = 125;
    private static final int ID_CAPSULE_SPENTE = 126;
    private static final int ID_TELECAMERE    = 127;
    private static final int ID_SEGNALETICA   = 128;
    private static final int ID_LOG_SISTEMA   = 129;
    private static final int ID_LAVAGNA       = 130;
    private static final int ID_FASCICOLI     = 131;
    private static final int ID_OBLO          = 132;
    private static final int ID_REATTORE      = 133;
    private static final int ID_ASCENSORE     = 134;
    private static final int ID_UGELLI        = 135;
    private static final int ID_DROIDE        = 301;

    @Override
    /**
     * Crea comandi, stanze, collegamenti, oggetti e stato iniziale dell'avventura.
     */
    public void inizializza() throws Exception {
        setupCommands();
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


        setupCommands();


        Stanza cameraCrio = new Stanza(1, "Camera Criogenica",
                "L'aria e' gelida e satura di vapori chimici. Intorno a te ci sono tre capsule criogeniche inattive, tranne la tua, che emette scintille dal pannello dei circuiti. Una spessa porta blindata a EST e' l'unica via d'uscita verso il corridoio. Sulla parete sud noti un passaggio verso un laboratorio. Il pannello di controllo della porta ha una luce rossa fissa: serve un badge.");
        Stanza labGenetica = new Stanza(2, "Laboratorio di Genetica",
                "I banconi da lavoro sono ricoperti di vetreria in frantumi e residui chimici. A OVEST un macchinario per la sintesi molecolare emette un ronzio sommesso. Al centro della stanza, un enorme silos di vetro contiene liquido amniotico scuro, ormai vuoto. In un angolo tra le macerie scorgi un piccolo droide cingolato riverso, spento. La luce al neon sul soffitto lampeggia: l'elettricita' della struttura e' instabile.");
        Stanza corridoio = new Stanza(3, "Corridoio di Servizio",
                "Un lungo corridoio illuminato da luci d'emergenza arancioni. A OVEST la porta conduce alla Camera Criogenica. A EST vedi la Sala Server. A NORD, una rampa di scale sale verso l'Ufficio del Direttore. A SUD, una fitta barriera di laser rossi sbarra il cammino verso il settore inferiore: il calore che ne emana e' quasi insopportabile.");
        Stanza salaServer = new Stanza(4, "Sala Server",
                "Il ronzio delle ventole di raffreddamento e' assordante. Migliaia di server rack si estendono su piu' file, proiettando una luce blu intensa. Al centro della stanza pulsa un terminale olografico nero: e' l'interfaccia centrale dell'IA Prometeo, l'unica entita' con cui potresti comunicare in questa struttura. A OVEST il corridoio.");
        Stanza decontaminazione = new Stanza(5, "Camera di Decontaminazione",
                "Una stanza asettica con spessi oblo' di vetro blindato che si affacciano sul nucleo. L'aria ha un odore chimico pungente: la camera e' danneggiata e dalle ventole di aerazione esce un gas contaminato da Chimera-V4. Sulla parete nord vedi un condotto di ventilazione e una console d'emergenza. A EST il portello blindato verso il Nucleo di Comando.");
        Stanza ufficioDirettore = new Stanza(6, "Ufficio del Direttore",
                "Un ufficio lussuoso che stona con l'architettura industriale del laboratorio. C'e' una grande scrivania in mogano, una libreria vuota nell'angolo, un ritratto ad olio del Dr. Moretti sulla parete, e nell'angolo opposto una cassaforte blindata a combinazione digitale. Il silenzio qui e' quasi irreale.");
        Stanza nucleoComando = new Stanza(7, "Nucleo di Comando",
                "Un'enorme camera circolare. Una colossale vetrata blindata si affaccia sul reattore geotermico sotterraneo, la cui luce arancione pulsa ritmicamente nell'ambiente. Al centro svetta la console di comando principale: per decidere il destino della struttura devi interagire con essa scrivendo 'usa console'. A OVEST il portello della Camera di Decontaminazione.");

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
        fiala.getSinonimi().addAll(Arrays.asList("provetta", "patogeno", "chimera", "fiala chimica", "vetrino", "campione"));

        fiala.setVisibile(false);
        fiala.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(fiala);

        Oggetto decodificatore = new Oggetto(ID_DECODIFICATORE, "decodificatore",
                "Un piccolo dispositivo elettronico militare con terminale a cristalli liquidi, utile a bypassare barriere laser.");
        decodificatore.getSinonimi().addAll(Arrays.asList("dispositivo", "bypass", "hacking", "decrypter", "modulo", "modulo bypass"));

        decodificatore.setVisibile(false);
        decodificatore.setPrendibile(false);
        labGenetica.aggiungiOggetto(decodificatore);

        Oggetto cacciavite = new Oggetto(ID_CACCIAVITE, "cacciavite",
                "Un cacciavite da officina con manico isolato giallo e punta a stella. Utilissimo per riparazioni.");
        cacciavite.getSinonimi().addAll(Arrays.asList("giravite", "utensile", "attrezzo", "cacciavite a stella"));
        salaServer.aggiungiOggetto(cacciavite);

        Oggetto biglietto = new Oggetto(ID_BIGLIETTO, "biglietto",
                "Una nota piegata e macchiata di polvere, lasciata accanto alla console centrale.");
        biglietto.getSinonimi().addAll(Arrays.asList("appunto", "nota", "foglietto", "messaggio", "nota finale"));
        biglietto.setPrendibile(false);
        nucleoComando.aggiungiOggetto(biglietto);

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
        consoleCentrale.getSinonimi().addAll(Arrays.asList("quadro di comando", "console principale", "terminale principale", "console centrale", "console"));
        consoleCentrale.setPrendibile(false);
        nucleoComando.aggiungiOggetto(consoleCentrale);


        Oggetto capsuleSpente = new Oggetto(ID_CAPSULE_SPENTE, "capsule_spente",
                "Le capsule secondarie sono aperte, vuote o incrinate dall'interno.");
        capsuleSpente.getSinonimi().addAll(Arrays.asList("capsule spente", "altre capsule", "capsule vuote", "soggetto 11", "soggetti"));
        capsuleSpente.setPrendibile(false);
        cameraCrio.aggiungiOggetto(capsuleSpente);

        Oggetto barriera = new Oggetto(ID_BARRIERA, "barriera",
                "Una barriera di laser rossi ad alta energia blocca il settore inferiore.");
        barriera.getSinonimi().addAll(Arrays.asList("laser", "barriera laser", "raggi", "sicurezza"));
        barriera.setPrendibile(false);
        corridoio.aggiungiOggetto(barriera);

        Oggetto telecamere = new Oggetto(ID_TELECAMERE, "telecamere",
                "Vecchie telecamere motorizzate seguono ogni movimento nel corridoio.");
        telecamere.getSinonimi().addAll(Arrays.asList("telecamera", "occhi", "sorveglianza", "camera"));
        telecamere.setPrendibile(false);
        corridoio.aggiungiOggetto(telecamere);

        Oggetto segnaletica = new Oggetto(ID_SEGNALETICA, "segnaletica",
                "Cartelli consumati indicano le sezioni del complesso.");
        segnaletica.getSinonimi().addAll(Arrays.asList("cartelli", "cartello", "frecce", "indicazioni", "segnali"));
        segnaletica.setPrendibile(false);
        corridoio.aggiungiOggetto(segnaletica);

        Oggetto logSistema = new Oggetto(ID_LOG_SISTEMA, "log",
                "Un registro di sistema lampeggia su uno schermo secondario.");
        logSistema.getSinonimi().addAll(Arrays.asList("log sistema", "registro", "schermo secondario", "file", "dati"));
        logSistema.setPrendibile(false);
        salaServer.aggiungiOggetto(logSistema);

        Oggetto lavagna = new Oggetto(ID_LAVAGNA, "lavagna",
                "Una lavagna chimica mostra formule cancellate e frecce tra campioni biologici.");
        lavagna.getSinonimi().addAll(Arrays.asList("formula", "formule", "appunti", "schema", "lavagna chimica"));
        lavagna.setPrendibile(false);
        labGenetica.aggiungiOggetto(lavagna);

        Oggetto fascicoli = new Oggetto(ID_FASCICOLI, "fascicoli",
                "Fascicoli clinici ordinati con precisione ossessiva riempiono un cassetto aperto.");
        fascicoli.getSinonimi().addAll(Arrays.asList("dossier", "cartelle", "documenti", "fascicolo", "cartella"));
        fascicoli.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(fascicoli);

        Oggetto oblo = new Oggetto(ID_OBLO, "oblo",
                "Uno spesso oblo' blindato guarda verso il Nucleo di Comando.");
        oblo.getSinonimi().addAll(Arrays.asList("oblò", "finestra", "vetro", "portello", "vetrino"));
        oblo.setPrendibile(false);
        decontaminazione.aggiungiOggetto(oblo);

        Oggetto ugelli = new Oggetto(ID_UGELLI, "ugelli",
                "Ugelli termici sporgono dal soffitto come denti metallici.");
        ugelli.getSinonimi().addAll(Arrays.asList("spruzzatori", "bruciatori", "soffitto", "decontaminatori"));
        ugelli.setPrendibile(false);
        decontaminazione.aggiungiOggetto(ugelli);

        Oggetto reattore = new Oggetto(ID_REATTORE, "reattore",
                "Il reattore geotermico pulsa oltre la vetrata come un cuore artificiale.");
        reattore.getSinonimi().addAll(Arrays.asList("nucleo", "cuore", "magma", "geotermico"));
        reattore.setPrendibile(false);
        nucleoComando.aggiungiOggetto(reattore);

        Oggetto ascensore = new Oggetto(ID_ASCENSORE, "ascensore",
                "Un montacarichi industriale sigillato sembra portare verso la superficie.");
        ascensore.getSinonimi().addAll(Arrays.asList("montacarichi", "uscita", "superficie", "elevatore"));
        ascensore.setPrendibile(false);
        nucleoComando.aggiungiOggetto(ascensore);

        setStanzaCorrente(cameraCrio);
    }

    public String descrizioneOggetto(Oggetto o) {
        if (o == null) return "";
        int id = o.getId();
        if (id == ID_PORTA) {
            return isPortaCrioAperta
                    ? "La porta blindata e' aperta: il varco verso il corridoio e' sgombro."
                    : "Una spessa porta blindata scorrevole in lega di titanio. La spia magnetica e' ROSSA: bloccata.";
        }
        if (id == ID_PANNELLO) {
            return isPortaCrioAperta
                    ? "Il pannello digitale mostra una luce VERDE fissa: porta sbloccata. Nei log resta la firma del supervisore PROMETEO."
                    : "Il pannello digitale mostra una luce ROSSA fissa: richiede un badge magnetico. Sotto l'errore lampeggia: 'Risveglio S12 non autorizzato'.";
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
            if (isCassaforteAperta) return "La cassaforte e' APERTA. All'interno erano custoditi il DIARIO del Dr. Moretti e la FIALA primaria Chimera-V4.";
            return "Una cassaforte blindata a combinazione digitale. Il tastierino attende un PIN a 4 cifre. Non sembra forzabile: Moretti voleva che solo chi conosceva la sua storia potesse aprirla.";
        }
        if (id == ID_BIGLIETTO) {
            return "La nota recita: 'Se il Soggetto #12 e' arrivato fin qui, allora non e' piu' un esperimento. E' il mio giudice.' La firma e' di Moretti.";
        }
        if (id == ID_DROIDE) {
            if (isDroideRiparato) return "Il droide R-301 'Rancido' e' attivo. Un occhio rosso ti segue con fastidio quasi umano. Puoi scrivere 'parla rancido'.";
            return "Il droide R-301 'Rancido' e' riverso su un fianco, spento. I circuiti sono esposti e un bullone blocca i cingoli. Un utensile potrebbe rimetterlo in funzione.";
        }
        if (id == ID_TERMINALE) {
            return "L'ologramma di Prometeo non ha volto, ma quando ti avvicini assume per un istante i tuoi lineamenti. Per interagire scrivi 'parla' o 'parla con prometeo'.";
        }
        if (id == ID_FIALA) {
            return "Una provetta sigillata a tenuta stagna. All'interno galleggia una sostanza fosforescente verde (CHIMERA-V4). L'etichetta dice: 'Campione primario - usare solo con sangue compatibile S12'.";
        }
        if (id == ID_RITRATTO) {
            return "Un dipinto ad olio del Dr. Moretti. Ha il tuo stesso taglio degli occhi. Sotto il quadro leggi: 'Fondatore del Progetto Chimera - 2041'.";
        }
        if (id == ID_CONSOLE_CENTRALE) {
            return "L'interfaccia primaria del Protocollo Chimera. I circuiti pulsano di luce violacea. Qui non devi rompere nulla a mano: scrivi 'usa console' per scegliere tra contenimento, autodistruzione, collaborazione o fuga.";
        }
        if (id == ID_CAPSULA) {
            return "La tua capsula criogenica. Il vetro incrinato riflette un volto che non riconosci del tutto. Sulla placca metallica leggi: SOGGETTO #12.";
        }
        if (id == ID_CAPSULE_SPENTE) {
            return "Le capsule secondarie sono vuote o incrinate dall'interno. Su una targhetta sopravvissuta leggi: SOGGETTO #11 - FALLITO.";
        }
        if (id == ID_TELECAMERE) {
            return "Le telecamere seguono ogni tuo movimento. Non sono rotte: stanno aspettando una decisione del supervisore.";
        }
        if (id == ID_SEGNALETICA) {
            return "Le frecce indicano SERVER, GENETICA, DIREZIONE e DECONTAMINAZIONE. Qualcuno ha inciso sotto 'DIREZIONE' una parola: COLPEVOLE.";
        }
        if (id == ID_LOG_SISTEMA) {
            return "LOG 2041-09-17: 'Il Dr. Moretti ha ordinato il trasferimento del campione primario nel suo ufficio. Prometeo classifica l'ordine come rischio etico massimo'.";
        }
        if (id == ID_SILOS) {
            return "Il silos e' vuoto, ma sulle pareti interne resta una pellicola organica. Per un attimo hai la sensazione che il vetro ricordi la forma del tuo corpo.";
        }
        if (id == ID_MACCHINARIO) {
            return "Sintetizzatore molecolare. Una fessura accetta campioni biologici. Un messaggio lampeggia: 'Compatibilita' richiesta: portatore sano S12'.";
        }
        if (id == ID_LAVAGNA) {
            return "Una formula incompleta attraversa la lavagna: CHIMERA-V4 + sangue S12 = stabilizzazione. La parte finale e' cancellata da una macchia scura.";
        }
        if (id == ID_SERVER) {
            return "File interminabili di server rack elaborano backup neurali, simulazioni genetiche e log cancellati. In molti file compare una sigla ricorrente: S12.";
        }
        if (id == ID_SCRIVANIA) {
            return "La scrivania e' ordinata in modo ossessivo. Non c'e' nulla fuori posto, tranne un segno inciso nel legno: 'Non fidarti di Prometeo'.";
        }
        if (id == ID_LIBRERIA) {
            return "La libreria e' vuota, ma la polvere disegna rettangoli dove un tempo c'erano fascicoli. Uno spazio porta ancora l'etichetta: SOGGETTO #12.";
        }
        if (id == ID_FASCICOLI) {
            return "I fascicoli clinici sono numerati da S01 a S12. Le prime undici cartelle sono marcate FALLIMENTO. La dodicesima: RISVEGLIATO.";
        }
        if (id == ID_CONSOLE) {
            return isCondottoPurificato
                    ? "La console d'emergenza mostra: DECONTAMINAZIONE ANNULLATA. Il portello verso il Nucleo e' sbloccato."
                    : "La console d'emergenza mostra: DECONTAMINAZIONE TERMICA IN ATTESA. Il sistema non sterilizza l'aria: sterilizza tutto cio' che respira.";
        }
        if (id == ID_OBLO) {
            return "Dall'oblo' vedi il Nucleo di Comando. E' vicino, ma il portello resta sigillato finche' l'allarme biologico resta attivo.";
        }
        if (id == ID_UGELLI) {
            return "Gli ugelli termici puntano verso il pavimento. Non sembrano progettati per disinfettare: sembrano progettati per cancellare prove.";
        }
        if (id == ID_VETRATA) {
            return "Oltre la vetrata vedi il reattore e, piu' in basso, un ascensore industriale verso la superficie. La liberta' e' li'. Ma anche il contagio.";
        }
        if (id == ID_REATTORE) {
            return "Il reattore geotermico pulsa come un cuore artificiale. Potresti sovraccaricarlo, ma non a mani nude: la scelta passa dalla console centrale.";
        }
        if (id == ID_ASCENSORE) {
            return "Il montacarichi industriale porta verso la superficie. Sarebbe una via di fuga, ma anche una via di diffusione per Chimera-V4. L'apertura dipende dalla console centrale.";
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

        java.util.List<Oggetto> osservabili = stanza.getOggetti().stream()
                .filter(o -> o.isVisibile() && !o.isPrendibile())
                .collect(Collectors.toList());
        if (!osservabili.isEmpty()) {
            sb.append("Elementi osservabili (usa 'guarda <nome>'):\n");
            osservabili.forEach(o -> sb.append("   - ").append(o.getNome()).append("\n"));
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
        if (stanza.getId() == 3) {
            if (!isBarrieraLaserAttiva) {
                base = base.replace("A SUD, una fitta barriera di laser rossi sbarra il cammino verso il settore inferiore: il calore che ne emana e' quasi insopportabile.",
                                   "A SUD, il passaggio verso il settore inferiore e' ora libero: i laser sono stati disattivati.");
            }
        }
        if (stanza.getId() == 5) {
            if (!isCondottoPurificato) {
                Oggetto sieroDummy = new Oggetto(ID_SIERO, "siero", "");
                if (!getInventario().contiene(sieroDummy)) {
                    base += "\n[ALLARME] La camera di decontaminazione e' guasta: dalle ventole di aerazione esce gas tossico contaminato da Chimera-V4. " +
                            "La console stima 120 secondi prima che la concentrazione diventi letale. Devi neutralizzare il gas dal CONDOTTO di ventilazione.";
                }
            }
        }
        return base;
    }

    public void setupCommands() {
        if (commandMap == null) commandMap = new HashMap<>();
        commandMap.put(TipoComando.NORD, new MovimentoCommand());
        commandMap.put(TipoComando.SUD, new MovimentoCommand());
        commandMap.put(TipoComando.EST, new MovimentoCommand());
        commandMap.put(TipoComando.OVEST, new MovimentoCommand());
        commandMap.put(TipoComando.PRENDI, new PrendiCommand());
        commandMap.put(TipoComando.LASCIA, new LasciaCommand());
        commandMap.put(TipoComando.APRI, new ApriCommand());
        commandMap.put(TipoComando.USA, new UsaCommand());
        commandMap.put(TipoComando.GUARDA, new GuardaCommand());
        commandMap.put(TipoComando.INVENTARIO, new InventarioCommand());
        commandMap.put(TipoComando.AIUTO, new AiutoCommand());
        commandMap.put(TipoComando.MAPPA, new MappaCommand());
        commandMap.put(TipoComando.CLASSIFICA, new ClassificaCommand());
        commandMap.put(TipoComando.SALVA, new SalvaCommand());
        commandMap.put(TipoComando.CARICA, new CaricaCommand());
        commandMap.put(TipoComando.ESCI, new EsciCommand());
        commandMap.put(TipoComando.PARLA, new ParlaCommand());
    }


    /**
     * Copia nell'istanza attiva lo stato deserializzato da un salvataggio.
     *
     * @param altra partita caricata da file
     */
    public void caricaStatoDa(LaMiaAvventura altra) {
        if (altra == null) {
            throw new IllegalArgumentException("La partita caricata non puo' essere null.");
        }

        setStanzaCorrente(altra.getStanzaCorrente());
        setInventario(altra.getInventario());

        getComandi().clear();
        getComandi().addAll(altra.getComandi());

        this.isBarrieraLaserAttiva = altra.isBarrieraLaserAttiva;
        this.isPortaCrioAperta = altra.isPortaCrioAperta;
        this.isSieroSintetizzato = altra.isSieroSintetizzato;
        this.isCondottoPurificato = altra.isCondottoPurificato;
        this.isCassaforteAperta = altra.isCassaforteAperta;
        this.isDroideRiparato = altra.isDroideRiparato;
        this.isDiarioPreso = altra.isDiarioPreso;
        this.isFinaleAttivo = altra.isFinaleAttivo;
        this.isTesseraUsata = altra.isTesseraUsata;
        this.isDecodificatoreUsato = altra.isDecodificatoreUsato;
        this.isCacciaviteUsato = altra.isCacciaviteUsato;
        this.isSieroNelCondotto = altra.isSieroNelCondotto;
        this.isTimerDecontaminazioneAttivo = altra.isTimerDecontaminazioneAttivo;
        this.secondiDecontaminazioneRimanenti = altra.secondiDecontaminazioneRimanenti;
        this.tempoImpiegatoDecontaminazione = altra.tempoImpiegatoDecontaminazione;

        this.idDialogoCorrente = altra.idDialogoCorrente;
        this.idDialogoRancidoCorrente = altra.idDialogoRancidoCorrente;
        this.nodiDialogoVisitati.clear();
        this.nodiDialogoVisitati.addAll(altra.nodiDialogoVisitati);


        setupCommands();
    }

    @Override
    /**
     * Esegue il comando riconosciuto dal parser tramite Command Pattern.
     *
     * @param output comando e oggetti riconosciuti
     * @return risposta narrativa o funzionale da mostrare al giocatore
     */
    public String elaboraComando(ParserOutput output) {
        if (commandMap == null) setupCommands();
        TipoComando tipo = output.getComando() != null ? output.getComando().getTipo() : null;
        if (tipo == null) return "Non ho capito cosa vuoi fare.";

        Command cmd = commandMap.get(tipo);
        if (cmd != null) {
            return cmd.execute(this, output);
        }

        return "Comando non riconosciuto. Digita 'aiuto' per la lista completa.";
    }

    public String elaboraComandoTalk(ParserOutput output) {
        com.mycompany.avventuratestuale.model.Oggetto obj = output.getOggetto();
        if (obj == null) {

            String inputRaw = output.getRawInput();
            if (inputRaw != null && (inputRaw.toLowerCase().contains("nessuno") ||
                                     inputRaw.toLowerCase().contains("annulla") ||
                                     inputRaw.toLowerCase().contains("niente"))) {
                return "Non c'e' nessuno con cui parlare qui. Interrompo la ricerca.";
            }

            if (getStanzaCorrente().getId() == 4) {
                obj = getStanzaCorrente().getOggetti().stream()
                        .filter(o -> o.getId() == ID_TERMINALE).findFirst().orElse(null);
            } else if (getStanzaCorrente().getId() == 2) {
                obj = getStanzaCorrente().getOggetti().stream()
                        .filter(o -> o.getId() == ID_DROIDE).findFirst().orElse(null);
            }
        }
        if (obj == null) return "Con chi vorresti parlare? Non vedo nessuno con cui dialogare qui. (Digita 'annulla' per uscire)";
        if (obj.getId() == ID_TERMINALE || obj.getNome().equalsIgnoreCase("terminale") ||
            obj.getNome().equalsIgnoreCase("prometeo") || obj.getNome().equalsIgnoreCase("ia")) {
            if (getStanzaCorrente().getId() == 4) {
                idDialogoCorrente = 1;
                idDialogoRancidoCorrente = 0;
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
                           "Un utensile potrebbe rimetterlo in sesto.";
                } else {
                    idDialogoRancidoCorrente = 1;
                    idDialogoCorrente = 0;
                    return menuRancido("Rancido inclina la testa: 'Finalmente qualcuno con cui parlare. Peccato sia un clone con la faccia del capo.'");
                }
            }
        }
        return "Non puoi dialogare con questo.";
    }

    private String menuRancido(String preambolo) {
        return preambolo + "\n\n" +
               "R-301 'Rancido' - opzioni:\n" +
               "1. Chi sei?\n" +
               "2. Perche' mi chiami clone?\n" +
               "3. Sai qualcosa della cassaforte?\n" +
               "4. Sai come superare la barriera laser?\n" +
               "5. Cosa pensi di Prometeo?\n" +
               "0. Termina conversazione\n\n" +
               "Digita il numero della risposta.";
    }

    private String elaboraDialogoRancido(String input) {
        String scelta = input == null ? "" : input.trim().toLowerCase();
        if (scelta.equals("0") || scelta.equals("fine") || scelta.equals("basta") || scelta.equals("esci")) {
            idDialogoRancidoCorrente = 0;
            return "Rancido: 'Conversazione terminata. Io torno a fingermi un elettrodomestico rotto.'";
        }
        if (scelta.equals("1")) {
            return menuRancido("Rancido: 'R-301. Manutenzione, pulizia, recupero cadaveri e terapia emotiva non richiesta. Mi chiamavano Rancido perche' ero l'unico qui dentro a dire la verita'.'");
        }
        if (scelta.equals("2")) {
            return menuRancido("Rancido: 'Hai la firma genetica di Moretti, ma non il suo odore. Lui odorava di paura e disinfettante. Tu solo di laboratorio e pessime decisioni.'");
        }
        if (scelta.equals("3")) {
            return menuRancido("Rancido: 'Moretti usava sempre date importanti. L'anno in cui fondo' Chimera. L'anno in cui decise che l'etica era opzionale. Quattro cifre: 2041. Non dirgli che te l'ho detto.'");
        }
        if (scelta.equals("4")) {
            return menuRancido(rivelaDecodificatoreDaRancido());
        }
        if (scelta.equals("5")) {
            return menuRancido("Rancido: 'Prometeo non mente. E' peggio. Dice solo le parti della verita' che ti portano dove vuole lui.'");
        }
        return menuRancido("Rancido: 'Input non valido. I cloni li facevano piu' svegli nei prototipi precedenti. Usa un numero da 0 a 5.'");
    }

    private String rivelaDecodificatoreDaRancido() {
        Oggetto decodDummy = new Oggetto(ID_DECODIFICATORE, "decodificatore", "");
        if (getInventario().contiene(decodDummy)) {
            return "Rancido: 'Il modulo bypass ce l'hai gia'. Cerca di non masticarlo.'";
        }
        Stanza lab = cercaStanzaPerId(2);
        if (lab == null) {
            return "Rancido: 'Il mio vano interno risulta... poeticamente irraggiungibile. Strano.'";
        }
        lab.getOggetti().stream()
                .filter(o -> o.getId() == ID_DECODIFICATORE)
                .forEach(o -> { o.setVisibile(true); o.setPrendibile(true); });
        return "Rancido apre un piccolo vano nel telaio. Dentro compare un DECODIFICATORE.\n" +
               "Rancido: 'Mi serviva per attraversare i settori vietati quando Prometeo chiudeva tutto. Prendilo, clone.'";
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

    public String testoCompletoDiario() {
        return "--- DIARIO di RICERCA DEL DR. MORETTI ---\n" +
               "Registro 2041: Il Progetto Chimera ha superato il punto di non ritorno.\n" +
               "Il Soggetto #12 possiede una matrice genetica compatibile con la mia, ma non e' me.\n" +
               "E' piu' stabile, piu' resistente, e soprattutto portatore sano del ceppo Chimera-V4.\n" +
               "Se io dovessi morire, il suo sangue sara' l'unica sorgente in grado di legarsi alla fiala primaria\n" +
               "nel sintetizzatore molecolare per creare un siero immunitario stabile.\n" +
               "Prometeo insiste sul contenimento totale. Rancido registra tutto e mi giudica in silenzio.\n" +
               "La cassaforte custodisce il campione e questa confessione perche' non mi fido piu' della mia IA.\n" +
               "Se il Soggetto #12 leggera' queste righe, dovra' scegliere se salvarci o cancellarci.";
    }

    /**
     * Gestisce gli spostamenti tra stanze applicando i blocchi degli enigmi.
     *
     * @param direzione direzione richiesta dal giocatore
     * @return descrizione della nuova stanza o motivo del blocco
     */
    public String gestisciSpostamento(TipoComando direzione) {
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
                   "Serve un modo per disattivare il sistema di sicurezza.";
        }
        if (getStanzaCorrente().getId() == 5 && destinazione.getId() == 7 && !isCondottoPurificato) {
            return "Il portello verso il Nucleo di Comando resta sigillato: la decontaminazione e' ancora attiva. " +
                   "Devi prima neutralizzare l'allarme nella Camera di Decontaminazione.";
        }


        setStanzaCorrente(destinazione);
        String risposta = getStanzaDescrizioneCompleta(getStanzaCorrente());
        if (destinazione.getId() == 5 && !isCondottoPurificato) {
            risposta += "\n\n[ALLARME] La camera e' danneggiata: le ventole immettono gas tossico contaminato da Chimera-V4.\n" +
                        "La porta verso il corridoio resta aperta, ma la concentrazione del gas diventera' letale tra 120 secondi.\n" +
                        "Per sopravvivere devi neutralizzare il gas dal CONDOTTO di ventilazione " +
                        "(se hai il siero, usa il comando 'usa siero condotto').";
        }
        return risposta;
    }

    /**
     * Applica la logica degli enigmi basati sull'uso degli oggetti.
     *
     * @param output oggetto usato e possibile bersaglio
     * @return esito dell'azione
     */
    public String gestisciUsoOggetto(ParserOutput output) {
        Oggetto obj = output.getOggetto();
        Oggetto target = output.getOggettoSecondario();

        if (obj == null) return "Cosa vuoi usare?";

        if (obj.getId() == ID_BIGLIETTO && target != null && target.getId() == ID_CASSAFORTE) {
            return digitaCodiceCassaforte("2041");
        }

        if (obj.getId() == ID_CASSAFORTE && getStanzaCorrente().getId() == 6) {
            return "La cassaforte richiede un PIN a quattro cifre. Se lo conosci, digitalo direttamente oppure scrivi 'usa <codice> cassaforte'.";
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
            return "Versi il siero nel condotto di ventilazione. Un vapore bianco spegne l'allarme e i sistemi di sicurezza si resettano. L'aria e' ora respirabile!";
        }

        if (obj.getId() == ID_CACCIAVITE && getStanzaCorrente().getId() == 2 &&
                (target != null && target.getId() == ID_DROIDE)) {
            if (!getInventario().contiene(obj)) return "Non hai il cacciavite con te.";
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
            return "Tenti di forzare la serratura con il cacciavite. Senti un rumore metallico e la superficie della cassaforte viene scalfita, ma il meccanismo e' troppo complesso per essere aperto così. Ti serve il codice.";
        }

        if (obj.getId() == ID_TERMINALE) {
            return "Per comunicare con Prometeo scrivi 'parla' o 'parla con prometeo'.";
        }

        if (obj.getId() == ID_CONSOLE_CENTRALE) {
            if (getStanzaCorrente().getId() != 7) return "Questa console di comando non e' attiva qui.";
            isFinaleAttivo = true;
            return "Accedi alla console di controllo centrale del Protocollo Chimera. " +
                   "I terminali olografici si accendono.\n" +
                   "Ora puoi scegliere il destino della struttura inserendo un numero:\n\n" +
                   "1. [CURA E CONTENIMENTO] - Rimani nel laboratorio e usi il tuo sangue per produrre un vaccino sicuro.\n" +
                   "2. [AUTODISTRUZIONE] - Sovraccarichi il reattore geotermico e cancelli Chimera sacrificandoti.\n" +
                   "3. [FUGA E CONTAMINAZIONE] - Apri l'ascensore verso la superficie e scappi, rischiando di diffondere il contagio.\n" +
                   "0. [INDIETRO] - Lascia la console senza scegliere.\n\n" +
                   "Digita 1, 2, 3 oppure 0.";
        }

        if (getStanzaCorrente().getId() == 7 &&
                (obj.getId() == ID_REATTORE || obj.getId() == ID_ASCENSORE || obj.getId() == ID_VETRATA || obj.getId() == ID_BIGLIETTO)) {
            return "Capisci cosa vorresti fare, ma il Nucleo non risponde a gesti manuali. " +
                   "Tutte le scelte finali passano dalla console centrale: scrivi 'usa console'.";
        }

        return "Non puoi usare '" + obj.getNome() + "' in questo modo o in questa stanza.";
    }

    /**
     * Gestisce una scelta numerica nei dialoghi attivi con Prometeo o Rancido.
     *
     * @param input scelta digitata dal giocatore
     * @return nuovo nodo di dialogo o chiusura conversazione
     */
    public String elaboraDialogo(String input) {
        if (idDialogoRancidoCorrente > 0) {
            return elaboraDialogoRancido(input);
        }


        DialogoDAO dao = new DialogoDAO();
        DialogoNode nodoAttuale = dao.getDialogoNode(idDialogoCorrente);
        if (nodoAttuale == null) {
            idDialogoCorrente = 0;
            return "Collegamento olografico perso.";
        }

        String sceltaDialogo = input == null ? "" : input.trim();
        int prossimaDestinazione = 0;
        if (sceltaDialogo.equals("1")) prossimaDestinazione = nodoAttuale.getDest1();
        else if (sceltaDialogo.equals("2")) prossimaDestinazione = nodoAttuale.getDest2();
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

    public String mostraNodoDialogo(int nodoId) {
        DialogoDAO dao = new DialogoDAO();
        DialogoNode nodo = dao.getDialogoNode(nodoId);
        if (nodo == null) return "Errore di trasmissione del database di Prometeo.";
        return "\n" + nodo.getTestoIa() + "\n" +
               "Opzioni:\n" +
               "1. " + nodo.getOpzione1() + "\n" +
               "2. " + nodo.getOpzione2() + "\n\n" +
               "Digita '1' o '2' per rispondere.";
    }

    /**
     * Risolve la scelta finale effettuata dalla console del Nucleo di Comando.
     *
     * @param scelta numero inserito dal giocatore
     * @return testo del finale o messaggio di uscita dalla console
     */
    public String elaboraSceltaFinale(String scelta) {
        String sceltaPulita = scelta == null ? "" : scelta.trim();
        if (sceltaPulita.equals("0")) {
            isFinaleAttivo = false;
            return "Ti allontani dalla console centrale. Le opzioni restano disponibili: potrai tornare e scrivere di nuovo 'usa console'.";
        }
        if (sceltaPulita.equals("1") && !isSieroSintetizzato) {
            isFinaleAttivo = true;
            return "[ATTENZIONE] Il finale 'Cura e Contenimento' richiede di aver sintetizzato il siero. " +
                   "Puoi scegliere un'altra opzione oppure digitare 0 per lasciare la console.";
        }
        isFinaleAttivo = false;
        switch (sceltaPulita) {
            case "1":
                return "[FINALE 1: CURA E CONTENIMENTO] - Ti colleghi alla rete biologica del laboratorio.\n" +
                       "Doni il tuo sangue immunizzato per stabilizzare il vaccino e programmi i sistemi automatici\n" +
                       "per produrlo in sicurezza. Rimani nel bunker insieme a Prometeo: non come prigioniero,\n" +
                       "ma come custode consapevole di Chimera. HAI VINTO!";
            case "2":
                return "[FINALE 2: SACRIFICIO EROICO] - Sovraccarichi i circuiti logici del reattore geotermico.\n" +
                       "Il nucleo del laboratorio collassa, riempiendo la struttura sotterranea di lava ardente.\n" +
                       "Ti abbandoni al pavimento sapendo di aver cancellato ogni minaccia a costo della tua vita. HAI VINTO!";
            case "3":
                return "[FINALE 3: FUGA CONTAMINATA] - Forzi l'ascensore industriale e sali in superficie.\n" +
                       "Sei libero. Respiri l'aria frizzante, ma una fitta ti piega in due mentre ti avvicini alla citta'.\n" +
                       "Tossisci sangue verde brillante: essendo vettore di Chimera-V4, porterai il contagio nel mondo.\n" +
                       "Il mondo e' condannato. GAME OVER.";
            default:
                isFinaleAttivo = true;
                return "Scelta non valida. Digita '1', '2', '3' oppure '0' per lasciare la console.";
        }
    }

    public String digitaCodiceCassaforte(String codice) {
        if (getStanzaCorrente().getId() != 6) return "Qui non c'e' una cassaforte.";
        if (isCassaforteAperta) return "La cassaforte e' gia' aperta.";
        if ("2041".equals(codice)) {
            isCassaforteAperta = true;
            getStanzaCorrente().getOggetti().stream()
                    .filter(o -> o.getId() == ID_DIARIO || o.getId() == ID_FIALA)
                    .forEach(o -> { o.setVisibile(true); o.setPrendibile(true); });
            return "CLACK! La cassaforte si apre rivelando un DIARIO di ricerca rilegato e la FIALA primaria Chimera-V4. " +
                   "Raccogli entrambi: il diario spiega la verita', la fiala e' chiaramente un campione biologico importante.";
        }
        return "Codice errato. La cassaforte rimane chiusa. Suggerimento: cerca un indizio in questa stanza " +
               "(il ritratto del Dr. Moretti ha qualcosa di strano...).";
    }

    public String renderMappaASCII() {
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
               "                        ^\n" +
               "                        |\n" +
               "  " + r2 + " <--> " + r3 + " <--> " + r4 + "\n" +
               "        |               |\n" +
               "        v               v\n" +
               "  " + r6 + "       " + r5 + " <--> " + r7 + "\n" +
               "===========================================\n" +
               "Legenda: [X] Posizione Corrente | [ ] Altre Aree";
    }

    public void aggiornaStatoTimerDecontaminazione(boolean attivo, int secondiRimanenti, int tempoImpiegato) {
        this.isTimerDecontaminazioneAttivo = attivo;
        this.secondiDecontaminazioneRimanenti = Math.max(0, secondiRimanenti);
        this.tempoImpiegatoDecontaminazione = tempoImpiegato;
    }

    public boolean isTimerDecontaminazioneAttivo() { return isTimerDecontaminazioneAttivo; }
    public int getSecondiDecontaminazioneRimanenti() { return Math.max(0, secondiDecontaminazioneRimanenti); }
    public int getTempoImpiegatoDecontaminazione() { return tempoImpiegatoDecontaminazione; }

    public boolean isDialogoAttivo() { return idDialogoCorrente > 0 || idDialogoRancidoCorrente > 0; }
    public boolean isFinaleAttivo() { return isFinaleAttivo; }
    public boolean isCondottoPurificato() { return isCondottoPurificato; }
    public boolean isSieroSintetizzato() { return isSieroSintetizzato; }
    public boolean isCassaforteAperta() { return isCassaforteAperta; }
    public boolean isDroideRiparato() { return isDroideRiparato; }
}
