package model;

public class Dzial {
    private int id_dzialu;
    private String Dzial;

    public int getId_dzialu() {
        return id_dzialu;
    }

    public void setId_dzialu(int id_dzialu) {
        this.id_dzialu = id_dzialu;
    }

    public String getDzial() {
        return Dzial;
    }

    public void setDzial(String Dzial) {
        this.Dzial = Dzial;
    }

    public Dzial(int id_dzialu, String Dzial) {
        this.id_dzialu = id_dzialu;
        this.Dzial = Dzial;
    }



    
    @Override
    public String toString() {
        return "["+id_dzialu+"] - "+Dzial+"\n";
    }
}