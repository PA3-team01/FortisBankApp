package com.fortisbank.ui.forms;

     import com.fortisbank.business.services.users.customer.CustomerService;
     import com.fortisbank.business.services.transaction.TransactionService;
     import com.fortisbank.data.dal_utils.StorageMode;
     import com.fortisbank.contracts.models.accounts.Account;
     import com.fortisbank.contracts.collections.CustomerList;
     import com.fortisbank.contracts.models.transactions.Transaction;
     import com.fortisbank.contracts.models.transactions.TransactionFactory;
     import com.fortisbank.contracts.models.transactions.TransactionType;
     import com.fortisbank.contracts.models.users.Customer;
     import com.fortisbank.business.services.session.SessionManager;
     import com.fortisbank.ui.ui_utils.StyleUtils;

     import javax.swing.*;
     import java.awt.*;
     import java.math.BigDecimal;
     import java.util.Date;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     /**
      * The TransferForm class is a form for handling fund transfers between accounts.
      * It extends the TransactionForm class and provides functionality to transfer funds
      * from a source account to a destination account.
      */
     public class TransferForm extends TransactionForm {

         private static final Logger LOGGER = Logger.getLogger(TransferForm.class.getName());

         private final Account sourceAccount;
         private final JComboBox<Customer> customerSelector = new JComboBox<>();
         private final JComboBox<Account> destinationSelector = new JComboBox<>();
         private final JRadioButton selfTransferBtn = new JRadioButton("My Accounts");
         private final JRadioButton otherTransferBtn = new JRadioButton("Another Customer");
         private final JPanel dynamicRecipientPanel = new JPanel();
         private final JTextField amountField = new JTextField();
         private final JTextField descriptionField = new JTextField();

         public TransferForm(Account sourceAccount, StorageMode storageMode) {
             super("Transfer Funds", storageMode);
             this.sourceAccount = sourceAccount;
             setSize(600, 300);

             try {
                 setupTransferTypeSelector();
                 setupCustomerDropdown();
                 buildTransferPanel();
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error initializing TransferForm: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to initialize the form: " + e.getMessage());
             }
         }

         private void setupTransferTypeSelector() {
             ButtonGroup group = new ButtonGroup();
             group.add(selfTransferBtn);
             group.add(otherTransferBtn);

             selfTransferBtn.setSelected(true);

             selfTransferBtn.addActionListener(e -> showSelfTransferOptions());
             otherTransferBtn.addActionListener(e -> showOtherCustomerOptions());
             StyleUtils.styleRadioButton(selfTransferBtn);
             StyleUtils.styleRadioButton(otherTransferBtn);
         }

         private void setupCustomerDropdown() {
             try {
                 CustomerList customers = CustomerService.getInstance(storageMode).getAllCustomers();

                 for (Customer customer : customers) {
                     customerSelector.addItem(customer);
                 }
                 StyleUtils.styleDropdown(customerSelector);

                 customerSelector.setRenderer(new DefaultListCellRenderer() {
                     @Override
                     public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                         super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                         if (value instanceof Customer customer) {
                             setText(customer.getFullName());
                         }
                         return this;
                     }
                 });
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error setting up customer dropdown: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to load customers: " + e.getMessage());
             }
         }

         private void buildTransferPanel() {
             JPanel topPanel = new JPanel();
             topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
             topPanel.setOpaque(false);

             JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
             radioPanel.setOpaque(false);
             radioPanel.add(selfTransferBtn);
             radioPanel.add(otherTransferBtn);

             dynamicRecipientPanel.setLayout(new BoxLayout(dynamicRecipientPanel, BoxLayout.Y_AXIS));
             dynamicRecipientPanel.setOpaque(false);

             topPanel.add(radioPanel);
             topPanel.add(Box.createVerticalStrut(10));
             topPanel.add(dynamicRecipientPanel);

             getContentPane().add(topPanel, BorderLayout.CENTER);
             showSelfTransferOptions(); // default
         }

         private JPanel getAmountAndDescriptionPanel() {
             JPanel panel = new JPanel();
             panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
             panel.setOpaque(false);

             JLabel amountLabel = new JLabel("Amount:");
             JLabel descLabel = new JLabel("Description:");
             StyleUtils.styleLabel(amountLabel);
             StyleUtils.styleLabel(descLabel);
             StyleUtils.styleTextField(amountField);
             StyleUtils.styleTextField(descriptionField);

             panel.add(amountLabel);
             panel.add(amountField);
             panel.add(Box.createVerticalStrut(10));
             panel.add(descLabel);
             panel.add(descriptionField);

             return panel;
         }

         private void showSelfTransferOptions() {
             try {
                 dynamicRecipientPanel.removeAll();
                 destinationSelector.removeAllItems();

                 Customer self = SessionManager.getCustomer();
                 for (Account acc : self.getAccounts()) {
                     if (!acc.getAccountNumber().equals(sourceAccount.getAccountNumber()) && acc.isActive()) {
                         destinationSelector.addItem(acc);
                     }
                 }
                 StyleUtils.styleDropdown(destinationSelector);
                 JLabel label = new JLabel("Select Destination Account:");
                 StyleUtils.styleLabel(label);

                 dynamicRecipientPanel.add(label);
                 dynamicRecipientPanel.add(destinationSelector);
                 dynamicRecipientPanel.add(Box.createVerticalStrut(10));
                 dynamicRecipientPanel.add(getAmountAndDescriptionPanel());
                 dynamicRecipientPanel.revalidate();
                 dynamicRecipientPanel.repaint();
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error displaying self-transfer options: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to display self-transfer options: " + e.getMessage());
             }
         }

         private void showOtherCustomerOptions() {
             try {
                 dynamicRecipientPanel.removeAll();

                 JLabel customerLabel = new JLabel("Select Customer:");
                 StyleUtils.styleLabel(customerLabel);

                 dynamicRecipientPanel.add(customerLabel);
                 dynamicRecipientPanel.add(customerSelector);
                 dynamicRecipientPanel.add(Box.createVerticalStrut(10));
                 dynamicRecipientPanel.add(getAmountAndDescriptionPanel());
                 dynamicRecipientPanel.revalidate();
                 dynamicRecipientPanel.repaint();
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error displaying other customer options: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to display other customer options: " + e.getMessage());
             }
         }

         @Override
         protected boolean handleConfirm() {
             try {
                 Customer selectedCustomer = (Customer) customerSelector.getSelectedItem();
                 if (selectedCustomer == null) {
                     StyleUtils.showStyledErrorDialog(this, "Please select a customer.");
                     return false;
                 }

                 Account destination = selectedCustomer.getAccounts().stream()
                         .filter(Account::isActive)
                         .findFirst()
                         .orElse(null);

                 if (destination == null) {
                     StyleUtils.showStyledErrorDialog(this, "The selected customer has no active accounts.");
                     return false;
                 }

                 BigDecimal amount;
                 try {
                     amount = new BigDecimal(amountField.getText().trim());
                 } catch (NumberFormatException e) {
                     LOGGER.log(Level.WARNING, "Invalid amount format entered: {0}", e.getMessage());
                     StyleUtils.showStyledErrorDialog(this, "Invalid amount format.");
                     return false;
                 }

                 if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                     StyleUtils.showStyledErrorDialog(this, "Amount must be positive.");
                     return false;
                 }

                 String description = descriptionField.getText().trim();

                 Transaction tx = TransactionFactory.createTransaction(
                         TransactionType.TRANSFER,
                         description,
                         new Date(),
                         amount,
                         sourceAccount,
                         destination
                 );

                 TransactionService.getInstance(storageMode).executeTransaction(tx);
                 StyleUtils.showStyledSuccessDialog(this, "Transfer successful.");
                 return true;
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error during transfer confirmation: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to process transfer: " + e.getMessage());
                 return false;
             }
         }
     }