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

    @GetMapping("/my-orders") // Ruta d'historial personal de comandes.
    public String showMyOrders(Model model, Principal principal) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        List<Pedido> pedidos = pedidoService.buscarPorUsuario(usuario);
        model.addAttribute("pedidos", pedidos);
        return "orders/list";
    }

    @GetMapping("/details/{id}") // Vista de detall d'una comanda concreta.
    public String showOrderDetails(@PathVariable Long id, Model model, Principal principal) {
        Pedido pedido = pedidoService.buscarPorId(id);
        // Control d'accés: només el propietari de la comanda pot consultar el detall.
        if (pedido == null || !pedido.getUsuario().getEmail().equals(principal.getName())) {
            return "redirect:/orders/my-orders";
        }
        model.addAttribute("pedido", pedido);
        return "orders/details";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, Principal principal) {
        // Recuperem la cistella desada a sessió.
        @SuppressWarnings("unchecked")
        List<LineaPedido> cart = (List<LineaPedido>) session.getAttribute("cart");

        // Validació bàsica: no es pot tramitar una comanda sense línies.
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Resolem l'usuari autenticat per vincular-li la comanda.
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // Deleguem el procés transaccional de compra al servei.
            pedidoService.realizarPedido(cart, usuario);

            // Si tot ha anat bé, netegem la sessió de carret.
            session.removeAttribute("cart");

            return "redirect:/orders/success";
        } catch (RuntimeException e) {
            // En cas d'error funcional (p. ex. estoc), redirigim amb missatge.
            return "redirect:/cart?error=" + e.getMessage();
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "orders/success";
    }
}