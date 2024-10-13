package com.dmh.user_service.service;

import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.entity.User;

import java.util.Optional;


public interface IUserService {
    ResponseNewUser createUser(RequestNewUser requestNewUser);
    Optional<User> getUserById(Integer userId);

    //  public RequestNewUser updateUser (Integer id, RequestNewUser userDTO);
  //  public List<RequestNewUser> getAllUsers() throws DataAccessException;
  //  public void deleteUser(Integer id);

}
