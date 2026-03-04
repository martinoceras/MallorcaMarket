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
}