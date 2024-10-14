package com.dmh.account_service.controller;

import com.dmh.account_service.client.IUserServiceClient;
import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.dto.ResponseAccount;
import com.dmh.account_service.entity.Account;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    //Cambiar a traer por usuario autenticado
    @GetMapping("/{user_id}")
    public ResponseEntity<ResponseAccount> getAccountsByUserId(@PathVariable Integer user_id) {
        ResponseAccount account = accountService.getAccountByUserId(user_id);
        return ResponseEntity.ok(account);
    }
/*
    @GetMapping("/{id}")
    public Optional<Account> getAccountById(@PathVariable Integer id) {
        return accountRepository.findById(id);
    }
*/
}