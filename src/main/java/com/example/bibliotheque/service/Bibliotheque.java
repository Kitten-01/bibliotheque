package com.example.bibliotheque.service;

import com.example.bibliotheque.model.Emprunt;
import com.example.bibliotheque.model.EtatPhysique;
import com.example.bibliotheque.model.Livre;
import com.example.bibliotheque.model.StatutLivre;
import com.example.bibliotheque.model.TypeUsager;
import com.example.bibliotheque.model.Usager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Bibliotheque {

    private final List<Livre> livres =
            new ArrayList<>();

    private final List<Usager> usagers =
            new ArrayList<>();

    private final List<Emprunt> emprunts =
            new ArrayList<>();

    // compteur global
    private final AtomicInteger compteurGlobalEmprunts =
            new AtomicInteger(0);

    private LocalDate dateCourante =
            LocalDate.now();

    // =====================================================
    // LIVRES
    // =====================================================

    public synchronized void ajouterLivre(
            Livre livre
    ) {

        livres.add(livre);
    }

    public synchronized void modifierLivre(
            Livre livre
    ) {

        for (int i = 0; i < livres.size(); i++) {

            if (
                    livres.get(i)
                            .getIdExemplaire()
                            .equals(
                                    livre.getIdExemplaire()
                            )
            ) {

                livres.set(i, livre);
                return;
            }
        }
    }

    public synchronized void supprimerLivre(
            String idExemplaire
    ) {

        boolean emprunte =
                emprunts.stream()
                        .anyMatch(e ->
                                e.getLivre()
                                        .getIdExemplaire()
                                        .equals(idExemplaire)
                        );

        if (emprunte) {

            throw new IllegalStateException(
                    "Impossible de supprimer un livre emprunté"
            );
        }

        livres.removeIf(l ->
                l.getIdExemplaire()
                        .equals(idExemplaire)
        );
    }

    public synchronized List<Livre>
    getAllLivres() {

        return new ArrayList<>(livres);
    }

    // =====================================================
    // USAGERS
    // =====================================================

    public synchronized void ajouterUsager(
            Usager usager
    ) {

        usagers.add(usager);
    }

    public synchronized void modifierUsager(
            Usager usager
    ) {

        for (int i = 0; i < usagers.size(); i++) {

            if (
                    usagers.get(i)
                            .getId()
                            .equals(usager.getId())
            ) {

                usagers.set(i, usager);
                return;
            }
        }
    }

    public synchronized void supprimerUsager(
            String id
    ) {

        Usager usager =
                trouverUsager(id);

        if (usager == null) {
            return;
        }

        if (
                !usager.getEmprunts()
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "Impossible de supprimer un usager avec des emprunts actifs"
            );
        }

        usagers.removeIf(u ->
                u.getId().equals(id)
        );
    }

    public synchronized List<Usager>
    getAllUsagers() {

        return new ArrayList<>(usagers);
    }

    // =====================================================
    // EMPRUNTS
    // =====================================================

    public synchronized EmpruntResult emprunterLivre(
            String idUsager,
            String idLivre
    ) {

        Usager usager =
                trouverUsager(idUsager);

        Livre livre =
                trouverLivre(idLivre);

        if (
                usager == null
                        || livre == null
        ) {

            return new EmpruntResult(
                    false,
                    "Usager ou livre introuvable"
            );
        }

        // livre disponible
        if (
                livre.getStatut()
                        != StatutLivre.DISPONIBLE
        ) {

            return new EmpruntResult(
                    false,
                    "Livre non disponible"
            );
        }

        // livre à réparer
        if (
                livre.getEtatPhysique()
                        == EtatPhysique.A_REPARER
        ) {

            return new EmpruntResult(
                    false,
                    "Livre à réparer"
            );
        }

        // limite d'emprunts
        if (
                usager.getEmprunts().size()
                        >= usager.getLimiteEmprunts()
        ) {

            return new EmpruntResult(
                    false,
                    "Limite d'emprunts atteinte"
            );
        }

        // même ISBN interdit
        boolean deja =
                usager.getEmprunts()
                        .stream()
                        .anyMatch(e ->
                                e.getLivre()
                                        .getIsbn()
                                        .equals(livre.getIsbn())
                        );

        if (deja) {

            return new EmpruntResult(
                    false,
                    "Même ouvrage déjà emprunté"
            );
        }

        LocalDate dateRetour =
                DateUtils.ajouterJoursOuvrables(
                        dateCourante,
                        usager.getDureeMaxEmprunt()
                );

        Emprunt emprunt =
                new Emprunt(
                        livre,
                        usager,
                        dateCourante,
                        dateRetour
                );

        livre.setStatut(
                StatutLivre.EMPRUNTE
        );

        emprunts.add(emprunt);

        usager.getEmprunts()
                .add(emprunt);

        compteurGlobalEmprunts
                .incrementAndGet();

        return new EmpruntResult(
                true,
                "Emprunt réussi"
        );
    }

    public synchronized RetourResult retournerLivre(
            String idLivre,
            EtatPhysique nouvelEtat
    ) {

        Livre livre =
                trouverLivre(idLivre);

        if (livre == null) {

            return new RetourResult(
                    false,
                    "Livre introuvable"
            );
        }

        Optional<Emprunt> optional =
                emprunts.stream()
                        .filter(e ->
                                e.getLivre()
                                        .getIdExemplaire()
                                        .equals(idLivre)
                        )
                        .findFirst();

        if (optional.isEmpty()) {

            return new RetourResult(
                    false,
                    "Livre non emprunté"
            );
        }

        // retours interdits le weekend
        if (estWeekend(dateCourante)) {

            return new RetourResult(
                    false,
                    "Retour impossible la fin de semaine"
            );
        }

        Emprunt emprunt =
                optional.get();

        emprunts.remove(emprunt);

        emprunt.getUsager()
                .getEmprunts()
                .remove(emprunt);

        livre.setEtatPhysique(
                nouvelEtat
        );

        // réparation
        if (
                nouvelEtat
                        == EtatPhysique.A_REPARER
        ) {

            livre.setStatut(
                    StatutLivre.A_REPARER
            );

            LocalDate dateDispo =
                    DateUtils
                            .ajouterJoursOuvrables(
                                    dateCourante,
                                    3
                            );

            livre.setDateDisponibilite(
                    dateDispo
            );

        } else {

            livre.setStatut(
                    StatutLivre.DISPONIBLE
            );

            livre.setDateDisponibilite(
                    null
            );
        }

        return new RetourResult(
                true,
                "Retour effectué"
        );
    }

    // =====================================================
    // PERSISTANCE
    // =====================================================

    public synchronized void forcerAjoutEmprunt(
            Emprunt emprunt
    ) {

        if (emprunt == null) {
            return;
        }

        emprunts.add(emprunt);

        Livre livre =
                emprunt.getLivre();

        if (livre != null) {

            livre.setStatut(
                    StatutLivre.EMPRUNTE
            );
        }

        Usager usager =
                emprunt.getUsager();

        if (usager != null) {

            if (
                    !usager.getEmprunts()
                            .contains(emprunt)
            ) {

                usager.getEmprunts()
                        .add(emprunt);
            }
        }

        compteurGlobalEmprunts
                .incrementAndGet();
    }

    // =====================================================
    // RECHERCHE
    // =====================================================

    public synchronized List<Livre>
    rechercherParTitre(
            String titre
    ) {

        return livres.stream()
                .filter(l ->
                        l.getTitre()
                                .toLowerCase()
                                .contains(
                                        titre.toLowerCase()
                                )
                )
                .sorted(
                        Comparator.comparing(
                                Livre::getTitre
                        )
                )
                .collect(Collectors.toList());
    }

    public synchronized List<Livre>
    rechercherParAuteur(
            String auteur
    ) {

        return livres.stream()
                .filter(l ->
                        l.getAuteur()
                                .toLowerCase()
                                .contains(
                                        auteur.toLowerCase()
                                )
                )
                .sorted(
                        Comparator.comparing(
                                Livre::getAuteur
                        )
                )
                .collect(Collectors.toList());
    }

    public synchronized List<Livre>
    rechercherParIsbn(
            String isbn
    ) {

        return livres.stream()
                .filter(l ->
                        l.getIsbn()
                                .toLowerCase()
                                .contains(
                                        isbn.toLowerCase()
                                )
                )
                .sorted(
                        Comparator.comparing(
                                Livre::getIsbn
                        )
                )
                .collect(Collectors.toList());
    }

    // =====================================================
    // DISPONIBILITÉ
    // =====================================================

    public synchronized List<Livre>
    getLivresDisponibles() {

        return livres.stream()
                .filter(Livre::estDisponible)
                .collect(Collectors.toList());
    }

    public synchronized List<Livre>
    getLivresEnReparation() {

        return livres.stream()
                .filter(l ->
                        l.getStatut()
                                == StatutLivre.A_REPARER
                )
                .collect(Collectors.toList());
    }

    public synchronized Optional<LocalDate>
    getDateRetourPlusProche(
            String isbn
    ) {

        return emprunts.stream()
                .filter(e ->
                        e.getLivre()
                                .getIsbn()
                                .equals(isbn)
                )
                .map(
                        Emprunt::getDateRetourPrevue
                )
                .min(LocalDate::compareTo);
    }

    // =====================================================
    // STATISTIQUES
    // =====================================================

    public synchronized Map<TypeUsager, Long>
    getStatistiquesEmpruntsParType() {

        return emprunts.stream()
                .collect(
                        Collectors.groupingBy(
                                e ->
                                        e.getUsager()
                                                .getType(),
                                Collectors.counting()
                        )
                );
    }

    // =====================================================
    // RETARDS
    // =====================================================

    public synchronized List<Emprunt>
    getEmpruntsEnRetard() {

        return emprunts.stream()
                .filter(e ->
                        e.getDateRetourPrevue()
                                .isBefore(dateCourante)
                )
                .sorted(
                        Comparator.comparing(
                                Emprunt::getDateRetourPrevue
                        )
                )
                .collect(Collectors.toList());
    }

    public synchronized List<Emprunt>
    getAllEmprunts() {

        return new ArrayList<>(emprunts);
    }

    // =====================================================
    // DATE
    // =====================================================

    public synchronized void avancerDate(
            int jours
    ) {

        for (int i = 0; i < jours; i++) {

            dateCourante =
                    dateCourante.plusDays(1);

            verifierReparations();
        }
    }

    private synchronized void verifierReparations() {

        livres.stream()
                .filter(l ->
                        l.getStatut()
                                == StatutLivre.A_REPARER
                )
                .filter(l ->
                        l.getDateDisponibilite()
                                != null
                )
                .filter(l ->
                        !l.getDateDisponibilite()
                                .isAfter(dateCourante)
                )
                .forEach(l -> {

                    l.setStatut(
                            StatutLivre.DISPONIBLE
                    );

                    if (
                            l.getEtatPhysique()
                                    == EtatPhysique.A_REPARER
                    ) {

                        l.setEtatPhysique(
                                EtatPhysique.USE
                        );
                    }

                    l.setDateDisponibilite(
                            null
                    );
                });
    }

    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public int getCompteurGlobalEmprunts() {

        return compteurGlobalEmprunts.get();
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

    // =====================================================
    // HELPERS
    // =====================================================

    private Usager trouverUsager(
            String id
    ) {

        return usagers.stream()
                .filter(u ->
                        u.getId().equals(id)
                )
                .findFirst()
                .orElse(null);
    }

    private Livre trouverLivre(
            String id
    ) {

        return livres.stream()
                .filter(l ->
                        l.getIdExemplaire()
                                .equals(id)
                )
                .findFirst()
                .orElse(null);
    }

    private boolean estWeekend(
            LocalDate date
    ) {

        return date.getDayOfWeek()
                == DayOfWeek.SATURDAY
                ||
                date.getDayOfWeek()
                        == DayOfWeek.SUNDAY;
    }

    // =====================================================
    // WRAPPERS UI
    // =====================================================

    public static class EmpruntResult {

        private final boolean succes;
        private final String message;

        public EmpruntResult(
                boolean succes,
                String message
        ) {

            this.succes = succes;
            this.message = message;
        }

        public boolean isSucces() {
            return succes;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class RetourResult {

        private final boolean succes;
        private final String message;

        public RetourResult(
                boolean succes,
                String message
        ) {

            this.succes = succes;
            this.message = message;
        }

        public boolean isSucces() {
            return succes;
        }

        public String getMessage() {
            return message;
        }
    }
}