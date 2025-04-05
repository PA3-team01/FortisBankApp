package com.fortisbank.utils;

import com.fortisbank.exceptions.InvalidTransactionException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for centralized validation logic used across the application,
 * and for Swing field input filtering and live validation feedback.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- .]?\\d{3}[- .]?\\d{4}$"
    );
    /**
    Supported phone Formats:
    4185200999
    418-520-0999
    (418) 520-0999
    +1 418 520 0999
    +14185200999
    418.520.0999
    */

    // -------------------- General Validations --------------------

    public static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("The transaction amount must be positive.");
        }
    }

    public static void validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    public static void validateNotNull(Object obj, String fieldName) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }
    }

    public static void validateUUIDFormat(String uuid, String fieldName) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid UUID.");
        }
    }

    // -------------------- Input Format Validations --------------------

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidPIN(char[] pin) {
        return pin != null && pin.length == 4 && allDigits(pin);
    }

    public static boolean isStrongPassword(char[] password) {
        if (password == null || password.length < 8) return false;

        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : password) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;

            if (hasUpper && hasLower && hasDigit) return true;
        }

        return false;
    }

    private static boolean allDigits(char[] input) {
        for (char c : input) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    // -------------------- Swing Field Utilities --------------------

    public static void restrictToDigits(JTextField field, int maxLength) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (isNumeric(string) && (fb.getDocument().getLength() + string.length() <= maxLength)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (isNumeric(text) && (fb.getDocument().getLength() - length + text.length() <= maxLength)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean isNumeric(String text) {
                return text.chars().allMatch(Character::isDigit);
            }
        });
    }

    public static void attachRealTimeValidation(JTextField field, JLabel statusLabel, java.util.function.Predicate<String> validator, String successText, String errorText) {
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                String text = field.getText();
                if (validator.test(text)) {
                    statusLabel.setText(successText);
                    statusLabel.setForeground(Color.GREEN.darker());
                } else {
                    statusLabel.setText(errorText);
                    statusLabel.setForeground(Color.RED.darker());
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
    }

    public static void attachRealTimeValidation(JPasswordField field, JLabel statusLabel, java.util.function.Predicate<char[]> validator, String successText, String errorText) {
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                char[] input = field.getPassword();
                if (validator.test(input)) {
                    statusLabel.setText(successText);
                    statusLabel.setForeground(Color.GREEN.darker());
                } else {
                    statusLabel.setText(errorText);
                    statusLabel.setForeground(Color.RED.darker());
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
    }
}

