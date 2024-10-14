package com.dmh.account_service.mapper;
import com.dmh.account_service.dto.ResponseAccount;
import com.dmh.account_service.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    ResponseAccount responseAccount(Account account);
}
