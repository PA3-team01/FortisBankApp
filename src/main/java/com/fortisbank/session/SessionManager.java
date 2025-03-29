package com.fortisbank.session;

import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.users.User;

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

    // ------------------- Session Control -------------------

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    // ------------------- Role Checking -------------------

    public static boolean isCustomer() {
        return currentUser != null && currentUser instanceof Customer;
    }

    public static boolean isManager() {
        return currentUser != null && currentUser instanceof BankManager;
    }

    // ------------------- Typed Access -------------------

    public static Customer getCustomer() {
        return isCustomer() ? (Customer) currentUser : null;
    }

    public static BankManager getManager() {
        return isManager() ? (BankManager) currentUser : null;
    }
}
