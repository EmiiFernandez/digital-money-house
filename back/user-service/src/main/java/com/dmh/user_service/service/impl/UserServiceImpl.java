package com.dmh.user_service.service.impl;

import com.dmh.user_service.client.AccountClient;
import com.dmh.user_service.client.IAccountServiceClient;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.entity.User;
import com.dmh.user_service.exceptions.ResourceNotFoundException;
import com.dmh.user_service.mapper.UserMapper;
import com.dmh.user_service.repository.IUserRepository;
import com.dmh.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IAccountServiceClient accountClient;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    // "Create a new user with a new account"
    public ResponseNewUser createUser(RequestNewUser requestNewUser) {
        User user = userMapper.requestNewUser(requestNewUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        AccountClient createdAccount = accountClient.createAccount(user.getUser_id());

        ResponseNewUser response = userMapper.responseNewUser(user, createdAccount.getId());

        return response;
    }

     public Optional<User> getUserById(Integer userId) {
        return Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
    }
 /* }

  @Override
    public RequestNewUser updateUser(Integer id, RequestNewUser userDTO) {
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
/*
    @Override
    public List<RequestNewUser> getAllUsers() throws DataAccessException {
        try {
            List<User> users = userRepository.findAll();
            List<RequestNewUser> usersDTOS = new ArrayList<>();

            for (User user : users) {
                RequestNewUser requestNewUser = mapper.convertValue(user, RequestNewUser.class);
                usersDTOS.add(requestNewUser);
            }

            return usersDTOS;

        } catch (DataAccessException e) {
            throw new RuntimeException("Error en la base de datos" + e.getMessage());
        }

    }


    @Override
    public void deleteUser(Integer id) {
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el id: " + id));
        userRepository.deleteById(id);
    }*/
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