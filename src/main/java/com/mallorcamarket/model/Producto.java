package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data; // <--- IMPRESCINDIBLE
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data // <--- Aquesta línia genera els mètodes setVisible() i getVisible()
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

    // Només una vegada cada variable!
    private Boolean activo = true;
    private Boolean visible = true;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Usuario proveedor;
}