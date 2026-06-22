# Classe `LaMiaAvventura` spiegata per intero

File:

```text
src/main/java/com/mycompany/avventuratestuale/impl/LaMiaAvventura.java
```

`LaMiaAvventura` è la classe più importante del progetto dal punto di vista del gameplay. È l'implementazione concreta dell'avventura **Trapped Virus**.

Questa classe contiene:

- stanze;
- oggetti;
- collegamenti della mappa;
- flag degli enigmi;
- dialoghi;
- uso degli oggetti;
- spostamenti;
- finali;
- stato del timer serializzabile.

---

# 1. Package e import

```java
package com.mycompany.avventuratestuale.impl;
```

La classe sta nel package `impl`, cioè dove si trova l'implementazione concreta del gioco.

Import principali:

```java
import com.mycompany.avventuratestuale.core.*;
import com.mycompany.avventuratestuale.core.commands.*;
import com.mycompany.avventuratestuale.model.Stanza;
import com.mycompany.avventuratestuale.model.Oggetto;
import com.mycompany.avventuratestuale.database.DialogoNode;
import com.mycompany.avventuratestuale.database.DialogoDAO;
```

## Perché ci sono questi import

- `core.*`: importa `Gioco`, `ParserOutput`, `TipoComando`, `Command`, ecc.
- `core.commands.*`: importa tutte le classi comando in italiano.
- `Stanza` e `Oggetto`: servono per costruire il mondo di gioco.
- `DialogoDAO` e `DialogoNode`: servono per i dialoghi con Prometeo caricati da database.

---

# 2. Dichiarazione della classe

```java
public class LaMiaAvventura extends Gioco {
```

## Cosa significa `extends Gioco`

`LaMiaAvventura` eredita da `Gioco`.

`Gioco` è una classe astratta che definisce lo stato comune di una avventura:

- stanza corrente;
- inventario;
- lista comandi.

`LaMiaAvventura` fornisce la logica specifica di **Trapped Virus**.

---

# 3. `serialVersionUID`

```java
private static final long serialVersionUID = 1L;
```

Serve per la serializzazione. La partita viene salvata su file, quindi `LaMiaAvventura` deve essere compatibile con `Serializable`, ereditato da `Gioco`.

---

# 4. `commandMap`

```java
private transient Map<TipoComando, Command> commandMap = new HashMap<>();
```

## Cosa contiene

Mappa un tipo di comando alla classe che lo esegue.

Esempio:

```text
PRENDI -> PrendiCommand
USA -> UsaCommand
GUARDA -> GuardaCommand
```

## Perché `transient`

`transient` significa che il campo non viene serializzato.

Il motivo è che le classi comando sono oggetti di logica, non stato di gioco. Dopo un caricamento vengono ricreate con `setupCommands()`.

---

# 5. Flag narrativi

Esempi:

```java
private boolean isBarrieraLaserAttiva = true;
private boolean isPortaCrioAperta = false;
private boolean isSieroSintetizzato = false;
private boolean isCondottoPurificato = false;
private boolean isCassaforteAperta = false;
private boolean isDroideRiparato = false;
```

## Cosa sono

Sono variabili booleane che indicano lo stato degli enigmi.

Esempi:

- `isPortaCrioAperta`: indica se la porta iniziale è stata sbloccata.
- `isBarrieraLaserAttiva`: indica se la barriera blocca ancora il passaggio.
- `isSieroSintetizzato`: indica se il siero è stato creato.
- `isCondottoPurificato`: indica se il gas è stato neutralizzato.

## Perché servono

Il mondo cambia in base alle azioni del giocatore.

Esempio:

```java
if (isBarrieraLaserAttiva) {
    blocca passaggio;
}
```

Dopo aver usato il decodificatore:

```java
isBarrieraLaserAttiva = false;
```

---

# 6. Stato timer serializzabile

```java
private boolean isTimerDecontaminazioneAttivo = false;
private int secondiDecontaminazioneRimanenti = 120;
private int tempoImpiegatoDecontaminazione = -1;
```

## Perché è qui

Il thread reale del timer non può essere salvato direttamente. Quindi si salvano solo i dati necessari per ricostruirlo:

- se era attivo;
- quanti secondi mancavano;
- quanto tempo era stato impiegato se il gas era stato neutralizzato.

Quando la partita viene caricata, `InterfacciaGioco` legge questi valori e ricostruisce il timer.

---

# 7. Costanti ID oggetti

Esempio:

```java
private static final int ID_TESSERA = 101;
private static final int ID_FIALA = 102;
private static final int ID_DECODIFICATORE = 103;
```

## Perché usare ID

Gli ID permettono di riconoscere gli oggetti in modo sicuro, indipendentemente dal nome.

Esempio:

```java
if (obj.getId() == ID_TESSERA) {
    ...
}
```

Questo è più robusto di confrontare stringhe come:

```java
obj.getNome().equals("tessera")
```

---

# 8. Metodo `inizializza`

