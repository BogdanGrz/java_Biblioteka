package Biblioteka;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.Czytelnik;
import model.Ksiazka;
import model.Wypozyczenie;
import model.jointest;

public class Biblioteka {

    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:biblioteka.db";

    private Connection conn;
    private Statement stat;

    public Biblioteka() {
        try {
            Class.forName(Biblioteka.DRIVER);
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
        String createCzytelnicy = "CREATE TABLE IF NOT EXISTS czytelnicy (id_czytelnika INTEGER(8) PRIMARY KEY AUTOINCREMENT, imie varchar(255), nazwisko varchar(255), pesel int)";
        String createKsiazki = "CREATE TABLE IF NOT EXISTS ksiazki (id_ksiazki INTEGER PRIMARY KEY AUTOINCREMENT, tytul varchar(255), autor varchar(255), gatunek varchar(255))";
        String createWypozyczenia = "CREATE TABLE IF NOT EXISTS wypozyczenia (id_wypozycz INTEGER PRIMARY KEY AUTOINCREMENT, id_czytelnika int, id_ksiazki int)";
        try {
            stat.execute(createCzytelnicy);
            stat.execute(createKsiazki);
            stat.execute(createWypozyczenia);
        } catch (SQLException e) {
            System.err.println("Blad przy tworzeniu tabeli");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertCzytelnik(String imie, String nazwisko, String pesel) {
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
    }

    public boolean insertKsiazka(String tytul, String autor, String gatunek) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into ksiazki values (NULL, ?, ?, ?);");
            prepStmt.setString(1, tytul);
            prepStmt.setString(2, autor);
            prepStmt.setString(3, gatunek);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy wypozyczaniu");
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
    
    public boolean updateCzytelnik(String id, String wartosc, int column)  {
        System.out.println("WYWOLANIE UPDATE");
        String komenda;
        if (column==1) 
        komenda = "UPDATE czytelnicy SET imie='"+wartosc+"' WHERE id_czytelnika="+id;
        else if (column==2)
        komenda = "UPDATE czytelnicy SET nazwisko='"+wartosc+"' WHERE id_czytelnika="+id; 
        else if (column==3) komenda = "UPDATE czytelnicy SET pesel='"+wartosc+"' WHERE id_czytelnika="+id; 
        else return false;
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
                ksiazki.add(new Ksiazka(id, tytul, autor, gatunek));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return ksiazki;
    }
    
     public List <jointest> selectJoin() {
        List<jointest> selectJoin = new LinkedList<jointest>();
        try {
            
            ResultSet result = stat.executeQuery("SELECT tytul, id_czytelnika, wy.id_ksiazki FROM ksiazki ks INNER JOIN wypozyczenia wy on ks.id_ksiazki = wy.id_ksiazki");
            int idcz, idks;
            String tytul, czyt;
            //int rowCount = result.last() ? result.getRow() : 0;
            //result.beforeFirst();
           // System.out.println(rowCount);
            while(result.next()) {
                idks = result.getInt("id_ksiazki");
                tytul = result.getString("tytul");
                idcz = result.getInt("id_czytelnika");
                
                //autor = result.getString("autor");
                //gatunek = result.getString("gatunek");
                selectJoin.add(new jointest(idks, idcz, tytul));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        return selectJoin;
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