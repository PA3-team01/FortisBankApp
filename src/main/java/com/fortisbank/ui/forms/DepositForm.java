package com.fortisbank.ui.forms;

import com.fortisbank.business.services.transaction.TransactionService;
import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.models.transactions.Transaction;
import com.fortisbank.contracts.models.transactions.TransactionFactory;
import com.fortisbank.contracts.models.transactions.TransactionType;
import com.fortisbank.ui.ui_utils.StyleUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The DepositForm class is a form for handling deposit transactions.
 * It extends the TransactionForm class and provides functionality
 * to deposit funds into a specified account.
 */
public class DepositForm extends TransactionForm {

    private final Account targetAccount;

    /**
     * Constructs a DepositForm with the specified target account and storage mode.
     *
     * @param targetAccount the account to deposit funds into
     * @param storageMode the storage mode to use for transaction services
     */
    public DepositForm(Account targetAccount, StorageMode storageMode) {
        super("Deposit Funds", storageMode);
        this.targetAccount = targetAccount;
    }

    /**
     * Handles the confirmation of the deposit transaction.
     *
     * @return true if the transaction was successful, false otherwise
     */
    @Override
    protected boolean handleConfirm() {
        BigDecimal amount = getEnteredAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            StyleUtils.showStyledErrorDialog(this, "Amount must be positive.");
            return false;
        }

        String description = descriptionField.getText().trim();

        Transaction tx = TransactionFactory.createTransaction(
                TransactionType.DEPOSIT,
                description,
                new Date(),
                amount,
                targetAccount,
                targetAccount
        );

        try {
            TransactionService.getInstance(storageMode)
                    .executeTransaction(tx);
            StyleUtils.showStyledSuccessDialog(this, "Deposit successful.");
            return true;
        } catch (Exception e) {
            StyleUtils.showStyledErrorDialog(this, e.getMessage());
            return false;
        }
    }
}