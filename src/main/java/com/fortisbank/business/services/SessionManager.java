package com.fortisbank.business.services;

/**
 * SessionManager maintains the current session state of the application.
 *
 * Responsibilities:
 * - Stores the currently authenticated user (Customer or Manager).
 * - Tracks login/logout state.
 * - Exposes helper methods for:
 *   • Checking current user role (isClient(), isManager())
 *   • Retrieving logged-in user data.
 * - Ensures consistent access control across the UI and services.
 *
 * This component is essential for managing role-based access in the frontend layer.
 */

public class SessionManager {
}
