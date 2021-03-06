package projektbiblioteka;

/**
 *
 * @author B.Grzadzielewski
 */
import java.sql.ResultSet;
import model.Czytelnik;
import model.Ksiazka;
import model.Wypozyczenie;
import model.Miasto;
import Helpers.SendEmail;
import Biblioteka.DatabaseAPI;
import Helpers.Daty;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Integer.parseInt;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import static java.util.Collections.list;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.beans.binding.Bindings.select;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.*;
import static Helpers.PasswordEncryption.*;
import Helpers.PasswordGenerator;
import static Helpers.SendEmail.*;
import static Helpers.PasswordGenerator.*;
import Helpers.ProgressBar;
import model.Ulica;
import static Helpers.Daty.czyPrzyszlosc;
import Helpers.PasswordEncryption;
import static Helpers.Pesel.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import model.Autor;
import model.Dzial;
import model.Gatunek;
import model.Kategoria;
import model.Lokalizacja;
import model.Stan;
import model.Wydawnictwo;
import java.net.*;
import javax.swing.UnsupportedLookAndFeelException;

public class BibliotekaApp extends javax.swing.JFrame {
    
    JFrame loading2 = new javax.swing.JFrame();
    ProgressBar myProgressBar = new ProgressBar();
    
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    
    String selectedPesel="BRAK";
    
    
    
    File plik = new File("lang.txt");
    DatabaseAPI b = new DatabaseAPI();
    static String [] language = new String[] {"polska", "Córdoba", "La Plata"}; 
    
    DefaultTableModel model = new DefaultTableModel(new Object[][] {},
     new Object[] { language[0], "Imię","Nazwisko", "Pesel", "DOB", "Uźytkownik", "email", "Adres", "Telefon"});
    
    DefaultTableModel books = new DefaultTableModel(new Object[][] {},
     new Object[] { "ID", "Tytuł", "Autor", "Dział", "Gatunek", "Kategoria"});
    
    DefaultTableModel egzemplarze = new DefaultTableModel(new Object[][] {},
     new Object[] { "ID", "Tytuł", "Autor", "Lokalizacja", "Stan", "Wydawnictwo", "Rok wydania", "Język"});
    
     DefaultTableModel wypozyczenia = new DefaultTableModel(new Object[][] {},
     new Object[] { "ID wyp.", "Tytuł", "Autor", "Czytelnik", "Data wyp.", "Zwrot do", "Data zwrotu"});
     
     DefaultTableModel pracownicy = new DefaultTableModel(new Object[][] {},
     new Object[] { "ID pracownika", "Nazwisko", "Imię", "Login"});

   PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
        .useDigits(true)
        .useLower(true)
        .useUpper(true)
        .usePunctuation(false)
        .build();
    
     
        static String [] language_polski = {"ID", "Córdoba", "La Plata"};
        static String [] language_angielski = {"ID", "Córdoba", "La Plata"};
   
    static void jezyk() throws FileNotFoundException{
        Scanner odczyt = new Scanner(new File("lang.txt"));
        String text = "pol";
        text = odczyt.nextLine();
        System.out.println(text);
        Arrays.fill (language, null);
        if (text.equals("ang")) language = language_angielski.clone();
        else language = language_polski.clone();
        
    }    
    
    DefaultComboBoxModel modelbox = new DefaultComboBoxModel<>(new String[] {"Inne" });
    DefaultComboBoxModel modelboxmiasta = new DefaultComboBoxModel<>(new String[] {"Inne" });
    DefaultComboBoxModel modelboxulice = new DefaultComboBoxModel<>(new String[] {"Inna" });
    DefaultComboBoxModel modelboxgatunki = new DefaultComboBoxModel<>(new String[] {"" });
    DefaultComboBoxModel modelboxkategorie = new DefaultComboBoxModel<>(new String[] {"" });
    DefaultComboBoxModel modelboxdzialy = new DefaultComboBoxModel<>(new String[] {"" });
    DefaultComboBoxModel modelboxautorzy = new DefaultComboBoxModel<>(new String[] {"" });
    DefaultComboBoxModel modelboxstany = new DefaultComboBoxModel<>(new String[] {"" });
    DefaultComboBoxModel modelboxlokalizacje = new DefaultComboBoxModel<>(new String[] {"" });
    DefaultComboBoxModel modelboxwydawnictwa = new DefaultComboBoxModel<>(new String[] {"" });
 
    
    void czytAddLoading(){
        listaMiastaKod();
        
        listaUlice();
        czyt_add_imie.setText("");czyt_add_nazwisko.setText("");czyt_add_pesel.setText("");czyt_add_DOB.setText("");
        czyt_add_email.setText("");czyt_add_username.setText("");czyt_add_password.setText("");czyt_add_ulica.setText("");
        czyt_add_miasto.setText("");czyt_add_nr.setText("");czyt_add_telefon.setText(""); 
    }
    
    public void listadd() {
        List<Czytelnik> czytelnicy;
         czytelnicy=b.selectCzytelnicy();
         modelbox.addElement(czytelnicy.get(0).getImie()+"  "+czytelnicy.get(0).getNazwisko());
         modelbox.addElement(czytelnicy.get(1).getImie()+"  "+czytelnicy.get(1).getNazwisko());
         modelbox.addElement(czytelnicy.get(2).getImie()+"  "+czytelnicy.get(2).getNazwisko());
    }
    public void listaMiastaKod() {
        List<Miasto> miasta;
         miasta=b.selectMiasta();
         modelboxmiasta.removeAllElements();
         modelboxmiasta.addElement(" Inne...");
         dodajMiasto.setVisible(false);
         
         for (int i=0; i<miasta.size();i++)
         modelboxmiasta.addElement(miasta.get(i).getMiasto()+"  "+miasta.get(i).getKod());
         modelboxmiasta.setSelectedItem(null);
    }
    
    public void listaAutorzy() {
        List<Autor> autorzy;
         autorzy=b.selectAutorzy();
         modelboxautorzy.removeAllElements();
         modelboxautorzy.addElement(" Inny...");
        // modelboxautorzy.setVisible(false);
         
         for (int i=0; i<autorzy.size();i++)
         modelboxautorzy.addElement(autorzy.get(i).getNazwisko()+"  "+autorzy.get(i).getImie());
         modelboxautorzy.setSelectedItem(null);
    }
    
     public void listaUlice() {
        
        List<Ulica> ulice;
         ulice=b.selectUlice();
         modelboxulice.removeAllElements();
         modelboxulice.addElement(" Inna...");
         for (int i=0; i<ulice.size();i++){
         modelboxulice.addElement(ulice.get(i).getUlica());
         myProgressBar.setValue(i);
         }
         modelboxulice.setSelectedItem(null);
    }
     
     public void listaGatunki() {
        
        List<Gatunek> gatunki;
         gatunki=b.selectGatunki();
         modelboxgatunki.removeAllElements();
         //modelboxgatunki.addElement(" Inna...");
         for (int i=0; i<gatunki.size();i++){
         modelboxgatunki.addElement(gatunki.get(i).getGatunek());
         //myProgressBar.setValue(i);
         }
         modelboxgatunki.setSelectedItem(null);
    }
     
     public void listaKategorie() {
        
        List<Kategoria> kategorie;
         kategorie=b.selectKategorie();
         modelboxkategorie.removeAllElements();
         //modelboxgatunki.addElement(" Inna...");
         for (int i=0; i<kategorie.size();i++){
         modelboxkategorie.addElement(kategorie.get(i).getKategoria());
         //myProgressBar.setValue(i);
         }
         modelboxkategorie.setSelectedItem(null);
    }
     
     public void listaDzialy() {
        
        List<Dzial> dzialy;
         dzialy=b.selectDzialy();
         modelboxdzialy.removeAllElements();
         //modelboxgatunki.addElement(" Inna...");
         for (int i=0; i<dzialy.size();i++){
         modelboxdzialy.addElement(dzialy.get(i).getDzial());
         //myProgressBar.setValue(i);
         }
         modelboxdzialy.setSelectedItem(null);
    }
     
      public void listaLokalizacje() {
        
        List<Lokalizacja> lokalizacje;
         lokalizacje=b.selectLokalizacje();
         modelboxlokalizacje.removeAllElements();
         for (int i=0; i<lokalizacje.size();i++){
         modelboxlokalizacje.addElement(lokalizacje.get(i).getLokalizacja());
         }
         modelboxlokalizacje.setSelectedItem(null);
    }
      
        public void listaStany() {
        
        List<Stan> stany;
         stany=b.selectStany();
         modelboxstany.removeAllElements();
         for (int i=0; i<stany.size();i++){
         modelboxstany.addElement(stany.get(i).getStan());
         }
         modelboxstany.setSelectedItem(null);
    }
        
       public void listaWydawnictwa() {
        
        List<Wydawnictwo> wydawnictwa;
         wydawnictwa=b.selectWydawnictwa();
         modelboxwydawnictwa.removeAllElements();
         for (int i=0; i<wydawnictwa.size();i++){
         modelboxwydawnictwa.addElement(wydawnictwa.get(i).getWydawnictwo());
         }
         modelboxwydawnictwa.setSelectedItem(null);
    }
     
     
         
         
     
     static public String firstLetterCaps ( String data )
        {
         if (data == null || data.length() == 0) {
        return data;
         }
    return data.substring(0, 1).toUpperCase() + data.substring(1).toLowerCase();  
        }
    
    /*
    public String[][] ListToArr(List<Czytelnik> czytelnicy) {
        int dl= czytelnicy.size();
        
        String [][] czyt = new String[dl][4];
        for (int i=0; i<dl; i++)
        {
            String tmp=String.format("%06d",czytelnicy.get(i).getId() );
            czyt[i][0]=tmp;
            czyt[i][1]=czytelnicy.get(i).getImie();
            czyt[i][2]=czytelnicy.get(i).getNazwisko();
            czyt[i][3]=czytelnicy.get(i).getPesel();
        }
        return czyt;
    }
    */
    
