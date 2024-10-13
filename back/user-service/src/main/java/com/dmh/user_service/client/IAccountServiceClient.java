package com.dmh.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service", url = "http://localhost:8082/api/accounts")
public interface IAccountServiceClient {

        @PostMapping
        AccountClient createAccount(@RequestBody Integer user_id);
    }

