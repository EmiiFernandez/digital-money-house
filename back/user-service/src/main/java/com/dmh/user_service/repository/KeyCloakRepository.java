package com.dmh.user_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dmh.user_service.entity.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class KeyCloakRepository implements IUserRepository {
    @Autowired
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public Optional<User> findById(String id) {
        try {
            UserRepresentation userRepresentation = keycloak
                    .realm(realm)
                    .users()
                    .get(id)
                    .toRepresentation();
            // Agregar log para verificar si se encontró el usuario
            System.out.println("User found: " + userRepresentation);

            return Optional.of(fromRepresentation(userRepresentation));
        } catch (Exception e) {
            System.err.println("Error finding user: " + e.getMessage());

            return Optional.empty();
        }
    }

    @Override
    public List<User> findByUsername(String username) {
        List<UserRepresentation> userRepresentation = keycloak
                .realm(realm)
                .users()
                .search(username);

        return userRepresentation.stream().map(user -> fromRepresentation(user)).collect(Collectors.toList());
    }

    private User fromRepresentation(UserRepresentation userRepresentation) {
        Integer dni = Integer.parseInt(userRepresentation.getAttributes().getOrDefault("dni", List.of("0")).get(0));
        String phone = userRepresentation.getAttributes().getOrDefault("phone", List.of("")).get(0);

        return new User(
                userRepresentation.getId(),
                userRepresentation.getFirstName(),
                userRepresentation.getLastName(),
                userRepresentation.getEmail(),
                dni,
                phone
        );
    }
}
