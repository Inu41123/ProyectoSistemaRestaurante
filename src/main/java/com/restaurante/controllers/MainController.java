package com.restaurante.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.controlsfx.control.Notifications;

public class MainController {

    @FXML private TabPane tabPane;

    @FXML private ClienteController clienteController;
    @FXML private PlatoController platoController;
    @FXML private PedidoController pedidoController;

    @FXML
    private void initialize() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) return;
            String tabName = newTab.getText();

            switch (tabName) {
                case "Clientes" -> {
                    if (clienteController != null) {
                        clienteController.cargarClientes();
                    }
                }
                case "Platos" -> {
                    if (platoController != null) {
                        platoController.cargarPlatos();
                    }
                }
                case "Pedidos" -> {
                    if (pedidoController != null) {
                        pedidoController.cargarDatosIniciales();
                        pedidoController.cargarPedidos();
                    }
                }
            }
        });
    }

    public void postInitialize() {
        System.out.println("Post-inicialización ejecutada.");

        if (clienteController != null) clienteController.cargarClientes();
        if (platoController != null) platoController.cargarPlatos();
        if (pedidoController != null) {
            pedidoController.cargarDatosIniciales();
            pedidoController.cargarPedidos();
        }

        Notifications.create()
                .title("Bienvenido")
                .text("Sistema Delicias Gourmet listo para usar")
                .showInformation();
    }
}