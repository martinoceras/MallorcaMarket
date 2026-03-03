package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.repository.UsuarioRepository;
import com.mallorcamarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Utilitzem BCrypt per complir el requisit RNF-02
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Usuario registrar(Usuario usuario) {
        // 1. Encriptem la contrasenya escrita per l'usuari
        String passwordEncriptada = encoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        // 2. Per defecte, els nous registres són ROLE_CLIENT si no es diu el contrari
        if (usuario.getRol() == null) {
            usuario.setRol("ROLE_CLIENT");
        }

        // 3. Guardem a MySQL [cite: 696]
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}