package com.restaurante.controllers;

import com.restaurante.models.Plato;
import com.restaurante.services.PlatoService;
import javafx.application.Platform;
import net.synedra.validatorfx.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.Notifications;

import java.sql.SQLException;

public class PlatoController {

    @FXML private TableView<Plato> tablaPlatos;
    @FXML private TableColumn<Plato, Integer> colId;
    @FXML private TableColumn<Plato, String> colNombre;
    @FXML private TableColumn<Plato, Double> colPrecio;

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    private final PlatoService platoService = new PlatoService();
    private final Validator validator = new Validator();
    private Plato platoSeleccionado;

    @FXML
    private void initialize() {
        configurarTabla();
        configurarValidaciones();
        Platform.runLater(() -> {
            cargarPlatos();
            tablaPlatos.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSelection, newSelection) -> seleccionarPlato(newSelection)
            );
        });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        colPrecio.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty ? null : String.format("$%.2f", precio));
            }
        });

        tablaPlatos.setPlaceholder(new Label("No hay platos registrados"));
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
                .dependsOn("precio", txtPrecio.textProperty())
                .withMethod(context -> {
                    String precioStr = context.get("precio");
                    if (precioStr == null || precioStr.trim().isEmpty()) {
                        context.error("El precio es obligatorio");
                        return;
                    }
                    try {
                        double precio = Double.parseDouble(precioStr);
                        if (precio <= 0) {
                            context.error("El precio debe ser mayor a 0");
                        }
                    } catch (NumberFormatException e) {
                        context.error("Ingrese un número válido");
                    }
                })
                .decorates(txtPrecio);
    }

    public void cargarPlatos() {
        try {
            ObservableList<Plato> platos = FXCollections.observableArrayList(
                    platoService.listarPlatos()
            );
            tablaPlatos.setItems(platos);
        } catch (SQLException e) {
            mostrarError("Error al cargar platos: " + e.getMessage());
        }
    }

    private void seleccionarPlato(Plato plato) {
        if (plato != null) {
            platoSeleccionado = plato;
            txtNombre.setText(plato.getNombre());
            txtPrecio.setText(String.valueOf(plato.getPrecio()));
            btnGuardar.setText("Actualizar");
            btnEliminar.setDisable(false);
        }
    }

    @FXML
    private void guardarPlato() {
        if (!validator.validate()) {
            return;
        }

        Plato plato = platoSeleccionado != null ? platoSeleccionado : new Plato();
        plato.setNombre(txtNombre.getText().trim());

        try {
            plato.setPrecio(Double.parseDouble(txtPrecio.getText()));
        } catch (NumberFormatException e) {
            mostrarError("Formato de precio inválido");
            return;
        }

        try {
            if (platoSeleccionado == null) {
                platoService.crearPlato(plato);
                mostrarExito("Plato creado exitosamente");
            } else {
                // Actualizar plato existente
                platoService.actualizarPlato(plato);
                mostrarExito("Plato actualizado exitosamente");

                // Verificación en consola
                System.out.println("Plato actualizado: " + plato.getNombre() + " - $" + plato.getPrecio());
            }

            // Refrescar tabla y vista
            limpiarFormulario();
            cargarPlatos();
            tablaPlatos.refresh(); //fuerza el redibujado visual

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }
    @FXML
    private void eliminarPlato() {
        if (platoSeleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este plato?");
        alert.setContentText("Esta acción no se puede deshacer");

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                platoService.eliminarPlato(platoSeleccionado.getId());
                mostrarExito("Plato eliminado exitosamente");
                limpiarFormulario();
                cargarPlatos();
            } catch (SQLException e) {
                mostrarError("Error al eliminar plato: " + e.getMessage());
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        platoSeleccionado = null;
        txtNombre.clear();
        txtPrecio.clear();
        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tablaPlatos.getSelectionModel().clearSelection();
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
