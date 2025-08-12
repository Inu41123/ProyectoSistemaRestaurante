package com.restaurante.services;

import com.restaurante.dao.PlatoDAO;
import com.restaurante.models.Plato;

import java.sql.SQLException;
import java.util.List;

public class PlatoService {
    private final PlatoDAO platoDAO = new PlatoDAO();

    public void crearPlato(Plato plato) throws Exception {
        validarPlato(plato);
        platoDAO.crear(plato);
    }

    public void actualizarPlato(Plato plato) throws Exception {
        validarPlato(plato);
        platoDAO.actualizar(plato);
    }

    public void eliminarPlato(int id) throws SQLException {
        platoDAO.eliminar(id);
    }

    public List<Plato> listarPlatos() throws SQLException {
        return platoDAO.listarTodos();
    }

    private void validarPlato(Plato plato) throws Exception {
        if (plato.getNombre() == null || plato.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }

        if (plato.getPrecio() <= 0) {
            throw new Exception("El precio debe ser mayor a 0");
        }

        if (platoDAO.existeNombre(plato.getNombre())) {
            throw new Exception("El nombre del plato ya existe");
        }
    }
}