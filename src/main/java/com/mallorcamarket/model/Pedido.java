package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedido")
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now(); // Data de la compra [cite: 1408]
    private BigDecimal total; // Import total calculat [cite: 1409]
    private String status = "PENDING"; // Estats: PENDING, PAID, SHIPPED... [cite: 1410, 1414]

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario cliente; // El client que fa la compra [cite: 1412]

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<LineaPedido> lineas; // Detalls dels productes comprats [cite: 1431]
}