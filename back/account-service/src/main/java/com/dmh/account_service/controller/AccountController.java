package com.dmh.account_service.controller;

import com.dmh.account_service.entity.Account;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {


    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        if (account.getUser_id() == null) {
            throw new IllegalArgumentException("user_id must be provided");
        }
        return accountService.createAccount(account);
    }

    @GetMapping("/{id}")
    public Optional<Account> getAccountById(@PathVariable Integer id) {
        return accountRepository.findById(id);
    }

}