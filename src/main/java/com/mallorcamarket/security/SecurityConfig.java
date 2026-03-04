package com.mallorcamarket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutes Públiques: Qualsevol pot veure la botiga i els recursos estàtics
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // 2. Rutes d'Administrador: Només usuaris amb ROLE_ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 3. Rutes de Proveïdor: Només usuaris amb ROLE_PROVIDER
                        .requestMatchers("/proveedor/**").hasRole("PROVIDER")

                        // 4. Rutes d'Usuari Loguejat: Carret i Comandes (Client, Admin o Provider)
                        .requestMatchers("/cart/**", "/orders/**").authenticated()

                        // 5. Qualsevol altra petició requereix estar autenticat
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true) // On va l'usuari quan fa login correctament
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // On va l'usuari quan tanca sessió
                        .permitAll()
                );

        return http.build();
    }
}