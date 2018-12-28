package Biblioteka;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import Helpers.SendEmail;
import model.Autor;
import model.Czytelnik;
import model.Dzial;
import model.Gatunek;
import model.Kategoria;
import model.Ksiazka;
import model.Lokalizacja;
import model.Wypozyczenie;
import model.Miasto;
import model.Stan;
import model.Ulica;
import model.Wydawnictwo;

public class DatabaseAPI {

    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:biblioteka.db";
    //jdbc:sqlite:C:/Users/Bodzio/Desktop/biblioteka.db  - przyklad linku do innego katalogu z baza
    private Connection conn;
    private Statement stat;

    public DatabaseAPI() {
        try {
            Class.forName(DatabaseAPI.DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Brak sterownika JDBC");
            e.printStackTrace();
        }

        try {
            conn = DriverManager.getConnection(DB_URL);
            stat = conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Problem z otwarciem polaczenia");
            e.printStackTrace();
        }

        createTables();
    }

    public boolean createTables()  {
        String createCzytelnicy = "CREATE TABLE IF NOT EXISTS czytelnicy (id_czytelnika INTEGER PRIMARY KEY AUTOINCREMENT, imie varchar ( 255 ) NOT NULL, nazwisko varchar ( 255 ) NOT NULL, pesel varchar ( 11 ) NOT NULL UNIQUE, DOB varchar(12) NOT NULL, email varchar ( 40 ) UNIQUE, username varchar ( 40 ) UNIQUE, password varchar NOT NULL, zadluzenie REAL DEFAULT 0, miasto_id INTEGER, ulica_id INTEGER, numer_domu varchar (10), telefon varchar (20))";
        String createWypozyczenia = "CREATE TABLE IF NOT EXISTS wypozyczenia (id_wypozycz INTEGER PRIMARY KEY AUTOINCREMENT, id_czytelnika int, id_egzemplarza int, data_wypozyczenia VARCHAR(10), data_planowana VARCHAR(10), data_zwrotu VARCHAR(10))";
        String createUlice = "CREATE TABLE IF NOT EXISTS ulice (id_ulice INTEGER PRIMARY KEY AUTOINCREMENT, ulica varchar(255), numer varchar(255))";
        String createMiasta = "CREATE TABLE IF NOT EXISTS miasta (id_miasta INTEGER PRIMARY KEY AUTOINCREMENT, miasto varchar(255), kod varchar(6))";
        String createAutorzy = "CREATE TABLE IF NOT EXISTS autorzy (id_autora INTEGER PRIMARY KEY AUTOINCREMENT, nazwisko varchar(100), imie varchar(30))";
        String createDzialy = "CREATE TABLE IF NOT EXISTS dzialy (id_dzialu INTEGER PRIMARY KEY AUTOINCREMENT, nazwa_dzi varchar(100))";
        String createGatunki = "CREATE TABLE IF NOT EXISTS gatunki (id_gatunku INTEGER PRIMARY KEY AUTOINCREMENT, nazwa_gatunku varchar(100))";
        String createKategorie = "CREATE TABLE IF NOT EXISTS kategorie (id_kategori INTEGER PRIMARY KEY AUTOINCREMENT, nazwa_kat varchar(100))";
        String createWydawnictwa = "CREATE TABLE IF NOT EXISTS wydawnictwa (id_wydawnictwa INTEGER PRIMARY KEY AUTOINCREMENT, nazwa_wyd varchar(100))";
        String createKsiazki = "CREATE TABLE IF NOT EXISTS ksiazki (id_ksiazki INTEGER PRIMARY KEY AUTOINCREMENT, tytul varchar(255), autor int, autor2 int, autor3 int, id_dzial int, id_gatunek int, id_kat int DEFAULT 1, opis TEXT)";
        String createEgzemplarze = "CREATE TABLE IF NOT EXISTS egzemplarze (id_egzemplarza INTEGER PRIMARY KEY AUTOINCREMENT, id_ksiazki int, lokalizacja int, stan int, id_wyd int, rok_wyd varchar(4), jezyk varchar(100))";
        String createLokalizacje = "CREATE TABLE IF NOT EXISTS lokalizacje (id_lokalizacji INTEGER PRIMARY KEY AUTOINCREMENT, nazwa_lokalizacji varchar(255));";
        String createStany = "CREATE TABLE IF NOT EXISTS stany (id_stanu INTEGER PRIMARY KEY AUTOINCREMENT, nazwa_stanu varchar(255));";
        String createPracownicy = "CREATE TABLE IF NOT EXISTS pracownicy (id_pracownika INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL, imie TEXT DEFAULT \"\", nazwisko TEXT DEFAULT \"\")";
        try {
            stat.execute(createCzytelnicy);
            stat.execute(createKsiazki);
            stat.execute(createWypozyczenia);
            stat.execute(createUlice);
            stat.execute(createAutorzy);
            stat.execute(createDzialy);
            stat.execute(createGatunki);
            stat.execute(createKategorie);
            stat.execute(createWydawnictwa);
            stat.execute(createEgzemplarze);
            stat.execute(createMiasta);
            stat.execute(createLokalizacje);
            stat.execute(createStany);
            stat.execute(createPracownicy);
        } catch (SQLException e) {
            System.err.println("Blad przy tworzeniu tabeli");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*public boolean insertCzytelnik(String imie, String nazwisko, String pesel) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into czytelnicy values (NULL, ?, ?, ?);");
            prepStmt.setString(1, imie);
            prepStmt.setString(2, nazwisko);
            prepStmt.setString(3, pesel);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy wstawianiu czytelnika");
            e.printStackTrace();
            return false;
        }
        return true;
    }*/

    public boolean insertCzytelnik(String imie, String nazwisko, String pesel, String DOB, String email, String username, String password, int miasto_id, int ulica_id, String numer_domu, String telefon) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into czytelnicy values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            prepStmt.setString(1, imie);
            prepStmt.setString(2, nazwisko);
            prepStmt.setString(3, pesel);
            prepStmt.setString(4, DOB);
            prepStmt.setString(5, email);
            prepStmt.setString(6, username);
            prepStmt.setString(7, password);
            prepStmt.setDouble(8, 0.0);
            prepStmt.setInt(9, miasto_id);
            prepStmt.setInt(10, ulica_id);
            prepStmt.setString(11, numer_domu);
            prepStmt.setString(12, telefon);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu czytelnika");
            return false;
        }
        return true;
    }
    
    public boolean insertBook(String tytul, int autor, int dzial, int gatunek, int kategoria, String opis) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into ksiazki values (NULL, ?, ?, ?, ?, ?, ?);");
            prepStmt.setString(1, tytul);
            prepStmt.setInt(2, autor);
            prepStmt.setInt(3, dzial);
            prepStmt.setInt(4, gatunek);
            prepStmt.setInt(5, kategoria);
            prepStmt.setString(6, opis);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu ksiazki");
            return false;
        }
        return true;
    }
    
    public boolean insertEgzemplarz(int id_ksiazki, int lokalizacja, int stan, int wydawnictwo, String rok, String jezyk) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into egzemplarze values (NULL, ?, ?, ?, ?, ?, ?);");
            prepStmt.setInt(1, id_ksiazki);
            prepStmt.setInt(2, lokalizacja);
            prepStmt.setInt(3, stan);
            prepStmt.setInt(4, wydawnictwo);
            prepStmt.setString(5, rok);
            prepStmt.setString(6, jezyk);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu egzemplarza do bazy");
            return false;
        }
        return true;
    }
    
    

    public boolean insertWypozycz(int idCzytelnik, int idKsiazka) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into wypozyczenia values (NULL, ?, ?);");
            prepStmt.setInt(1, idCzytelnik);
            prepStmt.setInt(2, idKsiazka);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy wypozyczaniu");
            return false;
        }
        return true;
    }
    
     public boolean insertUlica(String ulica) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into ulice values (NULL, ?);");
            prepStmt.setString(1, ulica);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu ulicy");
            return false;
        }
        return true;
    }
     
      public boolean insertMiasto(String miasto, String kod) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into miasta values (NULL, ?, ?);");
            prepStmt.setString(1, miasto);
            prepStmt.setString(2, kod);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu miasta");
            return false;
        }
        return true;
    }
      
      public boolean insertAutor(String nazwisko, String imie) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into autorzy values (NULL, ?, ?);");
            prepStmt.setString(1, nazwisko);
            prepStmt.setString(2, imie);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu autora do bazy");
            return false;
        }
        return true;
    }
      
      public boolean insertPracownik(String nazwisko, String imie, String login, String haslo) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into pracownicy values (NULL, ?, ?, ?, ?);");
            prepStmt.setString(1, login);
            prepStmt.setString(2, haslo);
            prepStmt.setString(3, imie);
            prepStmt.setString(4, nazwisko);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu pracownika do bazy");
            return false;
        }
        return true;
    }
      
       public boolean insertWypozyczenie(int czytelnik, int egzemplarz, String dataWyp, String dataPla, String dataZwr) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into wypozyczenia values (NULL, ?, ?, ?, ?, ?);");
            prepStmt.setInt(1, czytelnik);
            prepStmt.setInt(2, egzemplarz);
            prepStmt.setString(3, dataWyp);
            prepStmt.setString(4, dataPla);
            prepStmt.setString(5, dataZwr);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy dodawaniu wypozyczenia do bazy");
            return false;
        }
        return true;
    }
      

    
    
    

    public List<Czytelnik> selectCzytelnicy() {
        List<Czytelnik> czytelnicy = new LinkedList<Czytelnik>();
        String select="SELECT * FROM czytelnicy";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String imie, nazwisko, pesel;
            while(result.next()) {
                id = result.getInt("id_czytelnika");           
                imie = result.getString("imie");
                nazwisko = result.getString("nazwisko");
                pesel = result.getString("pesel");
                czytelnicy.add(new Czytelnik(id, imie, nazwisko, pesel));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return czytelnicy;
    }
    
     public List<Miasto> selectMiasta() {
        List<Miasto> miasta = new LinkedList<Miasto>();
        String select="SELECT * from miasta ORDER BY miasto ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String miasto, kod;
            while(result.next()) {
                id = result.getInt("id_miasta");           
                miasto = result.getString("miasto");
                kod = result.getString("kod");

                miasta.add(new Miasto(id, miasto, kod));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return miasta;
    }
     
     public List<Autor> selectAutorzy() {
        List<Autor> autorzy = new LinkedList<Autor>();
        String select="SELECT * from autorzy ORDER BY nazwisko ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String nazwisko, imie;
            while(result.next()) {
                id = result.getInt("id_autora");           
                nazwisko = result.getString("nazwisko");
                imie = result.getString("imie");

                autorzy.add(new Autor(id, nazwisko, imie));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return autorzy;
    }
     
     public int selectMiastaWhereKod(String kod) {
        //List<Miasta> miasta = new LinkedList<Miasta>();
        String select="SELECT id_miasta from miasta WHERE kod='"+kod+"';";
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            
           // String miasto, kod;
            while(result.next()) {
                id = result.getInt("id_miasta");           
               // miasto = result.getString("miasto");
                //kod = result.getString("kod");

               // miasta.add(new Miasta(id, miasto, kod));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return id;
    }
     
     public int selectAutorzyWhereNazwiskoIImie(String nazwisko, String imie) {
        //List<Miasta> miasta = new LinkedList<Miasta>();
        String select="SELECT id_autora from autorzy WHERE nazwisko='"+nazwisko+"' AND imie='"+imie+"';";
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                id = result.getInt("id_autora");           
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return id;
    }
     
     
     
     
     
     public String selectMiastoWhereKod(String kod) {
        //List<Miasta> miasta = new LinkedList<Miasta>();
        String select="SELECT miasto from miasta WHERE kod='"+kod+"';";
        System.out.println(select);
        String miasto="";
        try {
            ResultSet result = stat.executeQuery(select);
            
           // String miasto, kod;
            while(result.next()) {
                miasto = result.getString("miasto");           
               // miasto = result.getString("miasto");
                //kod = result.getString("kod");

               // miasta.add(new Miasta(id, miasto, kod));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
                    }
        return miasto;
    }
     
     public int selectUlicaWhereUlica(String ulica) {
        String select="SELECT id_ulice from ulice WHERE ulica='"+ulica+"';";
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                id = result.getInt("id_ulice");           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return id;
    }
     
     
     public int selectIDWhereSzukana(String szukana, String nazwaKolId, String nazwaKolSzukanej, String nazwaTabeli) {
        
        String select="SELECT "+nazwaKolId+" from "+nazwaTabeli+" WHERE "+nazwaKolSzukanej+"='"+szukana+"';";
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                id = result.getInt(nazwaKolId);           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return id;
    }
     
     
     public int selectCountUlica(String ulica) {
        String select="Select COUNT (ulica) as ulica from ulice where ulica LIKE '"+ulica+"';";
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                id = result.getInt("ulica");           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("blad bazy: Select COUNT (ulica) as ulica from ulice where ulica LIKE '"+ulica+"';");
            return 0;
        }
        return id;
    }     
     
     public int selectMaxIDEgzemplarze() {
        String select="Select MAX (id_egzemplarza) AS Result from egzemplarze";
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                id = result.getInt("Result");           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("blad SELECT MAX ID egzemplarze");
            return 0;
        }
        return id;
    }   
     
     public int selectMaxIDUniwersal(String kolumna, String tabela) {
        String select="Select MAX ("+kolumna+") AS Result from "+tabela;
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                id = result.getInt("Result");           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("blad SELECT MAX ID "+ tabela);
            return 0;
        }
        return id;
    }
     
     
     public int selectCountUniwersalny(String co, String gdzie_tab, String gdzie_col) {
        String select="Select COUNT ("+gdzie_col+") as ile from "+gdzie_tab+" where "+gdzie_col+" LIKE '"+co+"';";
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                id = result.getInt("ile");           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("blad bazy: Select COUNT (ile) as ile from "+gdzie_tab+" where "+gdzie_col+" LIKE '"+co+"';");
            return 0;
        }
        return id;
    }     
     
     
     
     
    public int selectCountMiasto(String kod) {
        String select="Select COUNT (kod) as kod from miasta where kod LIKE '"+kod+"';";
        System.out.println(select);
        int id=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                id = result.getInt("kod");           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return id;
    }      
    
    public int selectSzukanaWhereWarunekReturnINT(String szukana, String tabela, String kolumna, int warunek) {
        String select="Select "+szukana+" from "+tabela+" where "+kolumna+" LIKE '"+warunek+"';";
        System.out.println(select);
        int wynik=100;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                wynik = result.getInt(szukana);           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return wynik;
    } 
    
    public int selectID_WYP_with_where2(int id_egz) {
        String select="SELECT id_wypozycz from wypozyczenia where data_zwrotu = \"\" AND id_egzemplarza="+id_egz+";";
        System.out.println(select);
        int wynik=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                wynik = result.getInt("id_wypozycz");           
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return wynik;
    } 
    
    public int selectCountUniwersal(String szukana, String tabela, String kolumna) {
        String select="Select COUNT ("+kolumna+") as kod from "+tabela+" where "+kolumna+" LIKE '"+szukana+"';";
        System.out.println(select);
        int ile=0;
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                ile = result.getInt("kod");           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return ile;
    }      
    
    public String selectSzukanaWhereWarunek(String szukana, String tabela, String kolumna, String warunek) {
        String select="Select "+szukana+" from "+tabela+" where "+kolumna+" LIKE '"+warunek+"';";
        System.out.println(select);
        String wynik="";
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                wynik = result.getString(szukana);           

            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return wynik;
    }    
    
    public List selectSzukaneWhereWarunek(String szukana, String tabela, String kolumna, String warunek) {
        String select="Select "+szukana+" from "+tabela+" where "+kolumna+" LIKE '"+warunek+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getString(szukana)); //0
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Blad przy select SZUKANE where warunek ");
            return null;
        }
        
        return lista1;
    }
    
    
    
    public List<Ulica> selectUlice() {
        List<Ulica> ulice = new LinkedList<Ulica>();
        String select="SELECT * from ulice ORDER BY ulica ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String ulica;
            while(result.next()) {
                id = result.getInt("id_ulice");           
                ulica = result.getString("ulica");

                ulice.add(new Ulica(id, ulica));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return ulice;
    } 
    
    
    public List<Gatunek> selectGatunki() {
        List<Gatunek> gatunki = new LinkedList<Gatunek>();
        String select="SELECT * from gatunki ORDER BY nazwa_gatunku COLLATE NOCASE ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String gatunek;
            while(result.next()) {
                id = result.getInt("id_gatunku");           
                gatunek = result.getString("nazwa_gatunku");

                gatunki.add(new Gatunek(id, gatunek));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return gatunki;
    } 
    
    public List<Kategoria> selectKategorie() {
        List<Kategoria> Kategorie = new LinkedList<Kategoria>();
        String select="SELECT * from kategorie ORDER BY nazwa_kat COLLATE NOCASE ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String Kategoria;
            while(result.next()) {
                id = result.getInt("id_kategori");           
                Kategoria = result.getString("nazwa_kat");

                Kategorie.add(new Kategoria(id, Kategoria));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Kategorie;
    }
    
    public List<Dzial> selectDzialy() {
        List<Dzial> Dzialy = new LinkedList<Dzial>();
        String select="SELECT * from dzialy ORDER BY nazwa_dzi COLLATE NOCASE ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String Dzial;
            while(result.next()) {
                id = result.getInt("id_dzialu");           
                Dzial = result.getString("nazwa_dzi");

                Dzialy.add(new Dzial(id, Dzial));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Dzialy;
    } 
    
    public List<Lokalizacja> selectLokalizacje() {
        List<Lokalizacja> Lokalizacje = new LinkedList<Lokalizacja>();
        String select="SELECT * from lokalizacje ORDER BY nazwa_lokalizacji COLLATE NOCASE ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String Lokalizacja;
            while(result.next()) {
                id = result.getInt("id_lokalizacji");           
                Lokalizacja = result.getString("nazwa_lokalizacji");

                Lokalizacje.add(new Lokalizacja(id, Lokalizacja));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Lokalizacje;
    } 
    
    
    public List<Stan> selectStany() {
        List<Stan> Stany = new LinkedList<Stan>();
        String select="SELECT * from stany ORDER BY nazwa_stanu COLLATE NOCASE ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String Stan;
            while(result.next()) {
                id = result.getInt("id_stanu");           
                Stan = result.getString("nazwa_stanu");

                Stany.add(new Stan(id, Stan));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Stany;
    } 
    
    
    public List<Wydawnictwo> selectWydawnictwa() {
        List<Wydawnictwo> Wydawnictwa = new LinkedList<Wydawnictwo>();
        String select="SELECT * from wydawnictwa ORDER BY nazwa_wyd COLLATE NOCASE ASC";
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String Wydawnictwo;
            while(result.next()) {
                id = result.getInt("id_wydawnictwa");           
                Wydawnictwo = result.getString("nazwa_wyd");

                Wydawnictwa.add(new Wydawnictwo(id, Wydawnictwo));
    
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Wydawnictwa;
    } 
    
    
    
    
    public String[][] selectCzytelnicyToArray() {
        String select="SELECT * FROM czytelnicy";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_czytelnika")); //0
                lista1.add(result.getString("imie"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("pesel"));      //3
                lista1.add(result.getString("DOB"));        //4
                lista1.add(result.getString("username"));   //6
                lista1.add(result.getString("email"));      //6
                lista1.add(result.getString("password"));   //7
                lista1.add(result.getFloat("zadluzenie"));  //8
                lista1.add(result.getInt("miasto_id"));     //9
                lista1.add(result.getInt("ulica_id"));      //10
                lista1.add(result.getString("numer_domu")); //11
                lista1.add(result.getString("telefon"));    //12   
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        int rozmiar=lista1.size();
         String[][] tmp = new String[rozmiar/13][9];
         for (int i=0, j=0; i<(rozmiar/13); i++, j+=13)
         {
             tmp[i][0]=String.format("%06d", lista1.get(j));
             tmp[i][1]=(String)lista1.get(j+1);
             tmp[i][2]=(String)lista1.get(j+2);
             tmp[i][3]=(String)lista1.get(j+3);
             tmp[i][4]=(String)lista1.get(j+4);
             tmp[i][5]=(String)lista1.get(j+5);
             tmp[i][6]=(String)lista1.get(j+6);
             String tempo=(String.format("%d", lista1.get(j+10))+" "+String.format("%d", lista1.get(j+9))+" "+(String)lista1.get(j+11));
             tmp[i][7]=tempo;
             tmp[i][8]=(String)lista1.get(j+12);
         }
        return tmp;
    }
    
    public String[][] selectCzytelnicyZAdresem(String a,String be,String c,String d,String ee,String f,String g,String h,String ii) {
        String select="SELECT czytelnicy.id_czytelnika, czytelnicy.imie, czytelnicy.nazwisko, czytelnicy.pesel, czytelnicy.DOB, czytelnicy.username, czytelnicy.email, ulice.ulica, czytelnicy.numer_domu, miasta.miasto, miasta.kod, czytelnicy.telefon "
                + "FROM czytelnicy INNER JOIN ulice ON ulice.id_ulice = czytelnicy.ulica_id INNER JOIN miasta ON miasta.id_miasta = czytelnicy.miasto_id "
                + "where czytelnicy.id_czytelnika like '"+a+"' AND czytelnicy.imie like '"+be+"' AND czytelnicy.nazwisko like '"+c+"' AND czytelnicy.pesel like '"+d+"' AND czytelnicy.DOB like '"+ee+"' AND czytelnicy.username like '"+f+"' AND czytelnicy.email like '"+g+"' AND (miasta.miasto like '"+h+"' OR ulice.ulica like '"+h+"' OR miasta.kod like '"+h+"') AND czytelnicy.telefon like '"+ii+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_czytelnika")); //0
                lista1.add(result.getString("imie"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("pesel"));      //3
                lista1.add(result.getString("DOB"));        //4
                lista1.add(result.getString("username"));   //5
                lista1.add(result.getString("email"));      //6
                lista1.add(result.getString("ulica"));     //7
                lista1.add(result.getString("numer_domu"));      //8
                lista1.add(result.getString("miasto")); //9
                lista1.add(result.getString("kod")); //10
                lista1.add(result.getString("telefon"));    //11   
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        int rozmiar=lista1.size();
        System.out.println(rozmiar);
         String[][] tmp = new String[rozmiar/12][9];
         for (int i=0, j=0; i<(rozmiar/12); i++, j+=12)
         {
             System.out.println(i+" ");
             tmp[i][0]=String.format("%06d", lista1.get(j));
             tmp[i][1]=(String)lista1.get(j+1);
             tmp[i][2]=(String)lista1.get(j+2);
             tmp[i][3]=(String)lista1.get(j+3);
             tmp[i][4]=(String)lista1.get(j+4);
             tmp[i][5]=(String)lista1.get(j+5);
             tmp[i][6]=(String)lista1.get(j+6);
             String tempo=lista1.get(j+7)+"  \t"+lista1.get(j+8)+" - "+lista1.get(j+9)+"  \t"+lista1.get(j+10);
             tmp[i][7]=tempo;
             tmp[i][8]=(String)lista1.get(j+11);
         }
        return tmp;
    }
    
    
    public String[][] selectPracownicy(String a,String be,String c,String d) {
        String select="select id_pracownika, imie, nazwisko, username from pracownicy where id_pracownika like '"+a+"' AND imie like '"+be+"' AND nazwisko like '"+c+"' AND username like '"+d+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_pracownika")); //0
                lista1.add(result.getString("imie"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("username"));      //3
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        int rozmiar=lista1.size();
        System.out.println(rozmiar);
         String[][] tmp = new String[rozmiar/4][4];
         for (int i=0, j=0; i<(rozmiar/4); i++, j+=4)
         {
             tmp[i][0]=String.format("%06d", lista1.get(j));
             tmp[i][1]=(String)lista1.get(j+1);
             tmp[i][2]=(String)lista1.get(j+2);
             tmp[i][3]=(String)lista1.get(j+3);
         }
        return tmp;
    }
    
    public String[][] selectCzytelnicyZAdresem_WherePESEL(String pesel) {
        String select="SELECT czytelnicy.id_czytelnika, czytelnicy.imie, czytelnicy.nazwisko, czytelnicy.pesel, czytelnicy.DOB, czytelnicy.username, czytelnicy.password, czytelnicy.email, ulice.ulica, czytelnicy.numer_domu, miasta.miasto, miasta.kod, czytelnicy.telefon FROM czytelnicy INNER JOIN ulice ON ulice.id_ulice = czytelnicy.ulica_id INNER JOIN miasta ON miasta.id_miasta = czytelnicy.miasto_id WHERE czytelnicy.pesel='"+pesel+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_czytelnika")); //0
                lista1.add(result.getString("imie"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("pesel"));      //3
                lista1.add(result.getString("DOB"));        //4
                lista1.add(result.getString("email"));   //5
                lista1.add(result.getString("username"));   //6
                lista1.add(result.getString("password"));      //7
                lista1.add(result.getString("ulica"));     //8
                lista1.add(result.getString("numer_domu"));      //9
                lista1.add(result.getString("miasto")); //10
                lista1.add(result.getString("kod")); //11
                lista1.add(result.getString("telefon"));    //12   
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Blad selectczytelnicyz adresem_where pesel");
            return null;
        }
        int rozmiar=lista1.size();
         String[][] tmp = new String[rozmiar/13][10];
         for (int i=0, j=0; i<(rozmiar/13); i++, j+=13)
         {
             tmp[i][0]=String.format("%06d", lista1.get(j));
             tmp[i][1]=(String)lista1.get(j+1);
             tmp[i][2]=(String)lista1.get(j+2);
             tmp[i][3]=(String)lista1.get(j+3);
             tmp[i][4]=(String)lista1.get(j+4);
             tmp[i][5]=(String)lista1.get(j+5);
             tmp[i][6]=(String)lista1.get(j+6);
             tmp[i][7]=(String)lista1.get(j+7);
             String tempo=lista1.get(j+8)+"  "+lista1.get(j+9)+" - "+lista1.get(j+10)+"  "+lista1.get(j+11);
             tmp[i][8]=tempo;
             tmp[i][9]=(String)lista1.get(j+12);
         }
        return tmp;
    }
    
    
    
    public List<Czytelnik> selectCzytelnicyByName(String name) {
        List<Czytelnik> czytelnicyByName = new LinkedList<Czytelnik>();
        String szukane=name, select; 
        select =("SELECT * FROM czytelnicy where imie='"+szukane+"'"); 
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            int id;
            String imie, nazwisko, pesel;
            while(result.next()) {
                id = result.getInt("id_czytelnika");
                imie = result.getString("imie");
                nazwisko = result.getString("nazwisko");
                pesel = result.getString("pesel");
                czytelnicyByName.add(new Czytelnik(id, imie, nazwisko, pesel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        return czytelnicyByName;
    }
    
    public List selectCzytelnicyByPESEL(String pesel) {
        //List<Czytelnik> czytelnicyByPESEL = new LinkedList<Czytelnik>();
        String select="SELECT czytelnicy.id_czytelnika, czytelnicy.imie, czytelnicy.nazwisko, czytelnicy.pesel, czytelnicy.DOB, czytelnicy.username, czytelnicy.password, czytelnicy.email, ulice.ulica, czytelnicy.numer_domu, miasta.miasto, miasta.kod, czytelnicy.telefon, czytelnicy.zadluzenie FROM czytelnicy INNER JOIN ulice ON ulice.id_ulice = czytelnicy.ulica_id INNER JOIN miasta ON miasta.id_miasta = czytelnicy.miasto_id WHERE czytelnicy.pesel='"+pesel+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_czytelnika")); //0
                lista1.add(result.getString("imie"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("pesel"));      //3
                lista1.add(result.getString("DOB"));        //4
                lista1.add(result.getString("email"));   //5
                lista1.add(result.getString("username"));   //6
                lista1.add(result.getString("password"));      //7
                lista1.add(result.getString("ulica"));     //8
                lista1.add(result.getString("numer_domu"));      //9
                lista1.add(result.getString("miasto")); //10
                lista1.add(result.getString("kod")); //11
                lista1.add(result.getString("telefon"));    //12 
                lista1.add(result.getFloat("zadluzenie"));    //13
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Blad selectczytelnicyz adresem_where ID");
            return null;
        }
        
        return lista1;
    }
    
    public List selectBookByID(int id) {
        //List<Czytelnik> czytelnicyByPESEL = new LinkedList<Czytelnik>();
        String select="SELECT ksiazki.id_ksiazki, ksiazki.tytul, autorzy.nazwisko, autorzy.imie, kategorie.nazwa_kat, dzialy.nazwa_dzi, gatunki.nazwa_gatunku, ksiazki.opis FROM ksiazki JOIN autorzy ON ksiazki.autor=autorzy.id_autora JOIN kategorie ON ksiazki.id_kat=kategorie.id_kategori JOIN dzialy ON ksiazki.id_dzial=dzialy.id_dzialu JOIN gatunki ON ksiazki.id_gatunek=gatunki.id_gatunku  where ksiazki.id_ksiazki='"+id+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_ksiazki")); //0
                lista1.add(result.getString("tytul"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("imie"));      //3
                lista1.add(result.getString("nazwa_kat"));        //4
                lista1.add(result.getString("nazwa_dzi"));   //5
                lista1.add(result.getString("nazwa_gatunku"));   //6
                lista1.add(result.getString("opis"));      //7
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Blad select books where ID");
            return null;
        }
        
        return lista1;
    }
    
    public List selectEgzemplarzByID(int id) {
        //List<Czytelnik> czytelnicyByPESEL = new LinkedList<Czytelnik>();
        String select="select egzemplarze.id_egzemplarza, ksiazki.tytul, autorzy.nazwisko, autorzy.imie, lokalizacje.nazwa_lokalizacji, stany.nazwa_stanu, wydawnictwa.nazwa_wyd, egzemplarze.rok_wyd, egzemplarze.jezyk FROM egzemplarze INNER JOIN ksiazki ON egzemplarze.id_ksiazki=ksiazki.id_ksiazki JOIN autorzy ON ksiazki.autor=autorzy.id_autora JOIN lokalizacje ON egzemplarze.lokalizacja=lokalizacje.id_lokalizacji JOIN stany ON egzemplarze.stan=stany.id_stanu JOIN wydawnictwa ON egzemplarze.id_wyd=wydawnictwa.id_wydawnictwa where egzemplarze.id_egzemplarza like '"+id+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_egzemplarza")); //0
                lista1.add(result.getString("tytul"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("imie"));      //3
                lista1.add(result.getString("nazwa_lokalizacji"));        //4
                lista1.add(result.getString("nazwa_stanu"));   //5
                lista1.add(result.getString("nazwa_wyd"));      //6
                lista1.add(result.getString("rok_wyd"));      //7
                lista1.add(result.getString("jezyk"));      //8
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Blad select egzemplarz where ID");
            return null;
        }
        
        return lista1;
    }
    
    
    public boolean updateCzytelnikImie(String imie, int id)  {
        String komenda;
        komenda = "UPDATE czytelnicy SET imie='"+imie+"' WHERE id_czytelnika="+id;
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean updatePracownik(int id, String imie, String nazwisko, String login, String password) {
        String komenda;
        komenda = "UPDATE pracownicy SET imie='"+imie+"',  nazwisko='"+nazwisko+"', username='"+login+"', password='"+password+"' WHERE id_pracownika="+id;
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli pracownicy");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean updatePracownik(int id, String imie, String nazwisko, String login) {
        String komenda;
        komenda = "UPDATE pracownicy SET imie='"+imie+"',  nazwisko='"+nazwisko+"', username='"+login+"' WHERE id_pracownika="+id;
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli pracownicy");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean updateWypozyczenieDataZwrotu(int id, String data)  {
        String komenda;
        komenda = "UPDATE wypozyczenia SET data_zwrotu='"+data+"' WHERE id_wypozycz="+id;
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli wypozyczenia");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean updateWypozyczenieDataPlanowana(int id, String data)  {
        String komenda;
        komenda = "UPDATE wypozyczenia SET data_planowana='"+data+"' WHERE id_wypozycz="+id;
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli wypozyczenia - data planowana");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    

    
    
    public boolean editCzytelnik(String PESEL, String name, String surname, String username, String nr, String email, String phone, Float debt)  {
        System.out.println("WYWOLANIE EDIT user");
        String komenda;
       
        komenda = "UPDATE czytelnicy SET imie='"+name+"', nazwisko='"+surname+"',username='"+username+"',numer_domu='"+nr+"',email='"+email+"',telefon='"+phone+"',zadluzenie='"+debt+"' WHERE pesel="+PESEL;
        
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli (edit user)");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean editBook(int id_ksiazki, String tytul, int id_dzial, int id_gatunek, int id_kat, String opis)  {
        System.out.println("WYWOLANIE EDIT book");
        String komenda;
       
        komenda = "UPDATE ksiazki SET tytul='"+tytul+"', id_dzial='"+id_dzial+"', id_gatunek='"+id_gatunek+"',id_kat='"+id_kat+"',opis='"+opis+"' WHERE id_ksiazki="+id_ksiazki;
        
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli (edit book)");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean editEgzemplarz(int id_egzemplarza, int lok)  {
        System.out.println("WYWOLANIE EDIT egzemplarz");
        String komenda;
       
        komenda = "UPDATE egzemplarze SET lokalizacja="+lok+" WHERE id_egzemplarza="+id_egzemplarza;
        
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli (edit egzemplarz)");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean editEgzemplarz(int id_egzemplarza, int lok, int sta, int wyd, String rok, String jezyk)  {
        System.out.println("WYWOLANIE EDIT egzemplarz");
        String komenda;
       
        komenda = "UPDATE egzemplarze SET lokalizacja='"+lok+"', stan='"+sta+"', id_wyd='"+wyd+"',rok_wyd='"+rok+"',jezyk='"+jezyk+"' WHERE id_egzemplarza="+id_egzemplarza;
        
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy zmianie w tabeli (edit egzemplarz)");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    
    public boolean DeleteCzytelnikId(String id)  {
        String komenda;
        komenda = "DELETE FROM czytelnicy WHERE id_czytelnika="+id;
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy usuwaniu z tabeli");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean DeletePracownikId(String id)  {
        String komenda;
        komenda = "DELETE FROM pracownicy WHERE id_pracownika="+id;
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy usuwaniu z tabeli pracownicy");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    public boolean DeleteOneUniwersalWhereID(String where, String from, String kolumna)  {
        String komenda;
        komenda = "DELETE FROM "+from+" WHERE "+kolumna+"="+where;
        System.out.println(komenda);
        try {
            stat.executeUpdate(komenda);
        } catch (SQLException e) {
            System.err.println("Blad przy usuwaniu z tabeli");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Ksiazka> selectKsiazki() {
        List<Ksiazka> ksiazki = new LinkedList<Ksiazka>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM ksiazki");
            int id;
            String tytul, autor, gatunek;
            while(result.next()) {
                id = result.getInt("id_ksiazki");
                tytul = result.getString("tytul");
                autor = result.getString("autor");
                gatunek = result.getString("gatunek");
                //ksiazki.add(new Ksiazka(id, tytul, autor, gatunek));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return ksiazki;
    }
    
    
    public String[][] selectBooksToTable(String a, String be, String c, String d, String ee, String f) {
        String select="select ksiazki.id_ksiazki, ksiazki.tytul, autorzy.nazwisko, autorzy.imie, dzialy.nazwa_dzi, gatunki.nazwa_gatunku, kategorie.nazwa_kat "
                + "FROM ksiazki INNER JOIN autorzy ON ksiazki.autor=autorzy.id_autora INNER JOIN dzialy ON ksiazki.id_dzial=dzialy.id_dzialu INNER JOIN gatunki ON gatunki.id_gatunku=ksiazki.id_gatunek INNER JOIN kategorie ON ksiazki.id_kat=kategorie.id_kategori "
                + "where ksiazki.id_ksiazki like '"+a+"' AND ksiazki.tytul like '"+be+"' AND (autorzy.imie like '"+c+"' OR autorzy.nazwisko like '"+c+"') AND dzialy.nazwa_dzi like '"+d+"' AND gatunki.nazwa_gatunku like '"+ee+"' AND kategorie.nazwa_kat like '"+f+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_ksiazki")); //0
                lista1.add(result.getString("tytul"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("imie"));      //3
                lista1.add(result.getString("nazwa_dzi"));        //4
                lista1.add(result.getString("nazwa_gatunku"));   //5
                lista1.add(result.getString("nazwa_kat"));      //6  
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        int rozmiar=lista1.size();
        System.out.println(rozmiar);
         String[][] tmp = new String[rozmiar/7][6];
         for (int i=0, j=0; i<(rozmiar/7); i++, j+=7)
         {
             System.out.println(i+" ");
             tmp[i][0]=String.format("%08d", lista1.get(j));
             tmp[i][1]=(String)lista1.get(j+1);
             String tempo=lista1.get(j+2)+"  "+lista1.get(j+3);
             tmp[i][2]=tempo;
             tmp[i][3]=(String)lista1.get(j+4);
             tmp[i][4]=(String)lista1.get(j+5);
             tmp[i][5]=(String)lista1.get(j+6);
         }
        return tmp;
    }
    
    
    public String[][] selectWypozyczeniaToTable(String a, String be, String c, String d, String ee, String f, String g) {
        String select="select wypozyczenia.id_wypozycz, ksiazki.tytul, autorzy.nazwisko, autorzy.imie, czytelnicy.nazwisko as czy_naz, czytelnicy.imie as czy_imi, wypozyczenia.data_wypozyczenia, wypozyczenia.data_planowana, wypozyczenia.data_zwrotu "
                + "FROM wypozyczenia JOIN egzemplarze ON wypozyczenia.id_egzemplarza=egzemplarze.id_egzemplarza JOIN czytelnicy ON wypozyczenia.id_czytelnika=czytelnicy.id_czytelnika JOIN ksiazki ON ksiazki.id_ksiazki = egzemplarze.id_ksiazki JOIN autorzy ON autorzy.id_autora = ksiazki.autor "
                + "where wypozyczenia.id_wypozycz like '"+a+"' AND ksiazki.tytul like '"+be+"' AND (autorzy.imie like '"+c+"' OR autorzy.nazwisko like '"+c+"') AND (czytelnicy.nazwisko like '"+d+"' OR czytelnicy.imie like '"+d+"') AND wypozyczenia.data_wypozyczenia like '"+ee+"' AND wypozyczenia.data_planowana like '"+f+"' AND wypozyczenia.data_zwrotu like '"+g+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_wypozycz")); //0
                lista1.add(result.getString("tytul"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("imie"));      //3
                lista1.add(result.getString("czy_naz"));        //4
                lista1.add(result.getString("czy_imi"));   //5
                lista1.add(result.getString("data_wypozyczenia"));      //6  
                lista1.add(result.getString("data_planowana"));      //7  
                lista1.add(result.getString("data_zwrotu"));      //8  
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        int rozmiar=lista1.size();
        System.out.println(rozmiar);
         String[][] tmp = new String[rozmiar/9][7];
         for (int i=0, j=0; i<(rozmiar/9); i++, j+=9)
         {
             System.out.println(i+" ");
             tmp[i][0]=String.format("%08d", lista1.get(j));
             tmp[i][1]=(String)lista1.get(j+1);
             String tempo=lista1.get(j+2)+"  "+lista1.get(j+3);
             tmp[i][2]=tempo;
             String tempo2=lista1.get(j+4)+"  "+lista1.get(j+5);
             tmp[i][3]=tempo2;
             tmp[i][4]=(String)lista1.get(j+6);
             tmp[i][5]=(String)lista1.get(j+7);
             tmp[i][6]=(String)lista1.get(j+8);
         }
        return tmp;
    }
    
    
    
    public String[][] selectEgzemplarzeToTable(int id_ksiazki) {
        String select="select egzemplarze.id_egzemplarza, ksiazki.tytul, autorzy.nazwisko, autorzy.imie, lokalizacje.nazwa_lokalizacji, stany.nazwa_stanu, wydawnictwa.nazwa_wyd, egzemplarze.rok_wyd, egzemplarze.jezyk FROM egzemplarze INNER JOIN ksiazki ON egzemplarze.id_ksiazki=ksiazki.id_ksiazki JOIN autorzy ON ksiazki.autor=autorzy.id_autora JOIN lokalizacje ON egzemplarze.lokalizacja=lokalizacje.id_lokalizacji JOIN stany ON egzemplarze.stan=stany.id_stanu JOIN wydawnictwa ON egzemplarze.id_wyd=wydawnictwa.id_wydawnictwa where ksiazki.id_ksiazki like '"+id_ksiazki+"';";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_egzemplarza")); //0
                lista1.add(result.getString("tytul"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("imie"));      //3
                lista1.add(result.getString("nazwa_lokalizacji"));        //4
                lista1.add(result.getString("nazwa_stanu"));   //5
                lista1.add(result.getString("nazwa_wyd"));      //6
                lista1.add(result.getString("rok_wyd"));      //7
                lista1.add(result.getString("jezyk"));      //8
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        int rozmiar=lista1.size();
        System.out.println(rozmiar);
         String[][] tmp = new String[rozmiar/9][8];
         for (int i=0, j=0; i<(rozmiar/9); i++, j+=9)
         {
             System.out.println(i+" ");
             tmp[i][0]=String.format("%08d", lista1.get(j));
             tmp[i][1]=(String)lista1.get(j+1);
             String tempo=lista1.get(j+2)+"  "+lista1.get(j+3);
             tmp[i][2]=tempo;
             tmp[i][3]=(String)lista1.get(j+4);
             tmp[i][4]=(String)lista1.get(j+5);
             tmp[i][5]=(String)lista1.get(j+6);
             tmp[i][6]=(String)lista1.get(j+7);
             tmp[i][7]=(String)lista1.get(j+8);
         }
        return tmp;
    }
    
    public String[][] selectEgzemplarzeToTableAll() {
        String select="select egzemplarze.id_egzemplarza, ksiazki.tytul, autorzy.nazwisko, autorzy.imie, lokalizacje.nazwa_lokalizacji, stany.nazwa_stanu, wydawnictwa.nazwa_wyd, egzemplarze.rok_wyd, egzemplarze.jezyk FROM egzemplarze INNER JOIN ksiazki ON egzemplarze.id_ksiazki=ksiazki.id_ksiazki JOIN autorzy ON ksiazki.autor=autorzy.id_autora JOIN lokalizacje ON egzemplarze.lokalizacja=lokalizacje.id_lokalizacji JOIN stany ON egzemplarze.stan=stany.id_stanu JOIN wydawnictwa ON egzemplarze.id_wyd=wydawnictwa.id_wydawnictwa;";
        List lista1 = new ArrayList();
        System.out.println(select);
        try {
            ResultSet result = stat.executeQuery(select);
            while(result.next()) {
                lista1.add(result.getInt("id_egzemplarza")); //0
                lista1.add(result.getString("tytul"));       //1
                lista1.add(result.getString("nazwisko"));   //2
                lista1.add(result.getString("imie"));      //3
                lista1.add(result.getString("nazwa_lokalizacji"));        //4
                lista1.add(result.getString("nazwa_stanu"));   //5
                lista1.add(result.getString("nazwa_wyd"));      //6
                lista1.add(result.getString("rok_wyd"));      //7
                lista1.add(result.getString("jezyk"));      //8
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        int rozmiar=lista1.size();
        System.out.println(rozmiar);
         String[][] tmp = new String[rozmiar/9][8];
         for (int i=0, j=0; i<(rozmiar/9); i++, j+=9)
         {
             System.out.println(i+" ");
             tmp[i][0]=String.format("%08d", lista1.get(j));
             tmp[i][1]=(String)lista1.get(j+1);
             String tempo=lista1.get(j+2)+"  "+lista1.get(j+3);
             tmp[i][2]=tempo;
             tmp[i][3]=(String)lista1.get(j+4);
             tmp[i][4]=(String)lista1.get(j+5);
             tmp[i][5]=(String)lista1.get(j+6);
             tmp[i][6]=(String)lista1.get(j+7);
             tmp[i][7]=(String)lista1.get(j+8);
         }
        return tmp;
    }
    
    
    
     public void CzytelnikImie (String imie, List<Czytelnik> czytelnicy){
        for(Czytelnik c: czytelnicy)
            if (c.getImie().equalsIgnoreCase(imie))  
                System.out.println(c);
    }
     
     

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Problem z zamknieciem polaczenia");
            e.printStackTrace();
        }
    }
}