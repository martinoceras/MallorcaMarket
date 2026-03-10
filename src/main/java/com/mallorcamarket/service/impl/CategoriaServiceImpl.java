package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.Categoria;
import com.mallorcamarket.repository.CategoriaRepository;
import com.mallorcamarket.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Capa de negoci intermèdia entre controlador i repositori.
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired // Injecció del repositori JPA de categories.
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll(); // Retorna el catàleg complet de categories.
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }
}