package com.example.bibliotheque.controller;

import com.bibliotheque.model.*;
import com.bibliotheque.service.Bibliotheque;
import com.bibliotheque.service.PersistanceService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BibliothequeController {
    private Bibliotheque bibliotheque;
    private PersistanceService persistance;
    
    public BibliothequeController() {
        this.persistance = new PersistanceService();
        chargerDonnees();
    }
    
    private void chargerDonnees() {
        try {
            Optional<Bibliotheque> chargee = persistance.charger();
            if (chargee.isPresent()) {
                bibliotheque = chargee.get();
            } else {
                bibliotheque = new Bibliotheque();
                initDonneesExemple();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            bibliotheque = new Bibliotheque();
            initDonneesExemple();
        }
    }
    
    private void initDonneesExemple() {
        // Ajouter quelques livres d'exemple
        bibliotheque.ajouterLivre(new Livre("Java pour les nuls", "John Doe", "978-1234567890", EtatPhysique.NEUF));
        bibliotheque.ajouterLivre(new Livre("Programmation Orientée Objet", "Jane Smith", "978-0987654321", EtatPhysique.BON));
        bibliotheque.ajouterLivre(new Livre("Design Patterns", "Erich Gamma", "978-0201633610", EtatPhysique.USE));
        
        // Ajouter quelques usagers
        bibliotheque.ajouterUsager(new Etudiant("Alice Martin"));
        bibliotheque.ajouterUsager(new Professeur("Robert Tremblay"));
        bibliotheque.ajouterUsager(new Visiteur("Claire Dubois"));
    }
    
    public void sauvegarder() {
        try {
            persistance.sauvegarder(bibliotheque);
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }
    
    // Gestion des livres
    public void ajouterLivre(Livre livre) {
        bibliotheque.ajouterLivre(livre);
    }
    
    public void modifierLivre(Livre livre) {
        bibliotheque.modifierLivre(livre);
    }
    
    public void supprimerLivre(String idExemplaire) {
        bibliotheque.supprimerLivre(idExemplaire);
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
    
    // Recherches
    public List<Livre> rechercherParTitre(String titre) {
        return bibliotheque.rechercherParTitre(titre);
    }
    
    public List<Livre> rechercherParAuteur(String auteur) {
        return bibliotheque.rechercherParAuteur(auteur);
    }
    
    public List<Livre> rechercherParIsbn(String isbn) {
        return bibliotheque.rechercherParIsbn(isbn);
    }
    
    // Gestion des usagers
    public void ajouterUsager(Usager usager) {
        bibliotheque.ajouterUsager(usager);
    }
    
    public void modifierUsager(Usager usager) {
        bibliotheque.modifierUsager(usager);
    }
    
    public void supprimerUsager(String id) {
        bibliotheque.supprimerUsager(id);
    }
    
    public List<Usager> getAllUsagers() {
        return bibliotheque.getAllUsagers();
    }
    
    // Gestion des emprunts
    public EmpruntResultWrapper emprunterLivre(String idUsager, String idExemplaire) {
        Bibliotheque.EmpruntResult result = bibliotheque.emprunterLivre(idUsager, idExemplaire);
        return new EmpruntResultWrapper(result.isSucces(), result.getMessage(), result.getEmprunt());
    }
    
    public RetourResultWrapper retournerLivre(String idExemplaire, EtatPhysique nouvelEtat) {
        Bibliotheque.RetourResult result = bibliotheque.retournerLivre(idExemplaire, nouvelEtat);
        return new RetourResultWrapper(result.isSucces(), result.getMessage(), result.getLivre(), result.getNouvelEtat());
    }
    
    // Statistiques
    public Map<TypeUsager, Long> getStatistiquesEmpruntsParType() {
        return bibliotheque.getStatistiquesEmpruntsParType();
    }
    
    public List<Emprunt> getEmpruntsEnRetard() {
        return bibliotheque.getEmpruntsEnRetard();
    }
    
    public int getJoursRetard(Emprunt emprunt) {
        if (emprunt.estEnRetard(getDateCourante())) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(emprunt.getDateRetourPrevue(), getDateCourante());
        }
        return 0;
    }
    
    public Optional<LocalDate> getDateRetourPlusProche(String isbn) {
        return bibliotheque.getDateRetourPlusProche(isbn);
    }
    
    public int getCompteurGlobalEmprunts() {
        return bibliotheque.getCompteurGlobalEmprunts();
    }
    
    public LocalDate getDateCourante() {
        return bibliotheque.getDateCourante();
    }
    
    // Wrappers pour les résultats
    public static class EmpruntResultWrapper {
        private final boolean succes;
        private final String message;
        private final Emprunt emprunt;
        
        public EmpruntResultWrapper(boolean succes, String message, Emprunt emprunt) {
            this.succes = succes;
            this.message = message;
            this.emprunt = emprunt;
        }
        
        public boolean isSucces() { return succes; }
        public String getMessage() { return message; }
        public Emprunt getEmprunt() { return emprunt; }
    }
    
    public static class RetourResultWrapper {
        private final boolean succes;
        private final String message;
        private final Livre livre;
        private final EtatPhysique nouvelEtat;
        
        public RetourResultWrapper(boolean succes, String message, Livre livre, EtatPhysique nouvelEtat) {
            this.succes = succes;
            this.message = message;
            this.livre = livre;
            this.nouvelEtat = nouvelEtat;
        }
        
        public boolean isSucces() { return succes; }
        public String getMessage() { return message; }
        public Livre getLivre() { return livre; }
        public EtatPhysique getNouvelEtat() { return nouvelEtat; }
    }
}