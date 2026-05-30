package com.example.bibliotheque.service;

import com.example.bibliotheque.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDate;

public class PersistanceService {

    private static final String FILE_NAME =
            "bibliotheque.json";

    private final Gson gson;

    private final Object lock =
            new Object();

    public PersistanceService() {

        this.gson = new GsonBuilder()

                .setPrettyPrinting()

                .registerTypeAdapter(
                        LocalDate.class,

                        (JsonSerializer<LocalDate>)
                                (src, type, ctx) ->
                                        new JsonPrimitive(
                                                src.toString()
                                        )
                )

                .registerTypeAdapter(
                        LocalDate.class,

                        (JsonDeserializer<LocalDate>)
                                (json, type, ctx) ->
                                        LocalDate.parse(
                                                json.getAsString()
                                        )
                )

                .create();
    }

    // =====================================================
    // SAUVEGARDE
    // =====================================================

    public void sauvegarder(
            Bibliotheque bibliotheque
    ) throws IOException {

        synchronized (lock) {

            BibliothequeData data =
                    new BibliothequeData();

            data.setLivres(
                    bibliotheque.getAllLivres()
            );

            data.setUsagers(
                    bibliotheque.getAllUsagers()
            );

            data.setEmprunts(
                    bibliotheque.getAllEmprunts()
            );

            data.setCompteurEmprunts(
                    bibliotheque
                            .getCompteurGlobalEmprunts()
            );

            data.setDateCourante(
                    bibliotheque.getDateCourante()
            );

            try (Writer writer =
                         new FileWriter(FILE_NAME)) {

                gson.toJson(data, writer);
            }
        }
    }

    // =====================================================
    // CHARGEMENT
    // =====================================================

    public Bibliotheque charger()
            throws IOException {

        synchronized (lock) {

            File file =
                    new File(FILE_NAME);

            if (!file.exists()) {
                return new Bibliotheque();
            }

            try (Reader reader =
                         new FileReader(file)) {

                Type type =
                        new TypeToken<BibliothequeData>() {
                        }.getType();

                BibliothequeData data =
                        gson.fromJson(reader, type);

                Bibliotheque bibliotheque =
                        new Bibliotheque();

                if (data == null) {
                    return bibliotheque;
                }

                // ================= LIVRES =================

                if (data.getLivres() != null) {

                    data.getLivres()
                            .forEach(
                                    bibliotheque::ajouterLivre
                            );
                }

                // ================= USAGERS =================

                if (data.getUsagers() != null) {

                    data.getUsagers()
                            .forEach(
                                    bibliotheque::ajouterUsager
                            );
                }

                // ================= EMPRUNTS =================

                if (data.getEmprunts() != null) {

                    data.getEmprunts()
                            .forEach(
                                    bibliotheque::forcerAjoutEmprunt
                            );
                }

                // ================= DATE =================

                if (data.getDateCourante() != null) {

                    while (
                            bibliotheque.getDateCourante()
                                    .isBefore(
                                            data.getDateCourante()
                                    )
                    ) {

                        bibliotheque.avancerDate(1);
                    }
                }

                return bibliotheque;
            }
        }
    }
}