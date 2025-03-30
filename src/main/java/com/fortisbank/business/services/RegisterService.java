package com.fortisbank.business.services;

import com.fortisbank.exceptions.RegistrationFailedException;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.utils.SecurityUtils;

import java.util.UUID;
import java.util.Arrays;

import static com.fortisbank.utils.ValidationUtils.*;

public class RegisterService {

    private static final java.util.Map<StorageMode, RegisterService> instances = new java.util.EnumMap<>(StorageMode.class);

    private final CustomerService customerService;
    private final AccountService accountService;
    private final BankManagerService bankManagerService;

    private RegisterService(StorageMode mode) {
        this.customerService = CustomerService.getInstance(mode);
        this.accountService = AccountService.getInstance(mode);
        this.bankManagerService = BankManagerService.getInstance(mode);
    }

    public static synchronized RegisterService getInstance(StorageMode mode) {
        return instances.computeIfAbsent(mode, RegisterService::new);
    }

    // ---------------- CUSTOMER REGISTRATION ----------------

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
            Customer newCustomer = new Customer(customerId, firstName, lastName, email, phoneNumber, hashedPassword, hashedPIN);
            newCustomer.setHashedPassword(hashedPassword);

            customerService.createCustomer(newCustomer);
            accountService.createDefaultCheckingAccountFor(newCustomer);

            System.out.println("Customer registered successfully.");
            return true;

        } catch (Exception e) {
            throw new RegistrationFailedException("Customer registration failed unexpectedly.", e);
        }
    }

    // ---------------- MANAGER REGISTRATION ----------------

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

            BankManager manager = new BankManager(
                    firstName,
                    lastName,
                    email,
                    hashedPassword,
                    hashedPIN
            );

            bankManagerService.createManager(manager);

            System.out.println("Bank Manager registered successfully.");
            return true;

        } catch (Exception e) {
            throw new RegistrationFailedException("Bank Manager registration failed unexpectedly.", e);
        }
    }
}
