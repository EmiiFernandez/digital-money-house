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

        if (tokenRequest == null || tokenRequest.email() == null || tokenRequest.password() == null) {
            log.error("Invalid registration request: missing required fields");
            throw new BadRequestException("Email and password are required");
        }

        log.debug("Processing registration for user: {}", tokenRequest.email());

        try {
            Keycloak keycloak = keycloakClientConfiguration.initializeKeycloakAdmin();
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();

            if (userExists(usersResource, tokenRequest.email())) {
                log.warn("User already exists: {}", tokenRequest.email());
                throw new ConflictException("User already exists");
            }

            UserRepresentation userRepresentation = createUserRepresentation(tokenRequest);
            Response response = usersResource.create(userRepresentation);

            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                assignDefaultRole(realmResource, usersResource, userId);
                log.info("User registered successfully with ID: {}", userId);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ErrorResponse(userId + " User registered successfully"));
            }

            log.error("Failed to create user. Status: {}", response.getStatus());
            throw new InternalServerErrorException("Failed to register user");

        } catch (ConflictException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Registration error: " + e.getMessage());
        }
    }

    @Override
    public TokenResponse authenticateUser(String email, String password) {
        if (email == null || password == null) {
            throw new BadRequestException("Email and password are required");
        }

        log.debug("Attempting authentication for user: {}", email);
        try {
            return keycloakService.getTokens(email, password);
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", email, e.getMessage());
            throw new UnauthorizedException("Authentication failed");
        }
    }


    @Override
    public void logoutUser(String token) {
        log.debug("Calling Keycloak logout service");

        keycloakService.logoutUser(token); // Usamos siempre el mismo token (Bearer o refresh)
    }

    @Override
    public ResponseEntity<?> validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadRequestException("Token is required for validation");
        }

        log.debug("Processing token validation request");

        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            // Validate expiration
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                throw new BadRequestException("Token has expired");
            }

            // Validate issuer
            String issuer = claims.getIssuer();
            String expectedIssuer = keycloakProperties.getServerUrl() + "/realms/" + keycloakProperties.getRealm();
            if (!expectedIssuer.equals(issuer)) {
                throw new BadRequestException("Invalid token issuer");
            }

            return ResponseEntity.ok(Map.of("message", "Token is valid"));

        } catch (BadRequestException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new BadRequestException("Invalid token");
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
