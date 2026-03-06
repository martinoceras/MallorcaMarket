package com.mallorcamarket.repository;

import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Aquest és el mètode que el teu Service està buscant ara mateix
    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);

    // Mètode per a la zona de proveïdor: cerca comandes que tinguin els seus productes
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.lineas l WHERE l.producto.proveedor = :proveedor ORDER BY p.fecha DESC")
    List<Pedido> findByProveedor(@Param("proveedor") Usuario proveedor);


}