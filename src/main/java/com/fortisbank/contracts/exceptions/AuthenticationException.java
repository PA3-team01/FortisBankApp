package com.fortisbank.contracts.exceptions;

/**
 * Thrown when authentication fails due to incorrect credentials,
 * invalid input, or unexpected errors during the login process.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
