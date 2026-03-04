package com.mallorcamarket.service;

import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.model.LineaPedido;
import java.util.List;

public interface PedidoService {
    // Mètode per processar la compra (Requisit RF-05)
    void realizarPedido(List<LineaPedido> cart, Usuario usuario);

    List<Pedido> buscarPorUsuario(Usuario usuario);
    Pedido buscarPorId(Long id);
}
