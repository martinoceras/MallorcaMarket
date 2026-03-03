package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// @Entity: Transforma aquesta classe en la taula 'productos' a MySQL [cite: 1841]
@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT") // Permet descripcions llargues [cite: 1844]
    private String descripcion;

    // Fem servir BigDecimal per a preus per evitar errors de precisió decimal [cite: 1900]
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    // Camp per a "eliminació lògica": desactiva el producte sense esborrar-lo de la BD [cite: 1902]
    private Boolean activo = true;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // RELACIÓ N:1 - Molts productes pertanyen a una Categoria [cite: 1857, 1889]
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // RELACIÓ N:1 - Molts productes poden ser venuts per un mateix Usuari (Proveïdor) [cite: 1856, 1889]
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario vendedor;

    public Producto() {}
}