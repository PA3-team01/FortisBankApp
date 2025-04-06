package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class representing a withdrawal transaction.
 */
public class WithdrawalTransaction extends Transaction {

    /**
     * Constructor initializing the withdrawal transaction with specified values.
     *
     * @param description the description of the transaction
     * @param transactionDate the date of the transaction
     * @param amount the amount of the transaction
     * @param sourceAccount the account from which the amount is withdrawn
     */
    public WithdrawalTransaction(String description, Date transactionDate, BigDecimal amount, Account sourceAccount) {
        super(null, description, transactionDate, TransactionType.WITHDRAWAL, amount, sourceAccount, null);
    }
}