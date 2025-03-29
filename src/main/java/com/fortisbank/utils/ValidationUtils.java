package com.fortisbank.utils;

import com.fortisbank.exceptions.InvalidTransactionException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for centralized validation logic used across the application.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

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
}
