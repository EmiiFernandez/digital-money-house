package com.dmh.account_service.service;

import com.dmh.account_service.dto.RequestAlias;
import com.dmh.account_service.dto.ResponseAccount;
import com.dmh.account_service.entity.Account;

import java.util.Optional;

public interface AccountService {
    Account createAccount(Integer user_id);
    ResponseAccount updateAliasAccount(Integer account_id, RequestAlias requestAlias);

}
