package com.dmh.account_service.service.impl;

import com.dmh.account_service.client.IUserServiceClient;
import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.dto.RequestAlias;
import com.dmh.account_service.dto.ResponseAccount;
import com.dmh.account_service.entity.Account;
import com.dmh.account_service.exceptions.InternalServerErrorException;
import com.dmh.account_service.exceptions.NotFoundException;
import com.dmh.account_service.mapper.AccountMapper;
import com.dmh.account_service.repository.AccountRepository;
import com.dmh.account_service.service.AccountService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final IUserServiceClient userServiceClient;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private static final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public Account createAccount(Integer user_id) {
        log.info("Creating account for user ID: {}", user_id);

        // Verificar existencia del usuario
        try {
            UserClient user = userServiceClient.getUserById(user_id)
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + user_id));

            log.info("User verified successfully. Creating account...");

            // Crear y guardar la cuenta
            Account account = Account.builder()
                    .user_id(user_id)
                    .alias(generateAlias())
                    .cvu(generateCvu())
                    .build();

            Account savedAccount = accountRepository.save(account);
            log.info("Account created successfully with ID: {}", savedAccount.getId());

            return savedAccount;

        } catch (FeignException.NotFound e) {
            log.error("User not found in user-service: {}", user_id);
            throw new NotFoundException("User not found with ID: " + user_id);
        } catch (FeignException e) {
            log.error("Error communicating with user-service: {}", e.getMessage());
            throw new InternalServerErrorException("Error verifying user existence");
        }
    }


    public void deleteAccountByUserId(Integer user_id) {
        Account account = accountRepository.findAccountByUserId(user_id)
                .orElseThrow(() -> new NotFoundException("Account not found for user ID: " + user_id));
        accountRepository.delete(account);
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
