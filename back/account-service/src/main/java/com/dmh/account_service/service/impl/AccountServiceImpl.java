package com.dmh.account_service.service.impl;

import com.dmh.account_service.client.IUserServiceClient;
import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.dto.RequestAlias;
import com.dmh.account_service.dto.ResponseAccount;
import com.dmh.account_service.entity.Account;
import com.dmh.account_service.exceptions.NotFoundException;
import com.dmh.account_service.mapper.AccountMapper;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.AccountService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final IUserServiceClient userServiceClient;

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    private static final SecureRandom secureRandom = new SecureRandom();


    //Create account by register user
    public Account createAccount(Integer user_id) {
        try {
            Optional<UserClient> user = userServiceClient.getUserById(user_id);
        } catch (FeignException e) {
            throw new NotFoundException("Usuario no encontrado");
        }

        Account newAccount = new Account();
        newAccount.setUser_id(user_id);
        newAccount.setAlias(generateAlias());
        newAccount.setCvu(generateCvu());

        return accountRepository.save(newAccount);
    }

    //"Find account by user_id in the token." //FALTA AGREGAR EL TOKEN
    public ResponseAccount getAccountByUserId(Integer user_id) {
        Account account = accountRepository.findAccountByUserId(user_id)
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada para el usuario: " + user_id));


        return accountMapper.responseAccount(account);
    }

    //Update only the alias of the account. //FALTA AGREGAR EL TOKEN
    public ResponseAccount updateAliasAccount(Integer account_id, RequestAlias requestAlias) {
        Account account = accountRepository.findById(account_id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + account_id));

        accountMapper.updateAlias(requestAlias, account);

        accountRepository.save(account);

        return accountMapper.responseAccount(account);
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
