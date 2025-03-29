package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;

import java.math.BigDecimal;
import java.util.Date;

public class WithdrawalTransaction extends Transaction {
    public WithdrawalTransaction(String description, Date transactionDate, BigDecimal amount, Account sourceAccount) {
        super(null, description, transactionDate, TransactionType.WITHDRAWAL, amount, sourceAccount, null);
    }
}
