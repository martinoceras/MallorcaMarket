package com.mallorcamarket.service;

import com.mallorcamarket.model.Categoria;
import java.util.List;

// Contracte de servei per gestionar el catàleg de categories.
public interface CategoriaService {
    List<Categoria> listarTodas();
    Categoria guardar(Categoria categoria);
    Categoria buscarPorId(Long id);
}