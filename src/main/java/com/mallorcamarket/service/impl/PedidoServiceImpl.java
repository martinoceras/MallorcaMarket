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
 * Implementació de la capa de negoci de comandes.
 * Aquest servei centralitza el checkout i la visió de comandes per rol.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    // Repositoris necessaris per persistir capçalera, línies i actualització d'estoc.
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    /**
     * Executa el procés de compra dins una transacció única.
     * Si una línia falla (per exemple, estoc insuficient), es desfà tota l'operació.
     */
    @Override
    @Transactional
    public void realizarPedido(List<LineaPedido> cart, Usuario usuario) {
        // 1) Creem la capçalera de comanda amb metadades bàsiques.
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("CONFIRMAT");

        // Calculem el total global a partir de les línies de la cistella.
        BigDecimal total = cart.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(total);

        // Guardem primer la capçalera per obtenir ID i vincular-hi les línies.
        final Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 2) Per cada línia, validem estoc, actualitzem producte i persistim detall.
        for (LineaPedido item : cart) {
            Producto producto = item.getProducto();
            int nouStock = producto.getStock() - item.getCantidad();

            // Validació crítica: no permetre vendes amb estoc negatiu.
            if (nouStock < 0) {
                throw new RuntimeException("No hi ha prou estoc per a: " + producto.getNombre());
            }

            producto.setStock(nouStock);
            productoRepository.save(producto);

            item.setPedido(pedidoGuardado);
            lineaPedidoRepository.save(item);
        }
    }

    /**
     * Recupera l'historial d'un usuari ordenat de més recent a més antic.
     */
    @Override
    public List<Pedido> buscarPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    /**
     * Consulta puntual d'una comanda per ID.
     */
    @Override
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Override
    public List<Pedido> buscarPorProveedor(Usuario proveedor) {
        // Consulta base: totes les comandes on apareix el proveïdor.
        return pedidoRepository.findByProveedor(proveedor);
    }

    @Override
    public void guardar(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    @Override
    public boolean pertanyAlProveedor(Pedido pedido, Usuario proveedor) {
        if (pedido == null || proveedor == null) {
            return false;
        }

        // Comprovació defensiva per evitar errors si alguna línia té referències incompletes.
        return pedido.getLineas().stream()
                .anyMatch(linea -> linea.getProducto() != null
                        && linea.getProducto().getProveedor() != null
                        && linea.getProducto().getProveedor().getId().equals(proveedor.getId()));
    }

    @Override
    public List<Pedido> buscarPorProveedorFiltered(Usuario proveedor) {
        if (proveedor == null) {
            return List.of();
        }

        // Construïm una vista "projectada" on cada proveïdor només veu les seves línies.
        return buscarPorProveedor(proveedor).stream()
                .map(pedido -> buildProveedorView(pedido, proveedor))
                .filter(pedido -> !pedido.getLineas().isEmpty())
                .toList();
    }

    private Pedido buildProveedorView(Pedido pedido, Usuario proveedor) {
        List<LineaPedido> filteredLines = pedido.getLineas().stream()
                .filter(linea -> lineaPerteneceAProveedor(linea, proveedor))
                .toList();

        BigDecimal providerTotal = filteredLines.stream()
                .map(linea -> linea.getPrecioUnitario().multiply(BigDecimal.valueOf(linea.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido view = new Pedido();
        view.setId(pedido.getId());
        view.setFecha(pedido.getFecha());
        view.setEstado(pedido.getEstado());
        view.setUsuario(pedido.getUsuario());
        view.setLineas(filteredLines);
        view.setTotal(providerTotal);
        return view;
    }

    private boolean lineaPerteneceAProveedor(LineaPedido linea, Usuario proveedor) {
        return linea != null
                && linea.getProducto() != null
                && linea.getProducto().getProveedor() != null
                && linea.getProducto().getProveedor().getId().equals(proveedor.getId());
    }

    @Override
    public List<Pedido> listarTodos() {
        // Vista administrativa ordenada per data descendent.
        return pedidoRepository.findAll().stream()
                .sorted((p1, p2) -> p2.getFecha().compareTo(p1.getFecha()))
                .toList();
    }
}
