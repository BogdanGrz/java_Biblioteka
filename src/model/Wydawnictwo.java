package model;

public class Wydawnictwo {
    private int id_wydawnictwa;
    private String Wydawnictwo;

    public int getId_wydawnictwa() {
        return id_wydawnictwa;
    }

    public void setId_wydawnictwa(int id_wydawnictwa) {
        this.id_wydawnictwa = id_wydawnictwa;
    }

    public String getWydawnictwo() {
        return Wydawnictwo;
    }

    public void setWydawnictwo(String Wydawnictwo) {
        this.Wydawnictwo = Wydawnictwo;
    }

    public Wydawnictwo(int id_wydawnictwa, String Wydawnictwo) {
        this.id_wydawnictwa = id_wydawnictwa;
        this.Wydawnictwo = Wydawnictwo;
    }

    
    @Override
    public String toString() {
        return "["+id_wydawnictwa+"] - "+Wydawnictwo+"\n";
    }
}