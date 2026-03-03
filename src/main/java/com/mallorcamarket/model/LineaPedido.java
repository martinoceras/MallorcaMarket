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

    private Integer cantidad; // Quantitat comprada [cite: 1423]
    private BigDecimal precioUnitario; // Preu en el moment de la compra

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido; // Comanda a la qual pertany [cite: 1427]

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto; // Producte comprat [cite: 1428]
}