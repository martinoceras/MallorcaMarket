package com.mallorcamarket.service;

import com.mallorcamarket.model.Usuario;

public interface UsuarioService {
    // Mètode per registrar un nou usuari al sistema
    Usuario registrar(Usuario usuario);

    // Mètode per cercar per email (útil per al login futur)
    Usuario buscarPorEmail(String email);
}