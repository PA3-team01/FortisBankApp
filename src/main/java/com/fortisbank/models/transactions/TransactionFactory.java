package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionFactory {

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
