# 07 — Specifica algebrica non assiomatica dell'Inventario

# 1. Perché è stata scelta la struttura `Inventario`

Il template dell'esame richiede una specifica algebrica non assiomatica di una struttura dati usata nel progetto.

La struttura scelta è:

```text
Inventario
```

È adatta perché:

- è centrale nel gameplay;
- contiene oggetti raccolti dal giocatore;
- ha operazioni semplici e chiare;
- è effettivamente implementata nel codice.

---

# 2. Differenza tra specifica assiomatica e algebrica

Una specifica assiomatica usa spesso precondizioni e postcondizioni.

Esempio:

```text
pre: l'inventario esiste
post: l'oggetto è stato inserito
```

Una specifica algebrica descrive invece il comportamento tramite equazioni.

Esempio:

```text
vuoto(creaInv()) = true
quanti(inserisci(inv, x)) = quanti(inv) + 1
```

Il progetto usa il secondo approccio, cioè una specifica algebrica.

---

# 3. Sorts

```text
sorts:
    Inventario, Oggetto, Intero, Booleano
```

I sorts sono i tipi coinvolti nella specifica.

- `Inventario`: struttura dati principale;
- `Oggetto`: elemento contenuto;
- `Intero`: risultato di `quanti`;
- `Booleano`: risultato di `vuoto` e `contiene`.

---

# 4. Operations

```text
creaInv()                         -> Inventario
inserisci(Inventario, Oggetto)    -> Inventario
rimuovi(Inventario, Oggetto)      -> Inventario
vuoto(Inventario)                 -> Booleano
quanti(Inventario)                -> Intero
contiene(Inventario, Oggetto)     -> Booleano
```

---

# 5. Costruttori, osservatori e trasformatori

## Costruttori

```text
creaInv()
inserisci(Inventario, Oggetto)
```

Servono a costruire valori dell'ADT.

## Osservatori

```text
vuoto(Inventario)
quanti(Inventario)
contiene(Inventario, Oggetto)
```

Servono a interrogare l'ADT senza modificarlo.

## Trasformatore

```text
rimuovi(Inventario, Oggetto)
```

`rimuovi` restituisce un nuovo inventario senza l'oggetto indicato. Non è un osservatore puro, perché produce un nuovo valore dell'ADT.

---

# 6. Equazioni di riduzione

```text
declare:
    inv: Inventario
    x, y: Oggetto

equations:
    vuoto(creaInv()) = true
    vuoto(inserisci(inv, x)) = false

    quanti(creaInv()) = 0
    quanti(inserisci(inv, x)) = quanti(inv) + 1

    contiene(creaInv(), y) = false
    contiene(inserisci(inv, x), y) =
        if equal(x, y) then true else contiene(inv, y)

    rimuovi(creaInv(), y) = creaInv()
    rimuovi(inserisci(inv, x), y) =
        if equal(x, y) then inv else inserisci(rimuovi(inv, y), x)
```

---

# 7. Esempio di riduzione

Vogliamo dimostrare:

```text
rimuovi(inserisci(creaInv(), A), A) = creaInv()
```

Applichiamo l'equazione di `rimuovi`:

```text
rimuovi(inserisci(inv, x), y) =
    if equal(x, y) then inv else inserisci(rimuovi(inv, y), x)
```

Sostituendo:

```text
inv = creaInv()
x = A
y = A
```

otteniamo:

```text
if equal(A, A) then creaInv() else inserisci(rimuovi(creaInv(), A), A)
```

Poiché:

```text
equal(A, A) = true
```

il risultato è:

```text
creaInv()
```

Quindi:

```text
rimuovi(inserisci(creaInv(), A), A) = creaInv()
```

---

# 8. Collegamento con il codice

Nel codice:

```java
public class Inventario implements Serializable
```

Operazioni:

```java
public static Inventario creaInv()
public Inventario inserisci(Oggetto x)
public Inventario rimuovi(Oggetto y)
public boolean vuoto()
public int quanti()
public boolean contiene(Oggetto y)
```

Il codice rispecchia la specifica.

---

# 9. Perché è conforme al corso

La specifica usa:

- sorts;
- operations;
- equazioni;
- costruttori;
- osservatori;
- riduzione di un termine.

Sono gli stessi concetti spiegati nelle lezioni sulle specifiche algebriche dei tipi astratti di dato.
