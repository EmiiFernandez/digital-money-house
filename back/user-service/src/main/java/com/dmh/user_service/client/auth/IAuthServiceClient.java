package com.dmh.user_service.client.auth;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "AUTH-SERVICE",
        url = "${auth.service.url:http://auth-service:8083/api/auth}",
        fallbackFactory = AuthServiceFallback.class
)public interface IAuthServiceClient {
    @PostMapping()
    ResponseEntity<?> registerUserCredentials(@RequestBody @Valid TokenRequest tokenRequest);
    @PostMapping("/validate")
    ResponseEntity<Boolean> validateCredentials(@RequestBody TokenRequest tokenRequest);
    @DeleteMapping("/users/{keycloakId}")
    void deleteUser(@PathVariable String keycloakId);
    @GetMapping("/users/search")
    String getKeycloakUserId(@RequestParam("email") String email);

}

