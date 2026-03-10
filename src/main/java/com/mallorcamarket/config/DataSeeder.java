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
    private PasswordEncoder passwordEncoder; // Reutilitza el bean de seguretat definit a SecurityConfig.

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository userRepo,
                                   ProductoRepository prodRepo,
                                   CategoriaRepository catRepo) {
        return args -> {
            // Inicialitzem dades només si no existeixen.
            if (userRepo.findByEmail("info@oli.cat") == null) {

                // 1) Catàleg bàsic de categories per poder classificar productes des del primer inici.
                Categoria alimentacio = new Categoria();
                alimentacio.setNombre("Alimentació");
                alimentacio.setDescripcion("Productes gastronòmics de l'illa");
                catRepo.save(alimentacio);

                Categoria artesania = new Categoria();
                artesania.setNombre("Artesania");
                artesania.setDescripcion("Productes fets a mà");
                catRepo.save(artesania);

                // 2) Usuari proveïdor de demostració per validar el flux de gestió d'inventari.
                Usuario venedor = new Usuario();
                venedor.setNombre("Oli de Mallorca SL");
                venedor.setEmail("info@oli.cat");
                // La contrasenya es desa xifrada per mantenir la mateixa política que producció.
                venedor.setPassword(passwordEncoder.encode("1234"));
                venedor.setRol("ROLE_PROVIDER"); // Rol necessari per accedir al panell de proveïdor.
                userRepo.save(venedor);

                // 3) Productes de prova per comprovar botiga pública, cistella i comandes.
                Producto p1 = new Producto();
                p1.setNombre("Oli d'Oliva Verge Extra");
                p1.setDescripcion("Oli amb denominació d'origen de la Serra de Tramuntana");
                p1.setPrecio(new BigDecimal("12.50")); // BigDecimal evita errors de precisió amb imports.
                p1.setStock(50);
                p1.setActivo(true);
                p1.setProveedor(venedor); // Associació amb el venedor propietari.
                p1.setCategoria(alimentacio); // Assignació de categoria per a filtres i visualització.
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