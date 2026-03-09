package com.mallorcamarket.controller;

import com.mallorcamarket.model.Categoria;
import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.CategoriaService;
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
    @Autowired private CategoriaService categoriaService;

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
                                @AuthenticationPrincipal UserDetails currentUser,
                                RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        Producto producto = productoService.buscarPorId(id);

        if (isNotOwnedByProveedor(producto, proveedor)) {
            ra.addFlashAttribute("error", "No pots editar aquest producte.");
            return "redirect:/proveedor/products";
        }

        producto.setPrecio(precio);
        producto.setStock(stock);
        productoService.guardar(producto);
        ra.addFlashAttribute("success", "Dades actualitzades.");
        return "redirect:/proveedor/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails currentUser,
                                RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        Producto p = productoService.buscarPorId(id);

        if (isNotOwnedByProveedor(p, proveedor)) {
            ra.addFlashAttribute("error", "No pots eliminar aquest producte.");
            return "redirect:/proveedor/products";
        }

        p.setActivo(false);
        productoService.guardar(p);
        ra.addFlashAttribute("success", "Producte enviat a la paperera.");
        return "redirect:/proveedor/products";
    }

    @GetMapping("/products/restore/{id}")
    public String restoreProduct(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails currentUser,
                                 RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        Producto p = productoService.buscarPorId(id);

        if (isNotOwnedByProveedor(p, proveedor)) {
            ra.addFlashAttribute("error", "No pots restaurar aquest producte.");
            return "redirect:/proveedor/products";
        }

        p.setActivo(true);
        productoService.guardar(p);
        ra.addFlashAttribute("success", "Producte restaurat.");
        return "redirect:/proveedor/products";
    }

    @PostMapping("/products/permanent-delete/{id}")
    public String permanentDeleteProduct(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails currentUser,
                                         RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        Producto p = productoService.buscarPorId(id);

        if (isNotOwnedByProveedor(p, proveedor)) {
            ra.addFlashAttribute("error", "No pots eliminar aquest producte.");
            return "redirect:/proveedor/products";
        }

        productoService.eliminar(id);
        ra.addFlashAttribute("success", "Producte eliminat permanentment de la base de dades.");
        return "redirect:/proveedor/products";
    }

    @PostMapping("/products/toggle-visible/{id}")
    public String toggleVisibility(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails currentUser,
                                   RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        Producto p = productoService.buscarPorId(id);

        if (isNotOwnedByProveedor(p, proveedor)) {
            ra.addFlashAttribute("error", "No pots modificar aquest producte.");
            return "redirect:/proveedor/products";
        }

        p.setVisible(!Boolean.TRUE.equals(p.getVisible()));
        productoService.guardar(p);
        String status = Boolean.TRUE.equals(p.getVisible()) ? "públic" : "privat";
        ra.addFlashAttribute("success", "Producte marcat com a " + status + ".");
        return "redirect:/proveedor/products";
    }

    // --- 2. VENDES REBUDES (LA NOVA FUNCIONALITAT) ---
    @GetMapping("/orders")
    public String myOrders(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        List<Pedido> comandes = pedidoService.buscarPorProveedorFiltered(proveedor);
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
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "proveedor/product-form"; // crear HTML específic per a aquest formulari (templates/proveedor/product-form.html)
    }

    // --- OBRIR FORMULARI D'EDICIÓ (Amb dades ja posades) ---
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id,
                                      Model model,
                                      @AuthenticationPrincipal UserDetails currentUser,
                                      RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());
        Producto producto = productoService.buscarPorId(id);

        if (isNotOwnedByProveedor(producto, proveedor)) {
            ra.addFlashAttribute("error", "No pots editar aquest producte.");
            return "redirect:/proveedor/products";
        }

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "proveedor/product-form";
    }

    // --- DESAR LES DADES DEL FORMULARI ---
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Producto formProducto,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());

        Producto productoToSave;
        if (formProducto.getId() == null) {
            // Producte nou
            productoToSave = new Producto();
            productoToSave.setActivo(true);
            productoToSave.setVisible(true);
        } else {
            // Edicio d'un producte existent
            productoToSave = productoService.buscarPorId(formProducto.getId());
            if (isNotOwnedByProveedor(productoToSave, proveedor)) {
                ra.addFlashAttribute("error", "No pots modificar aquest producte.");
                return "redirect:/proveedor/products";
            }
        }

        // Actualitza camps editables i conserva proveidor/flags dels productes existents.
        productoToSave.setNombre(formProducto.getNombre());
        productoToSave.setDescripcion(formProducto.getDescripcion());
        productoToSave.setPrecio(formProducto.getPrecio());
        productoToSave.setStock(formProducto.getStock());
        productoToSave.setImageUrl(formProducto.getImageUrl());
        productoToSave.setProveedor(proveedor);

        Long categoriaId = formProducto.getCategoria() != null ? formProducto.getCategoria().getId() : null;
        if (categoriaId == null) {
            ra.addFlashAttribute("error", "Has de seleccionar una categoria.");
            return "redirect:/proveedor/products";
        }

        Categoria categoria = categoriaService.buscarPorId(categoriaId);
        if (categoria == null) {
            ra.addFlashAttribute("error", "La categoria seleccionada no existeix.");
            return "redirect:/proveedor/products";
        }
        productoToSave.setCategoria(categoria);

        productoService.guardar(productoToSave);
        ra.addFlashAttribute("success", "Producte desat correctament.");
        return "redirect:/proveedor/products";
    }

    private boolean isNotOwnedByProveedor(Producto producto, Usuario proveedor) {
        return producto == null
                || producto.getProveedor() == null
                || proveedor == null
                || !producto.getProveedor().getId().equals(proveedor.getId());
    }
}

