package com.dmh.user_service.service;

import com.dmh.user_service.dto.RequestUpdateUser;
import com.dmh.user_service.dto.ResponseGetUser;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.dto.RequestNewUser;

public interface IUserService {
    ResponseNewUser createUser(RequestNewUser requestNewUser);
    ResponseGetUser getUserById(Integer user_id);
    ResponseGetUser updateUser(Integer user_id, RequestUpdateUser requestUpdateUser);
    void deleteUser(Integer user_id);
}
