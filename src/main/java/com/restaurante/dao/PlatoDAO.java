package com.restaurante.dao;

import com.restaurante.models.Plato;
import com.restaurante.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatoDAO {

    public void crear(Plato plato) throws SQLException {
        String sql = "INSERT INTO Platos (nombre, precio) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, plato.getNombre());
            stmt.setDouble(2, plato.getPrecio());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    plato.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Plato> listarTodos() throws SQLException {
        List<Plato> platos = new ArrayList<>();
        String sql = "SELECT * FROM Platos";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Plato plato = new Plato();
                plato.setId(rs.getInt("id"));
                plato.setNombre(rs.getString("nombre"));
                plato.setPrecio(rs.getDouble("precio"));
                platos.add(plato);
            }
        }
        return platos;
    }

    public void actualizar(Plato plato) throws SQLException {
        String sql = "UPDATE Platos SET nombre = ?, precio = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plato.getNombre());
            stmt.setDouble(2, plato.getPrecio());
            stmt.setInt(3, plato.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Platos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Platos WHERE nombre = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}