package com.dmh.auth_service.controller;

import com.dmh.auth_service.dto.TokenRequest;
import com.dmh.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody TokenRequest tokenRequest) {
        String token = authService.login(tokenRequest.email(), tokenRequest.password());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("refreshToken") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestHeader("refreshToken") String refreshToken) {
        String newToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newToken);
    }
}
