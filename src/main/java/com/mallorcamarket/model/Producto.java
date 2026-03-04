package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imageUrl;
    private Boolean activo = true;
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Relació amb Categoria
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // AQUEST ÉS EL NOM CORRECTE QUE HEM D'USAR SEMPRE
    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Usuario proveedor;
}