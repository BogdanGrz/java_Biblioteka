package model;

public class Lokalizacja {
    private int id_lokalizacji;
    private String Lokalizacja;

    public int getId_lokalizacji() {
        return id_lokalizacji;
    }

    public void setId_lokalizacji(int id_lokalizacji) {
        this.id_lokalizacji = id_lokalizacji;
    }

    public String getLokalizacja() {
        return Lokalizacja;
    }

    public void setLokalizacja(String Lokalizacja) {
        this.Lokalizacja = Lokalizacja;
    }

    public Lokalizacja(int id_lokalizacji, String Lokalizacja) {
        this.id_lokalizacji = id_lokalizacji;
        this.Lokalizacja = Lokalizacja;
    }

   
    @Override
    public String toString() {
        return "["+id_lokalizacji+"] - "+Lokalizacja+"\n";
    }
}