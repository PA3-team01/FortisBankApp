package com.fortisbank.contracts.models.transactions;

import com.fortisbank.contracts.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class representing a deposit transaction.
 */
public class DepositTransaction extends Transaction {

    /**
     * Constructor initializing the deposit transaction with specified values.
     *
     * @param description the description of the transaction
     * @param transactionDate the date of the transaction
     * @param amount the amount of the transaction
     * @param destinationAccount the account to which the amount is deposited
     */
    public DepositTransaction(String description, Date transactionDate, BigDecimal amount, Account destinationAccount) {
        super(null, description, transactionDate, TransactionType.DEPOSIT, amount, null, destinationAccount);
    }
}