package com.dmh.account_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface IUserServiceClient {

    @PostMapping()
    UserClient createUser(@RequestBody UserClient user);

    @GetMapping("/{id}")
    Optional<UserClient> getUserById(@PathVariable("id") Integer id);
}