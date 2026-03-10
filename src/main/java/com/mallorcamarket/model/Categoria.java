package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

// Entitat de suport per classificar productes dins el catàleg.
@Entity
@Table(name = "categoria")
@Data // Redueix codi repetitiu del model (getters/setters).
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre; // Exemples: Alimentació, Artesania.

    private String descripcion; // Text descriptiu per a la gestió interna.

    // Relació 1:N: una categoria pot incloure múltiples productes.
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    public Categoria() {}
}