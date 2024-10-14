package com.dmh.account_service.mapper;
import com.dmh.account_service.dto.RequestAlias;
import com.dmh.account_service.dto.ResponseAccount;
import com.dmh.account_service.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    ResponseAccount responseAccount(Account account);

    void  updateAlias(RequestAlias requestAlias, @MappingTarget Account account);

}
