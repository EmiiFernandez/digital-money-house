package com.dmh.auth_service.model;

public record AuthResponse(String accessToken, String refreshToken) {
}
