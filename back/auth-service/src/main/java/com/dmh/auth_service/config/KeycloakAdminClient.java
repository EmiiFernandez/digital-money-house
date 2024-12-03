package com.dmh.auth_service.config;

import com.dmh.auth_service.dto.TokenRequest;
import com.dmh.auth_service.exceptions.ConflictException;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KeycloakAdminClient {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakAdminClient.class);

    private final Keycloak keycloakAdmin;
    private final KeycloakProperties keycloakProperties;

    @Transactional
    public String createUserInKeycloak(TokenRequest tokenRequest) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(tokenRequest.email());
            user.setEmail(tokenRequest.email());
            user.setEnabled(true);
            validateUserCreation(user);
            Response response = keycloakAdmin.realm(keycloakProperties.getRealm())
                    .users()
                    .create(user);
            if (response.getStatus() != 201) {
                logger.error("Error al crear usuario en Keycloak: " + response.getStatus() + " - " + response.getStatusInfo());
                throw new ConflictException("Error creando usuario en Keycloak: " + response.getStatus());
            }
            String userId = extractUserIdFromResponse(response);
            setUserPassword(userId, tokenRequest.password());
            return userId;
        } catch (Exception e) {
            logger.error("Error en registro de Keycloak", e);
            throw new ConflictException("Registro fallido");
        }
    }

    private void validateUserCreation(UserRepresentation user) {
        if (StringUtils.isBlank(user.getUsername())) {
            throw new ValidationException("Nombre de usuario es requerido");
        }
    }

    private void setUserPassword(String userId, String password) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        credentials.setTemporary(false);
        keycloakAdmin.realm(keycloakProperties.getRealm())
                .users()
                .get(userId)
                .resetPassword(credentials);
    }

    private String extractUserIdFromResponse(Response response) {
        return response.getLocation()
                .getPath()
                .replaceAll(".*/([^/]+)$", "$1");

    }
}