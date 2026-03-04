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
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        model.addAttribute("cart", cart);

        BigDecimal total = cart.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("total", total);

        // Atenció: Assegura't que el fitxer es digui "view.html" dins de "templates/cart/"
        return "cart/view";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        boolean trobat = false;
        for (LineaPedido item : cart) {
            if (item.getProducto().getId().equals(id)) {
                // Control d'estoc en afegir des de la botiga
                if (item.getCantidad() < item.getProducto().getStock()) {
                    item.setCantidad(item.getCantidad() + 1);
                }
                trobat = true;
                break;
            }
        }

        if (!trobat) {
            Producto producto = productoService.buscarPorId(id);
            if (producto != null && producto.getStock() > 0) {
                LineaPedido newItem = new LineaPedido();
                newItem.setProducto(producto);
                newItem.setCantidad(1);
                newItem.setPrecioUnitario(producto.getPrecio());
                cart.add(newItem);
            }
        }

        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // --- NOUS MÈTODES PER ALS BOTONS + I - DEL CARRET ---

    @GetMapping("/add-one/{id}")
    public String addOne(@PathVariable Long id, HttpSession session) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");
        if (cart != null) {
            for (LineaPedido item : cart) {
                if (item.getProducto().getId().equals(id)) {
                    // Verifiquem l'estoc real abans d'incrementar
                    if (item.getCantidad() < item.getProducto().getStock()) {
                        item.setCantidad(item.getCantidad() + 1);
                    }
                    break;
                }
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove-one/{id}")
    public String removeOne(@PathVariable Long id, HttpSession session) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");
        if (cart != null) {
            for (LineaPedido item : cart) {
                if (item.getProducto().getId().equals(id)) {
                    if (item.getCantidad() > 1) {
                        item.setCantidad(item.getCantidad() - 1);
                    } else {
                        // Si només queda 1, l'eliminem del carret
                        return "redirect:/cart/remove/" + id;
                    }
                    break;
                }
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getProducto().getId().equals(id));
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }
}