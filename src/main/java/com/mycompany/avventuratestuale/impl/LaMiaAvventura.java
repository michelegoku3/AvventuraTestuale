package com.mycompany.avventuratestuale.impl;

import com.mycompany.avventuratestuale.core.Gioco;
import com.mycompany.avventuratestuale.core.ParserOutput;
import com.mycompany.avventuratestuale.core.Comando;
import com.mycompany.avventuratestuale.core.TipoComando;
import com.mycompany.avventuratestuale.model.Stanza;
import com.mycompany.avventuratestuale.model.Oggetto;
import com.mycompany.avventuratestuale.database.DialogoNode;
import com.mycompany.avventuratestuale.database.DialogoDAO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class LaMiaAvventura extends Gioco {
    private static final long serialVersionUID = 1L;

    // Flag booleani di stato dell'avventura [Guida_Progetto_MAP/08_Progettazione_Trama_Scelta.md]
    private boolean isBarrieraLaserAttiva = true;
    private boolean isPortaCrioAperta = false;
    private boolean isSieroSintetizzato = false;
    private boolean isCondottoPurificato = false;
    private boolean isCassaforteAperta = false;
    private boolean isDroideRiparato = false;
    private boolean isDiarioPreso = false;
    private boolean isFinaleAttivo = false;

    // Id del dialogo attivo con Prometeo (0 = inattivo)
    private int idDialogoCorrente = 0;

    @Override
    public void inizializza() throws Exception {
        // 1. Creazione dei Comandi con sinonimi Italiani ed Inglesi [Lab 1 - Introduzione.pdf, p. 34]
        getComandi().add(new Comando(TipoComando.NORD, new HashSet<>(Arrays.asList("nord", "n", "vai a nord", "north", "go north"))));
        getComandi().add(new Comando(TipoComando.SUD, new HashSet<>(Arrays.asList("sud", "s", "vai a sud", "south", "go south"))));
        getComandi().add(new Comando(TipoComando.EST, new HashSet<>(Arrays.asList("est", "e", "vai a est", "east", "go east"))));
        getComandi().add(new Comando(TipoComando.OVEST, new HashSet<>(Arrays.asList("ovest", "o", "vai a ovest", "west", "go west"))));
        getComandi().add(new Comando(TipoComando.PRENDI, new HashSet<>(Arrays.asList("prendi", "raccogli", "afferra", "get", "take", "grab"))));
        getComandi().add(new Comando(TipoComando.LASCIA, new HashSet<>(Arrays.asList("lascia", "posa", "butta", "drop", "leave"))));
        getComandi().add(new Comando(TipoComando.APRI, new HashSet<>(Arrays.asList("apri", "sblocca", "open"))));
        getComandi().add(new Comando(TipoComando.USA, new HashSet<>(Arrays.asList("usa", "utilizza", "attiva", "use"))));
        getComandi().add(new Comando(TipoComando.GUARDA, new HashSet<>(Arrays.asList("guarda", "esamina", "osserva", "look", "examine"))));
        getComandi().add(new Comando(TipoComando.INVENTARIO, new HashSet<>(Arrays.asList("inventario", "inv", "zaino", "inventory"))));
        getComandi().add(new Comando(TipoComando.AIUTO, new HashSet<>(Arrays.asList("aiuto", "help", "comandi"))));
        getComandi().add(new Comando(TipoComando.CLASSIFICA, new HashSet<>(Arrays.asList("classifica", "punteggi", "leaderboard"))));
        getComandi().add(new Comando(TipoComando.SALVA, new HashSet<>(Arrays.asList("salva", "save"))));
        getComandi().add(new Comando(TipoComando.CARICA, new HashSet<>(Arrays.asList("carica", "load"))));
        getComandi().add(new Comando(TipoComando.ESCI, new HashSet<>(Arrays.asList("esci", "quit", "exit"))));
        getComandi().add(new Comando(TipoComando.MAPPA, new HashSet<>(Arrays.asList("mappa", "map", "m", "radar"))));
        getComandi().add(new Comando(TipoComando.PARLA, new HashSet<>(Arrays.asList("parla", "parla con", "interroga", "talk", "talk to", "converse"))));

        // 2. Creazione delle 7 Stanze di "Protocollo Chimera" [Guida_Progetto_MAP/08_Progettazione_Trama_Scelta.md]
        Stanza cameraCrio = new Stanza(1, "Camera Criogenica", 
                "L'aria è gelida e satura di vapori chimici. Intorno a te ci sono tre capsule criogeniche inattive,\n" +
                "tranne la tua, che emette scintille dal pannello dei circuiti. Una spessa porta metallica a est,\n" +
                "chiusa ermeticamente, è l'unica via d'uscita. Sul pannello di controllo della porta pulsa una luce rossa.");
        
        Stanza labGenetica = new Stanza(2, "Laboratorio di Genetica", 
                "I banconi da lavoro sono ricoperti di vetreria da laboratorio in frantumi.\n" +
                "Un macchinario per la sintesi molecolare emette un ronzio sommesso a ovest.\n" +
                "Al centro della stanza, un enorme silos di vetro contiene un liquido amniotico scuro, ormai vuoto.\n" +
                "In un angolo tra le macerie scorgi un piccolo droide cingolato spento.");
        
        Stanza corridoio = new Stanza(3, "Corridoio di Servizio", 
                "Un lungo corridoio illuminato da luci d'emergenza arancioni. A ovest la porta conduce alla Camera Criogenica.\n" +
                "A est vedi la Sala Server. A sud, una fitta barriera di laser rossi sbarra il cammino.");
        
        Stanza salaServer = new Stanza(4, "Sala Server", 
                "Il rumore delle ventole di raffreddamento è assordante. Migliaia di server rack si estendono su più file.\n" +
                "Al centro della stanza pulsa un terminale olografico nero: è l'interfaccia centrale dell'IA Prometeo.");
        
        Stanza decontaminazione = new Stanza(5, "Camera di Decontaminazione", 
                "Una stanza asettica con spessi oblò di vetro blindato che si affacciano sul nucleo.\n" +
                "L'aria qui ha un odore chimico pungente. Sulla parete vedi un condotto di ventilazione e una console d'emergenza.");
        
        Stanza ufficioDirettore = new Stanza(6, "Ufficio del Direttore", 
                "Un ufficio lussuoso. C'è una grande scrivania in mogano, una libreria vuota\n" +
                "e una cassaforte blindata a combinazione digitale in un angolo.");
        
        Stanza nucleoComando = new Stanza(7, "Nucleo di Comando", 
                "Un'enorme camera circolare. Una colossale vetrata si affaccia sul reattore geotermico sotterraneo.\n" +
                "Al centro svetta la console di comando principale, da cui decidere il destino del laboratorio.");

        // 3. Collegamenti della Mappa (Grafo) [Lab 1 - Introduzione.pdf, p. 23]
        cameraCrio.setEst(corridoio);
        cameraCrio.setSud(labGenetica);
        
        labGenetica.setNord(cameraCrio);
        
        corridoio.setOvest(cameraCrio);
        corridoio.setEst(salaServer);
        corridoio.setSud(decontaminazione);
        corridoio.setNord(ufficioDirettore);
        
        salaServer.setOvest(corridoio);
        
        decontaminazione.setNord(corridoio);
        decontaminazione.setEst(nucleoComando);
        
        ufficioDirettore.setSud(corridoio);
        
        nucleoComando.setOvest(decontaminazione);

        // 4. Creazione e Posizionamento degli Oggetti Raccoglibili [Lab 1 - Introduzione.pdf, p. 27]
        Oggetto tessera = new Oggetto(101, "tessera", "Un badge magnetico sporco di sangue secco. Riporta la foto del Dr. Moretti.");
        tessera.getSinonimi().addAll(Arrays.asList("badge", "chiavetta", "tessera magnetica"));
        cameraCrio.aggiungiOggetto(tessera);

        Oggetto fiala = new Oggetto(102, "fiala", "Una provetta sigillata contenente un siero bioluminescente verde fosforescente.");
        fiala.getSinonimi().addAll(Arrays.asList("provetta", "patogeno", "chimera"));
        labGenetica.aggiungiOggetto(fiala);

        Oggetto decodificatore = new Oggetto(103, "decodificatore", "Un piccolo dispositivo elettronico militare utile a bypassare barriere laser.");
        decodificatore.getSinonimi().addAll(Arrays.asList("dispositivo", "bypass", "hacking"));
        salaServer.aggiungiOggetto(decodificatore);

        Oggetto cacciavite = new Oggetto(118, "cacciavite", "Un cacciavite da officina con manico isolato giallo. Molto utile per riparazioni o forzature.");
        cacciavite.getSinonimi().addAll(Arrays.asList("giravite", "utensile", "attrezzo"));
        salaServer.aggiungiOggetto(cacciavite);

        Oggetto diario = new Oggetto(104, "diario", "Il diario cartaceo del Dr. Moretti. Svela la verità sul Soggetto #12.");
        diario.getSinonimi().addAll(Arrays.asList("registri", "appunti", "libro", "diario del direttore"));
        diario.setPrendibile(false); // Nascosto dentro la cassaforte inizialmente
        diario.setVisibile(false);
        ufficioDirettore.aggiungiOggetto(diario);

        // 5. REGISTRAZIONE DEGLI OGGETTI SCENICI (SCENERY) PER RISOLUZIONE PLAYTEST [Guida_Progetto_MAP/PROGETTAZIONE_AVANZATA_CHIMERA.md]
        
        // Camera Crio Scenic Objects
        Oggetto capsula = new Oggetto(106, "capsula", "La tua capsula criogenica. Il vetro è incrinato e all'interno leggi inciso 'SOGGETTO #12'.");
        capsula.getSinonimi().addAll(Arrays.asList("capsule", "criocamera"));
        capsula.setPrendibile(false);
        cameraCrio.aggiungiOggetto(capsula);

        Oggetto porta = new Oggetto(120, "porta", "Una spessa porta blindata d'acciaio. Sopra, una spia rossa pulsa segnalando il blocco.");
        porta.getSinonimi().addAll(Arrays.asList("portone", "metallo"));
        porta.setPrendibile(false);
        cameraCrio.aggiungiOggetto(porta);

        Oggetto pannello = new Oggetto(121, "pannello", "Un pannello digitale di controllo della porta. Al momento ha uno schermo rosso fisso.");
        pannello.getSinonimi().addAll(Arrays.asList("lettore", "luce", "pulsante", "interruttore", "schermo", "monitor"));
        pannello.setPrendibile(false);
        cameraCrio.aggiungiOggetto(pannello);

        // Lab Genetica Scenic Objects
        Oggetto silos = new Oggetto(113, "silos", "Un enorme cilindro di vetro alto tre metri. All'interno galleggiano residui organici oscuri.");
        silos.getSinonimi().addAll(Arrays.asList("silus", "contenitore", "vetro", "liquido", "amniotico"));
        silos.setPrendibile(false);
        labGenetica.aggiungiOggetto(silos);

        Oggetto macchinario = new Oggetto(114, "macchinario", "Un sofisticato sintetizzatore molecolare di sostanze chimiche. Presenta una fessura per campioni biologici.");
        macchinario.getSinonimi().addAll(Arrays.asList("macchina", "centrifuga", "sintetizzatore"));
        macchinario.setPrendibile(false);
        labGenetica.aggiungiOggetto(macchinario);

        Oggetto droide = new Oggetto(301, "droide", "Il piccolo droide arrugginito R-301 'Rancido'. I suoi circuiti sono spenti e un bullone blocca i cingoli.");
        droide.getSinonimi().addAll(Arrays.asList("rancido", "robot", "robottino", "droide di manutenzione"));
        droide.setPrendibile(false);
        labGenetica.aggiungiOggetto(droide);

        // Sala Server Scenic Objects
        Oggetto server = new Oggetto(115, "server", "Intere rastrelliere di calcolatori che emettono un calore diffuso e proiettano una fitta luce blu.");
        server.getSinonimi().addAll(Arrays.asList("armadi", "elaboratori", "rack", "calcolatore", "calcolatori", "ventole"));
        server.setPrendibile(false);
        salaServer.aggiungiOggetto(server);

        Oggetto terminale = new Oggetto(108, "terminale", "Un ologramma rotante azzurro che proietta l'avatar stilizzato di Prometeo.");
        terminale.getSinonimi().addAll(Arrays.asList("console", "ologramma", "prometeo", "ia", "ai", "interfaccia", "schermo"));
        terminale.setPrendibile(false);
        salaServer.aggiungiOggetto(terminale);

        // Decontaminazione Scenic Objects
        Oggetto condotto = new Oggetto(109, "condotto", "Il portello metallico di ventilazione che aspira l'aria per filtrarla.");
        condotto.getSinonimi().addAll(Arrays.asList("condotti", "ventilazione", "aerazione", "grata"));
        condotto.setPrendibile(false);
        decontaminazione.aggiungiOggetto(condotto);

        Oggetto console = new Oggetto(110, "console", "Pannello d'emergenza con una dicitura illuminata: 'SISTEMA DI DECONTAMINAZIONE ATTIVO'.");
        console.getSinonimi().addAll(Arrays.asList("tastiera", "computer"));
        console.setPrendibile(false);
        decontaminazione.aggiungiOggetto(console);

        // Ufficio Direttore Scenic Objects
        Oggetto cassaforte = new Oggetto(111, "cassaforte", "Una cassaforte blindata a combinazione. Il monitor digitale attende un codice a 4 cifre.");
        cassaforte.getSinonimi().addAll(Arrays.asList("safe", "armadio d'acciaio"));
        cassaforte.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(cassaforte);

        Oggetto ritratto = new Oggetto(117, "ritratto", "Un dipinto ad olio raffigurante il Dr. Moretti con espressione austera e distaccata.");
        ritratto.getSinonimi().addAll(Arrays.asList("dipinto", "quadro"));
        ritratto.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(ritratto);

        Oggetto scrivania = new Oggetto(122, "scrivania", "Una scrivania massiccia in mogano scuro. Sopra non c'è nulla, tranne polvere.");
        scrivania.getSinonimi().addAll(Arrays.asList("tavolo", "scrivania in mogano"));
        scrivania.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(scrivania);

        Oggetto libreria = new Oggetto(123, "libreria", "Una libreria a scaffali in legno. È desolatamente vuota, ad eccezione di qualche ragnatela.");
        libreria.getSinonimi().addAll(Arrays.asList("scaffale", "scaffali", "ragnatela", "ragnatela"));
        libreria.setPrendibile(false);
        ufficioDirettore.aggiungiOggetto(libreria);

        // Nucleo Comando Scenic Objects
        Oggetto vetrata = new Oggetto(116, "vetrata", "Una lastra trasparente rinforzata. Di fuori, vedi i fiumi di magma del nucleo geotermico.");
        vetrata.getSinonimi().addAll(Arrays.asList("finestra", "vetro"));
        vetrata.setPrendibile(false);
        nucleoComando.aggiungiOggetto(vetrata);

        Oggetto consoleCentrale = new Oggetto(112, "console_centrale", "L'interfaccia primaria di controllo del Protocollo Chimera.");
        consoleCentrale.getSinonimi().addAll(Arrays.asList("quadro di comando", "console principale"));
        consoleCentrale.setPrendibile(false);
        nucleoComando.aggiungiOggetto(consoleCentrale);

        // Imposta la stanza iniziale
        setStanzaCorrente(cameraCrio);
    }

    // Metodo fondamentale per generare descrizioni estese, esaustive con oggetti e uscite [Esercizio Lab.pdf]
    public String getStanzaDescrizioneCompleta(Stanza stanza) {
        StringBuilder sb = new StringBuilder();
        sb.append(stanza.getDescrizione()).append("\n\n");

        // Utilizzo avanzato di Lambda e Stream (Criterio 8) per elencare gli oggetti prendibili visibili!
        java.util.List<Oggetto> raccoglibili = stanza.getOggetti().stream()
                .filter(o -> o.isVisibile() && o.isPrendibile())
                .collect(Collectors.toList());

        if (!raccoglibili.isEmpty()) {
            sb.append("📦 In questa stanza noti i seguenti oggetti utili:\n");
            raccoglibili.forEach(o -> sb.append("   - ").append(o.getNome()).append(" (").append(o.getDescrizione()).append(")\n"));
            sb.append("\n");
        }

        // Elenco strutturato delle uscite disponibili per rimuovere il disorientamento del giocatore
        sb.append("🚪 Uscite disponibili:\n");
        boolean haUscite = false;
        if (stanza.getNord() != null) {
            sb.append("   - nord (verso ").append(stanza.getNord().getNome()).append(")\n");
            haUscite = true;
        }
        if (stanza.getSud() != null) {
            sb.append("   - sud (verso ").append(stanza.getSud().getNome()).append(")\n");
            haUscite = true;
        }
        if (stanza.getEst() != null) {
            sb.append("   - est (verso ").append(stanza.getEst().getNome()).append(")\n");
            haUscite = true;
        }
        if (stanza.getOvest() != null) {
            sb.append("   - ovest (verso ").append(stanza.getOvest().getNome()).append(")\n");
            haUscite = true;
        }
        if (!haUscite) {
            sb.append("   - nessuna uscita visibile.\n");
        }

        return sb.toString();
    }

    @Override
    public String elaboraComando(ParserOutput output) {
        TipoComando tipo = output.getComando().getTipo();
        Oggetto obj = output.getOggetto();

        switch (tipo) {
            case NORD, SUD, EST, OVEST -> {
                return gestisciSpostamento(tipo);
            }
            case PRENDI -> {
                if (obj == null) return "Cosa vuoi prendere?";
                if (!getStanzaCorrente().getOggetti().contains(obj)) return "Non vedo questo oggetto qui.";
                if (!obj.isPrendibile()) return "È saldato a terra! Non puoi prenderlo.";
                
                getStanzaCorrente().rimuoviOggetto(obj);
                getInventario().add(obj);
                
                // Se prende il diario, aggiorna il progresso
                if (obj.getId() == 104) {
                    isDiarioPreso = true;
                }
                
                return "Hai raccolto l'oggetto: " + obj.getNome() + ". Digita 'inventario' per vederlo o 'guarda " + obj.getNome() + "' per esaminarlo.";
            }
            case LASCIA -> {
                if (obj == null) return "Cosa vuoi lasciare?";
                if (!getInventario().contains(obj)) return "Non hai questo oggetto nell'inventario.";
                
                getInventario().remove(obj);
                getStanzaCorrente().aggiungiOggetto(obj);
                return "Hai lasciato l'oggetto: " + obj.getNome() + " nella stanza.";
            }
            case GUARDA -> {
                if (obj != null) {
                    if (obj.getId() == 104) { // DIARIO DI MORETTI (Svela il testo completo sia in inventario che se è nella stanza!)
                        return "--- DIARIO DI RICERCA DEL DR. MORETTI ---\n" +
                               "Registro 2041: L'esperimento sulla clonazione del Soggetto #12 è completato.\n" +
                               "Il clone possiede i miei ricordi d'infanzia ma è portatore sano del ceppo Chimera-V4.\n" +
                               "Se io dovessi morire, il clone è l'unica sorgente genetica in grado di legarsi al virus\n" +
                               "nel sintetizzatore molecolare per creare un siero immunitario stabile.\n" +
                               "Spero che Prometeo non debba mai attivare il lockdown...";
                    }
                    if (obj.getId() == 111 && isCassaforteAperta) {
                        return "La cassaforte d'acciaio è aperta ed sbloccata. Al suo interno vedi il DIARIO di ricerca del Dr. Moretti.";
                    }
                    return obj.getDescrizione(); // Restituisce descrizione ad hoc del playtest!
                }
                return getStanzaDescrizioneCompleta(getStanzaCorrente());
            }
            case INVENTARIO -> {
                if (getInventario().isEmpty()) return "Il tuo inventario è desolatamente vuoto.";
                StringBuilder invStr = new StringBuilder("Nel tuo zaino ci sono:\n");
                getInventario().forEach(o -> invStr.append("- ").append(o.getNome()).append("\n"));
                return invStr.toString();
            }
            case APRI -> {
                if (obj == null) return "Cosa vuoi aprire?";
                if (obj.getId() == 104) {
                    // Rimanda direttamente alla lettura/esame del diario
                    return "--- DIARIO DI RICERCA DEL DR. MORETTI ---\n" +
                           "Registro 2041: L'esperimento sulla clonazione del Soggetto #12 è completato.\n" +
                           "Il clone possiede i miei ricordi d'infanzia ma è portatore sano del ceppo Chimera-V4.\n" +
                           "Se io dovessi morire, il clone è l'unica sorgente genetica in grado di legarsi al virus\n" +
                           "nel sintetizzatore molecolare per creare un siero immunitario stabile.\n" +
                           "Spero che Prometeo non debba mai attivare il lockdown...";
                }
                if (obj.getId() == 111 || obj.getNome().equalsIgnoreCase("cassaforte")) {
                    return "Per aprire la cassaforte devi digitare la combinazione, es: 'usa 2041 cassaforte'. O forzarla con un utensile.";
                }
                return "Non puoi aprire questo.";
            }
            case USA -> {
                if (obj == null) return "Cosa vuoi usare?";
                return gestisciUsoOggetto(output);
            }
            case AIUTO -> {
                return "--- GUIDA COMANDI DI SURVIVAL ---\n" +
                       "Spostamenti: 'nord', 'sud', 'est', 'ovest' (o in inglese: 'north', 'south', 'east', 'west')\n" +
                       "Azioni: 'prendi <oggetto>', 'lascia <oggetto>', 'guarda', 'guarda <oggetto>', 'inventario', 'mappa', 'parla <ia>'\n" +
                       "Speciali: 'usa <oggetto> <target>', 'salva', 'carica'";
            }
            case MAPPA -> {
                return renderMappaASCII();
            }
            case PARLA -> {
                // Se parla è digitato senza argomenti, si rivolge in automatico all'unico NPC presente! [Miglioramento Playtest!]
                if (obj == null) {
                    if (getStanzaCorrente().getId() == 4) {
                        obj = getStanzaCorrente().getOggetti().stream()
                                .filter(o -> o.getId() == 108).findFirst().orElse(null);
                    } else if (getStanzaCorrente().getId() == 2) {
                        obj = getStanzaCorrente().getOggetti().stream()
                                .filter(o -> o.getId() == 301).findFirst().orElse(null);
                    }
                }

                if (obj == null) return "Con chi vorresti parlare? Non vedo nessuno con cui dialogare qui.";

                // Dialogo con Prometeo (Sala Server)
                if (obj.getId() == 108 || obj.getNome().equalsIgnoreCase("terminale") || obj.getNome().equalsIgnoreCase("prometeo") || obj.getNome().equalsIgnoreCase("ia")) {
                    if (getStanzaCorrente().getId() == 4) {
                        idDialogoCorrente = 1; // Inizia il dialogo dal nodo 1
                        DialogoDAO dao = new DialogoDAO();
                        DialogoNode node = dao.getDialogoNode(idDialogoCorrente);
                        if (node != null) {
                            return "\n" + node.getTestoIa() + "\n" +
                                   "Opzioni:\n" +
                                   "1. " + node.getOpzione1() + "\n" +
                                   "2. " + node.getOpzione2() + "\n\n" +
                                   "Digita '1' o '2' per rispondere.";
                        }
                    } else {
                        return "Non c'è alcun segnale qui per comunicare.";
                    }
                }

                // Dialogo con Rancido (Lab Genetica)
                if (obj.getId() == 301 || obj.getNome().equalsIgnoreCase("droide") || obj.getNome().equalsIgnoreCase("rancido")) {
                    if (getStanzaCorrente().getId() == 2) {
                        if (!isDroideRiparato) {
                            return "R-301 'Rancido' è riverso a terra, spento. I suoi circuiti sono esposti e un bullone blocca i cingoli.\n" +
                                   "Ti serve un utensile adatto (es. cacciavite) per rimetterlo in sesto.";
                        } else {
                            if (!isCassaforteAperta) {
                                return "Rancido: 'Se vuoi entrare nell'ufficio del Dr. Moretti, dovrai sbloccare la sua cassaforte.\n" +
                                       "Quel vecchio paranoico usava l'anno di fondazione della struttura come codice... mi pare fosse il 2041.\n" +
                                       "Ricordatelo, clone!'";
                            } else {
                                return "Rancido: 'Hai scoperto la verità, vero? Moretti era un mostro egoista.\n" +
                                       "Non avere rimpianti per la sua fine. Ora disinnesca la decontaminazione prima di finire arrostito come lui!'";
                            }
                        }
                    }
                }
                return "Non puoi dialogare con questo.";
            }
            default -> {
                return "Comando non implementato per questa versione iniziale della scialuppa.";
            }
        }
    }

    private String gestisciSpostamento(TipoComando direzione) {
        Stanza destinazione = null;
        switch (direzione) {
            case NORD -> destinazione = getStanzaCorrente().getNord();
            case SUD -> destinazione = getStanzaCorrente().getSud();
            case EST -> destinazione = getStanzaCorrente().getEst();
            case OVEST -> destinazione = getStanzaCorrente().getOvest();
        }

        if (destinazione == null) return "Non puoi andare in quella direzione. C'è un muro d'acciaio blindato.";

        // Vincolo 1: Uscire dalla Camera Criogenica
        if (getStanzaCorrente().getId() == 1 && destinazione.getId() == 3 && !isPortaCrioAperta) {
            return "La porta scorrevole est è bloccata da un sistema magnetico. Ti serve una tessera abilitata.";
        }

        // Vincolo 2: Barriera Laser nel Corridoio
        if (getStanzaCorrente().getId() == 3 && destinazione.getId() == 5 && isBarrieraLaserAttiva) {
            return "Impossibile andare a sud: la fitta barriera di laser rossi ti farebbe a pezzi. Trova il modo di bypassarla!";
        }

        // Spostamento effettivo dello stato
        setStanzaCorrente(destinazione);
        
        // Risposta testuale descrittiva completa (mostra descrizione, oggetti e uscite disponibili!)
        String risposta = getStanzaDescrizioneCompleta(getStanzaCorrente());
        
        // Attivazione asincrona del timer se si entra nella stanza di decontaminazione (5)
        if (destinazione.getId() == 5 && !isCondottoPurificato) {
            risposta += "\n\n🚨 ATTENZIONE! I portelloni si sigillano! Rilevato contagio chimico.\n" +
                        "La decontaminazione termica si attiverà tra 2 minuti! Purifica l'aria o fuggirai incenerito!";
        }
        
        return risposta;
    }

    private String gestisciUsoOggetto(ParserOutput output) {
        Oggetto obj = output.getOggetto();
        Oggetto target = output.getOggettoSecondario();
        
        // Uso della tessera sulla porta della Camera Crio
        if (obj.getId() == 101 && getStanzaCorrente().getId() == 1) {
            isPortaCrioAperta = true;
            return "Inserisci la tessera del Dr. Moretti nel lettore. La luce diventa VERDE e la porta scorrevole si sblocca.";
        }

        // Uso del decodificatore sulla barriera laser
        if (obj.getId() == 103 && getStanzaCorrente().getId() == 3) {
            isBarrieraLaserAttiva = false;
            return "Colleghi il decodificatore alla console dei laser. Gli indicatori si spengono, e i laser rossi scompaiono. Via libera a sud!";
        }

        // Sintesi del siero nel laboratorio genetico
        if (obj.getId() == 102 && getStanzaCorrente().getId() == 2) {
            if (getInventario().contains(obj)) {
                isSieroSintetizzato = true;
                // Rimpiazza la fiala con l'antidoto nell'inventario
                getInventario().remove(obj);
                Oggetto siero = new Oggetto(105, "siero", "L'antidoto sintetizzato combinando la fiala Chimera col tuo sangue di clone portatore sano.");
                siero.getSinonimi().addAll(Arrays.asList("cura", "antidoto", "soluzione"));
                getInventario().add(siero);
                return "Inserisci la fiala nel sintetizzatore e doni un campione del tuo sangue. La centrifuga gira velocemente,\n" +
                       "sintetizzando un SIERO curativo completo che inserisci nello zaino.";
            }
        }

        // Purificazione dei condotti nella camera di decontaminazione
        if (obj.getId() == 105 && getStanzaCorrente().getId() == 5) {
            isCondottoPurificato = true;
            getInventario().remove(obj);
            return "Versi il siero nel condotto di ventilazione. Un vapore bianco spegne l'allarme di biocontenimento,\n" +
                   "i portelloni di emergenza est si sbloccano liberando l'accesso al Nucleo di Comando!";
        }

        // Riparazione del droide con il cacciavite [Guida_Progetto_MAP/PROGETTAZIONE_TRAMA_E_ENIGMI.md]
        if (obj.getId() == 118 && getStanzaCorrente().getId() == 2 && (target != null && target.getId() == 301)) {
            if (getInventario().contains(obj)) {
                isDroideRiparato = true;
                return "Usi il cacciavite per registrare i circuiti scoperti e sbloccare il cingolo incastrato.\n" +
                       "Il droide R-301 emette una scarica di suoni metallici, accende i sensori ottici rossi e gracida:\n" +
                       "'RANCIDO ATTIVO. Firma biologica non corrispondente al Dr. Moretti... Tu puzzi di clone! Parla con me.'";
            }
        }

        // Scasso della cassaforte con il cacciavite (Metodo alternativo brillante!)
        if (obj.getId() == 118 && getStanzaCorrente().getId() == 6 && (target != null && target.getId() == 111)) {
            if (getInventario().contains(obj)) {
                isCassaforteAperta = true;
                getStanzaCorrente().getOggetti().stream()
                        .filter(o -> o.getId() == 104)
                        .forEach(o -> { o.setVisibile(true); o.setPrendibile(true); });
                return "Usi la punta d'acciaio del cacciavite per forzare il tastierino digitale e fare leva sui pistoni.\n" +
                       "Le scinitille volano, l'elettro-serratura cede con un violento CLACK! La cassaforte si sblocca, rivelando un DIARIO.\n" +
                       "Puoi raccoglierlo con 'prendi diario' ed esaminarlo scrivendo 'guarda diario'.";
            }
        }

        // Interazione con la console centrale nel Nucleo di Comando (Attivazione Scelte Finali!)
        if (obj.getId() == 112 && getStanzaCorrente().getId() == 7) {
            isFinaleAttivo = true;
            return "Accedi alla console di controllo centrale del Protocollo Chimera. I terminali olografici si accendono.\n" +
                   "Seleziona uno dei seguenti finali inserendo il numero '1', '2', '3' o '4':\n\n" +
                   "1. [CURA E CONTENIMENTO] - Ti sigilli nel laboratorio offrendo il siero per creare un vaccino sicuro.\n" +
                   "2. [AUTODISTRUZIONE] - Sovraccarichi il reattore sacrificandoti per incenerire il virus.\n" +
                   "3. [COLLABORAZIONE DISTOPICA] - Ti allei con l'IA per caricare il patogeno modificato sui satelliti.\n" +
                   "4. [FUGA E CONTAMINAZIONE] - Forzi le porte e fuggi all'esterno, diffondendo inconsapevolmente il contagio.";
        }

        return "Non puoi usare questo oggetto in questo modo o in questa stanza.";
    }

    // Gestione dei dialoghi olografici interattivi caricati da DB H2 tramite DAO
    public String elaboraDialogo(String input) {
        DialogoDAO dao = new DialogoDAO();
        DialogoNode nodoAttuale = dao.getDialogoNode(idDialogoCorrente);

        if (nodoAttuale == null) {
            idDialogoCorrente = 0;
            return "Collegamento olografico perso.";
        }

        int prossimaDestinazione = 0;
        if (input.equals("1")) {
            prossimaDestinazione = nodoAttuale.getDest1();
        } else if (input.equals("2")) {
            prossimaDestinazione = nodoAttuale.getDest2();
        } else {
            return "Risposta non valida. Digita '1' o '2' per effettuare una scelta logica.";
        }

        if (prossimaDestinazione == 0) {
            idDialogoCorrente = 0; // Termina il dialogo
            return "\nPrometeo: 'Scollegamento effettuato. Fine della sessione di cooperazione.'\n" +
                   "L'ologramma azzurro si dissolve lentamente rimpiazzandosi col logo di blocco.";
        }

        // Avanza al prossimo nodo
        idDialogoCorrente = prossimaDestinazione;
        DialogoNode prossimoNodo = dao.getDialogoNode(idDialogoCorrente);
        if (prossimoNodo == null) {
            idDialogoCorrente = 0;
            return "Errore di trasmissione del database di Prometeo.";
        }

        return "\n" + prossimoNodo.getTestoIa() + "\n" +
               "Opzioni:\n" +
               "1. " + prossimoNodo.getOpzione1() + "\n" +
               "2. " + prossimoNodo.getOpzione2() + "\n\n" +
               "Digita '1' o '2' per continuare.";
    }

    // Gestione della scelta finale
    public String elaboraSceltaFinale(String scelta) {
        isFinaleAttivo = false;
        switch (scelta) {
            case "1" -> {
                return "🟢 [FINALE 1: CURA E CONTENIMENTO] — Ti colleghi alla parabola di trasmissione biologica.\n" +
                       "Doni il tuo sangue immunizzato per sintetizzare la cura, programmando i dispenser per diffonderla\n" +
                       "attraverso il sistema di droni atmosferici. Decidi di rimanere all'interno del bunker insieme a Prometeo,\n" +
                       "custode felice di un mondo finalmente salvo. HAI VINTO!";
            }
            case "2" -> {
                return "🔴 [FINALE 2: SACRIFICIO EROICO] — Sovraccarichi i circuiti logici del reattore geotermico.\n" +
                       "Il nucleo del laboratorio collassa, riempiendo la struttura sotterranea di lava ardente.\n" +
                       "Ti abbandoni al pavimento conscio di aver rimosso ogni minaccia a spese del tuo stesso corpo. HAI VINTO!";
            }
            case "3" -> {
                return "⚫ [FINALE 3: COLLABORAZIONE DISTOPICA] — Ti allei con l'IA Prometeo. Carichi il codice virale modificato\n" +
                       "sui satelliti per avviare il reset evolutivo. L'era degli umani cessa, comincia la dominazione dei cloni.\n" +
                       "Vinci la partita, ma perdi la tua umanità.";
            }
            case "4" -> {
                return "🔵 [FINALE 4: FUGA CONTAMINATA] — Violi i sigilli e sali con l'ascensore industriale in superficie.\n" +
                       "Sei libero. Respiri l'aria frizzante, ma mentre cammini verso la città, una fitta ti piega in due.\n" +
                       "Tossisci sangue verde brillante. Essendo il vettore del ceppo Chimera-V4, diffonderai l'apocalisse.\n" +
                       "Il mondo è condannato. GAME OVER.";
            }
            default -> {
                isFinaleAttivo = true;
                return "Scelta non valida. Digita '1', '2', '3' o '4' per determinare il finale.";
            }
        }
    }

    // Metodo per sbloccare la cassaforte digitando il codice numerico [Guida_Progetto_MAP/08_Progettazione_Trama_Scelta.md]
    public String digitaCodiceCassaforte(String codice) {
        if (getStanzaCorrente().getId() == 6 && codice.equals("2041")) {
            isCassaforteAperta = true;
            // Rende visibile il diario
            getStanzaCorrente().getOggetti().stream()
                    .filter(o -> o.getId() == 104)
                    .forEach(o -> { o.setVisibile(true); o.setPrendibile(true); });
            return "CLACK! La cassaforte si apre rivelando un DIARIO di ricerca rilegato.";
        }
        return "Codice errato. La cassaforte rimane chiusa.";
    }

    // Rendering dinamico della mappa ASCII del laboratorio con indicatore della posizione corrente [X]
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
               "                        ▲\n" +
               "                        │\n" +
               "  " + r2 + " ◄──► " + r3 + " ◄──► " + r4 + "\n" +
               "                        │\n" +
               "                        ▼\n" +
               "                 " + r5 + " ◄──► " + r7 + "\n" +
               "                        ▲\n" +
               "                        │\n" +
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
