package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos") // Nomenclatura en plural coherent amb la resta de taules.
@Data
@NoArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Marca temporal de creació de la comanda.
    private LocalDateTime fecha = LocalDateTime.now();

    private BigDecimal total;

    // Estat funcional del procés de compra (PENDENT, CONFIRMAT, ENVIAT...).
    private String estado = "PENDENT";

    // Usuari client que ha realitzat la comanda.
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Línies de detall vinculades a la capçalera de comanda.
    // orphanRemoval elimina de BD les línies desvinculades de la comanda.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas;
}