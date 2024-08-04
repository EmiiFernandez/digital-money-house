package com.dmh.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service", url = "http://localhost:8082/")
public interface IAccountClient {
    @PostMapping("/api/accounts")
    public AccountClient createAccount(@RequestBody AccountClient account);
}
