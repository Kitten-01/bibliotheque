package com.example.bibliotheque.model;

public class Etudiant extends Usager {

    public Etudiant(String nom) {
        super(nom);
    }

    @Override
    public int getLimiteEmprunts() {
        return 3;
    }

    @Override
    public int getDureeMaxEmprunt() {
        return 14;
    }

    @Override
    public TypeUsager getType() {
        return TypeUsager.ETUDIANT;
    }
}