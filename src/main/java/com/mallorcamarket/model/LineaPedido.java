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

    private Integer cantidad; // Quantitat comprada
    private BigDecimal precioUnitario; // Preu en el moment de la compra

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido; // Comanda a la qual pertany

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto; // Producte comprat
}