package com.dmh.account_service.dto;

import java.math.BigDecimal;

public record ResponseAccount(
        Integer id,
        String alias,
        BigDecimal available_amount,
        String cvu,
        Integer user_id
) {}