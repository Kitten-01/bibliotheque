package com.example.bibliotheque.service;

import com.bibliotheque.model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Bibliotheque {
    private final Map<String, Livre> livres; // idExemplaire -> Livre
    private final Map<String, Usager> usagers; // id -> Usager
    private final List<Emprunt> empruntsActifs;
    private final List<Emprunt> historiqueEmprunts;
    private final AtomicInteger compteurGlobalEmprunts;
    private LocalDate dateCourante;
    
    public Bibliotheque() {
        this.livres = Collections.synchronizedMap(new HashMap<>());
        this.usagers = Collections.synchronizedMap(new HashMap<>());
        this.empruntsActifs = Collections.synchronizedList(new ArrayList<>());
        this.historiqueEmprunts = Collections.synchronizedList(new ArrayList<>());
        this.compteurGlobalEmprunts = new AtomicInteger(0);
        this.dateCourante = LocalDate.now();
    }
    
    // Gestion des livres
    public synchronized void ajouterLivre(Livre livre) {
        livres.put(livre.getIdExemplaire(), livre);
    }
    
    public synchronized void supprimerLivre(String idExemplaire) {
        Livre livre = livres.get(idExemplaire);
        if (livre != null && livre.getStatut() == StatutLivre.DISPONIBLE) {
            livres.remove(idExemplaire);
        } else {
            throw new IllegalStateException("Impossible de supprimer un livre emprunté ou en réparation");
        }
    }
    
    public synchronized void modifierLivre(Livre livre) {
        if (livres.containsKey(livre.getIdExemplaire())) {
            livres.put(livre.getIdExemplaire(), livre);
        } else {
            throw new IllegalArgumentException("Livre non trouvé");
        }
    }
    
    public Optional<Livre> trouverLivreParExemplaire(String idExemplaire) {
        return Optional.ofNullable(livres.get(idExemplaire));
    }
    
    // Recherches avec programmation fonctionnelle
    public List<Livre> rechercherParTitre(String titre) {
        return livres.values().stream()
            .filter(livre -> livre.getTitre().toLowerCase().contains(titre.toLowerCase()))
            .sorted(Comparator.comparing(Livre::getTitre))
            .collect(Collectors.toList());
    }
    
    public List<Livre> rechercherParAuteur(String auteur) {
        return livres.values().stream()
            .filter(livre -> livre.getAuteur().toLowerCase().contains(auteur.toLowerCase()))
            .sorted(Comparator.comparing(Livre::getTitre))
            .collect(Collectors.toList());
    }
    
    public List<Livre> rechercherParIsbn(String isbn) {
        return livres.values().stream()
            .filter(livre -> livre.getIsbn().equals(isbn))
            .sorted(Comparator.comparing(Livre::getTitre))
            .collect(Collectors.toList());
    }
    
    public List<Livre> getLivresDisponibles() {
        return livres.values().stream()
            .filter(Livre::estDisponible)
            .collect(Collectors.toList());
    }
    
    public List<Livre> getLivresEnReparation() {
        return livres.values().stream()
            .filter(livre -> livre.getStatut() == StatutLivre.EN_REPARATION)
            .collect(Collectors.toList());
    }
    
    // Gestion des usagers
    public synchronized void ajouterUsager(Usager usager) {
        usagers.put(usager.getId(), usager);
    }
    
    public synchronized void supprimerUsager(String id) {
        Usager usager = usagers.get(id);
        if (usager != null && usager.getEmpruntsEnCours().isEmpty()) {
            usagers.remove(id);
        } else {
            throw new IllegalStateException("Impossible de supprimer un usager avec des emprunts en cours");
        }
    }
    
    public synchronized void modifierUsager(Usager usager) {
        if (usagers.containsKey(usager.getId())) {
            usagers.put(usager.getId(), usager);
        } else {
            throw new IllegalArgumentException("Usager non trouvé");
        }
    }
    
    public Optional<Usager> trouverUsagerParId(String id) {
        return Optional.ofNullable(usagers.get(id));
    }
    
    public List<Usager> getAllUsagers() {
        return new ArrayList<>(usagers.values());
    }
    
    // Gestion des emprunts
    public synchronized EmpruntResult emprunterLivre(String idUsager, String idExemplaire) {
        Usager usager = usagers.get(idUsager);
        Livre livre = livres.get(idExemplaire);
        
        if (usager == null) {
            return EmpruntResult.echec("Usager non trouvé");
        }
        if (livre == null) {
            return EmpruntResult.echec("Livre non trouvé");
        }
        if (livre.getStatut() != StatutLivre.DISPONIBLE) {
            return EmpruntResult.echec("Livre non disponible (statut: " + livre.getStatut() + ")");
        }
        if (!usager.peutEmprunter()) {
            return EmpruntResult.echec("L'usager a déjà atteint sa limite d'emprunts");
        }
        if (usager.aEmprunteLivre(livre.getIsbn())) {
            return EmpruntResult.echec("L'usager a déjà emprunté un autre exemplaire de cet ouvrage");
        }
        
        // Effectuer l'emprunt
        Emprunt emprunt = new Emprunt(livre, usager, dateCourante, usager.getDureeEmpruntJours());
        livre.setStatut(StatutLivre.EMPRUNTE);
        usager.ajouterEmprunt(emprunt);
        empruntsActifs.add(emprunt);
        compteurGlobalEmprunts.incrementAndGet();
        
        return EmpruntResult.succes(emprunt);
    }
    
    public synchronized RetourResult retournerLivre(String idExemplaire, EtatPhysique nouvelEtat) {
        Livre livre = livres.get(idExemplaire);
        if (livre == null) {
            return RetourResult.echec("Livre non trouvé");
        }
        if (livre.getStatut() != StatutLivre.EMPRUNTE) {
            return RetourResult.echec("Ce livre n'est pas emprunté");
        }
        if (!DateUtils.estJourOuvrable(dateCourante)) {
            return RetourResult.echec("Les retours ne sont acceptés que les jours ouvrables");
        }
        
        // Trouver l'emprunt actif
        Optional<Emprunt> empruntOpt = empruntsActifs.stream()
            .filter(e -> e.getLivre().equals(livre) && e.estActif())
            .findFirst();
            
        if (!empruntOpt.isPresent()) {
            return RetourResult.echec("Emprunt non trouvé");
        }
        
        Emprunt emprunt = empruntOpt.get();
        Usager usager = emprunt.getUsager();
        
        // Mettre à jour l'état du livre
        livre.setEtatPhysique(nouvelEtat);
        emprunt.setDateRetourReelle(dateCourante);
        usager.retirerEmprunt(livre);
        empruntsActifs.remove(emprunt);
        historiqueEmprunts.add(emprunt);
        
        // Gérer l'état après retour
        if (nouvelEtat == EtatPhysique.A_REPARER) {
            livre.setStatut(StatutLivre.EN_REPARATION);
            LocalDate dateDisponible = DateUtils.ajouterJoursOuvrables(dateCourante, 3);
            livre.setDateDisponibilite(dateDisponible);
        } else {
            livre.setStatut(StatutLivre.DISPONIBLE);
            livre.setDateDisponibilite(null);
        }
        
        return RetourResult.succes(livre, nouvelEtat);
    }
    
    // Vérification quotidienne des réparations
    public synchronized void verifierReparations() {
        LocalDate aujourdhui = dateCourante;
        
        livres.values().stream()
            .filter(livre -> livre.getStatut() == StatutLivre.EN_REPARATION)
            .filter(livre -> livre.getDateDisponibilite() != null && 
                            !livre.getDateDisponibilite().isAfter(aujourdhui))
            .forEach(livre -> {
                livre.setStatut(StatutLivre.DISPONIBLE);
                livre.setDateDisponibilite(null);
                System.out.println("Livre réparé et disponible: " + livre.getTitre());
            });
    }
    
    // Avancer la date (pour simulation)
    public synchronized void avancerDate(int jours) {
        for (int i = 0; i < jours; i++) {
            dateCourante = dateCourante.plusDays(1);
            if (DateUtils.estJourOuvrable(dateCourante)) {
                verifierReparations();
            }
        }
    }
    
    // Statistiques avec programmation fonctionnelle
    public Map<TypeUsager, Long> getStatistiquesEmpruntsParType() {
        return historiqueEmprunts.stream()
            .collect(Collectors.groupingBy(
                emprunt -> emprunt.getUsager().getType(),
                Collectors.counting()
            ));
    }
    
    public List<Emprunt> getEmpruntsEnRetard() {
        return empruntsActifs.stream()
            .filter(emprunt -> emprunt.estEnRetard(dateCourante))
            .sorted(Comparator.comparing(Emprunt::getDateRetourPrevue))
            .collect(Collectors.toList());
    }
    
    public Optional<LocalDate> getDateRetourPlusProche(String isbn) {
        return empruntsActifs.stream()
            .filter(emprunt -> emprunt.getLivre().getIsbn().equals(isbn))
            .map(Emprunt::getDateRetourPrevue)
            .min(LocalDate::compareTo);
    }
    
    // Getters
    public List<Livre> getAllLivres() {
        return new ArrayList<>(livres.values());
    }
    
    public int getCompteurGlobalEmprunts() {
        return compteurGlobalEmprunts.get();
    }
    
    public LocalDate getDateCourante() {
        return dateCourante;
    }
    
    public void setDateCourante(LocalDate date) {
        this.dateCourante = date;
    }
    
    public List<Emprunt> getEmpruntsActifs() {
        return new ArrayList<>(empruntsActifs);
    }
    
    // Classes de résultat
    public static class EmpruntResult {
        private final boolean succes;
        private final String message;
        private final Emprunt emprunt;
        
        private EmpruntResult(boolean succes, String message, Emprunt emprunt) {
            this.succes = succes;
            this.message = message;
            this.emprunt = emprunt;
        }
        
        public static EmpruntResult succes(Emprunt emprunt) {
            return new EmpruntResult(true, "Emprunt réussi", emprunt);
        }
        
        public static EmpruntResult echec(String message) {
            return new EmpruntResult(false, message, null);
        }
        
        public boolean isSucces() { return succes; }
        public String getMessage() { return message; }
        public Emprunt getEmprunt() { return emprunt; }
    }
    
    public static class RetourResult {
        private final boolean succes;
        private final String message;
        private final Livre livre;
        private final EtatPhysique nouvelEtat;
        
        private RetourResult(boolean succes, String message, Livre livre, EtatPhysique nouvelEtat) {
            this.succes = succes;
            this.message = message;
            this.livre = livre;
            this.nouvelEtat = nouvelEtat;
        }
        
        public static RetourResult succes(Livre livre, EtatPhysique nouvelEtat) {
            return new RetourResult(true, "Retour réussi", livre, nouvelEtat);
        }
        
        public static RetourResult echec(String message) {
            return new RetourResult(false, message, null, null);
        }
        
        public boolean isSucces() { return succes; }
        public String getMessage() { return message; }
        public Livre getLivre() { return livre; }
        public EtatPhysique getNouvelEtat() { return nouvelEtat; }
    }
}