    void SelectCzytelnicyToTable(String[][] tab) {
         model.setRowCount(0);
         for (int i=0; i<tab.length; i++)
           model.addRow(tab[i]);
    }
    void SelectBooksToTable(String[][] tab) {
         books.setRowCount(0);
         for (int i=0; i<tab.length; i++)
           books.addRow(tab[i]);
    }
    void SelectEgzemplarzeToTable(String[][] tab) {
         egzemplarze.setRowCount(0);
         for (int i=0; i<tab.length; i++)
           egzemplarze.addRow(tab[i]);
    }
    void SelectWypozyczeniaToTable(String[][] tab) {
         wypozyczenia.setRowCount(0);
         for (int i=0; i<tab.length; i++)
           wypozyczenia.addRow(tab[i]);
    }
     void SelectPracownicyToTable(String[][] tab) {
         pracownicy.setRowCount(0);
         for (int i=0; i<tab.length; i++)
           pracownicy.addRow(tab[i]);
    }
    
    
    public BibliotekaApp() {
        initComponents();
        LogowanieFrame.setLocation(dim.width/2-(LogowanieFrame.getSize().width)/2, dim.height/2-LogowanieFrame.getSize().height/2);
        zaloguj.requestFocus();
        LogowanieFrame.setVisible(true);
        //LogowanieFrame.requestFocus();
        zaloguj.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        oknotest = new javax.swing.JFrame();
        czyt_add_imie = new javax.swing.JTextField();
        czyt_add_nazwisko = new javax.swing.JTextField();
        czyt_add_pesel = new javax.swing.JTextField();
        czyt_add_DOB = new javax.swing.JTextField();
        czyt_add_email = new javax.swing.JTextField();
        czyt_add_username = new javax.swing.JTextField();
        czyt_add_password = new javax.swing.JTextField();
        czyt_add_miasto = new javax.swing.JTextField();
        czyt_add_ulica = new javax.swing.JTextField();
        czyt_add_nr = new javax.swing.JTextField();
        czyt_add_telefon = new javax.swing.JTextField();
        dodajButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        lista_miasta = new javax.swing.JComboBox<>(modelboxmiasta);
        lista_ulice = new javax.swing.JComboBox<>(modelboxulice);
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        komunikatCzyt = new javax.swing.JLabel();
        loading = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        dodajMiasto = new javax.swing.JFrame();
        miastoADD = new javax.swing.JTextField();
        kodADD = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        userDetails = new javax.swing.JFrame();
        ID_user = new javax.swing.JLabel();
        user_name = new javax.swing.JTextField();
        user_surname = new javax.swing.JTextField();
        user_DOB = new javax.swing.JTextField();
        user_pesel = new javax.swing.JTextField();
        user_username = new javax.swing.JTextField();
        user_street = new javax.swing.JTextField();
        user_nr = new javax.swing.JTextField();
        user_email = new javax.swing.JTextField();
        user_phone = new javax.swing.JTextField();
        user_city = new javax.swing.JTextField();
        user_debt = new javax.swing.JTextField();
        user_close = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        user_edycja = new javax.swing.JButton();
        booksDetails = new javax.swing.JFrame();
        jPanel4 = new javax.swing.JPanel();
        dzial = new javax.swing.JTextField();
        listDzial = new javax.swing.JComboBox<>(modelboxdzialy);
        kategoria = new javax.swing.JTextField();
        listGatunek = new javax.swing.JComboBox<>(modelboxgatunki);
        books_close = new javax.swing.JButton();
        listKategoria = new javax.swing.JComboBox<>(modelboxkategorie);
        jLabel32 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        books_edycja1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        opis = new javax.swing.JTextArea();
        jButton8 = new javax.swing.JButton();
        ID_book = new javax.swing.JLabel();
        title = new javax.swing.JTextField();
        autor = new javax.swing.JTextField();
        gatunek = new javax.swing.JTextField();
        oknoDodajBook = new javax.swing.JFrame();
        jPanel5 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        dodajButtonNew = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        tittleNew = new javax.swing.JTextField();
        autorNew = new javax.swing.JTextField();
        listDzialNew = new javax.swing.JComboBox<>(modelboxdzialy);
        listGatunekNew = new javax.swing.JComboBox<>(modelboxgatunki);
        tytul = new javax.swing.JLabel();
        dzialNew = new javax.swing.JTextField();
        gatunekNew = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        kategoriaNew = new javax.swing.JTextField();
        listKategoriaNew = new javax.swing.JComboBox<>(modelboxkategorie);
        jScrollPane4 = new javax.swing.JScrollPane();
        opisNew = new javax.swing.JTextArea();
        jLabel52 = new javax.swing.JLabel();
        listAutorNew = new javax.swing.JComboBox<>(modelboxautorzy);
        dodajAutora = new javax.swing.JFrame();
        nazwiskoAutorAdd = new javax.swing.JTextField();
        imieAutorAdd = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        oknoDodajEgzemplarz = new javax.swing.JFrame();
        jPanel6 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        dodajButtonEgz = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        tittleNew1 = new javax.swing.JTextField();
        autorNew1 = new javax.swing.JTextField();
        listLokalizacja = new javax.swing.JComboBox<>(modelboxlokalizacje);
        listStan = new javax.swing.JComboBox<>(modelboxstany);
        tytul1 = new javax.swing.JLabel();
        lokalizacjaField = new javax.swing.JTextField();
        stanField = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        wydawnictwoField = new javax.swing.JTextField();
        listWydawnictwo = new javax.swing.JComboBox<>(modelboxwydawnictwa);
        jLabel54 = new javax.swing.JLabel();
        rokField = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jezykField = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        newIDEgz = new javax.swing.JTextField();
        egzemplarzEdit = new javax.swing.JFrame();
        jPanel8 = new javax.swing.JPanel();
        lokalizacja = new javax.swing.JTextField();
        listLOK = new javax.swing.JComboBox<>(modelboxlokalizacje);
        wydawnictwo = new javax.swing.JTextField();
        listSTA = new javax.swing.JComboBox<>(modelboxstany);
        books_close2 = new javax.swing.JButton();
        listWYD = new javax.swing.JComboBox<>(modelboxwydawnictwa);
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        egz_edycja = new javax.swing.JButton();
        ID_egzemplarz = new javax.swing.JLabel();
        title2 = new javax.swing.JTextField();
        autor2 = new javax.swing.JTextField();
        stan = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        rok = new javax.swing.JTextField();
        jezyk = new javax.swing.JTextField();
        oknoWypozycz = new javax.swing.JFrame();
        jPanel7 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        wypozyczButton = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        wypozyczEgzemplarz = new javax.swing.JTextField();
        wypozyczCzytelnik = new javax.swing.JTextField();
        tytul2 = new javax.swing.JLabel();
        oknoZwrot = new javax.swing.JFrame();
        jPanel9 = new javax.swing.JPanel();
        jTextField5 = new javax.swing.JTextField();
        zwrotButton = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        zwrotEgzemplarz = new javax.swing.JTextField();
        tytul3 = new javax.swing.JLabel();
        oknoProlongata = new javax.swing.JFrame();
        jPanel10 = new javax.swing.JPanel();
        jTextField6 = new javax.swing.JTextField();
        prolongujButton = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jLabel47 = new javax.swing.JLabel();
        prolongataEgzemplarz = new javax.swing.JTextField();
        tytul4 = new javax.swing.JLabel();
        LogowanieFrame = new javax.swing.JDialog();
        jPanel11 = new javax.swing.JPanel();
        user = new javax.swing.JTextField();
        zaloguj = new javax.swing.JButton();
        jLabel62 = new javax.swing.JLabel();
        pass = new javax.swing.JPasswordField();
        komunikatL = new javax.swing.JLabel();
        oknoDodajPracownika = new javax.swing.JFrame();
        jPanel12 = new javax.swing.JPanel();
        jTextField7 = new javax.swing.JTextField();
        dodajPracownikaButton = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        nazwiskoPracownika = new javax.swing.JTextField();
        tytul5 = new javax.swing.JLabel();
        imiePracownika = new javax.swing.JTextField();
        loginPracownika = new javax.swing.JTextField();
        hasloPracownika = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        oknoEdycjaPracownika = new javax.swing.JFrame();
        jPanel13 = new javax.swing.JPanel();
        jTextField8 = new javax.swing.JTextField();
        zapiszPracownikaButton = new javax.swing.JButton();
        edycjaPracownikaButton = new javax.swing.JButton();
        jLabel69 = new javax.swing.JLabel();
        nazwiskoPracownika1 = new javax.swing.JTextField();
        tytul6 = new javax.swing.JLabel();
        imiePracownika1 = new javax.swing.JTextField();
        loginPracownika1 = new javax.swing.JTextField();
        hasloPracownika1 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        IDprac = new javax.swing.JTextField();
        zmianaHasla = new javax.swing.JDialog();
        jPanel14 = new javax.swing.JPanel();
        zaloguj1 = new javax.swing.JButton();
        jLabel73 = new javax.swing.JLabel();
        pass1 = new javax.swing.JPasswordField();
        userZmiana = new javax.swing.JLabel();
        pass2 = new javax.swing.JPasswordField();
        pass3 = new javax.swing.JPasswordField();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        komunikat = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        zakladki = new javax.swing.JTabbedPane();
        ZakladkaCzytelnicy = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TabelaCzytelnicy = new javax.swing.JTable(model);
        pokazCzytelnikow = new javax.swing.JButton();
        IDfield = new javax.swing.JTextField();
        usunCzyt = new javax.swing.JButton();
        filtr_ID = new javax.swing.JTextField();
        filtr_ID1 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        edycjaButton = new javax.swing.JButton();
        filtr_ID2 = new javax.swing.JTextField();
        filtr_ID3 = new javax.swing.JTextField();
        filtr_ID4 = new javax.swing.JTextField();
        filtr_ID5 = new javax.swing.JTextField();
        filtr_ID6 = new javax.swing.JTextField();
        filtr_ID7 = new javax.swing.JTextField();
        filtr_ID8 = new javax.swing.JTextField();
        pokazCzytelnikow1 = new javax.swing.JButton();
        ZakladkaKsiazki = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelaBooks = new javax.swing.JTable(books);
        pokazKsiazki = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        dodajEgzButton = new javax.swing.JButton();
        books_details = new javax.swing.JButton();
        ID_books_field = new javax.swing.JTextField();
        usunKsiazke = new javax.swing.JButton();
        filtr_ID9 = new javax.swing.JTextField();
        filtr_ID10 = new javax.swing.JTextField();
        filtr_ID11 = new javax.swing.JTextField();
        filtr_ID12 = new javax.swing.JTextField();
        filtr_ID13 = new javax.swing.JTextField();
        filtr_ID14 = new javax.swing.JTextField();
        pokazCzytelnikow2 = new javax.swing.JButton();
        pokazKsiazki1 = new javax.swing.JButton();
        ZakladkaEgzemplarze = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        TabelaEgzemplarze = new javax.swing.JTable(egzemplarze);
        egzemplarz_edit = new javax.swing.JButton();
        ID_egzemplarza_field = new javax.swing.JTextField();
        usunEgzemplarz = new javax.swing.JButton();
        pokazEgzemplarze = new javax.swing.JButton();
        ZakladkaWypozyczenia = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        TabelaWypozyczenia = new javax.swing.JTable(wypozyczenia);
        pokazWypozyczenia = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        dodajEgzButton1 = new javax.swing.JButton();
        books_details1 = new javax.swing.JButton();
        ID_wyp_field = new javax.swing.JTextField();
        filtr_ID15 = new javax.swing.JTextField();
        filtr_ID16 = new javax.swing.JTextField();
        filtr_ID17 = new javax.swing.JTextField();
        filtr_ID18 = new javax.swing.JTextField();
        filtr_ID19 = new javax.swing.JTextField();
        filtr_ID20 = new javax.swing.JTextField();
        pokazCzytelnikow3 = new javax.swing.JButton();
        czysc_filtr_wyp = new javax.swing.JButton();
        filtr_ID21 = new javax.swing.JTextField();
        ID_egz_field = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        ZakladkaPracownicy = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        TabelaPracownicy = new javax.swing.JTable(pracownicy);
        pokazPracownikow = new javax.swing.JButton();
        IDfieldPracownicy = new javax.swing.JTextField();
        usunPracownika = new javax.swing.JButton();
        filtr_ID22 = new javax.swing.JTextField();
        filtr_ID23 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        edycjaButtonPracownicy = new javax.swing.JButton();
        filtr_ID24 = new javax.swing.JTextField();
        filtr_ID25 = new javax.swing.JTextField();
        czyscFiltrPracownicy = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        zalogowanyUser = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        komunikaty = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();

        oknotest.setTitle("Dodaj Czytelnika");
        /*
        oknotest.setLocation(new java.awt.Point(0, 0));
        */
        oknotest.setMinimumSize(new java.awt.Dimension(650, 550));
        oknotest.setResizable(false);
        oknotest.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                oknotestWindowActivated(evt);
            }
        });

        czyt_add_imie.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_imie.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_imie.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_imie.setSelectionColor(new java.awt.Color(255, 102, 102));

        czyt_add_nazwisko.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_nazwisko.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_nazwisko.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_nazwisko.setSelectionColor(new java.awt.Color(255, 102, 102));
        czyt_add_nazwisko.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                czyt_add_nazwiskoActionPerformed(evt);
            }
        });

        czyt_add_pesel.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_pesel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_pesel.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_pesel.setSelectionColor(new java.awt.Color(255, 102, 102));
        czyt_add_pesel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                czyt_add_peselFocusLost(evt);
            }
        });
        czyt_add_pesel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                czyt_add_peselActionPerformed(evt);
            }
        });

        czyt_add_DOB.setEditable(false);
        czyt_add_DOB.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_DOB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_DOB.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_DOB.setSelectionColor(new java.awt.Color(255, 102, 102));

        czyt_add_email.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_email.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_email.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_email.setSelectionColor(new java.awt.Color(255, 102, 102));

        czyt_add_username.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_username.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_username.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_username.setSelectionColor(new java.awt.Color(255, 102, 102));

        czyt_add_password.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_password.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_password.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_password.setSelectionColor(new java.awt.Color(255, 102, 102));

        czyt_add_miasto.setEditable(false);
        czyt_add_miasto.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_miasto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_miasto.setText("wybierz z listy");
        czyt_add_miasto.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_miasto.setName(""); // NOI18N
        czyt_add_miasto.setSelectionColor(new java.awt.Color(255, 102, 102));

        czyt_add_ulica.setEditable(false);
        czyt_add_ulica.setBackground(new java.awt.Color(255, 255, 204));
        czyt_add_ulica.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_ulica.setText("wybierz z listy");
        czyt_add_ulica.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_ulica.setSelectionColor(new java.awt.Color(255, 102, 102));

        czyt_add_nr.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_nr.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_nr.setSelectionColor(new java.awt.Color(255, 102, 102));

        czyt_add_telefon.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        czyt_add_telefon.setMargin(new java.awt.Insets(2, 4, 2, 2));
        czyt_add_telefon.setSelectionColor(new java.awt.Color(255, 102, 102));

        dodajButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dodajButton.setText("Dodaj");
        dodajButton.setEnabled(false);
        dodajButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dodajButtonActionPerformed(evt);
            }
        });

        jButton3.setText("Anuluj");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Imie:");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Nazwisko:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Pesel:");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Data urodzenia:");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("eMail:");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Hasło:");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Miasto:");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Ulica:");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Numer domu:");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Telefon:");

        jButton6.setText("Generuj");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        /*
        lista_miasta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lista_miasta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        lista_miasta.setToolTipText("wybierz z listy");
        */
        lista_miasta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                lista_miastaFocusLost(evt);
            }
        });
        lista_miasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lista_miastaActionPerformed(evt);
            }
        });

        /*
        lista_ulice.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        lista_ulice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lista_uliceActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Dodawanie Czytelnika");

        jLabel16.setForeground(new java.awt.Color(255, 51, 51));
        jLabel16.setText("*");

        jLabel17.setForeground(new java.awt.Color(255, 51, 51));
        jLabel17.setText("*");

        jLabel19.setForeground(new java.awt.Color(255, 51, 51));
        jLabel19.setText("*");

        jLabel20.setText("unikalne");

        jTextField1.setBackground(new java.awt.Color(255, 255, 204));
        jTextField1.setText(" wymagane ");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Nazwa użytkownika:");

        jLabel22.setForeground(new java.awt.Color(255, 51, 51));
        jLabel22.setText("*");

        komunikatCzyt.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        komunikatCzyt.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout oknotestLayout = new javax.swing.GroupLayout(oknotest.getContentPane());
        oknotest.getContentPane().setLayout(oknotestLayout);
        oknotestLayout.setHorizontalGroup(
            oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(oknotestLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(oknotestLayout.createSequentialGroup()
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(oknotestLayout.createSequentialGroup()
                        .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(oknotestLayout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, oknotestLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, oknotestLayout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6))
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, oknotestLayout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addGap(0, 0, 0)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, oknotestLayout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel21)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(oknotestLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(komunikatCzyt, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dodajButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(czyt_add_telefon, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_nr, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_ulica, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_miasto, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_password, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_username, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_email, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_DOB, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_pesel, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_nazwisko, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(czyt_add_imie, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(oknotestLayout.createSequentialGroup()
                            .addComponent(jLabel19)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel20)
                            .addGap(18, 18, 18)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton6)
                        .addComponent(lista_ulice, 0, 210, Short.MAX_VALUE)
                        .addComponent(lista_miasta, 0, 210, Short.MAX_VALUE))
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        oknotestLayout.setVerticalGroup(
            oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(oknotestLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(czyt_add_imie, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(czyt_add_nazwisko, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(czyt_add_pesel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(czyt_add_DOB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(czyt_add_email, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(czyt_add_username, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(czyt_add_password, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lista_miasta, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(czyt_add_miasto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(czyt_add_ulica, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(lista_ulice, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(oknotestLayout.createSequentialGroup()
                        .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(czyt_add_nr, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(czyt_add_telefon, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(oknotestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dodajButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(komunikatCzyt)))
                    .addComponent(jButton3))
                .addContainerGap())
        );

        loading.setUndecorated(true);
        loading.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        loading.setAlwaysOnTop(true);
        loading.setBackground(new java.awt.Color(0, 255, 0));
        loading.setLocation(new java.awt.Point(300, 250));
        loading.setMinimumSize(new java.awt.Dimension(200, 100));
        loading.setResizable(false);
        loading.setType(java.awt.Window.Type.UTILITY);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout loadingLayout = new javax.swing.GroupLayout(loading.getContentPane());
        loading.getContentPane().setLayout(loadingLayout);
        loadingLayout.setHorizontalGroup(
            loadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        loadingLayout.setVerticalGroup(
            loadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loadingLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        dodajMiasto.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dodajMiasto.setTitle("Dodaj Miejscowość");
        dodajMiasto.setMinimumSize(new java.awt.Dimension(320, 200));

        miastoADD.setBackground(new java.awt.Color(255, 255, 204));
        miastoADD.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        kodADD.setBackground(new java.awt.Color(255, 255, 204));
        kodADD.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        kodADD.setSelectedTextColor(new java.awt.Color(255, 102, 102));

        jLabel13.setText("Nazwa Miejscowości");

        jLabel14.setText("Kod pocztowy");

        jButton11.setText("Dodaj");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Anuluj");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dodajMiastoLayout = new javax.swing.GroupLayout(dodajMiasto.getContentPane());
        dodajMiasto.getContentPane().setLayout(dodajMiastoLayout);
        dodajMiastoLayout.setHorizontalGroup(
            dodajMiastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dodajMiastoLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(dodajMiastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(dodajMiastoLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel13))
                    .addComponent(miastoADD, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dodajMiastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14)
                    .addComponent(kodADD))
                .addGap(28, 28, 28))
        );
        dodajMiastoLayout.setVerticalGroup(
            dodajMiastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dodajMiastoLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(dodajMiastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dodajMiastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(miastoADD, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kodADD, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dodajMiastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        userDetails.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        userDetails.setTitle("Czytelnik");
        userDetails.setResizable(false);
        userDetails.setSize(new java.awt.Dimension(680, 400));

        ID_user.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ID_user.setForeground(new java.awt.Color(0, 0, 204));
        ID_user.setText("ID: ");

        user_name.setEditable(false);
        user_name.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        user_name.setText("IMIE");
        user_name.setBorder(null);

        user_surname.setEditable(false);
        user_surname.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        user_surname.setText("NAZWISKO");
        user_surname.setBorder(null);
        user_surname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_surnameActionPerformed(evt);
            }
        });

        user_DOB.setEditable(false);
        user_DOB.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_DOB.setText("DOB");
        user_DOB.setBorder(null);
        user_DOB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_DOBActionPerformed(evt);
            }
        });

        user_pesel.setEditable(false);
        user_pesel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_pesel.setText("PESEL");
        user_pesel.setBorder(null);
        user_pesel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_peselActionPerformed(evt);
            }
        });

        user_username.setEditable(false);
        user_username.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_username.setText("USERNAME");
        user_username.setBorder(null);
        user_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_usernameActionPerformed(evt);
            }
        });

        user_street.setEditable(false);
        user_street.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_street.setText("ULICA");
        user_street.setBorder(null);
        user_street.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_streetActionPerformed(evt);
            }
        });

        user_nr.setEditable(false);
        user_nr.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_nr.setText("NR");
        user_nr.setBorder(null);
        user_nr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_nrActionPerformed(evt);
            }
        });

        user_email.setEditable(false);
        user_email.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_email.setText("EMAIL");
        user_email.setBorder(null);
        user_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_emailActionPerformed(evt);
            }
        });

        user_phone.setEditable(false);
        user_phone.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_phone.setText("TELEFON");
        user_phone.setBorder(null);
        user_phone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_phoneActionPerformed(evt);
            }
        });

        user_city.setEditable(false);
        user_city.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_city.setText("MIASTO");
        user_city.setBorder(null);
        user_city.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_cityActionPerformed(evt);
            }
        });

        user_debt.setEditable(false);
        user_debt.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        user_debt.setText("Zadłuzenie:");
        user_debt.setBorder(null);
        user_debt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_debtActionPerformed(evt);
            }
        });

        user_close.setText("Zamknij");
        user_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_closeActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Adres:");

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("Telefon:");

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Użytkownik:");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("eMail:");

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("PESEL:");

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Zadłużenie:");

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("Data Urodzenia:");

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setText("nr.");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Miejscowość:");

        user_edycja.setText("Edycja");
        user_edycja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_edycjaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout userDetailsLayout = new javax.swing.GroupLayout(userDetails.getContentPane());
        userDetails.getContentPane().setLayout(userDetailsLayout);
        userDetailsLayout.setHorizontalGroup(
            userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userDetailsLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(userDetailsLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(6, 6, 6)
                        .addComponent(user_street, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addGap(2, 2, 2)
                        .addComponent(user_nr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(user_city, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(userDetailsLayout.createSequentialGroup()
                        .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(userDetailsLayout.createSequentialGroup()
                                .addComponent(ID_user, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(user_name, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(user_surname, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(userDetailsLayout.createSequentialGroup()
                                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userDetailsLayout.createSequentialGroup()
                                            .addComponent(jLabel23)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(user_username, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(15, 15, 15))
                                        .addGroup(userDetailsLayout.createSequentialGroup()
                                            .addComponent(jLabel18)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(user_phone, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                    .addGroup(userDetailsLayout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(user_pesel, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(131, 131, 131)))
                                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(userDetailsLayout.createSequentialGroup()
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(user_DOB, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(userDetailsLayout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(user_email, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(userDetailsLayout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(user_debt, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 72, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userDetailsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(user_edycja, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(user_close)
                .addGap(32, 32, 32))
        );
        userDetailsLayout.setVerticalGroup(
            userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userDetailsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ID_user)
                    .addComponent(user_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_surname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(user_DOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_pesel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel27))
                .addGap(20, 20, 20)
                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(user_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addGap(20, 20, 20)
                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(user_street, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_nr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_city, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29))
                .addGap(20, 20, 20)
                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(user_phone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_debt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel26))
                .addGap(18, 18, 18)
                .addGroup(userDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(user_close)
                    .addComponent(user_edycja, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        booksDetails.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        booksDetails.setTitle("Książka");
        booksDetails.setBackground(new java.awt.Color(204, 204, 204));
        booksDetails.setUndecorated(true);
        booksDetails.setResizable(false);
        booksDetails.setSize(new java.awt.Dimension(680, 400));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 51), new java.awt.Color(0, 153, 153)));

        dzial.setEditable(false);
        dzial.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        dzial.setText("DZIAL");
        dzial.setBorder(null);
        dzial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dzialActionPerformed(evt);
            }
        });

        /*
        listDzial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listDzial.setEnabled(false);
        listDzial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listDzialActionPerformed(evt);
            }
        });

        kategoria.setEditable(false);
        kategoria.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        kategoria.setText("KATEGORIA");
        kategoria.setBorder(null);
        kategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kategoriaActionPerformed(evt);
            }
        });

        /*
        listGatunek.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listGatunek.setEnabled(false);
        listGatunek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listGatunekActionPerformed(evt);
            }
        });

        books_close.setText("Zamknij");
        books_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                books_closeActionPerformed(evt);
            }
        });

        /*
        listKategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listKategoria.setEnabled(false);
        listKategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listKategoriaActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel32.setText("Kategoria:");

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setText("Autorzy:");

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setText("Dział:");

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel36.setText("Gatunek:");

        books_edycja1.setText("Edycja");
        books_edycja1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                books_edycja1ActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Opis"));

        opis.setEditable(false);
        opis.setColumns(20);
        opis.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        opis.setLineWrap(true);
        opis.setRows(5);
        opis.setWrapStyleWord(true);
        opis.setBorder(null);
        opis.setMargin(new java.awt.Insets(40, 40, 40, 40));
        jScrollPane2.setViewportView(opis);

        jButton8.setText("Egzemplarze");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        ID_book.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ID_book.setForeground(new java.awt.Color(0, 0, 204));
        ID_book.setText("ID: ");

        title.setEditable(false);
        title.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        title.setText("TYTUL");
        title.setBorder(null);
        title.setCaretPosition(0);
        title.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        autor.setEditable(false);
        autor.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        autor.setText("AUTOR");
        autor.setBorder(null);
        autor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autorActionPerformed(evt);
            }
        });

        gatunek.setEditable(false);
        gatunek.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        gatunek.setText("GATUNEK");
        gatunek.setBorder(null);
        gatunek.setCaretPosition(0);
        gatunek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gatunekActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(ID_book, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addGap(128, 128, 128)
                                .addComponent(autor, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel34)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dzial, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(listDzial, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(40, 40, 40)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel36)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(gatunek, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(listGatunek, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(40, 40, 40)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel32)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(kategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(listKategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 647, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 40, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jButton8)
                        .addGap(18, 18, 18)
                        .addComponent(books_edycja1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(books_close)
                        .addGap(53, 53, 53))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ID_book)
                    .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addGap(33, 33, 33)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gatunek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dzial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(jLabel36)
                    .addComponent(kategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listDzial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(listGatunek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(listKategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(books_edycja1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(books_close, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout booksDetailsLayout = new javax.swing.GroupLayout(booksDetails.getContentPane());
        booksDetails.getContentPane().setLayout(booksDetailsLayout);
        booksDetailsLayout.setHorizontalGroup(
            booksDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        booksDetailsLayout.setVerticalGroup(
            booksDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        oknoDodajBook.setTitle("Dodaj Książkę");
        oknoDodajBook.setLocation(new java.awt.Point(0, 0));
        oknoDodajBook.setMinimumSize(new java.awt.Dimension(650, 550));
        oknoDodajBook.setUndecorated(true);
        oknoDodajBook.setResizable(false);
        oknoDodajBook.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                oknoDodajBookWindowActivated(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 255, 255), new java.awt.Color(0, 51, 51)));

        jTextField2.setEditable(false);
        jTextField2.setBackground(new java.awt.Color(255, 255, 204));
        jTextField2.setText(" wymagane ");

        dodajButtonNew.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dodajButtonNew.setText("Dodaj");
        dodajButtonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dodajButtonNewActionPerformed(evt);
            }
        });

        jButton13.setText("Anuluj");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Tytuł:");

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("Autor:");

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel40.setText("Dział:");

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText("Gatunek:");

        tittleNew.setBackground(new java.awt.Color(255, 255, 204));
        tittleNew.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tittleNew.setMargin(new java.awt.Insets(2, 4, 2, 2));
        tittleNew.setSelectionColor(new java.awt.Color(255, 102, 102));

        autorNew.setEditable(false);
        autorNew.setBackground(new java.awt.Color(255, 255, 204));
        autorNew.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        autorNew.setMargin(new java.awt.Insets(2, 4, 2, 2));
        autorNew.setSelectionColor(new java.awt.Color(255, 102, 102));
        autorNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autorNewActionPerformed(evt);
            }
        });

        /*
        listDzialNew.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        listDzialNew.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        listDzialNew.setToolTipText("wybierz z listy");
        */
        listDzialNew.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                listDzialNewFocusLost(evt);
            }
        });
        listDzialNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listDzialNewActionPerformed(evt);
            }
        });

        /*
        listGatunekNew.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listGatunekNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listGatunekNewActionPerformed(evt);
            }
        });

        tytul.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tytul.setText("Dodawanie Książki");

        dzialNew.setEditable(false);
        dzialNew.setBackground(new java.awt.Color(255, 255, 204));
        dzialNew.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dzialNew.setText("wybierz z listy");
        dzialNew.setMargin(new java.awt.Insets(2, 4, 2, 2));
        dzialNew.setName(""); // NOI18N
        dzialNew.setSelectionColor(new java.awt.Color(255, 102, 102));

        gatunekNew.setEditable(false);
        gatunekNew.setBackground(new java.awt.Color(255, 255, 204));
        gatunekNew.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        gatunekNew.setText("wybierz z listy");
        gatunekNew.setMargin(new java.awt.Insets(2, 4, 2, 2));
        gatunekNew.setSelectionColor(new java.awt.Color(255, 102, 102));

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("Kategoria:");

        kategoriaNew.setEditable(false);
        kategoriaNew.setBackground(new java.awt.Color(255, 255, 204));
        kategoriaNew.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        kategoriaNew.setText("wybierz z listy");
        kategoriaNew.setMargin(new java.awt.Insets(2, 4, 2, 2));
        kategoriaNew.setSelectionColor(new java.awt.Color(255, 102, 102));

        /*
        listKategoriaNew.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listKategoriaNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listKategoriaNewActionPerformed(evt);
            }
        });

        opisNew.setColumns(20);
        opisNew.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        opisNew.setRows(5);
        jScrollPane4.setViewportView(opisNew);

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("Opis:");

        /*
        listAutorNew.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        listAutorNew.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        listAutorNew.setToolTipText("wybierz z listy");
        */
        listAutorNew.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                listAutorNewFocusLost(evt);
            }
        });
        listAutorNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listAutorNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tittleNew, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(autorNew, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dzialNew, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(listDzialNew, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(listAutorNew, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addComponent(tytul, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(gatunekNew, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(listGatunekNew, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(kategoriaNew, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(listKategoriaNew, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(dodajButtonNew, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton13)
                                .addGap(13, 13, 13)))))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tytul, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tittleNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autorNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(listAutorNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dzialNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40)
                    .addComponent(listDzialNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel41)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(listGatunekNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(gatunekNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel51)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(listKategoriaNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(kategoriaNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addGap(133, 133, 133))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dodajButtonNew, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout oknoDodajBookLayout = new javax.swing.GroupLayout(oknoDodajBook.getContentPane());
        oknoDodajBook.getContentPane().setLayout(oknoDodajBookLayout);
        oknoDodajBookLayout.setHorizontalGroup(
            oknoDodajBookLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        oknoDodajBookLayout.setVerticalGroup(
            oknoDodajBookLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        dodajAutora.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dodajAutora.setTitle("Dodaj Autora");
        dodajAutora.setMinimumSize(new java.awt.Dimension(320, 200));
        dodajAutora.setSize(new java.awt.Dimension(500, 150));

        nazwiskoAutorAdd.setBackground(new java.awt.Color(255, 255, 204));
        nazwiskoAutorAdd.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        imieAutorAdd.setBackground(new java.awt.Color(255, 255, 204));
        imieAutorAdd.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        imieAutorAdd.setSelectedTextColor(new java.awt.Color(255, 102, 102));

        jLabel33.setText("Nazwisko Autora");

        jLabel37.setText("Imie Autora");

        jButton14.setText("Dodaj");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Anuluj");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dodajAutoraLayout = new javax.swing.GroupLayout(dodajAutora.getContentPane());
        dodajAutora.getContentPane().setLayout(dodajAutoraLayout);
        dodajAutoraLayout.setHorizontalGroup(
            dodajAutoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dodajAutoraLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(dodajAutoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dodajAutoraLayout.createSequentialGroup()
                        .addGroup(dodajAutoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(dodajAutoraLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jLabel33))
                            .addComponent(nazwiskoAutorAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(dodajAutoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dodajAutoraLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(imieAutorAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(dodajAutoraLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel37)))))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        dodajAutoraLayout.setVerticalGroup(
            dodajAutoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dodajAutoraLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(dodajAutoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dodajAutoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nazwiskoAutorAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imieAutorAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dodajAutoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        oknoDodajEgzemplarz.setTitle("Dodaj Egzemplarz");
        oknoDodajEgzemplarz.setLocation(new java.awt.Point(0, 0));
        oknoDodajEgzemplarz.setMinimumSize(new java.awt.Dimension(650, 550));
        oknoDodajEgzemplarz.setUndecorated(true);
        oknoDodajEgzemplarz.setResizable(false);
        oknoDodajEgzemplarz.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                oknoDodajEgzemplarzWindowActivated(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 255, 255), new java.awt.Color(0, 51, 51)));

        jTextField3.setEditable(false);
        jTextField3.setBackground(new java.awt.Color(255, 255, 204));
        jTextField3.setText(" wymagane ");

        dodajButtonEgz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dodajButtonEgz.setText("Dodaj");
        dodajButtonEgz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dodajButtonEgzActionPerformed(evt);
            }
        });

        jButton16.setText("Anuluj");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("Tytuł:");

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("Autor:");

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText("Lokalizacja:");

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Stan:");

        tittleNew1.setEditable(false);
        tittleNew1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tittleNew1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        tittleNew1.setSelectionColor(new java.awt.Color(255, 102, 102));

        autorNew1.setEditable(false);
        autorNew1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        autorNew1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        autorNew1.setSelectionColor(new java.awt.Color(255, 102, 102));
        autorNew1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autorNew1ActionPerformed(evt);
            }
        });

        /*
        listLokalizacja.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        listLokalizacja.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        listLokalizacja.setToolTipText("wybierz z listy");
        */
        listLokalizacja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                listLokalizacjaFocusLost(evt);
            }
        });
        listLokalizacja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listLokalizacjaActionPerformed(evt);
            }
        });

        /*
        listStan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listStan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listStanActionPerformed(evt);
            }
        });

        tytul1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tytul1.setText("Dodawanie Egzemplarza");

        lokalizacjaField.setEditable(false);
        lokalizacjaField.setBackground(new java.awt.Color(255, 255, 204));
        lokalizacjaField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lokalizacjaField.setText("wybierz z listy");
        lokalizacjaField.setMargin(new java.awt.Insets(2, 4, 2, 2));
        lokalizacjaField.setName(""); // NOI18N
        lokalizacjaField.setSelectionColor(new java.awt.Color(255, 102, 102));

        stanField.setEditable(false);
        stanField.setBackground(new java.awt.Color(255, 255, 204));
        stanField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        stanField.setText("wybierz z listy");
        stanField.setMargin(new java.awt.Insets(2, 4, 2, 2));
        stanField.setSelectionColor(new java.awt.Color(255, 102, 102));

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel53.setText("Wydawnictwo:");

        wydawnictwoField.setEditable(false);
        wydawnictwoField.setBackground(new java.awt.Color(255, 255, 204));
        wydawnictwoField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        wydawnictwoField.setText("wybierz z listy");
        wydawnictwoField.setMargin(new java.awt.Insets(2, 4, 2, 2));
        wydawnictwoField.setSelectionColor(new java.awt.Color(255, 102, 102));

        /*
        listWydawnictwo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listWydawnictwo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listWydawnictwoActionPerformed(evt);
            }
        });

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel54.setText("Rok wydania:");

        rokField.setBackground(new java.awt.Color(255, 255, 204));
        rokField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        rokField.setMargin(new java.awt.Insets(2, 4, 2, 2));
        rokField.setSelectionColor(new java.awt.Color(255, 102, 102));
        rokField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rokFieldActionPerformed(evt);
            }
        });

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel55.setText("Język:");

        jezykField.setBackground(new java.awt.Color(255, 255, 204));
        jezykField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jezykField.setMargin(new java.awt.Insets(2, 4, 2, 2));
        jezykField.setSelectionColor(new java.awt.Color(255, 102, 102));
        jezykField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jezykFieldActionPerformed(evt);
            }
        });

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel56.setText("ID:");

        newIDEgz.setEditable(false);
        newIDEgz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        newIDEgz.setMargin(new java.awt.Insets(2, 4, 2, 2));
        newIDEgz.setSelectionColor(new java.awt.Color(255, 102, 102));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(lokalizacjaField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(listLokalizacja, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(autorNew1)
                                .addComponent(tittleNew1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addComponent(tytul1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(stanField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(listStan, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(rokField)
                                    .addComponent(wydawnictwoField, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(listWydawnictwo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jezykField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(newIDEgz, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dodajButtonEgz, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(122, 122, 122)
                                .addComponent(jButton16)))))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tytul1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tittleNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autorNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lokalizacjaField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(listLokalizacja, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listStan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stanField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wydawnictwoField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53)
                    .addComponent(listWydawnictwo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rokField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jezykField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addGap(19, 19, 19)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(newIDEgz, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dodajButtonEgz, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton16))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout oknoDodajEgzemplarzLayout = new javax.swing.GroupLayout(oknoDodajEgzemplarz.getContentPane());
        oknoDodajEgzemplarz.getContentPane().setLayout(oknoDodajEgzemplarzLayout);
        oknoDodajEgzemplarzLayout.setHorizontalGroup(
            oknoDodajEgzemplarzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        oknoDodajEgzemplarzLayout.setVerticalGroup(
            oknoDodajEgzemplarzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        egzemplarzEdit.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        egzemplarzEdit.setTitle("Książka");
        egzemplarzEdit.setBackground(new java.awt.Color(204, 204, 204));
        egzemplarzEdit.setUndecorated(true);
        egzemplarzEdit.setResizable(false);
        egzemplarzEdit.setSize(new java.awt.Dimension(680, 400));

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 51), new java.awt.Color(0, 153, 153)));

        lokalizacja.setEditable(false);
        lokalizacja.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lokalizacja.setText("LOKALIZACJA");
        lokalizacja.setBorder(null);
        lokalizacja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lokalizacjaActionPerformed(evt);
            }
        });

        /*
        listLOK.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listLOK.setEnabled(false);
        listLOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listLOKActionPerformed(evt);
            }
        });

        wydawnictwo.setEditable(false);
        wydawnictwo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        wydawnictwo.setText("WYDAWNICTWO");
        wydawnictwo.setBorder(null);
        wydawnictwo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wydawnictwoActionPerformed(evt);
            }
        });

        /*
        listSTA.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listSTA.setEnabled(false);
        listSTA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listSTAActionPerformed(evt);
            }
        });

        books_close2.setText("Zamknij");
        books_close2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                books_close2ActionPerformed(evt);
            }
        });

        /*
        listWYD.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        */
        listWYD.setEnabled(false);
        listWYD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listWYDActionPerformed(evt);
            }
        });

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel48.setText("Wydawnictwo:");

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel49.setText("Autorzy:");

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel50.setText("Lokalizacja:");

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel57.setText("Stan:");

        egz_edycja.setText("Edycja");
        egz_edycja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                egz_edycjaActionPerformed(evt);
            }
        });

        ID_egzemplarz.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ID_egzemplarz.setForeground(new java.awt.Color(0, 0, 204));
        ID_egzemplarz.setText("ID: ");

        title2.setEditable(false);
        title2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        title2.setText("TYTUL");
        title2.setBorder(null);
        title2.setCaretPosition(0);
        title2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        autor2.setEditable(false);
        autor2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        autor2.setText("AUTOR");
        autor2.setBorder(null);
        autor2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autor2ActionPerformed(evt);
            }
        });

        stan.setEditable(false);
        stan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        stan.setText("STAN");
        stan.setBorder(null);
        stan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stanActionPerformed(evt);
            }
        });

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel58.setText("Rok wydania:");

        jLabel59.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel59.setText("Język:");

        rok.setEditable(false);

        jezyk.setEditable(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addComponent(ID_egzemplarz, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(title2, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel49)
                                .addGap(128, 128, 128)
                                .addComponent(autor2, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(egz_edycja, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(334, 334, 334)
                                        .addComponent(books_close2))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel50)
                                            .addComponent(listLOK, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lokalizacja, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addComponent(jLabel58)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(rok, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(41, 41, 41)
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addComponent(jLabel59)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jezyk, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel57)
                                            .addComponent(listSTA, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(stan, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(41, 41, 41)
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel48)
                                            .addComponent(listWYD, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(wydawnictwo, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(1, 1, 1)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ID_egzemplarz)
                    .addComponent(title2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel50)
                            .addComponent(jLabel57)
                            .addComponent(jLabel48))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lokalizacja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(wydawnictwo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(listLOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(listSTA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(listWYD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel58)
                            .addComponent(jLabel59)
                            .addComponent(rok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jezyk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(98, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(egz_edycja, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(books_close2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        javax.swing.GroupLayout egzemplarzEditLayout = new javax.swing.GroupLayout(egzemplarzEdit.getContentPane());
        egzemplarzEdit.getContentPane().setLayout(egzemplarzEditLayout);
        egzemplarzEditLayout.setHorizontalGroup(
            egzemplarzEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        egzemplarzEditLayout.setVerticalGroup(
            egzemplarzEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        oknoWypozycz.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        oknoWypozycz.setTitle("Wypożyczenie");
        oknoWypozycz.setLocation(new java.awt.Point(0, 0));
        oknoWypozycz.setMinimumSize(new java.awt.Dimension(400, 250));
        oknoWypozycz.setUndecorated(true);
        oknoWypozycz.setResizable(false);
        oknoWypozycz.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                oknoWypozyczWindowActivated(evt);
            }
        });

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 255, 255), new java.awt.Color(0, 51, 51)));

        jTextField4.setEditable(false);
        jTextField4.setBackground(new java.awt.Color(255, 255, 204));
        jTextField4.setText(" wymagane ");

        wypozyczButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        wypozyczButton.setText("Wypożycz");
        wypozyczButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wypozyczButtonActionPerformed(evt);
            }
        });

        jButton17.setText("Anuluj");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText("Egzemplarz:");

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel45.setText("Czytelnik:");

        wypozyczEgzemplarz.setBackground(new java.awt.Color(255, 255, 204));
        wypozyczEgzemplarz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        wypozyczEgzemplarz.setMargin(new java.awt.Insets(2, 4, 2, 2));
        wypozyczEgzemplarz.setSelectionColor(new java.awt.Color(255, 102, 102));

        wypozyczCzytelnik.setBackground(new java.awt.Color(255, 255, 204));
        wypozyczCzytelnik.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        wypozyczCzytelnik.setMargin(new java.awt.Insets(2, 4, 2, 2));
        wypozyczCzytelnik.setSelectionColor(new java.awt.Color(255, 102, 102));
        wypozyczCzytelnik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wypozyczCzytelnikActionPerformed(evt);
            }
        });

        tytul2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tytul2.setText("Wypożyczenie");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(tytul2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(wypozyczButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                                .addComponent(jButton17))
                            .addComponent(wypozyczCzytelnik)
                            .addComponent(wypozyczEgzemplarz))))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tytul2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wypozyczEgzemplarz, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wypozyczCzytelnik, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45))
                .addGap(41, 41, 41)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wypozyczButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton17))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout oknoWypozyczLayout = new javax.swing.GroupLayout(oknoWypozycz.getContentPane());
        oknoWypozycz.getContentPane().setLayout(oknoWypozyczLayout);
        oknoWypozyczLayout.setHorizontalGroup(
            oknoWypozyczLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        oknoWypozyczLayout.setVerticalGroup(
            oknoWypozyczLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        oknoZwrot.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        oknoZwrot.setTitle("Zwrot egzemplarza");
        oknoZwrot.setLocation(new java.awt.Point(0, 0));
        oknoZwrot.setMinimumSize(new java.awt.Dimension(400, 250));
        oknoZwrot.setUndecorated(true);
        oknoZwrot.setResizable(false);
        oknoZwrot.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                oknoZwrotWindowActivated(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 255, 255), new java.awt.Color(0, 51, 51)));

        jTextField5.setEditable(false);
        jTextField5.setBackground(new java.awt.Color(255, 255, 204));
        jTextField5.setText(" wymagane ");

        zwrotButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        zwrotButton.setText("Zwrot");
        zwrotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zwrotButtonActionPerformed(evt);
            }
        });

        jButton18.setText("Anuluj");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("Egzemplarz:");

        zwrotEgzemplarz.setBackground(new java.awt.Color(255, 255, 204));
        zwrotEgzemplarz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        zwrotEgzemplarz.setMargin(new java.awt.Insets(2, 4, 2, 2));
        zwrotEgzemplarz.setSelectionColor(new java.awt.Color(255, 102, 102));

        tytul3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tytul3.setText("Zwrot");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(tytul3, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(zwrotEgzemplarz, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(zwrotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton18)))))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tytul3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zwrotEgzemplarz, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addGap(32, 32, 32)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zwrotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout oknoZwrotLayout = new javax.swing.GroupLayout(oknoZwrot.getContentPane());
        oknoZwrot.getContentPane().setLayout(oknoZwrotLayout);
        oknoZwrotLayout.setHorizontalGroup(
            oknoZwrotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        oknoZwrotLayout.setVerticalGroup(
            oknoZwrotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        oknoProlongata.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        oknoProlongata.setTitle("Zwrot egzemplarza");
        oknoProlongata.setLocation(new java.awt.Point(0, 0));
        oknoProlongata.setMinimumSize(new java.awt.Dimension(400, 250));
        oknoProlongata.setUndecorated(true);
        oknoProlongata.setResizable(false);
        oknoProlongata.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                oknoProlongataWindowActivated(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 255, 255), new java.awt.Color(0, 51, 51)));

        jTextField6.setEditable(false);
        jTextField6.setBackground(new java.awt.Color(255, 255, 204));
        jTextField6.setText(" wymagane ");

        prolongujButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        prolongujButton.setText("Prolonguj");
        prolongujButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prolongujButtonActionPerformed(evt);
            }
        });

        jButton19.setText("Anuluj");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("Egzemplarz:");

        prolongataEgzemplarz.setBackground(new java.awt.Color(255, 255, 204));
        prolongataEgzemplarz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        prolongataEgzemplarz.setMargin(new java.awt.Insets(2, 4, 2, 2));
        prolongataEgzemplarz.setSelectionColor(new java.awt.Color(255, 102, 102));

        tytul4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tytul4.setText("Prolongata");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(tytul4, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(prolongataEgzemplarz, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(prolongujButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton19)))))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tytul4, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prolongataEgzemplarz, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47))
                .addGap(32, 32, 32)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prolongujButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout oknoProlongataLayout = new javax.swing.GroupLayout(oknoProlongata.getContentPane());
        oknoProlongata.getContentPane().setLayout(oknoProlongataLayout);
        oknoProlongataLayout.setHorizontalGroup(
            oknoProlongataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        oknoProlongataLayout.setVerticalGroup(
            oknoProlongataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        LogowanieFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        LogowanieFrame.setTitle("Logowanie");
        LogowanieFrame.setModal(true);
        LogowanieFrame.setUndecorated(true);
        LogowanieFrame.setResizable(false);
        LogowanieFrame.setSize(new java.awt.Dimension(400, 290));

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 51, 102), new java.awt.Color(0, 51, 51)));
        jPanel11.setPreferredSize(new java.awt.Dimension(400, 290));

        user.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        user.setText("Użytkownik");
        user.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                userFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                userFocusLost(evt);
            }
        });
        user.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                userKeyPressed(evt);
            }
        });

        zaloguj.setText("Zaloguj");
        zaloguj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zalogujActionPerformed(evt);
            }
        });

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel62.setText("Logowanie");

        pass.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        pass.setText("hasło");
        pass.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passFocusLost(evt);
            }
        });
        pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passKeyPressed(evt);
            }
        });

        komunikatL.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        komunikatL.setForeground(new java.awt.Color(255, 0, 51));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(0, 78, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(80, 80, 80))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(zaloguj, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(118, 118, 118))))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(komunikatL)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(zaloguj, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(komunikatL)
                .addGap(2, 2, 2))
        );

        javax.swing.GroupLayout LogowanieFrameLayout = new javax.swing.GroupLayout(LogowanieFrame.getContentPane());
        LogowanieFrame.getContentPane().setLayout(LogowanieFrameLayout);
        LogowanieFrameLayout.setHorizontalGroup(
            LogowanieFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        LogowanieFrameLayout.setVerticalGroup(
            LogowanieFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        oknoDodajPracownika.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        oknoDodajPracownika.setTitle("Zwrot egzemplarza");
        oknoDodajPracownika.setLocation(new java.awt.Point(0, 0));
        oknoDodajPracownika.setMinimumSize(new java.awt.Dimension(400, 400));
        oknoDodajPracownika.setUndecorated(true);
        oknoDodajPracownika.setResizable(false);
        oknoDodajPracownika.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                oknoDodajPracownikaWindowActivated(evt);
            }
        });

        jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 255, 255), new java.awt.Color(0, 51, 51)));
        jPanel12.setPreferredSize(new java.awt.Dimension(412, 400));

        jTextField7.setEditable(false);
        jTextField7.setBackground(new java.awt.Color(255, 255, 204));
        jTextField7.setText(" wymagane ");

        dodajPracownikaButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dodajPracownikaButton.setText("Dodaj");
        dodajPracownikaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dodajPracownikaButtonActionPerformed(evt);
            }
        });

        jButton22.setText("Anuluj");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel65.setText("Nazwisko:");

        nazwiskoPracownika.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        nazwiskoPracownika.setMargin(new java.awt.Insets(2, 4, 2, 2));
        nazwiskoPracownika.setSelectionColor(new java.awt.Color(255, 102, 102));

        tytul5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tytul5.setText("Dodaj pracownika");

        imiePracownika.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        imiePracownika.setMargin(new java.awt.Insets(2, 4, 2, 2));
        imiePracownika.setSelectionColor(new java.awt.Color(255, 102, 102));

        loginPracownika.setBackground(new java.awt.Color(255, 255, 204));
        loginPracownika.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        loginPracownika.setMargin(new java.awt.Insets(2, 4, 2, 2));
        loginPracownika.setSelectionColor(new java.awt.Color(255, 102, 102));

        hasloPracownika.setBackground(new java.awt.Color(255, 255, 204));
        hasloPracownika.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        hasloPracownika.setToolTipText("min. 8 znaków w tym jeden maly i duży");
        hasloPracownika.setMargin(new java.awt.Insets(2, 4, 2, 2));
        hasloPracownika.setSelectionColor(new java.awt.Color(255, 102, 102));

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel66.setText("Imie:");

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel67.setText("Login:");

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel68.setText("Hasło:");

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel63.setText("min. 8 znaków w tym jeden mały i duży");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(tytul5, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(nazwiskoPracownika, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(imiePracownika, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(loginPracownika, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(hasloPracownika, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addComponent(dodajPracownikaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(27, 27, 27)
                                    .addComponent(jButton22))))))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(tytul5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1)))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nazwiskoPracownika, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imiePracownika, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginPracownika, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hasloPracownika, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68))
                .addGap(1, 1, 1)
                .addComponent(jLabel63)
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dodajPracownikaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton22))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout oknoDodajPracownikaLayout = new javax.swing.GroupLayout(oknoDodajPracownika.getContentPane());
        oknoDodajPracownika.getContentPane().setLayout(oknoDodajPracownikaLayout);
        oknoDodajPracownikaLayout.setHorizontalGroup(
            oknoDodajPracownikaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        oknoDodajPracownikaLayout.setVerticalGroup(
            oknoDodajPracownikaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        oknoEdycjaPracownika.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        oknoEdycjaPracownika.setTitle("Edycja Pracownika");
        oknoEdycjaPracownika.setLocation(new java.awt.Point(0, 0));
        oknoEdycjaPracownika.setMinimumSize(new java.awt.Dimension(400, 400));
        oknoEdycjaPracownika.setUndecorated(true);
        oknoEdycjaPracownika.setResizable(false);
        oknoEdycjaPracownika.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                oknoEdycjaPracownikaWindowActivated(evt);
            }
        });

        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 255, 255), new java.awt.Color(0, 51, 51)));
        jPanel13.setPreferredSize(new java.awt.Dimension(412, 400));

        jTextField8.setEditable(false);
        jTextField8.setBackground(new java.awt.Color(255, 255, 204));
        jTextField8.setText(" wymagane ");

        zapiszPracownikaButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        zapiszPracownikaButton.setText("Zapisz");
        zapiszPracownikaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zapiszPracownikaButtonActionPerformed(evt);
            }
        });

        edycjaPracownikaButton.setText("Anuluj");
        edycjaPracownikaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edycjaPracownikaButtonActionPerformed(evt);
            }
        });

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel69.setText("Nazwisko:");

        nazwiskoPracownika1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        nazwiskoPracownika1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        nazwiskoPracownika1.setSelectionColor(new java.awt.Color(255, 102, 102));

        tytul6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tytul6.setText("Edycja pracownika");

        imiePracownika1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        imiePracownika1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        imiePracownika1.setSelectionColor(new java.awt.Color(255, 102, 102));

        loginPracownika1.setBackground(new java.awt.Color(255, 255, 204));
        loginPracownika1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        loginPracownika1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        loginPracownika1.setSelectionColor(new java.awt.Color(255, 102, 102));

        hasloPracownika1.setBackground(new java.awt.Color(255, 255, 204));
        hasloPracownika1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        hasloPracownika1.setToolTipText("min. 8 znaków w tym jeden maly i duży");
        hasloPracownika1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        hasloPracownika1.setSelectionColor(new java.awt.Color(255, 102, 102));

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel70.setText("Imie:");

        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel71.setText("Login:");

        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel72.setText("Hasło:");

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel64.setText("min. 8 znaków w tym jeden mały i duży");

        IDprac.setEditable(false);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(nazwiskoPracownika1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(imiePracownika1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(loginPracownika1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addComponent(zapiszPracownikaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(edycjaPracownikaButton))))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(hasloPracownika1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tytul6, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(IDprac, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tytul6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(IDprac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(imiePracownika1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel70))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nazwiskoPracownika1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(loginPracownika1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel71))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(hasloPracownika1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel64)
                .addGap(35, 35, 35)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zapiszPracownikaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edycjaPracownikaButton))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout oknoEdycjaPracownikaLayout = new javax.swing.GroupLayout(oknoEdycjaPracownika.getContentPane());
        oknoEdycjaPracownika.getContentPane().setLayout(oknoEdycjaPracownikaLayout);
        oknoEdycjaPracownikaLayout.setHorizontalGroup(
            oknoEdycjaPracownikaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        oknoEdycjaPracownikaLayout.setVerticalGroup(
            oknoEdycjaPracownikaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        zmianaHasla.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        zmianaHasla.setTitle("Logowanie");
        zmianaHasla.setModal(true);
        zmianaHasla.setUndecorated(true);
        zmianaHasla.setResizable(false);
        zmianaHasla.setSize(new java.awt.Dimension(400, 420));

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 51, 102), new java.awt.Color(0, 51, 51)));

        zaloguj1.setText("Zmień");
        zaloguj1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zaloguj1ActionPerformed(evt);
            }
        });

        jLabel73.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel73.setText("Zmiana hasła");

        pass1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        pass1.setToolTipText("powtórz nowe hasło");
        pass1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pass1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pass1FocusLost(evt);
            }
        });

        userZmiana.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        userZmiana.setForeground(new java.awt.Color(0, 0, 255));
        userZmiana.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        pass2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        pass2.setToolTipText("wpisz obecne haslo");
        pass2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pass2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pass2FocusLost(evt);
            }
        });
        pass2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pass2ActionPerformed(evt);
            }
        });

        pass3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        pass3.setToolTipText("wpisz nowe hasło");
        pass3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pass3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pass3FocusLost(evt);
            }
        });
        pass3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pass3ActionPerformed(evt);
            }
        });

        jLabel74.setText("nowe hasło");

        jLabel75.setText("obecne hasło");

        jLabel77.setText("powtórz nowe hasło");

        jButton1.setText("Anuluj");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        komunikat.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        komunikat.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(43, Short.MAX_VALUE)
                .addComponent(userZmiana, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel77)
                    .addComponent(jLabel75)
                    .addComponent(jLabel74)
                    .addComponent(pass3, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pass2, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pass1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(80, 80, 80))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(92, 92, 92))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(zaloguj1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButton1)
                .addGap(27, 27, 27))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(komunikat)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(userZmiana, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel75)
                .addGap(1, 1, 1)
                .addComponent(pass2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel74)
                .addGap(1, 1, 1)
                .addComponent(pass3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel77)
                .addGap(1, 1, 1)
                .addComponent(pass1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(zaloguj1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(komunikat)
                .addContainerGap())
        );

        javax.swing.GroupLayout zmianaHaslaLayout = new javax.swing.GroupLayout(zmianaHasla.getContentPane());
        zmianaHasla.getContentPane().setLayout(zmianaHaslaLayout);
        zmianaHaslaLayout.setHorizontalGroup(
            zmianaHaslaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        zmianaHaslaLayout.setVerticalGroup(
            zmianaHaslaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("System Biblioteczny Bookworm");
        setBackground(new java.awt.Color(204, 255, 204));
        setFocusable(false);
        setLocation(new java.awt.Point(0, 0));
        setResizable(false);

        zakladki.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        zakladki.setForeground(new java.awt.Color(0, 51, 204));
        zakladki.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        ZakladkaCzytelnicy.setAlignmentX(0.0F);
        ZakladkaCzytelnicy.setAlignmentY(0.0F);

        jScrollPane3.setMaximumSize(new java.awt.Dimension(820, 320));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(820, 320));
        jScrollPane3.setVerticalScrollBarPolicy(jScrollPane3.VERTICAL_SCROLLBAR_ALWAYS);
        //jScrollPane3.setVerticalScrollBar(verticalScrollBar);

        TabelaCzytelnicy.setAutoCreateRowSorter(true);
        TabelaCzytelnicy.getColumnModel().getColumn(0).setPreferredWidth(60);
        TabelaCzytelnicy.getColumnModel().getColumn(1).setPreferredWidth(100);
        TabelaCzytelnicy.getColumnModel().getColumn(2).setPreferredWidth(110);
        TabelaCzytelnicy.getColumnModel().getColumn(3).setPreferredWidth(85);
        TabelaCzytelnicy.getColumnModel().getColumn(4).setPreferredWidth(80);
        TabelaCzytelnicy.getColumnModel().getColumn(5).setPreferredWidth(100);
        TabelaCzytelnicy.getColumnModel().getColumn(6).setPreferredWidth(110);
        TabelaCzytelnicy.getColumnModel().getColumn(7).setPreferredWidth(200);
        TabelaCzytelnicy.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TabelaCzytelnicy.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        TabelaCzytelnicy.setModel(model);
        TabelaCzytelnicy.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        TabelaCzytelnicy.setMaximumSize(new java.awt.Dimension(800, 300));
        TabelaCzytelnicy.setMinimumSize(new java.awt.Dimension(800, 300));
        TabelaCzytelnicy.setName(""); // NOI18N
        TabelaCzytelnicy.setRowHeight(24);
        TabelaCzytelnicy.getTableHeader().setReorderingAllowed(false);
        TabelaCzytelnicy.getTableHeader().setResizingAllowed(false);
        TabelaCzytelnicy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TabelaCzytelnicyMousePressed(evt);
            }
        });
        TabelaCzytelnicy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TabelaCzytelnicyKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TabelaCzytelnicyKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(TabelaCzytelnicy);

        pokazCzytelnikow.setText("Pokaż Czytelników");
        pokazCzytelnikow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokazCzytelnikowActionPerformed(evt);
            }
        });

        IDfield.setEditable(false);
        IDfield.setText("ID");

        usunCzyt.setForeground(new java.awt.Color(255, 102, 102));
        usunCzyt.setText("Usuń Czytelnika");
        usunCzyt.setEnabled(false);
        usunCzyt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usunCzytActionPerformed(evt);
            }
        });

        filtr_ID.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID.setAlignmentX(0.0F);
        filtr_ID.setAlignmentY(0.0F);
        filtr_ID.setAutoscrolls(false);
        filtr_ID.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID.setName(""); // NOI18N
        filtr_ID.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID.setVerifyInputWhenFocusTarget(false);
        filtr_ID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                filtr_IDFocusGained(evt);
            }
        });
        filtr_ID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_IDActionPerformed(evt);
            }
        });

        filtr_ID1.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID1.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID1.setAlignmentX(0.0F);
        filtr_ID1.setAlignmentY(0.0F);
        filtr_ID1.setAutoscrolls(false);
        filtr_ID1.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID1.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID1.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID1.setName(""); // NOI18N
        filtr_ID1.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID1ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton9.setForeground(new java.awt.Color(0, 51, 204));
        jButton9.setText("Dodaj Czytelnika");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        edycjaButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        edycjaButton.setForeground(new java.awt.Color(204, 148, 62));
        edycjaButton.setText("Edycja Czytelnika");
        edycjaButton.setEnabled(false);
        edycjaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edycjaButtonActionPerformed(evt);
            }
        });

        filtr_ID2.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID2.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID2.setAlignmentX(0.0F);
        filtr_ID2.setAlignmentY(0.0F);
        filtr_ID2.setAutoscrolls(false);
        filtr_ID2.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID2.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID2.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID2.setName(""); // NOI18N
        filtr_ID2.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID2ActionPerformed(evt);
            }
        });

        filtr_ID3.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID3.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID3.setAlignmentX(0.0F);
        filtr_ID3.setAlignmentY(0.0F);
        filtr_ID3.setAutoscrolls(false);
        filtr_ID3.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID3.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID3.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID3.setName(""); // NOI18N
        filtr_ID3.setPreferredSize(new java.awt.Dimension(70, 30));

        filtr_ID4.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID4.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID4.setAlignmentX(0.0F);
        filtr_ID4.setAlignmentY(0.0F);
        filtr_ID4.setAutoscrolls(false);
        filtr_ID4.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID4.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID4.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID4.setName(""); // NOI18N
        filtr_ID4.setPreferredSize(new java.awt.Dimension(70, 30));

        filtr_ID5.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID5.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID5.setAlignmentX(0.0F);
        filtr_ID5.setAlignmentY(0.0F);
        filtr_ID5.setAutoscrolls(false);
        filtr_ID5.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID5.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID5.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID5.setName(""); // NOI18N
        filtr_ID5.setPreferredSize(new java.awt.Dimension(70, 30));

        filtr_ID6.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID6.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID6.setAlignmentX(0.0F);
        filtr_ID6.setAlignmentY(0.0F);
        filtr_ID6.setAutoscrolls(false);
        filtr_ID6.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID6.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID6.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID6.setName(""); // NOI18N
        filtr_ID6.setPreferredSize(new java.awt.Dimension(70, 30));

        filtr_ID7.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID7.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID7.setAlignmentX(0.0F);
        filtr_ID7.setAlignmentY(0.0F);
        filtr_ID7.setAutoscrolls(false);
        filtr_ID7.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID7.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID7.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID7.setName(""); // NOI18N
        filtr_ID7.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID7ActionPerformed(evt);
            }
        });

        filtr_ID8.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID8.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID8.setAlignmentX(0.0F);
        filtr_ID8.setAlignmentY(0.0F);
        filtr_ID8.setAutoscrolls(false);
        filtr_ID8.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID8.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID8.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID8.setName(""); // NOI18N
        filtr_ID8.setPreferredSize(new java.awt.Dimension(70, 30));

        pokazCzytelnikow1.setText("Wyczyść filtry");
        pokazCzytelnikow1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokazCzytelnikow1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ZakladkaCzytelnicyLayout = new javax.swing.GroupLayout(ZakladkaCzytelnicy);
        ZakladkaCzytelnicy.setLayout(ZakladkaCzytelnicyLayout);
        ZakladkaCzytelnicyLayout.setHorizontalGroup(
            ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaCzytelnicyLayout.createSequentialGroup()
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(pokazCzytelnikow)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pokazCzytelnikow1)
                        .addGap(51, 51, 51)
                        .addComponent(IDfield, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(usunCzyt))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaCzytelnicyLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                        .addComponent(filtr_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID3, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID5, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID6, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                        .addComponent(filtr_ID7, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(edycjaButton)))
                .addGap(230, 230, 230))
            .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1009, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ZakladkaCzytelnicyLayout.setVerticalGroup(
            ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaCzytelnicyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edycjaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filtr_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pokazCzytelnikow)
                    .addComponent(IDfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usunCzyt)
                    .addComponent(pokazCzytelnikow1))
                .addGap(26, 26, 26))
        );

        zakladki.addTab("Czytelnicy", ZakladkaCzytelnicy);

        jScrollPane1 = new javax.swing.JScrollPane();

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setMaximumSize(new java.awt.Dimension(820, 320));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(820, 320));
        jScrollPane1.setVerticalScrollBarPolicy(jScrollPane3.VERTICAL_SCROLLBAR_ALWAYS);

        TabelaBooks.setAutoCreateRowSorter(true);
        TabelaBooks.getColumnModel().getColumn(0).setPreferredWidth(40);
        TabelaBooks.getColumnModel().getColumn(1).setPreferredWidth(200);
        TabelaBooks.getColumnModel().getColumn(2).setPreferredWidth(160);
        TabelaBooks.getColumnModel().getColumn(3).setPreferredWidth(85);
        TabelaBooks.getColumnModel().getColumn(4).setPreferredWidth(80);
        TabelaBooks.getColumnModel().getColumn(5).setPreferredWidth(60);
        TabelaBooks.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TabelaBooks.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        TabelaBooks.setModel(books);
        TabelaBooks.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);

        TabelaBooks.setMaximumSize(new java.awt.Dimension(800, 300));

        TabelaBooks.setMinimumSize(new java.awt.Dimension(800, 300));

        TabelaBooks.setName(""); // NOI18N

        TabelaBooks.setRowHeight(24);
        TabelaBooks.getTableHeader().setReorderingAllowed(false);
        TabelaBooks.getTableHeader().setResizingAllowed(false);
        TabelaBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TabelaBooksMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(TabelaBooks);

        pokazKsiazki.setText("Pokaż ksiązki");
        pokazKsiazki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokazKsiazkiActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 51, 204));
        jButton5.setText("Dodaj Książkę");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        dodajEgzButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        dodajEgzButton.setForeground(new java.awt.Color(0, 153, 153));
        dodajEgzButton.setText("Dodaj Egzemplarz");
        dodajEgzButton.setEnabled(false);
        dodajEgzButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dodajEgzButtonActionPerformed(evt);
            }
        });

        books_details.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        books_details.setForeground(new java.awt.Color(204, 148, 62));
        books_details.setText("Szczegóły");
        books_details.setEnabled(false);
        books_details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                books_detailsActionPerformed(evt);
            }
        });

        ID_books_field.setEditable(false);
        ID_books_field.setText("ID");

        usunKsiazke.setForeground(new java.awt.Color(255, 102, 102));
        usunKsiazke.setText("Usuń książkę");
        usunKsiazke.setEnabled(false);
        usunKsiazke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usunKsiazkeActionPerformed(evt);
            }
        });

        filtr_ID9.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID9.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID9.setAlignmentX(0.0F);
        filtr_ID9.setAlignmentY(0.0F);
        filtr_ID9.setAutoscrolls(false);
        filtr_ID9.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID9.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID9.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID9.setName(""); // NOI18N
        filtr_ID9.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID9.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                filtr_ID9FocusGained(evt);
            }
        });
        filtr_ID9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID9ActionPerformed(evt);
            }
        });

        filtr_ID10.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID10.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID10.setAlignmentX(0.0F);
        filtr_ID10.setAlignmentY(0.0F);
        filtr_ID10.setAutoscrolls(false);
        filtr_ID10.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID10.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID10.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID10.setName(""); // NOI18N
        filtr_ID10.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID10ActionPerformed(evt);
            }
        });

        filtr_ID11.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID11.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID11.setAlignmentX(0.0F);
        filtr_ID11.setAlignmentY(0.0F);
        filtr_ID11.setAutoscrolls(false);
        filtr_ID11.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID11.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID11.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID11.setName(""); // NOI18N
        filtr_ID11.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID11ActionPerformed(evt);
            }
        });

        filtr_ID12.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID12.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID12.setAlignmentX(0.0F);
        filtr_ID12.setAlignmentY(0.0F);
        filtr_ID12.setAutoscrolls(false);
        filtr_ID12.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID12.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID12.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID12.setName(""); // NOI18N
        filtr_ID12.setPreferredSize(new java.awt.Dimension(70, 30));

        filtr_ID13.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID13.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID13.setAlignmentX(0.0F);
        filtr_ID13.setAlignmentY(0.0F);
        filtr_ID13.setAutoscrolls(false);
        filtr_ID13.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID13.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID13.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID13.setName(""); // NOI18N
        filtr_ID13.setPreferredSize(new java.awt.Dimension(70, 30));

        filtr_ID14.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID14.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID14.setAlignmentX(0.0F);
        filtr_ID14.setAlignmentY(0.0F);
        filtr_ID14.setAutoscrolls(false);
        filtr_ID14.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID14.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID14.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID14.setName(""); // NOI18N
        filtr_ID14.setPreferredSize(new java.awt.Dimension(70, 30));

        pokazCzytelnikow2.setText("Wyczyść filtry");

        pokazKsiazki1.setText("Wyczyść filtry");
        pokazKsiazki1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokazKsiazki1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ZakladkaKsiazkiLayout = new javax.swing.GroupLayout(ZakladkaKsiazki);
        ZakladkaKsiazki.setLayout(ZakladkaKsiazkiLayout);
        ZakladkaKsiazkiLayout.setHorizontalGroup(
            ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1009, Short.MAX_VALUE)
            .addGroup(ZakladkaKsiazkiLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(pokazKsiazki)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pokazKsiazki1)
                .addGap(86, 86, 86)
                .addComponent(ID_books_field, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usunKsiazke)
                .addContainerGap(460, Short.MAX_VALUE))
            .addGroup(ZakladkaKsiazkiLayout.createSequentialGroup()
                .addComponent(filtr_ID9, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filtr_ID10, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filtr_ID11, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filtr_ID12, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filtr_ID13, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filtr_ID14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaKsiazkiLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dodajEgzButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(books_details, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(145, 145, 145))
            .addGroup(ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ZakladkaKsiazkiLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pokazCzytelnikow2)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        ZakladkaKsiazkiLayout.setVerticalGroup(
            ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaKsiazkiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dodajEgzButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(books_details, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filtr_ID9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pokazKsiazki)
                    .addComponent(ID_books_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usunKsiazke)
                    .addComponent(pokazKsiazki1))
                .addGap(26, 26, 26))
            .addGroup(ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ZakladkaKsiazkiLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pokazCzytelnikow2)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        zakladki.addTab("Książki", ZakladkaKsiazki);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane5.setMaximumSize(new java.awt.Dimension(820, 320));

        jScrollPane5.setPreferredSize(new java.awt.Dimension(820, 320));
        jScrollPane5.setVerticalScrollBarPolicy(jScrollPane3.VERTICAL_SCROLLBAR_ALWAYS);

        TabelaEgzemplarze.setAutoCreateRowSorter(true);
        TabelaEgzemplarze.getColumnModel().getColumn(0).setPreferredWidth(40);
        TabelaEgzemplarze.getColumnModel().getColumn(1).setPreferredWidth(200);
        TabelaEgzemplarze.getColumnModel().getColumn(2).setPreferredWidth(160);
        TabelaEgzemplarze.getColumnModel().getColumn(3).setPreferredWidth(85);
        TabelaEgzemplarze.getColumnModel().getColumn(4).setPreferredWidth(60);
        TabelaEgzemplarze.getColumnModel().getColumn(5).setPreferredWidth(60);
        TabelaEgzemplarze.getColumnModel().getColumn(6).setPreferredWidth(40);
        TabelaEgzemplarze.getColumnModel().getColumn(7).setPreferredWidth(60);

        TabelaEgzemplarze.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TabelaEgzemplarze.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        TabelaEgzemplarze.setModel(egzemplarze);
        TabelaEgzemplarze.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);

        TabelaEgzemplarze.setMaximumSize(new java.awt.Dimension(800, 300));

        TabelaEgzemplarze.setMinimumSize(new java.awt.Dimension(800, 300));

        TabelaEgzemplarze.setName(""); // NOI18N

        TabelaEgzemplarze.setRowHeight(24);
        TabelaEgzemplarze.getTableHeader().setReorderingAllowed(false);
        TabelaEgzemplarze.getTableHeader().setResizingAllowed(false);
        TabelaEgzemplarze.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TabelaEgzemplarzeMousePressed(evt);
            }
        });
        jScrollPane5.setViewportView(TabelaEgzemplarze);

        egzemplarz_edit.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        egzemplarz_edit.setForeground(new java.awt.Color(204, 148, 62));
        egzemplarz_edit.setText("Edycja Egzemplarza");
        egzemplarz_edit.setEnabled(false);
        egzemplarz_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                egzemplarz_editActionPerformed(evt);
            }
        });

        ID_egzemplarza_field.setEditable(false);
        ID_egzemplarza_field.setText("ID");

        usunEgzemplarz.setForeground(new java.awt.Color(255, 102, 102));
        usunEgzemplarz.setText("Usuń egzemplarz");
        usunEgzemplarz.setEnabled(false);
        usunEgzemplarz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usunEgzemplarzActionPerformed(evt);
            }
        });

        pokazEgzemplarze.setText("Pokaż egzemplarze");
        pokazEgzemplarze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokazEgzemplarzeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ZakladkaEgzemplarzeLayout = new javax.swing.GroupLayout(ZakladkaEgzemplarze);
        ZakladkaEgzemplarze.setLayout(ZakladkaEgzemplarzeLayout);
        ZakladkaEgzemplarzeLayout.setHorizontalGroup(
            ZakladkaEgzemplarzeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1009, Short.MAX_VALUE)
            .addGroup(ZakladkaEgzemplarzeLayout.createSequentialGroup()
                .addGroup(ZakladkaEgzemplarzeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ZakladkaEgzemplarzeLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(pokazEgzemplarze)
                        .addGap(259, 259, 259)
                        .addComponent(ID_egzemplarza_field, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usunEgzemplarz))
                    .addGroup(ZakladkaEgzemplarzeLayout.createSequentialGroup()
                        .addGap(656, 656, 656)
                        .addComponent(egzemplarz_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ZakladkaEgzemplarzeLayout.setVerticalGroup(
            ZakladkaEgzemplarzeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaEgzemplarzeLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(egzemplarz_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ZakladkaEgzemplarzeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ID_egzemplarza_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usunEgzemplarz)
                    .addComponent(pokazEgzemplarze))
                .addGap(26, 26, 26))
        );

        zakladki.addTab("Egzemplarze", ZakladkaEgzemplarze);

        jScrollPane6 = new javax.swing.JScrollPane();

        jScrollPane6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane6.setMaximumSize(new java.awt.Dimension(820, 320));

        jScrollPane6.setPreferredSize(new java.awt.Dimension(820, 320));
        jScrollPane6.setVerticalScrollBarPolicy(jScrollPane3.VERTICAL_SCROLLBAR_ALWAYS);

        TabelaWypozyczenia.setAutoCreateRowSorter(true);
        TabelaWypozyczenia.getColumnModel().getColumn(0).setPreferredWidth(40);
        TabelaWypozyczenia.getColumnModel().getColumn(1).setPreferredWidth(180);
        TabelaWypozyczenia.getColumnModel().getColumn(2).setPreferredWidth(160);
        TabelaWypozyczenia.getColumnModel().getColumn(3).setPreferredWidth(120);
        TabelaWypozyczenia.getColumnModel().getColumn(4).setPreferredWidth(60);
        TabelaWypozyczenia.getColumnModel().getColumn(5).setPreferredWidth(60);
        TabelaWypozyczenia.getColumnModel().getColumn(6).setPreferredWidth(60);
        TabelaWypozyczenia.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TabelaWypozyczenia.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        TabelaWypozyczenia.setModel(wypozyczenia);
        TabelaWypozyczenia.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);

        TabelaWypozyczenia.setMaximumSize(new java.awt.Dimension(800, 300));

        TabelaWypozyczenia.setMinimumSize(new java.awt.Dimension(800, 300));

        TabelaWypozyczenia.setName(""); // NOI18N

        TabelaWypozyczenia.setRowHeight(24);
        TabelaWypozyczenia.getTableHeader().setReorderingAllowed(false);
        TabelaWypozyczenia.getTableHeader().setResizingAllowed(false);
        TabelaWypozyczenia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TabelaWypozyczeniaMousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(TabelaWypozyczenia);

        pokazWypozyczenia.setText("Pokaż wypożyczenia");
        pokazWypozyczenia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokazWypozyczeniaActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton7.setForeground(new java.awt.Color(0, 51, 204));
        jButton7.setText("Wypożyczenie");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        dodajEgzButton1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        dodajEgzButton1.setForeground(new java.awt.Color(0, 153, 153));
        dodajEgzButton1.setText("Zwrot");
        dodajEgzButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dodajEgzButton1ActionPerformed(evt);
            }
        });

        books_details1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        books_details1.setForeground(new java.awt.Color(204, 148, 62));
        books_details1.setText("Prolongata");
        books_details1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                books_details1ActionPerformed(evt);
            }
        });

        ID_wyp_field.setEditable(false);
        ID_wyp_field.setText("ID");

        filtr_ID15.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID15.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID15.setAlignmentX(0.0F);
        filtr_ID15.setAlignmentY(0.0F);
        filtr_ID15.setAutoscrolls(false);
        filtr_ID15.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID15.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID15.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID15.setName(""); // NOI18N
        filtr_ID15.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID15.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                filtr_ID15FocusGained(evt);
            }
        });
        filtr_ID15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID15ActionPerformed(evt);
            }
        });

        filtr_ID16.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID16.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID16.setAlignmentX(0.0F);
        filtr_ID16.setAlignmentY(0.0F);
        filtr_ID16.setAutoscrolls(false);
        filtr_ID16.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID16.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID16.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID16.setName(""); // NOI18N
        filtr_ID16.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID16ActionPerformed(evt);
            }
        });

        filtr_ID17.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID17.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID17.setAlignmentX(0.0F);
        filtr_ID17.setAlignmentY(0.0F);
        filtr_ID17.setAutoscrolls(false);
        filtr_ID17.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID17.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID17.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID17.setName(""); // NOI18N
        filtr_ID17.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID17ActionPerformed(evt);
            }
        });

        filtr_ID18.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID18.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID18.setAlignmentX(0.0F);
        filtr_ID18.setAlignmentY(0.0F);
        filtr_ID18.setAutoscrolls(false);
        filtr_ID18.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID18.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID18.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID18.setName(""); // NOI18N
        filtr_ID18.setPreferredSize(new java.awt.Dimension(70, 30));

        filtr_ID19.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID19.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID19.setAlignmentX(0.0F);
        filtr_ID19.setAlignmentY(0.0F);
        filtr_ID19.setAutoscrolls(false);
        filtr_ID19.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID19.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID19.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID19.setName(""); // NOI18N
        filtr_ID19.setPreferredSize(new java.awt.Dimension(70, 30));

        filtr_ID20.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID20.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID20.setAlignmentX(0.0F);
        filtr_ID20.setAlignmentY(0.0F);
        filtr_ID20.setAutoscrolls(false);
        filtr_ID20.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID20.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID20.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID20.setName(""); // NOI18N
        filtr_ID20.setPreferredSize(new java.awt.Dimension(70, 30));

        pokazCzytelnikow3.setText("Wyczyść filtry");

        czysc_filtr_wyp.setText("Wyczyść filtry");
        czysc_filtr_wyp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                czysc_filtr_wypActionPerformed(evt);
            }
        });

        filtr_ID21.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID21.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID21.setAlignmentX(0.0F);
        filtr_ID21.setAlignmentY(0.0F);
        filtr_ID21.setAutoscrolls(false);
        filtr_ID21.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID21.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID21.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID21.setName(""); // NOI18N
        filtr_ID21.setPreferredSize(new java.awt.Dimension(70, 30));

        ID_egz_field.setEditable(false);
        ID_egz_field.setText("ID");

        jLabel60.setText("ID wypożyczenia:");

        jLabel61.setText("ID egzemplarza:");

        javax.swing.GroupLayout ZakladkaWypozyczeniaLayout = new javax.swing.GroupLayout(ZakladkaWypozyczenia);
        ZakladkaWypozyczenia.setLayout(ZakladkaWypozyczeniaLayout);
        ZakladkaWypozyczeniaLayout.setHorizontalGroup(
            ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ZakladkaWypozyczeniaLayout.createSequentialGroup()
                .addGroup(ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ZakladkaWypozyczeniaLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dodajEgzButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ZakladkaWypozyczeniaLayout.createSequentialGroup()
                        .addComponent(filtr_ID15, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID16, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID17, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID18, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID19, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ZakladkaWypozyczeniaLayout.createSequentialGroup()
                        .addComponent(filtr_ID21, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID20, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(books_details1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(63, 63, 63))
            .addGroup(ZakladkaWypozyczeniaLayout.createSequentialGroup()
                .addGroup(ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ZakladkaWypozyczeniaLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(pokazWypozyczenia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(czysc_filtr_wyp)
                        .addGap(78, 78, 78)
                        .addComponent(jLabel60)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ID_wyp_field, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(jLabel61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ID_egz_field, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1009, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ZakladkaWypozyczeniaLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pokazCzytelnikow3)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        ZakladkaWypozyczeniaLayout.setVerticalGroup(
            ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaWypozyczeniaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dodajEgzButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(books_details1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filtr_ID15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pokazWypozyczenia)
                    .addComponent(ID_wyp_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(czysc_filtr_wyp)
                    .addComponent(ID_egz_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60)
                    .addComponent(jLabel61))
                .addGap(26, 26, 26))
            .addGroup(ZakladkaWypozyczeniaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ZakladkaWypozyczeniaLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pokazCzytelnikow3)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        zakladki.addTab("Wypożyczenia", ZakladkaWypozyczenia);

        ZakladkaPracownicy.setAlignmentX(0.0F);
        ZakladkaPracownicy.setAlignmentY(0.0F);

        jScrollPane7.setMaximumSize(new java.awt.Dimension(820, 320));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(820, 320));
        jScrollPane7.setVerticalScrollBarPolicy(jScrollPane3.VERTICAL_SCROLLBAR_ALWAYS);
        //jScrollPane3.setVerticalScrollBar(verticalScrollBar);

        TabelaPracownicy.setAutoCreateRowSorter(true);
        TabelaPracownicy.getColumnModel().getColumn(0).setPreferredWidth(50);
        TabelaPracownicy.getColumnModel().getColumn(1).setPreferredWidth(100);
        TabelaPracownicy.getColumnModel().getColumn(2).setPreferredWidth(100);
        TabelaPracownicy.getColumnModel().getColumn(3).setPreferredWidth(100);
        TabelaPracownicy.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        TabelaPracownicy.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TabelaPracownicy.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        TabelaPracownicy.setModel(pracownicy);
        TabelaPracownicy.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        TabelaPracownicy.setMaximumSize(new java.awt.Dimension(800, 300));
        TabelaPracownicy.setMinimumSize(new java.awt.Dimension(800, 300));
        TabelaPracownicy.setName(""); // NOI18N
        TabelaPracownicy.setRowHeight(24);
        TabelaCzytelnicy.getTableHeader().setReorderingAllowed(false);
        TabelaCzytelnicy.getTableHeader().setResizingAllowed(false);
        TabelaPracownicy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TabelaPracownicyMousePressed(evt);
            }
        });
        jScrollPane7.setViewportView(TabelaPracownicy);

        pokazPracownikow.setText("Pokaż Pracowników");
        pokazPracownikow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokazPracownikowActionPerformed(evt);
            }
        });

        IDfieldPracownicy.setEditable(false);
        IDfieldPracownicy.setText("ID");

        usunPracownika.setForeground(new java.awt.Color(255, 102, 102));
        usunPracownika.setText("Usuń Pracownika");
        usunPracownika.setEnabled(false);
        usunPracownika.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usunPracownikaActionPerformed(evt);
            }
        });

        filtr_ID22.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID22.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID22.setAlignmentX(0.0F);
        filtr_ID22.setAlignmentY(0.0F);
        filtr_ID22.setAutoscrolls(false);
        filtr_ID22.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID22.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID22.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID22.setName(""); // NOI18N
        filtr_ID22.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID22.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                filtr_ID22FocusGained(evt);
            }
        });
        filtr_ID22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID22ActionPerformed(evt);
            }
        });

        filtr_ID23.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID23.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID23.setAlignmentX(0.0F);
        filtr_ID23.setAlignmentY(0.0F);
        filtr_ID23.setAutoscrolls(false);
        filtr_ID23.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID23.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID23.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID23.setName(""); // NOI18N
        filtr_ID23.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID23ActionPerformed(evt);
            }
        });

        jButton10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton10.setForeground(new java.awt.Color(0, 51, 204));
        jButton10.setText("Dodaj Pracownika");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        edycjaButtonPracownicy.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        edycjaButtonPracownicy.setForeground(new java.awt.Color(204, 148, 62));
        edycjaButtonPracownicy.setText("Edycja Pracownika");
        edycjaButtonPracownicy.setEnabled(false);
        edycjaButtonPracownicy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edycjaButtonPracownicyActionPerformed(evt);
            }
        });

        filtr_ID24.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID24.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID24.setAlignmentX(0.0F);
        filtr_ID24.setAlignmentY(0.0F);
        filtr_ID24.setAutoscrolls(false);
        filtr_ID24.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID24.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID24.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID24.setName(""); // NOI18N
        filtr_ID24.setPreferredSize(new java.awt.Dimension(70, 30));
        filtr_ID24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtr_ID24ActionPerformed(evt);
            }
        });

        filtr_ID25.setBackground(new java.awt.Color(232, 255, 255));
        filtr_ID25.setForeground(new java.awt.Color(153, 153, 153));
        filtr_ID25.setAlignmentX(0.0F);
        filtr_ID25.setAlignmentY(0.0F);
        filtr_ID25.setAutoscrolls(false);
        filtr_ID25.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID25.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID25.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID25.setName(""); // NOI18N
        filtr_ID25.setPreferredSize(new java.awt.Dimension(70, 30));

        czyscFiltrPracownicy.setText("Wyczyść filtry");
        czyscFiltrPracownicy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                czyscFiltrPracownicyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ZakladkaPracownicyLayout = new javax.swing.GroupLayout(ZakladkaPracownicy);
        ZakladkaPracownicy.setLayout(ZakladkaPracownicyLayout);
        ZakladkaPracownicyLayout.setHorizontalGroup(
            ZakladkaPracownicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ZakladkaPracownicyLayout.createSequentialGroup()
                .addGroup(ZakladkaPracownicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ZakladkaPracownicyLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(pokazPracownikow)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(czyscFiltrPracownicy)
                        .addGap(51, 51, 51)
                        .addComponent(IDfieldPracownicy, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(usunPracownika))
                    .addGroup(ZakladkaPracownicyLayout.createSequentialGroup()
                        .addComponent(filtr_ID22, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID23, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID24, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(filtr_ID25, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ZakladkaPracownicyLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(edycjaButtonPracownicy))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(339, 425, Short.MAX_VALUE))
        );
        ZakladkaPracownicyLayout.setVerticalGroup(
            ZakladkaPracownicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaPracownicyLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(ZakladkaPracownicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edycjaButtonPracownicy, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ZakladkaPracownicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filtr_ID22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ZakladkaPracownicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pokazPracownikow)
                    .addComponent(IDfieldPracownicy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usunPracownika)
                    .addComponent(czyscFiltrPracownicy))
                .addGap(26, 26, 26))
        );

        zakladki.addTab("Pracownicy", null, ZakladkaPracownicy, "tylko dla administratora");

        jLabel1.setText("Użytkownik: ");

        jButton2.setText("wyloguj");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        zalogowanyUser.setEditable(false);
        zalogowanyUser.setBackground(new java.awt.Color(204, 255, 204));
        zalogowanyUser.setText("nie zalogowany");
        zalogowanyUser.setToolTipText("kliknij żeby zmienić hasło");
        zalogowanyUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                zalogowanyUserMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(zalogowanyUser, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(zakladki, javax.swing.GroupLayout.PREFERRED_SIZE, 1018, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton2)
                    .addComponent(zalogowanyUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(zakladki, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        zakladki.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 778, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );

        komunikaty.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        komunikaty.setForeground(new java.awt.Color(0, 0, 204));
        komunikaty.setFocusable(false);
        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(komunikaty, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(komunikaty)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void pokazCzytelnikowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazCzytelnikowActionPerformed
       String a="%",be="%",c="%",d="%",e="%",f="%",g="%",h="%",i="%";
       String tmp=filtr_ID.getText();
       a+=tmp.replaceFirst("^0+(?!$)", "")+"%";
       be+=filtr_ID1.getText()+"%";
       c+=filtr_ID2.getText()+"%";
       d+=filtr_ID3.getText()+"%";
       e+=filtr_ID4.getText()+"%";
       f+=filtr_ID5.getText()+"%";
       g+=filtr_ID6.getText()+"%";
       h+=filtr_ID7.getText()+"%";
       i+=filtr_ID8.getText()+"%";
       SelectCzytelnicyToTable(b.selectCzytelnicyZAdresem(a,be,c,d,e,f,g,h,i));
       IDfield.setText("ID");
        edycjaButton.setEnabled(false);
        usunCzyt.setEnabled(false);
    }//GEN-LAST:event_pokazCzytelnikowActionPerformed

    private void TabelaCzytelnicyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TabelaCzytelnicyKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            
                int row = TabelaCzytelnicy.getSelectedRow();
                int column = TabelaCzytelnicy.getSelectedColumn();

                // resul is the new value to insert in the DB
                String resul = TabelaCzytelnicy.getValueAt(row, column).toString();
                // id is the primary key of my DB
                String id = TabelaCzytelnicy.getValueAt(row, 0).toString();
               // b.updateCzytelnik(id,resul,column);
        }
        //else if (evt.getKeyCode() == KeyEvent.VK_UP) {int row = jTable2.getSelectedRow();
         //String tmp = jTable2.getValueAt(row, 0).toString();
        //IDfield.setText(tmp);}
    }//GEN-LAST:event_TabelaCzytelnicyKeyPressed

    private void usunCzytActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usunCzytActionPerformed
         int row = TabelaCzytelnicy.getSelectedRow();
         String tmp = TabelaCzytelnicy.getValueAt(row, 0).toString()+" "+TabelaCzytelnicy.getValueAt(row, 1).toString()+" "+TabelaCzytelnicy.getValueAt(row, 2).toString();
         
        int potwierdzenie=JOptionPane.showConfirmDialog(this, "Usunąć: "+tmp+" ?", "Usunąć czytelnika?", JOptionPane.OK_CANCEL_OPTION);
        if (potwierdzenie==0)
        {
            b.DeleteCzytelnikId(IDfield.getText());
            pokazCzytelnikow.doClick();
        }
        usunCzyt.setEnabled(false);
        IDfield.setText("ID");
        komunikaty.setText("USUNIĘTO CZYTELNIKA");
    }//GEN-LAST:event_usunCzytActionPerformed

    private void TabelaCzytelnicyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabelaCzytelnicyMousePressed
         int row = TabelaCzytelnicy.getSelectedRow();
         String tmp = TabelaCzytelnicy.getValueAt(row, 0).toString();
         selectedPesel = TabelaCzytelnicy.getValueAt(row, 3).toString();
        IDfield.setText(tmp);
        edycjaButton.setEnabled(true);
       usunCzyt.setEnabled(true);
    }//GEN-LAST:event_TabelaCzytelnicyMousePressed

    private void TabelaCzytelnicyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TabelaCzytelnicyKeyReleased
        if ((evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN) ) 
        {int row = TabelaCzytelnicy.getSelectedRow();
         String tmp = TabelaCzytelnicy.getValueAt(row, 0).toString();
         selectedPesel = TabelaCzytelnicy.getValueAt(row, 3).toString();
        IDfield.setText(tmp);}
    }//GEN-LAST:event_TabelaCzytelnicyKeyReleased

    private void filtr_IDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_IDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_IDActionPerformed

    private void filtr_IDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filtr_IDFocusGained
        filtr_ID.selectAll();
    }//GEN-LAST:event_filtr_IDFocusGained

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        oknotest.setLocation(dim.width/2-(oknotest.getSize().width)/2, dim.height/2-oknotest.getSize().height/2);
    SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>()
    {
        @Override
        protected Void doInBackground()
        {
            czytAddLoading();
            return null;
        }
 
        @Override
        protected void done()
        {
       // loading.dispose();
            myProgressBar.setVisible(false);
            myProgressBar.setValue(0);
            oknotest.setVisible(true);
            oknotest.toFront();
            czyt_add_imie.requestFocus(true);
        }
    };
    
    worker.execute();
    myProgressBar.setVisible(true);
    //oknotest.requestFocus();
    //oknotest.setAlwaysOnTop(rootPaneCheckingEnabled);       
      
    }//GEN-LAST:event_jButton9ActionPerformed

    private void edycjaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edycjaButtonActionPerformed
        
        userDetails.setLocation(dim.width/2-(userDetails.getSize().width)/2, dim.height/2-userDetails.getSize().height/2);
        List lista = new ArrayList();
        lista = b.selectCzytelnicyByPESEL(selectedPesel);
       
        ID_user.setText("ID: "+lista.get(0));
        user_name.setText((String)lista.get(1));
        user_surname.setText((String)lista.get(2));
        user_DOB.setText((String)lista.get(4));
        user_pesel.setText((String)lista.get(3));
        user_username.setText((String)lista.get(6));
        user_street.setText((String)lista.get(8));
        user_nr.setText((String)lista.get(9));
        user_city.setText((String)lista.get(10)+ " "+(String)lista.get(11));
        user_phone.setText((String)lista.get(12));
        user_debt.setText(Float.toString((float)lista.get(13)));
        user_email.setText((String)lista.get(5));
        
        
        
        
        
        userDetails.setVisible(true);
    }//GEN-LAST:event_edycjaButtonActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        dodajMiasto.dispose();  
        komunikaty.setText("");
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        String miasto=miastoADD.getText();
        String kod=kodADD.getText();
        if (Pattern.matches("^[0-9]{2}-[0-9]{3}$", kod)&& !miasto.isEmpty())  {
            if (b.selectCountMiasto(kod)!=0) {
                komunikaty.setText("taki kod pocztowy juz jest w bazie");
                
                String miasto2=b.selectMiastoWhereKod(kod);
                czyt_add_miasto.setText(miasto2+" "+kod);
            }
            else {b.insertMiasto(miasto, kod); modelboxmiasta.addElement(miasto+" "+kod);
            czyt_add_miasto.setText(miasto+" "+kod);}
            dodajMiasto.setVisible(false);
            miastoADD.setText("");
            kodADD.setText("");
            }
        else {
            komunikaty.setText("bledne dane");
            if (!miasto.isEmpty()) { kodADD.requestFocus(); kodADD.selectAll(); }
            else miastoADD.requestFocus();
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void user_debtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_debtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_debtActionPerformed

    private void user_cityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_cityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_cityActionPerformed

    private void user_phoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_phoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_phoneActionPerformed

    private void user_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_emailActionPerformed

    private void user_nrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_nrActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_nrActionPerformed

    private void user_streetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_streetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_streetActionPerformed

    private void user_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_usernameActionPerformed

    private void user_peselActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_peselActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_peselActionPerformed

    private void user_DOBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_DOBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_DOBActionPerformed

    private void user_surnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_surnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_surnameActionPerformed

    private void user_edycjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_edycjaActionPerformed
        if(user_edycja.getText().equals("Edycja")){
        user_name.setEditable(true);user_name.setBorder(BorderFactory.createEtchedBorder());
        user_surname.setEditable(true);user_surname.setBorder(BorderFactory.createEtchedBorder());
        user_email.setEditable(true);user_name.setBorder(BorderFactory.createEtchedBorder());
        user_username.setEditable(true);user_name.setBorder(BorderFactory.createEtchedBorder());
        user_phone.setEditable(true);user_name.setBorder(BorderFactory.createEtchedBorder());
        user_debt.setEditable(true);user_name.setBorder(BorderFactory.createEtchedBorder());
        user_nr.setEditable(true);user_name.setBorder(BorderFactory.createEtchedBorder());
        user_edycja.setText("Zapisz");}
        else 
        {
            user_edycja.setText("Edycja");
            b.editCzytelnik(user_pesel.getText(), user_name.getText(), user_surname.getText(), user_username.getText(), user_nr.getText(),  user_email.getText(), user_phone.getText(), Float.parseFloat(user_debt.getText()));
            user_name.setEditable(false);user_name.setBorder(null);
        user_surname.setEditable(false);user_surname.setBorder(null);
        user_email.setEditable(false);user_name.setBorder(null);
        user_username.setEditable(false);user_name.setBorder(null);
        user_phone.setEditable(false);user_name.setBorder(null);
        user_debt.setEditable(false);user_name.setBorder(null);
        user_nr.setEditable(false);user_name.setBorder(null);
            
        }
    }//GEN-LAST:event_user_edycjaActionPerformed

    private void pokazKsiazkiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazKsiazkiActionPerformed
       String a="%",be="%",c="%",d="%",e="%",f="%";
       String tmp=filtr_ID9.getText();
       a+=tmp.replaceFirst("^0+(?!$)", "")+"%";
       be+=filtr_ID10.getText()+"%";
       c+=filtr_ID11.getText()+"%";
       d+=filtr_ID12.getText()+"%";
       e+=filtr_ID13.getText()+"%";
       f+=filtr_ID14.getText()+"%";

        
        SelectBooksToTable(b.selectBooksToTable(a,be,c,d,e,f));
        ID_books_field.setText("ID");
        books_details.setEnabled(false);
        usunKsiazke.setEnabled(false);
        dodajEgzButton.setEnabled(false);
        
    }//GEN-LAST:event_pokazKsiazkiActionPerformed

    private void user_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_closeActionPerformed
       userDetails.dispose();
       komunikaty.setText("");
    }//GEN-LAST:event_user_closeActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
       oknoDodajBook.setLocation(dim.width/2-(oknoDodajBook.getSize().width)/2, dim.height/2-oknoDodajBook.getSize().height/2);
        listaGatunki();
        listaKategorie();
        listaDzialy();
        listaAutorzy();
        dzialNew.setText("wybierz z listy");
        autorNew.setText("wybierz z listy");
        gatunekNew.setText("wybierz z listy");
        kategoriaNew.setText("wybierz z listy");
       oknoDodajBook.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void books_detailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_books_detailsActionPerformed
        booksDetails.setLocation(dim.width/2-(booksDetails.getSize().width)/2, dim.height/2-booksDetails.getSize().height/2);
        listDzial.setVisible(false);
        listGatunek.setVisible(false);
        listKategoria.setVisible(false);
        listaGatunki();
        listaKategorie();
        listaDzialy();
        int tmp= Integer.parseInt(ID_books_field.getText());
        List lista = new ArrayList();
        lista = b.selectBookByID(tmp);
        
        ID_book.setText("ID: "+ID_books_field.getText());
        title.setText((String)lista.get(1));
        title.setCaretPosition(0);
        autor.setText((String)lista.get(2)+"  "+(String)lista.get(3));
        autor.setCaretPosition(0);
        kategoria.setText((String)lista.get(4));
        kategoria.setCaretPosition(0);
        dzial.setText((String)lista.get(5));
        dzial.setCaretPosition(0);
        gatunek.setText((String)lista.get(6));
        gatunek.setCaretPosition(0);
        opis.setText((String)lista.get(7));
        opis.setCaretPosition(0);
