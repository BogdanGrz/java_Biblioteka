package model;

public class jointest {
    private int idKsiazka;
    private int idCzytelnik;
    //private String nazwaCz;
    private String nazwaKs;

    

    public jointest() {}
    public jointest(int idKsiazka, int idCzytelnik, String nazwaKs) {
        this.idKsiazka = idKsiazka;
        this.idCzytelnik = idCzytelnik;
        this.nazwaKs = nazwaKs;

    }
    @Override
    public String toString() {
        return "["+idKsiazka+"] - "+idCzytelnik+" - "+nazwaKs;
    
}}