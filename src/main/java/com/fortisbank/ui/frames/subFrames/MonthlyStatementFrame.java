package com.fortisbank.ui.frames.subFrames;

    import com.fortisbank.contracts.models.accounts.Account;
    import com.fortisbank.contracts.collections.TransactionList;
    import com.fortisbank.contracts.models.transactions.Transaction;
    import com.fortisbank.ui.ui_utils.StyleUtils;

    import javax.swing.*;
    import java.awt.*;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The MonthlyStatementFrame class represents the monthly statement window of the Fortis Bank application.
     * It extends JFrame and provides a user interface to display account transactions for a specific month and year.
     */
    public class MonthlyStatementFrame extends JFrame {

        private static final Logger LOGGER = Logger.getLogger(MonthlyStatementFrame.class.getName());

        /**
         * Constructs a MonthlyStatementFrame with the specified account, transactions, month, and year.
         *
         * @param account the account for which the statement is generated
         * @param transactions the list of transactions for the specified period
         * @param month the month of the statement period
         * @param year the year of the statement period
         */
        public MonthlyStatementFrame(Account account, TransactionList transactions, String month, int year) {
            try {
                // === Frame Setup ===
                setUndecorated(true);
                setLayout(new BorderLayout());
                setSize(600, 500);
                setLocationRelativeTo(null);
                StyleUtils.applyGlobalFrameStyle(this);

                // === Top Custom Title Bar ===
                JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Monthly Statement", null);
                add(titleBar, BorderLayout.NORTH);

                // === Main Content Panel ===
                JPanel container = new JPanel(new BorderLayout());
                StyleUtils.styleFormPanel(container);

                // === HEADER INFO ===
                JPanel header = new JPanel();
                header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
                header.setOpaque(false);

                JLabel title = new JLabel("Monthly Statement");
                StyleUtils.styleFormTitle(title);
                header.add(title);
                header.add(Box.createVerticalStrut(8));

                JLabel accountInfo = new JLabel("Account: " + account.getAccountType()
                        + " (" + account.getAccountNumber() + ") — Balance: $" + String.format("%.2f", account.getAvailableBalance()));
                StyleUtils.styleLabel(accountInfo);
                header.add(accountInfo);

                JLabel period = new JLabel("Period: " + month + " " + year);
                StyleUtils.styleLabel(period);
                header.add(period);

                container.add(header, BorderLayout.NORTH);

                // === TRANSACTIONS ===
                JPanel txPanel = new JPanel();
                txPanel.setLayout(new BoxLayout(txPanel, BoxLayout.Y_AXIS));
                txPanel.setOpaque(false);

                if (transactions == null || transactions.isEmpty()) {
                    JLabel emptyLabel = new JLabel("No transactions for this period.");
                    StyleUtils.styleLabel(emptyLabel);
                    txPanel.add(emptyLabel);
                } else {
                    for (Transaction tx : transactions) {
                        JLabel txLabel = new JLabel("• [" + tx.getTransactionDate() + "] "
                                + tx.getTransactionType() + " — $" + String.format("%.2f", tx.getAmount()));
                        StyleUtils.styleLabel(txLabel);
                        txPanel.add(txLabel);
                        txPanel.add(Box.createVerticalStrut(4));
                    }
                }

                JScrollPane scrollPane = new JScrollPane(txPanel);
                scrollPane.setBorder(null);
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);

                container.add(scrollPane, BorderLayout.CENTER);
                add(container, BorderLayout.CENTER);

                setVisible(true);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing MonthlyStatementFrame: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the monthly statement frame: " + e.getMessage());
            }
        }
    }