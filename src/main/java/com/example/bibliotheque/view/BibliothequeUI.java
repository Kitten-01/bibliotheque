package com.example.bibliotheque.view;

import com.bibliotheque.controller.BibliothequeController;
import com.bibliotheque.model.*;
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
    private TextArea affichageZone;
    private Label compteurLabel;
    private Label dateLabel;
    private ListView<Livre> livresListView;
    private ListView<Usager> usagersListView;
    
    @Override
    public void start(Stage primaryStage) {
        controller = new BibliothequeController();
        
        // Créer l'interface principale
        BorderPane root = new BorderPane();
        
        // Menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        
        // Centre - onglets
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
            createLivresTab(),
            createUsagersTab(),
            createEmpruntsTab(),
            createRechercheTab(),
            createStatistiquesTab()
        );
        root.setCenter(tabPane);
        
        // Bas - status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
        
        // Scene
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Système de Gestion de Bibliothèque - Cégep");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            controller.sauvegarder();
            Platform.exit();
        });
        primaryStage.show();
        
        actualiserAffichage();
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // Menu Fichier
        Menu fichierMenu = new Menu("Fichier");
        MenuItem importerMarc = new MenuItem("Importer fichier MARC");
        importerMarc.setOnAction(e -> importerFichierMarc());
        MenuItem sauvegarder = new MenuItem("Sauvegarder");
        sauvegarder.setOnAction(e -> controller.sauvegarder());
        MenuItem quitter = new MenuItem("Quitter");
        quitter.setOnAction(e -> Platform.exit());
        fichierMenu.getItems().addAll(importerMarc, new SeparatorMenuItem(), sauvegarder, quitter);
        
        // Menu Aide
        Menu aideMenu = new Menu("Aide");
        MenuItem aPropos = new MenuItem("À propos");
        aPropos.setOnAction(e -> afficherAPropos());
        aideMenu.getItems().add(aPropos);
        
        menuBar.getMenus().addAll(fichierMenu, aideMenu);
        return menuBar;
    }
    
    private Tab createLivresTab() {
        Tab tab = new Tab("Livres");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Boutons
        HBox buttonBar = new HBox(10);
        Button ajouterBtn = new Button("Ajouter Livre");
        Button modifierBtn = new Button("Modifier Livre");
        Button supprimerBtn = new Button("Supprimer Livre");
        Button rafraichirBtn = new Button("Rafraîchir");
        
        ajouterBtn.setOnAction(e -> ajouterLivre());
        modifierBtn.setOnAction(e -> modifierLivre());
        supprimerBtn.setOnAction(e -> supprimerLivre());
        rafraichirBtn.setOnAction(e -> actualiserAffichage());
        
        buttonBar.getChildren().addAll(ajouterBtn, modifierBtn, supprimerBtn, rafraichirBtn);
        
        // Liste des livres
        livresListView = new ListView<>();
        livresListView.setCellFactory(lv -> new ListCell<Livre>() {
            @Override
            protected void updateItem(Livre item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (%s) [%s]",
                        item.getTitre(), item.getAuteur(), item.getIsbn(), item.getStatut()));
                }
            }
        });
        
        content.getChildren().addAll(buttonBar, livresListView);
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createUsagersTab() {
        Tab tab = new Tab("Usagers");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Boutons
        HBox buttonBar = new HBox(10);
        Button ajouterBtn = new Button("Ajouter Usager");
        Button modifierBtn = new Button("Modifier Usager");
        Button supprimerBtn = new Button("Supprimer Usager");
        Button rafraichirBtn = new Button("Rafraîchir");
        
        ajouterBtn.setOnAction(e -> ajouterUsager());
        modifierBtn.setOnAction(e -> modifierUsager());
        supprimerBtn.setOnAction(e -> supprimerUsager());
        rafraichirBtn.setOnAction(e -> actualiserAffichage());
        
        buttonBar.getChildren().addAll(ajouterBtn, modifierBtn, supprimerBtn, rafraichirBtn);
        
        // Liste des usagers
        usagersListView = new ListView<>();
        usagersListView.setCellFactory(lv -> new ListCell<Usager>() {
            @Override
            protected void updateItem(Usager item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        
        content.getChildren().addAll(buttonBar, usagersListView);
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createEmpruntsTab() {
        Tab tab = new Tab("Emprunts/Retours");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Emprunt
        TitledPane empruntPane = new TitledPane("Emprunter un livre", createEmpruntPanel());
        empruntPane.setExpanded(true);
        
        // Retour
        TitledPane retourPane = new TitledPane("Retourner un livre", createRetourPanel());
        retourPane.setExpanded(true);
        
        // Emprunts en retard
        TitledPane retardPane = new TitledPane("Emprunts en retard", createRetardPanel());
        retardPane.setExpanded(true);
        
        content.getChildren().addAll(empruntPane, retourPane, retardPane);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    private GridPane createEmpruntPanel() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        ComboBox<Usager> usagerCombo = new ComboBox<>();
        ComboBox<Livre> livreCombo = new ComboBox<>();
        Button emprunterBtn = new Button("Emprunter");
        Label resultatLabel = new Label();
        
        usagerCombo.setPromptText("Choisir un usager");
        livreCombo.setPromptText("Choisir un livre disponible");
        
        emprunterBtn.setOnAction(e -> {
            Usager usager = usagerCombo.getValue();
            Livre livre = livreCombo.getValue();
            if (usager != null && livre != null) {
                BibliothequeController.EmpruntResultWrapper result = controller.emprunterLivre(usager.getId(), livre.getIdExemplaire());
                if (result.isSucces()) {
                    resultatLabel.setText("✓ " + result.getMessage());
                    actualiserAffichage();
                } else {
                    resultatLabel.setText("✗ " + result.getMessage());
                }
            } else {
                resultatLabel.setText("Veuillez sélectionner un usager et un livre");
            }
        });
        
        grid.add(new Label("Usager:"), 0, 0);
        grid.add(usagerCombo, 1, 0);
        grid.add(new Label("Livre:"), 0, 1);
        grid.add(livreCombo, 1, 1);
        grid.add(emprunterBtn, 1, 2);
        grid.add(resultatLabel, 1, 3);
        
        // Rafraîchir les listes
        usagerCombo.setItems(FXCollections.observableArrayList(controller.getAllUsagers()));
        livreCombo.setItems(FXCollections.observableArrayList(controller.getLivresDisponibles()));
        
        return grid;
    }
    
    private GridPane createRetourPanel() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        ComboBox<Livre> livreCombo = new ComboBox<>();
        ComboBox<EtatPhysique> etatCombo = new ComboBox<>(FXCollections.observableArrayList(EtatPhysique.values()));
        Button retournerBtn = new Button("Retourner");
        Label resultatLabel = new Label();
        
        livreCombo.setPromptText("Choisir un livre emprunté");
        etatCombo.setValue(EtatPhysique.BON);
        
        retournerBtn.setOnAction(e -> {
            Livre livre = livreCombo.getValue();
            EtatPhysique nouvelEtat = etatCombo.getValue();
            if (livre != null && nouvelEtat != null) {
                BibliothequeController.RetourResultWrapper result = controller.retournerLivre(livre.getIdExemplaire(), nouvelEtat);
                if (result.isSucces()) {
                    resultatLabel.setText("✓ " + result.getMessage());
                    actualiserAffichage();
                } else {
                    resultatLabel.setText("✗ " + result.getMessage());
                }
            } else {
                resultatLabel.setText("Veuillez sélectionner un livre");
            }
        });
        
        grid.add(new Label("Livre emprunté:"), 0, 0);
        grid.add(livreCombo, 1, 0);
        grid.add(new Label("Nouvel état:"), 0, 1);
        grid.add(etatCombo, 1, 1);
        grid.add(retournerBtn, 1, 2);
        grid.add(resultatLabel, 1, 3);
        
        return grid;
    }
    
    private VBox createRetardPanel() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        ListView<String> retardListView = new ListView<>();
        Button rafraichirBtn = new Button("Rafraîchir");
        
        rafraichirBtn.setOnAction(e -> {
            retardListView.getItems().clear();
            controller.getEmpruntsEnRetard().forEach(emprunt -> {
                retardListView.getItems().add(
                    String.format("%s - %s (retard de %d jours)",
                        emprunt.getLivre().getTitre(),
                        emprunt.getUsager().getNom(),
                        controller.getJoursRetard(emprunt))
                );
            });
        });
        
        content.getChildren().addAll(rafraichirBtn, retardListView);
        return content;
    }
    
    private Tab createRechercheTab() {
        Tab tab = new Tab("Recherche");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Choix du type de recherche
        ToggleGroup searchGroup = new ToggleGroup();
        RadioButton titreRadio = new RadioButton("Par titre");
        RadioButton auteurRadio = new RadioButton("Par auteur");
        RadioButton isbnRadio = new RadioButton("Par ISBN");
        titreRadio.setToggleGroup(searchGroup);
        auteurRadio.setToggleGroup(searchGroup);
        isbnRadio.setToggleGroup(searchGroup);
        titreRadio.setSelected(true);
        
        HBox searchTypeBox = new HBox(10);
        searchTypeBox.getChildren().addAll(titreRadio, auteurRadio, isbnRadio);
        
        // Champ de recherche
        TextField searchField = new TextField();
        searchField.setPromptText("Terme de recherche...");
        
        Button rechercherBtn = new Button("Rechercher");
        
        // Résultats
        ListView<String> resultatsListView = new ListView<>();
        
        rechercherBtn.setOnAction(e -> {
            String terme = searchField.getText();
            if (terme == null || terme.trim().isEmpty()) return;
            
            resultatsListView.getItems().clear();
            
            if (titreRadio.isSelected()) {
                controller.rechercherParTitre(terme).forEach(livre -> {
                    resultatsListView.getItems().add(formatLivreResultat(livre));
                });
            } else if (auteurRadio.isSelected()) {
                controller.rechercherParAuteur(terme).forEach(livre -> {
                    resultatsListView.getItems().add(formatLivreResultat(livre));
                });
            } else {
                controller.rechercherParIsbn(terme).forEach(livre -> {
                    resultatsListView.getItems().add(formatLivreResultat(livre));
                });
            }
        });
        
        content.getChildren().addAll(searchTypeBox, searchField, rechercherBtn, resultatsListView);
        tab.setContent(content);
        
        return tab;
    }
    
    private Tab createStatistiquesTab() {
        Tab tab = new Tab("Statistiques");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        TextArea statsArea = new TextArea();
        statsArea.setEditable(false);
        statsArea.setPrefRowCount(20);
        
        Button actualiserBtn = new Button("Actualiser les statistiques");
        actualiserBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("=== STATISTIQUES DE LA BIBLIOTHÈQUE ===\n\n");
            sb.append("Compteur global d'emprunts: ").append(controller.getCompteurGlobalEmprunts()).append("\n\n");
            sb.append("Emprunts par type d'usager:\n");
            controller.getStatistiquesEmpruntsParType().forEach((type, count) -> {
                sb.append("  - ").append(type).append(": ").append(count).append(" emprunts\n");
            });
            sb.append("\nTotal des livres: ").append(controller.getAllLivres().size()).append("\n");
            sb.append("Livres disponibles: ").append(controller.getLivresDisponibles().size()).append("\n");
            sb.append("Livres en réparation: ").append(controller.getLivresEnReparation().size()).append("\n");
            sb.append("\nDate courante: ").append(controller.getDateCourante().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            
            statsArea.setText(sb.toString());
        });
        
        content.getChildren().addAll(actualiserBtn, statsArea);
        tab.setContent(content);
        
        return tab;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #e0e0e0;");
        
        compteurLabel = new Label();
        dateLabel = new Label();
        
        statusBar.getChildren().addAll(compteurLabel, dateLabel);
        
        return statusBar;
    }
    
    private void actualiserAffichage() {
        if (livresListView != null) {
            livresListView.setItems(FXCollections.observableArrayList(controller.getAllLivres()));
        }
        if (usagersListView != null) {
            usagersListView.setItems(FXCollections.observableArrayList(controller.getAllUsagers()));
        }
        if (compteurLabel != null) {
            compteurLabel.setText("Emprunts totaux: " + controller.getCompteurGlobalEmprunts());
        }
        if (dateLabel != null) {
            dateLabel.setText("Date: " + controller.getDateCourante().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }
    
    private void ajouterLivre() {
        // Dialogue d'ajout de livre
        Dialog<Livre> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un livre");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField titreField = new TextField();
        TextField auteurField = new TextField();
        TextField isbnField = new TextField();
        ComboBox<EtatPhysique> etatCombo = new ComboBox<>(FXCollections.observableArrayList(EtatPhysique.values()));
        etatCombo.setValue(EtatPhysique.NEUF);
        
        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(new Label("Auteur:"), 0, 1);
        grid.add(auteurField, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        grid.add(new Label("État initial:"), 0, 3);
        grid.add(etatCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new Livre(titreField.getText(), auteurField.getText(), 
                               isbnField.getText(), etatCombo.getValue());
            }
            return null;
        });
        
        Optional<Livre> result = dialog.showAndWait();
        result.ifPresent(livre -> {
            controller.ajouterLivre(livre);
            actualiserAffichage();
        });
    }
    
    private void modifierLivre() {
        Livre selected = livresListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un livre à modifier");
            return;
        }
        
        Dialog<Livre> dialog = new Dialog<>();
        dialog.setTitle("Modifier le livre");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField titreField = new TextField(selected.getTitre());
        TextField auteurField = new TextField(selected.getAuteur());
        TextField isbnField = new TextField(selected.getIsbn());
        ComboBox<EtatPhysique> etatCombo = new ComboBox<>(FXCollections.observableArrayList(EtatPhysique.values()));
        etatCombo.setValue(selected.getEtatPhysique());
        
        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(new Label("Auteur:"), 0, 1);
        grid.add(auteurField, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        grid.add(new Label("État:"), 0, 3);
        grid.add(etatCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                selected.setTitre(titreField.getText());
                selected.setAuteur(auteurField.getText());
                selected.setIsbn(isbnField.getText());
                selected.setEtatPhysique(etatCombo.getValue());
                return selected;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(livre -> {
            controller.modifierLivre(livre);
            actualiserAffichage();
        });
    }
    
    private void supprimerLivre() {
        Livre selected = livresListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un livre à supprimer");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce livre ?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.supprimerLivre(selected.getIdExemplaire());
                actualiserAffichage();
            }
        });
    }
    
    private void ajouterUsager() {
        Dialog<Usager> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un usager");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nomField = new TextField();
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("Étudiant", "Professeur", "Visiteur"));
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK && !nomField.getText().isEmpty()) {
                String type = typeCombo.getValue();
                if ("Étudiant".equals(type)) {
                    return new Etudiant(nomField.getText());
                } else if ("Professeur".equals(type)) {
                    return new Professeur(nomField.getText());
                } else {
                    return new Visiteur(nomField.getText());
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(usager -> {
            controller.ajouterUsager(usager);
            actualiserAffichage();
        });
    }
    
    private void modifierUsager() {
        Usager selected = usagersListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un usager à modifier");
            return;
        }
        
        Dialog<Usager> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'usager");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nomField = new TextField(selected.getNom());
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                selected.setNom(nomField.getText());
                return selected;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(usager -> {
            controller.modifierUsager(usager);
            actualiserAffichage();
        });
    }
    
    private void supprimerUsager() {
        Usager selected = usagersListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un usager à supprimer");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet usager ?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    controller.supprimerUsager(selected.getId());
                    actualiserAffichage();
                } catch (IllegalStateException e) {
                    showAlert("Erreur", e.getMessage());
                }
            }
        });
    }
    
    private void importerFichierMarc() {
        // Simplifié - dans une vraie implémentation, utiliser FileChooser
        showAlert("Information", "La fonction d'import MARC nécessite un sélecteur de fichier");
    }
    
    private void afficherAPropos() {
        showAlert("À propos", "Système de Gestion de Bibliothèque\nVersion 1.0\n© Cégep");
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private String formatLivreResultat(Livre livre) {
        String disponibilite;
        if (livre.estDisponible()) {
            disponibilite = "Disponible";
        } else {
            Optional<LocalDate> dateRetour = controller.getDateRetourPlusProche(livre.getIsbn());
            disponibilite = dateRetour.map(d -> "Retour prévu: " + d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                      .orElse("Non disponible");
        }
        return String.format("%s - %s (%s) - %s", livre.getTitre(), livre.getAuteur(), livre.getIsbn(), disponibilite);
    }
}