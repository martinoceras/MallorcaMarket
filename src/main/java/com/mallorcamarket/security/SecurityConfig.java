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

    // 1. Definim el codificador de contrasenyes (Requisit RNF-02)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Permetem accés total a la home, el registre i el login
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**").permitAll()
                        // El carret només per a usuaris loguejats
                        .requestMatchers("/cart/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Assegura't de tenir un controlador que retorni "login"
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}