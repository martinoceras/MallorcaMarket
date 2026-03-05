package com.mallorcamarket.repository;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Cerca per a la botiga pública:
     * Retorna només els productes que no han estat eliminats (activo = true)
     * i que el proveïdor ha decidit mostrar (visible = true).
     */
    List<Producto> findByActivoTrueAndVisibleTrue();

    /**
     * Cerca per al panell de gestió del proveïdor:
     * Retorna tots els productes d'un venedor concret, estiguin o no ocults.
     */
    List<Producto> findByProveedor(Usuario proveedor);

    /**
     * Cerca genèrica de productes no eliminats.
     */
    List<Producto> findByActivoTrue();
}