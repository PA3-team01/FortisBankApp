package com.fortisbank.ui.forms;

import com.fortisbank.business.services.TransactionService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.ui.uiUtils.StyleUtils;

import java.math.BigDecimal;
import java.util.Date;

public class WithdrawalForm extends TransactionForm {

    private final Account sourceAccount;

    public WithdrawalForm(Account sourceAccount, StorageMode storageMode) {
        super("Withdraw Funds", storageMode);
        this.sourceAccount = sourceAccount;
    }

    @Override
    protected boolean handleConfirm() {
        BigDecimal amount = getEnteredAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            StyleUtils.showStyledErrorDialog(this, "Amount must be positive.");
            return false;
        }

        String description = descriptionField.getText().trim();

        Transaction tx = TransactionFactory.createTransaction(
                TransactionType.WITHDRAWAL,
                description,
                new Date(),
                amount,
                sourceAccount,
                null
        );

        try {
            TransactionService.getInstance(storageMode)
                    .executeTransaction(tx);
            StyleUtils.showStyledSuccessDialog(this, "Withdrawal successful.");
            return true;
        } catch (Exception e) {
            StyleUtils.showStyledErrorDialog(this, e.getMessage());
            return false;
        }
    }
}
