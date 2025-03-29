package com.fortisbank.business.services;

import com.fortisbank.exceptions.RegistrationFailedException;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.utils.SecurityUtils;

import java.util.UUID;
import java.util.Arrays;

import static com.fortisbank.utils.ValidationUtils.*;

/**
 * RegisterService handles the full registration process for new customers.
 *
 * Responsibilities:
 * - Validates unique email, phone number, and CustomerID.
 * - Hashes the provided PIN/password before saving the customer.
 * - Creates and links a mandatory checking account during signup.
 * - Persists the customer and account using their respective services.
 * - Notifies the customer or logs the action.
 *
 * This class ensures that new customers are registered securely
 * and that the initial account setup complies with business rules.
 */
public class RegisterService {

    private final CustomerService customerService;
    private final AccountService accountService;

    public RegisterService(CustomerService customerService, AccountService accountService) {
        this.customerService = customerService;
        this.accountService = accountService;
    }

    public boolean registerNewCustomer(String firstName, String lastName, String email, String phoneNumber,
                                       char[] rawPassword, char[] rawPIN) {
        // 1. Validate uniqueness
        if (customerService.emailExists(email)) {
            throw new RegistrationFailedException("Email already in use.");
        }
        if (customerService.phoneExists(phoneNumber)) {
            throw new RegistrationFailedException("Phone number already in use.");
        }

        // 2. Validate formats
        if (!isValidEmail(email)) {
            throw new RegistrationFailedException("Invalid email format.");
        }
        if (!isValidPhone(phoneNumber)) {
            throw new RegistrationFailedException("Invalid phone format.");
        }
        if (!isValidPIN(rawPIN)) {
            throw new RegistrationFailedException("PIN must be exactly 4 digits.");
        }
        if (!isStrongPassword(rawPassword)) {
            throw new RegistrationFailedException("Password must be at least 8 characters and include upper, lower, and digit.");
        }

        try {
            // 3. Hash credentials
            String hashedPassword = SecurityUtils.hashPassword(rawPassword);
            String hashedPIN = SecurityUtils.hashPIN(rawPIN);

            // 4. Clear sensitive input
            Arrays.fill(rawPassword, '\0');
            Arrays.fill(rawPIN, '\0');

            // 5. Generate unique ID
            String customerId = UUID.randomUUID().toString();

            // 6. Create customer object
            Customer newCustomer = new Customer(customerId, firstName, lastName, hashedPIN, email, phoneNumber);
            newCustomer.setHashedPassword(hashedPassword);

            // 7. Persist customer
            customerService.createCustomer(newCustomer);

            // 8. Create default account
            Account checkingAccount = accountService.createDefaultCheckingAccountFor(newCustomer);

            // 9. Success
            System.out.println("Customer registered successfully.");
            return true;

        } catch (Exception e) {
            throw new RegistrationFailedException("Registration process failed unexpectedly.", e);
        }
    }
}
