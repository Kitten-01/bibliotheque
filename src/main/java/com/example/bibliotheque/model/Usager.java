package com.example.bibliotheque.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class Usager {
    protected String id;
    protected String nom;
    protected List<Emprunt> empruntsEnCours;

    public Usager(String nom) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.empruntsEnCours = new ArrayList<>();
    }

    // Constructeur pour chargement JSON
    public Usager(String id, String nom, List<Emprunt> empruntsEnCours) {
        this.id = id;
        this.nom = nom;
        this.empruntsEnCours = empruntsEnCours != null ? empruntsEnCours : new ArrayList<>();
    }

    public abstract int getMaxLivres();
    public abstract int getDureeEmpruntJours();
    public abstract TypeUsager getType();

    public String getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public List<Emprunt> getEmpruntsEnCours() { return new ArrayList<>(empruntsEnCours); }
    
    public boolean peutEmprunter() {
        return empruntsEnCours.size() < getMaxLivres();
    }
    
    public boolean aEmprunteLivre(String isbn) {
        return empruntsEnCours.stream()
            .anyMatch(emprunt -> emprunt.getLivre().getIsbn().equals(isbn));
    }
    
    public void ajouterEmprunt(Emprunt emprunt) {
        empruntsEnCours.add(emprunt);
    }
    
    public boolean retirerEmprunt(Livre livre) {
        return empruntsEnCours.removeIf(emprunt -> emprunt.getLivre().equals(livre));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usager usager = (Usager) o;
        return Objects.equals(id, usager.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) - Emprunts: %d/%d", 
            getType(), nom, id.substring(0, 8), empruntsEnCours.size(), getMaxLivres());
    }
}
