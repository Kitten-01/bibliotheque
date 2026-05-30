package com.example.bibliotheque.view;

import com.example.bibliotheque.controller.BibliothequeController;
import com.example.bibliotheque.model.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BibliothequeUI extends Application {

    private BibliothequeController controller;

    private Label compteurLabel;
    private Label dateLabel;

    private ListView<Livre> livresListView;
    private ListView<Usager> usagersListView;

    @Override
    public void start(Stage primaryStage) {

        controller = new BibliothequeController();

        BorderPane root = new BorderPane();

        root.setTop(createMenuBar());
        root.setCenter(createTabs());
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setTitle("Système de Gestion de Bibliothèque");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> {
            controller.sauvegarder();
            Platform.exit();
        });

        primaryStage.show();

        actualiserAffichage();
    }

    // ================= MENU =================

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fichier = new Menu("Fichier");

        MenuItem sauvegarder = new MenuItem("Sauvegarder");
        sauvegarder.setOnAction(e -> controller.sauvegarder());

        MenuItem quitter = new MenuItem("Quitter");
        quitter.setOnAction(e -> Platform.exit());

        fichier.getItems().addAll(sauvegarder, new SeparatorMenuItem(), quitter);

        Menu aide = new Menu("Aide");
        MenuItem apropos = new MenuItem("À propos");
        apropos.setOnAction(e ->
                showAlert("À propos", "Bibliothèque Cégep - Version JavaFX")
        );
        aide.getItems().add(apropos);

        menuBar.getMenus().addAll(fichier, aide);

        return menuBar;
    }

    // ================= TABS =================

    private TabPane createTabs() {

        TabPane tabPane = new TabPane();

        tabPane.getTabs().addAll(
                createLivresTab(),
                createUsagersTab(),
                createEmpruntsTab(),
                createRechercheTab(),
                createStatistiquesTab()
        );

        return tabPane;
    }

    // ================= LIVRES =================

    private Tab createLivresTab() {

        Tab tab = new Tab("Livres");

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        HBox buttons = new HBox(10);

        Button add = new Button("Ajouter");
        Button del = new Button("Supprimer");
        Button refresh = new Button("Rafraîchir");

        add.setOnAction(e -> ajouterLivre());
        del.setOnAction(e -> supprimerLivre());
        refresh.setOnAction(e -> actualiserAffichage());

        buttons.getChildren().addAll(add, del, refresh);

        livresListView = new ListView<>();

        livresListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Livre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : item.getTitre() + " | " +
                        item.getAuteur() + " | " +
                        item.getStatut());
            }
        });

        box.getChildren().addAll(buttons, livresListView);

        tab.setContent(box);

        return tab;
    }

    // ================= USAGERS =================

    private Tab createUsagersTab() {

        Tab tab = new Tab("Usagers");

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        usagersListView = new ListView<>();

        usagersListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Usager item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        box.getChildren().add(usagersListView);

        tab.setContent(box);

        return tab;
    }

    // ================= EMPRUNTS =================

    private Tab createEmpruntsTab() {

        Tab tab = new Tab("Emprunts");

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label label = new Label("Module emprunts à connecter au controller");

        box.getChildren().add(label);

        tab.setContent(box);

        return tab;
    }

    // ================= RECHERCHE =================

    private Tab createRechercheTab() {

        Tab tab = new Tab("Recherche");

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Recherche...");

        ListView<String> resultats = new ListView<>();

        searchField.setOnAction(e -> {

            String query = searchField.getText();

            resultats.getItems().clear();

            controller.rechercherParTitre(query)
                    .forEach(livre ->
                            resultats.getItems().add(
                                    livre.getTitre() + " | " + livre.getAuteur()
                            )
                    );
        });

        box.getChildren().addAll(searchField, resultats);

        tab.setContent(box);

        return tab;
    }

    // ================= STATISTIQUES =================

    private Tab createStatistiquesTab() {

        Tab tab = new Tab("Stats");

        TextArea area = new TextArea();
        area.setEditable(false);

        Button refresh = new Button("Actualiser");

        refresh.setOnAction(e -> {

            StringBuilder sb = new StringBuilder();

            sb.append("Emprunts totaux: ")
                    .append(controller.getCompteurGlobalEmprunts())
                    .append("\n\n");

            sb.append("Livres: ")
                    .append(controller.getAllLivres().size())
                    .append("\n");

            sb.append("Usagers: ")
                    .append(controller.getAllUsagers().size())
                    .append("\n");

            area.setText(sb.toString());
        });

        VBox box = new VBox(10, refresh, area);
        box.setPadding(new Insets(10));

        tab.setContent(box);

        return tab;
    }

    // ================= STATUS BAR =================

    private HBox createStatusBar() {

        HBox bar = new HBox(20);
        bar.setPadding(new Insets(5));

        compteurLabel = new Label();
        dateLabel = new Label();

        bar.getChildren().addAll(compteurLabel, dateLabel);

        return bar;
    }

    // ================= UPDATE =================

    private void actualiserAffichage() {

        if (livresListView != null) {
            livresListView.setItems(
                    FXCollections.observableArrayList(controller.getAllLivres())
            );
        }

        if (usagersListView != null) {
            usagersListView.setItems(
                    FXCollections.observableArrayList(controller.getAllUsagers())
            );
        }

        if (compteurLabel != null) {
            compteurLabel.setText(
                    "Emprunts: " + controller.getCompteurGlobalEmprunts()
            );
        }

        if (dateLabel != null) {
            dateLabel.setText(
                    "Date: " + controller.getDateCourante()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        }
    }

    // ================= ACTIONS =================

    private void ajouterLivre() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Titre du livre");

        dialog.showAndWait().ifPresent(titre -> {

            Livre livre = new Livre(
                    titre,
                    "Auteur",
                    "ISBN-" + System.currentTimeMillis(),
                    EtatPhysique.NEUF
            );

            controller.ajouterLivre(livre);
            actualiserAffichage();
        });
    }

    private void supprimerLivre() {

        Livre selected = livresListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            controller.supprimerLivre(selected.getIdExemplaire());
            actualiserAffichage();
        }
    }

    private void showAlert(String title, String msg) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}