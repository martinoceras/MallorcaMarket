package com.mallorcamarket.repository;

import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Repositori de persistència d'usuaris.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Consulta derivada de Spring Data per localitzar usuaris pel seu email.
    Usuario findByEmail(String email);
}