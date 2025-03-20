package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionFactory {

    public static TransactionInterface createTransaction(
            TransactionType type,
            String description,
            Date transactionDate,
            BigDecimal amount,
            Account sourceAccount,
            Account destinationAccount) {

        switch (type) {
            case DEPOSIT:
                return new DepositTransaction(description, transactionDate, amount, destinationAccount);

            case WITHDRAWAL:
                return new WithdrawalTransaction(description, transactionDate, amount, sourceAccount);

            case TRANSFER:
                return new TransferTransaction(description, transactionDate, amount, sourceAccount, destinationAccount);

            case FEE:
                return new FeeTransaction(description, transactionDate, amount, sourceAccount);

            default:
                throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }
}
