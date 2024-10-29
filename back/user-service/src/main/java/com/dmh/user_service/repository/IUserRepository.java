package com.dmh.user_service.repository;

import com.dmh.user_service.entity.User;

import java.util.List;
import java.util.Optional;

//@Repository
public interface IUserRepository {
    Optional<User> findById(String id);

    List<User> findByUsername(String email);
}
