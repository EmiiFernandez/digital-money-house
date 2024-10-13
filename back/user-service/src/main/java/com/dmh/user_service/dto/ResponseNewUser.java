package com.dmh.user_service.dto;

public record ResponseNewUser(
        Integer account_id,
        String email,
        Integer user_id
) {
}