//        user_nr.setText((String)lista.get(9));
//        user_city.setText((String)lista.get(10)+ " "+(String)lista.get(11));
//        user_phone.setText((String)lista.get(12));
//        user_debt.setText(Float.toString((float)lista.get(13)));
//        user_email.setText((String)lista.get(5));
        
        
        
        
        
        booksDetails.setVisible(true);
    }//GEN-LAST:event_books_detailsActionPerformed

    private void TabelaBooksMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabelaBooksMousePressed
        int row = TabelaBooks.getSelectedRow();
         String tmp = TabelaBooks.getValueAt(row, 0).toString();
        usunKsiazke.setEnabled(true);
        ID_books_field.setText(tmp);
        books_details.setEnabled(true);
        dodajEgzButton.setEnabled(true);
    }//GEN-LAST:event_TabelaBooksMousePressed

    private void gatunekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gatunekActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gatunekActionPerformed

    private void autorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_autorActionPerformed

    private void books_edycja1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_books_edycja1ActionPerformed
        int tmp= Integer.parseInt(ID_books_field.getText());
        if (books_edycja1.getText().equals("Edycja"))
        {
            listGatunek.setEnabled(true);
            listGatunek.setVisible(true);
            listKategoria.setEnabled(true);
            listKategoria.setVisible(true);
            listDzial.setEnabled(true);
            listDzial.setVisible(true);
            opis.setEditable(true);
            title.setEditable(true);
            books_edycja1.setText("Zapisz");
        }
        else
        {

            int id_dzialu=0, id_kategori=0, id_gatunku=0;
            id_dzialu=b.selectIDWhereSzukana(dzial.getText(), "id_dzialu", "nazwa_dzi", "dzialy");
            id_kategori=b.selectIDWhereSzukana(kategoria.getText(), "id_kategori", "nazwa_kat", "kategorie");
            id_gatunku=b.selectIDWhereSzukana(gatunek.getText(), "id_gatunku", "nazwa_gatunku", "gatunki");
            b.editBook(tmp, title.getText(), id_dzialu, id_gatunku, id_kategori, opis.getText());
            
            listGatunek.setEnabled(false);
            listKategoria.setEnabled(false);
            listDzial.setEnabled(false);
            listDzial.setVisible(false);
            listGatunek.setVisible(false);
            listKategoria.setVisible(false);
            books_edycja1.setText("Edycja");
            opis.setEditable(false);
            title.setEditable(false);
        }
    }//GEN-LAST:event_books_edycja1ActionPerformed

    private void listKategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listKategoriaActionPerformed
        kategoria.setText((String)listKategoria.getSelectedItem());
    }//GEN-LAST:event_listKategoriaActionPerformed

    private void books_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_books_closeActionPerformed
        listGatunek.setEnabled(false);
        listKategoria.setEnabled(false);
        listDzial.setEnabled(false);
        books_edycja1.setText("Edycja");
        opis.setEditable(false);
        booksDetails.dispose();
        komunikaty.setText("");
    }//GEN-LAST:event_books_closeActionPerformed

    private void listGatunekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listGatunekActionPerformed
        gatunek.setText((String)listGatunek.getSelectedItem());
    }//GEN-LAST:event_listGatunekActionPerformed

    private void kategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kategoriaActionPerformed

    private void listDzialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listDzialActionPerformed
        dzial.setText((String)listDzial.getSelectedItem());
    }//GEN-LAST:event_listDzialActionPerformed

    private void dzialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dzialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dzialActionPerformed

    private void oknoDodajBookWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknoDodajBookWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_oknoDodajBookWindowActivated

    private void oknotestWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknotestWindowActivated
        czyt_add_imie.requestFocus();
    }//GEN-LAST:event_oknotestWindowActivated

    private void lista_uliceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lista_uliceActionPerformed
        czyt_add_ulica.setText((String)lista_ulice.getSelectedItem());
        if (czyt_add_ulica.getText().equals(" Inna...")) {czyt_add_ulica.setEditable(true); czyt_add_ulica.setText(""); czyt_add_ulica.requestFocus();}
        else czyt_add_ulica.setEditable(false);
    }//GEN-LAST:event_lista_uliceActionPerformed

    private void lista_miastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lista_miastaActionPerformed
        czyt_add_miasto.setText((String)lista_miasta.getSelectedItem());
    }//GEN-LAST:event_lista_miastaActionPerformed

    private void lista_miastaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lista_miastaFocusLost
        
        if (czyt_add_miasto.getText().equals(" Inne...")) {czyt_add_miasto.setEditable(false); czyt_add_miasto.setText("");
            
            dodajMiasto.setLocation(dim.width/2-(dodajMiasto.getSize().width/2), dim.height/2-dodajMiasto.getSize().height/2);
            dodajMiasto.setVisible(true);}

        else czyt_add_ulica.setEditable(false);
    }//GEN-LAST:event_lista_miastaFocusLost

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        oknotest.dispose();
        komunikaty.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void dodajButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dodajButtonActionPerformed
        String imie = firstLetterCaps(czyt_add_imie.getText());
        String nazwisko = firstLetterCaps(czyt_add_nazwisko.getText());
        String pesel = czyt_add_pesel.getText();
        String DOB = czyt_add_DOB.getText();
        String email = czyt_add_email.getText();
        String username = czyt_add_username.getText();
        String password = czyt_add_password.getText();
        String id_ulica = czyt_add_ulica.getText();
        int ulica=0;
        int miasto=0;
        String id_miasto = czyt_add_miasto.getText();
        String numer_domu = czyt_add_nr.getText();
        String telefon = czyt_add_telefon.getText();

        if (imie.isEmpty())
        {
            komunikatCzyt.setText("bledne imie");
            czyt_add_imie.requestFocus();
            czyt_add_imie.selectAll();
        }
        else if (nazwisko.isEmpty() )
        {
            komunikatCzyt.setText("bledne nazwisko");
            czyt_add_nazwisko.requestFocus();
            czyt_add_nazwisko.selectAll();
        }
        else if (!sprawdzPesel(pesel))
        {
            komunikatCzyt.setText("bledny pesel");
            czyt_add_pesel.requestFocus();
            czyt_add_pesel.selectAll();
        }
        //        else if (DOB.length()!=10 )
        //            {
            //            System.out.println("bledna data urodzenia");
            //            czyt_add_DOB.requestFocus();
            //            czyt_add_DOB.selectAll();
            //            }
        else if (email.isEmpty() || !email.contains("@") || !email.contains(".") )
        {
            komunikatCzyt.setText("bledny email");
            czyt_add_email.requestFocus();
            czyt_add_email.selectAll();
        }
        else if (password.length()<8)
        {
            komunikatCzyt.setText("bledne haslo (min. 8)");
            czyt_add_password.requestFocus();
            czyt_add_password.selectAll();
        }
        else if (id_miasto.isEmpty())
        {
            komunikatCzyt.setText("puste miato");
            czyt_add_miasto.requestFocus();
            czyt_add_miasto.selectAll();
        }
        else if (id_ulica.isEmpty()) {
            komunikatCzyt.setText("pusta ulica"); }
        

        else    {
            if (username.isEmpty() )
            {   komunikatCzyt.setText("wstawiam domyslna nazwe uzytkownika (pesel)");
                username = pesel;
                czyt_add_username.setText(username);
            }

            int tmp=0;
            tmp = b.selectCountUniwersalny(pesel, "czytelnicy", "pesel");
            if (tmp!=0) komunikatCzyt.setText("taki PESEL juz istnieje");
            else { tmp=0;
                tmp = b.selectCountUniwersalny(username, "czytelnicy", "username");
                if (tmp!=0) komunikatCzyt.setText("taki USERNAME juz istnieje");
                else { tmp=0;
                    tmp = b.selectCountUniwersalny(email, "czytelnicy", "email");
                    if (tmp!=0) komunikatCzyt.setText("taki E-MAIL juz istnieje");
                    else {
                        if (lista_ulice.getSelectedItem().equals(" Inna..."))
                            {
                            System.out.println("ulica nie z listy");
                                System.out.println(id_ulica);
                            if (b.selectCountUlica(id_ulica)==0) {b.insertUlica(id_ulica);}
                            }
                        ulica  = b.selectUlicaWhereUlica(id_ulica);
                        String s="";
                        s=id_miasto.substring(id_miasto.lastIndexOf(' ') + 1);
                        miasto =b.selectMiastaWhereKod(s);
                        String hash=hashPassword(password);

                        b.insertCzytelnik(imie, nazwisko, pesel, DOB, email, username, hash, miasto, ulica, numer_domu, telefon);

                        System.out.println("dodano czytelnika");
                        
                        String [][]dodane = new String[1][10];

                        dodane = b.selectCzytelnicyZAdresem_WherePESEL(pesel);
                        //System.out.println(Arrays.deepToString(dodane));
                        String dodany = "ID: " + dodane[0][0] + "\nImie: " + dodane[0][1]+ "\nNazwisko: " + dodane[0][2]+ "\nPesel: " + dodane[0][3]+ "\nData Urodzenia: " + dodane[0][4]+ "\neMail: " + dodane[0][5]+ "\nUżytkownik: " + dodane[0][6]+ "\nHasło: " +password+ "\nAdres: " + dodane[0][8]+ "\nTelefon: " + dodane[0][9];
                        //System.out.println(dodany);
                        
                        JOptionPane.showMessageDialog(this, "Dodano nowego czytelnika:\n\n"+dodany, "Dodano czytelnika", JOptionPane.INFORMATION_MESSAGE);
                        oknotest.setVisible(false);
                        pokazCzytelnikow.doClick();
                    }}}
                }
    }//GEN-LAST:event_dodajButtonActionPerformed

    private void czyt_add_nazwiskoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_czyt_add_nazwiskoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_czyt_add_nazwiskoActionPerformed

    private void czyt_add_peselActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_czyt_add_peselActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_czyt_add_peselActionPerformed

    private void czyt_add_peselFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_czyt_add_peselFocusLost
        czyt_add_DOB.setText(dataUrodzenia(czyt_add_pesel.getText().trim()));
        if (!sprawdzPesel(czyt_add_pesel.getText().trim()))
        {
            dodajButton.setEnabled(false);
            czyt_add_pesel.requestFocus();  czyt_add_pesel.selectAll();
        }
        else if (czyPrzyszlosc(czyt_add_DOB.getText()))
        {
            czyt_add_DOB.setText("Pesel z przyszłości");
            dodajButton.setEnabled(false);
            czyt_add_pesel.requestFocus();  czyt_add_pesel.selectAll();
        }
        else dodajButton.setEnabled(true);
    }//GEN-LAST:event_czyt_add_peselFocusLost

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String password = passwordGenerator.generate(8);
        czyt_add_password.setText(password);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void listKategoriaNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listKategoriaNewActionPerformed
        kategoriaNew.setText((String)listKategoriaNew.getSelectedItem());
    }//GEN-LAST:event_listKategoriaNewActionPerformed

    private void listGatunekNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listGatunekNewActionPerformed
        gatunekNew.setText((String)listGatunekNew.getSelectedItem());
    }//GEN-LAST:event_listGatunekNewActionPerformed

    private void listDzialNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listDzialNewActionPerformed
        dzialNew.setText((String)listDzialNew.getSelectedItem());

    }//GEN-LAST:event_listDzialNewActionPerformed

    private void listDzialNewFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listDzialNewFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_listDzialNewFocusLost

    private void autorNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorNewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_autorNewActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        dzialNew.setText("wybierz z listy");
        gatunekNew.setText("wybierz z listy");
        kategoriaNew.setText("wybierz z listy");
        tittleNew.setText("");
        autorNew.setText("");
        opisNew.setText("");
        oknoDodajBook.dispose();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void dodajButtonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dodajButtonNewActionPerformed
       if (tittleNew.getText().isEmpty())
       {
           System.out.println("Brak tytulu");
           tittleNew.requestFocus();
       }
       else if (autorNew.getText().isEmpty())
       {
           System.out.println("Brak autora");
           autorNew.requestFocus();
       }
       else if (dzialNew.getText().equals("wybierz z listy"))
       {
           System.out.println("Brak dzialu");
           listDzialNew.requestFocus();
       }
       else if (gatunekNew.getText().equals("wybierz z listy"))
       {
           System.out.println("Brak gatunku");
           listGatunekNew.requestFocus();
       }
       else if (kategoriaNew.getText().equals("wybierz z listy"))
       {
           System.out.println("Brak kategori");
           listKategoriaNew.requestFocus();
       }
       else
       {
           String autor=autorNew.getText();
           String nazwisko_autora=""; 
           nazwisko_autora=autor.substring(0,autor.indexOf(" "));
           nazwisko_autora.trim();
           System.out.println("nazwisko autora: "+ nazwisko_autora);
           String imie_autora=""; 
           imie_autora=autor.substring(autor.lastIndexOf(" ")+1);
           imie_autora.trim();
           System.out.println("imie autora: "+ imie_autora);
           int autor_id  = b.selectAutorzyWhereNazwiskoIImie(nazwisko_autora, imie_autora);
            int id_dzialu=0, id_kategori=0, id_gatunku=0;
            id_dzialu=b.selectIDWhereSzukana(dzial.getText(), "id_dzialu", "nazwa_dzi", "dzialy");
            id_kategori=b.selectIDWhereSzukana(kategoria.getText(), "id_kategori", "nazwa_kat", "kategorie");
            id_gatunku=b.selectIDWhereSzukana(gatunek.getText(), "id_gatunku", "nazwa_gatunku", "gatunki");
            b.insertBook(tittleNew.getText(), autor_id, id_dzialu, id_gatunku, id_kategori, opisNew.getText() );
            JOptionPane.showMessageDialog(this, "Dodano nowegą Książkę :\n\n"+tittleNew.getText(), "Dodano książkę", JOptionPane.INFORMATION_MESSAGE);
            oknoDodajBook.setVisible(false);
       }    pokazKsiazki.doClick();
    }//GEN-LAST:event_dodajButtonNewActionPerformed

    private void listAutorNewFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listAutorNewFocusLost
        if (autorNew.getText().equals(" Inny..."))
                {
                    dodajAutora.setLocation(dim.width/2-(dodajAutora.getSize().width/2), dim.height/2-dodajAutora.getSize().height/2);
                    dodajAutora.setVisible(true);
                    
                }
    }//GEN-LAST:event_listAutorNewFocusLost

    private void listAutorNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listAutorNewActionPerformed
        autorNew.setText((String)listAutorNew.getSelectedItem());
    }//GEN-LAST:event_listAutorNewActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        String nazwisko=nazwiskoAutorAdd.getText();
        String imie=imieAutorAdd.getText();
      
                
        if (!nazwisko.isEmpty() && !imie.isEmpty())  {
            if (b.selectCountUniwersal(nazwisko, "autorzy", "nazwisko")!=0) {
                System.out.println("taki autor juz jest w bazie");
                List lista1 = new ArrayList();
                lista1=b.selectSzukaneWhereWarunek("imie", "autorzy", "nazwisko", nazwisko);
                
                boolean czyImie=false;
                for (int i=0; i<lista1.size(); i++)
                {
                    if (lista1.get(i).equals(imie)) czyImie=true;
                }
                if (czyImie==true)
                {
                    
                    //nie dodawaj autora
                    JOptionPane.showMessageDialog(this, "Taki Autor juz istnieje:\n\n"+nazwisko+" "+imie, "Nie dodano autora", JOptionPane.INFORMATION_MESSAGE);
                    dodajAutora.setVisible(false);
                    autorNew.setText(nazwisko+" "+imie);
                    oknoDodajBook.toFront();
                }
                else {
                    //System.out.println("nazwisko jest, ale z innym imieniem");
                    //dodaj autora
                    b.insertAutor(nazwisko, imie);
                    dodajAutora.setVisible(false);
                    autorNew.setText(nazwisko+" "+imie);
                    oknoDodajBook.toFront();
                    modelboxautorzy.addElement(nazwisko+" "+imie);
                    }
            
               }
            else b.insertAutor(nazwisko, imie);
            dodajAutora.setVisible(false);
            autorNew.setText(nazwisko+" "+imie);
            oknoDodajBook.toFront();
            modelboxautorzy.addElement(nazwisko+" "+imie);
           
        }
        else System.out.println("puste dane");
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        dodajAutora.dispose();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void usunKsiazkeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usunKsiazkeActionPerformed
        int row = TabelaBooks.getSelectedRow();
        String selectedTytul = TabelaBooks.getValueAt(row, 1).toString();
        int potwierdzenie=JOptionPane.showConfirmDialog(this, "Usunąć: "+selectedTytul+" ?", "Usunąć książkę?", JOptionPane.OK_CANCEL_OPTION);
        if (potwierdzenie==0)
        {
            b.DeleteOneUniwersalWhereID(ID_books_field.getText(), "ksiazki", "id_ksiazki");
            pokazKsiazki.doClick();
        }
    }//GEN-LAST:event_usunKsiazkeActionPerformed

    private void dodajButtonEgzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dodajButtonEgzActionPerformed
        if (lokalizacjaField.getText().isEmpty()|| lokalizacjaField.getText().equals("Wybierz z listy"))
        {
            
            lokalizacjaField.setText("Wybierz z listy");
            lokalizacjaField.requestFocus();
            lokalizacjaField.selectAll();  
        }
        else 
            if (stanField.getText().isEmpty()|| stanField.getText().equals("Wybierz z listy"))
        {
            
            stanField.setText("Wybierz z listy");
            stanField.requestFocus();
            stanField.selectAll();  
        } else
        if (wydawnictwoField.getText().isEmpty()|| wydawnictwoField.getText().equals("Wybierz z listy"))
        {
            
            wydawnictwoField.setText("Wybierz z listy");
            wydawnictwoField.requestFocus();
            wydawnictwoField.selectAll(); 
        }
        else { String patern = rokField.getText();
               if (!Pattern.matches("^[0-9]{4}$", patern)) 
                    {
                        rokField.setText("RRRR");
                        rokField.requestFocus();
                        rokField.selectAll();
                    }
        
        else if (jezykField.getText().equals("") || jezykField.getText().equals("Wpisz język egzemplarza") )
                {
                        jezykField.setText("Wpisz język egzemplarza");
                        jezykField.requestFocus();
                        jezykField.selectAll();
                }
        else {
        int lok= (b.selectIDWhereSzukana(lokalizacjaField.getText(), "id_lokalizacji", "nazwa_lokalizacji", "lokalizacje"));
        int sta= (b.selectIDWhereSzukana(stanField.getText(), "id_stanu", "nazwa_stanu", "stany"));
        int wyd= (b.selectIDWhereSzukana(wydawnictwoField.getText(), "id_wydawnictwa", "nazwa_wyd", "wydawnictwa"));
        int row = TabelaBooks.getSelectedRow();
        int id= Integer.parseInt((String)TabelaBooks.getValueAt(row, 0));
        b.insertEgzemplarz(id, lok, sta, wyd, rokField.getText(), jezykField.getText());
        rokField.setText("");
        jezykField.setText("");
        oknoDodajEgzemplarz.setVisible(false);
        JOptionPane.showMessageDialog(this, "Dodano nowy egzemplarz:\n\n"+newIDEgz.getText()+"\n\n"+tittleNew1.getText()+"\n"+autorNew1.getText(), "Dodano egzemplarz", JOptionPane.INFORMATION_MESSAGE);
        }}
    }//GEN-LAST:event_dodajButtonEgzActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        oknoDodajEgzemplarz.dispose();
    }//GEN-LAST:event_jButton16ActionPerformed

    private void autorNew1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorNew1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_autorNew1ActionPerformed

    private void listLokalizacjaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listLokalizacjaFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_listLokalizacjaFocusLost

    private void listLokalizacjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listLokalizacjaActionPerformed
        lokalizacjaField.setText((String)listLokalizacja.getSelectedItem());
    }//GEN-LAST:event_listLokalizacjaActionPerformed

    private void listStanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listStanActionPerformed
        stanField.setText((String)listStan.getSelectedItem());
    }//GEN-LAST:event_listStanActionPerformed

    private void listWydawnictwoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listWydawnictwoActionPerformed
        wydawnictwoField.setText((String)listWydawnictwo.getSelectedItem());
    }//GEN-LAST:event_listWydawnictwoActionPerformed

    private void oknoDodajEgzemplarzWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknoDodajEgzemplarzWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_oknoDodajEgzemplarzWindowActivated

    private void rokFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rokFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rokFieldActionPerformed

    private void jezykFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jezykFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jezykFieldActionPerformed

    private void dodajEgzButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dodajEgzButtonActionPerformed
        oknoDodajEgzemplarz.setLocation(dim.width/2-(oknoDodajEgzemplarz.getSize().width)/2, dim.height/2-oknoDodajEgzemplarz.getSize().height/2);
        listaLokalizacje();
        listaStany();
        listaWydawnictwa();
        int row = TabelaBooks.getSelectedRow();
        int id= Integer.parseInt((String)TabelaBooks.getValueAt(row, 0));
        String selectedTytul = TabelaBooks.getValueAt(row, 1).toString();
        String selectedAutor = TabelaBooks.getValueAt(row, 2).toString();
        tittleNew1.setText(selectedTytul);
        autorNew1.setText(selectedAutor);
        dzialNew.setText("wybierz z listy");
        autorNew.setText("wybierz z listy");
        gatunekNew.setText("wybierz z listy");
        kategoriaNew.setText("wybierz z listy");
        int noweID = b.selectMaxIDEgzemplarze()+1;
        newIDEgz.setText(String.format("%08d", noweID));
        //System.out.println("id ksiazki: "+ id);
        rokField.setText("");
        jezykField.setText("");
        oknoDodajEgzemplarz.setVisible(true);
    }//GEN-LAST:event_dodajEgzButtonActionPerformed

    private void TabelaEgzemplarzeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabelaEgzemplarzeMousePressed
       int row = TabelaEgzemplarze.getSelectedRow();
         String tmp = TabelaEgzemplarze.getValueAt(row, 0).toString();
        usunEgzemplarz.setEnabled(true);
        ID_egzemplarza_field.setText(tmp);
        egzemplarz_edit.setEnabled(true);
        
    }//GEN-LAST:event_TabelaEgzemplarzeMousePressed

    private void egzemplarz_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_egzemplarz_editActionPerformed
        egzemplarzEdit.setLocation(dim.width/2-(egzemplarzEdit.getSize().width)/2, dim.height/2-egzemplarzEdit.getSize().height/2);
        listLOK.setVisible(false);
        listSTA.setVisible(false);
        listWYD.setVisible(false);
        listaLokalizacje();
        listaStany();
        listaWydawnictwa();
        int tmp= Integer.parseInt(ID_egzemplarza_field.getText());
        List lista = new ArrayList();
        lista = b.selectEgzemplarzByID(tmp);
        //System.out.println("wczytal liste egzemplarz po ID");
        //System.out.println(lista.get(0));
        ID_egzemplarz.setText("ID: "+ID_egzemplarza_field.getText());
        title2.setText((String)lista.get(1));
        title2.setCaretPosition(0);
        autor2.setText((String)lista.get(2)+"  "+(String)lista.get(3));
        autor2.setCaretPosition(0);
        lokalizacja.setText((String)lista.get(4));
        lokalizacja.setCaretPosition(0);
        stan.setText((String)lista.get(5));
        stan.setCaretPosition(0);
        wydawnictwo.setText((String)lista.get(6));
        wydawnictwo.setCaretPosition(0);
        rok.setText((String)lista.get(7));
        rok.setCaretPosition(0);
        jezyk.setText((String)lista.get(8));
        jezyk.setCaretPosition(0);
        
        
        
        
        egzemplarzEdit.setVisible(true);
        
    }//GEN-LAST:event_egzemplarz_editActionPerformed

    private void usunEgzemplarzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usunEgzemplarzActionPerformed
          int row = TabelaEgzemplarze.getSelectedRow();
        String selectedTytul = TabelaEgzemplarze.getValueAt(row, 1).toString();
        int potwierdzenie=JOptionPane.showConfirmDialog(this, "Usunąć: "+selectedTytul+" ?", "Usunąć egzemplarz?", JOptionPane.OK_CANCEL_OPTION);
        if (potwierdzenie==0)
        {
            b.DeleteOneUniwersalWhereID(ID_egzemplarza_field.getText(), "egzemplarze", "id_egzemplarza");
            int ID_book=  Integer.parseInt(ID_egzemplarza_field.getText());
            SelectEgzemplarzeToTable(b.selectEgzemplarzeToTable(ID_book));
        }
    }//GEN-LAST:event_usunEgzemplarzActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // ZakladkaEgzemplarze.requestFocus();
        int ID_book=  Integer.parseInt(ID_books_field.getText());
        SelectEgzemplarzeToTable(b.selectEgzemplarzeToTable(ID_book));
        zakladki.setSelectedIndex(2);
        booksDetails.setVisible(false);
        // pokazEgzemplarze.doClick();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void lokalizacjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lokalizacjaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lokalizacjaActionPerformed

    private void listLOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listLOKActionPerformed
        lokalizacja.setText((String)listLOK.getSelectedItem());
    }//GEN-LAST:event_listLOKActionPerformed

    private void wydawnictwoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wydawnictwoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_wydawnictwoActionPerformed

    private void listSTAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listSTAActionPerformed
        stan.setText((String)listSTA.getSelectedItem());
    }//GEN-LAST:event_listSTAActionPerformed

    private void books_close2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_books_close2ActionPerformed
        egzemplarzEdit.dispose();
    }//GEN-LAST:event_books_close2ActionPerformed

    private void listWYDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listWYDActionPerformed
        wydawnictwo.setText((String)listWYD.getSelectedItem());
    }//GEN-LAST:event_listWYDActionPerformed

    private void egz_edycjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_egz_edycjaActionPerformed
        int tmp= Integer.parseInt(ID_egzemplarza_field.getText());
        if (egz_edycja.getText().equals("Edycja"))
        {
            listLOK.setEnabled(true);
            listLOK.setVisible(true);
            listSTA.setEnabled(true);
            listSTA.setVisible(true);
            listWYD.setEnabled(true);
            listWYD.setVisible(true);
            rok.setEditable(true);
            jezyk.setEditable(true);
            egz_edycja.setText("Zapisz");
        }
        else
        {

            int id_lok=0, id_sta=0, id_wyd=0;
            id_lok=b.selectIDWhereSzukana(lokalizacja.getText(), "id_lokalizacji", "nazwa_lokalizacji", "lokalizacje");
            id_sta=b.selectIDWhereSzukana(stan.getText(), "id_stanu", "nazwa_stanu", "stany");
            id_wyd=b.selectIDWhereSzukana(wydawnictwo.getText(), "id_wydawnictwa", "nazwa_wyd", "wydawnictwa");
            b.editEgzemplarz(tmp,id_lok, id_sta, id_wyd, rok.getText(), jezyk.getText());
           
            listLOK.setEnabled(false);
            listSTA.setEnabled(false);
            listWYD.setEnabled(false);
            listWYD.setVisible(false);
            listLOK.setVisible(false);
            listSTA.setVisible(false);
            egz_edycja.setText("Edycja");
            rok.setEditable(false);
            jezyk.setEditable(false);
            String id_egz=Integer.toString(tmp);
           
            int id_book_from_egz=b.selectIDWhereSzukana(id_egz, "id_ksiazki", "id_egzemplarza" ,"egzemplarze");
            //int ID_egz=  Integer.parseInt(ID_books_field.getText());
            SelectEgzemplarzeToTable(b.selectEgzemplarzeToTable(id_book_from_egz));
            usunEgzemplarz.setEnabled(false);
            egzemplarz_edit.setEnabled(false);
        }
    }//GEN-LAST:event_egz_edycjaActionPerformed

    private void autor2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autor2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_autor2ActionPerformed

    private void stanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stanActionPerformed

    private void filtr_ID7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID7ActionPerformed

    private void filtr_ID2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID2ActionPerformed

    private void filtr_ID1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID1ActionPerformed

    private void pokazCzytelnikow1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazCzytelnikow1ActionPerformed
        filtr_ID.setText("");filtr_ID1.setText("");filtr_ID2.setText("");filtr_ID3.setText("");
        filtr_ID4.setText("");filtr_ID5.setText("");filtr_ID6.setText("");filtr_ID7.setText("");filtr_ID8.setText("");
    }//GEN-LAST:event_pokazCzytelnikow1ActionPerformed

    private void filtr_ID9FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filtr_ID9FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID9FocusGained

    private void filtr_ID9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID9ActionPerformed

    private void filtr_ID10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID10ActionPerformed

    private void filtr_ID11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID11ActionPerformed

    private void pokazKsiazki1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazKsiazki1ActionPerformed
         filtr_ID9.setText("");filtr_ID10.setText("");filtr_ID11.setText("");filtr_ID12.setText("");
         filtr_ID13.setText("");filtr_ID14.setText("");
    }//GEN-LAST:event_pokazKsiazki1ActionPerformed

    private void czysc_filtr_wypActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_czysc_filtr_wypActionPerformed
        filtr_ID15.setText("");filtr_ID16.setText("");filtr_ID17.setText("");filtr_ID18.setText("");
        filtr_ID19.setText("");filtr_ID20.setText("");filtr_ID21.setText("");
    }//GEN-LAST:event_czysc_filtr_wypActionPerformed

    private void filtr_ID17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID17ActionPerformed

    private void filtr_ID16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID16ActionPerformed

    private void filtr_ID15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID15ActionPerformed

    private void filtr_ID15FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filtr_ID15FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID15FocusGained

    private void books_details1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_books_details1ActionPerformed
        oknoProlongata.setLocation(dim.width/2-(oknoProlongata.getSize().width)/2, dim.height/2-oknoProlongata.getSize().height/2);
        prolongataEgzemplarz.setText("");
        oknoProlongata.setVisible(true);
    }//GEN-LAST:event_books_details1ActionPerformed

    private void dodajEgzButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dodajEgzButton1ActionPerformed
        oknoZwrot.setLocation(dim.width/2-(oknoZwrot.getSize().width)/2, dim.height/2-oknoZwrot.getSize().height/2);
        zwrotEgzemplarz.setText("");
        oknoZwrot.setVisible(true);
    }//GEN-LAST:event_dodajEgzButton1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        oknoWypozycz.setLocation(dim.width/2-(oknoWypozycz.getSize().width)/2, dim.height/2-oknoWypozycz.getSize().height/2);
        wypozyczEgzemplarz.setText("");
        wypozyczCzytelnik.setText("");
        oknoWypozycz.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void pokazWypozyczeniaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazWypozyczeniaActionPerformed
       String a="%",be="%",c="%",d="%",e="%",f="%",g="%";
       String tmp=filtr_ID15.getText();
       a+=tmp.replaceFirst("^0+(?!$)", "")+"%";
       be+=filtr_ID16.getText()+"%";
       c+=filtr_ID17.getText()+"%";
       d+=filtr_ID18.getText()+"%";
       e+=filtr_ID19.getText()+"%";
       f+=filtr_ID21.getText()+"%";
       g+=filtr_ID20.getText()+"%";
        
        SelectWypozyczeniaToTable(b.selectWypozyczeniaToTable(a,be,c,d,e,f,g));
        ID_wyp_field.setText("ID");
        ID_egz_field.setText("ID");

    }//GEN-LAST:event_pokazWypozyczeniaActionPerformed

    private void TabelaWypozyczeniaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabelaWypozyczeniaMousePressed
         int row = TabelaWypozyczenia.getSelectedRow();
         String tmp = TabelaWypozyczenia.getValueAt(row, 0).toString();
        //usunEgzemplarz.setEnabled(true);
        ID_wyp_field.setText(tmp);
        int id_wyp=Integer.parseInt(tmp);
        int id_egz=b.selectSzukanaWhereWarunekReturnINT("id_egzemplarza", "wypozyczenia", "id_wypozycz", id_wyp);
        ID_egz_field.setText(Integer.toString(id_egz));
    }//GEN-LAST:event_TabelaWypozyczeniaMousePressed

    private void oknoWypozyczWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknoWypozyczWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_oknoWypozyczWindowActivated

    private void wypozyczCzytelnikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wypozyczCzytelnikActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_wypozyczCzytelnikActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        oknoWypozycz.dispose();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void wypozyczButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wypozyczButtonActionPerformed
        String czytelnik=wypozyczCzytelnik.getText();
        String egzemplarz=wypozyczEgzemplarz.getText();
        czytelnik=czytelnik.replaceFirst("^0+(?!$)", "");
        egzemplarz=egzemplarz.replaceFirst("^0+(?!$)", "");
        if (!Pattern.matches("^[0-9]{1,8}$", egzemplarz)) 
                {
                    System.out.println("bledne ID egzemplarza");
                }
        else if (!Pattern.matches("^[0-9]{1,8}$", czytelnik)) 
                {
                System.out.println("bledne ID czytelnika");
                }
        
        else if (b.selectCountUniwersalny(czytelnik, "czytelnicy", "id_czytelnika")==0)
                {
            System.out.println("nie znaleziono ID czytelnika");
                }
        else if (b.selectCountUniwersalny(egzemplarz, "egzemplarze", "id_egzemplarza")==0)
                {
            System.out.println("nie znaleziono ID egzemplarza");
                }
        else 
                {
                    //sprawdzic czy dane id_egzemplarza ma lokalizacje 1 lub 2 
                    
                    int czy=Integer.parseInt(czytelnik);
                    int egz=Integer.parseInt(egzemplarz);
                    if (b.selectSzukanaWhereWarunekReturnINT("lokalizacja", "egzemplarze", "id_egzemplarza", egz)>2)
                    {
                        System.out.println("ten egzemplarz nie ma statusu wypozyczalnia lub czytelnia");
                        JOptionPane.showMessageDialog(this, "Egzemplarz nr: "+egzemplarz+ " nie może być wypożyczony", "Nie mozna wypożyczyć", JOptionPane.INFORMATION_MESSAGE);
                        oknoWypozycz.setVisible(false);
                    }
                    else {
                         b.insertWypozyczenie(czy, egz, Helpers.Daty.dzis(), Helpers.Daty.dzisPlus(31), "");
                         System.out.println("wypozyczono");
                         int dodany=b.selectMaxIDUniwersal("id_wypozycz", "wypozyczenia");
                         b.editEgzemplarz(egz, 3);
                         //JOptionPane.showMessageDialog(this, "Wypożyczono:\n\n"+dodany, "Wypożyczono", JOptionPane.INFORMATION_MESSAGE);
                         oknoWypozycz.setVisible(false);
                         filtr_ID15.setText(String.valueOf(dodany));
                         pokazWypozyczenia.doClick();
                         }
                }
    }//GEN-LAST:event_wypozyczButtonActionPerformed

    private void pokazEgzemplarzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazEgzemplarzeActionPerformed
        SelectEgzemplarzeToTable(b.selectEgzemplarzeToTableAll());
        ID_egzemplarza_field.setText("ID");
        egzemplarz_edit.setEnabled(false);
        usunEgzemplarz.setEnabled(false);
    }//GEN-LAST:event_pokazEgzemplarzeActionPerformed

    private void zwrotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zwrotButtonActionPerformed
        String egzemplarz=zwrotEgzemplarz.getText();
        egzemplarz=egzemplarz.replaceFirst("^0+(?!$)", "");
        if (!Pattern.matches("^[0-9]{1,8}$", egzemplarz)) 
                {
                    System.out.println("bledne ID egzemplarza");
                }
        else if (b.selectCountUniwersalny(egzemplarz, "egzemplarze", "id_egzemplarza")==0)
                {
            System.out.println("nie znaleziono ID egzemplarza");
                }
        else 
                {                    
                    int egz=Integer.parseInt(egzemplarz);
                    if (b.selectSzukanaWhereWarunekReturnINT("lokalizacja", "egzemplarze", "id_egzemplarza", egz)!=3)
                    {
                        System.out.println("ten egzemplarz nie ma statusu WYPOŻYCZONY");
                        JOptionPane.showMessageDialog(this, "Egzemplarz nr: "+egzemplarz+ "\n nie ma statusu WYPOŻYCZONY", "Nie mozna zwrócić", JOptionPane.INFORMATION_MESSAGE);
                        oknoZwrot.setVisible(false);
                    }
                    else {
                         int id_wyp=b.selectID_WYP_with_where2(egz);
                         String data=Daty.dzis();
                         b.updateWypozyczenieDataZwrotu(id_wyp, data);
                         b.editEgzemplarz(egz, 1);
                         System.out.println("zwrócono");
                         //int dodany=b.selectMaxIDUniwersal("id_wypozycz", "wypozyczenia");
                         //b.editEgzemplarz(egz, 1);
                         JOptionPane.showMessageDialog(this, "Zwrócono egzemplarz:\n\n"+egz, "Zwrócono", JOptionPane.INFORMATION_MESSAGE);
                         String data_plan=b.selectSzukanaWhereWarunek("data_planowana", "wypozyczenia", "id_wypozycz", Integer.toString(id_wyp));
                         System.out.println(data_plan);
                         if (Daty.czyPrzeszlosc(data_plan)) System.out.println("po terminie - naliczam zadluzenie");
                         oknoZwrot.setVisible(false);
                         //filtr_ID15.setText(String.valueOf(dodany));
                         pokazWypozyczenia.doClick();
                         }
                }
    }//GEN-LAST:event_zwrotButtonActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        oknoZwrot.dispose();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void oknoZwrotWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknoZwrotWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_oknoZwrotWindowActivated

    private void prolongujButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prolongujButtonActionPerformed
        String egzemplarz=prolongataEgzemplarz.getText();
        egzemplarz=egzemplarz.replaceFirst("^0+(?!$)", "");
        if (!Pattern.matches("^[0-9]{1,8}$", egzemplarz)) 
                {
                    System.out.println("bledne ID egzemplarza");
                    prolongataEgzemplarz.requestFocus();
                    prolongataEgzemplarz.selectAll();
                }
        else if (b.selectCountUniwersalny(egzemplarz, "egzemplarze", "id_egzemplarza")==0)
                {
                    System.out.println("nie znaleziono ID egzemplarza");
                    prolongataEgzemplarz.requestFocus();
                    prolongataEgzemplarz.selectAll();
                }
        else 
                {                    
                    int egz=Integer.parseInt(egzemplarz);
                    if (b.selectSzukanaWhereWarunekReturnINT("lokalizacja", "egzemplarze", "id_egzemplarza", egz)!=3)
                    {
                        System.out.println("ten egzemplarz nie ma statusu WYPOŻYCZONY");
                        JOptionPane.showMessageDialog(this, "Egzemplarz nr: "+egzemplarz+ "\n nie ma statusu WYPOŻYCZONY", "Nie mozna zwrócić", JOptionPane.INFORMATION_MESSAGE);
                        oknoProlongata.setVisible(false);
                    }
                    else {
                         int id_wyp=b.selectID_WYP_with_where2(egz);
                         String data_plan=b.selectSzukanaWhereWarunek("data_planowana", "wypozyczenia", "id_wypozycz", Integer.toString(id_wyp));
                        
                         String data_wypozyczenia = b.selectSzukanaWhereWarunek("data_wypozyczenia", "wypozyczenia", "id_wypozycz", Integer.toString(id_wyp));
                         String data_prolongowana=Daty.miesiacePlusOdDaty(1, data_plan);
                         if (Daty.roznicaDni(data_wypozyczenia, data_plan)>31) {System.out.println("nie mozna juz prolongowac");
                          JOptionPane.showMessageDialog(this, "Egzemplarz:\n"+egz+"\n był juz prolongowany do: "+data_plan+"\n\n nie można prolongować", "Nie można prolongować", JOptionPane.INFORMATION_MESSAGE);}
                         else {
                            b.updateWypozyczenieDataPlanowana(id_wyp, data_prolongowana);
                            System.out.println("prolongowano");
                             JOptionPane.showMessageDialog(this, "Prolongowano egzemplarz:\n\n"+egz+"\n do: "+data_prolongowana, "Prolongowano", JOptionPane.INFORMATION_MESSAGE);
                            }
                         oknoProlongata.setVisible(false);
                         pokazWypozyczenia.doClick();
                         }
                }
    }//GEN-LAST:event_prolongujButtonActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
      oknoProlongata.dispose();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void oknoProlongataWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknoProlongataWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_oknoProlongataWindowActivated

    private void zalogujActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zalogujActionPerformed
        String username=user.getText();
        String password=pass.getText();
        //System.out.println(hashPassword("Kowalska"));
        String passHash=b.selectSzukanaWhereWarunek("password", "pracownicy", "username", username);
        if (passHash.equals("")) 
        {
            komunikatL.setText("nie ma uzytkownika");
            if (username.equals("admin")) 
            {
                b.insertPracownik("admin", "admin", "admin", hashPassword("admin"));
                pass.setText("admin");
                
                komunikatL.setText("dodano użytkownika admin/admin");
            }
        }
          else {
                 //System.out.println(passHash);
                 if (PasswordEncryption.checkPassword(password,passHash))
                 {
                     komunikatL.setText("");
                     LogowanieFrame.dispose();
                     komunikaty.setText("");
                     zalogowanyUser.setText(username);
                      if (!username.equals("admin")) zakladki.setEnabledAt(4, false);
                      else zakladki.setEnabledAt(4, true);
                      zakladki.setForegroundAt(4, Color.orange);
                      zakladki.setBackgroundAt(4, Color.DARK_GRAY);

                 }
                 else System.out.println("access denied");
                 komunikatL.setText("Brak dostępu");
                }
    }//GEN-LAST:event_zalogujActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        zakladki.setSelectedIndex(0);
        user.setText("Użytkownik");
        pass.setText("Hasło");
        komunikatL.setText("");
        zalogowanyUser.setText("nie zalogowany");
        LogowanieFrame.setVisible(true);
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void userFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_userFocusGained
        if (user.getText().equals("Użytkownik")) user.setText("");
    }//GEN-LAST:event_userFocusGained

    private void userFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_userFocusLost
        if (user.getText().isEmpty()) user.setText("Użytkownik");
    }//GEN-LAST:event_userFocusLost

    private void passFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passFocusGained
        if (pass.getText().equals("hasło"))pass.setText("");
    }//GEN-LAST:event_passFocusGained

    private void passFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passFocusLost
       if (pass.getText().isEmpty()) pass.setText("hasło");
    }//GEN-LAST:event_passFocusLost

    private void TabelaPracownicyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabelaPracownicyMousePressed
           int row = TabelaPracownicy.getSelectedRow();
         String tmp = TabelaPracownicy.getValueAt(row, 0).toString();
         //selectedPesel = TabelaPracownicy.getValueAt(row, 3).toString();
        IDfieldPracownicy.setText(tmp);
        edycjaButtonPracownicy.setEnabled(true);
        usunPracownika.setEnabled(true);
    }//GEN-LAST:event_TabelaPracownicyMousePressed

    private void pokazPracownikowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazPracownikowActionPerformed
       String a="%",be="%",c="%",d="%";
       String tmp=filtr_ID22.getText();
       a+=tmp.replaceFirst("^0+(?!$)", "")+"%";
       be+=filtr_ID23.getText()+"%";
       c+=filtr_ID24.getText()+"%";
       d+=filtr_ID25.getText()+"%";
       SelectPracownicyToTable(b.selectPracownicy(a,be,c,d));
       IDfieldPracownicy.setText("ID");
       edycjaButtonPracownicy.setEnabled(false);
       usunPracownika.setEnabled(false);
    }//GEN-LAST:event_pokazPracownikowActionPerformed

    private void usunPracownikaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usunPracownikaActionPerformed
        
        int row = TabelaPracownicy.getSelectedRow();
        if (TabelaPracownicy.getValueAt(row, 0).toString().equals("000001")) 
        {
            System.out.println("nie mozna usunac admina");
            JOptionPane.showMessageDialog(this, "Nie można usunąc pracownika 'admin'", "nie mozna usunąć", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
        String tmp = TabelaPracownicy.getValueAt(row, 0).toString()+" "+TabelaPracownicy.getValueAt(row, 1).toString()+" "+TabelaPracownicy.getValueAt(row, 2).toString();
         
        int potwierdzenie=JOptionPane.showConfirmDialog(this, "Usunąć: "+tmp+" ?", "Usunąć pracownika?", JOptionPane.OK_CANCEL_OPTION);
        if (potwierdzenie==0)
        {
            b.DeletePracownikId(IDfieldPracownicy.getText());
            pokazPracownikow.doClick();
            komunikaty.setText("USUNIETO Pracownika");
        }
        }
    }//GEN-LAST:event_usunPracownikaActionPerformed

    private void filtr_ID22FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filtr_ID22FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID22FocusGained

    private void filtr_ID22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID22ActionPerformed

    private void filtr_ID23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID23ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        oknoDodajPracownika.setLocation(dim.width/2-(oknoDodajPracownika.getSize().width)/2, dim.height/2-oknoDodajPracownika.getSize().height/2);
        nazwiskoPracownika.setText("");
        imiePracownika.setText("");
        loginPracownika.setText("");
        hasloPracownika.setText("");
        oknoDodajPracownika.setVisible(true);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void edycjaButtonPracownicyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edycjaButtonPracownicyActionPerformed
        int row = TabelaPracownicy.getSelectedRow();
        IDprac.setText(TabelaPracownicy.getValueAt(row, 0).toString());
        nazwiskoPracownika1.setText(TabelaPracownicy.getValueAt(row, 1).toString());
        imiePracownika1.setText(TabelaPracownicy.getValueAt(row, 2).toString());
        loginPracownika1.setText(TabelaPracownicy.getValueAt(row, 3).toString());
        hasloPracownika1.setText("");
        oknoEdycjaPracownika.setLocation(dim.width/2-(oknoEdycjaPracownika.getSize().width)/2, dim.height/2-oknoEdycjaPracownika.getSize().height/2);
        oknoEdycjaPracownika.setVisible(true);
    }//GEN-LAST:event_edycjaButtonPracownicyActionPerformed

    private void filtr_ID24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtr_ID24ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtr_ID24ActionPerformed

    private void czyscFiltrPracownicyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_czyscFiltrPracownicyActionPerformed
        filtr_ID22.setText("");filtr_ID23.setText("");filtr_ID24.setText("");filtr_ID25.setText("");
    }//GEN-LAST:event_czyscFiltrPracownicyActionPerformed

    private void dodajPracownikaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dodajPracownikaButtonActionPerformed
        String nazwisko=nazwiskoPracownika.getText();
        String imie=imiePracownika.getText();
        String login=loginPracownika.getText();
        String haslo=hasloPracownika.getText();
        boolean hasUppercase = !haslo.equals(haslo.toLowerCase());
        boolean hasLowercase = !haslo.equals(haslo.toUpperCase());
        boolean isAtLeast8   = haslo.length() >= 8;//Checks for at least 8 characters
        if(!hasUppercase || !hasLowercase || !isAtLeast8) 
        {
            System.out.println("haslo nie spelnia wymagan");
                hasloPracownika.requestFocus();
                hasloPracownika.selectAll();
        }
        else
                
        if (!nazwisko.isEmpty() && !imie.isEmpty() && !login.isEmpty() && !haslo.isEmpty())  {
            if (b.selectCountUniwersal(login, "pracownicy", "username")!=0) 
                {
                System.out.println("taki login juz jest w bazie");
                loginPracownika.requestFocus();
                loginPracownika.selectAll();
                }
            else    {
                        String hasloHash=hashPassword(haslo);
                        b.insertPracownik(nazwisko, imie, login, hasloHash);
                        oknoDodajPracownika.setVisible(false);
                        pokazPracownikow.doClick();
                    }
        }
        else komunikaty.setText("blędne dane");
    }//GEN-LAST:event_dodajPracownikaButtonActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        oknoDodajPracownika.dispose();
        komunikaty.setText("");
    }//GEN-LAST:event_jButton22ActionPerformed

    private void oknoDodajPracownikaWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknoDodajPracownikaWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_oknoDodajPracownikaWindowActivated

    private void zapiszPracownikaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zapiszPracownikaButtonActionPerformed
        String newPassword = hasloPracownika1.getText();
        String nazwisko=nazwiskoPracownika1.getText();
        String imie=imiePracownika1.getText();
        String login=loginPracownika1.getText();
        String tmp = IDprac.getText().replaceFirst("^0+(?!$)", "");
        
        
        if (login.isEmpty()) 
        {
            komunikaty.setText("pusty login");
            loginPracownika1.requestFocus();
            loginPracownika1.selectAll();
        }
        else if (!b.selectSzukanaWhereWarunek("username", "pracownicy", "id_pracownika", tmp).equals(login) && b.selectCountUniwersal(login, "pracownicy", "username") != 0)
            {
                    komunikaty.setText("taki login juz jest w bazie");
                    loginPracownika1.requestFocus();
                    loginPracownika1.selectAll();
            }
        else if (newPassword.length() < 1)
                {
                b.updatePracownik(Integer.parseInt(IDprac.getText()), imie, nazwisko, login);
                System.out.println("update pracownik bez zmiany hasla");
                    oknoEdycjaPracownika.setVisible(false);
                    pokazPracownikow.doClick();
                }
        else 
        {
            boolean hasUppercase = !newPassword.equals(newPassword.toLowerCase());
            boolean hasLowercase = !newPassword.equals(newPassword.toUpperCase());
            boolean isAtLeast8 = newPassword.length() >= 8;//Checks for at least 8 characters
            if (!hasUppercase || !hasLowercase || !isAtLeast8) 
            {
                    komunikaty.setText("haslo nie spelnia wymagan");
                    hasloPracownika1.requestFocus();
                    hasloPracownika1.selectAll();
            }
            else 
            {
                    String hasloHash = hashPassword(newPassword);
                    b.updatePracownik(Integer.parseInt(IDprac.getText()), imie, nazwisko, login, hasloHash);
                    System.out.println("update pracownik ze zmiana hasla");
                    oknoEdycjaPracownika.setVisible(false);
                    pokazPracownikow.doClick();
            }
        }

   
    }//GEN-LAST:event_zapiszPracownikaButtonActionPerformed

    private void edycjaPracownikaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edycjaPracownikaButtonActionPerformed
        oknoEdycjaPracownika.dispose();
        komunikaty.setText("");
    }//GEN-LAST:event_edycjaPracownikaButtonActionPerformed

    private void oknoEdycjaPracownikaWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknoEdycjaPracownikaWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_oknoEdycjaPracownikaWindowActivated

    private void zalogowanyUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zalogowanyUserMouseClicked
        zmianaHasla.setLocation(dim.width/2-(zmianaHasla.getSize().width)/2, dim.height/2-zmianaHasla.getSize().height/2);
        userZmiana.setText(zalogowanyUser.getText());
        pass1.setText("");pass2.setText("");pass3.setText("");komunikat.setText("");
        System.out.println("kliknales");
        zmianaHasla.setVisible(true);
    }//GEN-LAST:event_zalogowanyUserMouseClicked

    private void zaloguj1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zaloguj1ActionPerformed
        if (pass1.getText().equals(pass3.getText()) && pass1.getText().length()>7)
        {
            String pass=b.selectSzukanaWhereWarunek("password", "pracownicy", "username", userZmiana.getText());
            
            boolean czyPasuje = checkPassword(pass2.getText(),pass);
            
            if (czyPasuje==true) 
            {
                b.updatePracownik(userZmiana.getText(), hashPassword(pass1.getText()));
                komunikaty.setText("zmieniono haslo pracownika");
                zmianaHasla.setVisible(false); 
            }
            else 
            {
                pass2.requestFocus();
                pass2.selectAll();
                komunikat.setText("błędne obecne hasło");
            }
            
        }
        else
        {
            if (pass3.getText().length()<8) {pass3.requestFocus();pass3.selectAll();komunikat.setText("za krótkie hasło (min. 8) ");}
            else {pass1.requestFocus();pass1.selectAll();komunikat.setText("'powtórz hasło' nie pasuje");};
        }
    }//GEN-LAST:event_zaloguj1ActionPerformed

    private void pass1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pass1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_pass1FocusGained

    private void pass1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pass1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_pass1FocusLost

    private void pass2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pass2FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_pass2FocusGained

    private void pass2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pass2FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_pass2FocusLost

    private void pass2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pass2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pass2ActionPerformed

    private void pass3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pass3FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_pass3FocusGained

    private void pass3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pass3FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_pass3FocusLost

    private void pass3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pass3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pass3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        zmianaHasla.dispose();
        komunikaty.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void userKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            zaloguj.doClick();
        }
    }//GEN-LAST:event_userKeyPressed

    private void passKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            zaloguj.doClick();
        }
    }//GEN-LAST:event_passKeyPressed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws FileNotFoundException {
        
             
        jezyk();
        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
           // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
          //  UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BibliotekaApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BibliotekaApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BibliotekaApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(BibliotekaApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BibliotekaApp().setVisible(true); 
                
            }
        });
        
        
        
      

        //SwingUtilities.updateComponentTreeUI(userDetails);
        
