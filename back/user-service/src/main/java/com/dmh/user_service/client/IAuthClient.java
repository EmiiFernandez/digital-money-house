package com.dmh.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "http://localhost:8083/api/auth")
public interface IAuthClient {

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody AuthClient authClient);
}

