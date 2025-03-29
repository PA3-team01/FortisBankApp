package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

public class TransferTransaction extends Transaction {
    public TransferTransaction(String description, Date transactionDate, BigDecimal amount, Account sourceAccount, Account destinationAccount) {
        super(null, description, transactionDate, TransactionType.TRANSFER, amount, sourceAccount, destinationAccount);
    }
}
