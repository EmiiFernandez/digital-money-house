/*package com.dmh.user_service.mapper;

import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.dto.RequestUpdateUser;
import com.dmh.user_service.dto.ResponseGetUser;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User requestNewUser(RequestNewUser requestNewUser);

    @Mapping(target = "account_id", source = "account_id")
    ResponseNewUser responseNewUser(User user, Integer account_id);

    ResponseGetUser responseGetUser(User user);

    void updateUser(RequestUpdateUser requestUpdateUser, @MappingTarget User user);

}
*/