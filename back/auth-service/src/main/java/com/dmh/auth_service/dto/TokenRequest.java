package com.dmh.auth_service.dto;

public record TokenRequest(
        String email,
        String password
) {
}
