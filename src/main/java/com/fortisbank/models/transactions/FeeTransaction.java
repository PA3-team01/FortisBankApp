package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class representing a fee transaction.
 */
public class FeeTransaction extends Transaction {

    /**
     * Constructor initializing the fee transaction with specified values.
     *
     * @param description the description of the transaction
     * @param transactionDate the date of the transaction
     * @param amount the amount of the transaction
     * @param sourceAccount the account from which the fee is deducted
     */
    public FeeTransaction(String description, Date transactionDate, BigDecimal amount, Account sourceAccount) {
        super(null, description, transactionDate, TransactionType.FEE, amount, sourceAccount, null);
    }
}