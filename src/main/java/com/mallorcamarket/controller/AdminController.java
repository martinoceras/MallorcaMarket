package com.mallorcamarket.controller;

import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin") // Totes les rutes d'aquest controlador començaran per /admin
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    // Aquest mètode llista tots els usuaris a la taula
    @GetMapping("/users")
    public String listUsers(Model model) {
        // Recuperem la llista de la base de dades a través del servei
        List<Usuario> usuarios = usuarioService.listarTodos();

        // La passem a la vista (HTML) amb el nom "usuarios"
        model.addAttribute("usuarios", usuarios);

        return "admin/users"; // Busca el fitxer a templates/admin/users.html
    }

    // Aquest mètode rep l'ID d'un usuari i li inverteix l'estat (Actiu/Inactiu)
    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id) {
        // Cridem al servei per fer la lògica de canviar l'estat
        usuarioService.cambiarEstado(id);

        // Una vegada fet el canvi, recarreguem la pàgina de la llista
        return "redirect:/admin/users";
    }
}