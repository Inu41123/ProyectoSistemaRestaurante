package com.restaurante.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;

@Data
public class ItemPedido {
    private Plato plato;
    private int cantidad;

    public ObjectProperty<Integer> cantidadProperty() {
        return new SimpleObjectProperty<>(cantidad);
    }
}