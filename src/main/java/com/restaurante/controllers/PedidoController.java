package com.restaurante.controllers;

import com.restaurante.models.*;
import com.restaurante.services.*;
import net.synedra.validatorfx.Validator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.Notifications;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoController {

    @FXML private ComboBox<Cliente> cbClientes;
    @FXML private TableView<Plato> tablaPlatosDisponibles;
    @FXML private TableView<ItemPedido> tablaPlatosSeleccionados;
    @FXML private TableColumn<ItemPedido, String> colPlato;
    @FXML private TableColumn<ItemPedido, Integer> colCantidad;
    @FXML private TableColumn<ItemPedido, Double> colPrecio;
    @FXML private TableColumn<ItemPedido, Double> colSubtotal;
    @FXML private Label lblTotal;
    @FXML private Spinner<Integer> spCantidad;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;

    @FXML private TableView<Pedido> tablaPedidos;
    @FXML private TableColumn<Pedido, Integer> colPedidoId;
    @FXML private TableColumn<Pedido, Integer> colCliente;
    @FXML private TableColumn<Pedido, LocalDateTime> colFecha;
    @FXML private TableColumn<Pedido, Double> colTotal;

    private final ClienteService clienteService = new ClienteService();
    private final PlatoService platoService = new PlatoService();
    private final PedidoService pedidoService = new PedidoService();
    private final Validator validator = new Validator();
    private final ObservableList<ItemPedido> itemsPedido = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        configurarComponentes();
        Platform.runLater(() -> {
            cargarDatosIniciales();
            configurarValidaciones();
            cargarPedidos();
            actualizarTotal();
        });
    }

    private void configurarComponentes() {
        spCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        colPlato.setCellValueFactory(cd -> cd.getValue().getPlato().nombreProperty());
        colCantidad.setCellValueFactory(cd -> cd.getValue().cantidadProperty());
        colPrecio.setCellValueFactory(cd -> cd.getValue().getPlato().precioProperty().asObject());

        colSubtotal.setCellValueFactory(cd -> {
            double subtotal = cd.getValue().getPlato().getPrecio() * cd.getValue().getCantidad();
            return new SimpleObjectProperty<>(subtotal);
        });

        tablaPlatosSeleccionados.setItems(itemsPedido);

        colPrecio.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty ? null : String.format("$%.2f", precio));
            }
        });

        colSubtotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                setText(empty ? null : String.format("$%.2f", subtotal));
            }
        });

        colPedidoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteId"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(cd -> {
            double total = cd.getValue().getItems().stream()
                    .mapToDouble(item -> item.getPlato().getPrecio() * item.getCantidad())
                    .sum();
            return new SimpleObjectProperty<>(total);
        });

        colTotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                setText(empty ? null : String.format("$%.2f", total));
            }
        });
    }

    public void cargarDatosIniciales() {
        try {
            ObservableList<Cliente> clientes = FXCollections.observableArrayList(
                    clienteService.listarClientes()
            );
            cbClientes.setItems(clientes);

            ObservableList<Plato> platos = FXCollections.observableArrayList(
                    platoService.listarPlatos()
            );
            tablaPlatosDisponibles.setItems(platos);

        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    public void cargarPedidos() {
        try {
            List<Pedido> pedidos = pedidoService.obtenerTodos();
            tablaPedidos.setItems(FXCollections.observableArrayList(pedidos));
        } catch (Exception e) {
            mostrarError("Error al cargar pedidos: " + e.getMessage());
        }
    }

    private void configurarValidaciones() {
        validator.createCheck()
                .dependsOn("cliente", cbClientes.valueProperty())
                .withMethod(c -> {
                    if (c.get("cliente") == null) {
                        c.error("Seleccione un cliente");
                    }
                })
                .decorates(cbClientes);

        validator.createCheck()
                .dependsOn("platos", tablaPlatosSeleccionados.itemsProperty())
                .withMethod(c -> {
                    if (tablaPlatosSeleccionados.getItems().isEmpty()) {
                        c.error("Agregue al menos un plato");
                    }
                })
                .decorates(tablaPlatosSeleccionados);
    }

    @FXML
    private void agregarPlato() {
        Plato platoSeleccionado = tablaPlatosDisponibles.getSelectionModel().getSelectedItem();
        if (platoSeleccionado == null) {
            mostrarError("Seleccione un plato");
            return;
        }

        int cantidad = spCantidad.getValue();
        if (cantidad <= 0) {
            mostrarError("Cantidad debe ser mayor a 0");
            return;
        }

        for (ItemPedido item : itemsPedido) {
            if (item.getPlato().getId() == platoSeleccionado.getId()) {
                item.setCantidad(item.getCantidad() + cantidad);
                tablaPlatosSeleccionados.refresh();
                actualizarTotal();
                return;
            }
        }

        ItemPedido nuevoItem = new ItemPedido();
        nuevoItem.setPlato(platoSeleccionado);
        nuevoItem.setCantidad(cantidad);
        itemsPedido.add(nuevoItem);
        actualizarTotal();
    }

    @FXML
    private void eliminarPlato() {
        ItemPedido itemSeleccionado = tablaPlatosSeleccionados.getSelectionModel().getSelectedItem();
        if (itemSeleccionado != null) {
            itemsPedido.remove(itemSeleccionado);
            actualizarTotal();
        }
    }

    @FXML
    private void guardarPedido() {
        if (!validator.validate()) {
            return;
        }

        try {
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setClienteId(cbClientes.getValue().getId());
            nuevoPedido.setFecha(LocalDateTime.now());
            nuevoPedido.setItems(new ArrayList<>(itemsPedido));

            pedidoService.crearPedido(nuevoPedido);
            mostrarExito("Pedido creado exitosamente");
            limpiarFormulario();
            cargarPedidos();
        } catch (Exception e) {
            mostrarError("Error al guardar pedido: " + e.getMessage());
        }
    }

    private void actualizarTotal() {
        double total = itemsPedido.stream()
                .mapToDouble(item -> item.getPlato().getPrecio() * item.getCantidad())
                .sum();
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    private void limpiarFormulario() {
        cbClientes.getSelectionModel().clearSelection();
        itemsPedido.clear();
        actualizarTotal();
        spCantidad.getValueFactory().setValue(1);
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