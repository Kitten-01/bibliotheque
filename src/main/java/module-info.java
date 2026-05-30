module com.example.bibliotheque {

    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Gson
    requires com.google.gson;

    // Exports
    exports com.example.bibliotheque;
    exports com.example.bibliotheque.model;
    exports com.example.bibliotheque.service;
    exports com.example.bibliotheque.controller;
    exports com.example.bibliotheque.view;

    // JavaFX opens
    opens com.example.bibliotheque to javafx.fxml;
    opens com.example.bibliotheque.controller to javafx.fxml;
    opens com.example.bibliotheque.view to javafx.fxml;

    // Gson opens
    opens com.example.bibliotheque.model to com.google.gson;
    opens com.example.bibliotheque.service to com.google.gson;
}