package com.example.bibliotheque.service;

import com.bibliotheque.model.EtatPhysique;
import com.bibliotheque.model.Livre;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarcImporter {
    private static final Random random = new Random();
    
    // Distribution des exemplaires
    private static final int[] EXEMPLAIRES_DISTRIBUTION = {1, 2, 3, 4};
    private static final double[] PROBABILITES = {0.50, 0.30, 0.15, 0.05};
    
    public List<Livre> importerFichierMarc(Path cheminFichier) throws IOException {
        List<Livre> livres = new ArrayList<>();
        String contenu = Files.readString(cheminFichier);
        
        // Parser manuel du format ISO 2709 simplifié
        // Chaque notice commence par un leader de 24 caractères
        String[] notices = contenu.split("(?=\\d{5}cam)");
        
        for (String notice : notices) {
            if (notice.length() < 100) continue;
            
            Optional<Livre> livreOpt = extraireLivreDeNotice(notice);
            if (livreOpt.isPresent()) {
                Livre livre = livreOpt.get();
                int nbExemplaires = getNombreExemplaires();
                
                // Créer les exemplaires
                for (int i = 0; i < nbExemplaires; i++) {
                    EtatPhysique etatInitial = i == 0 ? EtatPhysique.NEUF : EtatPhysique.BON;
                    Livre exemplaire = new Livre(
                        livre.getTitre(),
                        livre.getAuteur(),
                        livre.getIsbn(),
                        etatInitial
                    );
                    livres.add(exemplaire);
                }
            }
        }
        
        return livres;
    }
    
    private Optional<Livre> extraireLivreDeNotice(String notice) {
        String titre = extraireChamp(notice, "245");
        String auteur = extraireChamp(notice, "100");
        if (auteur == null || auteur.isEmpty()) {
            auteur = extraireChamp(notice, "700");
        }
        String isbn = extraireChamp(notice, "020");
        
        if (titre != null && !titre.isEmpty() && auteur != null && !auteur.isEmpty()) {
            // Nettoyer le titre (enlever les sous-champs)
            titre = nettoyerChamp(titre);
            auteur = nettoyerChamp(auteur);
            isbn = nettoyerIsbn(isbn);
            
            return Optional.of(new Livre(titre, auteur, isbn, EtatPhysique.NEUF));
        }
        
        return Optional.empty();
    }
    
    private String extraireChamp(String notice, String codeChamp) {
        // Recherche du champ dans la notice
        Pattern pattern = Pattern.compile(codeChamp + "\\s+[0-9a-f]{2}[^\\x00-\\x1F]*?\\x1E");
        Matcher matcher = pattern.matcher(notice);
        
        if (matcher.find()) {
            String champ = matcher.group();
            // Extraire le contenu après le code et les indicateurs
            int debut = codeChamp.length() + 3; // code + 2 indicateurs + 1 séparateur
            if (debut < champ.length()) {
                return champ.substring(debut, champ.length() - 1);
            }
        }
        return null;
    }
    
    private String nettoyerChamp(String champ) {
        if (champ == null) return null;
        // Enlever les sous-champs (ex: $a, $b, etc.)
        return champ.replaceAll("\\$[a-z]\\s*", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
    }
    
    private String nettoyerIsbn(String isbn) {
        if (isbn == null) return "ISBN-INCONNU";
        // Nettoyer l'ISBN (enlever les tirets et espaces)
        isbn = isbn.replaceAll("[\\s-]", "");
        if (isbn.matches("\\d{10}|\\d{13}")) {
            return isbn;
        }
        return "ISBN-INCONNU-" + System.currentTimeMillis();
    }
    
    private int getNombreExemplaires() {
        double r = random.nextDouble();
        double cumul = 0;
        for (int i = 0; i < PROBABILITES.length; i++) {
            cumul += PROBABILITES[i];
            if (r <= cumul) {
                return EXEMPLAIRES_DISTRIBUTION[i];
            }
        }
        return 1;
    }
}
