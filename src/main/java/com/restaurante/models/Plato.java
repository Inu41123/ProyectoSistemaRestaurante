package com.restaurante.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class Plato {
    private int id;
    private String nombre;
    private double precio;

    public StringProperty nombreProperty() {
        return new SimpleStringProperty(nombre);
    }

    public DoubleProperty precioProperty() {
        return new SimpleDoubleProperty(precio);
    }
}