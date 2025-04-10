package com.fortisbank.business.services.account;

    import com.fortisbank.business.services.notification.NotificationService;
    import com.fortisbank.contracts.exceptions.AccountRepositoryException;
    import com.fortisbank.data.interfaces.IAccountRepository;
    import com.fortisbank.data.dal_utils.RepositoryFactory;
    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.contracts.models.accounts.Account;
    import com.fortisbank.contracts.models.accounts.AccountFactory;
    import com.fortisbank.contracts.models.accounts.AccountType;
    import com.fortisbank.contracts.collections.AccountList;
    import com.fortisbank.contracts.models.others.NotificationType;
    import com.fortisbank.contracts.models.users.Customer;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.ZoneId;
    import java.util.Date;
    import java.util.EnumMap;
    import java.util.Map;
    import java.util.Objects;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * AccountService manages account-related operations (CRUD) using the selected storage mode.
     * This service is now focused solely on accounts and no longer handles transactions.
     */
    public class AccountService implements IAccountService {

        private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());
        private static final Map<StorageMode, AccountService> instances = new EnumMap<>(StorageMode.class);
        private static final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal("100.00");

        private final IAccountRepository accountRepository;
        private final StorageMode storageMode;

        private AccountService(StorageMode storageMode) {
            this.storageMode = storageMode;
            this.accountRepository = RepositoryFactory.getInstance(storageMode).getAccountRepository();
        }

        public static synchronized AccountService getInstance(StorageMode storageMode) {
            return instances.computeIfAbsent(storageMode, AccountService::new);
        }

        @Override
        public void createAccount(Account account) {
            validateAccount(account);
            try {
                accountRepository.insertAccount(account);
            } catch (AccountRepositoryException e) {
                LOGGER.log(Level.SEVERE, "Error creating account: {0}", e.getMessage());
                throw new RuntimeException("Failed to create account", e);
            }
        }

        @Override
        public void updateAccount(Account account) {
            if (account == null || account.getAccountNumber() == null) {
                throw new IllegalArgumentException("Invalid account provided for update.");
            }
            try {
                accountRepository.updateAccount(account);
            } catch (AccountRepositoryException e) {
                LOGGER.log(Level.SEVERE, "Error updating account: {0}", e.getMessage());
                throw new RuntimeException("Failed to update account", e);
            }
        }

        @Override
        public void deleteAccount(String accountId) {
            validateId(accountId, "Account ID is required for deletion.");
            try {
                accountRepository.deleteAccount(accountId);
            } catch (AccountRepositoryException e) {
                LOGGER.log(Level.SEVERE, "Error deleting account: {0}", e.getMessage());
                throw new RuntimeException("Failed to delete account", e);
            }
        }

        @Override
        public Account getAccount(String accountId) {
            validateId(accountId, "Account ID is required.");
            try {
                return accountRepository.getAccountById(accountId);
            } catch (AccountRepositoryException e) {
                LOGGER.log(Level.SEVERE, "Error retrieving account: {0}", e.getMessage());
                throw new RuntimeException("Failed to retrieve account", e);
            }
        }

        @Override
        public AccountList getAccountsByCustomerId(String customerId) {
            validateId(customerId, "Customer ID is required.");
            try {
                return accountRepository.getAccountsByCustomerId(customerId);
            } catch (AccountRepositoryException e) {
                LOGGER.log(Level.SEVERE, "Error retrieving accounts by customer ID: {0}", e.getMessage());
                throw new RuntimeException("Failed to retrieve accounts by customer ID", e);
            }
        }

        @Override
        public AccountList getAllAccounts() {
            try {
                return accountRepository.getAllAccounts();
            } catch (AccountRepositoryException e) {
                LOGGER.log(Level.SEVERE, "Error retrieving all accounts: {0}", e.getMessage());
                throw new RuntimeException("Failed to retrieve all accounts", e);
            }
        }

        public Account createDefaultCheckingAccountFor(Customer customer) {
            Objects.requireNonNull(customer, "Customer is required to create a default checking account.");
            Objects.requireNonNull(customer.getUserId(), "Customer ID is required.");

            Account checkingAccount = AccountFactory.createAccount(
                    AccountType.CHECKING,
                    null,
                    customer,
                    new Date(),
                    BigDecimal.ZERO
            );

            createAccount(checkingAccount);
            return checkingAccount;
        }

        public void closeAccount(Account account) {
            validateAccountForClosure(account);

            try {
                account.setActive(false);
                updateAccount(account);

                Customer customer = account.getCustomer();
                if (customer != null && customer.getAccounts() != null) {
                    customer.getAccounts().remove(account);
                    RepositoryFactory.getInstance(storageMode).getCustomerRepository().updateCustomer(customer);
                }

                LOGGER.log(Level.INFO, "Account {0} closed successfully.", account.getAccountNumber());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error closing account: {0}", e.getMessage());
                throw new RuntimeException("Failed to close account", e);
            }
        }

        public void autoCloseInactiveCurrencyAccounts() {
            try {
                AccountList allAccounts = getAllAccounts();
                NotificationService notificationService = NotificationService.getInstance(storageMode);

                for (Account account : allAccounts) {
                    if (shouldCloseInactiveCurrencyAccount(account)) {
                        closeInactiveCurrencyAccount(account, notificationService);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error auto-closing inactive currency accounts: {0}", e.getMessage());
                throw new RuntimeException("Failed to auto-close inactive currency accounts", e);
            }
        }

        public void checkLowBalanceAndNotify() {
            try {
                AccountList allAccounts = getAllAccounts();
                NotificationService notificationService = NotificationService.getInstance(storageMode);

                for (Account account : allAccounts) {
                    if (account.isActive()) {
                        handleLowBalanceNotification(account, notificationService);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error checking low balance and notifying: {0}", e.getMessage());
                throw new RuntimeException("Failed to check low balance and notify", e);
            }
        }

        private void validateAccount(Account account) {
            if (account == null) throw new IllegalArgumentException("Account cannot be null.");
            if (account.getCustomer() == null) throw new IllegalArgumentException("Account must be linked to a customer.");
            if (account.getAccountType() == null) throw new IllegalArgumentException("Account type must be specified.");
        }

        private void validateId(String id, String errorMessage) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException(errorMessage);
            }
        }

        private void validateAccountForClosure(Account account) {
            if (account == null || account.getAccountNumber() == null) {
                throw new IllegalArgumentException("Account is required to close.");
            }
            if (account.getAvailableBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException("Unable to close account: balance must be zero.");
            }
        }

        private boolean shouldCloseInactiveCurrencyAccount(Account account) {
            if (account.getAccountType() != AccountType.CURRENCY || !account.isActive()) {
                return false;
            }

            Date lastActivity = account.getTransactions().getLastActivityDate();
            if (lastActivity == null) {
                lastActivity = account.getOpenedDate();
            }

            LocalDate lastActivityDate = lastActivity.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate oneYearAgo = LocalDate.now().minusYears(1);

            return lastActivityDate.isBefore(oneYearAgo);
        }

        private void closeInactiveCurrencyAccount(Account account, NotificationService notificationService) {
            try {
                account.setActive(false);
                updateAccount(account);

                Customer customer = account.getCustomer();
                if (customer != null && customer.getAccounts() != null) {
                    customer.getAccounts().remove(account);
                    RepositoryFactory.getInstance(storageMode).getCustomerRepository().updateCustomer(customer);
                }

                notificationService.sendNotification(
                        customer,
                        NotificationType.INFO,
                        "Currency Account Closed",
                        "Your currency account (" + account.getAccountNumber() + ") was automatically closed due to inactivity over 1 year.",
                        customer,
                        account
                );

                LOGGER.log(Level.INFO, "Closed inactive currency account: {0}", account.getAccountNumber());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error closing inactive currency account: {0}", e.getMessage());
                throw new RuntimeException("Failed to close inactive currency account", e);
            }
        }

        private void handleLowBalanceNotification(Account account, NotificationService notificationService) {
            try {
                BigDecimal balance = account.getAvailableBalance();
                boolean belowThreshold = balance.compareTo(LOW_BALANCE_THRESHOLD) < 0;

                if (belowThreshold && !account.isLowBalanceAlertSent()) {
                    notificationService.sendNotification(
                            account.getCustomer(),
                            NotificationType.INFO,
                            "Low Balance Warning",
                            String.format("Your account (%s) balance has dropped below $%.2f. Current balance: $%.2f",
                                    account.getAccountNumber(), LOW_BALANCE_THRESHOLD, balance),
                            account.getCustomer(),
                            account
                    );
                    account.setLowBalanceAlertSent(true);
                    updateAccount(account);
                } else if (!belowThreshold && account.isLowBalanceAlertSent()) {
                    account.setLowBalanceAlertSent(false);
                    updateAccount(account);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error handling low balance notification: {0}", e.getMessage());
                throw new RuntimeException("Failed to handle low balance notification", e);
            }
        }
    }