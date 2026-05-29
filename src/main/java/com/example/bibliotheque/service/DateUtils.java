package com.example.bibliotheque.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    
    public static boolean estJourOuvrable(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }
    
    public static LocalDate ajouterJoursOuvrables(LocalDate date, int joursOuvrables) {
        LocalDate result = date;
        int joursAjoutes = 0;
        
        while (joursAjoutes < joursOuvrables) {
            result = result.plusDays(1);
            if (estJourOuvrable(result)) {
                joursAjoutes++;
            }
        }
        return result;
    }
    
    public static long joursOuvrablesEntre(LocalDate debut, LocalDate fin) {
        long jours = 0;
        LocalDate courant = debut;
        
        while (courant.isBefore(fin)) {
            if (estJourOuvrable(courant)) {
                jours++;
            }
            courant = courant.plusDays(1);
        }
        return jours;
    }
}