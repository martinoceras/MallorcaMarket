package com.mallorcamarket.controller;

import com.mallorcamarket.model.LineaPedido;
import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.PedidoService;
import com.mallorcamarket.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired private PedidoService pedidoService;
    @Autowired private UsuarioService usuarioService;

    @GetMapping("/my-orders") // URL final: /orders/my-orders
    public String showMyOrders(Model model, Principal principal) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        List<Pedido> pedidos = pedidoService.buscarPorUsuario(usuario);
        model.addAttribute("pedidos", pedidos);
        return "orders/list";
    }

    @GetMapping("/details/{id}") // URL final: /orders/details/{id}
    public String showOrderDetails(@PathVariable Long id, Model model, Principal principal) {
        Pedido pedido = pedidoService.buscarPorId(id);
        if (pedido == null || !pedido.getUsuario().getEmail().equals(principal.getName())) {
            return "redirect:/orders/my-orders";
        }
        model.addAttribute("pedido", pedido);
        return "orders/details";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, Principal principal) {
        // Recuperem el carret de la sessió
        @SuppressWarnings("unchecked")
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");

        // Validem que el carret no estigui buit
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Recuperem l'usuari actual
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // Realitzem el pedido amb la lògica transaccional del servei
            pedidoService.realizarPedido(cart, usuario);

            // Esborrem el carret de la sessió després de confirmar la comanda
            session.removeAttribute("cart");

            // Redirigim a la pàgina d'èxit
            return "redirect:/orders/success";
        } catch (RuntimeException e) {
            // Si hi ha error (p.ex. estoc insuficient), redirigim al carret amb l'error
            return "redirect:/cart?error=" + e.getMessage();
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "orders/success";
    }
}