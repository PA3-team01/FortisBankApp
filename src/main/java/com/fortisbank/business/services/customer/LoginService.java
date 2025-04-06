package com.fortisbank.business.services.customer;

    import com.fortisbank.business.services.manager.BankManagerService;
    import com.fortisbank.exceptions.AuthenticationException;
    import com.fortisbank.models.users.BankManager;
    import com.fortisbank.models.users.Customer;
    import com.fortisbank.models.users.User;
    import com.fortisbank.session.SessionManager;
    import com.fortisbank.utils.SecurityUtils;

    import java.util.Arrays;

    import static com.fortisbank.utils.ValidationUtils.isValidEmail;
    import static com.fortisbank.utils.ValidationUtils.isValidPIN;

    /**
     * Service class responsible for handling login operations for customers and bank managers.
     */
    public class LoginService {

        private static LoginService instance;

        private final CustomerService customerService;
        private final BankManagerService managerService;

        /**
         * Private constructor to prevent instantiation.
         *
         * @param customerService the customer service
         * @param managerService the bank manager service
         */
        private LoginService(CustomerService customerService, BankManagerService managerService) {
            this.customerService = customerService;
            this.managerService = managerService;
        }

        /**
         * Returns the singleton instance of LoginService.
         *
         * @param customerService the customer service
         * @param managerService the bank manager service
         * @return the singleton instance of LoginService
         */
        public static synchronized LoginService getInstance(CustomerService customerService, BankManagerService managerService) {
            if (instance == null) {
                instance = new LoginService(customerService, managerService);
            }
            return instance;
        }

        // ---------------- LOGIN WITH PIN ----------------

        /**
         * Logs in a user using their email and PIN.
         *
         * @param email the email of the user
         * @param rawPIN the raw PIN of the user
         * @return the authenticated user
         * @throws AuthenticationException if the authentication fails
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
         * Logs in a user using their email and password.
         *
         * @param email the email of the user
         * @param rawPassword the raw password of the user
         * @return the authenticated user
         * @throws AuthenticationException if the authentication fails
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
         * Authenticates a user by checking both customers and managers.
         *
         * @param email the email of the user
         * @param rawInput the raw input (PIN or password) of the user
         * @param usePIN true if using PIN for authentication, false if using password
         * @return the authenticated user
         * @throws Exception if an error occurs during authentication
         */
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

        /**
         * Authenticates a user by verifying their PIN or password.
         *
         * @param user the user to be authenticated
         * @param rawInput the raw input (PIN or password) of the user
         * @param usePIN true if using PIN for authentication, false if using password
         * @return the authenticated user
         * @throws Exception if an error occurs during authentication
         */
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

        /**
         * Logs out the current user.
         */
        public void logout() {
            SessionManager.clear();
        }
    }