package com.mallorcamarket.controller;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.Usuario;
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

@Controller
@RequestMapping("/proveedor") // Totes les rutes estaran sota /proveedor/...
public class ProveedorController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Llista NOMÉS els productes que pertanyen al proveïdor que ha fet login.
     * @AuthenticationPrincipal ens dóna l'usuari que està connectat actualment.
     */
    @GetMapping("/products")
    public String myProducts(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        // 1. Busquem l'usuari a la BD pel seu email (username)
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());

        // 2. Passem al model només els productes d'aquest proveïdor
        model.addAttribute("productos", productoService.buscarPorProveedor(proveedor));

        return "proveedor/products"; // Crearàs templates/proveedor/products.html
    }

    /**
     * Rep les dades del formulari de gestió de productes del proveïdor.
     * Actualitza el preu i l'estoc a la base de dades.
     */
    @PostMapping("/products/update")
    public String updateProduct(@RequestParam("id") Long id,
                                @RequestParam("precio") BigDecimal precio,
                                @RequestParam("stock") Integer stock,
                                RedirectAttributes ra) {

        // 1. Busquem el producte a la base de dades per l'ID
        Producto producto = productoService.buscarPorId(id);

        if (producto != null) {
            // 2. Actualitzem els valors amb les noves dades del formulari
            producto.setPrecio(precio);
            producto.setStock(stock);

            // 3. Desem els canvis mitjançant el servei
            productoService.guardar(producto);

            // 4. Missatge de confirmació per a l'usuari
            ra.addFlashAttribute("success", "S'ha actualitzat el preu a " + precio + "€ i l'estoc a " + stock + " unitats.");
        }

        return "redirect:/proveedor/products";
    }

    /**
     * Mètode per mostrar el formulari de creació de producte.
     */
    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        // Enviem un objecte buit que el formulari omplirà
        model.addAttribute("producto", new Producto());
        return "proveedor/product-form"; // Crearem aquesta plantilla
    }

    /**
     * Mètode per processar el guardat del nou producte.
     */
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Producto producto,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes ra) {

        // 1. Obtenim l'objecte Usuario del proveïdor que està connectat
        Usuario proveedor = usuarioService.buscarPorEmail(currentUser.getUsername());

        // 2. Assignem aquest usuari com a "amo" del producte (relació N:1)
        producto.setProveedor(proveedor);

        // 3. Ens assegurem que el producte estigui actiu per defecte
        producto.setActivo(true);

        // 4. Guardem a la base de dades mitjançant el servei
        productoService.guardar(producto);

        // 5. Missatge de confirmació per a la vista
        ra.addFlashAttribute("success", "El producte '" + producto.getNombre() + "' s'ha creat correctament.");

        return "redirect:/proveedor/products";
    }

    /**
     * Mostra el formulari d'edició amb les dades del producte ja carregades.
     */
    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        // 1. Busquem el producte a la BD
        Producto producto = productoService.buscarPorId(id);

        // 2. El passem al model. Com que té ID, el formulari sabrà que és una edició.
        model.addAttribute("producto", producto);

        // 3. Reutilitzem la mateixa vista de crear producte
        return "proveedor/product-form";
    }

    /**
     * Realitza una baixa lògica del producte.
     * En lloc d'esborrar-lo (DELETE), canviem el seu estat a 'activo = false'.
     */
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {

        // 1. Recuperem el producte de la base de dades pel seu ID
        Producto producto = productoService.buscarPorId(id);

        if (producto != null) {
            // 2. Canviem l'estat a fals (Baixa Lògica)
            // Això fa que deixi de sortir a la botiga gràcia al mètode findByActivoTrue()
            producto.setActivo(false);

            // 3. Guardem el canvi
            productoService.guardar(producto);

            // 4. Enviem feedback al proveïdor
            ra.addFlashAttribute("success", "El producte '" + producto.getNombre() + "' ha estat donat de baixa i ja no és visible a la botiga.");
        }

        return "redirect:/proveedor/products";
    }
}