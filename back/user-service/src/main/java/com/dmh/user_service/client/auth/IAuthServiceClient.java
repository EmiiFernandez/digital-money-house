package com.dmh.user_service.client.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", url = "http://localhost:8083/api/auth")
public interface IAuthServiceClient {
    @PostMapping()
    ResponseEntity<?> registerUserCredentials(@RequestBody TokenRequest tokenRequest);
    @PostMapping("/validate")
    ResponseEntity<Boolean> validateCredentials(@RequestBody TokenRequest tokenRequest);
    @DeleteMapping("/users/{keycloakId}")
    void deleteUser(@PathVariable String keycloakId);
    @GetMapping("/users/search")
    String getKeycloakUserId(@RequestParam("email") String email);

}

