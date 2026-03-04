package com.mallorcamarket.model;

import jakarta.persistence.*;
import lombok.Data;

// @Entity indica a Spring que aquesta classe s'ha de transformar en una taula de la BD
@Entity
@Table(name = "usuarios") // Donam nom a la taula a MySQL
@Data // Genera automàticament Getters, Setters, toString i Equals (gràcies a Lombok)
public class Usuario {

    // @Id marca aquest camp com a la clau primària (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // L'ID serà autoincremental (1, 2, 3...)
    private Long id;

    // @Column(unique = true) evita que hi hagi dos usuaris amb el mateix correu
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nombre;

    // Aquest camp servirà per controlar si és "ADMIN", "PROVEEDOR" o "CLIENTE"
    private String rol;

    // Constructor buit (Obligatori per a JPA)
    public Usuario() {
    }

    // Constructor per facilitar la creació d'usuaris des del codi
    public Usuario(String email, String password, String nombre, String rol) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
    }
    // Mètode per saber si està actiu (Getter)
    // Mètode per canviar l'estat (Setter)
    // Dins de la classe Usuario
    private boolean enabled; // Aquest booleà guarda si l'usuari pot entrar (true) o no (false)

}