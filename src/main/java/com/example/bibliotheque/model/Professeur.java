package com.example.bibliotheque.model;

public class Professeur extends Usager {
    
    public Professeur(String nom) {
        super(nom);
    }
    
    public Professeur(String id, String nom, java.util.List<Emprunt> empruntsEnCours) {
        super(id, nom, empruntsEnCours);
    }
    
    @Override
    public int getMaxLivres() {
        return 6;
    }
    
    @Override
    public int getDureeEmpruntJours() {
        return 30;
    }
    
    @Override
    public TypeUsager getType() {
        return TypeUsager.PROFESSEUR;
    }
}
