package com.dmh.user_service.mapper;

import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User requestNewUser(RequestNewUser requestNewUser);

    ResponseNewUser responseNewUser(User user, Integer accountId);
}
