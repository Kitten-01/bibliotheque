package com.example.bibliotheque.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTest {

    @Test
    void testAjouterJoursOuvrables() {

        LocalDate vendredi =
                LocalDate.of(2026, 5, 29);

        LocalDate resultat =
                DateUtils.ajouterJoursOuvrables(
                        vendredi,
                        1
                );

        // doit tomber lundi
        assertEquals(
                LocalDate.of(2026, 6, 1),
                resultat
        );
    }
}