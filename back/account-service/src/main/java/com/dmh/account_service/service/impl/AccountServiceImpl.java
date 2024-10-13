package com.dmh.account_service.service.impl;

import com.dmh.account_service.client.IUserServiceClient;
import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.entity.Account;
import com.dmh.account_service.exceptions.ResourceNotFoundException;
import com.dmh.account_service.exceptions.UserNotFoundException;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.AccountService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
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

    private static final SecureRandom secureRandom = new SecureRandom();


    public Account createAccount(Integer user_id) {
        try {
            Optional<UserClient> user = userServiceClient.getUserById(user_id);
        } catch (FeignException e) {
            throw new UserNotFoundException("Usuario no encontrado");
        }

        Account newAccount = new Account();
        newAccount.setUser_id(user_id);
        newAccount.setAlias(generateAlias());
        newAccount.setCvu(generateCvu());

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
        {
            String alias = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("generateAlias.txt").getInputStream()))) {
                String[] words = br.lines().toArray(String[]::new);
                if (words.length > 2) {  
                    Random random = new Random();
                    String word1 = words[random.nextInt(words.length)];
                    String word2 = words[random.nextInt(words.length)];
                    String word3 = words[random.nextInt(words.length)];
                    alias = word1 + "." + word2 + "." + word3;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return alias;
        }
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
