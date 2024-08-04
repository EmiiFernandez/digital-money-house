package com.dmh.user_service.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    @Setter(AccessLevel.NONE)
    private Integer user_id;
    private int dni;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String phone;
}
