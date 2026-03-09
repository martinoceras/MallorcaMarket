package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.Categoria;
import com.mallorcamarket.repository.CategoriaRepository;
import com.mallorcamarket.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Indica que és un component de lògica de negoci
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired // Connecta amb el repositori de dades
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll(); // Recupera totes les categories de MySQL
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