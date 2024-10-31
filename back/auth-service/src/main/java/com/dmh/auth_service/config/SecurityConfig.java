package com.dmh.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/auth/login", "/auth/refresh").permitAll() // Permitir estas rutas sin autenticación
                        .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                )
                .oauth2Login(withDefaults()); // Configuración de OAuth2 para login

        return http.build();
    }
}

