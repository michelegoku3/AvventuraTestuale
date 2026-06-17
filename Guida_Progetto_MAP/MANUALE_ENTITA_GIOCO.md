# Manuale delle Entità di Gioco — "Protocollo Chimera"
### Catalogo Tecnico di Stanze, Oggetti Raccoglibili, Oggetti Scenici (Scenery) e Personaggi (NPC)

Questo documento elenca in modo sistematico e definitivo tutte le entità logiche e descrittive che compongono l'avventura testuale. È progettato per garantire un'immersione di gioco perfetta, risolvendo ogni possibile incongruenza nell'esame degli elementi descrittivi dello scenario `[Lab 1 - Introduzione.pdf]`.

---

## 1. Planimetria e Specifiche delle Stanze (Le 7 Locazioni)

Le stanze sono collegate a grafo tramite riferimenti geografici diretti (Composizione) `[Lezioni/2 - Paradigma OO.pdf, Slide 45]`.

### Stanza 1: Camera Criogenica (ID: 1)
* **Descrizione**: *"L'aria è gelida e satura di vapori chimici. Intorno a te ci sono tre capsule criogeniche inattive, tranne la tua, che emette scintille dal pannello dei circuiti. Una spessa porta metallica a est, chiusa ermeticamente, è l'unica via d'uscita. Sul pannello di controllo della porta pulsa una luce rossa."*
* **Uscite**: Est ➔ Corridoio (ID: 3, bloccato da porta magnetica), Sud ➔ Lab Genetica (ID: 2).
* **Oggetti associati**: `tessera` (raccoglibile), `capsula` (scenico), `porta` (scenico), `pannello` (scenico).

### Stanza 2: Laboratorio di Genetica (ID: 2)
* **Descrizione**: *"I banconi da lavoro sono ricoperti di vetreria da laboratorio in frantumi. Un macchinario per la sintesi molecolare emette un ronzio sommesso a ovest. Al centro della stanza, un enorme silos di vetro contiene un liquido amniotico scuro, ormai vuoto."*
* **Uscite**: Nord ➔ Camera Criogenica (ID: 1).
* **Oggetti associati**: `fiala` (raccoglibile), `silos` (scenico), `macchinario` (scenico), `droide` (NPC).

### Stanza 3: Corridoio di Servizio (ID: 3)
* **Descrizione**: *"Un lungo corridoio illuminato da luci d'emergenza arancioni. A ovest la porta conduce alla Camera Criogenica. A est vedi la Sala Server. A sud, una fitta barriera di laser rossi sbarra il cammino."*
* **Uscite**: Ovest ➔ Camera Criogenica (ID: 1), Est ➔ Sala Server (ID: 4), Nord ➔ Ufficio Direttore (ID: 6), Sud ➔ Camera Decontaminazione (ID: 5, bloccato da barriera laser).
* **Oggetti associati**: `barriera` (scenico), `luci` (scenico).

### Stanza 4: Sala Server (ID: 4)
* **Descrizione**: *"Il rumore delle ventole di raffreddamento è assordante. Migliaia di server rack si estendono su più file. Al centro della stanza pulsa un terminale olografico nero: è l'interfaccia centrale dell'IA Prometeo."*
* **Uscite**: Ovest ➔ Corridoio (ID: 3).
* **Oggetti associati**: `decodificatore` (raccoglibile), `cacciavite` (raccoglibile), `server` (scenico), `terminale` (NPC).

### Stanza 5: Camera di Decontaminazione (ID: 5)
* **Descrizione**: *"Una stanza asettica con spessi oblò di vetro blindato che si affacciano sul nucleo. L'aria qui ha un odore chimico pungente. Sulla parete vedi un condotto di ventilazione e una console d'emergenza."*
* **Uscite**: Nord ➔ Corridoio (ID: 3), Est ➔ Nucleo Comando (ID: 7, bloccato da blocco termico).
* **Oggetti associati**: `condotto` (scenico), `console` (scenico), `oblò` (scenico).

### Stanza 6: Ufficio del Direttore (ID: 6)
* **Descrizione**: *"Un ufficio lussuoso. C'è una grande scrivania in mogano, una libreria vuota e una cassaforte blindata a combinazione digitale in un angolo."*
* **Uscite**: Sud ➔ Corridoio (ID: 3).
* **Oggetti associati**: `diario` (raccoglibile, nascosto), `cassaforte` (scenico), `ritratto` (scenico), `scrivania` (scenico), `libreria` (scenico).

### Stanza 7: Nucleo di Comando (ID: 7)
* **Descrizione**: *"Un'enorme camera circolare. Una colossale vetrata si affaccia sul reattore geotermico sotterraneo. Al centro svetta la console di comando principale, da cui decidere il destino del laboratorio."*
* **Uscite**: Ovest ➔ Camera Decontaminazione (ID: 5).
* **Oggetti associati**: `vetrata` (scenico), `console_centrale` (scenico), `reattore` (scenico).

---

## 2. Catalogo degli Oggetti Raccoglibili (Inventory Items)

Questi oggetti possono essere raccolti, lasciati, esaminati e usati su determinati bersagli (target):

### 1. `tessera` (ID: 101)
* **Sinonimi**: badge, chiavetta, tessera magnetica, card.
* **Descrizione**: *"Un badge in plastica del Dr. Moretti con chip a induzione magnetica. Presenta una spia magnetica integrata."*
* **Uso**: Usato sul `pannello` (ID: 121) della Camera Criogenica sblocca l'accesso a est.

