package com.dmh.user_service.service.impl;

import com.dmh.user_service.client.AccountClient;
import com.dmh.user_service.client.IAccountServiceClient;
import com.dmh.user_service.dto.RequestUpdateUser;
import com.dmh.user_service.dto.ResponseGetUser;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.entity.User;
import com.dmh.user_service.exceptions.ConflictException;
import com.dmh.user_service.exceptions.NotFoundException;
import com.dmh.user_service.mapper.UserMapper;
import com.dmh.user_service.repository.IUserRepository;
import com.dmh.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IAccountServiceClient accountClient;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    // "Create a new user with a new account"
    public ResponseNewUser createUser(RequestNewUser requestNewUser) {
        if (userRepository.findByEmail(requestNewUser.email()).isPresent()) {
            throw new ConflictException("Email already registered.");
        }

        if (userRepository.findByDni(requestNewUser.dni()).isPresent()) {
            throw new ConflictException("DNI already registered.");
        }

        User user = userMapper.requestNewUser(requestNewUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        AccountClient createdAccount = accountClient.createAccount(user.getUser_id());

        ResponseNewUser response = userMapper.responseNewUser(user, createdAccount.getId());

        return response;
    }

    //"Get email, firstname, lastname, phone, and dni from a specific user."
    public ResponseGetUser getUserById(Integer user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new NotFoundException("User not found by ID: " + user_id));

        return userMapper.responseGetUser(user);
    }

    //Update email, password, firstname, lastname, phone, or dni from a specific user.
    public ResponseGetUser updateUser(Integer user_id, RequestUpdateUser requestUpdateUser) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + user_id));

        if (requestUpdateUser.password() != null) {
            user.setPassword(passwordEncoder.encode(requestUpdateUser.password()));
        }

        userMapper.updateUser(requestUpdateUser, user);

        userRepository.save(user);

        return userMapper.responseGetUser(user);
    }
}