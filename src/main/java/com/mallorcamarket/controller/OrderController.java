package com.mallorcamarket.controller;

import com.mallorcamarket.model.LineaPedido;
import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.PedidoService;
import com.mallorcamarket.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String checkout(HttpSession session, Authentication auth) {
        // 1. Obtenir el carret de la sessió [cite: 431]
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        // 2. Obtenir l'usuari que està loguejat actualment [cite: 424]
        String email = auth.getName();
        Usuario cliente = usuarioService.buscarPorEmail(email);

        try {
            // 3. Cridar al servei transaccional per processar la compra [cite: 425, 430]
            // Recorda que aquí dins es valida l'estoc (RF-08) i es resta de MySQL [cite: 432, 436]
            pedidoService.realizarPedido(cliente, cart);

            // 4. Si tot ha anat bé, buidem el carret de la sessió [cite: 437]
            session.removeAttribute("cart");

            // Redirigim a una pàgina d'èxit o a l'historial (RF-10) [cite: 221]
            return "redirect:/?success_order";

        } catch (RuntimeException e) {
            // Si el PedidoService llança un error (ex: no hi ha estoc), tornem al carret amb el missatge
            return "redirect:/cart?error=" + e.getMessage();
        }
    }
}