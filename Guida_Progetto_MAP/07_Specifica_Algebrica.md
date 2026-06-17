# Capitolo 7: Specifica Algebrica (Non Assiomatica)
### Obiettivo: Progettare la specifica formale dell'astrazione dati "Inventario" conformemente alle lezioni teoriche sui paradigmi di programmazione e di astrazione dati.

La sezione **Specifica Algebrica** del template di relazione tecnica richiede esplicitamente `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 2]`:
> *"Fornire una specifica algebrica di una struttura dati a scelta tra quelle utilizzate nel progetto. Deve essere fornita una specifica algebrica non assiomatica!"*

Questo capitolo affronta il criterio 9 di valutazione (**Qualità della documentazione**) e risponde direttamente alla richiesta formale e formidabile di modellazione matematica richiesta dal docente `[MAP M-Z - Documentazione Progetto - Template.pdf, p. 3]`.

---

## 1. Fondamenti Teorici: Specifica Assiomatica vs. Specifica Algebrica
Nelle lezioni del corso (nel capitolo sull'astrazione dati), il Prof. Basile contrappone i due principali approcci formali per la specifica dei Tipi di Dato Astratto (ADT):

* **Specifica Assiomatica (Non richiesta per questa sezione!)**:
  Si basa sulla notazione delle asserzioni formali e consta di una segnatura sintattica (domini e operatori) e di una specifica semantica basata su **Precondizioni** (che definiscono quando l'operatore è applicabile) e **Postcondizioni** (che stabiliscono le relazioni tra argomenti e risultato nello stato d'arrivo) `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 68-69]`.
  * *Esempio di metafora slide*: *"Se hai il serbatoio pieno (Pre) e giri la chiave (Operazione), allora il motore si accende (Post)". È una visione imperativa e legata allo stato* `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 103]`.
* **Specifica Algebrica (Richiesta!)**:
  Non descrive gli stati intermedi o le precondizioni di esecuzione, ma tratta il Tipo Astratto di Dato come un sistema di equazioni matematiche:
  * *Esempio di metafora slide*: *"Non ci importa come è fatto il motore dentro. Ci importa che 'Accelerare + Frenare = Restare dove sei'. È una visione dichiarativa e legata alle proprietà"* `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 103]`.

---

## 2. I Tre Pilastri della Specifica Algebrica
In conformità con la teoria del corso, la specifica algebrica è composta da tre elementi `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 104]`:
1. **SORT (I Tipi)**: Quali tipi di dati stiamo usando (es. `Inventario`, `Oggetto`, `Booleano`, `Intero`).
2. **AZIONI (La Sintassi)**: La firma (signature) delle funzioni. Specifica il nome del metodo, i domini di partenza e il dominio di arrivo.
3. **EQUAZIONI (La Semantica)**: Le regole del gioco espresse sotto forma di uguaglianze che permettono la riscrittura e la semplificazione dei termini.

Inoltre, per assicurare che la specifica sia **completa, consistente e non ridondante** `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 106]`, si suddividono gli operatori in `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 107]`:
* **Costruttori**: Operatori che creano o istanziano l'ADT (es. `creaInv()`, `inserisci(...)`).
* **Osservazioni**: Operatori che interrogano l'ADT per ritrovare informazioni (es. `contiene(...)`, `quanti(...)`, `vuoto(...)`).

---

## 3. Specifica Algebrica Completa dell'ADT `Inventario`

Di seguito viene riportata la specifica algebrica formale non assiomatica dell'inventario utilizzato nell'avventura, strutturata esattamente secondo i canoni didattici del corso `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 104-108]`, `[Esercizi Specifiche Algebriche.pdf, p. 2-3]`.

### A. Specifica Sintattica
```text
sorts: Inventario, Oggetto, Intero, Booleano

operations:
    // Costruttori (Generano o modificano la struttura dell'ADT)
    creaInv()                         → Inventario
    inserisci(Inventario, Oggetto)     → Inventario
    
    // Osservazioni (Interrogano l'ADT per estrarre informazioni)
    vuoto(Inventario)                 → Booleano
    quanti(Inventario)                → Intero
    contiene(Inventario, Oggetto)     → Booleano
    rimuovi(Inventario, Oggetto)       → Inventario
```

### B. Matrice Costruttori-Osservazioni
Il comportamento dell'astrazione dati viene definito incrociando ciascun costruttore con ciascun operatore di osservazione nella matrice logica `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 107-108]`:

| Osservazioni / Costruttore | `creaInv()` | `inserisci(inv, x)` |
| :--- | :--- | :--- |
| **`vuoto(inv)`** | `true` | `false` |
| **`quanti(inv)`** | `0` | `quanti(inv) + 1` |
| **`contiene(inv, y)`** | `false` | `se (equal(x, y)) allora true altrimenti contiene(inv, y)` |
| **`rimuovi(inv, y)`** | `creaInv()` | `se (equal(x, y)) allora inv altrimenti inserisci(rimuovi(inv, y), x)` |

*(Nota: L'operatore `equal(x, y)` fa parte dell'algebra standard degli oggetti del dominio `Oggetto` e restituisce un valore booleano).*

### C. Specifica Semantica (Assiomi ed Equazioni)
```text
declare:
    inv: Inventario
    x, y: Oggetto

equations:
    // 1. Assiomi per l'osservazione vuoto(inv)
    vuoto(creaInv()) = true;
    vuoto(inserisci(inv, x)) = false;

    // 2. Assiomi per l'osservazione quanti(inv)
    quanti(creaInv()) = 0;
    quanti(inserisci(inv, x)) = quanti(inv) + 1;

    // 3. Assiomi per l'osservazione contiene(inv, y)
    contiene(creaInv(), y) = false;
    contiene(inserisci(inv, x), y) = se (equal(x, y)) allora true altrimenti contiene(inv, y);

    // 4. Assiomi per l'osservatore/trasformatore rimuovi(inv, y)
    rimuovi(creaInv(), y) = creaInv();
    rimuovi(inserisci(inv, x), y) = se (equal(x, y)) allora inv altrimenti inserisci(rimuovi(inv, y), x);
```

---

## 4. Dimostrazione di Correttezza Formale (Teorema di Riscrittura)
Durante la prova orale, il Prof. Basile potrebbe richiedere di svolgere una dimostrazione alla lavagna partendo dagli assiomi da te scritti nella relazione, similmente all'esercizio svolto a lezione `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 117]`, `[Esercizi Specifiche Algebriche.pdf, p. 4]`.

**Enunciato del Teorema**:
Vogliamo dimostrare formalmente l'uguaglianza:
$$rimuovi(inserisci(creaInv(), A), A) = creaInv()$$

**Dimostrazione per Riduzione e Riscrittura**:
1. Consideriamo il termine sinistro dell'uguaglianza:
   $$rimuovi(inserisci(creaInv(), A), A)$$
2. Applichiamo l'assioma semantico numero 4 dell'operazione `rimuovi`, dove l'argomento `inv` è sostituito con `creaInv()`, l'argomento `x` con `A` e l'argomento `y` con `A`:
   $$rimuovi(inserisci(inv, x), y) = se\ (equal(x, y))\ allora\ inv\ altrimenti\ inserisci(rimuovi(inv, y), x)$$
3. Sostituendo i termini nel ramo destro dell'assioma otteniamo:
   $$se\ (equal(A, A))\ allora\ creaInv()\ altrimenti\ inserisci(rimuovi(creaInv(), A), A)$$
4. Sappiamo dall'algebra degli oggetti che il confronto riflessivo di un elemento con se stesso `equal(A, A)` si riduce identicamente al valore booleano `true`:
   $$se\ (true)\ allora\ creaInv()\ altrimenti\ inserisci(rimuovi(creaInv(), A), A)$$
5. Risolvendo l'operatore condizionale `se-allora-altrimenti` con condizione `true`, si seleziona il ramo `allora`, riducendo l'intero termine a:
   $$creaInv()$$
6. Il termine sinistro si è ridotto esattamente al termine destro. L'uguaglianza è verificata.

**Q.E.D. (Quod Erat Demonstrandum / Come Volevasi Dimostrare)**

---

## 5. Proprietà della Specifica (Chiarimenti per il Colloquio Orale)
Se il docente ti chiede di commentare le proprietà matematiche del sistema di equazioni che hai scritto, rispondi con queste definizioni esatte:

* **Completezza**: La specifica si dice *completa* perché copre tutte le combinazioni possibili tra le operazioni di osservazione (`vuoto`, `quanti`, `contiene`, `rimuovi`) e le operazioni di costruzione (`creaInv`, `inserisci`), permettendo di valutare e ridurre qualsiasi termine ben formato composto da questi operatori `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 106-107]`.
* **Consistenza**: La specifica si dice *consistente (o non contraddittoria)* perché non permette di derivare identità logiche false o assurde (ad esempio non potremo mai derivare che un inventario vuoto ha dimensione `1` o che `true = false`) `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 106]`.
* **Non Ridondanza**: La specifica è *non ridondante (o minimale)* perché nessuno degli assiomi semantici definiti può essere ricavato come teorema a partire dagli altri assiomi rimanenti, ottimizzando il set di regole del sistema algebrico `[Lezioni/1 - Paradigmi di Programmazione e Astrazione.pdf, Slide 106]`.

---

### 🎉 Conclusioni della Guida
Utilizzando questa guida formale, potrai documentare il tuo progetto MAP in modo ineccepibile, allineando sia la stesura del codice che la stesura della relazione teorica con le esatte richieste e definizioni espresse dal Prof. Pierpaolo Basile durante le lezioni. Buon lavoro per il tuo esame di Metodi Avanzati di Programmazione!
