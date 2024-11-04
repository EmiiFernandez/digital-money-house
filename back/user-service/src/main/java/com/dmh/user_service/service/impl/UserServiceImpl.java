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
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final IAccountServiceClient accountServiceClient;
    private final IAuthServiceClient authServiceClient;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseNewUser createUser(RequestNewUser requestNewUser) {
        log.info("Starting user registration process for email: {}", requestNewUser.email());

        // 1. Validaciones iniciales
        validateUniqueConstraints(requestNewUser);

        // 2. Crear usuario local
        User user = createLocalUser(requestNewUser);
        log.info("Local user created with ID: {}", user.getUser_id());

        try {
            // 3. Registrar en auth-service
            String keycloakUserId = registerInAuthService(requestNewUser);
            user.setKeycloakId(keycloakUserId);
            user.setStatus(UserStatus.ACTIVE);
            user = userRepository.saveAndFlush(user); // Forzar la persistencia
            log.info("User registered in auth service with ID: {}", keycloakUserId);

            // 4. Crear cuenta con reintentos y manejo de transacciones
            AccountClient account = createUserAccount(user);

            return userMapper.responseNewUser(user, account.getId());

        } catch (Exception e) {
            throw new InternalServerErrorException("Error al registrar el usuario: " + e.getMessage());
        }
    }



    private String registerInAuthService(RequestNewUser requestNewUser) {
        try {
            TokenRequest tokenRequest = new TokenRequest(
                    requestNewUser.email(),
                    requestNewUser.password(),
                    requestNewUser.email()
            );

            // Primero registramos el usuario
            authServiceClient.registerUserCredentials(tokenRequest);

            // Luego obtenemos su ID
            return authServiceClient.getKeycloakUserId(requestNewUser.email());

        } catch (FeignException e) {
            log.error("Error registering in auth service: {}", e.getMessage());
            if (e.status() == 409) {
                throw new ConflictException("User already exists in auth service");
            }
            throw new InternalServerErrorException("Failed to register user in auth service");
        }
    }

    private User createLocalUser(RequestNewUser requestNewUser) {
        if (userRepository.existsByEmail(requestNewUser.email())) {
            throw new ConflictException("Email already registered locally");
        }

        User user = userMapper.requestNewUser(requestNewUser);
        user.setPassword(passwordEncoder.encode(requestNewUser.password()));
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }


    private AccountClient createUserAccount(User user) throws InterruptedException {
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(1000) // 1 segundo entre intentos
                .retryOn(FeignException.class)
                .build();

        return retryTemplate.execute(context -> {
            try {
                // Asegurar que la transacción del usuario está confirmada
                Thread.sleep(500); // Pequeña espera para asegurar consistencia
                return accountServiceClient.createAccount(user.getUser_id());
            } catch (FeignException e) {
                log.error("Error creating account (attempt {}): {}", context.getRetryCount(), e.getMessage());
                throw e;
            }
        });
    }

    private void handleRegistrationError(Exception e, String keycloakUserId) {
        log.error("Error during user registration process", e);

        // Si tenemos ID de Keycloak, intentar limpieza
        if (keycloakUserId != null) {
            try {
                authServiceClient.deleteUser(keycloakUserId);
            } catch (Exception authEx) {
                log.error("Failed to cleanup auth service user: {}", authEx.getMessage());
            }
        }

        // Determinar tipo de error y lanzar excepción apropiada
        if (e instanceof FeignException) {
            FeignException fe = (FeignException) e;
            if (fe.status() == 404) {
                throw new NotFoundException("Resource not found: " + fe.getMessage());
            } else if (fe.status() == 409) {
                throw new ConflictException("Resource already exists: " + fe.getMessage());
            }
        }
    }

    private void rollbackRegistration(String keycloakUserId, User user) {
        if (user != null) {
            try {
                // Marcar usuario como FAILED en vez de eliminarlo
                user.setStatus(UserStatus.FAILED);
                userRepository.save(user);
                log.info("User marked as FAILED: {}", user.getUser_id());
            } catch (Exception e) {
                log.error("Error marking user as FAILED: {}", e.getMessage());
            }
        }

        if (user != null) {
            try {
                authServiceClient.deleteUser(keycloakUserId);
                log.info("Keycloak user deleted: {}", keycloakUserId);
            } catch (Exception e) {
                log.error("Error deleting Keycloak user: {}", e.getMessage());
            }
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

    private void cleanupAuthServiceUser(String keycloakId) {
        try {
            log.debug("Attempting to delete user from auth service with ID: {}", keycloakId);
            authServiceClient.deleteUser(keycloakId);
            log.debug("Successfully deleted user from auth service");
        } catch (FeignException.NotFound ex) {
            log.warn("User not found in auth service during cleanup: {}", keycloakId);
        } catch (FeignException ex) {
            log.error("Unexpected error during auth service user cleanup: {}", ex.getMessage());
            throw new ConflictException("Error al eliminar usuario del servicio de autenticación");
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
