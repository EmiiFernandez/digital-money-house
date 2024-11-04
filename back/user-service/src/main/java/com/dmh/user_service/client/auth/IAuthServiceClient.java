package com.dmh.user_service.client.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", url = "http://localhost:8083")
public interface IAuthServiceClient {
    @PostMapping("/api/auth")
    ResponseEntity<?> registerUserCredentials(@RequestBody TokenRequest tokenRequest);
    @PostMapping("/api/auth/validate")
    ResponseEntity<Boolean> validateCredentials(@RequestBody TokenRequest tokenRequest);
    @DeleteMapping("/users/{user_id}")
    void deleteUser(@PathVariable String keycloakId);
    @GetMapping("/api/auth/users/search")
    String getKeycloakUserId(@RequestParam("email") String email);

}

