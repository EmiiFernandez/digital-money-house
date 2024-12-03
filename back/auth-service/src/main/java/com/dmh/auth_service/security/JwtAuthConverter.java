package com.dmh.auth_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @Value("${keycloak.client-id}")
    private String clientId;

    public JwtAuthConverter() {
        this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        this.jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities, extractPrincipal(jwt));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        return Optional.ofNullable(jwtGrantedAuthoritiesConverter.convert(jwt))
                .map(authorities -> authorities.stream()
                        .filter(this::isValidAuthority)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    private boolean isValidAuthority(GrantedAuthority authority) {
        return authority != null &&
                authority.getAuthority() != null &&
                !authority.getAuthority().isEmpty();
    }

    private String extractPrincipal(Jwt jwt) {
        return jwt.getClaimAsString("preferred_username");
    }
}
