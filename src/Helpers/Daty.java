/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author PC
 */
public class Daty {
    
    public static long obliczWiek(String data) {
        DateTimeFormatter formatDaty = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        long numberOfYears = -1;
        try {
            LocalDate date = LocalDate.parse(data, formatDaty);
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = date;
            numberOfYears = ChronoUnit.YEARS.between(startDate, endDate);
            System.out.println(numberOfYears);
        } catch (DateTimeParseException exc) {
            System.out.printf("bledne dane wejsciowe");
        }
        return numberOfYears;
    }
    
    public static boolean czyPrzyszlosc(String data) {
        DateTimeFormatter formatDaty = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate date = LocalDate.parse(data, formatDaty);
            LocalDate now = LocalDate.now();
            LocalDate podanaData = date;
            if (podanaData.isAfter(now)) {System.out.println("data pozniejsza niz dzis"); return true;}
        } catch (DateTimeParseException exc) {
            System.out.printf("bledne dane wejsciowe");
        }
        return false;
}
    public static boolean czyPrzeszlosc(String data) {
        DateTimeFormatter formatDaty = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate date = LocalDate.parse(data, formatDaty);
            LocalDate now = LocalDate.now();
            LocalDate podanaData = date;
            if (podanaData.isBefore(now)) {System.out.println("po terminie"); return true;}
        } catch (DateTimeParseException exc) {
            System.out.printf("bledne dane wejsciowe");
        }
        return false;
}
    
    
    public static String dzis() {
         LocalDate localDate = LocalDate.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
         String dzis = localDate.format(formatter);
         return dzis;
    }
    
    public static String dzisPlus(int ile_do_przodu) {
         LocalDate localDate = LocalDate.now().plusDays(ile_do_przodu);
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
         String dzis = localDate.format(formatter);
         return dzis;
    }
    
    public static String miesiacePlusOdDaty(int ile_do_przodu, String od_kiedy) {
        DateTimeFormatter formatDaty = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String wynik="";
        try {
         LocalDate date = LocalDate.parse(od_kiedy, formatDaty);
         LocalDate plusMonths = date.plusMonths(ile_do_przodu);
         wynik = plusMonths.format(formatDaty);
         } catch (DateTimeParseException exc) {
            System.out.printf("bledne dane wejsciowe");
            }   
         return wynik;
    }
    
    public static long roznicaDni(String StartDate, String EndDate) {
        DateTimeFormatter formatDaty = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        long numberOfDays = -1;
        try {
            LocalDate Start_date = LocalDate.parse(StartDate, formatDaty);
            LocalDate End_date = LocalDate.parse(EndDate, formatDaty);
            numberOfDays = ChronoUnit.DAYS.between(Start_date, End_date);
            System.out.println(numberOfDays);
        } catch (DateTimeParseException exc) {
            System.out.printf("bledne dane wejsciowe");
        }
        return numberOfDays;
    }
    
}
