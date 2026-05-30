package com.example.bibliotheque.service;

import com.example.bibliotheque.model.EtatPhysique;
import com.example.bibliotheque.model.Livre;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarcImporter {

    private static final int[] EXEMPLAIRES_DISTRIBUTION = {
            1,
            2,
            3,
            4
    };

    private static final double[] PROBABILITES = {
            0.50,
            0.30,
            0.15,
            0.05
    };

    public List<Livre> importerFichierMarc(Path cheminFichier)
            throws IOException {

        List<Livre> livres = new ArrayList<>();

        String contenu = Files.readString(cheminFichier);

        String[] notices =
                contenu.split("(?=\\d{5}[a-z]{3})");

        for (String notice : notices) {

            if (notice.length() < 100) {
                continue;
            }

            Optional<Livre> livreOpt =
                    extraireLivreDeNotice(notice);

            if (livreOpt.isPresent()) {

                Livre livre = livreOpt.get();

                int nbExemplaires =
                        getNombreExemplaires();

                for (int i = 0; i < nbExemplaires; i++) {

                    EtatPhysique etat =
                            (i == 0)
                                    ? EtatPhysique.NEUF
                                    : EtatPhysique.BON;

                    Livre exemplaire = new Livre(
                            livre.getTitre(),
                            livre.getAuteur(),
                            livre.getIsbn(),
                            etat
                    );

                    livres.add(exemplaire);
                }
            }
        }

        return livres;
    }

    private Optional<Livre> extraireLivreDeNotice(
            String notice
    ) {

        String titre =
                nettoyerChamp(
                        extraireChamp(notice, "245")
                );

        String auteur =
                nettoyerChamp(
                        extraireChamp(notice, "100")
                );

        if (auteur == null || auteur.isBlank()) {

            auteur =
                    nettoyerChamp(
                            extraireChamp(notice, "700")
                    );
        }

        String isbn =
                nettoyerIsbn(
                        extraireChamp(notice, "020")
                );

        if (titre != null
                && !titre.isBlank()
                && auteur != null
                && !auteur.isBlank()) {

            return Optional.of(
                    new Livre(
                            titre,
                            auteur,
                            isbn,
                            EtatPhysique.NEUF
                    )
            );
        }

        return Optional.empty();
    }

    private String extraireChamp(
            String notice,
            String codeChamp
    ) {

        Pattern pattern = Pattern.compile(
                codeChamp + "[^\\x1E]*\\x1E"
        );

        Matcher matcher = pattern.matcher(notice);

        if (matcher.find()) {

            String champ = matcher.group();

            int debut = Math.min(champ.length(), 6);

            return champ.substring(
                    debut,
                    champ.length() - 1
            );
        }

        return null;
    }

    private String nettoyerChamp(String champ) {

        if (champ == null) {
            return null;
        }

        return champ.replaceAll("\\$[a-z]\\s*", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String nettoyerIsbn(String isbn) {

        if (isbn == null) {

            return "ISBN-INCONNU-"
                    + UUID.randomUUID();
        }

        isbn = isbn.replaceAll("[\\s-]", "");

        if (isbn.matches("\\d{10}|\\d{13}")) {
            return isbn;
        }

        return "ISBN-INCONNU-"
                + UUID.randomUUID();
    }

    private int getNombreExemplaires() {

        double r =
                ThreadLocalRandom.current().nextDouble();

        double cumul = 0;

        for (int i = 0;
             i < PROBABILITES.length;
             i++) {

            cumul += PROBABILITES[i];

            if (r <= cumul) {
                return EXEMPLAIRES_DISTRIBUTION[i];
            }
        }

        return 1;
    }
}
