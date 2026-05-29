package com.example.bibliotheque.model;

public class Etudiant extends Usager {
    
    public Etudiant(String nom) {
        super(nom);
    }
    
    public Etudiant(String id, String nom, java.util.List<Emprunt> empruntsEnCours) {
        super(id, nom, empruntsEnCours);
    }
    
    @Override
    public int getMaxLivres() {
        return 3;
    }
    
    @Override
    public int getDureeEmpruntJours() {
        return 14;
    }
    
    @Override
    public TypeUsager getType() {
        return TypeUsager.ETUDIANT;
    }
}