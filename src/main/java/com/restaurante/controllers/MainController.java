package com.restaurante.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {
    @FXML private TabPane tabPane;
    @FXML private Tab tabClientes;
    @FXML private Tab tabPlatos;
    @FXML private Tab tabPedidos;

    // Referencias a los controladores de cada pestaña
    private ClienteController clienteController;
    private PlatoController platoController;
    private PedidoController pedidoController;

    // Metodo para inicialización tardía
    public void postInitialize() {
        try {
            // Inicializar controladores de pestañas
            clienteController = new ClienteController();
            platoController = new PlatoController();
            pedidoController = new PedidoController();

            // Cargar datos después de que la ventana está visible
            clienteController.cargarClientes();
            platoController.cargarPlatos();
            pedidoController.cargarDatosIniciales();

        } catch (Exception e) {
            System.err.println("Error en postInitialize: " + e.getMessage());
        }
    }
}