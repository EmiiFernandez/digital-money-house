package com.dmh.auth_service.controller;

import com.dmh.auth_service.dto.TokenRequest;
import com.dmh.auth_service.dto.TokenResponse;
import com.dmh.auth_service.exceptions.*;
import com.dmh.auth_service.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INTERNAL_SERVICE')")
    public ResponseEntity<?> registerUserCredentials(
            @Valid @RequestBody TokenRequest tokenRequest
    ) {
        log.info("User registration attempt for email: {}", tokenRequest.email());

        try {
            ResponseEntity<?> response = authService.registerUserCredentials(tokenRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (BadRequestException e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateUser(
            @Valid @RequestBody TokenRequest tokenRequest
    ) {
        log.info("Authentication attempt for email: {}", tokenRequest.email());

        try {
            TokenResponse tokenResponse = authService.authenticateUser(
                    tokenRequest.email(),
                    tokenRequest.password()
            );

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.token())
                    .body(tokenResponse);
        } catch (UnauthorizedException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        String accessToken = extractToken(authorizationHeader);

        log.info("Logout request initiated");

        try {
            authService.logoutUser(accessToken);
            return ResponseEntity
                    .ok()
                    .body(Map.of("message", "Logout successful"));
        } catch (Exception e) {
            log.error("Logout failed", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Logout failed"));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        String token = extractToken(authorizationHeader);

        log.info("Token validation request received");

        return authService.validateToken(token);
    }

    @DeleteMapping("/users/{keycloakId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INTERNAL-SERVICE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String keycloakId) {
        log.info("Admin-initiated user deletion for ID: {}", keycloakId);

        try {
            authService.deleteUser(keycloakId);
        } catch (Exception e) {
            log.error("User deletion failed", e);
            throw new InternalServerErrorException(
                    "Failed to delete user"
            );
        }
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getKeycloakUserId(@RequestParam String email) {
        log.info("Admin searching for Keycloak user ID by email");

        return Optional.ofNullable(authService.getUserIdFromKeycloak(email))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Utility method to extract token from Authorization header
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    // Optional method to get current authenticated user details
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return Optional.ofNullable(authentication)
                .map(auth -> ResponseEntity.ok().body(Map.of(
                        "username", auth.getName(),
                        "authorities", auth.getAuthorities()
                )))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
