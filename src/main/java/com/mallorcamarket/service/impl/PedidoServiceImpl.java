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

/**
 * Implementació de la lògica de negoci per a les comandes (RF-05)
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    // Injectem els repositoris necessaris per gestionar la persistència a MySQL
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    /**
     * Gestiona tot el procés de compra de forma atòmica (RF-05, RF-06)
     * L'anotació @Transactional garanteix que si falla la resta d'estoc,
     * no es guardi la comanda a la base de dades (Integritat de dades).
     */
    @Override
    @Transactional
    public void realizarPedido(List<LineaPedido> cart, Usuario usuario) {
        // 1. CREACIÓ DE LA CAPÇALERA DE LA COMANDA
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("CONFIRMAT");

        // Calculem el total de la compra recorrent el carret
        BigDecimal total = cart.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(total);

        // Guardem el pedido inicial per generar el seu ID a la base de dades
        final Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 2. PROCESSAMENT DE PRODUCTES I ACTUALITZACIÓ D'ESTOC (RF-03, RF-08)
        for (LineaPedido item : cart) {
            // Recuperem el producte actual de la base de dades
            Producto producto = item.getProducto();

            // Calculem el nou estoc restant les unitats comprades
            int nouStock = producto.getStock() - item.getCantidad();

            // Validació de seguretat: si no hi ha prou estoc, llancem error i la transacció es cancel·la
            if (nouStock < 0) {
                throw new RuntimeException("No hi ha prou estoc per a: " + producto.getNombre());
            }

            // Actualitzem el producte amb el nou estoc a la taula 'productos'
            producto.setStock(nouStock);
            productoRepository.save(producto);

            // Relacionem la línia de detall amb la comanda principal i la guardem
            item.setPedido(pedidoGuardado);
            lineaPedidoRepository.save(item);
        }
    }

    /**
     * Recupera l'historial de comandes d'un usuari concret ordenat per data (descendent)
     */
    @Override
    public List<Pedido> buscarPorUsuario(Usuario usuario) {
        // Assegura't que el nom coincideixi exactament amb el del Repositori
        return pedidoRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    /**
     * Busca una comanda específica pel seu identificador (per a la vista de detalls)
     */
    @Override
    public Pedido buscarPorId(Long id) {
        // Fem servir Optional.orElse(null) per evitar errors si l'ID no existeix
        return pedidoRepository.findById(id).orElse(null);
    }
    @Override
    public List<Pedido> buscarPorProveedor(Usuario proveedor) {
        // Cridem al repositori per fer la consulta a la base de dades
        return pedidoRepository.findByProveedor(proveedor);
    }
    @Override
    public void guardar(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    @Override
    public boolean pertanyAlProveedor(Pedido pedido, Usuario proveedor) {
        // Verifiquem si alguna de les línies del pedido té un producte que ven aquest proveïdor
        return pedido.getLineas().stream()
                .anyMatch(linea -> linea.getProducto().getProveedor().getId().equals(proveedor.getId()));
    }

    @Override
    public List<Pedido> buscarPorProveedorFiltered(Usuario proveedor) {
        // Get all orders that have products from this provider
        List<Pedido> allOrders = buscarPorProveedor(proveedor);

        // Filter the line items for each order to only show products from this provider
        allOrders.forEach(pedido -> {
            List<LineaPedido> filteredLines = pedido.getLineas().stream()
                    .filter(linea -> linea.getProducto().getProveedor().getId().equals(proveedor.getId()))
                    .toList();
            pedido.setLineas(filteredLines);
        });

        return allOrders;
    }
}