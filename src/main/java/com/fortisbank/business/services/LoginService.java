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

    /**
     * verifier si le format du email et le pin est valide
     * throw AuthenticationException si l'authentification n'est pas valide
     * @param email
     * @param rawPIN
     * @return
     */
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

    /**
     * verifier si le format du email et le password est valide
     * throw AuthenticationException si l'authentification n'est pas valide
     * @param email
     * @param rawPassword
     * @return
     */
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

    /**
     * authentification qui check customers et managers
     * @param email Email
     * @param rawInput Input
     * @param usePIN PIN
     * @return
     * @throws Exception
     */
    private User authenticate(String email, char[] rawInput, boolean usePIN) throws Exception {
        // Try customers
        //check les customers pour un email qui match
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

        boolean verified = usePIN
                ? SecurityUtils.verifyPIN(rawInput, user.getPINHash())
                : SecurityUtils.verifyPassword(rawInput, user.getHashedPassword());

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
