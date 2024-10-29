package com.dmh.user_service.service.impl;

import com.dmh.user_service.entity.User;
import com.dmh.user_service.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl {

    private IUserRepository userRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Metodo para buscar un usuario por su ID
    public User findById(String id) {
        // Utiliza el metodo del repositorio para obtener el usuario por su ID
        return userRepository.findById(id).orElse(null);
    }

    // Metodo para buscar una lista de usuarios por su nombre
    public List<User> findByName(String name) {
        // Utiliza el metodo del repositorio para obtener la lista de usuarios por nombre
        return userRepository.findByUsername(name);
    }
}



   /* // Metodo para buscar un usuario por su ID
    public User findById(String id) {
        // Utiliza el metodo del repositorio para obtener el usuario por su ID
        return userRepository.findById(Integer.valueOf(id)).orElse(null);
    }

    // Metodo para buscar una lista de usuarios por su nombre
    public List<User> findByName(String name) {
        // Utiliza el metodo del repositorio para obtener la lista de usuarios por nombre
        return userRepository.findByUsername(name);
    }
}*/
  /*  // "Create a new user with a new account"
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
    }*/
