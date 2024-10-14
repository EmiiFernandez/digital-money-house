package com.dmh.user_service.dto;

public record ResponseGetUser(
        Integer user_id,
         Integer dni,
         String email,
         String firstname,
         String lastname,
         String phone
) {
}
