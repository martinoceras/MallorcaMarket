package com.mallorcamarket.controller;

import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // Mostra el formulari de registre [cite: 699, 1269]
    @GetMapping("/register")
    public String mostrarFormulariRegistre(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register"; // Ubicació a templates
    }

    // Rep les dades del formulari i les envia al servei [cite: 702, 1262]
    @PostMapping("/register")
    public String registrarUsuari(Usuario usuario) {
        usuarioService.registrar(usuario);
        return "redirect:/login?success"; // Redirigim al login amb missatge d'èxit
    }
    @GetMapping("/login")
    public String login() {
        return "login/login"; // Això buscarà src/main/resources/templates/login.html
    }

}