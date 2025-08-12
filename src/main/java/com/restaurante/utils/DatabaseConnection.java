package com.restaurante.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Cambia aquí los valores según tu configuración
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=DeliciasGourmetDB;encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("Intentando conectar a la base de datos...");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión establecida correctamente.");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos:");
            e.printStackTrace();
            throw e; // Relanzamos para que los controladores lo detecten
        }
    }
}
