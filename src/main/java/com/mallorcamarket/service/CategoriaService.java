package com.mallorcamarket.service;

import com.mallorcamarket.model.Categoria;
import java.util.List;

// Interfície per definir les operacions amb categories [cite: 1403, 1486]
public interface CategoriaService {
    List<Categoria> listarTodas();
    Categoria guardar(Categoria categoria);
    Categoria buscarPorId(Long id);
}