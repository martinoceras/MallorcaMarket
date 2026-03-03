package com.mallorcamarket.service;

import com.mallorcamarket.model.Producto;
import java.util.List;

// Una interfície és un contracte: defineix quines operacions pot fer el sistema [cite: 2023]
public interface ProductoService {

    // Mètode per obtenir tots els productes actius del mercat
    List<Producto> listarTodosActivos();

    // Mètode per cercar un producte concret pel seu ID
    Producto buscarPorId(Long id);

    // Mètode per guardar o actualitzar un producte (CRUD) [cite: 524]
    Producto guardar(Producto producto);

    // Mètode per desactivar un producte sense esborrar-lo (esborrat lògic)
    void eliminarLogico(Long id);
}