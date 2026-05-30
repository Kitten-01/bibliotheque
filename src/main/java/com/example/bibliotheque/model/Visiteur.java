package com.example.bibliotheque.model;

public class Visiteur extends Usager {

    public Visiteur(String nom) {
        super(nom);
    }

    @Override
    public int getLimiteEmprunts() {
        return 2;
    }

    @Override
    public int getDureeMaxEmprunt() {
        return 7;
    }

    @Override
    public TypeUsager getType() {
        return TypeUsager.VISITEUR;
    }
}