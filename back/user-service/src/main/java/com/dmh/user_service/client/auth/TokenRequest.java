package com.dmh.user_service.client.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenRequest {
    private String email;
    private String password;
    private String username;
    }

