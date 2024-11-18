package com.dmh.auth_service.controller;

import com.dmh.auth_service.dto.TokenRequest;
import com.dmh.auth_service.dto.TokenResponse;
import com.dmh.auth_service.exceptions.BadRequestException;
import com.dmh.auth_service.exceptions.InternalServerErrorException;
import com.dmh.auth_service.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping
    public ResponseEntity<?> registerUserCredentials(@Valid @RequestBody TokenRequest tokenRequest) {
        log.debug("Received registration request for user: {}", tokenRequest.email());
        log.debug("Headers: {}", RequestContextHolder.currentRequestAttributes());
        log.debug("Request path: {}", ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI());
        return authService.registerUserCredentials(tokenRequest);
    }

    @PostMapping("/login")
    public TokenResponse authenticateUser(@Valid @RequestBody TokenRequest tokenRequest) {
        log.debug("Received authentication request for user: {}", tokenRequest.email());
        return authService.authenticateUser(tokenRequest.email(), tokenRequest.password());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        log.debug("Logout request received");
        log.debug("Authorization header: {}", accessToken);

       authService.logoutUser(accessToken);

        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        log.debug("Received token validation request");
        return authService.validateToken(token.replace("Bearer ", ""));
    }

    @DeleteMapping("/users/{keycloakId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String keycloakId) {
        try {
            log.info("Deleting user credentials for userId: {}", keycloakId);
            authService.deleteUser(keycloakId);
        } catch (Exception e) {
            log.error("Error deleting user credentials for userId: {}", keycloakId, e);
            throw new InternalServerErrorException("Failed to delete user credentials");
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<String> getKeycloakUserId(@RequestParam String email) {
        log.debug("Searching for Keycloak user ID for email: {}", email);
        String userId = authService.getUserIdFromKeycloak(email);
        return ResponseEntity.ok(userId);
    }

}

