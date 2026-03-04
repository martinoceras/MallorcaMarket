package com.mallorcamarket.controller;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.ProductoService;
import com.mallorcamarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador per a les funcions d'administració de la plataforma (Back-office).
 * Gestiona el manteniment d'usuaris (CU-05) i productes (CU-08).
 */
@Controller
@RequestMapping("/admin") // Totes les rutes d'aquest controlador requeriran el prefix /admin
public class AdminController {

    // Injecció de dependències dels serveis necessaris
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    // =========================================================================
    // GESTIÓ D'USUARIS (CU-05)
    // =========================================================================

    /**
     * Llista tots els usuaris registrats a la base de dades.
     * @param model Objecte per passar dades a la vista de Thymeleaf.
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        // Obtenim la llista des del servei i l'afegim al model
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/users"; // Retorna la plantilla templates/admin/users.html
    }

    /**
     * Activa o desactiva un usuari segons el seu estat actual.
     * @param id Identificador únic de l'usuari a modificar.
     */
    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id) {
        // Crida a la lògica de negoci per commutar l'estat enabled
        usuarioService.cambiarEstado(id);
        return "redirect:/admin/users"; // Redirecció per evitar re-enviaments de formulari
    }

    // =========================================================================
    // GESTIÓ DE PRODUCTES (CU-08)
    // =========================================================================

    /**
     * Mostra l'inventari de productes per a la seva gestió.
     */
    @GetMapping("/products")
    public String listProducts(Model model) {
        // Carreguem els productes per pintar la taula d'edició
        model.addAttribute("productos", productoService.listarTodos());
        return "admin/products"; // Retorna templates/admin/products.html
    }

    /**
     * Actualitza les dades d'un producte (preu i estoc).
     * @param producto Objecte mapejat automàticament des del formulari HTML.
     * @param ra Atributs de redirecció per enviar missatges de confirmació (Flash Attributes).
     */
    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Producto producto, RedirectAttributes ra) {
        // Persistim els canvis a MySQL mitjançant el servei
        productoService.guardar(producto);

        // Enviem un missatge d'èxit que només durarà una petició (evita persistència visual)
        ra.addFlashAttribute("success", "El producte '" + producto.getNombre() + "' s'ha actualitzat correctament.");

        return "redirect:/admin/products";
    }
}