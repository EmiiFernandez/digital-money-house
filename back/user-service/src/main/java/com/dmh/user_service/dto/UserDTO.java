package com.dmh.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private int dni;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String phone;
}
