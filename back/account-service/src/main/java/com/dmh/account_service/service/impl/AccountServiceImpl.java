package com.dmh.account_service.service.impl;

import com.dmh.account_service.client.IUserServiceClient;
import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.entity.Account;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    IUserServiceClient userServiceClient;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ObjectMapper mapper;

    private static final SecureRandom secureRandom = new SecureRandom();

    public Account createAccount(Account account) {
        Optional<UserClient> user = userServiceClient.getUserById(account.getUser_id());

        Account newAccount = new Account();
        newAccount.setAlias(generateAlias(user.orElse(null)));
        newAccount.setAvailable_amount(0);
        newAccount.setCvu(generateCvu());
        newAccount.setUser_id(user.get().getUser_id());

        return accountRepository.save(newAccount);
    }

    /*@Override
    public Integer createAccountForUser(Integer user_id) {
        Optional<UserClient> user = userServiceClient.getUserById(user_id);

        Account account = new Account();
        account.setAlias(generateAlias(user.orElse(null)));
        account.setAvailable_amount(0);
        account.setCvu(generateCvu());
        account.setUser_id(user.get().getUser_id());

        Account newAccount = accountRepository.save(account);

        return newAccount.getId();
    }
*/
    private String generateCvu() {
        StringBuilder cvuBuilder = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            int digit = secureRandom.nextInt(10); // Genera un número aleatorio entre 0 y 9
            cvuBuilder.append(digit);
        }
        return cvuBuilder.toString();
    }

    private String generateAlias(UserClient user) {
        return user.getFirstname().toUpperCase() + "." + user.getLastname().toUpperCase() + "." + user.getUser_id();
    }
}
