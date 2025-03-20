package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

public class TransferTransaction extends Transaction {
    public TransferTransaction(String description, Date transactionDate, BigDecimal amount, Account sourceAccount, Account destinationAccount) {
        super(null, description, transactionDate, TransactionType.TRANSFER, amount, sourceAccount, destinationAccount);
    }

    @Override
    public void processTransaction() {
        if (sourceAccount == null || destinationAccount == null) {
            throw new IllegalArgumentException("Source or destination account missing for transfer.");
        }
        sourceAccount.transfer(destinationAccount, amount);
        System.out.println("Transfer transaction processed: " + this);
    }
}
