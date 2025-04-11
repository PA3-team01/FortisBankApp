package com.fortisbank.ui.forms;

     import com.fortisbank.business.services.account.AccountLoanRequestService;
     import com.fortisbank.business.services.account.InterestRateConfigService;
     import com.fortisbank.business.services.users.manager.BankManagerService;
     import com.fortisbank.data.dal_utils.StorageMode;
     import com.fortisbank.contracts.models.accounts.*;
     import com.fortisbank.contracts.models.users.BankManager;
     import com.fortisbank.contracts.models.users.Customer;
     import com.fortisbank.business.services.session.SessionManager;
     import com.fortisbank.ui.ui_utils.StyleUtils;
     import com.fortisbank.contracts.utils.IdGenerator;

     import javax.swing.*;
     import java.awt.*;
     import java.math.BigDecimal;
     import java.util.Date;
     import java.util.List;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     /**
      * The AccountRequestForm class is a JFrame that provides a form for customers
      * to request new accounts. It dynamically adjusts fields based on the selected
      * account type and allows submission of the request.
      */
     public class AccountRequestForm extends JFrame {

         private static final Logger LOGGER = Logger.getLogger(AccountRequestForm.class.getName());

         private final JComboBox<AccountType> typeSelector = new JComboBox<>();
         private final JComboBox<BankManager> managerSelector = new JComboBox<>();
         private final JPanel dynamicFieldsPanel = new JPanel();
         private final JTextField balanceField = new JTextField();
         private final JLabel interestRateLabel = new JLabel();
         private final JTextField creditLimitField = new JTextField();
         private final JTextField currencyCodeField = new JTextField();

         private final StorageMode storageMode;

         public AccountRequestForm(StorageMode storageMode) {
             super("Request New Account");
             this.storageMode = storageMode;
             try {
                 initializeUI();
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error initializing AccountRequestForm: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to initialize the form: " + e.getMessage());
             }
         }

         private void initializeUI() {
             setSize(450, 400);
             setLocationRelativeTo(null);
             setUndecorated(true);
             StyleUtils.applyGlobalFrameStyle(this);
             setLayout(new BorderLayout());

             JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Account Request", null);
             add(titleBar, BorderLayout.NORTH);

             JPanel formPanel = new JPanel();
             formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
             StyleUtils.styleFormPanel(formPanel);

             JLabel typeLabel = new JLabel("Account Type:");
             StyleUtils.styleLabel(typeLabel);
             for (AccountType type : AccountType.values()) typeSelector.addItem(type);
             StyleUtils.styleDropdown(typeSelector);
             typeSelector.addActionListener(e -> renderDynamicFields((AccountType) typeSelector.getSelectedItem()));

             JLabel managerLabel = new JLabel("Select Manager:");
             StyleUtils.styleLabel(managerLabel);
             StyleUtils.styleDropdown(managerSelector);
             populateManagerDropdown();

             dynamicFieldsPanel.setLayout(new BoxLayout(dynamicFieldsPanel, BoxLayout.Y_AXIS));
             dynamicFieldsPanel.setOpaque(false);

             formPanel.add(typeLabel);
             formPanel.add(typeSelector);
             formPanel.add(Box.createVerticalStrut(10));
             formPanel.add(managerLabel);
             formPanel.add(managerSelector);
             formPanel.add(Box.createVerticalStrut(10));
             formPanel.add(dynamicFieldsPanel);

             renderDynamicFields(AccountType.CHECKING);

             JButton requestBtn = new JButton("Submit Request");
             StyleUtils.styleButton(requestBtn, true);
             requestBtn.addActionListener(e -> handleRequest());

             JPanel bottom = new JPanel();
             bottom.setOpaque(false);
             bottom.add(requestBtn);

             add(formPanel, BorderLayout.CENTER);
             add(bottom, BorderLayout.SOUTH);
         }

         private void populateManagerDropdown() {
             try {
                 List<BankManager> managers = BankManagerService.getInstance(storageMode).getAllManagers();
                 for (BankManager manager : managers) managerSelector.addItem(manager);

                 managerSelector.setRenderer(new DefaultListCellRenderer() {
                     @Override
                     public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                         JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                         if (value instanceof BankManager m) {
                             label.setText(m.getFullName());
                         }
                         return label;
                     }
                 });
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error populating manager dropdown: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to load managers: " + e.getMessage());
             }
         }

         private void renderDynamicFields(AccountType type) {
             dynamicFieldsPanel.removeAll();

             try {
                 InterestRateConfigService rateService = InterestRateConfigService.getInstance();
                 BigDecimal savingsRate = rateService.getRate(AccountType.SAVINGS).multiply(BigDecimal.valueOf(100));
                 BigDecimal creditRate = rateService.getRate(AccountType.CREDIT).multiply(BigDecimal.valueOf(100));

                 switch (type) {
                     case CHECKING, SAVINGS, CURRENCY -> {
                         JLabel balanceLabel = new JLabel("Initial Balance:");
                         StyleUtils.styleLabel(balanceLabel);
                         StyleUtils.styleTextField(balanceField);
                         dynamicFieldsPanel.add(balanceLabel);
                         dynamicFieldsPanel.add(balanceField);
                     }
                 }

                 switch (type) {
                     case SAVINGS -> {
                         JLabel rateLabel = new JLabel("Interest Rate (%):");
                         interestRateLabel.setText(savingsRate + " %");
                         interestRateLabel.setForeground(Color.GRAY);
                         StyleUtils.styleLabel(rateLabel);
                         StyleUtils.styleLabel(interestRateLabel);
                         dynamicFieldsPanel.add(rateLabel);
                         dynamicFieldsPanel.add(interestRateLabel);
                     }
                     case CREDIT -> {
                         JLabel creditLabel = new JLabel("Requested Credit Amount:");
                         JLabel rateLabel = new JLabel("Interest Rate (%):");
                         interestRateLabel.setText(creditRate + " %");
                         interestRateLabel.setForeground(Color.GRAY);
                         StyleUtils.styleLabel(creditLabel);
                         StyleUtils.styleLabel(rateLabel);
                         StyleUtils.styleLabel(interestRateLabel);
                         StyleUtils.styleTextField(creditLimitField);
                         dynamicFieldsPanel.add(creditLabel);
                         dynamicFieldsPanel.add(creditLimitField);
                         dynamicFieldsPanel.add(rateLabel);
                         dynamicFieldsPanel.add(interestRateLabel);
                     }
                     case CURRENCY -> {
                         JLabel codeLabel = new JLabel("Currency Code (e.g., USD):");
                         StyleUtils.styleLabel(codeLabel);
                         StyleUtils.styleTextField(currencyCodeField);
                         dynamicFieldsPanel.add(codeLabel);
                         dynamicFieldsPanel.add(currencyCodeField);
                     }
                 }

                 dynamicFieldsPanel.revalidate();
                 dynamicFieldsPanel.repaint();
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error rendering dynamic fields: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to render fields: " + e.getMessage());
             }
         }

         private void handleRequest() {
             try {
                 AccountType selectedType = (AccountType) typeSelector.getSelectedItem();
                 BankManager selectedManager = (BankManager) managerSelector.getSelectedItem();
                 Customer customer = SessionManager.getCustomer();

                 if (selectedType == null || selectedManager == null || customer == null) {
                     throw new IllegalArgumentException("Invalid input. Please ensure all fields are filled.");
                 }

                 BigDecimal balance = new BigDecimal(balanceField.getText().trim());
                 String accountNumber = IdGenerator.generateId();
                 Date now = new Date();

                 Account account = switch (selectedType) {
                     case CHECKING -> new CheckingAccount(accountNumber, customer, now, balance);
                     case SAVINGS -> new SavingsAccount(accountNumber, customer, now, balance,
                             InterestRateConfigService.getInstance().getRate(AccountType.SAVINGS));
                     case CREDIT -> {
                         BigDecimal creditAmount = new BigDecimal(creditLimitField.getText().trim());
                         CreditAccount credit = new CreditAccount(accountNumber, customer, now, creditAmount,
                                 InterestRateConfigService.getInstance().getRate(AccountType.CREDIT));
                         credit.setAvailableBalance(creditAmount);
                         yield credit;
                     }
                     case CURRENCY -> new CurrencyAccount(accountNumber, customer, now, balance,
                             currencyCodeField.getText().trim());
                 };

                 account.setActive(false);

                 AccountLoanRequestService.getInstance(storageMode)
                         .submitAccountRequest(customer, account, selectedManager);

                 StyleUtils.showStyledSuccessDialog(this, "Request submitted successfully.");
                 dispose();
             } catch (NumberFormatException ex) {
                 StyleUtils.showStyledErrorDialog(this, "Invalid numeric input: " + ex.getMessage());
             } catch (Exception ex) {
                 LOGGER.log(Level.SEVERE, "Error handling request: {0}", ex.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to create account: " + ex.getMessage());
             }
         }
     }