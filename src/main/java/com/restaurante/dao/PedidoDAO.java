package com.restaurante.dao;

import com.restaurante.models.*;
import com.restaurante.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public void crear(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO Pedidos (cliente_id, fecha, total) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO Pedido_Platos (pedido_id, plato_id, cantidad) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {

            stmtPedido.setInt(1, pedido.getClienteId());
            stmtPedido.setTimestamp(2, Timestamp.valueOf(pedido.getFecha()));
            stmtPedido.setDouble(3, pedido.getTotal());

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
        String sql = """
            SELECT p.id, p.fecha, p.total, c.nombre AS cliente_nombre
            FROM Pedidos p
            JOIN Clientes c ON p.cliente_id = c.id
            WHERE p.cliente_id = ?
            ORDER BY p.fecha DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("id"));
                    pedido.setClienteId(clienteId);
                    pedido.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    pedido.setTotal(rs.getDouble("total"));
                    pedido.setClienteNombre(rs.getString("cliente_nombre"));
                    pedido.setItems(obtenerItemsPedido(pedido.getId()));
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }

    public List<Pedido> listarTodos() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = """
            SELECT p.id, p.cliente_id, p.fecha, p.total, c.nombre AS cliente_nombre
            FROM Pedidos p
            JOIN Clientes c ON p.cliente_id = c.id
            ORDER BY p.fecha DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setClienteId(rs.getInt("cliente_id"));
                pedido.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                pedido.setTotal(rs.getDouble("total"));
                pedido.setClienteNombre(rs.getString("cliente_nombre"));
                pedido.setItems(obtenerItemsPedido(pedido.getId()));
                pedidos.add(pedido);
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
        String sql = "SELECT nombre, precio FROM Platos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, platoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Plato plato = new Plato();
                    plato.setId(platoId);
                    plato.setNombre(rs.getString("nombre"));
                    plato.setPrecio(rs.getDouble("precio"));
                    return plato;
                }
            }
        }
        return null;
    }

    public void eliminar(int pedidoId) throws SQLException {
        String sql = "DELETE FROM Pedidos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            stmt.executeUpdate();
        }
    }

    public void actualizar(Pedido pedido) throws SQLException {
        String sqlUpdatePedido = "UPDATE Pedidos SET cliente_id = ?, fecha = ?, total = ? WHERE id = ?";
        String sqlDeleteItems = "DELETE FROM Pedido_Platos WHERE pedido_id = ?";
        String sqlInsertItems = "INSERT INTO Pedido_Platos (pedido_id, plato_id, cantidad) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdatePedido);
             PreparedStatement stmtDeleteItems = conn.prepareStatement(sqlDeleteItems);
             PreparedStatement stmtInsertItems = conn.prepareStatement(sqlInsertItems)) {

            // Actualizar datos del pedido
            stmtUpdate.setInt(1, pedido.getClienteId());
            stmtUpdate.setTimestamp(2, Timestamp.valueOf(pedido.getFecha()));
            stmtUpdate.setDouble(3, pedido.getTotal());
            stmtUpdate.setInt(4, pedido.getId());
            stmtUpdate.executeUpdate();

            // Eliminar platos anteriores
            stmtDeleteItems.setInt(1, pedido.getId());
            stmtDeleteItems.executeUpdate();

            // Insertar nuevos platos
            for (ItemPedido item : pedido.getItems()) {
                stmtInsertItems.setInt(1, pedido.getId());
                stmtInsertItems.setInt(2, item.getPlato().getId());
                stmtInsertItems.setInt(3, item.getCantidad());
                stmtInsertItems.addBatch();
            }
            stmtInsertItems.executeBatch();
        }
    }
}