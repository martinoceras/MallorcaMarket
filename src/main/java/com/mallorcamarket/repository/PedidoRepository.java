package com.mallorcamarket.repository;

import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Historial de comandes d'un client ordenat de més recent a més antic.
    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);

    // Comandes on apareixen productes d'un proveïdor concret.
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.lineas l WHERE l.producto.proveedor = :proveedor ORDER BY p.fecha DESC")
    List<Pedido> findByProveedor(@Param("proveedor") Usuario proveedor);


}