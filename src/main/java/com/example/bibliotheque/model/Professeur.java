package com.example.bibliotheque.model;

public class Professeur extends Usager {

    public Professeur(String nom) {
        super(nom);
    }

    @Override
    public int getLimiteEmprunts() {
        return 6;
    }

    @Override
    public int getDureeMaxEmprunt() {
        return 30;
    }

    @Override
    public TypeUsager getType() {
        return TypeUsager.PROFESSEUR;
    }
}