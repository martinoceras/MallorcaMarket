package com.mallorcamarket.service;

import com.mallorcamarket.model.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario registrar(Usuario usuario);
    Usuario buscarPorEmail(String email);
    List<Usuario> listarTodos(); // Promesa 1
    void cambiarEstado(Long id);  // Promesa 2
}
