package com.fortisbank.business.services.users.customer;

     import com.fortisbank.business.services.users.manager.BankManagerService;
     import com.fortisbank.contracts.exceptions.AuthenticationException;
     import com.fortisbank.contracts.models.users.BankManager;
     import com.fortisbank.contracts.models.users.Customer;
     import com.fortisbank.contracts.models.users.User;
     import com.fortisbank.business.services.session.SessionManager;
     import com.fortisbank.business.bll_utils.SecurityUtils;

     import java.util.Arrays;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     import static com.fortisbank.contracts.utils.ValidationUtils.isValidEmail;
     import static com.fortisbank.contracts.utils.ValidationUtils.isValidPIN;

     /**
      * Service class responsible for handling login operations for customers and bank managers.
      */
     public class LoginService {

         private static final Logger LOGGER = Logger.getLogger(LoginService.class.getName());
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

         public User loginWithPIN(String email, char[] rawPIN) {
             validateNotNull(email, "Email");
             validateNotNull(rawPIN, "PIN");
             try {
                 if (!isValidEmail(email)) throw new AuthenticationException("Invalid email format.");
                 if (!isValidPIN(rawPIN)) throw new AuthenticationException("Invalid PIN format.");

                 User user = authenticate(email, rawPIN, true);
                 if (user != null) return user;

                 throw new AuthenticationException("No user found with the provided email.");
             } catch (AuthenticationException ae) {
                 LOGGER.log(Level.WARNING, "Authentication failed: {0}", ae.getMessage());
                 throw ae;
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Unexpected error during login with PIN: {0}", e.getMessage());
                 throw new AuthenticationException("Login failed unexpectedly.", e);
             } finally {
                 Arrays.fill(rawPIN, '\0');
             }
         }

         public User loginWithPassword(String email, char[] rawPassword) {
             validateNotNull(email, "Email");
             validateNotNull(rawPassword, "Password");
             try {
                 if (!isValidEmail(email)) throw new AuthenticationException("Invalid email format.");
                 if (rawPassword.length == 0) throw new AuthenticationException("Password cannot be empty.");

                 User user = authenticate(email, rawPassword, false);
                 if (user != null) return user;

                 throw new AuthenticationException("No user found with the provided email.");
             } catch (AuthenticationException ae) {
                 LOGGER.log(Level.WARNING, "Authentication failed: {0}", ae.getMessage());
                 throw ae;
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Unexpected error during login with password: {0}", e.getMessage());
                 throw new AuthenticationException("Login failed unexpectedly.", e);
             } finally {
                 Arrays.fill(rawPassword, '\0');
             }
         }

         private User authenticate(String email, char[] rawInput, boolean usePIN) throws Exception {
             try {
                 for (Customer customer : customerService.getAllCustomers()) {
                     if (email.equalsIgnoreCase(customer.getEmail())) {
                         return authenticateUser(customer, rawInput, usePIN);
                     }
                 }

                 for (BankManager manager : managerService.getAllManagers()) {
                     if (email.equalsIgnoreCase(manager.getEmail())) {
                         return authenticateUser(manager, rawInput, usePIN);
                     }
                 }

                 return null;
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error during authentication: {0}", e.getMessage());
                 throw e;
             }
         }

         private User authenticateUser(User user, char[] rawInput, boolean usePIN) throws Exception {
             try {
                 boolean verified = usePIN
                         ? SecurityUtils.verifyPIN(rawInput, user.getPINHash())
                         : SecurityUtils.verifyPassword(rawInput, user.getHashedPassword());

                 if (!verified) {
                     throw new AuthenticationException("Incorrect " + (usePIN ? "PIN." : "password."));
                 }

                 SessionManager.setCurrentUser(user);
                 LOGGER.log(Level.INFO, "User authenticated successfully: {0}", user.getEmail());
                 return user;
             } catch (AuthenticationException ae) {
                 LOGGER.log(Level.WARNING, "Authentication failed for user: {0}", user.getEmail());
                 throw ae;
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Unexpected error during user authentication: {0}", e.getMessage());
                 throw e;
             }
         }

         public void logout() {
             try {
                 SessionManager.clear();
                 LOGGER.log(Level.INFO, "User logged out successfully.");
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error during logout: {0}", e.getMessage());
                 throw new RuntimeException("Logout failed.", e);
             }
         }

         private void validateNotNull(Object obj, String fieldName) {
             if (obj == null) {
                 LOGGER.log(Level.SEVERE, "{0} cannot be null", fieldName);
                 throw new IllegalArgumentException(fieldName + " cannot be null");
             }
         }
     }