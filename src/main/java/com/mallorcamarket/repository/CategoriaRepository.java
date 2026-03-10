package com.mallorcamarket.repository;

import com.mallorcamarket.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Repositori JPA estàndard: ja incorpora operacions CRUD sense codi addicional.
}