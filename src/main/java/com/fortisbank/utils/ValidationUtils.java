package com.fortisbank.utils;

import com.fortisbank.exceptions.InvalidTransactionException;

import java.math.BigDecimal;
import java.util.Objects;

public class ValidationUtils {

    //montant (> 0)
    public static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("The transaction amount must be positive.");
        }
    }

    //name
    public static void validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    //verify if null
    public static void validateNotNull(Object obj, String fieldName) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }
    }
}
