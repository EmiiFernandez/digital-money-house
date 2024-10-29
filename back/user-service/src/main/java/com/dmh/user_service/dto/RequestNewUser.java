package com.dmh.user_service.dto;

public record RequestNewUser(
        int dni,
        String email,
        String firstname,
        String lastname,
        String phone) {
}