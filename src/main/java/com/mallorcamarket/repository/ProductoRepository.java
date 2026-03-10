package com.mallorcamarket.repository;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Consulta per a la botiga pública.
     * Només retorna productes actius i visibles per als clients.
     */
    List<Producto> findByActivoTrueAndVisibleTrue();

    /**
     * Consulta de gestió per proveïdor.
     * Retorna tots els productes d'un venedor, inclosos els ocults.
     */
    List<Producto> findByProveedor(Usuario proveedor);

    /**
     * Consulta auxiliar de productes actius.
     */
    List<Producto> findByActivoTrue();
}