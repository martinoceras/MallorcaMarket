package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.*;
import com.mallorcamarket.repository.*;
import com.mallorcamarket.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    // AQUESTES LÍNIES SÓN LES QUE ET FALTAVEN PERQUÈ NO SURTI EN VERMELL:
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    @Override
    @Transactional
    public void realizarPedido(List<LineaPedido> cart, Usuario usuario) {
        // 1. Creem la capçalera de la comanda
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setCreatedAt(LocalDateTime.now());
        pedido.setStatus("CONFIRMADO");

        // Calculem el total (usant BigDecimal per precisió)
        BigDecimal total = cart.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(total);

        // Guardem el pedido per obtenir el seu ID
        final Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 2. Processem cada línia i actualitzem l'estoc (RF-03)
        for (LineaPedido item : cart) {
            Producto producto = item.getProducto();

            // Restem la quantitat del carret al stock actual de la BD
            int nouStock = producto.getStock() - item.getCantidad();
            if (nouStock < 0) {
                throw new RuntimeException("No hi ha prou estoc per a: " + producto.getNombre());
            }
            producto.setStock(nouStock);
            productoRepository.save(producto);

            // Assignem el pedido a la línia i la guardem
            item.setPedido(pedidoGuardado);
            lineaPedidoRepository.save(item);
        }
    }
    @Override
    public List<Pedido> buscarPorUsuario(Usuario usuario) {
        // Suposant que el teu PedidoRepository té aquest mètode (Spring Data JPA el crea sol)
        return pedidoRepository.findByUsuarioOrderByCreatedAtDesc(usuario);
    }
}