package com.mallorcamarket.repository;

import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Spring buscarà per usuari i ho ordenarà per data de creació descendent
    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);

    // Aquesta és la que necessitem per al proveïdor (Panell de vendes)
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.lineas l WHERE l.producto.proveedor = :proveedor")
    List<Pedido> findByProveedor(@Param("proveedor") Usuario proveedor);
}
