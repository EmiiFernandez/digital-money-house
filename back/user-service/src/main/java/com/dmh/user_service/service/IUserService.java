package com.dmh.user_service.service;

import com.dmh.user_service.dto.UserDTO;
import com.dmh.user_service.entity.User;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    public UserDTO createUser(UserDTO userDTO) throws DataAccessException;
    public UserDTO updateUser (Integer id, UserDTO userDTO);
    public List<UserDTO> getAllUsers() throws DataAccessException;
    public Optional<User> getUserById(Integer id);
    public void deleteUser(Integer id);

}
