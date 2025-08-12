package com.restaurante.controllers;

import com.restaurante.models.Cliente;
import com.restaurante.services.ClienteService;
import javafx.application.Platform;
import net.synedra.validatorfx.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.Notifications;

import java.sql.SQLException;

public class ClienteController {

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCorreo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    private final ClienteService clienteService = new ClienteService();
    private final Validator validator = new Validator();
    private Cliente clienteSeleccionado;

    @FXML
    private void initialize() {
        configurarTabla();
        configurarValidaciones();
        cargarClientes();

        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> seleccionarCliente(newSelection)
        );
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        tablaClientes.setPlaceholder(new Label("No hay clientes registrados"));
    }

    private void configurarValidaciones() {
        validator.createCheck()
                .dependsOn("nombre", txtNombre.textProperty())
                .withMethod(context -> {
                    String nombre = context.get("nombre");
                    if (nombre == null || nombre.trim().isEmpty()) {
                        context.error("El nombre es obligatorio");
                    }
                })
                .decorates(txtNombre);

        validator.createCheck()
                .dependsOn("correo", txtCorreo.textProperty())
                .withMethod(context -> {
                    String correo = context.get("correo");
                    if (correo == null || correo.trim().isEmpty()) {
                        context.error("El correo es obligatorio");
                    } else if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        context.error("Formato de correo inválido");
                    }
                })
                .decorates(txtCorreo);
    }

    public void cargarClientes() {
        try {
            ObservableList<Cliente> clientes = FXCollections.observableArrayList(
                    clienteService.listarClientes()
            );
            tablaClientes.setItems(clientes);
        } catch (Exception e) {
            mostrarError("Error al cargar clientes: " + e.getMessage());
        }
    }

    private void seleccionarCliente(Cliente cliente) {
        if (cliente != null) {
            clienteSeleccionado = cliente;
            txtNombre.setText(cliente.getNombre());
            txtCorreo.setText(cliente.getCorreo());
            btnGuardar.setText("Actualizar");
            btnEliminar.setDisable(false);
        }
    }

    @FXML
    private void guardarCliente() {
        if (!validator.validate()) {
            return;
        }

        Cliente cliente = clienteSeleccionado != null ? clienteSeleccionado : new Cliente();
        cliente.setNombre(txtNombre.getText().trim());
        cliente.setCorreo(txtCorreo.getText().trim());

        try {
            if (clienteSeleccionado == null) {
                clienteService.crearCliente(cliente);
                mostrarExito("Cliente creado exitosamente");
            } else {
                clienteService.actualizarCliente(cliente);
                mostrarExito("Cliente actualizado exitosamente");
            }
            limpiarFormulario();
            cargarClientes();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarCliente() {
        if (clienteSeleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este cliente?");
        alert.setContentText("Esta acción no se puede deshacer");

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                clienteService.eliminarCliente(clienteSeleccionado.getId());
                mostrarExito("Cliente eliminado exitosamente");
                limpiarFormulario();
                cargarClientes();
            } catch (SQLException e) {
                mostrarError("Error al eliminar cliente: " + e.getMessage());
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        clienteSeleccionado = null;
        txtNombre.clear();
        txtCorreo.clear();
        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tablaClientes.getSelectionModel().clearSelection();
    }

    private void mostrarExito(String mensaje) {
        Notifications.create()
                .title("Éxito")
                .text(mensaje)
                .showInformation();
    }

    private void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title("Error")
                    .text(mensaje)
                    .showError();
        });
    }
}