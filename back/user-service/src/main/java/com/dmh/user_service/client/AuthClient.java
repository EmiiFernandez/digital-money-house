package com.dmh.user_service.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthClient {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}

