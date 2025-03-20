package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

public class DepositTransaction extends Transaction {
    public DepositTransaction(String description, Date transactionDate, BigDecimal amount, Account destinationAccount) {
        super(null, description, transactionDate, TransactionType.DEPOSIT, amount, null, destinationAccount);
    }

    @Override
    public void processTransaction() {
        if (destinationAccount == null) {
            throw new IllegalArgumentException("Destination account missing for deposit.");
        }
        destinationAccount.deposit(amount);
        System.out.println("Deposit transaction processed: " + this);
    }
}
