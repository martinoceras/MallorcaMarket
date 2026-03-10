package com.mallorcamarket.service;

import com.mallorcamarket.model.Usuario;
import java.util.List;

public interface UsuarioService {
    // Alta d'usuari amb regles bàsiques de seguretat (xifrat i rol per defecte).
    Usuario registrar(Usuario usuario);

    // Recupera un usuari per email, útil per autenticació i perfils.
    Usuario buscarPorEmail(String email);

    // Llista completa per a la vista d'administració.
    List<Usuario> listarTodos();

    // Commuta l'estat actiu/inactiu d'un usuari des del panell admin.
    void cambiarEstado(Long id);
}
