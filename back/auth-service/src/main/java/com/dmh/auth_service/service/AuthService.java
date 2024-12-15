package com.dmh.auth_service.service;

import com.dmh.auth_service.configuration.KeycloakClientConfiguration;
import com.dmh.auth_service.configuration.KeycloakProperties;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import com.dmh.auth_service.dto.TokenResponse;
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

    @Value("${keycloak.serverUrl}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Override
    @Transactional
    public ResponseEntity<?> registerUserCredentials(TokenRequest tokenRequest) {
        log.info("Starting user registration process for email: {}", tokenRequest.email());
        log.debug("Keycloak Server URL: {}, Realm: {}", keycloakServerUrl, keycloakRealm);

        if (tokenRequest == null || tokenRequest.email() == null || tokenRequest.password() == null) {
            log.error("Invalid registration request: missing required fields");
            throw new BadRequestException("Email and password are required");
        }

        try {
            Keycloak keycloak = keycloakClientConfiguration.initializeKeycloakAdmin();

            // Additional logging for troubleshooting
            log.debug("Keycloak Client initialized. Attempting to access realm: {}", keycloakProperties.getRealm());

            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());

            // Verify realm exists
            try {
                realmResource.toRepresentation();
            } catch (Exception e) {
                log.error("Realm does not exist or cannot be accessed: {}", keycloakProperties.getRealm());
                throw new InternalServerErrorException("Invalid Keycloak realm configuration");
            }

            UsersResource usersResource = realmResource.users();

            if (userExists(usersResource, tokenRequest.email())) {
                log.warn("User already exists: {}", tokenRequest.email());
                throw new ConflictException("User already exists");
            }

            UserRepresentation userRepresentation = createUserRepresentation(tokenRequest);

            Response response = usersResource.create(userRepresentation);

            log.debug("User creation response status: {}", response.getStatus());

            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                assignDefaultRole(realmResource, usersResource, userId);
                log.info("User registered successfully with ID: {}", userId);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ErrorResponse(userId + " User registered successfully"));
            }

            log.error("Failed to create user. Response status: {}, Response info: {}",
                    response.getStatus(), response.getStatusInfo());

            throw new InternalServerErrorException("Failed to register user. Status: " + response.getStatus());

        } catch (Exception e) {
            log.error("Comprehensive registration error", e);
            throw new InternalServerErrorException("Registration error: " + e.getMessage());
        }
    }

    @Override
    public TokenResponse authenticateUser(String email, String password) {
        if (email == null || password == null) {
            throw new BadRequestException("Email and password are required");
        }

        try {
            TokenResponse tokenResponse = keycloakService.getTokens(email, password);

            // Additional validation
            if (tokenResponse == null ||
                    StringUtils.isEmpty(tokenResponse.token()) ||
                    StringUtils.isEmpty(tokenResponse.refreshToken())) {
                throw new UnauthorizedException("Invalid authentication credentials");
            }

            return tokenResponse;
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Authentication failed: Invalid credentials", e);
            throw new UnauthorizedException("Invalid username or password");
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Authentication service unavailable");
        }
    }


    public ResponseEntity<?> validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadRequestException("Token is required for validation");
        }

        // Validación de configuración
        if (keycloakProperties.getServerUrl() == null || keycloakProperties.getRealm() == null) {
            log.error("Keycloak configuration is missing. AuthServerUrl: {}, Realm: {}",
                    keycloakProperties.getServerUrl(),
                    keycloakProperties.getRealm());
            throw new IllegalStateException("Keycloak configuration is missing");
        }

        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            // Validación de expiración
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                throw new BadRequestException("Token has expired");
            }

            // Construcción del issuer esperado
            String expectedIssuer = keycloakProperties.getServerUrl();
            if (!expectedIssuer.endsWith("/")) {
                expectedIssuer += "/";
            }
            expectedIssuer += "realms/" + keycloakProperties.getRealm();

            // Log para debugging
            String actualIssuer = claims.getIssuer();
            log.debug("Token validation - Expected issuer: {}", expectedIssuer);
            log.debug("Token validation - Actual issuer: {}", actualIssuer);

            if (!expectedIssuer.equals(actualIssuer)) {
                log.error("Issuer mismatch - Expected: {}, Got: {}", expectedIssuer, actualIssuer);
                throw new BadRequestException("Invalid token issuer");
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Token is valid",
                    "issuer", actualIssuer,
                    "expiration", expirationTime,
                    "claims", claims.getClaims()
            ));

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new BadRequestException("Token validation failed: " + e.getMessage());
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
        user.setEmailVerified(true); //false si quiero verificar email

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
                .get("USER")
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

    @Override
    public void logoutUser(String token) {
        log.debug("Calling Keycloak logout service");

        keycloakService.logoutUser(token);
    }
}
