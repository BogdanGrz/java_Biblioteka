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
import static Helpers.Pesel.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import model.Autor;
import model.Dzial;
import model.Gatunek;
import model.Kategoria;
import model.Lokalizacja;
import model.Stan;
import model.Wydawnictwo;

public class BibliotekaApp extends javax.swing.JFrame {
    JFrame loading2 = new javax.swing.JFrame();
    ProgressBar myProgressBar = new ProgressBar();
    
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    
    String selectedPesel="BRAK";
    
    
    File plik = new File("lang.txt");
    DatabaseAPI b = new DatabaseAPI();
    static String [] language = new String[] {"polska", "Córdoba", "La Plata"}; 
    
    DefaultTableModel model = new DefaultTableModel(new Object[][] {},
     new Object[] { language[0], "Imie","Nazwisko", "Pesel", "DOB", "Uźytkownik", "email", "Adres", "Telefon"});
    
    DefaultTableModel books = new DefaultTableModel(new Object[][] {},
     new Object[] { "ID", "Tytuł", "Autor", "Dział", "Gatunek", "Kategoria"});

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
        System.out.println(language[0]);
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
      
//        public void listaStany() {
//        
//        List<Stan> stany;
//         stany=b.selectStany();
//         modelboxstany.removeAllElements();
//         for (int i=0; i<stany.size();i++){
//         modelboxstany.addElement(stany.get(i).getStan());
//         }
//         modelboxstany.setSelectedItem(null);
//    }
        
//       public void listaWydawnictwa() {
//        
//        List<Wydawnictwo> wydawnictwa;
//         wydawnictwa=b.selectWydawnictwa();
//         modelboxwydawnictwa.removeAllElements();
//         for (int i=0; i<wydawnictwa.size();i++){
//         modelboxwydawnictwa.addElement(wydawnictwa.get(i).getWydawnictwo());
//         }
//         modelboxwydawnictwa.setSelectedItem(null);
//    }
     
     
         
         
     
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
    
    
    public BibliotekaApp() {
        initComponents();
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
        dodajButtonNew1 = new javax.swing.JButton();
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
        dzialNew1 = new javax.swing.JTextField();
        gatunekNew1 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        kategoriaNew1 = new javax.swing.JTextField();
        listWydawnictwo = new javax.swing.JComboBox<>(modelboxwydawnictwa);
        jLabel54 = new javax.swing.JLabel();
        autorNew2 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        autorNew3 = new javax.swing.JTextField();
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
        ZakladkaKsiazki = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelaBooks = new javax.swing.JTable(books);
        pokazKsiazki = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        dodajEgzButton = new javax.swing.JButton();
        books_details = new javax.swing.JButton();
        ID_books_field = new javax.swing.JTextField();
        usunKsiazke = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                        .addComponent(dodajButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
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

        dodajButtonNew1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dodajButtonNew1.setText("Dodaj");
        dodajButtonNew1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dodajButtonNew1ActionPerformed(evt);
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

        dzialNew1.setEditable(false);
        dzialNew1.setBackground(new java.awt.Color(255, 255, 204));
        dzialNew1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dzialNew1.setText("wybierz z listy");
        dzialNew1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        dzialNew1.setName(""); // NOI18N
        dzialNew1.setSelectionColor(new java.awt.Color(255, 102, 102));

        gatunekNew1.setEditable(false);
        gatunekNew1.setBackground(new java.awt.Color(255, 255, 204));
        gatunekNew1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        gatunekNew1.setText("wybierz z listy");
        gatunekNew1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        gatunekNew1.setSelectionColor(new java.awt.Color(255, 102, 102));

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel53.setText("Wydawnictwo:");

        kategoriaNew1.setEditable(false);
        kategoriaNew1.setBackground(new java.awt.Color(255, 255, 204));
        kategoriaNew1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        kategoriaNew1.setText("wybierz z listy");
        kategoriaNew1.setMargin(new java.awt.Insets(2, 4, 2, 2));
        kategoriaNew1.setSelectionColor(new java.awt.Color(255, 102, 102));

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

        autorNew2.setBackground(new java.awt.Color(255, 255, 204));
        autorNew2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        autorNew2.setMargin(new java.awt.Insets(2, 4, 2, 2));
        autorNew2.setSelectionColor(new java.awt.Color(255, 102, 102));
        autorNew2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autorNew2ActionPerformed(evt);
            }
        });

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel55.setText("Język:");

        autorNew3.setBackground(new java.awt.Color(255, 255, 204));
        autorNew3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        autorNew3.setMargin(new java.awt.Insets(2, 4, 2, 2));
        autorNew3.setSelectionColor(new java.awt.Color(255, 102, 102));
        autorNew3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autorNew3ActionPerformed(evt);
            }
        });

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
                                .addComponent(dzialNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addComponent(gatunekNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                            .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(dodajButtonNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton16)
                                .addGap(13, 13, 13))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(autorNew2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(kategoriaNew1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(autorNew3, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(listWydawnictwo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(51, Short.MAX_VALUE))
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
                    .addComponent(dzialNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(listLokalizacja, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listStan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gatunekNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listWydawnictwo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kategoriaNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autorNew2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autorNew3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addGap(71, 71, 71)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dodajButtonNew1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Biblioteka");
        setBackground(new java.awt.Color(204, 255, 204));
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

        pokazCzytelnikow.setText("Pokaz Czytelników");
        pokazCzytelnikow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pokazCzytelnikowActionPerformed(evt);
            }
        });

        IDfield.setText("ID");

        usunCzyt.setForeground(new java.awt.Color(255, 102, 102));
        usunCzyt.setText("Usuń Czytelnika");
        usunCzyt.setEnabled(false);
        usunCzyt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usunCzytActionPerformed(evt);
            }
        });

        filtr_ID.setText("ID czyt");
        filtr_ID.setAlignmentX(0.0F);
        filtr_ID.setAlignmentY(0.0F);
        filtr_ID.setAutoscrolls(false);
        filtr_ID.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID.setName(""); // NOI18N
        filtr_ID.setPreferredSize(new java.awt.Dimension(70, 30));
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

        filtr_ID1.setText("imie");
        filtr_ID1.setAlignmentX(0.0F);
        filtr_ID1.setAlignmentY(0.0F);
        filtr_ID1.setAutoscrolls(false);
        filtr_ID1.setMargin(new java.awt.Insets(2, 0, 0, 0));
        filtr_ID1.setMaximumSize(new java.awt.Dimension(70, 25));
        filtr_ID1.setMinimumSize(new java.awt.Dimension(70, 25));
        filtr_ID1.setName(""); // NOI18N
        filtr_ID1.setPreferredSize(new java.awt.Dimension(70, 30));

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

        javax.swing.GroupLayout ZakladkaCzytelnicyLayout = new javax.swing.GroupLayout(ZakladkaCzytelnicy);
        ZakladkaCzytelnicy.setLayout(ZakladkaCzytelnicyLayout);
        ZakladkaCzytelnicyLayout.setHorizontalGroup(
            ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaCzytelnicyLayout.createSequentialGroup()
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                        .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                                .addGap(55, 55, 55)
                                .addComponent(pokazCzytelnikow)
                                .addGap(200, 200, 200)
                                .addComponent(IDfield, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(usunCzyt))
                            .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                                .addComponent(filtr_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(filtr_ID1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaCzytelnicyLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edycjaButton)
                .addGap(254, 254, 254))
            .addGroup(ZakladkaCzytelnicyLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1009, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ZakladkaCzytelnicyLayout.setVerticalGroup(
            ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaCzytelnicyLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edycjaButton, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filtr_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtr_ID1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ZakladkaCzytelnicyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pokazCzytelnikow)
                    .addComponent(IDfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usunCzyt))
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

        ID_books_field.setText("ID");

        usunKsiazke.setForeground(new java.awt.Color(255, 102, 102));
        usunKsiazke.setText("Usuń książkę");
        usunKsiazke.setEnabled(false);
        usunKsiazke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usunKsiazkeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ZakladkaKsiazkiLayout = new javax.swing.GroupLayout(ZakladkaKsiazki);
        ZakladkaKsiazki.setLayout(ZakladkaKsiazkiLayout);
        ZakladkaKsiazkiLayout.setHorizontalGroup(
            ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1009, Short.MAX_VALUE)
            .addGroup(ZakladkaKsiazkiLayout.createSequentialGroup()
                .addGroup(ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ZakladkaKsiazkiLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(pokazKsiazki)
                        .addGap(200, 200, 200)
                        .addComponent(ID_books_field, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usunKsiazke))
                    .addGroup(ZakladkaKsiazkiLayout.createSequentialGroup()
                        .addGap(410, 410, 410)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dodajEgzButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(books_details, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ZakladkaKsiazkiLayout.setVerticalGroup(
            ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ZakladkaKsiazkiLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                    .addComponent(dodajEgzButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(books_details, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ZakladkaKsiazkiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pokazKsiazki)
                    .addComponent(ID_books_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usunKsiazke))
                .addGap(26, 26, 26))
        );

        zakladki.addTab("Książki", ZakladkaKsiazki);

        jLabel1.setText("Użytkownik: ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(100, 100, 100))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(zakladki, javax.swing.GroupLayout.PREFERRED_SIZE, 1018, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(zakladki, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        zakladki.getAccessibleContext().setAccessibleName("Czytelnicy");

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

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(23, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void pokazCzytelnikowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazCzytelnikowActionPerformed
       
       SelectCzytelnicyToTable(b.selectCzytelnicyZAdresem());
       
    }//GEN-LAST:event_pokazCzytelnikowActionPerformed

    private void TabelaCzytelnicyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TabelaCzytelnicyKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("WYWOLANIE");
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
        System.out.println("USUNIETO CZYTELNIKA");
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
        System.out.println("wczytal liste");
        System.out.println(lista.get(0));
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
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        String miasto=miastoADD.getText();
        String kod=kodADD.getText();
        if (Pattern.matches("^[0-9]{2}-[0-9]{3}$", kod)&& !miasto.isEmpty())  {
            if (b.selectCountMiasto(kod)!=0) {
                System.out.println("taki kod pocztowy juz jest w bazie");
                
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
            System.out.println("bledne dane");
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
            System.out.println("pomyslna edycja czytelnika "+user_pesel.getText());
        }
    }//GEN-LAST:event_user_edycjaActionPerformed

    private void pokazKsiazkiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pokazKsiazkiActionPerformed
        SelectBooksToTable(b.selectBooksToTable());
        
    }//GEN-LAST:event_pokazKsiazkiActionPerformed

    private void user_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_closeActionPerformed
       userDetails.dispose();
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
        System.out.println("wczytal liste ksiazka po ID");
        System.out.println(lista.get(0));
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
            System.out.println("UZYSKANE ID DZIALU to : "+id_dzialu);
            System.out.println("UZYSKANE ID KATEGORII to : "+id_kategori);
            System.out.println("UZYSKANE ID GATUNKU to : "+id_gatunku);
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
        System.out.println("test");
        if (czyt_add_miasto.getText().equals(" Inne...")) {czyt_add_miasto.setEditable(false); czyt_add_miasto.setText("");
            System.out.println("miasto nie z listy");
            dodajMiasto.setLocation(dim.width/2-(dodajMiasto.getSize().width/2), dim.height/2-dodajMiasto.getSize().height/2);
            dodajMiasto.setVisible(true);}

        else czyt_add_ulica.setEditable(false);
    }//GEN-LAST:event_lista_miastaFocusLost

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        oknotest.dispose();
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
            System.out.println("bledne imie");
            czyt_add_imie.requestFocus();
            czyt_add_imie.selectAll();
        }
        else if (nazwisko.isEmpty() )
        {
            System.out.println("bledne nazwisko");
            czyt_add_nazwisko.requestFocus();
            czyt_add_nazwisko.selectAll();
        }
        else if (!sprawdzPesel(pesel))
        {
            System.out.println("bledny pesel");
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
            System.out.println("bledny email");
            czyt_add_email.requestFocus();
            czyt_add_email.selectAll();
        }
        else if (password.length()<8)
        {
            System.out.println("bledne haslo (min. 8 znakow)");
            czyt_add_password.requestFocus();
            czyt_add_password.selectAll();
        }
        else if (id_miasto.isEmpty())
        {
            System.out.println("puste miato");
            czyt_add_miasto.requestFocus();
            czyt_add_miasto.selectAll();
        }
        else if (id_ulica.isEmpty()) {
            System.out.println("pusta ulica"); }
        

        else    {
            if (username.isEmpty() )
            {   System.out.println("wstawiam domyslna nazwe uzytkownika (pesel)");
                username = pesel;
                czyt_add_username.setText(username);
            }

            int tmp=0;
            tmp = b.selectCountUniwersalny(pesel, "czytelnicy", "pesel");
            if (tmp!=0) System.out.println("taki PESEL juz istnieje");
            else { tmp=0;
                tmp = b.selectCountUniwersalny(username, "czytelnicy", "username");
                if (tmp!=0) System.out.println("taki USERNAME juz istnieje");
                else { tmp=0;
                    tmp = b.selectCountUniwersalny(email, "czytelnicy", "email");
                    if (tmp!=0) System.out.println("taki E-MAIL juz istnieje");
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
                System.out.println(lista1.size());
                boolean czyImie=false;
                for (int i=0; i<lista1.size(); i++)
                {
                    if (lista1.get(i).equals(imie)) czyImie=true;
                }
                if (czyImie==true)
                {
                    System.out.println("imie tez istnieje");
                    //nie dodawaj autora
                    JOptionPane.showMessageDialog(this, "Taki Autor juz istnieje:\n\n"+nazwisko+" "+imie, "Nie dodano autora", JOptionPane.INFORMATION_MESSAGE);
                    dodajAutora.setVisible(false);
                    autorNew.setText(nazwisko+" "+imie);
                    oknoDodajBook.toFront();
                }
                else {
                    System.out.println("nazwisko jest, ale z innym imieniem");
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

    private void dodajButtonNew1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dodajButtonNew1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dodajButtonNew1ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton16ActionPerformed

    private void autorNew1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorNew1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_autorNew1ActionPerformed

    private void listLokalizacjaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listLokalizacjaFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_listLokalizacjaFocusLost

    private void listLokalizacjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listLokalizacjaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listLokalizacjaActionPerformed

    private void listStanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listStanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listStanActionPerformed

    private void listWydawnictwoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listWydawnictwoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listWydawnictwoActionPerformed

    private void oknoDodajEgzemplarzWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_oknoDodajEgzemplarzWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_oknoDodajEgzemplarzWindowActivated

    private void autorNew2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorNew2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_autorNew2ActionPerformed

    private void autorNew3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorNew3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_autorNew3ActionPerformed

    private void dodajEgzButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dodajEgzButtonActionPerformed
        oknoDodajEgzemplarz.setLocation(dim.width/2-(oknoDodajEgzemplarz.getSize().width)/2, dim.height/2-oknoDodajEgzemplarz.getSize().height/2);
        listaLokalizacje();
//        listaStany();
//        listaWydawnictwa();
        
        dzialNew.setText("wybierz z listy");
        autorNew.setText("wybierz z listy");
        gatunekNew.setText("wybierz z listy");
        kategoriaNew.setText("wybierz z listy");
       oknoDodajBook.setVisible(true);
    }//GEN-LAST:event_dodajEgzButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws FileNotFoundException {

        jezyk();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BibliotekaApp().setVisible(true);   
            }
        });
        
//        Thread watek = new Thread(new Runnable() {
//             int i=0;
//             //DateTime dt= new DateTime();
//             ScheduledExecutorService exec=Executors.newScheduledThreadPool(1);
//             Runnable cos = new Runnable() {
//                 public void run() {
//                     System.out.println("watek 2: "+i);
//                        i++;
//                        System.out.println(System.currentTimeMillis());
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
    private javax.swing.JLabel ID_user;
    private javax.swing.JTextField IDfield;
    private javax.swing.JTable TabelaBooks;
    private javax.swing.JTable TabelaCzytelnicy;
    private javax.swing.JPanel ZakladkaCzytelnicy;
    private javax.swing.JPanel ZakladkaKsiazki;
    private javax.swing.JTextField autor;
    private javax.swing.JTextField autorNew;
    private javax.swing.JTextField autorNew1;
    private javax.swing.JTextField autorNew2;
    private javax.swing.JTextField autorNew3;
    private javax.swing.JFrame booksDetails;
    private javax.swing.JButton books_close;
    private javax.swing.JButton books_details;
    private javax.swing.JButton books_edycja1;
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
    private javax.swing.JButton dodajButtonNew;
    private javax.swing.JButton dodajButtonNew1;
    private javax.swing.JButton dodajEgzButton;
    private javax.swing.JFrame dodajMiasto;
    private javax.swing.JTextField dzial;
    private javax.swing.JTextField dzialNew;
    private javax.swing.JTextField dzialNew1;
    private javax.swing.JButton edycjaButton;
    private javax.swing.JTextField filtr_ID;
    private javax.swing.JTextField filtr_ID1;
    private javax.swing.JTextField gatunek;
    private javax.swing.JTextField gatunekNew;
    private javax.swing.JTextField gatunekNew1;
    private javax.swing.JTextField imieAutorAdd;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField kategoria;
    private javax.swing.JTextField kategoriaNew;
    private javax.swing.JTextField kategoriaNew1;
    private javax.swing.JTextField kodADD;
    private javax.swing.JComboBox<String> listAutorNew;
    private javax.swing.JComboBox<String> listDzial;
    private javax.swing.JComboBox<String> listDzialNew;
    private javax.swing.JComboBox<String> listGatunek;
    private javax.swing.JComboBox<String> listGatunekNew;
    private javax.swing.JComboBox<String> listKategoria;
    private javax.swing.JComboBox<String> listKategoriaNew;
    private javax.swing.JComboBox<String> listLokalizacja;
    private javax.swing.JComboBox<String> listStan;
    private javax.swing.JComboBox<String> listWydawnictwo;
    private javax.swing.JComboBox<String> lista_miasta;
    private javax.swing.JComboBox<String> lista_ulice;
    private javax.swing.JFrame loading;
    private javax.swing.JTextField miastoADD;
    private javax.swing.JTextField nazwiskoAutorAdd;
    private javax.swing.JFrame oknoDodajBook;
    private javax.swing.JFrame oknoDodajEgzemplarz;
    private javax.swing.JFrame oknotest;
    private javax.swing.JTextArea opis;
    private javax.swing.JTextArea opisNew;
    private javax.swing.JButton pokazCzytelnikow;
    private javax.swing.JButton pokazKsiazki;
    private javax.swing.JTextField title;
    private javax.swing.JTextField tittleNew;
    private javax.swing.JTextField tittleNew1;
    private javax.swing.JLabel tytul;
    private javax.swing.JLabel tytul1;
    private javax.swing.JFrame userDetails;
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
    private javax.swing.JButton usunKsiazke;
    private javax.swing.JTabbedPane zakladki;
    // End of variables declaration//GEN-END:variables
}
