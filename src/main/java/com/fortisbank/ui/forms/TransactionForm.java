package com.fortisbank.ui.forms;

    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.ui.ui_utils.StyleUtils;

    import javax.swing.*;
    import java.awt.*;
    import java.math.BigDecimal;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The TransactionForm class is an abstract JFrame that provides a form
     * for handling transaction-related operations. It includes fields for
     * the transaction amount and description, and buttons to confirm or cancel
     * the transaction.
     */
    public abstract class TransactionForm extends JFrame {

        private static final Logger LOGGER = Logger.getLogger(TransactionForm.class.getName());

        protected final JTextField amountField = new JTextField();
        protected final JTextField descriptionField = new JTextField();
        protected StorageMode storageMode;

        /**
         * Constructs a TransactionForm with the specified title and storage mode.
         *
         * @param title the title of the form
         * @param mode the storage mode to use for services
         */
        public TransactionForm(String title, StorageMode mode) {
            try {
                storageMode = mode;
                setUndecorated(true);
                setLayout(new BorderLayout());
                setSize(400, 300);
                setLocationRelativeTo(null);
                StyleUtils.applyGlobalFrameStyle(this);

                // === Custom Top Bar ===
                JPanel titleBar = StyleUtils.createCustomTitleBar(this, title, null);
                add(titleBar, BorderLayout.NORTH);

                // === Form Panel ===
                JPanel formPanel = new JPanel();
                formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
                StyleUtils.styleFormPanel(formPanel);

                JLabel amountLabel = new JLabel("Amount:");
                StyleUtils.styleLabel(amountLabel);
                StyleUtils.styleTextField(amountField);

                JLabel descLabel = new JLabel("Description:");
                StyleUtils.styleLabel(descLabel);
                StyleUtils.styleTextField(descriptionField);

                formPanel.add(amountLabel);
                formPanel.add(amountField);
                formPanel.add(Box.createVerticalStrut(10));
                formPanel.add(descLabel);
                formPanel.add(descriptionField);

                add(formPanel, BorderLayout.CENTER);

                // === Buttons ===
                JButton confirmBtn = new JButton("Confirm");
                JButton cancelBtn = new JButton("Cancel");

                StyleUtils.styleButton(confirmBtn, true);
                StyleUtils.styleButton(cancelBtn, false);

                confirmBtn.addActionListener(e -> {
                    try {
                        if (handleConfirm()) {
                            dispose(); // Close on success
                        }
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error during transaction confirmation: {0}", ex.getMessage());
                        StyleUtils.showStyledErrorDialog(this, "An error occurred while processing the transaction: " + ex.getMessage());
                    }
                });

                cancelBtn.addActionListener(e -> dispose());

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setOpaque(false);
                buttonPanel.add(cancelBtn);
                buttonPanel.add(confirmBtn);

                add(buttonPanel, BorderLayout.SOUTH);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing TransactionForm: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the form: " + e.getMessage());
            }
        }

        /**
         * Retrieves the entered amount from the amount field.
         *
         * @return the entered amount as a BigDecimal, or null if the format is invalid
         */
        protected BigDecimal getEnteredAmount() {
            try {
                return new BigDecimal(amountField.getText().trim());
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid amount format entered: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Invalid amount format. Please enter a valid number.");
                return null;
            }
        }

        /**
         * Handles the confirmation of the transaction.
         *
         * @return true if the transaction was successful, false otherwise
         */
        protected abstract boolean handleConfirm();
    }