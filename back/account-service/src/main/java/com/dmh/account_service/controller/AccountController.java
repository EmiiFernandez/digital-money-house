package com.dmh.account_service.controller;

import com.dmh.account_service.client.IUserServiceClient;
import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.dto.RequestAlias;
import com.dmh.account_service.dto.ResponseAccount;
import com.dmh.account_service.entity.Account;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IUserServiceClient userClient;


    @PostMapping()
    @PreAuthorize("hasRole('INTERNAL_SERVICE')")
    public Account createAccount(@RequestBody Integer user_id) {
        Optional<UserClient> user = userClient.getUserById(user_id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("user_id must be provided");
        }
        return accountService.createAccount(user_id);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<ResponseAccount> getAccountsByUserId(@PathVariable Integer user_id) {
        ResponseAccount account = accountService.getAccountByUserId(user_id);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{account_id}")
    public ResponseEntity<ResponseAccount> updateUser(
            @PathVariable Integer account_id,
            @RequestBody RequestAlias requestAlias) {
        ResponseAccount updatedAlias = accountService.updateAliasAccount(account_id, requestAlias);
        return ResponseEntity.ok(updatedAlias);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer user_id) {
        accountService.deleteAccountByUserId(user_id);
        return ResponseEntity.noContent().build();
    }

    /*Crear getAccount por jwt*/
}