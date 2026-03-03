package com.mallorcamarket.controller;

import com.mallorcamarket.model.Producto;
import com.mallorcamarket.model.LineaPedido;
import com.mallorcamarket.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart") // Ruta base per a totes les operacions del carret
public class CartController {

    @Autowired
    private ProductoService productoService;

    /**
     * Mostra la vista del carret recuperant la llista de la sessió.
     */
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
        }

        model.addAttribute("cart", cart);

        // Calculem el total sumant el preu * quantitat de cada línia
        BigDecimal total = cart.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("total", total);

        return "cart/view"; // Retorna el fitxer templates/cart/view.html
    }

    /**
     * Afegeix un producte o incrementa la quantitat si ja existeix.
     */
    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session) {
        // Recuperem la llista actual de la sessió
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");

        // Si no existeix cap carret, el creem de nou
        if (cart == null) {
            cart = new ArrayList<>();
        }

        // Busquem si el producte ja està a la llista per no duplicar files
        boolean trobat = false;
        for (LineaPedido item : cart) {
            if (item.getProducto().getId().equals(id)) {
                item.setCantidad(item.getCantidad() + 1); // Incrementem quantitat
                trobat = true;
                break;
            }
        }

        // Si no l'hem trobat, el busquem a la BD i l'afegim com a nova línia
        if (!trobat) {
            Producto producto = productoService.buscarPorId(id);
            if (producto != null) {
                LineaPedido newItem = new LineaPedido();
                newItem.setProducto(producto);
                newItem.setCantidad(1);
                newItem.setPrecioUnitario(producto.getPrecio());
                cart.add(newItem);
            }
        }

        // Tornem a guardar la llista actualitzada a la sessió (SENSE INSERT A BD)
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }

    /**
     * Elimina completament un producte del carret.
     */
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getProducto().getId().equals(id));
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    /**
     * Actualitza la quantitat des de l'input numèric de la vista.
     */
    @PostMapping("/update")
    public String updateQuantity(@RequestParam("id") Long id,
                                 @RequestParam("cantidad") Integer cantidad,
                                 HttpSession session) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");
        if (cart != null) {
            for (LineaPedido item : cart) {
                if (item.getProducto().getId().equals(id)) {
                    // Validem que hi hagi stock suficient i la quantitat sigui positiva
                    if (cantidad > 0 && cantidad <= item.getProducto().getStock()) {
                        item.setCantidad(cantidad);
                    }
                    break;
                }
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }
}