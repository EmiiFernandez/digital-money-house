package com.dmh.auth_service.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record TokenResponse (
        String token,
        String refreshToken,
        Long expiresIn,
        String tokenType,
        Set<String>roles
){

}

