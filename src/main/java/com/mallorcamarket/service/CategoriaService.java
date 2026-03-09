package com.mallorcamarket.service;

import com.mallorcamarket.model.Categoria;
import java.util.List;

// Interfície per definir les operacions amb categories
public interface CategoriaService {
    List<Categoria> listarTodas();
    Categoria guardar(Categoria categoria);
    Categoria buscarPorId(Long id);
}