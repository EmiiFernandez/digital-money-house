package com.dmh.auth_service.repository;

import com.dmh.auth_service.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Integer> {
    Optional<UserAuth> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
