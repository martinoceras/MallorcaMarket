package com.mallorcamarket.security;

import com.mallorcamarket.model.Usuario;
import com.mallorcamarket.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Busquem l'usuari pel seu email a la base de dades
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuari no trobat: " + email);
        }

        // 2. Retornem un objecte User de Spring Security amb les dades de la nostra BD
        return new User(
                usuario.getEmail(),
                usuario.getPassword(), // Aquesta ja estarà encriptada amb BCrypt
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getRol()))
        );
    }
}