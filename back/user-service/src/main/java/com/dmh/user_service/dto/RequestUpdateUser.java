package com.dmh.user_service.dto;

public record RequestUpdateUser(
        Integer dni,
        String email,
        String firstname,
        String lastname,
        String password,
        String phone) {
}