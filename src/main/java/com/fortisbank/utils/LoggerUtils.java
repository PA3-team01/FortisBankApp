package com.fortisbank.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtils {
    private static final Logger LOGGER = Logger.getLogger(LoggerUtils.class.getName());

    public static void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public static void logWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public static void logError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }
}
