package com.restaurante.dao;

import com.restaurante.models.*;
import com.restaurante.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public void crear(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO Pedidos (cliente_id, fecha) VALUES (?, ?)";
        String sqlItem = "INSERT INTO Pedido_Platos (pedido_id, plato_id, cantidad) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {

            stmtPedido.setInt(1, pedido.getClienteId());
            stmtPedido.setTimestamp(2, Timestamp.valueOf(pedido.getFecha()));
            stmtPedido.executeUpdate();

            try (ResultSet generatedKeys = stmtPedido.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int pedidoId = generatedKeys.getInt(1);
                    pedido.setId(pedidoId);

                    for (ItemPedido item : pedido.getItems()) {
                        stmtItem.setInt(1, pedidoId);
                        stmtItem.setInt(2, item.getPlato().getId());
                        stmtItem.setInt(3, item.getCantidad());
                        stmtItem.addBatch();
                    }
                    stmtItem.executeBatch();
                }
            }
        }
    }

    public List<Pedido> listarPorCliente(int clienteId) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT id, fecha FROM Pedidos WHERE cliente_id = ? ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("id"));
                    pedido.setClienteId(clienteId);
                    pedido.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    pedido.setItems(obtenerItemsPedido(pedido.getId()));
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }

    private List<ItemPedido> obtenerItemsPedido(int pedidoId) throws SQLException {
        List<ItemPedido> items = new ArrayList<>();
        String sql = "SELECT plato_id, cantidad FROM Pedido_Platos WHERE pedido_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemPedido item = new ItemPedido();
                    item.setPlato(obtenerPlato(rs.getInt("plato_id")));
                    item.setCantidad(rs.getInt("cantidad"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Plato obtenerPlato(int platoId) throws SQLException {
        Plato plato = new Plato();
        plato.setId(platoId);
        return plato;
    }

    public void eliminar(int pedidoId) throws SQLException {
        String sql = "DELETE FROM Pedidos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            stmt.executeUpdate();
        }
    }
}