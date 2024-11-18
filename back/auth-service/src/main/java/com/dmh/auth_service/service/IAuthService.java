package com.dmh.auth_service.service;

import com.dmh.auth_service.dto.TokenRequest;
import com.dmh.auth_service.dto.TokenResponse;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<?> registerUserCredentials(TokenRequest tokenRequest);
    TokenResponse authenticateUser(String email, String password);
    void logoutUser(String token);
    ResponseEntity<?> validateToken(String token);
    void deleteUser(String keycloakId);
    String getUserIdFromKeycloak(String email);
}

