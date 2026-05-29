package com.example.bibliotheque.model;

public class Visiteur extends Usager {
    
    public Visiteur(String nom) {
        super(nom);
    }
    
    public Visiteur(String id, String nom, java.util.List<Emprunt> empruntsEnCours) {
        super(id, nom, empruntsEnCours);
    }
    
    @Override
    public int getMaxLivres() {
        return 1;
    }
    
    @Override
    public int getDureeEmpruntJours() {
        return 7;
    }
    
    @Override
    public TypeUsager getType() {
        return TypeUsager.VISITEUR;
    }
}
