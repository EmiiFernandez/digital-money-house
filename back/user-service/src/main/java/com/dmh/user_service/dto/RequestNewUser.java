package com.dmh.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestNewUser(
        String firstname,
        String lastname,
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,
        Integer dni,
        String phone,
        @NotBlank(message = "Password is required")
     //   @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
     //           message = "Password must be at least 8 characters long and contain letters and numbers")
        String password) {
}