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
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired private ProductoService productoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private PedidoService pedidoService;

    // --- 1. PRODUCTES (FUNCIONALITAT ORIGINAL RECUPERADA) ---
    @GetMapping("/products")
    public String myProducts(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        List<Producto> productos = productoService.buscarPorProveedor(proveedor);
        model.addAttribute("productos", productos);
        return "proveedor/products";
    }

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
            ra.addFlashAttribute("success", "Dades actualitzades.");
        }
        return "redirect:/proveedor/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        Producto p = productoService.buscarPorId(id);
        if (p != null) {
            p.setActivo(false);
            productoService.guardar(p);
            ra.addFlashAttribute("success", "Producte enviat a la paperera.");
        }
        return "redirect:/proveedor/products";
    }

    @GetMapping("/products/restore/{id}")
    public String restoreProduct(@PathVariable Long id, RedirectAttributes ra) {
        Producto p = productoService.buscarPorId(id);
        if (p != null) {
            p.setActivo(true);
            productoService.guardar(p);
            ra.addFlashAttribute("success", "Producte restaurat.");
        }
        return "redirect:/proveedor/products";
    }

    @PostMapping("/products/permanent-delete/{id}")
    public String permanentDeleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        Producto p = productoService.buscarPorId(id);
        if (p != null) {
            productoService.eliminar(id);
            ra.addFlashAttribute("success", "Producte eliminat permanentment de la base de dades.");
        }
        return "redirect:/proveedor/products";
    }

    @PostMapping("/products/toggle-visible/{id}")
    public String toggleVisibility(@PathVariable Long id, RedirectAttributes ra) {
        Producto p = productoService.buscarPorId(id);
        if (p != null) {
            p.setVisible(!p.getVisible());
            productoService.guardar(p);
            String status = p.getVisible() ? "públic" : "privat";
            ra.addFlashAttribute("success", "Producte marcat com a " + status + ".");
        }
        return "redirect:/proveedor/products";
    }

    // --- 2. VENDES REBUDES (LA NOVA FUNCIONALITAT) ---
    @GetMapping("/orders")
    public String myOrders(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        List<Pedido> comandes = pedidoService.buscarPorProveedor(proveedor);
        model.addAttribute("comandes", comandes);
        return "proveedor/orders";
    }

    @PostMapping("/orders/enviar/{id}")
    public String enviarPedido(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        Pedido pedido = pedidoService.buscarPorId(id);
        Usuario proveedor = usuarioService.buscarPorEmail(principal.getName());
        if (pedido != null && pedidoService.pertanyAlProveedor(pedido, proveedor)) {
            pedido.setEstado("ENVIAT");
            pedidoService.guardar(pedido);
            ra.addFlashAttribute("success", "Comanda #" + id + " enviada!");
        }
        return "redirect:/proveedor/orders";
    }
    // --- OBRIR FORMULARI DE NOU PRODUCTE ---
    @GetMapping("/products/new")
    public String showNewProductForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "proveedor/product-form"; // Anem a crear aquest HTML ara
    }

    // --- OBRIR FORMULARI D'EDICIÓ (Amb dades ja posades) ---
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);
        return "proveedor/product-form"; // Reutilitzem el mateix HTML
    }

    // --- DESAR LES DADES DEL FORMULARI ---
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Producto producto,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        producto.setProveedor(proveedor);

        // Si és nou, ens assegurem que estigui actiu
        if (producto.getId() == null) {
            producto.setActivo(true);
            producto.setVisible(true);
        }

        productoService.guardar(producto);
        ra.addFlashAttribute("success", "Producte desat correctament.");
        return "redirect:/proveedor/products";
    }
}