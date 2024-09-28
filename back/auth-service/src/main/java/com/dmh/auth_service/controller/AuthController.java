package com.dmh.auth_service.controller;

import com.dmh.auth_service.model.AuthResponse;
import com.dmh.auth_service.model.NewUserRecord;
import com.dmh.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    // Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody NewUserRecord newUser) {
        try {
            authService.registerUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register user: " + e.getMessage());
        }
    }

    // Inicio de sesión de usuario
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody NewUserRecord loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Cierre de sesión (opcional, si necesitas gestionarlo aquí)
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestParam String refreshToken) {
        try {
            authService.logout(refreshToken);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to logout: " + e.getMessage());
        }
    }
}
