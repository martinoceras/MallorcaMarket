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
 * Controlador del backoffice d'administració.
 * Agrupa operacions de manteniment sobre usuaris, productes, categories i comandes.
 */
@Controller
@RequestMapping("/admin") // Prefix comú de totes les rutes del panell admin.
public class AdminController {

    // Serveis de domini consumits per les vistes del backoffice.
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
     * Carrega el llistat complet d'usuaris per a administració.
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/users";
    }

    /**
     * Commuta l'estat actiu/inactiu d'un usuari.
     */
    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id) {
        usuarioService.cambiarEstado(id);
        return "redirect:/admin/users";
    }

    // =========================================================================
    // GESTIÓ DE PRODUCTES
    // =========================================================================

    /**
     * Mostra la taula de productes amb el catàleg de categories disponible.
     */
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/products";
    }

    /**
     * Actualitza camps editables d'un producte des del panell admin.
     * Es fa càrrega prèvia d'entitat per evitar perdre informació no editable.
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
     * Dona d'alta una nova categoria des de la vista d'inventari.
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
     * Edita una categoria existent mantenint la validació bàsica del nom.
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
     * Vista global de comandes per a supervisió administrativa.
     */
    @GetMapping("/orders")
    public String listAllOrders(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "admin/orders";
    }
}