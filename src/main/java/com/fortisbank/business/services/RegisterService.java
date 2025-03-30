package com.fortisbank.business.services;

import com.fortisbank.exceptions.RegistrationFailedException;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.utils.SecurityUtils;

import java.util.UUID;
import java.util.Arrays;

import static com.fortisbank.utils.ValidationUtils.*;

/**
 * RegisterService handles the full registration process for new users.
 *
 * Responsibilities:
 * - Validates unique email, phone number, and CustomerID.
 * - Hashes the provided PIN/password before saving the user.
 * - Creates and links a mandatory checking account during customer signup.
 * - Persists the customer or manager using their respective services.
 * - Notifies or logs the result.
 */
public class RegisterService {

    private final CustomerService customerService;
    private final AccountService accountService;
    private final BankManagerService bankManagerService;

    public RegisterService(CustomerService customerService, AccountService accountService, BankManagerService bankManagerService) {
        this.customerService = customerService;
        this.accountService = accountService;
        this.bankManagerService = bankManagerService;
    }

    public boolean registerNewCustomer(String firstName, String lastName, String email, String phoneNumber,
                                       char[] rawPassword, char[] rawPIN) {
        if (customerService.emailExists(email)) {
            throw new RegistrationFailedException("Email already in use.");
        }
        if (customerService.phoneExists(phoneNumber)) {
            throw new RegistrationFailedException("Phone number already in use.");
        }

        if (!isValidEmail(email)) throw new RegistrationFailedException("Invalid email format.");
        if (!isValidPhone(phoneNumber)) throw new RegistrationFailedException("Invalid phone format.");
        if (!isValidPIN(rawPIN)) throw new RegistrationFailedException("PIN must be exactly 4 digits.");
        if (!isStrongPassword(rawPassword)) throw new RegistrationFailedException("Weak password.");

        try {
            String hashedPassword = SecurityUtils.hashPassword(rawPassword);
            String hashedPIN = SecurityUtils.hashPIN(rawPIN);
            Arrays.fill(rawPassword, '\0');
            Arrays.fill(rawPIN, '\0');

            String customerId = UUID.randomUUID().toString();
            Customer newCustomer = new Customer(customerId, firstName, lastName, hashedPIN, email, phoneNumber);
            newCustomer.setHashedPassword(hashedPassword);

            customerService.createCustomer(newCustomer);
            accountService.createDefaultCheckingAccountFor(newCustomer);

            System.out.println("Customer registered successfully.");
            return true;

        } catch (Exception e) {
            throw new RegistrationFailedException("Customer registration failed unexpectedly.", e);
        }
    }

    public boolean registerNewBankManager(String firstName, String lastName, String email,
                                          char[] rawPassword, char[] rawPIN) {

        if (bankManagerService.emailExists(email)) {
            throw new RegistrationFailedException("Email already in use.");
        }

        if (!isValidEmail(email)) throw new RegistrationFailedException("Invalid email format.");
        if (!isValidPIN(rawPIN)) throw new RegistrationFailedException("PIN must be exactly 4 digits.");
        if (!isStrongPassword(rawPassword)) throw new RegistrationFailedException("Weak password.");

        try {
            String hashedPassword = SecurityUtils.hashPassword(rawPassword);
            String hashedPIN = SecurityUtils.hashPIN(rawPIN);
            Arrays.fill(rawPassword, '\0');
            Arrays.fill(rawPIN, '\0');

            String managerId = UUID.randomUUID().toString();
            BankManager manager = new BankManager(managerId, firstName, lastName, email, hashedPIN);
            manager.setHashedPassword(hashedPassword);

            bankManagerService.createManager(manager);

            System.out.println("Bank Manager registered successfully.");
            return true;

        } catch (Exception e) {
            throw new RegistrationFailedException("Bank Manager registration failed unexpectedly.", e);
        }
    }
}
