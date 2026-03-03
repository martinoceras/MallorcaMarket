package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.repository.ProductoRepository;
import com.mallorcamarket.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Indica a Spring que aquesta classe conté la lògica de negoci [cite: 1486]
public class ProductoServiceImpl implements ProductoService {

    // Injectem el Repositori (el mosso de magatzem) per accedir a MySQL [cite: 1494]
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> listarTodosActivos() {
        // Cridem al mètode personalitzat que varem fer al Repositori
        return productoRepository.findByActivoTrue();
    }

    @Override
    public Producto buscarPorId(Long id) {
        // Busquem el producte o retornem null si no existeix
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public Producto guardar(Producto producto) {
        // Aquí podríem afegir lògica com: "Validar que el preu no sigui negatiu" [cite: 1907]
        return productoRepository.save(producto);
    }

    @Override
    public void eliminarLogico(Long id) {
        // Obtenim el producte de la base de dades
        Producto p = buscarPorId(id);
        if (p != null) {
            p.setActivo(false); // Canviem l'estat en lloc d'esborrar la fila
            productoRepository.save(p); // Guardem el canvi
        }
    }
}