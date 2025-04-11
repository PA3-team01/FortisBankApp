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
      * The DepositForm class is a form for handling deposit transactions.
      * It extends the TransactionForm class and provides functionality
      * to deposit funds into a specified account.
      */
     public class DepositForm extends TransactionForm {

         private static final Logger LOGGER = Logger.getLogger(DepositForm.class.getName());
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
             try {
                 BigDecimal amount = getEnteredAmount();
                 if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                     StyleUtils.showStyledErrorDialog(this, "Amount must be positive.");
                     return false;
                 }

                 String description = descriptionField.getText().trim();
                 if (description.isEmpty()) {
                     StyleUtils.showStyledErrorDialog(this, "Description cannot be empty.");
                     return false;
                 }

                 Transaction tx = TransactionFactory.createTransaction(
                         TransactionType.DEPOSIT,
                         description,
                         new Date(),
                         amount,
                         targetAccount,
                         targetAccount
                 );

                 TransactionService.getInstance(storageMode).executeTransaction(tx);
                 StyleUtils.showStyledSuccessDialog(this, "Deposit successful.");
                 return true;
             } catch (NumberFormatException e) {
                 LOGGER.log(Level.SEVERE, "Invalid amount entered: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Invalid amount. Please enter a valid number.");
                 return false;
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error processing deposit transaction: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to process deposit: " + e.getMessage());
                 return false;
             }
         }
     }