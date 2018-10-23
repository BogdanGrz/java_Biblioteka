package model;

public class Ulice {
    private int id;
    private String ulica;
    private String numer;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUlica() {
        return ulica;
    }
    public void setUlica_id(String ulica) {
        this.ulica = ulica;
    }
    public String getNumer() {
        return numer;
    }
    public void setNumer(String numer) {
        this.numer = numer;
    }

    public Ulice() {}
    public Ulice(int id, String ulica, String numer) {
        this.id = id;
        this.ulica = ulica;
        this.numer=numer;
    }

    @Override
    public String toString() {
        return "["+id+"] - "+ulica+" "+numer+"\n";
    }
}