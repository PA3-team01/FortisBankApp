package com.fortisbank.business.services;
/**
 * LoginService handles authentication of users (both clients and bank managers).
 *
 * Responsibilities:
 * - Validates email and PINHash combination during login.
 * - Retrieves the authenticated Customer or Manager from the repository.
 * - Stores the currently logged-in user in a central session manager.
 * - Provides methods to log in and log out.
 * - Supports future extensibility for MFA (Multi-Factor Authentication) or lockout logic.
 *
 * This service acts as the gateway to access secured features in the UI layer.
 */

public class LoginService {
}
