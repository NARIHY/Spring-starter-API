package com.tm_service.com.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactivation de CSRF avec la nouvelle syntaxe
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Permet l'accès à toutes les routes

        return http.build();
    }
}
