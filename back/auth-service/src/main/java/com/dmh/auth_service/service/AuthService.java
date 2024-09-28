package com.dmh.auth_service.service;

import com.dmh.auth_service.model.AuthResponse;
import com.dmh.auth_service.model.NewUserRecord;

public interface AuthService {
    public AuthResponse login(NewUserRecord loginRequest);
    public void registerUser(NewUserRecord newUser);
    public void logout(String refreshToken);
}
