package com.restaurante.models;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Pedido {
    private int id;
    private int clienteId;
    private LocalDateTime fecha;
    private double total;
    private String clienteNombre; // 👈 Este campo debe existir
    private List<ItemPedido> items;

    public double calcularTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPlato().getPrecio() * item.getCantidad())
                .sum();
    }
}