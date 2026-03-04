package com.mallorcamarket.controller;

import com.mallorcamarket.model.LineaPedido;
import com.mallorcamarket.model.Pedido;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.PedidoService;
import com.mallorcamarket.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    // Aquest mètode rep la petició del botó "Confirmar Pedido" de la vista del carret [cite: 421]
    @PostMapping("/checkout")
    public String checkout(HttpSession session, Principal principal) {
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Recuperem l'usuari loguejat de la base de dades
        String email = principal.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        try {
            // Cridem al servei que hem arreglat abans (el que restava l'estoc)
            pedidoService.realizarPedido(cart, usuario);

            // Netegem el carret de la sessió perquè la compra ja s'ha fet
            session.removeAttribute("cart");

            return "redirect:/orders/success";
        } catch (Exception e) {
            // Si hi ha un error (per exemple, si algú ha comprat l'última unitat just abans)
            return "redirect:/cart?error=stock";
        }
    }
    @GetMapping("/success")
    public String showSuccess() {
        return "orders/success"; // Això busca el fitxer templates/orders/success.html
    }
    @GetMapping("/my-orders")
    public String showMyOrders(Model model, Principal principal) {
        // 1. Identifiquem qui és l'usuari loguejat
        String email = principal.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        // 2. Recuperem les seves comandes
        List<Pedido> pedidos = pedidoService.buscarPorUsuario(usuario);

        // 3. Passem la llista a la vista
        model.addAttribute("pedidos", pedidos);

        return "orders/list"; // Crearem aquest fitxer a templates/orders/list.html
    }

    @GetMapping("/details/{id}")
    public String showOrderDetails(@PathVariable Long id, Model model, Principal principal) {
        Pedido pedido = pedidoService.buscarPorId(id);

        // Seguretat: Verifiquem que la comanda existeixi i sigui de l'usuari loguejat
        if (pedido == null || !pedido.getUsuario().getEmail().equals(principal.getName())) {
            return "redirect:/orders/my-orders";
        }

        model.addAttribute("pedido", pedido);
        return "orders/details"; // Crearàs templates/orders/details.html
    }
}