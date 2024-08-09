package com.dmh.account_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "user-service", url = "http://localhost:8081/")
public interface IUserServiceClient {

    @GetMapping("/api/users/{id}")
    public Optional<UserClient> getUserById(@PathVariable("id") Integer id);
}

