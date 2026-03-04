package com.mallorcamarket.service;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;

import java.util.List;

// Una interfície és un contracte: defineix quines operacions pot fer el sistema [cite: 2023]
public interface ProductoService {

    // Mètode per obtenir tots els productes actius del mercat
    List<Producto> listarTodosActivos();

    List<Producto> listarTodos(); // <--- AFEGEIX AIXÒ

    // Mètode per cercar un producte concret pel seu ID
    Producto buscarPorId(Long id);

    // Mètode per guardar o actualitzar un producte (CRUD) [cite: 524]
    void guardar(Producto producto);

    // Mètode per desactivar un producte sense esborrar-lo (esborrat lògic)
    void eliminarLogico(Long id);

    // Definim la "promesa" del mètode que llistarà només els productes d'un usuari
    List<Producto> buscarPorProveedor(Usuario proveedor);

}