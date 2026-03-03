package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.Categoria;
import com.mallorcamarket.repository.CategoriaRepository;
import com.mallorcamarket.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Indica que és un component de lògica de negoci [cite: 1485]
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired // Connecta amb el repositori de dades [cite: 1495, 1504]
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll(); // Recupera totes les categories de MySQL [cite: 1512]
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