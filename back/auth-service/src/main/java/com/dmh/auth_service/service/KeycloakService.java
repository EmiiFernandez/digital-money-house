package com.dmh.auth_service.service;

import com.dmh.auth_service.configuration.KeycloakProperties;
import com.dmh.auth_service.dto.TokenResponse;
import com.dmh.auth_service.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    private final WebClient webClient;

    public TokenResponse getTokens(String email, String password) {
        log.info("Requesting tokens for user: {}", email);

        String tokenUrl = keycloakProperties.getServerUrl() +
                "/realms/" + keycloakProperties.getRealm() +
                "/protocol/openid-connect/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
        formData.add(OAuth2Constants.CLIENT_ID, keycloakProperties.getClientId());
        formData.add(OAuth2Constants.CLIENT_SECRET, keycloakProperties.getClientSecret());
        formData.add("username", email);
        formData.add("password", password);

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::createTokenResponse)
                .onErrorMap(e -> {
                    log.error("Error during token request", e);
                    return new UnauthorizedException("Invalid credentials or Keycloak error");
                })
                .block();
    }

    public TokenResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");

        String tokenUrl = keycloakProperties.getServerUrl() +
                "/realms/" + keycloakProperties.getRealm() +
                "/protocol/openid-connect/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN);
        formData.add(OAuth2Constants.CLIENT_ID, keycloakProperties.getClientId());
        formData.add(OAuth2Constants.CLIENT_SECRET, keycloakProperties.getClientSecret());
        formData.add(OAuth2Constants.REFRESH_TOKEN, refreshToken);

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::createTokenResponse)
                .onErrorMap(e -> {
                    log.error("Error refreshing token", e);
                    return new InternalServerErrorException("Token refresh failed");
                })
                .block();
    }

    public void logoutUser(String accessToken) {
        log.info("Logging out user");

        String logoutUrl = keycloakProperties.getServerUrl() +
                "/realms/" + keycloakProperties.getRealm() +
                "/protocol/openid-connect/logout";

        webClient.post()
                .uri(logoutUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .toBodilessEntity()
                .onErrorMap(e -> {
                    log.error("Error during logout", e);
                    return new InternalServerErrorException("Logout failed");
                })
                .block();
    }

    private TokenResponse createTokenResponse(Map<String, Object> tokenData) {
        log.info("Creating token response");
        return TokenResponse.builder()
                .token((String) tokenData.get("access_token"))
                .refreshToken((String) tokenData.get("refresh_token"))
                .expiresIn(((Number) tokenData.getOrDefault("expires_in", 3600)).longValue())
                .tokenType((String) tokenData.getOrDefault("token_type", "Bearer"))
                .roles(extractRolesFromToken((String) tokenData.get("access_token")))
                .build();
    }

    private Set<String> extractRolesFromToken(String tokenString) {
        log.info("Extracting roles from token");
        try {
            AccessToken token = TokenVerifier.create(tokenString, AccessToken.class).getToken();
            Set<String> roles = new HashSet<>();

            if (token.getRealmAccess() != null) {
                roles.addAll(token.getRealmAccess().getRoles());
            }

            Map<String, AccessToken.Access> resourceAccess = token.getResourceAccess();
            if (resourceAccess != null) {
                AccessToken.Access clientAccess = resourceAccess.get(keycloakProperties.getClientId());
                if (clientAccess != null && clientAccess.getRoles() != null) {
                    roles.addAll(clientAccess.getRoles());
                }
            }

            return roles;
        } catch (Exception e) {
            log.error("Error extracting roles from token", e);
            return new HashSet<>();
        }
    }
}
