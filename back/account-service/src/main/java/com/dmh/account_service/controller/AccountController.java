package com.dmh.account_service.controller;

import com.dmh.account_service.client.IUserServiceClient;
import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.entity.Account;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/accounts")
public class AccountController {

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IUserServiceClient userClient;


    @PostMapping()
    public Account createAccount(@RequestBody Integer user_id) {
        Optional<UserClient> user = userClient.getUserById(user_id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("user_id must be provided");
        }
        return accountService.createAccount(user_id);
    }

    @GetMapping("/{id}")
    public Optional<Account> getAccountById(@PathVariable Integer id) {
        return accountRepository.findById(id);
    }

}