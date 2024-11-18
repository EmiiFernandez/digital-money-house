package com.dmh.auth_service.service;


import com.dmh.auth_service.config.KeycloakProperties;
import com.dmh.auth_service.dto.TokenRequest;
import com.dmh.auth_service.dto.TokenResponse;
import com.dmh.auth_service.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;
import org.keycloak.util.JsonSerialization;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {
    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;

    public TokenResponse getTokens(String email, String password) {
        log.debug("Requesting tokens for user: {}", email);

        try {
            String tokenUrl = keycloakProperties.getAuthServerUrl() +
                    "/realms/" + keycloakProperties.getRealm() +
                    "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
            form.add(OAuth2Constants.CLIENT_ID, keycloakProperties.getClientId());
            form.add(OAuth2Constants.CLIENT_SECRET, keycloakProperties.getClientSecret());
            form.add("username",email);
            form.add("password", password);
            form.add("scope", "openid offline_access"); // Add offline_access for refresh token

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    tokenUrl,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> tokenData = response.getBody();
                return TokenResponse.builder()
                        .token((String) tokenData.get("access_token"))
                        .refreshToken((String) tokenData.get("refresh_token"))
                        .expiresIn(((Number) tokenData.get("expires_in")).longValue())
                        .tokenType((String) tokenData.get("token_type"))
                        .roles(extractRolesFromToken((String) tokenData.get("access_token")))
                        .build();
            }

            throw new ConflictException("Failed to obtain tokens");
        } catch (Exception e) {
            log.error("Error getting tokens from Keycloak", e);
            throw new InternalServerErrorException("Authentication failed");
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        try {
            String tokenUrl = keycloakProperties.getAuthServerUrl() +
                    "/realms/" + keycloakProperties.getRealm() +
                    "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN);
            form.add(OAuth2Constants.CLIENT_ID, keycloakProperties.getClientId());
            form.add(OAuth2Constants.CLIENT_SECRET, keycloakProperties.getClientSecret());
            form.add(OAuth2Constants.REFRESH_TOKEN, refreshToken);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    tokenUrl,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> tokenData = response.getBody();
                return TokenResponse.builder()
                        .token((String) tokenData.get("access_token"))
                        .refreshToken((String) tokenData.get("refresh_token"))
                        .expiresIn(((Number) tokenData.get("expires_in")).longValue())
                        .tokenType((String) tokenData.get("token_type"))
                        .roles(extractRolesFromToken((String) tokenData.get("access_token")))
                        .build();
            }

            throw new ConflictException("Failed to refresh tokens");
        } catch (Exception e) {
            log.error("Error refreshing tokens from Keycloak", e);
            throw new InternalServerErrorException("Token refresh failed");
        }
    }



    public void logoutUser(String accessToken) {
        log.debug("Initiating logout for user with refresh token");

            // Construir URL del endpoint de logout
            String logoutUrl = keycloakProperties.getAuthServerUrl() +
                    "/realms/" + keycloakProperties.getRealm() +
                    "/protocol/openid-connect/logout";

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBearerAuth(accessToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    logoutUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Logout failed with status code: " + response.getStatusCode());
            }
        }




    private Set<String> extractRolesFromToken(String tokenString) {
        try {
            AccessToken token = TokenVerifier.create(tokenString, AccessToken.class).getToken();
            Set<String> roles = new HashSet<>();

            // Handle realm roles
            if (token.getRealmAccess() != null) {
                roles.addAll(token.getRealmAccess().getRoles());
            }

            // Handle resource/client roles
            Map<String, AccessToken.Access> resourceAccess = token.getResourceAccess();
            if (resourceAccess != null) {
                // Add roles for the current client
                AccessToken.Access clientAccess = resourceAccess.get(keycloakProperties.getClientId());
                if (clientAccess != null && clientAccess.getRoles() != null) {
                    roles.addAll(clientAccess.getRoles());
                }
            }

            return roles;
        } catch (Exception e) {
            log.error("Error extracting roles from token: {}", e.getMessage(), e);
            return new HashSet<>();
        }
    }
}
