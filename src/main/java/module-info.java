module com.example.bibliotheque {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bibliotheque to javafx.fxml;
    exports com.example.bibliotheque;
}