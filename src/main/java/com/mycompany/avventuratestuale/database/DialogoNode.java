package com.mycompany.avventuratestuale.database;

/**
 * DTO immutabile per un nodo di dialogo a due scelte.
 */
public class DialogoNode {
    private final int id;
    private final String testoIa;
    private final String opzione1;
    private final int dest1;
    private final String opzione2;
    private final int dest2;

    public DialogoNode(int id, String testoIa, String opzione1, int dest1, String opzione2, int dest2) {
        this.id = id;
        this.testoIa = testoIa;
        this.opzione1 = opzione1;
        this.dest1 = dest1;
        this.opzione2 = opzione2;
        this.dest2 = dest2;
    }

    public int getId() { return id; }
    public String getTestoIa() { return testoIa; }
    public String getOpzione1() { return opzione1; }
    public int getDest1() { return dest1; }
    public String getOpzione2() { return opzione2; }
    public int getDest2() { return dest2; }
}
