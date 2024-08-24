package com.dmh.user_service.service.impl;

import com.dmh.user_service.client.AccountClient;
import com.dmh.user_service.client.IAccountClient;
import com.dmh.user_service.dto.NewUserResponse;
import com.dmh.user_service.dto.UserDTO;
import com.dmh.user_service.entity.User;
import com.dmh.user_service.repository.IUserRepository;
import com.dmh.user_service.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAccountClient accountClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public NewUserResponse createUser(UserDTO userDTO) {
        User user = mapper.convertValue(userDTO, User.class);

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);

        // Set the user ID for the account and create it
        AccountClient account = new AccountClient();
        account.setUser_id(user.getUser_id());
        AccountClient createdAccount = accountClient.createAccount(account);

        // Prepare the response
        NewUserResponse response = new NewUserResponse();
        response.setUser_id(user.getUser_id());
        response.setEmail(user.getEmail());
        response.setAccount_id(createdAccount.getId());

        return response;
    }
 /* }

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
    }*/

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
            throw new RuntimeException("Error en la base de datos" + e.getMessage());
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
/*
   private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    public void createUser(NewUserRecord userRecord) {

        User user = User.builder()
                .id(securityService.getUserId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .countryIso2(userRecord.countryIso2())
                .dob(userRecord.dob())
                .gender(userRecord.gender())
                .name(userRecord.name())
                .language(userRecord.language()).build();

        User save = userRepository.save(user);
    }

 */