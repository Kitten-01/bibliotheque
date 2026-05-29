package com.example.bibliotheque.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Emprunt {
    private String id;
    private Livre livre;
    private Usager usager;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourReelle;

    public Emprunt(Livre livre, Usager usager, LocalDate dateEmprunt, int dureeJours) {
        this.id = UUID.randomUUID().toString();
        this.livre = livre;
        this.usager = usager;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateEmprunt.plusDays(dureeJours);
        this.dateRetourReelle = null;
    }

    // Constructeur pour chargement JSON
    public Emprunt(String id, Livre livre, Usager usager, LocalDate dateEmprunt, 
                   LocalDate dateRetourPrevue, LocalDate dateRetourReelle) {
        this.id = id;
        this.livre = livre;
        this.usager = usager;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourReelle = dateRetourReelle;
    }

    public String getId() { return id; }
    public Livre getLivre() { return livre; }
    public Usager getUsager() { return usager; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourReelle() { return dateRetourReelle; }
    
    public void setDateRetourReelle(LocalDate dateRetourReelle) {
        this.dateRetourReelle = dateRetourReelle;
    }
    
    public boolean estEnRetard(LocalDate dateReference) {
        return dateRetourReelle == null && dateRetourPrevue.isBefore(dateReference);
    }
    
    public boolean estActif() {
        return dateRetourReelle == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emprunt emprunt = (Emprunt) o;
        return Objects.equals(id, emprunt.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Emprunt: %s par %s, retour prévu: %s", 
            livre.getTitre(), usager.getNom(), dateRetourPrevue);
    }
}
