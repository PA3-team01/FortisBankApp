package com.fortisbank.data.dto;

import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.models.accounts.AccountFactory;
import com.fortisbank.contracts.models.accounts.AccountType;
import com.fortisbank.contracts.models.transactions.*;
import com.fortisbank.contracts.utils.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public record TransactionDTO(
        String transactionId,
        String transactionType,
        LocalDate transactionDate,
        BigDecimal amount,
        String description,
        String sourceAccountId,
        String destinationAccountId
) {
    public TransactionDTO {
        if (transactionId == null || transactionId.isBlank())
            throw new IllegalArgumentException("Transaction ID cannot be null or blank.");
        if (transactionType == null || transactionType.isBlank())
            throw new IllegalArgumentException("Transaction type cannot be null or blank.");
        if (transactionDate == null)
            throw new IllegalArgumentException("Transaction date cannot be null.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount must be non-negative.");
        if (description == null)
            throw new IllegalArgumentException("Description cannot be null.");
    }

    public static TransactionDTO fromEntity(Transaction tx) {
        return new TransactionDTO(
                tx.getTransactionId(),
                tx.getTransactionType().name(),
                ValidationUtils.toLocalDate(tx.getTransactionDate()),
                tx.getAmount(),
                tx.getDescription(),
                tx.getSourceAccount() != null ? tx.getSourceAccount().getAccountNumber() : null,
                tx.getDestinationAccount() != null ? tx.getDestinationAccount().getAccountNumber() : null
        );
    }

    public Transaction toEntity() {
        TransactionType type = TransactionType.valueOf(transactionType);
        Date date = ValidationUtils.toDate(transactionDate);

        // We assume CHECKING as a placeholder since type is unknown and customer is null.
        Account source = sourceAccountId != null
                ? AccountFactory.createAccount(AccountType.CHECKING, sourceAccountId, null, null, BigDecimal.ZERO)
                : null;

        Account dest = destinationAccountId != null
                ? AccountFactory.createAccount(AccountType.CHECKING, destinationAccountId, null, null, BigDecimal.ZERO)
                : null;

        Transaction tx = TransactionFactory.createTransaction(type, description, date, amount, source, dest);
        tx.setTransactionId(transactionId);
        return tx;
    }

}
