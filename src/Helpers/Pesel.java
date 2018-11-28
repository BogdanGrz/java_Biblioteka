package Helpers;

import static java.lang.Integer.parseInt;
import java.util.Random;
import java.util.regex.Pattern;

/**
 *
 * @author B.Grządzielewski
 */
public class Pesel {

    String numer = "82090918757";

    static public String dataUrodzenia(String numer) {
        if (sprawdzPesel(numer) == false) {
            return "bledny pesel";
        }
        int miesiac;
        String rok = "";
        miesiac = parseInt(numer.substring(2, 4));
        if (miesiac < 13) {
            rok = "19";
        } else if (miesiac > 20 && miesiac < 33) {
            rok = "20";
            miesiac -= 20;
        } else if (miesiac > 80 && miesiac < 93) {
            rok = "18";
            miesiac -= 80;
        } else if (miesiac > 40 && miesiac < 53) {
            rok = "21";
            miesiac -= 40;
        } else if (miesiac > 60 && miesiac < 73) {
            rok = "22";
            miesiac -= 60;
        }
        String miesiacStr = String.format("%02d", miesiac);
        String dataUr = rok + numer.substring(0, 2) + "-" + miesiacStr + "-" + numer.substring(4, 6);
        return dataUr;
    }

    public static boolean sprawdzPesel(String numer) {
        if (Pattern.matches("^[0-9]{11}$", numer) == false) {
            System.out.println("dane niezgodne z formatem");
            return false;
        }
        int miesiac = parseInt(numer.substring(2, 4));
        int dzien = parseInt(numer.substring(4, 6));
        if (miesiac > 12 && miesiac < 21 || miesiac > 32 && miesiac < 41 || miesiac > 52 && miesiac < 61 || miesiac > 72 && miesiac < 81 || miesiac > 92 || miesiac == 0) {
            return false;
        }
        int ile_dni = 0;
        int mie_1 = parseInt(numer.substring(2, 3));
        int mie_2 = parseInt(numer.substring(3, 4));
        if (mie_1 % 2 == 0) {
            if (mie_2 == 2) {
                ile_dni = 29;
            } else if (mie_2 == 1 || mie_2 == 3 || mie_2 == 5 || mie_2 == 7 || mie_2 == 8) {
                ile_dni = 31;
            } else {
                ile_dni = 30;
            }
        } else {
            if (mie_2 == 0 || mie_2 == 2) {
                ile_dni = 31;
            } else {
                ile_dni = 30;
            }
        }
        if (dzien > ile_dni || dzien < 1) {
            return false;
        }
        return true;
    }

    static String generujPesel(int rok_od, int rok_do) {
        if (rok_od < 1800 || rok_do > 2299 || rok_do < rok_od) {
            return "bledne dane wejsciowe";
        }
        String pesel = "";
        Random generator = new Random();
        int ile = rok_do - rok_od;
        int rok = generator.nextInt(ile + 1) + rok_od;
        int miesiac = generator.nextInt(12) + 1;
        if (rok < 1900) {
            miesiac += 80;
        } else if (rok > 1999 && rok < 2100) {
            miesiac += 20;
        } else if (rok > 2099 && rok < 2200) {
            miesiac += 40;
        } else if (rok > 2199) {
            miesiac += 60;
        }

        String rokStr = Integer.toString(rok);
        String rok2 = rokStr.substring(2, 4);
        String miesiacStr = String.format("%02d", miesiac);
        int ile_dni = 0;
        int mie_1 = parseInt(miesiacStr.substring(0, 1));
        int mie_2 = parseInt(miesiacStr.substring(1, 2));
        if (mie_1 % 2 == 0) {
            if (mie_2 == 2) {
                ile_dni = 29;
            } else if (mie_2 == 1 || mie_2 == 3 || mie_2 == 5 || mie_2 == 7 || mie_2 == 8) {
                ile_dni = 31;
            } else {
                ile_dni = 30;
            }
        } else {
            if (mie_2 == 0 || mie_2 == 2) {
                ile_dni = 31;
            } else {
                ile_dni = 30;
            }
        }

        String dzienStr = String.format("%02d", generator.nextInt(ile_dni) + 1);
        String lastStr = String.format("%05d", generator.nextInt(99999) + 1);
        pesel = rok2 + miesiacStr + dzienStr + lastStr;
        return pesel;
    }
}
