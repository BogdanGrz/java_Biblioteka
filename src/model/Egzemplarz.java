package model;

public class Egzemplarz {
    private int id_egzemplarza;
    private int id_ksiazki;
    private int lokazlizacja;
    private int stan;
    private int wydawnictwo;
    private String rok;
    private String jezykk;

    public int getId_egzemplarza() {
        return id_egzemplarza;
    }

    public void setId_egzemplarza(int id_egzemplarza) {
        this.id_egzemplarza = id_egzemplarza;
    }

    public int getId_ksiazki() {
        return id_ksiazki;
    }

    public void setId_ksiazki(int id_ksiazki) {
        this.id_ksiazki = id_ksiazki;
    }

    public int getLokazlizacja() {
        return lokazlizacja;
    }

    public void setLokazlizacja(int lokazlizacja) {
        this.lokazlizacja = lokazlizacja;
    }

    public int getStan() {
        return stan;
    }

    public void setStan(int stan) {
        this.stan = stan;
    }

    public int getWydawnictwo() {
        return wydawnictwo;
    }

    public void setWydawnictwo(int wydawnictwo) {
        this.wydawnictwo = wydawnictwo;
    }

    public String getRok() {
        return rok;
    }

    public void setRok(String rok) {
        this.rok = rok;
    }

    public String getJezykk() {
        return jezykk;
    }

    public void setJezykk(String jezykk) {
        this.jezykk = jezykk;
    }

    public Egzemplarz(int id_egzemplarza, int id_ksiazki, int lokazlizacja, int stan, int wydawnictwo, String rok, String jezykk) {
        this.id_egzemplarza = id_egzemplarza;
        this.id_ksiazki = id_ksiazki;
        this.lokazlizacja = lokazlizacja;
        this.stan = stan;
        this.wydawnictwo = wydawnictwo;
        this.rok = rok;
        this.jezykk = jezykk;
    }

    
    
    @Override
    public String toString() {
        return "["+id_egzemplarza+"] - "+id_ksiazki;
    }
}