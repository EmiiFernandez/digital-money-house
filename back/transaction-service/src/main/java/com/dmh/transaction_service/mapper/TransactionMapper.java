package com.dmh.transaction_service.mapper;

import com.dmh.transaction_service.dto.TransactionRequest;
import com.dmh.transaction_service.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionRequest toDto(Transaction transaction);
    Transaction toEntity(TransactionRequest transactionDto);
}
