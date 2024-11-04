package com.dmh.auth_service.service;

import com.dmh.auth_service.config.KeycloakClientConfiguration;
import com.dmh.auth_service.config.KeycloakProperties;
import com.dmh.auth_service.dto.TokenRequest;
import com.dmh.auth_service.dto.TokenResponse;
import com.dmh.auth_service.exceptions.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.util.JsonSerialization;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {
    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakClientConfiguration keycloakClientConfiguration;

    public TokenResponse getTokens(TokenRequest tokenRequest) {
        try {
            TokenManager tokenManager = keycloak.tokenManager();
            AccessTokenResponse response = tokenManager.grantToken();

            // Extract roles from the raw token
            Set<String> roles = extractRolesFromToken(response.getToken());

            return TokenResponse.builder()
                    .token(response.getToken())
                    .refreshToken(response.getRefreshToken())
                    .expiresIn(response.getExpiresIn())
                    .tokenType("Bearer")
                    .roles(roles)
                    .build();
        } catch (Exception e) {
            log.error("Error getting tokens from Keycloak", e);
            throw new InternalServerErrorException("Failed to authenticate with Keycloak");
        }
    }

    public void logout(String token) {
        try {
            keycloak.tokenManager().invalidate(token);
        } catch (Exception e) {
            log.error("Error during logout in Keycloak", e);
            throw new InternalServerErrorException("Failed to logout from Keycloak");
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractRolesFromToken(String token) {
        Set<String> roles = new HashSet<>();
        try {
            // Decode token payload (second part of JWT)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format");
            }

            // Decode the payload
            String decodedPayload = new String(java.util.Base64.getDecoder().decode(parts[1]));
            Map<String, Object> payload = JsonSerialization.readValue(decodedPayload, Map.class);

            // Extract realm roles
            Map<String, Object> realmAccess = (Map<String, Object>) payload.get("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                roles.addAll((Set<String>) realmAccess.get("roles"));
            }

            // Extract client roles
            Map<String, Object> resourceAccess = (Map<String, Object>) payload.get("resource_access");
            if (resourceAccess != null) {
                resourceAccess.forEach((client, access) -> {
                    if (access instanceof Map) {
                        Map<String, Object> clientAccess = (Map<String, Object>) access;
                        if (clientAccess.containsKey("roles")) {
                            roles.addAll((Set<String>) clientAccess.get("roles"));
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error extracting roles from token", e);
            throw new InternalServerErrorException("Failed to extract roles from token");
        }
        return roles;
    }
}
