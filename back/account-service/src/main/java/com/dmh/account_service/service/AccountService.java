package com.dmh.account_service.service;

import com.dmh.account_service.client.UserClient;
import com.dmh.account_service.entity.Account;

import java.util.List;

public interface AccountService {
    Account createAccount(Integer userClientId);

    List<Account> getAllAccounts();

    Account getAccountById(Integer id);

    Account saveAccount(Account account);

    void deleteAccount(Integer id);

    Account updateAccountAlias(Integer id, String alias);
}
