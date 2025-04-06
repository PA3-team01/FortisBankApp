package com.fortisbank.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for logging messages at different levels.
 */
public class LoggerUtils {
    private static final Logger LOGGER = Logger.getLogger(LoggerUtils.class.getName());

    /**
     * Logs an informational message.
     *
     * @param message the message to log
     */
    public static void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    /**
     * Logs a warning message.
     *
     * @param message the message to log
     */
    public static void logWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    /**
     * Logs an error message along with an exception.
     *
     * @param message the message to log
     * @param e the exception to log
     */
    public static void logError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }
}