//        Thread watek = new Thread(new Runnable() {
//             int i=0;
//             //DateTime dt= new DateTime();
//             ScheduledExecutorService exec=Executors.newScheduledThreadPool(1);
//             Runnable cos = new Runnable() {
//                 public void run() {
//                     System.out.println("watek 2: "+i);
//                        i++;
//                        System.out.println(System.currentTimeMillis());
//                        //czyscKomunikaty();
//                 }
//             };
//         public void run() {
//             exec.scheduleAtFixedRate(cos, 0, 5, TimeUnit.SECONDS);
//             
//            }
//        });
//        watek.start();

       // b.closeConnection(); 
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ID_book;
    private javax.swing.JTextField ID_books_field;
    private javax.swing.JTextField ID_egz_field;
    private javax.swing.JLabel ID_egzemplarz;
    private javax.swing.JTextField ID_egzemplarza_field;
    private javax.swing.JLabel ID_user;
    private javax.swing.JTextField ID_wyp_field;
    private javax.swing.JTextField IDfield;
    private javax.swing.JTextField IDfieldPracownicy;
    private javax.swing.JTextField IDprac;
    private javax.swing.JDialog LogowanieFrame;
    private javax.swing.JTable TabelaBooks;
    private javax.swing.JTable TabelaCzytelnicy;
    private javax.swing.JTable TabelaEgzemplarze;
    private javax.swing.JTable TabelaPracownicy;
    private javax.swing.JTable TabelaWypozyczenia;
    private javax.swing.JPanel ZakladkaCzytelnicy;
    private javax.swing.JPanel ZakladkaEgzemplarze;
    private javax.swing.JPanel ZakladkaKsiazki;
    private javax.swing.JPanel ZakladkaPracownicy;
    private javax.swing.JPanel ZakladkaWypozyczenia;
    private javax.swing.JTextField autor;
    private javax.swing.JTextField autor2;
    private javax.swing.JTextField autorNew;
    private javax.swing.JTextField autorNew1;
    private javax.swing.JFrame booksDetails;
    private javax.swing.JButton books_close;
    private javax.swing.JButton books_close2;
    private javax.swing.JButton books_details;
    private javax.swing.JButton books_details1;
    private javax.swing.JButton books_edycja1;
    private javax.swing.JButton czyscFiltrPracownicy;
    private javax.swing.JButton czysc_filtr_wyp;
    private javax.swing.JTextField czyt_add_DOB;
    private javax.swing.JTextField czyt_add_email;
    private javax.swing.JTextField czyt_add_imie;
    private javax.swing.JTextField czyt_add_miasto;
    private javax.swing.JTextField czyt_add_nazwisko;
    private javax.swing.JTextField czyt_add_nr;
    private javax.swing.JTextField czyt_add_password;
    private javax.swing.JTextField czyt_add_pesel;
    private javax.swing.JTextField czyt_add_telefon;
    private javax.swing.JTextField czyt_add_ulica;
    private javax.swing.JTextField czyt_add_username;
    private javax.swing.JFrame dodajAutora;
    private javax.swing.JButton dodajButton;
    private javax.swing.JButton dodajButtonEgz;
    private javax.swing.JButton dodajButtonNew;
    private javax.swing.JButton dodajEgzButton;
    private javax.swing.JButton dodajEgzButton1;
    private javax.swing.JFrame dodajMiasto;
    private javax.swing.JButton dodajPracownikaButton;
    private javax.swing.JTextField dzial;
    private javax.swing.JTextField dzialNew;
    private javax.swing.JButton edycjaButton;
    private javax.swing.JButton edycjaButtonPracownicy;
    private javax.swing.JButton edycjaPracownikaButton;
    private javax.swing.JButton egz_edycja;
    private javax.swing.JFrame egzemplarzEdit;
    private javax.swing.JButton egzemplarz_edit;
    private javax.swing.JTextField filtr_ID;
    private javax.swing.JTextField filtr_ID1;
    private javax.swing.JTextField filtr_ID10;
    private javax.swing.JTextField filtr_ID11;
    private javax.swing.JTextField filtr_ID12;
    private javax.swing.JTextField filtr_ID13;
    private javax.swing.JTextField filtr_ID14;
    private javax.swing.JTextField filtr_ID15;
    private javax.swing.JTextField filtr_ID16;
    private javax.swing.JTextField filtr_ID17;
    private javax.swing.JTextField filtr_ID18;
    private javax.swing.JTextField filtr_ID19;
    private javax.swing.JTextField filtr_ID2;
    private javax.swing.JTextField filtr_ID20;
    private javax.swing.JTextField filtr_ID21;
    private javax.swing.JTextField filtr_ID22;
    private javax.swing.JTextField filtr_ID23;
    private javax.swing.JTextField filtr_ID24;
    private javax.swing.JTextField filtr_ID25;
    private javax.swing.JTextField filtr_ID3;
    private javax.swing.JTextField filtr_ID4;
    private javax.swing.JTextField filtr_ID5;
    private javax.swing.JTextField filtr_ID6;
    private javax.swing.JTextField filtr_ID7;
    private javax.swing.JTextField filtr_ID8;
    private javax.swing.JTextField filtr_ID9;
    private javax.swing.JTextField gatunek;
    private javax.swing.JTextField gatunekNew;
    private javax.swing.JTextField hasloPracownika;
    private javax.swing.JTextField hasloPracownika1;
    private javax.swing.JTextField imieAutorAdd;
    private javax.swing.JTextField imiePracownika;
    private javax.swing.JTextField imiePracownika1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jezyk;
    private javax.swing.JTextField jezykField;
    private javax.swing.JTextField kategoria;
    private javax.swing.JTextField kategoriaNew;
    private javax.swing.JTextField kodADD;
    private javax.swing.JLabel komunikat;
    private javax.swing.JLabel komunikatCzyt;
    private javax.swing.JLabel komunikatL;
    private javax.swing.JLabel komunikaty;
    private javax.swing.JComboBox<String> listAutorNew;
    private javax.swing.JComboBox<String> listDzial;
    private javax.swing.JComboBox<String> listDzialNew;
    private javax.swing.JComboBox<String> listGatunek;
    private javax.swing.JComboBox<String> listGatunekNew;
    private javax.swing.JComboBox<String> listKategoria;
    private javax.swing.JComboBox<String> listKategoriaNew;
    private javax.swing.JComboBox<String> listLOK;
    private javax.swing.JComboBox<String> listLokalizacja;
    private javax.swing.JComboBox<String> listSTA;
    private javax.swing.JComboBox<String> listStan;
    private javax.swing.JComboBox<String> listWYD;
    private javax.swing.JComboBox<String> listWydawnictwo;
    private javax.swing.JComboBox<String> lista_miasta;
    private javax.swing.JComboBox<String> lista_ulice;
    private javax.swing.JFrame loading;
    private javax.swing.JTextField loginPracownika;
    private javax.swing.JTextField loginPracownika1;
    private javax.swing.JTextField lokalizacja;
    private javax.swing.JTextField lokalizacjaField;
    private javax.swing.JTextField miastoADD;
    private javax.swing.JTextField nazwiskoAutorAdd;
    private javax.swing.JTextField nazwiskoPracownika;
    private javax.swing.JTextField nazwiskoPracownika1;
    private javax.swing.JTextField newIDEgz;
    private javax.swing.JFrame oknoDodajBook;
    private javax.swing.JFrame oknoDodajEgzemplarz;
    private javax.swing.JFrame oknoDodajPracownika;
    private javax.swing.JFrame oknoEdycjaPracownika;
    private javax.swing.JFrame oknoProlongata;
    private javax.swing.JFrame oknoWypozycz;
    private javax.swing.JFrame oknoZwrot;
    private javax.swing.JFrame oknotest;
    private javax.swing.JTextArea opis;
    private javax.swing.JTextArea opisNew;
    private javax.swing.JPasswordField pass;
    private javax.swing.JPasswordField pass1;
    private javax.swing.JPasswordField pass2;
    private javax.swing.JPasswordField pass3;
    private javax.swing.JButton pokazCzytelnikow;
    private javax.swing.JButton pokazCzytelnikow1;
    private javax.swing.JButton pokazCzytelnikow2;
    private javax.swing.JButton pokazCzytelnikow3;
    private javax.swing.JButton pokazEgzemplarze;
    private javax.swing.JButton pokazKsiazki;
    private javax.swing.JButton pokazKsiazki1;
    private javax.swing.JButton pokazPracownikow;
    private javax.swing.JButton pokazWypozyczenia;
    private javax.swing.JTextField prolongataEgzemplarz;
    private javax.swing.JButton prolongujButton;
    private javax.swing.JTextField rok;
    private javax.swing.JTextField rokField;
    private javax.swing.JTextField stan;
    private javax.swing.JTextField stanField;
    private javax.swing.JTextField title;
    private javax.swing.JTextField title2;
    private javax.swing.JTextField tittleNew;
    private javax.swing.JTextField tittleNew1;
    private javax.swing.JLabel tytul;
    private javax.swing.JLabel tytul1;
    private javax.swing.JLabel tytul2;
    private javax.swing.JLabel tytul3;
    private javax.swing.JLabel tytul4;
    private javax.swing.JLabel tytul5;
    private javax.swing.JLabel tytul6;
    private javax.swing.JTextField user;
    private javax.swing.JFrame userDetails;
    private javax.swing.JLabel userZmiana;
    private javax.swing.JTextField user_DOB;
    private javax.swing.JTextField user_city;
    private javax.swing.JButton user_close;
    private javax.swing.JTextField user_debt;
    private javax.swing.JButton user_edycja;
    private javax.swing.JTextField user_email;
    private javax.swing.JTextField user_name;
    private javax.swing.JTextField user_nr;
    private javax.swing.JTextField user_pesel;
    private javax.swing.JTextField user_phone;
    private javax.swing.JTextField user_street;
    private javax.swing.JTextField user_surname;
    private javax.swing.JTextField user_username;
    private javax.swing.JButton usunCzyt;
    private javax.swing.JButton usunEgzemplarz;
    private javax.swing.JButton usunKsiazke;
    private javax.swing.JButton usunPracownika;
    private javax.swing.JTextField wydawnictwo;
    private javax.swing.JTextField wydawnictwoField;
    private javax.swing.JButton wypozyczButton;
    private javax.swing.JTextField wypozyczCzytelnik;
    private javax.swing.JTextField wypozyczEgzemplarz;
    private javax.swing.JTabbedPane zakladki;
    private javax.swing.JTextField zalogowanyUser;
    private javax.swing.JButton zaloguj;
    private javax.swing.JButton zaloguj1;
    private javax.swing.JButton zapiszPracownikaButton;
    private javax.swing.JDialog zmianaHasla;
    private javax.swing.JButton zwrotButton;
    private javax.swing.JTextField zwrotEgzemplarz;
    // End of variables declaration//GEN-END:variables
}
