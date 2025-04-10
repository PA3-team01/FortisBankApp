package com.fortisbank.business.services.session;

import com.fortisbank.contracts.models.users.BankManager;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.models.users.User;

/**
 * SessionManager maintains the current session state of the application.
 *
 * Responsibilities:
 * - Stores the currently authenticated user (Customer or Manager).
 * - Tracks login/logout state.
 * - Exposes helper methods for:
 *   • Checking current user role (isCustomer(), isManager())
 *   • Retrieving logged-in user data.
 * - Ensures consistent access control across the UI and services.
 */
public class SessionManager {

    private static User currentUser;
    // TODO: add saving mode to the session manager (e.g., file, database, etc.)

    // ------------------- Session Control -------------------

    /**
     * Sets the current user for the session.
     *
     * @param user the user to set as the current user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Clears the current session, logging out the user.
     */
    public static void clear() {
        currentUser = null;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Retrieves the current user of the session.
     *
     * @return the current user, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    // ------------------- Role Checking -------------------

    /**
     * Checks if the current user is a customer.
     *
     * @return true if the current user is a customer, false otherwise
     */
    public static boolean isCustomer() {
        return currentUser != null && currentUser instanceof Customer;
    }

    /**
     * Checks if the current user is a bank manager.
     *
     * @return true if the current user is a bank manager, false otherwise
     */
    public static boolean isManager() {
        return currentUser != null && currentUser instanceof BankManager;
    }

    // ------------------- Typed Access -------------------

    /**
     * Retrieves the current user as a customer.
     *
     * @return the current user as a customer, or null if the current user is not a customer
     */
    public static Customer getCustomer() {
        return isCustomer() ? (Customer) currentUser : null;
    }

    /**
     * Retrieves the current user as a bank manager.
     *
     * @return the current user as a bank manager, or null if the current user is not a bank manager
     */
    public static BankManager getManager() {
        return isManager() ? (BankManager) currentUser : null;
    }
}