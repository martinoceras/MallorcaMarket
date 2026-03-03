package com.mallorcamarket.repository;

import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que aquest component gestiona l'accés a la base de dades
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data JPA és "màgic": només escrivint el nom del mètode així,
    // ell sol crearà la consulta SQL: SELECT * FROM usuarios WHERE email = ?
    Usuario findByEmail(String email);
}