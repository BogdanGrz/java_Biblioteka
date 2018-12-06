package model;

public class Gatunek {
    private int id_gatunku;
    private String Gatunek;

    public int getId_gatunku() {
        return id_gatunku;
    }

    public void setId_gatunku(int id_gatunku) {
        this.id_gatunku = id_gatunku;
    }

    public String getGatunek() {
        return Gatunek;
    }

    public Gatunek(int id_gatunku, String Gatunek) {
        this.id_gatunku = id_gatunku;
        this.Gatunek = Gatunek;
    }

    public void setGatunek(String Gatunek) {
        this.Gatunek = Gatunek;
    }

    

    @Override
    public String toString() {
        return "["+id_gatunku+"] - "+Gatunek+"\n";
    }
}