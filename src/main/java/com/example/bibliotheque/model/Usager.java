package com.example.bibliotheque.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Usager {

    private final String id;
    private String nom;
    private final List<Emprunt> emprunts = new ArrayList<>();

    public Usager(String nom) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
    }

    // ================= ABSTRACTION =================

    public abstract int getLimiteEmprunts();
    public abstract int getDureeMaxEmprunt();
    public abstract TypeUsager getType();

    // ================= GETTERS =================

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public List<Emprunt> getEmprunts() {
        return emprunts;
    }

    // ================= SETTERS =================

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return nom + " (" + getType() + ")";
    }
}