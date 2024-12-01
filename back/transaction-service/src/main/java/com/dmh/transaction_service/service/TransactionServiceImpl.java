/*package com.dmh.transaction_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionDto createTransaction(Long accountId, CreateTransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .amount(request.amount())
                .type(request.type())
                .transactionDate(LocalDateTime.now())
                .description(request.description())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId)
                .stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
}
*/