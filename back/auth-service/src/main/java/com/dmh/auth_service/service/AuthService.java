package com.dmh.auth_service.service;

import com.dmh.auth_service.config.KeycloakClientConfiguration;
import com.dmh.auth_service.config.KeycloakProperties;
import com.dmh.auth_service.dto.TokenRequest;

import com.dmh.auth_service.exceptions.*;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.dmh.auth_service.dto.TokenResponse;
import com.dmh.auth_service.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;


@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class AuthService implements IAuthService {

    private final KeycloakProperties keycloakProperties;
    private final KeycloakClientConfiguration keycloakClientConfiguration;
    private final KeycloakService keycloakService;

    @Override
    @Transactional
    public ResponseEntity<?> registerUserCredentials(TokenRequest tokenRequest) {
        log.debug("Processing registration for user: {}", tokenRequest.email());

        try {
            Keycloak keycloak = keycloakClientConfiguration.initializeKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();

            // Verificar existencia del usuario
            if (userExists(usersResource, tokenRequest.email())) {
                log.warn("User already exists: {}", tokenRequest.email());
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "User already exists"));
            }

            // Crear el usuario
            UserRepresentation userRepresentation = createUserRepresentation(tokenRequest);
            Response response = usersResource.create(userRepresentation);

            log.debug("Create user response: Status: {}, Headers: {}", response.getStatus(), response.getLocation());

            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                assignDefaultRole(realmResource, usersResource, userId);
                log.info("User registered successfully with ID: {}", userId);

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(Map.of(
                                "message", "User registered successfully",
                                "userId", userId
                        ));
            }

            log.error("Failed to create user. Status: {}, Body: {}", response.getStatus(), response.readEntity(String.class));
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to register user"));

        } catch (Exception ex) {
            log.error("Error during user registration: {}", ex.getMessage(), ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration error: " + ex.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> authenticateUser(TokenRequest tokenRequest) {
        log.debug("Processing authentication for user: {}", tokenRequest.email());

        try {
            // Usar KeycloakService en lugar del AdminClient
            TokenResponse tokenResponse = keycloakService.getTokens(tokenRequest);

            if (tokenResponse == null || tokenResponse.token() == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ConflictException("Invalid credentials"));
            }

            return ResponseEntity.ok(tokenResponse);

        } catch (Exception ex) {
            log.error("Authentication failed: {}", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ConflictException("Invalid credentials"));
        }
    }

    @Override
    public ResponseEntity<?> logoutUser(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", keycloakProperties.getClientId());
            map.add("client_secret", keycloakProperties.getClientSecret());
            map.add("token", token);

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(map, headers);

            String logoutUrl = keycloakProperties.getAuthServerUrl() +
                    "/realms/" + keycloakProperties.getRealm() +
                    "/protocol/openid-connect/logout";

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity(logoutUrl, request, String.class);

            return ResponseEntity.ok(new ConflictException("Logged out successfully"));
        } catch (Exception ex) {
            log.error("Logout failed: {}", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ConflictException("Logout failed"));
        }

    }


    @Override
    public ResponseEntity<?> validateToken(String token) {
        log.debug("Processing token validation request");

        try {
            // Decodificar y validar el token
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            Date expirationTime = claims.getExpirationTime();
            if (expirationTime != null && expirationTime.before(new Date())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ConflictException("Token has expired"));
            }

            String issuer = claims.getIssuer();
            if (!issuer.equals(keycloakProperties.getServerUrl() + "/realms/" + keycloakProperties.getRealm())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ConflictException("Invalid token issuer"));
            }

            return ResponseEntity.ok(new ConflictException("Token is valid"));

        } catch (Exception ex) {
            log.error("Token validation failed: {}", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ConflictException("Invalid token"));
        }
    }

    private boolean userExists(UsersResource usersResource, String email) {
        return !usersResource.search(email).isEmpty();
    }

    private UserRepresentation createUserRepresentation(TokenRequest tokenRequest) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(tokenRequest.email());
        user.setEmail(tokenRequest.email());
        user.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(tokenRequest.password());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        return user;
    }

    private void assignDefaultRole(RealmResource realmResource, UsersResource usersResource, String userId) {
        UserResource userResource = usersResource.get(userId);
        RoleRepresentation userRole = realmResource.roles()
                .get("user-role")
                .toRepresentation();

        userResource.roles().realmLevel().add(Collections.singletonList(userRole));
    }

    public void deleteUser(String keycloakId) {
        try {
            // Inicializa el cliente de Keycloak
            Keycloak keycloak = keycloakClientConfiguration.initializeKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();

            // Intentar eliminar el usuario directamente usando su ID
            usersResource.delete(keycloakId);
            log.info("Successfully deleted user from Keycloak. UserId: {}", keycloakId);
        } catch (NotFoundException e) {
            log.warn("User not found in Keycloak during cleanup: {}", keycloakId);
            throw new NotFoundException("User not found in auth service");
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak. UserId: {}", keycloakId, e);
            throw new InternalServerErrorException("Failed to delete user from auth service");
        }
    }

    public String getUserIdFromKeycloak(String email) {
        try {
            Keycloak keycloak = keycloakClientConfiguration.initializeKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            List<UserRepresentation> users = realmResource.users().search(email, null, null, null, 0, 1);

            if (!users.isEmpty()) {
                String keycloakUserId = users.get(0).getId();
                log.debug("Found Keycloak user ID: {} for email: {}", keycloakUserId, email);
                return keycloakUserId;
            }

            log.warn("No user found in Keycloak for email: {}", email);
            throw new NotFoundException("User not found in Keycloak");

        } catch (Exception e) {
            log.error("Error searching for user in Keycloak: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to retrieve user from Keycloak");
        }
    }

}
