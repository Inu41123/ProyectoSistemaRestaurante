package com.restaurante.controllers;

import com.restaurante.models.*;
import com.restaurante.services.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    @FXML private Button btnNuevoPedido;
    @FXML private Button btnActualizarPedido;
    @FXML private Button btnRefrescarPedidos;
    @FXML private Button btnVerDetallePedidos;

    @FXML private TableView<Pedido> tablaPedidos;
    @FXML private TableColumn<Pedido, Integer> colPedidoId;
    @FXML private TableColumn<Pedido, String> colCliente;
    @FXML private TableColumn<Pedido, LocalDateTime> colFecha;
    @FXML private TableColumn<Pedido, Double> colTotal;
    //nuevo boton
    @FXML private Button btnEliminarPedido;

    private final ClienteService clienteService = new ClienteService();
    private final PlatoService platoService = new PlatoService();
    private final PedidoService pedidoService = new PedidoService();
    private final Validator validator = new Validator();
    private final ObservableList<ItemPedido> itemsPedido = FXCollections.observableArrayList();

    private Pedido pedidoSeleccionado = null;

    @FXML
    private void initialize() {
        configurarComponentes();
        Platform.runLater(() -> {
            cargarDatosIniciales();
            configurarValidaciones();
            cargarPedidos();
            actualizarTotal();
        });

        tablaPedidos.getSelectionModel().selectedItemProperty().addListener((obs, oldPedido, nuevoPedido) -> {
            if (nuevoPedido != null) {
                pedidoSeleccionado = nuevoPedido;
                cbClientes.getSelectionModel().select(
                        cbClientes.getItems().stream()
                                .filter(c -> c.getId() == nuevoPedido.getClienteId())
                                .findFirst().orElse(null)
                );
                itemsPedido.setAll(nuevoPedido.getItems());
                tablaPlatosSeleccionados.refresh();
                actualizarTotal();
            }
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
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

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
    private void actualizarPedido() {
        guardarPedido();
    }

    @FXML
    private void nuevoPedido() {
        pedidoSeleccionado = null;
        limpiarFormulario();
    }

    //agregar funcionalidad
    private void guardarPedido() {
        if (!validator.validate()) {
            return;
        }

        try {
            Cliente cliente = cbClientes.getValue();
            if (cliente == null) {
                mostrarError("Seleccione un cliente");
                return;
            }

            if (pedidoSeleccionado != null) {
                // Actualizar datos del pedido existente
                pedidoSeleccionado.setClienteId(cliente.getId());
                pedidoSeleccionado.setItems(new ArrayList<>(itemsPedido));

                // Recalcular y asignar el nuevo total
                double nuevoTotal = pedidoSeleccionado.calcularTotal();
                pedidoSeleccionado.setTotal(nuevoTotal);

                // Actualizar en la base de datos
                pedidoService.actualizarPedido(pedidoSeleccionado);

                // Confirmación visual
                mostrarExito("Pedido actualizado exitosamente");

                // Refrescar tabla y vista
                cargarPedidos();
                tablaPedidos.refresh(); //fuerza el redibujado visual

                // Verificación en consola
                System.out.println("Total actualizado: $" + nuevoTotal);
            } else {
                // Crear nuevo pedido
                Pedido nuevoPedido = new Pedido();
                nuevoPedido.setClienteId(cliente.getId());
                nuevoPedido.setFecha(LocalDateTime.now());
                nuevoPedido.setItems(new ArrayList<>(itemsPedido));
                nuevoPedido.setTotal(nuevoPedido.calcularTotal());

                pedidoService.crearPedido(nuevoPedido);
                mostrarExito("Pedido creado exitosamente");

                cargarPedidos();
                tablaPedidos.refresh();
            }

            limpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al guardar pedido: " + e.getMessage());
        }
    }

    @FXML
    private void refrescarPedidos() {
        cargarDatosIniciales();
        cargarPedidos();
        itemsPedido.clear();
        tablaPlatosSeleccionados.refresh();
        actualizarTotal();

        Notifications.create()
                .title("Actualizado")
                .text("La vista de pedidos se ha refrescado")
                .showInformation();
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

    @FXML
    private void verDetallePedidos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurante/views/DetallePedidos.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Detalle de Pedidos");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            mostrarError("No se pudo abrir la vista de detalle: " + e.getMessage());
        }
    }
//neuva funcionalidad
    @FXML
    private void eliminarPedido() {
        if (pedidoSeleccionado == null) {
            mostrarError("Seleccione un pedido para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este pedido?");
        confirmacion.setContentText("Esta acción no afectará al cliente ni a los platos.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    pedidoService.eliminarPedido(pedidoSeleccionado.getId());
                    mostrarExito("Pedido eliminado exitosamente.");
                    limpiarFormulario();
                    cargarPedidos();
                    tablaPedidos.refresh();
                } catch (Exception e) {
                    mostrarError("Error al eliminar pedido: " + e.getMessage());
                }
            }
        });
    }
}