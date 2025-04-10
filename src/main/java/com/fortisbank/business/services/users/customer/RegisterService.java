package com.fortisbank.business.services.users.customer;

     import com.fortisbank.business.services.account.AccountService;
     import com.fortisbank.business.services.users.manager.BankManagerService;
     import com.fortisbank.data.dal_utils.StorageMode;
     import com.fortisbank.contracts.exceptions.RegistrationFailedException;
     import com.fortisbank.contracts.models.users.BankManager;
     import com.fortisbank.contracts.models.users.Customer;
     import com.fortisbank.business.bll_utils.SecurityUtils;

     import java.util.Arrays;
     import java.util.UUID;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     import static com.fortisbank.contracts.utils.ValidationUtils.*;

     public class RegisterService {

         private static final Logger LOGGER = Logger.getLogger(RegisterService.class.getName());
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

         public boolean registerNewCustomer(String firstName, String lastName, String email, String phoneNumber,
                                            char[] rawPassword, char[] rawPIN) {
             validateNotNull(firstName, "First Name");
             validateNotNull(lastName, "Last Name");
             validateNotNull(email, "Email");
             validateNotNull(phoneNumber, "Phone Number");
             validateNotNull(rawPassword, "Password");
             validateNotNull(rawPIN, "PIN");

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

                 customerService.createCustomer(newCustomer);
                 accountService.createDefaultCheckingAccountFor(newCustomer);

                 LOGGER.log(Level.INFO, "Customer registered successfully: {0}", email);
                 return true;

             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Customer registration failed: {0}", e.getMessage());
                 throw new RegistrationFailedException("Customer registration failed unexpectedly.", e);
             }
         }

         public boolean registerNewBankManager(String firstName, String lastName, String email,
                                               char[] rawPassword, char[] rawPIN) {
             validateNotNull(firstName, "First Name");
             validateNotNull(lastName, "Last Name");
             validateNotNull(email, "Email");
             validateNotNull(rawPassword, "Password");
             validateNotNull(rawPIN, "PIN");

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

                 LOGGER.log(Level.INFO, "Bank Manager registered successfully: {0}", email);
                 return true;

             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Bank Manager registration failed: {0}", e.getMessage());
                 throw new RegistrationFailedException("Bank Manager registration failed unexpectedly.", e);
             }
         }

         private void validateNotNull(Object obj, String fieldName) {
             if (obj == null) {
                 LOGGER.log(Level.SEVERE, "{0} cannot be null", fieldName);
                 throw new IllegalArgumentException(fieldName + " cannot be null");
             }
         }
     }