package com.example.bibliotheque.service;

import com.example.bibliotheque.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BibliothequeTest {

    private Bibliotheque bibliotheque;

    private Livre livre;
    private Usager usager;

    @BeforeEach
    void setup() {

        bibliotheque = new Bibliotheque();

        livre = new Livre(
                "Clean Code",
                "Robert Martin",
                "123456",
                EtatPhysique.NEUF
        );

        usager = new Etudiant("Alice");

        bibliotheque.ajouterLivre(livre);
        bibliotheque.ajouterUsager(usager);
    }

    @Test
    void testAjoutLivre() {

        assertEquals(
                1,
                bibliotheque.getAllLivres().size()
        );
    }

    @Test
    void testAjoutUsager() {

        assertEquals(
                1,
                bibliotheque.getAllUsagers().size()
        );
    }

    @Test
    void testEmpruntReussi() {

        Bibliotheque.EmpruntResult result =
                bibliotheque.emprunterLivre(
                        usager.getId(),
                        livre.getIdExemplaire()
                );

        assertTrue(result.isSucces());

        assertEquals(
                StatutLivre.EMPRUNTE,
                livre.getStatut()
        );

        assertEquals(
                1,
                usager.getEmprunts().size()
        );
    }

    @Test
    void testRetourLivre() {

        bibliotheque.emprunterLivre(
                usager.getId(),
                livre.getIdExemplaire()
        );

        Bibliotheque.RetourResult result =
                bibliotheque.retournerLivre(
                        livre.getIdExemplaire(),
                        EtatPhysique.USE
                );

        assertTrue(result.isSucces());

        assertEquals(
                StatutLivre.DISPONIBLE,
                livre.getStatut()
        );
    }

    @Test
    void testLimiteEmprunts() {

        for (int i = 0; i < usager.getLimiteEmprunts(); i++) {

            Livre l = new Livre(
                    "Livre " + i,
                    "Auteur",
                    "ISBN-" + i,
                    EtatPhysique.NEUF
            );

            bibliotheque.ajouterLivre(l);

            bibliotheque.emprunterLivre(
                    usager.getId(),
                    l.getIdExemplaire()
            );
        }

        Livre extra = new Livre(
                "Extra",
                "Auteur",
                "999",
                EtatPhysique.NEUF
        );

        bibliotheque.ajouterLivre(extra);

        Bibliotheque.EmpruntResult result =
                bibliotheque.emprunterLivre(
                        usager.getId(),
                        extra.getIdExemplaire()
                );

        assertFalse(result.isSucces());
    }

    @Test
    void testLivreIndisponible() {

        Usager autre = new Etudiant("Bob");

        bibliotheque.ajouterUsager(autre);

        bibliotheque.emprunterLivre(
                usager.getId(),
                livre.getIdExemplaire()
        );

        Bibliotheque.EmpruntResult result =
                bibliotheque.emprunterLivre(
                        autre.getId(),
                        livre.getIdExemplaire()
                );

        assertFalse(result.isSucces());
    }
}