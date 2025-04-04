package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.repositories.StorageMode;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class CurrencyExchangePanel extends JPanel {

    private final StorageMode storageMode; // StorageMode field to store the storage mode

    public CurrencyExchangePanel(StorageMode storageMode) {
        this.storageMode = storageMode; // Initialize

        // Set layout and style for the panel
        setLayout(new BorderLayout());
        setOpaque(false);

        // === Title Section ===
        JLabel titleLabel = new JLabel("Currency Exchange");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // === Center Content Section ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Currency Selection Panel
        JPanel currencyPanel = new JPanel();
        currencyPanel.setLayout(new GridLayout(2, 2, 10, 10)); // 2x2 grid for input fields
        currencyPanel.setOpaque(false);

        JLabel fromLabel = new JLabel("From Currency:");
        JLabel toLabel = new JLabel("To Currency:");
        JComboBox<String> fromCurrencyComboBox = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "CAD", "JPY"});
        JComboBox<String> toCurrencyComboBox = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "CAD", "JPY"});

        // Styling labels
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add components to currency panel
        currencyPanel.add(fromLabel);
        currencyPanel.add(fromCurrencyComboBox);
        currencyPanel.add(toLabel);
        currencyPanel.add(toCurrencyComboBox);

        // === Amount and Conversion Rate Section ===
        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new GridLayout(2, 2, 10, 10));
        amountPanel.setOpaque(false);

        JLabel amountLabel = new JLabel("Amount to Convert:");
        JTextField amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel conversionRateLabel = new JLabel("Conversion Rate:");
        JTextField conversionRateField = new JTextField();
        conversionRateField.setFont(new Font("Arial", Font.PLAIN, 14));
        conversionRateField.setEditable(false);
        conversionRateField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Add components to amount panel
        amountPanel.add(amountLabel);
        amountPanel.add(amountField);
        amountPanel.add(conversionRateLabel);
        amountPanel.add(conversionRateField);

        // === Convert Button Section ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 14));
        convertButton.setBackground(new Color(66, 133, 244)); // Google blue color
        convertButton.setForeground(Color.WHITE);

        convertButton.addActionListener(e -> {
            // Logic to fetch conversion rate and perform conversion can be added here
            // mock the rate and display the result
            double amount = Double.parseDouble(amountField.getText());
            double conversionRate = 1.2; // Mocked conversion rate (database)
            double convertedAmount = amount * conversionRate;

            conversionRateField.setText(String.valueOf(conversionRate));
            JOptionPane.showMessageDialog(this, "Converted Amount: " + convertedAmount);
        });

        buttonPanel.add(convertButton);

        // === Add all components to center panel ===
        centerPanel.add(currencyPanel);
        centerPanel.add(Box.createVerticalStrut(20)); // Spacing between sections
        centerPanel.add(amountPanel);
        centerPanel.add(Box.createVerticalStrut(20)); // Spacing before the button
        centerPanel.add(buttonPanel);

        // === Adding center panel to the main panel ===
        add(centerPanel, BorderLayout.CENTER);
    }
}
