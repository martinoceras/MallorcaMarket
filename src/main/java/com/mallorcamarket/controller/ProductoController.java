package com.mallorcamarket.controller;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.ProductoService;
import com.mallorcamarket.service.CategoriaService;
import com.mallorcamarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    // Llistar productes (Pàgina principal) [cite: 949]
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        List<Producto> productos;

        // If logged in as a provider, show only their products
        if (currentUser != null && currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROVIDER"))) {
            Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
            productos = productoService.buscarPorProveedor(proveedor);
        } else {
            // If not logged in or not a provider, show all active and visible products
            productos = productoService.listarTodosActivos();
        }

        model.addAttribute("productos", productos);
        return "index";
    }


    // Mostrar formulari per a nou producte [cite: 719]
    @GetMapping("/productes/nou")
    public String mostrarFormulariNou(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productes/formulari";
    }

    // Aquest mètode rep les dades del formulari HTML
    @PostMapping("/productes/guardar")
    public String guardarProducte(@ModelAttribute("producto") Producto producto) {
        // 1. Cridem al servei per persistir l'objecte a MySQL [cite: 563, 1443]
        productoService.guardar(producto);

        // 2. Redirigim a la pàgina principal per veure el producte llistat [cite: 872]
        return "redirect:/";
    }

    // Editar un producte existent [cite: 1273]
    @GetMapping("/productes/editar/{id}")
    public String mostrarFormulariEditar(@PathVariable("id") Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productes/formulari";
    }

    // Esborrat lògic del producte [cite: 1902]
    @GetMapping("/productes/eliminar/{id}")
    public String eliminarProducte(@PathVariable("id") Long id) {
        // CANVIA eliminarLogico per eliminar
        productoService.eliminar(id);
        return "redirect:/";
    }
}