package com.dmh.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> {
            web.ignoring().requestMatchers(
                    HttpMethod.POST,
                    "/social/public/**",
                    "/messaging/public/**",
                    "/api/auth/public/**",
                    "/api/auth/users"
            );
            web.ignoring().requestMatchers(
                    HttpMethod.GET,
                    "/social/public/**",
                    "/messaging/public/**",
                    "/api/auth/public/**"
            );
            web.ignoring().requestMatchers(
                    HttpMethod.DELETE,
                    "/social/public/**",
                    "/messaging/public/**",
                    "/api/auth/public/**"
            );
            web.ignoring().requestMatchers(
                    HttpMethod.PUT,
                    "/social/public/**",
                    "/messaging/public/**",
                    "/api/auth/public/**",
                    "/api/auth/users/{id}/send-verification-email",
                    "/api/auth/users/forgot-password"
            );
            web.ignoring().requestMatchers(
                            HttpMethod.OPTIONS,
                            "/**"
                    )
                    .requestMatchers("/v3/api-docs/**", "/configuration/**", "/swagger-ui/**",
                            "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/api-docs/**", "/actuator/health");

        };
    }

}
