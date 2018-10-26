package model;

public class Czytelnik {
    private int id;
    private String imie;
    private String nazwisko;
    private String pesel;
    private String dob;
    private String email;
    private String login;
    private String haslo;
    private String telefon;
    private double zadluzenie;
    private int miasto;
    private int ulica;
    private String numer;

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public double getZadluzenie() {
        return zadluzenie;
    }

    public void setZadluzenie(double zadluzenie) {
        this.zadluzenie = zadluzenie;
    }

    public int getMiasto() {
        return miasto;
    }

    public void setMiasto(int miasto) {
        this.miasto = miasto;
    }

    public int getUlica() {
        return ulica;
    }

    public void setUlica(int ulica) {
        this.ulica = ulica;
    }

    public String getNumer() {
        return numer;
    }

    public void setNumer(String numer) {
        this.numer = numer;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getImie() {
        return imie;
    }
    public void setImie(String imie) {
        this.imie = imie;
    }
    public String getNazwisko() {
        return nazwisko;
    }
    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }
    public String getPesel() {
        return pesel;
    }
    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Czytelnik() { }
    public Czytelnik(int id, String imie, String nazwisko, String pesel) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pesel = pesel;
    }

    public Czytelnik(int id, String imie, String nazwisko, String pesel, String dob, String email, String login, String haslo, String telefon, int miasto, int ulica, String numer) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pesel = pesel;
        this.dob = dob;
        this.email = email;
        this.login = login;
        this.haslo = haslo;
        this.telefon = telefon;
        this.miasto = miasto;
        this.ulica = ulica;
        this.numer = numer;
    }

    public Czytelnik(int id, String imie, String nazwisko, String pesel, String dob, String haslo) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pesel = pesel;
        this.dob = dob;
        this.haslo = haslo;
    }

    @Override
    public String toString() {
        return "["+id+"] - "+imie+" "+nazwisko+" - "+pesel+"\n";
    }
}