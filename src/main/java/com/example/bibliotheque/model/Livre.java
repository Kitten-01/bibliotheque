package com.example.bibliotheque.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Livre {

    private String idExemplaire;

    private String titre;
    private String auteur;
    private String isbn;

    private StatutLivre statut;
    private EtatPhysique etatPhysique;

    private LocalDate dateDisponibilite;

    // Constructeur vide pour Gson
    public Livre() {
    }

    public Livre(
            String titre,
            String auteur,
            String isbn,
            EtatPhysique etatPhysique
    ) {

        this.idExemplaire =
                UUID.randomUUID().toString();

        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;

        this.statut =
                StatutLivre.DISPONIBLE;

        this.etatPhysique =
                etatPhysique;

        this.dateDisponibilite = null;
    }

    public String getIdExemplaire() {
        return idExemplaire;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public StatutLivre getStatut() {
        return statut;
    }

    public EtatPhysique getEtatPhysique() {
        return etatPhysique;
    }

    public LocalDate getDateDisponibilite() {
        return dateDisponibilite;
    }

    public boolean estDisponible() {

        return statut ==
                StatutLivre.DISPONIBLE;
    }

    public void setStatut(
            StatutLivre statut
    ) {
        this.statut = statut;
    }

    public void setEtatPhysique(
            EtatPhysique etatPhysique
    ) {
        this.etatPhysique =
                etatPhysique;
    }

    public void setDateDisponibilite(
            LocalDate dateDisponibilite
    ) {
        this.dateDisponibilite =
                dateDisponibilite;
    }

    @Override
    public String toString() {

        return titre
                + " - "
                + auteur
                + " ["
                + statut
                + "]";
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Livre livre)) {
            return false;
        }

        return Objects.equals(
                idExemplaire,
                livre.idExemplaire
        );
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                idExemplaire
        );
    }
}