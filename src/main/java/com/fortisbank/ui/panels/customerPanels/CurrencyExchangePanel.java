package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.others.CurrencyType;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CurrencyExchangePanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(CurrencyExchangePanel.class.getName());
    private final StorageMode storageMode;

    public CurrencyExchangePanel(StorageMode storageMode) {
        this.storageMode = storageMode;

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

        // === Left Panel (Dynamic Rates) ===
        JPanel leftRatePanel = new JPanel();
        leftRatePanel.setLayout(new BoxLayout(leftRatePanel, BoxLayout.Y_AXIS));
        leftRatePanel.setOpaque(false);

        JLabel ratesTitle = new JLabel("Rates (Base: USD):");
        ratesTitle.setFont(new Font("Arial", Font.BOLD, 22));
        ratesTitle.setForeground(Color.WHITE);
        leftRatePanel.add(ratesTitle);

        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        JLabel timeLabel = new JLabel("Last updated: " + timestamp);
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        timeLabel.setForeground(Color.LIGHT_GRAY);
        leftRatePanel.add(timeLabel);
        leftRatePanel.add(Box.createVerticalStrut(10));

        try {
            CurrencyType currencyType = CurrencyType.getInstance();
            Map<String, BigDecimal> rates = currencyType.getAllExchangeRates();

            for (Map.Entry<String, BigDecimal> entry : rates.entrySet()) {
                if (!entry.getKey().equalsIgnoreCase("USD")) {
                    JLabel rateLabel = new JLabel("1 USD = " + entry.getValue() + " " + entry.getKey());
                    rateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    rateLabel.setForeground(Color.LIGHT_GRAY);
                    leftRatePanel.add(rateLabel);
                    leftRatePanel.add(Box.createVerticalStrut(5));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading exchange rates: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to load exchange rates: " + e.getMessage());
        }

        add(leftRatePanel, BorderLayout.WEST);

        // === Center Panel ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.')) {
                    e.consume();
                }
            }
        });

        JLabel toLabel = new JLabel("To:");
        toLabel.setForeground(Color.WHITE);
        toLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JComboBox<String> toCurrencyComboBox = new JComboBox<>(new String[]{
                "$ USD", "€ EUR", "£ GBP", "₣ CAD", "¥ JPY"
        });
        toCurrencyComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        toCurrencyComboBox.setPreferredSize(new Dimension(200, 40));

        JLabel rateLabel = new JLabel("Rate:");
        rateLabel.setForeground(Color.WHITE);
        rateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JTextField conversionRateField = new JTextField();
        conversionRateField.setFont(new Font("Arial", Font.PLAIN, 16));
        conversionRateField.setEditable(false);
        conversionRateField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        conversionRateField.setPreferredSize(new Dimension(200, 40));

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
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBackground(new Color(66, 133, 244));
        convertButton.setForeground(Color.WHITE);
        convertButton.setPreferredSize(new Dimension(200, 40));

        convertButton.addActionListener(e -> {
            try {
                String fromCurrency = (String) fromCurrencyComboBox.getSelectedItem();
                String toCurrency = (String) toCurrencyComboBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());

                String fromCode = fromCurrency.split(" ")[1];
                String toCode = toCurrency.split(" ")[1];

                CurrencyType currencyType = CurrencyType.getInstance();
                BigDecimal fromRate = currencyType.getExchangeRate(fromCode);
                BigDecimal toRate = currencyType.getExchangeRate(toCode);

                if (fromRate.compareTo(BigDecimal.ZERO) == 0 || toRate.compareTo(BigDecimal.ZERO) == 0) {
                    StyleUtils.showStyledErrorDialog(this, "Unsupported currency conversion.");
                    return;
                }

                BigDecimal usdAmount = BigDecimal.valueOf(amount).divide(fromRate, 6, BigDecimal.ROUND_HALF_UP);
                BigDecimal targetAmount = usdAmount.multiply(toRate);
                BigDecimal conversionRate = toRate.divide(fromRate, 6, BigDecimal.ROUND_HALF_UP);

                conversionRateField.setText(String.format("%.4f", conversionRate));
                StyleUtils.showStyledSuccessDialog(this,
                        "Converted Amount: " + targetAmount.setScale(2, BigDecimal.ROUND_HALF_UP)
                );
            } catch (NumberFormatException ex) {
                StyleUtils.showStyledErrorDialog(this, "Please enter a valid number.");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error during conversion: {0}", ex.getMessage());
                StyleUtils.showStyledErrorDialog(this, "An error occurred during conversion: " + ex.getMessage());
            }
        });

        buttonPanel.add(convertButton);
        centerPanel.add(buttonPanel);

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(centerPanel);

        add(wrapperPanel, BorderLayout.CENTER);
    }
}