package com.dmh.user_service.mapper;

import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User requestNewUser(RequestNewUser requestNewUser);

    @Mapping(target = "account_id", source = "account_id")
    ResponseNewUser responseNewUser(User user, Integer account_id);
}
