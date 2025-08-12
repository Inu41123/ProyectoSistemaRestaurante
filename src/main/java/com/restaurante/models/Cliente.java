package com.restaurante.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class Cliente {
    private int id;
    private String nombre;
    private String correo;

    public StringProperty nombreProperty() {
        return new SimpleStringProperty(nombre);
    }

    public StringProperty correoProperty() {
        return new SimpleStringProperty(correo);
    }
}