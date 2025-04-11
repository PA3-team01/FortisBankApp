package com.fortisbank.data.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO(
        String transactionId,
        String transactionType,
        LocalDate transactionDate,
        BigDecimal amount,
        String description,
        String sourceAccountId,
        String destinationAccountId
) {}
