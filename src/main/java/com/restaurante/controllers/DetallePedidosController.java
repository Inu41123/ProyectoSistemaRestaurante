package com.restaurante.controllers;

import com.restaurante.models.*;
import com.restaurante.services.PedidoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class DetallePedidosController {

    @FXML private TableView<DetallePedidoItem> tablaDetallePedidos;
    @FXML private TableColumn<DetallePedidoItem, Integer> colDetalleId;
    @FXML private TableColumn<DetallePedidoItem, String> colDetalleCliente;
    @FXML private TableColumn<DetallePedidoItem, String> colDetallePlato;
    @FXML private TableColumn<DetallePedidoItem, Integer> colDetalleCantidad;
    @FXML private TableColumn<DetallePedidoItem, Double> colDetalleSubtotal;
    @FXML private TableColumn<DetallePedidoItem, java.time.LocalDateTime> colDetalleFecha;

    private final PedidoService pedidoService = new PedidoService();

    @FXML
    public void initialize() {
        colDetalleId.setCellValueFactory(data -> data.getValue().pedidoIdProperty().asObject());
        colDetalleCliente.setCellValueFactory(data -> data.getValue().clienteProperty());
        colDetallePlato.setCellValueFactory(data -> data.getValue().platoProperty());
        colDetalleCantidad.setCellValueFactory(data -> data.getValue().cantidadProperty().asObject());
        colDetalleSubtotal.setCellValueFactory(data -> data.getValue().subtotalProperty().asObject());
        colDetalleFecha.setCellValueFactory(data -> data.getValue().fechaProperty());

        cargarDetallePedidos();
    }

    private void cargarDetallePedidos() {
        ObservableList<DetallePedidoItem> detalleItems = FXCollections.observableArrayList();

        try {
            List<Pedido> pedidos = pedidoService.obtenerTodos();
            for (Pedido pedido : pedidos) {
                for (ItemPedido item : pedido.getItems()) {
                    DetallePedidoItem detalle = new DetallePedidoItem(
                            pedido.getId(),
                            pedido.getClienteNombre(),
                            item.getPlato().getNombre(),
                            item.getCantidad(),
                            item.getPlato().getPrecio() * item.getCantidad(),
                            pedido.getFecha()
                    );
                    detalleItems.add(detalle);
                }
            }
            tablaDetallePedidos.setItems(detalleItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}