package com.dmh.account_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "user-service", url = "http://localhost:8081/api/users")
public interface IUserServiceClient {

    @PostMapping()
    UserClient createUser(@RequestBody UserClient user);

    @GetMapping("/{user_id}")
    Optional<UserClient> getUserById(@PathVariable("user_id") Integer user_id);
}