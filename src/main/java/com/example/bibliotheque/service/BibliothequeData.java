package com.example.bibliotheque.service;

import com.example.bibliotheque.model.Emprunt;
import com.example.bibliotheque.model.Livre;
import com.example.bibliotheque.model.Usager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BibliothequeData {

    private List<Livre> livres;
    private List<Usager> usagers;
    private List<Emprunt> emprunts;

    private int compteurEmprunts;

    private LocalDate dateCourante;

    // Constructeur vide pour Gson
    public BibliothequeData() {

        this.livres = new ArrayList<>();
        this.usagers = new ArrayList<>();
        this.emprunts = new ArrayList<>();

        this.compteurEmprunts = 0;

        this.dateCourante = LocalDate.now();
    }

    public List<Livre> getLivres() {
        return livres;
    }

    public void setLivres(List<Livre> livres) {
        this.livres = livres;
    }

    public List<Usager> getUsagers() {
        return usagers;
    }

    public void setUsagers(List<Usager> usagers) {
        this.usagers = usagers;
    }

    public List<Emprunt> getEmprunts() {
        return emprunts;
    }

    public void setEmprunts(List<Emprunt> emprunts) {
        this.emprunts = emprunts;
    }

    public int getCompteurEmprunts() {
        return compteurEmprunts;
    }

    public void setCompteurEmprunts(
            int compteurEmprunts
    ) {
        this.compteurEmprunts =
                compteurEmprunts;
    }

    public LocalDate getDateCourante() {
        return dateCourante;
    }

    public void setDateCourante(
            LocalDate dateCourante
    ) {
        this.dateCourante =
                dateCourante;
    }
}