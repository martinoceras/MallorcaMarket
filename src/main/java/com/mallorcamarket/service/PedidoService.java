package com.mallorcamarket.service;

import com.mallorcamarket.model.LineaPedido;
import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import java.util.List;

public interface PedidoService {
    // Executa el procés de compra i persisteix capçalera + línies.
    void realizarPedido(List<LineaPedido> cart, Usuario usuario);

    // Historial de comandes d'un client.
    List<Pedido> buscarPorUsuario(Usuario usuario);

    // Consulta de detall per ID.
    Pedido buscarPorId(Long id);

    // Comandes on participa un proveïdor.
    List<Pedido> buscarPorProveedor(Usuario proveedor);

    // Persistència d'actualitzacions d'estat.
    void guardar(Pedido pedido);

    // Verifica si una comanda conté línies d'un proveïdor determinat.
    boolean pertanyAlProveedor(Pedido pedido, Usuario proveedor);

    // Projecta les comandes amb només les línies corresponents al proveïdor.
    List<Pedido> buscarPorProveedorFiltered(Usuario proveedor);

    // Llistat global per a administració.
    List<Pedido> listarTodos();
}