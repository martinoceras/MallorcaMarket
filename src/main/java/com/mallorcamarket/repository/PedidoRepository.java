package com.mallorcamarket.repository;

import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // AQUESTA ÉS LA LÍNIA MÀGICA:
    List<Pedido> findByUsuarioOrderByCreatedAtDesc(Usuario usuario);
}