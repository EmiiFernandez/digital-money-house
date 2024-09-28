package com.dmh.auth_service.service.impl;

import com.dmh.auth_service.config.KeycloakConfig;
import com.dmh.auth_service.model.AuthResponse;
import com.dmh.auth_service.model.NewUserRecord;
import com.dmh.auth_service.service.AuthService;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AuthServiceImpl implements AuthService {

    @Value("${app.keycloak.realm}")
    private String realm;

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void registerUser(NewUserRecord newUser) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(newUser.username());
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(newUser.firstName());
        userRepresentation.setLastName(newUser.lastName());

        // Ajusta los atributos al formato esperado por Keycloak
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("password", Collections.singletonList(newUser.password()));
        userRepresentation.setAttributes(attributes);

        Response response = keycloak.realm(keycloakConfig.getRealm())
                .users()
                .create(userRepresentation);

        if (response.getStatus() != 201) { // 201 Created
            throw new RuntimeException("Failed to register user: " + response.getStatusInfo());
        }
    }

    @Override
    public AuthResponse login(NewUserRecord loginRequest) {
        String keycloakTokenUrl = keycloakConfig.getServerUrl() + "/realms/" + keycloakConfig.getRealm() + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakConfig.getClientId());
        body.add("client_secret", keycloakConfig.getClientSecret());
        body.add("grant_type", "password");
        body.add("username", loginRequest.username());
        body.add("password", loginRequest.password());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(keycloakTokenUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> tokenData = response.getBody();
            return new AuthResponse(tokenData.get("access_token"), tokenData.get("refresh_token"));
        } else {
            throw new RuntimeException("Failed to authenticate");
        }
    }

    @Override
    public void logout(String refreshToken) {
        // URL para revocar el token en Keycloak
        String logoutUrl = keycloakConfig.getServerUrl() + "/realms/" + keycloakConfig.getRealm() + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakConfig.getClientId());
        body.add("client_secret", keycloakConfig.getClientSecret());
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(logoutUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() != HttpStatus.NO_CONTENT) { // 204 No Content
            throw new RuntimeException("Failed to logout: " + response.getStatusCode());
        }
    }
}