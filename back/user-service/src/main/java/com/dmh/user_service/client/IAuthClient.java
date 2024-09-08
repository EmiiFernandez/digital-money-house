package com.dmh.user_service.client;

import com.dmh.user_service.model.NewUserRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "http://localhost:8083/")
public interface IAuthClient {
    @PostMapping("api/auth/users")
    public ResponseEntity<?> createUser(@RequestBody NewUserRecord newUserRecord);
}

