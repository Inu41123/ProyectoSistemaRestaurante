package com.restaurante.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {
    @FXML private TabPane tabPane;
    @FXML private Tab tabClientes;
    @FXML private Tab tabPlatos;
    @FXML private Tab tabPedidos;

    @FXML private ClienteController clienteController;
    @FXML private PlatoController platoController;
    @FXML private PedidoController pedidoController;

    public void postInitialize() {
        try {
            // Ahora los controladores ya están inyectados por FXML
            clienteController.cargarClientes();
            platoController.cargarPlatos();
            pedidoController.cargarDatosIniciales();
        } catch (Exception e) {
            System.err.println("Error en postInitialize: " + e.getMessage());
        }
    }
}
