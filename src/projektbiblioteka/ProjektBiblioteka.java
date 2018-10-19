package projektbiblioteka;

import model.Czytelnik;
import model.Ksiazka;
import Biblioteka.Biblioteka;
import java.util.List;

public class ProjektBiblioteka {

    public static void main(String[] args) {
        Biblioteka b = new Biblioteka();
        b.insertCzytelnik("Karolina", "Maciaszek", "92873847182");
        b.insertCzytelnik("Piotr", "Wojtecki", "89273849128");
        b.insertCzytelnik("Abdul", "Dabdul", "pesel");

        b.insertKsiazka("Cień Wiatru", "Carlos Ruiz Zafon", "Horror");
        b.insertKsiazka("W pustyni i w puszczy", "Henryk Sienkiewicz", "Przygoda");
        b.insertKsiazka("Harry Potter", "Joanne Kathleen Rowling.", "Przygoda");

        List<Czytelnik> czytelnicy = b.selectCzytelnicy();
        List<Ksiazka> ksiazki = b.selectKsiazki();

        System.out.println("Lista czytelników: ");
        for(Czytelnik c: czytelnicy)
            System.out.println(c);

        System.out.println("Lista książek:");
        for(Ksiazka k: ksiazki)
            System.out.println(k);

        b.closeConnection();
    }
}