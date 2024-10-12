package com.dmh.account_service.service.impl;

import com.dmh.account_service.client.IUserServiceClient;
import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.entity.Account;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
        newAccount.setAlias(generateAlias());
        newAccount.setAvailable_amount(0);
        newAccount.setCvu(generateCvu());
        newAccount.setUser_id(user.get().getUser_id());

        return accountRepository.save(newAccount);

    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Integer id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccount(Integer id) {
        accountRepository.deleteById(id);
    }

    public Account updateAccountAlias(Integer id, String alias) {
        Account account = getAccountById(id);
        if (account != null) {
            account.setAlias(alias);
            return accountRepository.save(account);
        }
        return null;
    }

    private String generateAlias() {
        String alias = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("generateAlias.txt").getInputStream()))) {
            String[] words = br.lines().toArray(String[]::new);
            if (words.length > 0) {
                Random random = new Random();
                alias = words[random.nextInt(words.length)];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alias;
    }

    private String generateCvu() {
        StringBuilder cvuBuilder = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            int digit = secureRandom.nextInt(10); // Genera un número aleatorio entre 0 y 9
            cvuBuilder.append(digit);
        }
        return cvuBuilder.toString();
    }
}
