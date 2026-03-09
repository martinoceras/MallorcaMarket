package com.mallorcamarket.config;

import com.mallorcamarket.model.Categoria;
import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.repository.CategoriaRepository;
import com.mallorcamarket.repository.ProductoRepository;
import com.mallorcamarket.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Autowired
    private PasswordEncoder passwordEncoder; // Injectem l'encoder definit a SecurityConfig

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository userRepo,
                                   ProductoRepository prodRepo,
                                   CategoriaRepository catRepo) {
        return args -> {
            // Verifiquem si la base de dades ja té dades per evitar l'error "Duplicate entry"
            if (userRepo.findByEmail("info@oli.cat") == null) {

                // 1. CREAR CATEGORIES
                Categoria alimentacio = new Categoria();
                alimentacio.setNombre("Alimentació");
                alimentacio.setDescripcion("Productes gastronòmics de l'illa");
                catRepo.save(alimentacio);

                Categoria artesania = new Categoria();
                artesania.setNombre("Artesania");
                artesania.setDescripcion("Productes fets a mà");
                catRepo.save(artesania);

                // 2. CREAR USUARI VENEDOR
                Usuario venedor = new Usuario();
                venedor.setNombre("Oli de Mallorca SL");
                venedor.setEmail("info@oli.cat");
                // Encriptem la contrasenya "1234" amb BCrypt
                venedor.setPassword(passwordEncoder.encode("1234"));
                venedor.setRol("ROLE_PROVIDER"); // Rol de venedor
                userRepo.save(venedor);

                // 3. CREAR PRODUCTES DE PROVA
                Producto p1 = new Producto();
                p1.setNombre("Oli d'Oliva Verge Extra");
                p1.setDescripcion("Oli amb denominació d'origen de la Serra de Tramuntana");
                p1.setPrecio(new BigDecimal("12.50")); // Usem BigDecimal per precisió monetària
                p1.setStock(50);
                p1.setActivo(true);
                p1.setProveedor(venedor); // Assignem el venedor
                p1.setCategoria(alimentacio); // Assignem la categoria
                prodRepo.save(p1);

                Producto p2 = new Producto();
                p2.setNombre("Ensaïmada de Mallorca");
                p2.setDescripcion("Ensaïmada llisa tradicional de mida mitjana");
                p2.setPrecio(new BigDecimal("18.00"));
                p2.setStock(20);
                p2.setActivo(true);
                p2.setProveedor(venedor);
                p2.setCategoria(alimentacio);
                prodRepo.save(p2);

                System.out.println(">>> MallorcaMarket: Dades inicials carregades correctament amb seguretat BCrypt.");
            } else {
                System.out.println(">>> MallorcaMarket: Les dades ja existeixen a la BD, saltant inicialització.");
            }
        };
    }
}