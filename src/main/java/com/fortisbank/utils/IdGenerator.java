package com.fortisbank.utils;

import java.util.UUID;

public class IdGenerator {
    // Generates a UUID-based ID
    public static String generateId() {
        return UUID.randomUUID().toString(); // Generates a unique ID
    }
}