```java
public void inizializza() throws Exception
```

È il metodo che costruisce tutta l'avventura.

Fa principalmente quattro cose:

1. registra i comandi riconoscibili dal parser;
2. crea le stanze;
3. collega le stanze;
4. crea e posiziona gli oggetti.

---

## 8.1 Registrazione comandi

Esempio:

```java
getComandi().add(new Comando(TipoComando.PRENDI,
    new HashSet<>(Arrays.asList("prendi", "raccogli", "afferra", "get", "take", "grab"))));
```

Questo dice al parser che tutte quelle parole corrispondono a `PRENDI`.

## Sintassi

### `new Comando(...)`

Crea un nuovo oggetto `Comando`.

### `new HashSet<>(Arrays.asList(...))`

Crea un insieme di sinonimi partendo da una lista.

### `getComandi().add(...)`

Aggiunge il comando alla lista dei comandi conosciuti.

---

## 8.2 Creazione stanze

Esempio:

```java
Stanza cameraCrio = new Stanza(1, "Camera Criogenica", "descrizione...");
```

Ogni stanza ha:

- id;
- nome;
- descrizione.

---

## 8.3 Collegamento stanze

Esempio:

```java
cameraCrio.setEst(corridoio);
cameraCrio.setSud(labGenetica);
```

Significa:

- dalla Camera Criogenica andando a est si arriva al Corridoio;
- andando a sud si arriva al Laboratorio.

I collegamenti costruiscono la mappa.

---

## 8.4 Creazione oggetti

Esempio:

```java
Oggetto tessera = new Oggetto(ID_TESSERA, "tessera", "descrizione...");
tessera.getSinonimi().addAll(Arrays.asList("badge", "chiavetta", "card"));
cameraCrio.aggiungiOggetto(tessera);
```

Si crea l'oggetto, si aggiungono sinonimi e poi lo si inserisce in una stanza.

---

# 9. Metodo `descrizioneOggetto`

```java
public String descrizioneOggetto(Oggetto o)
```

Restituisce la descrizione di un oggetto.

Alcune descrizioni sono dinamiche.

Esempio:

```java
if (id == ID_PORTA) {
    return isPortaCrioAperta
        ? "La porta blindata e' aperta..."
        : "La porta blindata ... e' ROSSA: bloccata.";
}
```

## Operatore ternario

```java
condizione ? valoreSeVero : valoreSeFalso
```

Se la porta è aperta, restituisce una descrizione; altrimenti un'altra.

---

# 10. Metodo `getStanzaDescrizioneCompleta`

```java
public String getStanzaDescrizioneCompleta(Stanza stanza)
```

Costruisce il testo completo di una stanza:

- descrizione narrativa;
- oggetti utili visibili;
- elementi osservabili;
- uscite disponibili.

Usa `StringBuilder` per costruire una stringa lunga in modo efficiente.

---

## 10.1 Oggetti raccoglibili

```java
stanza.getOggetti().stream()
    .filter(o -> o.isVisibile() && o.isPrendibile())
    .collect(Collectors.toList())
```

Cerca oggetti visibili e prendibili.

---

## 10.2 Oggetti osservabili

```java
.filter(o -> o.isVisibile() && !o.isPrendibile())
```

Cerca elementi scenici visibili ma non prendibili.

Questo permette di mostrare:

```text
Elementi osservabili (usa 'guarda <nome>')
```

---

# 11. Metodo `descrizioneStanza`

```java
private String descrizioneStanza(Stanza stanza)
```

Restituisce la descrizione base della stanza, ma può modificarla in base allo stato del gioco.

Esempio:

```java
if (stanza.getId() == 3) {
    if (!isBarrieraLaserAttiva) {
        base = base.replace(...);
    }
}
```

Se la barriera è disattivata, la descrizione del corridoio cambia.

---

# 12. Metodo `setupCommands`

```java
public void setupCommands()
```

Collega ogni `TipoComando` alla classe che lo esegue.

Esempio:

```java
commandMap.put(TipoComando.PRENDI, new PrendiCommand());
commandMap.put(TipoComando.USA, new UsaCommand());
```

È il cuore del Command Pattern.

---

# 13. Metodo `caricaStatoDa`

```java
public void caricaStatoDa(LaMiaAvventura altra)
```

Copia lo stato di una partita caricata dentro l'istanza attualmente usata dalla GUI.

Serve perché la GUI mantiene un riferimento all'oggetto `gioco`. Non basta creare un nuovo oggetto caricato: bisogna copiare i dati dentro l'oggetto attivo.

Copia:

- stanza corrente;
- inventario;
- flag;
- timer;
- dialoghi;
- command map.

---

# 14. Metodo `elaboraComando`

```java
public String elaboraComando(ParserOutput output)
```

Prende il risultato del parser e trova il comando da eseguire.

Passaggi:

1. legge il tipo comando;
2. cerca la classe comando nella `commandMap`;
3. esegue `cmd.execute(this, output)`;
4. restituisce la risposta.

