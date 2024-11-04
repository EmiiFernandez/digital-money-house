package com.dmh.auth_service.service;

import com.dmh.auth_service.dto.TokenRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<?> registerUserCredentials(TokenRequest tokenRequest);
    ResponseEntity<?> authenticateUser(TokenRequest tokenRequest);
    ResponseEntity<?> logoutUser(String token);
    ResponseEntity<?> validateToken(String token);
    public void deleteUser(Integer user_id);
    String getUserIdFromKeycloak(String email);
}

