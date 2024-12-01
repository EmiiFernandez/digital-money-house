/*package com.dmh.transaction_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDto>> getAccountTransactions(
            @PathVariable Long accountId
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    @PostMapping("/{accountId}")
    public ResponseEntity<TransactionDto> createTransaction(
            @PathVariable Long accountId,
            @RequestBody @Valid CreateTransactionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(accountId, request));
    }
}*/