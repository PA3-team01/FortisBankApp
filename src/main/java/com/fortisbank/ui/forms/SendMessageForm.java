package com.fortisbank.ui.forms;

    import com.fortisbank.business.services.users.manager.BankManagerService;
    import com.fortisbank.business.services.notification.NotificationService;
    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.contracts.models.users.User;
    import com.fortisbank.business.services.session.SessionManager;
    import com.fortisbank.ui.ui_utils.StyleUtils;

    import javax.swing.*;
    import javax.swing.border.EmptyBorder;
    import java.awt.*;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The SendMessageForm class is a JPanel component that provides a form
     * for sending messages. It includes fields for the sender, recipient,
     * subject, and message body, and buttons to send or cancel the message.
     */
    public class SendMessageForm extends JPanel {

        private static final Logger LOGGER = Logger.getLogger(SendMessageForm.class.getName());

        private final JTextField fromField = new JTextField();
        private final JTextField toField = new JTextField();
        private final JTextField subjectField = new JTextField();
        private final JTextArea messageArea = new JTextArea(8, 30);
        private final StorageMode storageMode;

        /**
         * Constructs a SendMessageForm with the specified storage mode.
         *
         * @param storageMode the storage mode to use for services
         */
        public SendMessageForm(StorageMode storageMode) {
            this.storageMode = storageMode;
            try {
                setLayout(new BorderLayout());
                StyleUtils.styleFormPanel(this);

                // === Header ===
                JLabel title = new JLabel("Send a Message");
                StyleUtils.styleFormTitle(title);
                add(title, BorderLayout.NORTH);

                // === Center form area ===
                JPanel formPanel = new JPanel();
                formPanel.setLayout(new GridBagLayout());
                formPanel.setOpaque(false);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);

                // --- From Field ---
                User currentUser = SessionManager.getCurrentUser();
                fromField.setText(currentUser != null ? currentUser.getEmail() : "unknown@fortisbank.com");
                fromField.setEditable(false);
                StyleUtils.styleTextField(fromField);
                addLabeledField(formPanel, "From:", fromField, gbc, 0);

                // --- To Field ---
                StyleUtils.styleTextField(toField);
                addLabeledField(formPanel, "To:", toField, gbc, 1);

                // --- Subject Field ---
                StyleUtils.styleTextField(subjectField);
                addLabeledField(formPanel, "Subject:", subjectField, gbc, 2);

                // --- Message Body ---
                messageArea.setLineWrap(true);
                messageArea.setWrapStyleWord(true);
                messageArea.setFont(StyleUtils.FIELD_FONT);
                messageArea.setBackground(new Color(66, 66, 66));
                messageArea.setForeground(StyleUtils.TEXT_COLOR);
                messageArea.setCaretColor(StyleUtils.TEXT_COLOR);
                messageArea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(97, 97, 97)),
                        new EmptyBorder(8, 8, 8, 8)
                ));

                JLabel bodyLabel = new JLabel("Message:");
                StyleUtils.styleLabel(bodyLabel);
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.gridwidth = 1;
                formPanel.add(bodyLabel, gbc);

                gbc.gridx = 1;
                gbc.gridy = 3;
                gbc.gridwidth = 2;
                formPanel.add(new JScrollPane(messageArea), gbc);

                add(formPanel, BorderLayout.CENTER);

                // === Buttons ===
                JButton sendBtn = new JButton("Send");
                JButton cancelBtn = new JButton("Cancel");

                StyleUtils.styleButton(sendBtn, true);
                StyleUtils.styleButton(cancelBtn, false);

                sendBtn.addActionListener(e -> handleSend(currentUser));
                cancelBtn.addActionListener(e -> clearFields());

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setOpaque(false);
                buttonPanel.add(cancelBtn);
                buttonPanel.add(sendBtn);

                add(buttonPanel, BorderLayout.SOUTH);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing SendMessageForm: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the form: " + e.getMessage());
            }
        }

        /**
         * Adds a labeled field to the specified panel with proper alignment.
         *
         * @param panel the panel to add the field to
         * @param labelText the text for the label
         * @param field the text field to add
         * @param gbc the GridBagConstraints for layout
         * @param row the row index for the field
         */
        private void addLabeledField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int row) {
            JLabel label = new JLabel(labelText);
            StyleUtils.styleLabel(label);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            panel.add(label, gbc);

            gbc.gridx = 1;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            panel.add(field, gbc);
        }

        /**
         * Handles the send button action.
         *
         * @param currentUser the current user sending the message
         */
        private void handleSend(User currentUser) {
            try {
                if (validateFields()) {
                    var manager = BankManagerService.getInstance(storageMode).getAllManagers().getFirst();
                    NotificationService.getInstance(storageMode).notifyNewMessage(manager, currentUser.getFullName());
                    StyleUtils.showStyledSuccessDialog(this, "Message sent successfully!");
                    clearFields();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error sending message: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to send message: " + e.getMessage());
            }
        }

        /**
         * Validates the fields in the form.
         *
         * @return true if all fields are valid, false otherwise
         */
        private boolean validateFields() {
            if (toField.getText().trim().isEmpty()) {
                StyleUtils.showStyledErrorDialog(this, "Please enter a recipient email.");
                return false;
            }
            if (subjectField.getText().trim().isEmpty()) {
                StyleUtils.showStyledErrorDialog(this, "Please enter a subject.");
                return false;
            }
            if (messageArea.getText().trim().isEmpty()) {
                StyleUtils.showStyledErrorDialog(this, "Please enter a message.");
                return false;
            }
            return true;
        }

        /**
         * Clears the fields in the form.
         */
        private void clearFields() {
            toField.setText("");
            subjectField.setText("");
            messageArea.setText("");
        }
    }