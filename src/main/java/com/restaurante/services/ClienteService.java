package com.restaurante.services;

import com.restaurante.dao.ClienteDAO;
import com.restaurante.models.Cliente;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class ClienteService {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public void crearCliente(Cliente cliente) throws Exception {
        validarCliente(cliente);
        clienteDAO.crear(cliente);
    }

    public void actualizarCliente(Cliente cliente) throws Exception {
        validarCliente(cliente);
        clienteDAO.actualizar(cliente);
    }

    public void eliminarCliente(int id) throws SQLException {
        clienteDAO.eliminar(id);
    }

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listarTodos();
    }

    private void validarCliente(Cliente cliente) throws Exception {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }

        if (cliente.getCorreo() == null || cliente.getCorreo().trim().isEmpty()) {
            throw new Exception("El correo es obligatorio");
        }

        if (!EMAIL_PATTERN.matcher(cliente.getCorreo()).matches()) {
            throw new Exception("Formato de correo inválido");
        }

        if (clienteDAO.existeCorreo(cliente.getCorreo())) {
            throw new Exception("El correo ya está registrado");
        }
    }
}