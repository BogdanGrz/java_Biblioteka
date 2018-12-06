package model;

public class Kategoria {
    private int id_kategori;
    private String Kategoria;

    public int getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(int id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getKategoria() {
        return Kategoria;
    }

    public void setKategoria(String Kategoria) {
        this.Kategoria = Kategoria;
    }

    public Kategoria(int id_kategori, String Kategoria) {
        this.id_kategori = id_kategori;
        this.Kategoria = Kategoria;
    }

    
    @Override
    public String toString() {
        return "["+id_kategori+"] - "+Kategoria+"\n";
    }
}