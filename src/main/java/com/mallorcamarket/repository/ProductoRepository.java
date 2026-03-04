package com.mallorcamarket.repository;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Filtre per al catàleg públic (només actius)
    List<Producto> findByActivoTrue();

    // Filtre per a la zona de proveïdor (els seus productes)
    List<Producto> findByProveedor(Usuario proveedor);
}