package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

// @Entity: Defineix la categoria com una taula a la base de dades [cite: 1834]
@Entity
@Table(name = "categoria")
@Data // Genera automàticament getters i setters
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre; // Nom de la categoria (ex: Alimentació, Artesania) [cite: 1837]

    private String descripcion; // Descripció del tipus de productes [cite: 1838]

    // RELACIÓ 1:N - Una categoria pot tenir molts productes [cite: 1857, 1889]
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    public Categoria() {}
}