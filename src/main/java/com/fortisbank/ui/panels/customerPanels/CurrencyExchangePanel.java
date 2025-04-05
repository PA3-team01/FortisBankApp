package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.repositories.StorageMode;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CurrencyExchangePanel extends JPanel {

    private final StorageMode storageMode;

    // Hardcoded example rates (from currency → to currency → rate)
    private final Map<String, Double> exchangeRates = new HashMap<String, Double>() {{
        put("USD_TO_EUR", 0.85);
        put("USD_TO_CAD", 1.47);
        put("EUR_TO_USD", 1.18);
        put("EUR_TO_CAD", 1.73);
        put("GBP_TO_USD", 1.25);
        put("GBP_TO_EUR", 1.12);
        put("CAD_TO_USD", 0.68);
        put("CAD_TO_EUR", 0.49);
        put("JPY_TO_EUR", 0.0068);
    }};

    public CurrencyExchangePanel(StorageMode storageMode) {
        this.storageMode = storageMode;

        // Panel setup
        setLayout(new BorderLayout());
        setBackground(new Color(40, 44, 52));
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === Title ===
        JLabel titleLabel = new JLabel("Currency Exchange");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // === Left: Hardcoded Exchange Rate Panel ===
        JPanel leftRatePanel = new JPanel();
        leftRatePanel.setLayout(new BoxLayout(leftRatePanel, BoxLayout.Y_AXIS));
        leftRatePanel.setOpaque(false);

        JLabel ratesTitle = new JLabel("Rates:");
        ratesTitle.setFont(new Font("Arial", Font.BOLD, 22));
        ratesTitle.setForeground(Color.WHITE);
        leftRatePanel.add(ratesTitle);

        // Timestamp
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        JLabel timeLabel = new JLabel("Last updated: " + timestamp);
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        timeLabel.setForeground(Color.LIGHT_GRAY);
        leftRatePanel.add(timeLabel);

        leftRatePanel.add(Box.createVerticalStrut(10));

        // Example Rates (no flags)
        String[] exampleRates = {
                "USD to EUR: 1 USD = 0.85 EUR",
                "USD to CAD: 1 USD = 1.47 CAD",
                "EUR to USD: 1 EUR = 1.18 USD",
                "GBP to USD: 1 GBP = 1.25 USD",
                "CAD to USD: 1 CAD = 0.68 USD"
        };

        for (String rate : exampleRates) {
            JLabel rateLabel = new JLabel(rate);
            rateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            rateLabel.setForeground(Color.LIGHT_GRAY);
            leftRatePanel.add(rateLabel);
            leftRatePanel.add(Box.createVerticalStrut(5));
        }

        add(leftRatePanel, BorderLayout.WEST);

        // === Center Panel ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === Combined Input Grid ===
        JPanel inputGridPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        inputGridPanel.setOpaque(false);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setForeground(Color.WHITE);
        fromLabel.setFont(new Font("Arial", Font.BOLD, 18));


        JComboBox<String> fromCurrencyComboBox = new JComboBox<>(new String[]{
                "$ USD", "€ EUR", "£ GBP", "₣ CAD", "¥ JPY"
        });
        fromCurrencyComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        fromCurrencyComboBox.setPreferredSize(new Dimension(200, 40));

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JTextField amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        amountField.setPreferredSize(new Dimension(200, 40));

        // Restrict input to numbers only (including decimals)
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateInput();
            }

            private void validateInput() {
                String text = amountField.getText();
                if (!text.matches("^[0-9]*\\.?[0-9]*$")) { // Allow numbers and a single decimal point
                    amountField.setText(text.substring(0, text.length() - 1));
                }
            }
        });

        JLabel toLabel = new JLabel("To:");
        toLabel.setForeground(Color.WHITE);
        toLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger font for To label

        // JComboBox with currency symbols and names, with larger font and dimensions
        JComboBox<String> toCurrencyComboBox = new JComboBox<>(new String[]{
                "$ USD", "€ EUR", "£ GBP", "₣ CAD", "¥ JPY"
        });
        toCurrencyComboBox.setFont(new Font("Arial", Font.PLAIN, 16)); // Larger font for combo box
        toCurrencyComboBox.setPreferredSize(new Dimension(200, 40)); // Larger combo box size

        JLabel rateLabel = new JLabel("Rate:");
        rateLabel.setForeground(Color.WHITE);
        rateLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger font for Rate label
        JTextField conversionRateField = new JTextField();
        conversionRateField.setFont(new Font("Arial", Font.PLAIN, 16)); // Larger font for text field
        conversionRateField.setEditable(false);
        conversionRateField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        conversionRateField.setPreferredSize(new Dimension(200, 40)); // Larger text field size

        inputGridPanel.add(fromLabel);
        inputGridPanel.add(fromCurrencyComboBox);
        inputGridPanel.add(amountLabel);
        inputGridPanel.add(amountField);
        inputGridPanel.add(toLabel);
        inputGridPanel.add(toCurrencyComboBox);
        inputGridPanel.add(rateLabel);
        inputGridPanel.add(conversionRateField);

        centerPanel.add(inputGridPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // === Convert Button ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16)); // Larger font for button
        convertButton.setBackground(new Color(66, 133, 244));
        convertButton.setForeground(Color.WHITE);
        convertButton.setPreferredSize(new Dimension(200, 40)); // Larger button size

        convertButton.addActionListener(e -> {
            try {
                String fromCurrency = (String) fromCurrencyComboBox.getSelectedItem();
                String toCurrency = (String) toCurrencyComboBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());

                // Extract the currency code from the selected value (remove the symbol)
                String fromCurrencyCode = fromCurrency.split(" ")[1];
                String toCurrencyCode = toCurrency.split(" ")[1];

                // Using the proper currency codes for the key
                String rateKey = fromCurrencyCode + "_TO_" + toCurrencyCode;
                double conversionRate = exchangeRates.getOrDefault(rateKey, 1.0);
                double convertedAmount = amount * conversionRate;

                conversionRateField.setText(String.valueOf(conversionRate));

                // Round result to 2 decimal places
                String formattedAmount = String.format("%.2f", convertedAmount);

                JOptionPane.showMessageDialog(this,
                        "Converted Amount: " + formattedAmount,
                        "Conversion Result",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        buttonPanel.add(convertButton);
        centerPanel.add(buttonPanel);

        // === Wrapper to center centerPanel ===
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(centerPanel);

        add(wrapperPanel, BorderLayout.CENTER);
    }
}
