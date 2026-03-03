package com.mallorcamarket.repository;

import com.mallorcamarket.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Aquesta interfície ens permetrà fer CategoriaRepository.save() i .findAll() [cite: 1504]
}