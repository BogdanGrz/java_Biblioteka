package model;

public class Miasta {
    private int id;
    private String miasto;
    private String kod;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getMiasto() {
        return miasto;
    }
    public void setMiasto(String miasto) {
        this.miasto = miasto;
    }
    public String getKod() {
        return miasto;
    }
    public void setKod(String kod) {
        this.kod = kod;
    }

    public Miasta() {}
    public Miasta(int id, String miasto, String kod) {
        this.id = id;
        this.miasto = miasto;
        this.kod = kod;
    }

    @Override
    public String toString() {
        return "["+id+"] - "+miasto+" - "+ kod+"\n";
    }
}