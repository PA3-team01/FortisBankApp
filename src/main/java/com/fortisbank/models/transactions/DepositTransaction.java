package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

public class DepositTransaction extends Transaction {
    public DepositTransaction(String description, Date transactionDate, BigDecimal amount, Account destinationAccount) {
        super(null, description, transactionDate, TransactionType.DEPOSIT, amount, null, destinationAccount);
    }
}
