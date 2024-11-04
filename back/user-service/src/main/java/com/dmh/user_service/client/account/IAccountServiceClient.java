package com.dmh.user_service.client.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service", url = "http://localhost:8082/api/accounts")
public interface IAccountServiceClient {

    @PostMapping
    AccountClient createAccount(@RequestBody Integer user_id);


    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteAccount(@PathVariable Integer user_id);
}
