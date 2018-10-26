package model;

public class Ulice {
    private int id;
    private String ulica;

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

    public Ulice() {}
    public Ulice(int id, String ulica) {
        this.id = id;
        this.ulica = ulica;
    }

    @Override
    public String toString() {
        return "["+id+"] - "+ulica+"\n";
    }
}