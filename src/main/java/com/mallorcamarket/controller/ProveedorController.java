package com.mallorcamarket.controller;

import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.PedidoService;
import com.mallorcamarket.service.ProductoService;
import com.mallorcamarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;



@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PedidoService pedidoService;

    /**
     * Llista els productes del proveïdor autenticat.
     */
    @GetMapping("/products")
    public String myProducts(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        List<Producto> productos = productoService.buscarPorProveedor(proveedor);
        model.addAttribute("productos", productos);
        return "proveedor/products";
    }

    /**
     * Actualització ràpida de preu i estoc des de la llista.
     */
    @PostMapping("/products/update")
    public String updateProduct(@RequestParam("id") Long id,
                                @RequestParam("precio") BigDecimal precio,
                                @RequestParam("stock") Integer stock,
                                RedirectAttributes ra) {
        Producto producto = productoService.buscarPorId(id);
        if (producto != null) {
            producto.setPrecio(precio);
            producto.setStock(stock);
            productoService.guardar(producto);
            ra.addFlashAttribute("success", "Dades actualitzades correctament.");
        }
        return "redirect:/proveedor/products";
    }

    /**
     * Formulari per a nou producte.
     */
    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "proveedor/product-form";
    }

    /**
     * Guarda un producte (nou o editat).
     */
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Producto producto,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());

        producto.setProveedor(proveedor);

        // Si és un producte nou (id null), forcem els estats inicials
        if (producto.getId() == null) {
            producto.setActivo(true);
            producto.setVisible(true);
        }

        productoService.guardar(producto);
        ra.addFlashAttribute("success", "Producte desat correctament.");
        return "redirect:/proveedor/products";
    }

    /**
     * Formulari d'edició.
     */
    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);
        return "proveedor/product-form";
    }

    /**
     * Baixa lògica (Moure a la paperera).
     */
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        Producto producto = productoService.buscarPorId(id);
        if (producto != null) {
            producto.setActivo(false);
            productoService.guardar(producto);
            ra.addFlashAttribute("success", "Producte mogut a la paperera.");
        }
        return "redirect:/proveedor/products";
    }

    /**
     * Alterna la visibilitat (Ocultar/Mostrar a la botiga).
     */
    @GetMapping("/products/toggle-visible/{id}")
    public String toggleVisible(@PathVariable Long id, RedirectAttributes ra) {
        Producto p = productoService.buscarPorId(id);
        if (p != null) {
            // Usem Boolean.TRUE.equals per evitar errors de compilació amb is/get de Lombok
            boolean estatActual = Boolean.TRUE.equals(p.getVisible());
            p.setVisible(!estatActual);

            productoService.guardar(p);
            String estat = p.getVisible() ? "visible" : "ocult";
            ra.addFlashAttribute("success", "El producte ara està " + estat);
        }
        return "redirect:/proveedor/products";
    }

    /**
     * Restaura un producte de la paperera.
     */
    @GetMapping("/products/restore/{id}")
    public String restoreProduct(@PathVariable Long id, RedirectAttributes ra) {
        Producto p = productoService.buscarPorId(id);
        if (p != null) {
            p.setActivo(true);
            productoService.guardar(p);
            ra.addFlashAttribute("success", "Producte restaurat!");
        }
        return "redirect:/proveedor/products";
    }
    @GetMapping("/orders")
    public String myOrders(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        // 1. Identifiquem el proveïdor
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());

        // 2. Busquem les comandes on hi ha productes seus
        // Hauràs de crear aquest mètode al teu PedidoService
        List<Pedido> comandes = pedidoService.buscarPorProveedor(proveedor);

        model.addAttribute("comandes", comandes);
        model.addAttribute("proveedor", proveedor); // Útil per filtrar dades a la vista
        return "proveedor/orders";
    }
}