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
}
