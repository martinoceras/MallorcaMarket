package com.mallorcamarket.service;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import java.util.List;

public interface ProductoService {

    // Mètode per a la botiga pública (Actius i Visibles)
    List<Producto> listarTodosActivos();

    // Mètode per obtenir absolutament tots els productes (útil per admin)
    List<Producto> listarTodos();

    // Mètode per cercar un producte concret pel seu ID
    Producto buscarPorId(Long id);

    // Mètode per guardar o actualitzar un producte (CRUD)
    void guardar(Producto producto);

    // Mètode per esborrar o desactivar un producte
    void eliminar(Long id);

    // Mètode per llistar els productes d'un proveïdor específic
    List<Producto> buscarPorProveedor(Usuario proveedor);
}