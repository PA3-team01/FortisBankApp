package com.fortisbank.ui.panels.managerPanels;

import com.fortisbank.business.services.account.InterestRateConfigService;
import com.fortisbank.contracts.models.accounts.AccountType;
import com.fortisbank.contracts.models.others.InterestRate;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * The InterestRateManager class represents the interest rate management panel of the Fortis Bank application.
 * It extends JPanel and provides a user interface to manage interest rates for different account types.
 */
public class InterestRateManager extends JPanel {

    private final InterestRateConfigService rateService = InterestRateConfigService.getInstance();
    private final DefaultListModel<AccountType> accountTypeModel = new DefaultListModel<>();
    private final JList<AccountType> accountTypeList = new JList<>(accountTypeModel);
    private final JTextField rateField = new JTextField();
    private final JLabel lastUpdatedLabel = new JLabel("Last updated: N/A");

    /**
     * Constructs an InterestRateManager and initializes the user interface components.
     */
    public InterestRateManager() {
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
    }

    /**
     * Populates the rate details for the selected account type.
     *
     * @param selectedType the selected account type
     */
    private void populateRateDetails(AccountType selectedType) {
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
            JOptionPane.showMessageDialog(this, "Rate updated successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid rate: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}