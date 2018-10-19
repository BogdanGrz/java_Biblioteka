package model;

public class Wypozyczenie {
    private int idKsiazka;
    private int idCzytelnik;

    public int getIdKsiazka() {
        return idKsiazka;
    }
    public void setIdKsiazka(int idKsiazka) {
        this.idKsiazka = idKsiazka;
    }
    public int getIdCzytelnik() {
        return idCzytelnik;
    }
    public void setIdCzytelnik(int idCzytelnik) {
        this.idCzytelnik = idCzytelnik;
    }

    public Wypozyczenie() {}
    public Wypozyczenie(int idKsiazka, int idCzytelnik) {
        this.idKsiazka = idKsiazka;
        this.idCzytelnik = idCzytelnik;

    }
}