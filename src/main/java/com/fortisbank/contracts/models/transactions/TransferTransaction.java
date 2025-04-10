package com.fortisbank.contracts.models.transactions;

import com.fortisbank.contracts.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class representing a transfer transaction.
 */
public class TransferTransaction extends Transaction {

    /**
     * Constructor initializing the transfer transaction with specified values.
     *
     * @param description the description of the transaction
     * @param transactionDate the date of the transaction
     * @param amount the amount of the transaction
     * @param sourceAccount the account from which the amount is transferred
     * @param destinationAccount the account to which the amount is transferred
     */
    public TransferTransaction(String description, Date transactionDate, BigDecimal amount, Account sourceAccount, Account destinationAccount) {
        super(null, description, transactionDate, TransactionType.TRANSFER, amount, sourceAccount, destinationAccount);
    }
}