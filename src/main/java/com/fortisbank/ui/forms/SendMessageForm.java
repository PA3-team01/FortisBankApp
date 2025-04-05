package com.fortisbank.ui.forms;

import com.fortisbank.business.services.manager.BankManagerService;
import com.fortisbank.business.services.notification.NotificationService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.users.User;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
//TODO: add dropdown for manager selection
public class SendMessageForm extends JPanel {

    private final JTextField fromField = new JTextField();
    private final JTextField toField = new JTextField();
    private final JTextField subjectField = new JTextField();
    private final JTextArea messageArea = new JTextArea(8, 30);
    private final StorageMode storageMode;

    public SendMessageForm(StorageMode storageMode) {
        this.storageMode = storageMode;
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // === Header ===
        JLabel title = new JLabel("Send a Message");
        StyleUtils.styleFormTitle(title);
        add(title, BorderLayout.NORTH);

        // === Center form area ===
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // --- From Field ---
        User currentUser = SessionManager.getCurrentUser();
        fromField.setText(currentUser != null ? currentUser.getEmail() : "unknown@fortisbank.com");
        fromField.setEditable(false);
        StyleUtils.styleTextField(fromField);
        addLabeledField(formPanel, "From:", fromField);

        // --- To Field ---
        StyleUtils.styleTextField(toField);
        addLabeledField(formPanel, "To:", toField);

        // --- Subject Field ---
        StyleUtils.styleTextField(subjectField);
        addLabeledField(formPanel, "Subject:", subjectField);

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
        formPanel.add(bodyLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(new JScrollPane(messageArea));
        formPanel.add(Box.createVerticalStrut(10));

        add(formPanel, BorderLayout.CENTER);

        // === Buttons ===
        JButton sendBtn = new JButton("Send");
        JButton cancelBtn = new JButton("Cancel");

        StyleUtils.styleButton(sendBtn, true);
        StyleUtils.styleButton(cancelBtn, false);

        sendBtn.addActionListener(e -> {
            // get first manager from the list
            var manager = BankManagerService.getInstance(storageMode).getAllManagers().getFirst();
            if (validateFields()) {
                // TODO: add notification sending logic here
                // new instance of NotificationService
                NotificationService.getInstance(storageMode).notifyNewMessage(manager, currentUser.getFullName());
                StyleUtils.showStyledSuccessDialog(this, "Message sent successfully!");
                clearFields();
            }
        });

        cancelBtn.addActionListener(e -> clearFields());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(sendBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addLabeledField(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        StyleUtils.styleLabel(label);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }

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

    private void clearFields() {
        toField.setText("");
        subjectField.setText("");
        messageArea.setText("");
    }
}
