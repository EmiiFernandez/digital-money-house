package com.dmh.transaction_service.dto;

import com.dmh.transaction_service.entity.TransactionType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionRequest {
    @NotNull(message = "Amount is required")
    BigDecimal amount;
    @NotNull(message = "Transaction type is required")
    TransactionType type;
    String description;
};