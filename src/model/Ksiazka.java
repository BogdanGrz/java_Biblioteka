package model;

public class Ksiazka {
    private int id_ksiazki;
    private String tytul;
    private int autor;
    private int autor2;

    public int getId_ksiazki() {
        return id_ksiazki;
    }

    public void setId_ksiazki(int id_ksiazki) {
        this.id_ksiazki = id_ksiazki;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public int getAutor() {
        return autor;
    }

    public void setAutor(int autor) {
        this.autor = autor;
    }

    public int getAutor2() {
        return autor2;
    }

    public void setAutor2(int autor2) {
        this.autor2 = autor2;
    }

    public int getAutor3() {
        return autor3;
    }

    public void setAutor3(int autor3) {
        this.autor3 = autor3;
    }

    public String getRok_wyd() {
        return rok_wyd;
    }

    public void setRok_wyd(String rok_wyd) {
        this.rok_wyd = rok_wyd;
    }

    public String getJezyk() {
        return jezyk;
    }

    public void setJezyk(String jezyk) {
        this.jezyk = jezyk;
    }

    public int getId_dzial() {
        return id_dzial;
    }

    public void setId_dzial(int id_dzial) {
        this.id_dzial = id_dzial;
    }

    public int getId_gatunek() {
        return id_gatunek;
    }

    public void setId_gatunek(int id_gatunek) {
        this.id_gatunek = id_gatunek;
    }

    public int getId_kat() {
        return id_kat;
    }

    public void setId_kat(int id_kat) {
        this.id_kat = id_kat;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }
    private int autor3;
    private String rok_wyd;
    private String jezyk;
    private int id_dzial;
    private int id_gatunek;
    private int id_kat;
    private String opid;


    public Ksiazka() {}
    public Ksiazka(int id_ksiazki, String tytul, int autor, int autor2 ,int autor3) {
        this.id_ksiazki = id_ksiazki;
        this.tytul = tytul;
        this.autor = autor;
        this.autor2 = autor2;
        this.autor3 = autor3;
    }

    @Override
    public String toString() {
        return "["+id_ksiazki+"] - "+tytul+" - "+autor+" - ";
    }
}