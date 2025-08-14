package com.restaurante.services;

import com.restaurante.dao.PedidoDAO;
import com.restaurante.models.ItemPedido;
import com.restaurante.models.Pedido;

import java.sql.SQLException;
import java.util.List;

public class PedidoService {
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public void crearPedido(Pedido pedido) throws Exception {
        validarPedido(pedido);
        pedidoDAO.crear(pedido);
    }

    public List<Pedido> obtenerPedidosPorCliente(int clienteId) throws Exception {
        return pedidoDAO.listarPorCliente(clienteId);
    }

    public List<Pedido> obtenerTodos() throws Exception {
        return pedidoDAO.listarTodos();
    }

    public void eliminarPedido(int pedidoId) throws Exception {
        pedidoDAO.eliminar(pedidoId);
    }

    private void validarPedido(Pedido pedido) throws Exception {
        if (pedido.getClienteId() <= 0) {
            throw new Exception("Cliente no válido");
        }

        if (pedido.getItems() == null || pedido.getItems().isEmpty()) {
            throw new Exception("Un pedido debe tener al menos un plato");
        }

        for (ItemPedido item : pedido.getItems()) {
            if (item.getCantidad() <= 0) {
                throw new Exception("Cantidad debe ser mayor a 0 para: " + item.getPlato().getNombre());
            }
        }
    }

    public void actualizarPedido(Pedido pedido) throws Exception {
        validarPedido(pedido);
        pedidoDAO.actualizar(pedido);
    }


}