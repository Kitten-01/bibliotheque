package com.example.bibliotheque.controller;

import com.example.bibliotheque.model.*;
import com.example.bibliotheque.service.Bibliotheque;
import com.example.bibliotheque.service.PersistanceService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class BibliothequeController {

    private final Bibliotheque bibliotheque;
    private final PersistanceService persistence;

    public BibliothequeController() {
        this.persistence = new PersistanceService();

        Bibliotheque temp;
        try {
            temp = persistence.charger();
        } catch (IOException e) {
            temp = new Bibliotheque();
        }

        this.bibliotheque = temp;
    }

    // =========================
    // LIVRES
    // =========================

    public void ajouterLivre(Livre l) {
        bibliotheque.ajouterLivre(l);
    }

    public void modifierLivre(Livre l) {
        bibliotheque.modifierLivre(l); // ✔ correction ici
    }

    public void supprimerLivre(String id) {
        bibliotheque.supprimerLivre(id);
    }

    public List<Livre> getAllLivres() {
        return bibliotheque.getAllLivres();
    }

    public List<Livre> getLivresDisponibles() {
        return bibliotheque.getLivresDisponibles();
    }

    public List<Livre> getLivresEnReparation() {
        return bibliotheque.getLivresEnReparation();
    }

    // =========================
    // USAGERS
    // =========================

    public void ajouterUsager(Usager u) {
        bibliotheque.ajouterUsager(u);
    }

    public void modifierUsager(Usager u) {
        bibliotheque.modifierUsager(u);
    }

    public List<Usager> getAllUsagers() {
        return bibliotheque.getAllUsagers();
    }

    public void supprimerUsager(String id) {
        bibliotheque.supprimerUsager(id);
    }

    // =========================
    // EMPRUNTS
    // =========================

    public Bibliotheque.EmpruntResult emprunterLivre(String idUsager, String idLivre) {
        return bibliotheque.emprunterLivre(idUsager, idLivre);
    }

    public Bibliotheque.RetourResult retournerLivre(String idLivre, EtatPhysique etat) {
        return bibliotheque.retournerLivre(idLivre, etat);
    }

    public List<Emprunt> getEmpruntsEnRetard() {
        return bibliotheque.getEmpruntsEnRetard();
    }

    public List<Emprunt> getAllEmprunts() {
        return bibliotheque.getAllEmprunts();
    }

    // =========================
    // RECHERCHE (STREAM REQUIRED)
    // =========================

    public List<Livre> rechercherParTitre(String titre) {
        return bibliotheque.rechercherParTitre(titre);
    }

    public List<Livre> rechercherParAuteur(String auteur) {
        return bibliotheque.rechercherParAuteur(auteur);
    }

    public List<Livre> rechercherParIsbn(String isbn) {
        return bibliotheque.rechercherParIsbn(isbn);
    }

    public java.util.Optional<LocalDate> getDateRetourPlusProche(String isbn) {
        return bibliotheque.getDateRetourPlusProche(isbn);
    }

    // =========================
    // STATISTIQUES
    // =========================

    public Map<TypeUsager, Long> getStatistiquesEmpruntsParType() {
        return bibliotheque.getStatistiquesEmpruntsParType();
    }

    public int getCompteurGlobalEmprunts() {
        return bibliotheque.getCompteurGlobalEmprunts();
    }

    public LocalDate getDateCourante() {
        return bibliotheque.getDateCourante();
    }

    public long getJoursRetard(Emprunt e) {
        return ChronoUnit.DAYS.between(
                e.getDateRetourPrevue(),
                bibliotheque.getDateCourante()
        );
    }

    // =========================
    // PERSISTANCE
    // =========================

    public void sauvegarder() {
        try {
            persistence.sauvegarder(bibliotheque);
        } catch (IOException e) {
            throw new RuntimeException("Erreur sauvegarde bibliothèque", e);
        }
    }

    public void avancerDate(int jours) {
        bibliotheque.avancerDate(jours);
    }
}