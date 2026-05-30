package com.example.bibliotheque.model;

import java.time.LocalDate;
import java.util.Objects;

public class Emprunt {

    private Livre livre;
    private Usager usager;

    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;

    // Constructeur vide pour Gson
    public Emprunt() {
    }

    public Emprunt(Livre livre,
                    Usager usager,
                    LocalDate dateEmprunt,
                    LocalDate dateRetourPrevue) {

        if (livre == null || usager == null) {
            throw new IllegalArgumentException("Livre ou usager null");
        }

        this.livre = livre;
        this.usager = usager;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public Livre getLivre() {
        return livre;
    }

    public Usager getUsager() {
        return usager;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    @Override
    public String toString() {
        return livre.getTitre()
                + " | "
                + usager.getNom()
                + " | retour prévu : "
                + dateRetourPrevue;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Emprunt emprunt)) return false;

        return Objects.equals(livre.getIdExemplaire(),
                emprunt.livre.getIdExemplaire())
                &&
                Objects.equals(usager.getId(),
                        emprunt.usager.getId())
                &&
                Objects.equals(dateEmprunt,
                        emprunt.dateEmprunt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                livre.getIdExemplaire(),
                usager.getId(),
                dateEmprunt
        );
    }
}