### 2. `fiala` (ID: 102)
* **Sinonimi**: provetta, patogeno, chimera, fiala chimica.
* **Descrizione**: *"Una fiala sigillata a tenuta stagna. All'interno galleggia una sostanza fosforescente verde contenente il virus Chimera-V4."*
* **Uso**: Usato sul `macchinario` (ID: 114) del Laboratorio Genetico avvia la sintesi dell'antidoto.

### 3. `decodificatore` (ID: 103)
* **Sinonimi**: dispositivo, bypass, hacking, decodificatore.
* **Descrizione**: *"Un dispositivo portatile militare con terminale a cristalli liquidi per decifrare barriere di sicurezza laser."*
* **Uso**: Usato sulla `barriera` (ID: 124) del Corridoio disattiva i laser rossi a sud.

### 4. `diario` (ID: 104)
* **Sinonimi**: registri, appunti, libro, diario del direttore.
* **Descrizione**: *"Un diario rilegato contenente gli appunti segreti del Dr. Moretti. Svela che tu sei il clone 'Soggetto #12'."*
* **Uso**: Fornisce elementi informativi cruciali per comprendere il twist narrativo ed il codice di sicurezza della cassaforte.

### 5. `siero` (ID: 105)
* **Sinonimi**: cura, antidoto, soluzione.
* **Descrizione**: *"Una fiala spessa di siero biologico trasparente sintetizzato a partire dai tuoi cloni immunologici sani."*
* **Uso**: Usato sul `condotto` (ID: 109) della camera di decontaminazione blocca il timer di morte e sblocca l'est.

### 6. `cacciavite` (ID: 118)
* **Sinonimi**: utensile, attrezzo, giravite.
* **Descrizione**: *"Un cacciavite da officina con punta a stella e manico isolato in gomma."*
* **Uso**: Usato sul `droide` (ID: 301) lo riaccende e ripara. Usato sulla `cassaforte` (ID: 111) sblocca la cassaforte senza inserire la combinazione.

---

## 3. Catalogo degli Oggetti Scenici / Statici (Scenery Items)

Questi oggetti **non sono prendibili** (`setPrendibile(false)`) e servono a garantire una perfetta interazione d'atmosfera quando esaminati con `guarda <oggetto>` o se usati in modo errato:

### 1. `capsula` (ID: 106)
* **Sinonimi**: capsule, criocamera, vasca.
* **Descrizione**: *"La vasca criogenica in cui sei rimasto ibernato. Il pannello mostra la scritta incisa 'SOGGETTO CHIMERA #12'."*

### 2. `porta` (ID: 120)
* **Sinonimi**: portone, metallo, uscita.
* **Descrizione**: *"Una spessa porta blindata scorrevole in lega di titanio. È chiusa e sbarrata da pistoni idraulici."*

### 3. `pannello` (ID: 121)
* **Sinonimi**: lettore, luce, pulsante, schermo, monitor.
* **Descrizione**: *"Un lettore di badge magnetico montato sulla parete accanto alla porta blindata. Ha una spia rossa fissa."*

### 4. `silos` (ID: 113)
* **Sinonimi**: silus, contenitore, vetro, liquido, amniotico.
* **Descrizione**: *"Un enorme silos di vetro alto tre metri riempito di liquido amniotico denso e scuro, usato per la coltura dei cloni."*

### 5. `macchinario` (ID: 114)
* **Sinonimi**: macchina, centrifuga, sintetizzatore.
* **Descrizione**: *"Un macchinario di sintesi bio-molecolare molecolare. Presenta un vassoio di inserimento per fiale chimiche."*

### 6. `server` (ID: 115)
* **Sinonimi**: armadi, elaboratori, rack, calcolatore, calcolatori, ventole.
* **Descrizione**: *"File interminabili di server rack che elaborano dati ad altissima velocità, emettendo un calore opprimente."*

### 7. `barriera` (ID: 124)
* **Sinonimi**: laser, barriere, grata.
* **Descrizione**: *"Una fitta cortina di raggi laser rossi ad alta energia che sbarrano la via a sud. Il calore che emanano è spaventoso."*

### 8. `scrivania` (ID: 122)
* **Sinonimi**: tavolo, mogano, legno.
* **Descrizione**: *"Una scrivania direttiva in pregiato mogano scuro. Sulla superficie noti polvere e vecchi faldoni vuoti."*

### 9. `libreria` (ID: 123)
* **Sinonimi**: scaffale, scaffali, libri.
* **Descrizione**: *"Una libreria in legno vuota. Rimangono solo impronte di vecchi faldoni rimossi e ragnatele negli angoli."*

### 10. `condotto` (ID: 109)
* **Sinonimi**: condotti, ventilazione, aerazione, grata.
* **Descrizione**: *"La grata di aspirazione della ventilazione forzata della stanza. L'aria calda viene spinta fuori attraverso di essa."*

---

## 4. Catalogo dei Personaggi (NPC)

I personaggi con cui il giocatore può interagire direttamente:

### 1. Prometeo (ID: 108)
* **Sinonimi**: ia, ai, ologramma, terminale, prometeo.
* **Descrizione**: *"Un ologramma rotante azzurro che proietta l'avatar stilizzato di un volto umano androgino."*
* **Ruolo**: Interlocutore relazionale della Sala Server. Carica i bivi di dialogo da Database H2.

### 2. R-301 "Rancido" (ID: 301)
* **Sinonimi**: droide, rancido, robot, robottino.
* **Descrizione**: *"Un piccolo droide di manutenzione cilindrico arrugginito con cingoli usurati, riverso in un angolo del laboratorio."*
* **Ruolo**: NPC comprimario. Può essere riattivato con il cacciavite.
