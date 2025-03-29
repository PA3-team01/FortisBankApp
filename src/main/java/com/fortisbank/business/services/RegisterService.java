package com.fortisbank.business.services;

/**
 * RegisterService handles the full registration process for new customers.
 *
 * Responsibilities:
 * - Validates unique email, phone number, and CustomerID.
 * - Hashes the provided PIN before saving the customer.
 * - Creates and links a mandatory checking account during signup.
 * - Persists the customer and account using their respective services.
 * - Notifies the customer or logs the action.
 *
 * This class ensures that new customers are registered securely
 * and that the initial account setup complies with business rules.
 */

public class RegisterService {
}
