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

    // Punt d'entrada de la botiga: adapta el llistat segons el rol autenticat.
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        List<Producto> productos;

        // Si l'usuari és proveïdor, només veu els seus productes per facilitar la gestió pròpia.
        if (currentUser != null && currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROVIDER"))) {
            Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
            productos = productoService.buscarPorProveedor(proveedor);
        } else {
            // En mode client o visitant, només es mostren productes actius i visibles.
            productos = productoService.listarTodosActivos();
        }

        model.addAttribute("productos", productos);
        return "index";
    }


    // Mostra el formulari d'alta de producte dins el flux antic /productes.
    @GetMapping("/productes/nou")
    public String mostrarFormulariNou(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productes/formulari";
    }

    // Rep les dades del formulari i les persisteix a través del servei.
    @PostMapping("/productes/guardar")
    public String guardarProducte(@ModelAttribute("producto") Producto producto) {
        productoService.guardar(producto);
        return "redirect:/";
    }

    // Obre el formulari en mode edició carregant les dades actuals del producte.
    @GetMapping("/productes/editar/{id}")
    public String mostrarFormulariEditar(@PathVariable("id") Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productes/formulari";
    }

    // Elimina el producte segons la política configurada al servei.
    @GetMapping("/productes/eliminar/{id}")
    public String eliminarProducte(@PathVariable("id") Long id) {
        productoService.eliminar(id);
        return "redirect:/";
    }
}