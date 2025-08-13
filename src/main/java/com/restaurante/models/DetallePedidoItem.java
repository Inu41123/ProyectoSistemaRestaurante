package com.restaurante.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class DetallePedidoItem {
    private final IntegerProperty pedidoId = new SimpleIntegerProperty();
    private final StringProperty cliente = new SimpleStringProperty();
    private final StringProperty plato = new SimpleStringProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();
    private final DoubleProperty subtotal = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDateTime> fecha = new SimpleObjectProperty<>();

    public DetallePedidoItem(int pedidoId, String cliente, String plato, int cantidad, double subtotal, LocalDateTime fecha) {
        this.pedidoId.set(pedidoId);
        this.cliente.set(cliente);
        this.plato.set(plato);
        this.cantidad.set(cantidad);
        this.subtotal.set(subtotal);
        this.fecha.set(fecha);
    }

    public IntegerProperty pedidoIdProperty() { return pedidoId; }
    public StringProperty clienteProperty() { return cliente; }
    public StringProperty platoProperty() { return plato; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public DoubleProperty subtotalProperty() { return subtotal; }
    public ObjectProperty<LocalDateTime> fechaProperty() { return fecha; }
}