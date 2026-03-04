package com.mallorcamarket.service.impl;

import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.repository.UsuarioRepository;
import com.mallorcamarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    // Injectem el repositori per poder parlar amb la base de dades
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Eina per encriptar contrasenyes (Requisit RNF-02: Seguretat)
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Usuario registrar(Usuario usuario) {
        // 1. Agafem la contrasenya que l'usuari ha escrit i l'encriptem
        String passwordEncriptada = encoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        // 2. Si l'usuari no té rol, li posem ROLE_CLIENT per defecte
        if (usuario.getRol() == null) {
            usuario.setRol("ROLE_CLIENT");
        }

        // 3. Molt important: el posem com a "enabled" (actiu) perquè pugui fer login res més registrar-se
        usuario.setEnabled(true);

        // 4. Guardem les dades a la taula 'usuarios' de MySQL
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        // Busquem un usuari concret fent servir el mètode que vam definir al repositori
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> listarTodos() {
        // Recuperem la llista de tots els usuaris (per al panell de l'Administrador)
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional // Aquesta anotació assegura que el canvi d'estat es guardi de forma segura a la BD
    public void cambiarEstado(Long id) {
        // 1. Busquem l'usuari pel seu ID (si no el troba, torna null)
        Usuario u = usuarioRepository.findById(id).orElse(null);

        if (u != null) {
            // 2. LÒGICA DE COMMUTACIÓ (Toggle):
            // Si isEnabled() és 'true' (actiu), amb l'exclamació (!) es torna 'false' (inactiu).
            // Si és 'false', es torna 'true'. Així el botó serveix per activar i desactivar.
            u.setEnabled(!u.isEnabled());

            // 3. Guardem el canvi d'estat a la base de dades
            usuarioRepository.save(u);
        }
    }
} // Final de la classe: ara tot està ben tancat!