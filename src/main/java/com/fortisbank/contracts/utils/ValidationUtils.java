package com.fortisbank.contracts.utils;

import com.fortisbank.contracts.exceptions.InvalidTransactionException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
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

    /**
     * Validates that the given amount is positive.
     *
     * @param amount the amount to validate
     * @throws InvalidTransactionException if the amount is null or not positive
     */
    public static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("The transaction amount must be positive.");
        }
    }

    /**
     * Validates that the given string is not null or empty.
     *
     * @param value the string to validate
     * @param fieldName the name of the field being validated
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    /**
     * Validates that the given object is not null.
     *
     * @param obj the object to validate
     * @param fieldName the name of the field being validated
     * @throws IllegalArgumentException if the object is null
     */
    public static void validateNotNull(Object obj, String fieldName) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }
    }

    /**
     * Validates that the given string is a valid UUID.
     *
     * @param uuid the string to validate
     * @param fieldName the name of the field being validated
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static void validateUUIDFormat(String uuid, String fieldName) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid UUID.");
        }
    }

    // -------------------- Input Format Validations --------------------

    /**
     * Checks if the given email is valid.
     *
     * @param email the email to check
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks if the given phone number is valid.
     *
     * @param phone the phone number to check
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Checks if the given PIN is valid.
     *
     * @param pin the PIN to check
     * @return true if the PIN is valid, false otherwise
     */
    public static boolean isValidPIN(char[] pin) {
        return pin != null && pin.length == 4 && allDigits(pin);
    }

    /**
     * Checks if the given password is strong.
     *
     * @param password the password to check
     * @return true if the password is strong, false otherwise
     */
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

    /**
     * Checks if all characters in the given array are digits.
     *
     * @param input the array to check
     * @return true if all characters are digits, false otherwise
     */
    private static boolean allDigits(char[] input) {
        for (char c : input) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    // -------------------- Swing Field Utilities --------------------

    /**
     * Restricts the input of the given JTextField to digits only, with a maximum length.
     *
     * @param field the JTextField to restrict
     * @param maxLength the maximum length of the input
     */
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

    /**
     * Attaches real-time validation to the given JTextField.
     *
     * @param field the JTextField to validate
     * @param statusLabel the JLabel to display validation status
     * @param validator the validation function
     * @param successText the text to display on successful validation
     * @param errorText the text to display on validation failure
     */
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

    /**
     * Attaches real-time validation to the given JPasswordField.
     *
     * @param field the JPasswordField to validate
     * @param statusLabel the JLabel to display validation status
     * @param validator the validation function
     * @param successText the text to display on successful validation
     * @param errorText the text to display on validation failure
     */
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

    public static LocalDate toLocalDate(Date transactionDate) {
        // Convert Date to LocalDate
        if (transactionDate == null) {
            return null;
        }
        return new java.sql.Date(transactionDate.getTime()).toLocalDate();
    }

    public static Date toDate(LocalDate transactionDate) {
        // Convert LocalDate to Date
        if (transactionDate == null) {
            return null;
        }
        return java.sql.Date.valueOf(transactionDate);

    }
}