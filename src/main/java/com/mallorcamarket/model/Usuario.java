package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;

// Entitat que representa els comptes d'accés de la plataforma.
@Entity
@Table(name = "usuarios")
@Data // Lombok redueix codi repetitiu del model (getters/setters, etc.).
public class Usuario {

    // Identificador tècnic autogenerat per la base de dades.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'email és únic perquè s'utilitza com a credencial d'autenticació.
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nombre;

    // Rol funcional dins el sistema: ADMIN, PROVIDER o CLIENT.
    private String rol;

    // Constructor buit requerit per JPA.
    public Usuario() {
    }

    // Constructor auxiliar per crear usuaris de forma explícita en proves o seeding.
    public Usuario(String email, String password, String nombre, String rol) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Estat de bloqueig del compte (true = pot autenticar-se).
    private boolean enabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return email != null && email.equals(usuario.email);
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}