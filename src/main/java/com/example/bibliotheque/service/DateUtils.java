package com.example.bibliotheque.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtils {

    /**
     * Vérifie si une date est un jour ouvrable (lundi → vendredi)
     */
    public static boolean estJourOuvrable(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être null");
        }

        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    /**
     * Ajoute un nombre de jours ouvrables à une date
     */
    public static LocalDate ajouterJoursOuvrables(LocalDate date, int joursOuvrables) {
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut pas être null");
        }
        if (joursOuvrables < 0) {
            throw new IllegalArgumentException("Le nombre de jours doit être positif");
        }

        LocalDate result = date;
        int ajout = 0;

        while (ajout < joursOuvrables) {
            result = result.plusDays(1);

            if (estJourOuvrable(result)) {
                ajout++;
            }
        }

        return result;
    }

    /**
     * Calcule le nombre de jours ouvrables entre deux dates
     */
    public static long joursOuvrablesEntre(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Les dates ne peuvent pas être null");
        }

        if (fin.isBefore(debut)) {
            return 0;
        }

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