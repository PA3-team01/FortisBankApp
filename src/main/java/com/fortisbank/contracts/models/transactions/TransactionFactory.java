package com.fortisbank.contracts.models.transactions;

import com.fortisbank.contracts.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Factory class for creating different types of transactions.
 */
public class TransactionFactory {

    /**
     * Creates a transaction based on the specified type and parameters.
     *
     * @param type the type of the transaction
     * @param description the description of the transaction
     * @param transactionDate the date of the transaction
     * @param amount the amount involved in the transaction
     * @param sourceAccount the source account of the transaction
     * @param destinationAccount the destination account of the transaction
     * @return the created transaction
     * @throws IllegalArgumentException if the transaction type is invalid
     */
    public static Transaction createTransaction(
            TransactionType type,
            String description,
            Date transactionDate,
            BigDecimal amount,
            Account sourceAccount,
            Account destinationAccount) {

        Date date = (transactionDate != null) ? transactionDate : new Date();
        String desc = (description != null && !description.isBlank()) ? description : type.name();

        return switch (type) {
            case DEPOSIT -> new DepositTransaction(desc, date, amount, destinationAccount);
            case WITHDRAWAL -> new WithdrawalTransaction(desc, date, amount, sourceAccount);
            case TRANSFER -> new TransferTransaction(desc, date, amount, sourceAccount, destinationAccount);
            case FEE -> new FeeTransaction(desc, date, amount, sourceAccount);
            default -> throw new IllegalArgumentException("Invalid transaction type: " + type);
        };
    }
}