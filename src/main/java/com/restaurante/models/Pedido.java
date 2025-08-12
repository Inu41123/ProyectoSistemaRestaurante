package com.restaurante.models;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Pedido {
    private int id;
    private int clienteId;
    private LocalDateTime fecha;
    private List<ItemPedido> items;

    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPlato().getPrecio() * item.getCantidad())
                .sum();
    }
}