package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "linea_pedido")
@Data
public class LineaPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad; // Unitats del producte dins la comanda.
    private BigDecimal precioUnitario; // Preu congelat en el moment de compra.

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido; // Capçalera de comanda a la qual pertany la línia.

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto; // Producte comprat a la línia.
}