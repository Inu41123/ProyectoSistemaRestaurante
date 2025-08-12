module com.restaurante {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.naming;
    requires org.controlsfx.controls;    // Nombre correcto para ControlsFX
    requires net.synedra.validatorfx;    // Nombre correcto para ValidatorFX
    requires org.kordamp.ikonli.javafx;  // Nombre correcto para Ikonli
    requires static lombok;

    opens com.restaurante to javafx.graphics;
    opens com.restaurante.controllers to javafx.fxml;
    opens com.restaurante.models to javafx.base;

    exports com.restaurante;
    exports com.restaurante.controllers;
}