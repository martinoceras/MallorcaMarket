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
        // Punt clau d'autenticació: convertim l'email en un usuari intern de la BD.
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuari no trobat: " + email);
        }

        // Adaptem el nostre model Usuario al tipus User que espera Spring Security.
        return new User(
                usuario.getEmail(),
                usuario.getPassword(), // La contrasenya ja està xifrada amb BCrypt.
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getRol()))
        );
    }
}