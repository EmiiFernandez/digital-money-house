package com.dmh.user_service.service.impl;

import com.dmh.user_service.client.account.AccountClient;
import com.dmh.user_service.client.auth.TokenRequest;
import com.dmh.user_service.client.account.IAccountServiceClient;
import com.dmh.user_service.client.auth.IAuthServiceClient;
import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.dto.RequestUpdateUser;
import com.dmh.user_service.dto.ResponseGetUser;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.entity.User;
import com.dmh.user_service.enums.UserStatus;
import com.dmh.user_service.exceptions.BadRequestException;
import com.dmh.user_service.exceptions.ConflictException;
import com.dmh.user_service.exceptions.InternalServerErrorException;
import com.dmh.user_service.exceptions.NotFoundException;
import com.dmh.user_service.mapper.UserMapper;
import com.dmh.user_service.repository.UserRepository;
import com.dmh.user_service.service.IUserService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final IAccountServiceClient accountServiceClient;
    private final IAuthServiceClient authServiceClient;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public ResponseNewUser createUser(RequestNewUser requestNewUser) {
        log.info("Starting user registration process for email: {}", requestNewUser.email());
        User user = null;
        String keycloakUserId = null;
        AccountClient account = null;

        try {
            // 1. Validar restricciones únicas
            validateUniqueConstraints(requestNewUser);

            // 2. Crear usuario local en estado PENDING
            user = createLocalUser(requestNewUser);
            log.info("Local user created with ID: {}", user.getUser_id());

            // 3. Registrar en Keycloak
            try {
                keycloakUserId = registerInAuthService(requestNewUser);
                user.setKeycloakId(keycloakUserId);
                log.info("User registered in Keycloak with ID: {}", keycloakUserId);
            } catch (Exception keycloakError) {
                log.error("Error registering in Keycloak: {}", keycloakError.getMessage());
                throw new InternalServerErrorException("Failed to register user in Keycloak: " + keycloakError.getMessage());
            }

            // 4. Actualizar usuario local con ID de Keycloak y estado ACTIVE
            user.setStatus(UserStatus.ACTIVE);
            user = userRepository.save(user);

            // 5. Crear cuenta en account-service
            try {
                Thread.sleep(1000);
                account = accountServiceClient.createAccount(user.getUser_id());
                log.info("Account created successfully for user: {}", user.getUser_id());
            } catch (Exception accountError) {
                log.error("Error creating account for user {}: {}", user.getUser_id(), accountError.getMessage());
                // Revertir el estado a PENDING si falla la creación de la cuenta
                user.setStatus(UserStatus.PENDING);
                userRepository.save(user);
                throw new InternalServerErrorException("User created but account creation failed");
            }

            return userMapper.responseNewUser(user, account.getId());

        } catch (Exception e) {
            log.error("Error in user creation process: {}", e.getMessage());

            // Limpieza en caso de error
            if (keycloakUserId != null) {
                try {
                    cleanupAuthServiceUser(keycloakUserId);
                } catch (Exception cleanupError) {
                    log.error("Error cleaning up Keycloak user: {}", cleanupError.getMessage());
                }
            }

            if (user != null && user.getUser_id() != null) {
                try {
                    userRepository.delete(user);
                } catch (Exception deleteError) {
                    log.error("Error cleaning up local user: {}", deleteError.getMessage());
                }
            }

            if (e instanceof InternalServerErrorException) {
                throw e;
            }
            throw new InternalServerErrorException("Failed to complete user registration: " + e.getMessage());
        }
    }

    private User createLocalUser(RequestNewUser requestNewUser) {
        User user = userMapper.requestNewUser(requestNewUser);
        user.setPassword(passwordEncoder.encode(requestNewUser.password()));
        user.setStatus(UserStatus.PENDING);
        return userRepository.save(user);
    }

    // Registrar credenciales en Keycloak
    private String registerInAuthService(RequestNewUser requestNewUser) {
        try {
            TokenRequest tokenRequest = new TokenRequest(
                    requestNewUser.email(),
                    requestNewUser.password(),
                    requestNewUser.email()
            );

            authServiceClient.registerUserCredentials(tokenRequest);

            String keycloakId = authServiceClient.getKeycloakUserId(requestNewUser.email());

            if (keycloakId == null || keycloakId.isEmpty()) {
                throw new InternalServerErrorException("Error al obtener el id del usuario en Keycloak");
            }

            return keycloakId;
        } catch (FeignException e) {
            log.error("Error registering in auth service: {}", e.getMessage());
            throw new InternalServerErrorException("Error en auth-service: " + e.getMessage());
        }
    }


    private void cleanupAuthServiceUser(String keycloakId) {
        try {
            log.debug("Attempting to delete user from Keycloak with ID: {}", keycloakId);
            authServiceClient.deleteUser(keycloakId);
            log.debug("Successfully deleted user from Keycloak");
        } catch (FeignException.NotFound ex) {
            log.warn("User not found in Keycloak during cleanup: {}", keycloakId);
        } catch (FeignException ex) {
            log.error("Unexpected error during Keycloak user cleanup: {}", ex.getMessage());
            throw new ConflictException("Error deleting user from Keycloak");
        }
    }


    public ResponseGetUser getUserById(Integer user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new NotFoundException("User not found by ID: " + user_id));

        return userMapper.responseGetUser(user);
    }

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

    private void validateUniqueConstraints(RequestNewUser requestNewUser) {
        if (userRepository.existsByEmail(requestNewUser.email())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByDni(requestNewUser.dni())) {
            throw new BadRequestException("DNI already registered");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Integer user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        try {
            // Primero eliminar en auth service
            if (user.getKeycloakId() != null) {
                cleanupAuthServiceUser(user.getKeycloakId());
            }

            // Luego eliminar localmente
            userRepository.delete(user);

            log.info("User successfully deleted - userId: {}", user_id);
        } catch (Exception ex) {
            log.error("Error during user deletion process: {}", ex.getMessage());
            throw new InternalServerErrorException("Error durante el proceso de eliminación del usuario");
        }
    }

}
