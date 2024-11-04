package com.dmh.user_service.client.auth;

import com.dmh.user_service.exceptions.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceFallback implements IAuthServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceFallback.class);

    @Override
    public ResponseEntity<Void> registerUserCredentials(TokenRequest tokenRequest) {
        logger.error("Fallback: Failed to register user credentials in auth-service");
        throw new InternalServerErrorException("Auth service is currently unavailable");
    }

    @Override
    public ResponseEntity<Boolean> validateCredentials(TokenRequest tokenRequest) {
        logger.error("Fallback: Failed to validate credentials in auth-service");
        return ResponseEntity.ok(false);
    }

    @Override
    public void deleteUser(String keycloakId) {
        logger.error("Fallback: Failed to delete user credentials in auth-service");
        throw new InternalServerErrorException("Auth service is currently unavailable");


    }

    @Override
    public String getKeycloakUserId(String email) {
        logger.error("Fallback: Failed to search user credentials in auth-service");
        throw new InternalServerErrorException("Auth service is currently unavailable");
    }
}
