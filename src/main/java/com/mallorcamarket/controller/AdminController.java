package com.mallorcamarket.controller;

import com.mallorcamarket.model.Categoria;
import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.CategoriaService;
import com.mallorcamarket.service.PedidoService;
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
 * Gestiona el manteniment d'usuaris i productes.
 */
@Controller
@RequestMapping("/admin") // Totes les rutes d'aquest controlador requeriran el prefix /admin
public class AdminController {

    // Injecció de dependències dels serveis necessaris
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private CategoriaService categoriaService;

    // =========================================================================
    // GESTIÓ D'USUARIS
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
    // GESTIÓ DE PRODUCTES
    // =========================================================================

    /**
     * Mostra l'inventari de productes per a la seva gestió.
     */
    @GetMapping("/products")
    public String listProducts(Model model) {
        // Carreguem els productes per pintar la taula d'edició
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/products"; // Retorna templates/admin/products.html
    }

    /**
     * Actualitza les dades editables d'un producte sense perdre camps existents.
     */
    @PostMapping("/products/update")
    public String updateProduct(@RequestParam("id") Long id,
                                @RequestParam("precio") java.math.BigDecimal precio,
                                @RequestParam("stock") Integer stock,
                                @RequestParam("categoriaId") Long categoriaId,
                                RedirectAttributes ra) {
        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            ra.addFlashAttribute("error", "No s'ha trobat el producte.");
            return "redirect:/admin/products";
        }

        Categoria categoria = categoriaService.buscarPorId(categoriaId);
        if (categoria == null) {
            ra.addFlashAttribute("error", "La categoria seleccionada no existeix.");
            return "redirect:/admin/products";
        }

        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        productoService.guardar(producto);

        ra.addFlashAttribute("success", "El producte '" + producto.getNombre() + "' s'ha actualitzat correctament.");

        return "redirect:/admin/products";
    }

    /**
     * Crea una nova categoria per als productes.
     */
    @PostMapping("/categories/create")
    public String createCategory(@RequestParam("nombre") String nombre,
                                 @RequestParam(value = "descripcion", required = false) String descripcion,
                                 RedirectAttributes ra) {
        String cleanName = nombre != null ? nombre.trim() : "";
        if (cleanName.isEmpty()) {
            ra.addFlashAttribute("error", "El nom de la categoria es obligatori.");
            return "redirect:/admin/products";
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(cleanName);
        categoria.setDescripcion(descripcion != null ? descripcion.trim() : null);
        categoriaService.guardar(categoria);

        ra.addFlashAttribute("success", "Categoria creada correctament: " + cleanName + ".");
        return "redirect:/admin/products";
    }

    /**
     * Actualitza una categoria existent.
     */
    @PostMapping("/categories/update")
    public String updateCategory(@RequestParam("id") Long id,
                                 @RequestParam("nombre") String nombre,
                                 @RequestParam(value = "descripcion", required = false) String descripcion,
                                 RedirectAttributes ra) {
        Categoria categoria = categoriaService.buscarPorId(id);
        if (categoria == null) {
            ra.addFlashAttribute("error", "No s'ha trobat la categoria.");
            return "redirect:/admin/products";
        }

        String cleanName = nombre != null ? nombre.trim() : "";
        if (cleanName.isEmpty()) {
            ra.addFlashAttribute("error", "El nom de la categoria es obligatori.");
            return "redirect:/admin/products";
        }

        categoria.setNombre(cleanName);
        categoria.setDescripcion(descripcion != null ? descripcion.trim() : null);
        categoriaService.guardar(categoria);

        ra.addFlashAttribute("success", "Categoria actualitzada correctament: " + cleanName + ".");
        return "redirect:/admin/products";
    }

    // =========================================================================
    // GESTIÓ DE COMANDES
    // =========================================================================

    /**
     * Mostra totes les comandes de tots els proveïdors.
     */
    @GetMapping("/orders")
    public String listAllOrders(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "admin/orders";
    }
}