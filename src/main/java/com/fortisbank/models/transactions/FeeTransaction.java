package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

public class FeeTransaction extends Transaction {
    public FeeTransaction(String description, Date transactionDate, BigDecimal amount, Account sourceAccount) {
        super(null, description, transactionDate, TransactionType.FEE, amount, sourceAccount, null);
    }
}
