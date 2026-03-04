package com.mallorcamarket.controller;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.ProductoService;
import com.mallorcamarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // Indica a Spring que aquesta classe gestionarà rutes web (vistes HTML)
@RequestMapping("/admin") // Defineix que totes les URLs d'aquest fitxer comencen per /admin
public class AdminController {

    // @Autowired realitza la "Injecció de Dependències": connecta el controlador amb la lògica de negoci
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    // --- GESTIÓ D'USUARIS (CU-05) ---

    /**
     * Mostra la llista completa d'usuaris.
     * Accés mitjançant GET /admin/users
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        // Obtenim la llista d'usuaris i la enviem a la vista "usuarios"
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/users"; // Retorna el fitxer templates/admin/users.html
    }

    /**
     * Canvia l'estat (actiu/inactiu) d'un usuari segons el seu ID.
     */
    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id) {
        // @PathVariable agafa l'ID directament de la URL de la petició
        usuarioService.cambiarEstado(id);
        return "redirect:/admin/users"; // Refresca la pàgina per veure el canvi
    }

    // --- GESTIÓ DE PRODUCTES (CU-08) ---

    /**
     * Mostra l'inventari de productes per a la seva edició.
     * Accés mitjançant GET /admin/products
     */
    @GetMapping("/products")
    public String listProducts(Model model) {
        // Enviem la llista de productes al model per pintar-los a la taula
        model.addAttribute("productos", productoService.listarTodos());
        return "admin/products"; // Retorna el fitxer templates/admin/products.html
    }

    /**
     * Actualitza un producte (preu i estoc).
     * @ModelAttribute converteix els camps del formulari HTML directament en un objecte Producto
     */
    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Producto producto) {
        // Cridem al servei per persistir els canvis a la base de dades MySQL
        productoService.guardar(producto);
        return "redirect:/admin/products"; // Torna a la llista de productes
    }
}