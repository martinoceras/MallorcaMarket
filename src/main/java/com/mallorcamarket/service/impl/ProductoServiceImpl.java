package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.repository.ProductoRepository;
import com.mallorcamarket.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Implementació per a la botiga pública: només actius i visibles.
     */
    @Override
    public List<Producto> listarTodosActivos() {
        return productoRepository.findByActivoTrueAndVisibleTrue();
    }

    /**
     * AQUEST ÉS EL MÈTODE QUE ET FALTA I CAUSA L'ERROR:
     * Retorna absolutament tots els productes de la base de dades.
     */
    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public List<Producto> buscarPorProveedor(Usuario proveedor) {
        return productoRepository.findByProveedor(proveedor);
    }

    @Override
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public void guardar(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public void eliminar(Long id) {
        if (id != null) {
            productoRepository.deleteById(id);
        }
    }
}