package com.mallorcamarket.service;

import com.mallorcamarket.model.LineaPedido;
import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import java.util.List;

public interface PedidoService {
    // Mètodes que ja tenies...
    void realizarPedido(List<LineaPedido> cart, Usuario usuario);
    List<Pedido> buscarPorUsuario(Usuario usuario);
    Pedido buscarPorId(Long id);
    List<Pedido> buscarPorProveedor(Usuario proveedor);

    // AFEGEIX AIXÒ ARA:
    void guardar(Pedido pedido);
    boolean pertanyAlProveedor(Pedido pedido, Usuario proveedor);

    // Filter orders to show only line items for this provider
    List<Pedido> buscarPorProveedorFiltered(Usuario proveedor);

    // Get all orders for admin
    List<Pedido> listarTodos();
}