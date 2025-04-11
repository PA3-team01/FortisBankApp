package com.fortisbank.ui.panels.managerPanels;

    import com.fortisbank.business.services.account.InterestRateConfigService;
    import com.fortisbank.contracts.models.accounts.AccountType;
    import com.fortisbank.contracts.models.others.InterestRate;
    import com.fortisbank.ui.ui_utils.StyleUtils;

    import javax.swing.*;
    import java.awt.*;
    import java.math.BigDecimal;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The InterestRateManager class represents the interest rate management panel of the Fortis Bank application.
     * It extends JPanel and provides a user interface to manage interest rates for different account types.
     */
    public class InterestRateManager extends JPanel {

        private static final Logger LOGGER = Logger.getLogger(InterestRateManager.class.getName());
        private final InterestRateConfigService rateService = InterestRateConfigService.getInstance();
        private final DefaultListModel<AccountType> accountTypeModel = new DefaultListModel<>();
        private final JList<AccountType> accountTypeList = new JList<>(accountTypeModel);
        private final JTextField rateField = new JTextField();
        private final JLabel lastUpdatedLabel = new JLabel("Last updated: N/A");

        /**
         * Constructs an InterestRateManager and initializes the user interface components.
         */
        public InterestRateManager() {
            try {
                setLayout(new BorderLayout());
                StyleUtils.styleFormPanel(this);

                JLabel title = new JLabel("Interest Rate Management");
                StyleUtils.styleFormTitle(title);
                add(title, BorderLayout.NORTH);

                JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
                centerPanel.setOpaque(false);

                // Left: Account type list
                for (InterestRate rate : rateService.getAllRates()) {
                    accountTypeModel.addElement(rate.getAccountType());
                }
                accountTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                accountTypeList.addListSelectionListener(e -> populateRateDetails(accountTypeList.getSelectedValue()));
                JScrollPane listScroll = new JScrollPane(accountTypeList);
                listScroll.setPreferredSize(new Dimension(150, 100));

                // Right: Rate details
                JPanel rightPanel = new JPanel();
                rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
                rightPanel.setOpaque(false);

                JLabel rateLabel = new JLabel("Interest Rate (%):");
                StyleUtils.styleLabel(rateLabel);
                StyleUtils.styleTextField(rateField);
                StyleUtils.styleLabel(lastUpdatedLabel);

                JButton updateBtn = new JButton("Update Rate");
                StyleUtils.styleButton(updateBtn, true);
                updateBtn.addActionListener(e -> handleRateUpdate());

                rightPanel.add(rateLabel);
                rightPanel.add(rateField);
                rightPanel.add(Box.createVerticalStrut(5));
                rightPanel.add(lastUpdatedLabel);
                rightPanel.add(Box.createVerticalStrut(10));
                rightPanel.add(updateBtn);

                centerPanel.add(listScroll, BorderLayout.WEST);
                centerPanel.add(rightPanel, BorderLayout.CENTER);
                add(centerPanel, BorderLayout.CENTER);

                if (!accountTypeModel.isEmpty()) {
                    accountTypeList.setSelectedIndex(0);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing InterestRateManager: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the interest rate manager: " + e.getMessage());
            }
        }

        /**
         * Populates the rate details for the selected account type.
         *
         * @param selectedType the selected account type
         */
        private void populateRateDetails(AccountType selectedType) {
            try {
                if (selectedType == null) return;

                InterestRate rate = rateService.getAllRates().stream()
                        .filter(r -> r.getAccountType() == selectedType)
                        .findFirst().orElse(null);

                if (rate != null) {
                    rateField.setText(rate.getRate().multiply(BigDecimal.valueOf(100)).toString());
                    lastUpdatedLabel.setText("Last updated: " + rate.getLastUpdated());
                } else {
                    rateField.setText("");
                    lastUpdatedLabel.setText("Last updated: N/A");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error populating rate details: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to populate rate details: " + e.getMessage());
            }
        }

        /**
         * Handles the update of the interest rate for the selected account type.
         */
        private void handleRateUpdate() {
            AccountType selectedType = accountTypeList.getSelectedValue();
            if (selectedType == null) return;

            try {
                BigDecimal newRate = new BigDecimal(rateField.getText().trim())
                        .divide(BigDecimal.valueOf(100));

                rateService.updateRate(selectedType, newRate);
                populateRateDetails(selectedType);
                StyleUtils.showStyledSuccessDialog(this,"Rate updated successfully.");
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.SEVERE, "Invalid rate input: {0}", ex.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Invalid rate: Please enter a valid number.");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error updating rate: {0}", ex.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to update rate: " + ex.getMessage());
            }
        }
    }