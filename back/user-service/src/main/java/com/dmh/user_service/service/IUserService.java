/*package com.dmh.user_service.service;

import com.dmh.user_service.dto.RequestUpdateUser;
import com.dmh.user_service.dto.ResponseGetUser;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.entity.User;
import com.dmh.user_service.repository.IUserRepository;

import java.util.List;
import java.util.Optional;


public interface IUserService {
    //  ResponseNewUser createUser(RequestNewUser requestNewUser);
  //  ResponseGetUser getUserById(Integer user_id);
  //  ResponseGetUser updateUser(Integer user_id, RequestUpdateUser requestUpdateUser);
    User findById(String id);
    List<User> findByName(String name);
}
*/