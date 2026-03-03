package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.*;
import com.mallorcamarket.repository.*;
import com.mallorcamarket.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import com.mallorcamarket.repository.PedidoRepository;
import com.mallorcamarket.repository.ProductoRepository;
// ... resta d'imports

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Override
    @Transactional // Requisit RNF-09: Garanteix que si falla l'estoc, no es guardi el pedido [cite: 230, 1298]
    public Pedido realizarPedido(Usuario cliente, List<LineaPedido> items) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        BigDecimal total = BigDecimal.ZERO;

        for (LineaPedido item : items) {
            Producto prod = item.getProducto();

            // 1. Validar stock (Requisit RF-08) [cite: 219, 432, 1265]
            if (prod.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficient per: " + prod.getNombre());
            }

            // 2. Actualitzar stock automàticament [cite: 219, 436, 1282]
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepo.save(prod);

            // 3. Calcular el total de la línia [cite: 433, 1272]
            BigDecimal subtotal = prod.getPrecio().multiply(new BigDecimal(item.getCantidad()));
            total = total.add(subtotal);

            item.setPrecioUnitario(prod.getPrecio());
            item.setPedido(pedido);
        }

        pedido.setTotal(total);
        pedido.setLineas(items);

        // 4. Guardar pedido i historial (Requisit RF-10) [cite: 221, 445, 1273]
        return pedidoRepo.save(pedido);
    }
}