package com.dmh.user_service.repository;

import com.dmh.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findById(Integer id);

    List<User> findByUsername(String email);

    Optional<User> findByEmailOrDni(String email, Integer dni);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByDni(Integer dni);

    Optional<User> findByKeycloakId(String keycloakId);

}
