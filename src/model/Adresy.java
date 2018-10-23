package model;

public class Adresy {
    private int id;
    private int ulica_id;
    private int miasto_id;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUlica_id() {
        return ulica_id;
    }
    public void setUlica_id(int ulica_id) {
        this.ulica_id = ulica_id;
    }
    public int getMiasto_id() {
        return miasto_id;
    }
    public void setMiasto_id(int miasto_id) {
        this.miasto_id = miasto_id;
    }

    public Adresy() { }
    public Adresy(int id, int ulica_id, int miasto_id) {
        this.id = id;
        this.ulica_id = ulica_id;
        this.miasto_id=miasto_id;
    }

    @Override
    public String toString() {
        return "["+id+"] - "+ulica_id+" "+miasto_id+"\n";
    }
}