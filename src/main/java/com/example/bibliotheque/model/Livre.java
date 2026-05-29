package com.example.bibliotheque.model;


import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Livre {
    private String titre;
    private String auteur;
    private String isbn;
    private String idExemplaire;
    private StatutLivre statut;
    private EtatPhysique etatPhysique;
    private LocalDate dateDisponibilite; // Pour les livres en réparation

    public Livre(String titre, String auteur, String isbn, EtatPhysique etatPhysique) {
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.idExemplaire = UUID.randomUUID().toString();
        this.statut = StatutLivre.DISPONIBLE;
        this.etatPhysique = etatPhysique;
        this.dateDisponibilite = null;
    }

    // Constructeur pour chargement JSON
    public Livre(String titre, String auteur, String isbn, String idExemplaire, 
                 StatutLivre statut, EtatPhysique etatPhysique, LocalDate dateDisponibilite) {
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.idExemplaire = idExemplaire;
        this.statut = statut;
        this.etatPhysique = etatPhysique;
        this.dateDisponibilite = dateDisponibilite;
    }

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getIdExemplaire() { return idExemplaire; }
    
    public StatutLivre getStatut() { return statut; }
    public void setStatut(StatutLivre statut) { this.statut = statut; }
    
    public EtatPhysique getEtatPhysique() { return etatPhysique; }
    public void setEtatPhysique(EtatPhysique etatPhysique) { this.etatPhysique = etatPhysique; }
    
    public LocalDate getDateDisponibilite() { return dateDisponibilite; }
    public void setDateDisponibilite(LocalDate dateDisponibilite) { this.dateDisponibilite = dateDisponibilite; }

    public boolean estDisponible() {
        return statut == StatutLivre.DISPONIBLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livre livre = (Livre) o;
        return Objects.equals(idExemplaire, livre.idExemplaire);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idExemplaire);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) - %s", titre, auteur, isbn, statut);
    }
}