---

# 15. Metodo `elaboraComandoTalk`

```java
public String elaboraComandoTalk(ParserOutput output)
```

Gestisce il comando `parla`.

Può avviare dialoghi con:

- Prometeo;
- Rancido.

Se sei nella Sala Server e scrivi:

```text
parla prometeo
```

parte il dialogo con Prometeo.

Se sei nel Laboratorio e Rancido è riparato:

```text
parla rancido
```

parte il menu di Rancido.

---

# 16. Dialogo Rancido

Metodi coinvolti:

```java
menuRancido(String preambolo)
elaboraDialogoRancido(String input)
rivelaDecodificatoreDaRancido()
```

## `menuRancido`

Restituisce un menu testuale con opzioni numeriche.

## `elaboraDialogoRancido`

Legge la scelta dell'utente e restituisce una risposta.

## `rivelaDecodificatoreDaRancido`

Rende visibile e prendibile il decodificatore nascosto nel laboratorio.

---

# 17. Ricerca stanza ricorsiva

```java
private Stanza cercaStanzaR(Stanza s, int id, Set<Stanza> visitate)
```

È un metodo ricorsivo che visita la mappa per trovare una stanza con un certo ID.

## Perché serve `visitate`

La mappa è un grafo con collegamenti bidirezionali. Senza `visitate`, la ricerca potrebbe girare all'infinito.

Esempio:

```text
Camera -> Corridoio -> Camera -> Corridoio -> ...
```

Il set `visitate` evita di visitare due volte la stessa stanza.

---

# 18. Metodo `gestisciSpostamento`

```java
public String gestisciSpostamento(TipoComando direzione)
```

Gestisce il movimento.

Passaggi:

1. determina la stanza destinazione;
2. controlla se la destinazione esiste;
3. controlla blocchi narrativi;
4. aggiorna la stanza corrente;
5. restituisce la descrizione della nuova stanza.

Blocchi principali:

- porta criogenica chiusa;
- barriera laser attiva;
- Nucleo bloccato finché il gas non è neutralizzato.

---

# 19. Metodo `gestisciUsoOggetto`

```java
public String gestisciUsoOggetto(ParserOutput output)
```

È uno dei metodi più importanti.

Gestisce:

- `usa tessera`;
- `usa decodificatore`;
- `usa fiala`;
- `usa siero`;
- `usa cacciavite droide`;
- `usa console`.

Ogni blocco controlla:

- oggetto usato;
- stanza corrente;
- inventario;
- eventuale target;
- flag già attivati.

---

# 20. Metodo `elaboraDialogo`

```java
public String elaboraDialogo(String input)
```

Gestisce dialoghi attivi.

Se è attivo Rancido, delega a:

```java
elaboraDialogoRancido(input)
```

Altrimenti gestisce Prometeo leggendo i nodi dal database tramite `DialogoDAO`.

---

# 21. Metodo `mostraNodoDialogo`

```java
public String mostraNodoDialogo(int nodoId)
```

Carica un nodo dal database e costruisce il testo:

```text
Prometeo parla
Opzioni:
1. ...
2. ...
```

---

# 22. Metodo `elaboraSceltaFinale`

```java
public String elaboraSceltaFinale(String scelta)
```

Gestisce la scelta finale nel Nucleo.

Scelte:

```text
1 = cura e contenimento
2 = autodistruzione
3 = fuga contaminata
0 = torna indietro
```

Se la scelta non è valida, mantiene la console attiva.

---

# 23. Metodo `digitaCodiceCassaforte`

```java
public String digitaCodiceCassaforte(String codice)
```

Se il codice è `2041`, apre la cassaforte e rende visibili:

- diario;
- fiala.

Questo è uno degli enigmi principali.

---

# 24. Metodo `renderMappaASCII`

```java
public String renderMappaASCII()
```

Costruisce una mappa testuale del laboratorio e segna la posizione corrente con `[X]`.

---

# 25. Metodo `aggiornaStatoTimerDecontaminazione`

```java
public void aggiornaStatoTimerDecontaminazione(boolean attivo, int secondiRimanenti, int tempoImpiegato)
```

Aggiorna lo stato serializzabile del timer.

Serve per salvare e caricare correttamente una partita durante o dopo la decontaminazione.

---

# 26. Getter finali

Esempi:

```java
public boolean isDialogoAttivo()
public boolean isFinaleAttivo()
public boolean isCondottoPurificato()
```

Sono usati dalla GUI per sapere:

- se deve gestire un dialogo;
- se deve gestire la scelta finale;
- se deve fermare il timer;
- se la partita ha superato certi enigmi.

---

# 27. Argomenti MAP coperti

`LaMiaAvventura` copre:

- ereditarietà;
- classi astratte;
- incapsulamento;
- composizione;
- polimorfismo;
- collection;
- stream;
- lambda;
- ricorsione;
- gestione stato;
- serializzazione;
- Command Pattern;
- database tramite DAO;
- gameplay a stati.
