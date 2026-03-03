package com.mallorcamarket.repository;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Troba tots els productes que estiguin marcats com a actius [cite: 1902]
    List<Producto> findByActivoTrue();

    // Filtra productes per venedor (útil per al panel del proveïdor) [cite: 1922]
    List<Producto> findByVendedor(Usuario vendedor);

    // Cerca productes per categoria [cite: 1917]
    List<Producto> findByCategoriaId(Long categoriaId);
}