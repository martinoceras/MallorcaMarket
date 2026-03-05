package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos") // Millor en plural per a la base de dades
@Data
@NoArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hem canviat 'createdAt' per 'fecha' per solucionar l'error de l'StackTrace
    private LocalDateTime fecha = LocalDateTime.now();

    private BigDecimal total;

    // Usem "estado" per mantenir el català com a la resta del projecte
    private String estado = "PENDENT";

    // Relació amb el client que fa la compra
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relació amb els detalls de la comanda
    // orphanRemoval = true serveix perquè si esborres una línia, s'esborri de la BD automàticament
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas;
}