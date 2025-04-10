package com.fortisbank.contracts.utils;

import java.util.UUID;

/**
 * Utility class for generating unique IDs.
 */
public class IdGenerator {

    /**
     * Generates a UUID-based ID.
     *
     * @return a unique ID as a string
     */
    public static String generateId() {
        return UUID.randomUUID().toString(); // Generates a unique ID
    }
}