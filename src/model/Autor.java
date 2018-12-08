package model;

public class Autor {
    private int id_autora;
    private String nazwisko;
    private String imie;

    public Autor(int id_autora, String nazwisko, String imie) {
        this.id_autora = id_autora;
        this.nazwisko = nazwisko;
        this.imie = imie;
    }

    

    @Override
    public String toString() {
        return "["+id_autora+"] - "+nazwisko+" - "+ imie+"\n";
    }

    public int getId_autora() {
        return id_autora;
    }

    public void setId_autora(int id_autora) {
        this.id_autora = id_autora;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }
}