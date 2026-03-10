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

    // Mostra la vista de registre inicialitzant un model d'usuari buit.
    @GetMapping("/register")
    public String mostrarFormulariRegistre(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register";
    }

    // Processa el registre i delega la lògica de seguretat al servei d'usuaris.
    @PostMapping("/register")
    public String registrarUsuari(Usuario usuario) {
        usuarioService.registrar(usuario);
        return "redirect:/login?success"; // Redirigim al login amb missatge d'èxit
    }

    // Mostra el formulari de login personalitzat de Spring Security.
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

}