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
     import java.util.logging.Level;
     import java.util.logging.Logger;

     /**
      * The WithdrawalForm class is a form for handling withdrawal transactions.
      * It extends the TransactionForm class and provides functionality
      * to withdraw funds from a specified account.
      */
     public class WithdrawalForm extends TransactionForm {

         private static final Logger LOGGER = Logger.getLogger(WithdrawalForm.class.getName());
         private final Account sourceAccount;

         /**
          * Constructs a WithdrawalForm with the specified source account and storage mode.
          *
          * @param sourceAccount the account to withdraw funds from
          * @param storageMode the storage mode to use for transaction services
          */
         public WithdrawalForm(Account sourceAccount, StorageMode storageMode) {
             super("Withdraw Funds", storageMode);
             this.sourceAccount = sourceAccount;
         }

         /**
          * Handles the confirmation of the withdrawal transaction.
          *
          * @return true if the transaction was successful, false otherwise
          */
         @Override
         protected boolean handleConfirm() {
             try {
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
                         sourceAccount
                 );

                 TransactionService.getInstance(storageMode).executeTransaction(tx);
                 StyleUtils.showStyledSuccessDialog(this, "Withdrawal successful.");
                 return true;
             } catch (NumberFormatException e) {
                 LOGGER.log(Level.WARNING, "Invalid amount format entered: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Invalid amount format. Please enter a valid number.");
                 return false;
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error during withdrawal transaction: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to process withdrawal: " + e.getMessage());
                 return false;
             }
         }
     }