package com.dmh.account_service.dto;

public record AccountDTO(
        Integer id,
        String alias,
        Number available_amount,
        String cvu,
        Integer user_id) {
}

