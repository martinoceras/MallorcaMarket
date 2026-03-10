package com.mallorcamarket.service;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import java.util.List;

public interface ProductoService {

    // Retorna només productes publicables a la botiga (actius i visibles).
    List<Producto> listarTodosActivos();

    // Retorna l'inventari complet, incloent productes ocults o inactius.
    List<Producto> listarTodos();

    // Consulta un producte concret per identificador.
    Producto buscarPorId(Long id);

    // Desa o actualitza un producte segons l'estat de l'entitat.
    void guardar(Producto producto);

    // Elimina un producte per ID (baixa física segons implementació).
    void eliminar(Long id);

    // Recupera els productes associats a un proveïdor concret.
    List<Producto> buscarPorProveedor(Usuario proveedor);
}