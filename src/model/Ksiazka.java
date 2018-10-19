package model;

public class Ksiazka {
    private int id;
    private String tytul;
    private String autor;
    private String gatunek;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTytul() {
        return tytul;
    }
    public void setTytul(String tytul) {
        this.tytul = tytul;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public String getGatunek() {
        return gatunek;
    }
    public void setGatunek(String gatunek) {
        this.gatunek = gatunek;
    }

    public Ksiazka() {}
    public Ksiazka(int id, String tytul, String autor, String gatunek) {
        this.id = id;
        this.tytul = tytul;
        this.autor = autor;
        this.gatunek = gatunek;
    }

    @Override
    public String toString() {
        return "["+id+"] - "+tytul+" - "+autor+" - "+gatunek;
    }
}