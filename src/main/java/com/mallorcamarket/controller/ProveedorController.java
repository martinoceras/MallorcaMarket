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

    // --- BLOC TFG: GESTIÓ DEL CATÀLEG DEL PROVEÏDOR ---
    // Aquest endpoint només mostra productes del compte autenticat.
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

        // Validació d'autorització: cada proveïdor només pot modificar els seus productes.
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

        // Baixa lògica: preserva històric i possibilita restauració.
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

        // Baixa física: només després d'una acció explícita de l'usuari.
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

        // Alterna visibilitat pública sense afectar l'estat actiu intern.
        p.setVisible(!Boolean.TRUE.equals(p.getVisible()));
        productoService.guardar(p);
        String status = Boolean.TRUE.equals(p.getVisible()) ? "públic" : "privat";
        ra.addFlashAttribute("success", "Producte marcat com a " + status + ".");
        return "redirect:/proveedor/products";
    }

    // --- BLOC TFG: VENDES REBUDES ---
    // Mostra només la part de comandes que correspon al proveïdor autenticat.
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
        // Control de pertinença per evitar canvis d'estat en comandes alienes.
        if (pedido != null && pedidoService.pertanyAlProveedor(pedido, proveedor)) {
            pedido.setEstado("ENVIAT");
            pedidoService.guardar(pedido);
            ra.addFlashAttribute("success", "Comanda #" + id + " enviada!");
        }
        return "redirect:/proveedor/orders";
    }

    // --- BLOC TFG: FORMULARI D'ALTA I EDICIÓ DE PRODUCTE ---
    @GetMapping("/products/new")
    public String showNewProductForm(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "proveedor/product-form"; // Formulari compartit entre alta i edició.
    }

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

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Producto formProducto,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes ra) {
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());

        Producto productoToSave;
        if (formProducto.getId() == null) {
            // Flux d'alta: inicialitzam valors de domini per defecte.
            productoToSave = new Producto();
            productoToSave.setActivo(true);
            productoToSave.setVisible(true);
        } else {
            // Flux d'edició: recuperam entitat persistent per evitar pèrdua de camps.
            productoToSave = productoService.buscarPorId(formProducto.getId());
            if (isNotOwnedByProveedor(productoToSave, proveedor)) {
                ra.addFlashAttribute("error", "No pots modificar aquest producte.");
                return "redirect:/proveedor/products";
            }
        }

        // Estratègia de patch: només s'actualitzen camps exposats al formulari.
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

        // Validació defensiva davant categories inexistents o IDs manipulats.
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

    // Mètode auxiliar de seguretat per centralitzar la comprovació de propietat.
    private boolean isNotOwnedByProveedor(Producto producto, Usuario proveedor) {
        return producto == null
                || producto.getProveedor() == null
                || proveedor == null
                || !producto.getProveedor().getId().equals(proveedor.getId());
    }
}

