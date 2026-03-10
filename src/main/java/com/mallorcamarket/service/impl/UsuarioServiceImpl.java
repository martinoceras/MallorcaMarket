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

    // Accés a persistència d'usuaris.
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Component de xifrat utilitzat abans de desar contrasenyes.
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Usuario registrar(Usuario usuario) {
        // 1) Xifrem la contrasenya en text pla abans de persistir-la.
        String passwordEncriptada = encoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        // 2) Si no arriba cap rol, assignem perfil CLIENT per defecte.
        if (usuario.getRol() == null) {
            usuario.setRol("ROLE_CLIENT");
        }

        // 3) Activem el compte en el moment del registre.
        usuario.setEnabled(true);

        // 4) Persistim l'usuari a la base de dades.
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        // Consulta principal utilitzada per autenticació i identificació de perfil.
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> listarTodos() {
        // Llistat global per a la pantalla d'administració.
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional // Garantim consistència si hi ha concurrència en canvis d'estat.
    public void cambiarEstado(Long id) {
        // Recuperem l'usuari i commutem l'estat actiu/inactiu.
        Usuario u = usuarioRepository.findById(id).orElse(null);

        if (u != null) {
            u.setEnabled(!u.isEnabled());
            usuarioRepository.save(u);
        }
    }
}
