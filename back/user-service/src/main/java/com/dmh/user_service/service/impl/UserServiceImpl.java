package com.dmh.user_service.service.impl;

import com.dmh.user_service.dto.UserDTO;
import com.dmh.user_service.entity.User;
import com.dmh.user_service.repository.IUserRepository;
import com.dmh.user_service.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    ObjectMapper mapper;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        try {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("El email ya se encuentra en nuestra base de datos");
            }

            User user = new User();
            BeanUtils.copyProperties(userDTO, user);

            userRepository.save(user);

            return userDTO;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear el usuario en la base de datos: " + e.getMessage(), e);
        }
    }


    @Override
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el id: " + id));

        user.setDni(userDTO.getDni());
        user.setEmail(userDTO.getEmail());
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setPassword(userDTO.getPassword());
        user.setPhone(userDTO.getPhone());

        userRepository.save(user);

        return userDTO;
    }

    @Override
    public List<UserDTO> getAllUsers() throws DataAccessException {
        try {
            List<User> users = userRepository.findAll();
            List<UserDTO> usersDTOS = new ArrayList<>();

            for (User user : users) {
                UserDTO userDTO = mapper.convertValue(user, UserDTO.class);
                usersDTOS.add(userDTO);
            }

            return usersDTOS;

        } catch (DataAccessException e) {
            throw new RuntimeException("Error al crear el usuario en la base de datos" + e.getMessage());
        }

    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el id: " + id));
        userRepository.deleteById(id);
    }
}
