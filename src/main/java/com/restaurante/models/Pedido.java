package com.restaurante.models;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Pedido {
    private int id;
    private int clienteId;
    private LocalDateTime fecha;
    private double total; // 👈 Este campo permite usar setTotal()
    private List<ItemPedido> items;

    public double calcularTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPlato().getPrecio() * item.getCantidad())
                .sum();
    }
}