package com.dmh.auth_service.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private Keycloak keycloak;

    public String login(String username, String password) {
        // Solicitar el token de acceso al servidor de Keycloak
        try {
            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            return tokenResponse.getToken();
        } catch (Exception e) {
            throw new RuntimeException("Invalid login credentials");
        }
    }

    public void logout(String refreshToken) {
        // Lógica para cerrar sesión usando el refresh token
        try {
            keycloak.tokenManager().invalidate(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Logout failed");
        }
    }

    public String refreshAccessToken(String refreshToken) {
        // Lógica para refrescar el token de acceso
        try {
            AccessTokenResponse newTokenResponse = keycloak.tokenManager().refreshToken();
            return newTokenResponse.getToken();
        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed");
        }
    }
}
