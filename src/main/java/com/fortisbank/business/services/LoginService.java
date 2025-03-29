package com.fortisbank.business.services;

import com.fortisbank.exceptions.AuthenticationException;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.User;
import com.fortisbank.session.SessionManager;
import com.fortisbank.utils.SecurityUtils;

import java.util.Arrays;

import static com.fortisbank.utils.ValidationUtils.*;

public class LoginService { //TODO: Implement login attempt failures and account lockout

    private final CustomerService customerService;
    private final BankManagerService managerService;

    public LoginService(CustomerService customerService, BankManagerService managerService) {
        this.customerService = customerService;
        this.managerService = managerService;
    }

    // ---------------- LOGIN WITH PIN ----------------

    public User loginWithPIN(String email, char[] rawPIN) {
        try {
            if (!isValidEmail(email)) throw new AuthenticationException("Invalid email format.");
            if (!isValidPIN(rawPIN)) throw new AuthenticationException("Invalid PIN format.");

            User user = authenticateByEmail(email, rawPIN, true);
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
            if (!isStrongPassword(rawPassword)) throw new AuthenticationException("Invalid password format.");

            User user = authenticateByEmail(email, rawPassword, false);
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

    // ---------------- LOGOUT ----------------

    public void logout() {
        SessionManager.clear();
    }

    // ---------------- COMMON AUTH LOGIC ----------------

    private <T extends User> T authenticateByEmail(String email, char[] rawInput, boolean usePIN) throws Exception {
        for (Customer customer : customerService.getAllCustomers()) {
            T authenticated = tryAuthenticate((T) customer, email, rawInput, usePIN);
            if (authenticated != null) return authenticated;
        }

        for (BankManager manager : managerService.getAllManagers()) {
            T authenticated = tryAuthenticate((T) manager, email, rawInput, usePIN);
            if (authenticated != null) return authenticated;
        }

        return null;
    }

    private <T extends User> T tryAuthenticate(T user, String email, char[] rawInput, boolean usePIN) throws Exception {
        if (!user.getEmail().equalsIgnoreCase(email)) return null;

        boolean verified = usePIN
                ? SecurityUtils.verifyPIN(rawInput, user.getPINHash())
                : SecurityUtils.verifyPassword(rawInput, user.getHashedPassword());

        if (!verified)
            throw new AuthenticationException("Incorrect " + (usePIN ? "PIN." : "password."));

        SessionManager.setCurrentUser(user);
        return user;
    }
}
