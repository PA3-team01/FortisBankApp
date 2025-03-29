package com.fortisbank.exceptions;

/**
 * Thrown when the registration process fails due to invalid input,
 * duplication, or internal processing errors.
 */
public class RegistrationFailedException extends RuntimeException {

    public RegistrationFailedException(String message) {
        super(message);
    }

    public RegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
