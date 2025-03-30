package com.fortisbank.business.services;

import com.fortisbank.exceptions.AuthenticationException;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.users.User;
import com.fortisbank.session.SessionManager;
import com.fortisbank.utils.SecurityUtils;

import java.util.Arrays;

import static com.fortisbank.utils.ValidationUtils.*;

public class LoginService {

    private static LoginService instance;

    private final CustomerService customerService;
    private final BankManagerService managerService;

    private LoginService(CustomerService customerService, BankManagerService managerService) {
        this.customerService = customerService;
        this.managerService = managerService;
    }

    public static synchronized LoginService getInstance(CustomerService customerService, BankManagerService managerService) {
        if (instance == null) {
            instance = new LoginService(customerService, managerService);
        }
        return instance;
    }

    // ---------------- LOGIN WITH PIN ----------------

    public User loginWithPIN(String email, char[] rawPIN) {
        try {
            if (!isValidEmail(email)) throw new AuthenticationException("Invalid email format.");
            if (!isValidPIN(rawPIN)) throw new AuthenticationException("Invalid PIN format.");

            User user = authenticate(email, rawPIN, true);
            if (user != null) return user;

            throw new AuthenticationException("No user found with the provided email.");
        } catch (AuthenticationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AuthenticationException("Login failed unexpectedly.", e);
        } finally {
            Arrays.fill(rawPIN, '\0');
        }
    }

    // ---------------- LOGIN WITH PASSWORD ----------------

    public User loginWithPassword(String email, char[] rawPassword) {
        try {
            if (!isValidEmail(email)) throw new AuthenticationException("Invalid email format.");
            if (rawPassword == null || rawPassword.length == 0)
                throw new AuthenticationException("Password cannot be empty.");

            User user = authenticate(email, rawPassword, false);
            if (user != null) return user;

            throw new AuthenticationException("No user found with the provided email.");
        } catch (AuthenticationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AuthenticationException("Login failed unexpectedly.", e);
        } finally {
            Arrays.fill(rawPassword, '\0');
        }
    }

    // ---------------- COMMON AUTH ----------------

    private User authenticate(String email, char[] rawInput, boolean usePIN) throws Exception {
        // Try customers
        for (Customer customer : customerService.getAllCustomers()) {
            if (email.equalsIgnoreCase(customer.getEmail())) {
                return authenticateUser(customer, rawInput, usePIN);
            }
        }

        // Try managers
        for (BankManager manager : managerService.getAllManagers()) {
            if (email.equalsIgnoreCase(manager.getEmail())) {
                return authenticateUser(manager, rawInput, usePIN);
            }
        }

        return null;
    }

    private User authenticateUser(User user, char[] rawInput, boolean usePIN) throws Exception {
        System.out.println("Trying login for: " + user.getEmail());
        System.out.println("User type: " + user.getClass().getSimpleName());
        System.out.println("Expected " + (usePIN ? "PIN hash: " : "Password hash: ") +
                (usePIN ? user.getPINHash() : user.getHashedPassword()));
        System.out.println("Raw input: " + Arrays.toString(rawInput));

        boolean verified = usePIN
                ? SecurityUtils.verifyPIN(rawInput, user.getPINHash())
                : SecurityUtils.verifyPassword(rawInput, user.getHashedPassword());

        System.out.println("Verification result: " + verified);

        if (!verified) {
            throw new AuthenticationException("Incorrect " + (usePIN ? "PIN." : "password."));
        }

        SessionManager.setCurrentUser(user);
        return user;
    }

    public void logout() {
        SessionManager.clear();
    }
}
