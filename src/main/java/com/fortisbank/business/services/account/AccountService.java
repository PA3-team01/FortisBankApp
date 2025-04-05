package com.fortisbank.business.services.account;

import com.fortisbank.business.services.notification.NotificationService;
import com.fortisbank.data.repositories.IAccountRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.accounts.AccountFactory;
import com.fortisbank.models.accounts.AccountType;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.others.NotificationType;
import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 * AccountService manages account-related operations (CRUD) using the selected storage mode.
 * This service is now focused solely on accounts and no longer handles transactions.
 */
public class AccountService implements IAccountService {

    private static final Map<StorageMode, AccountService> instances = new EnumMap<>(StorageMode.class);
    public final IAccountRepository accountRepository;
    private final StorageMode storageMode;

    private AccountService(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.accountRepository = RepositoryFactory.getInstance(storageMode).getAccountRepository();
    }

    public static synchronized AccountService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, AccountService::new);
    }

    /**
     * DO NOT CALL THIS METHOD DIRECTLY FROM THE UI.
     *
     * Account creation must follow a proper request-and-approval process via AccountLoanRequestService.
     */
    @Override
    public void createAccount(Account account) {
        if (account == null) throw new IllegalArgumentException("Account cannot be null");
        if (account.getCustomer() == null) throw new IllegalArgumentException("Account must be linked to a customer");
        if (account.getAccountType() == null) throw new IllegalArgumentException("Account type must be specified");
        accountRepository.insertAccount(account);
    }

    @Override
    public void updateAccount(Account account) {
        if (account == null || account.getAccountNumber() == null) {
            throw new IllegalArgumentException("Invalid account provided for update.");
        }
        accountRepository.updateAccount(account);
    }

    @Override
    public void deleteAccount(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID is required for deletion.");
        }
        accountRepository.deleteAccount(accountId);
    }

    @Override
    public Account getAccount(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID is required.");
        }
        return accountRepository.getAccountById(accountId);
    }

    @Override
    public AccountList getAccountsByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        return accountRepository.getAccountsByCustomerId(customerId);
    }

    @Override
    public AccountList getAllAccounts() {
        return accountRepository.getAllAccounts();
    }

    /**
     * Creates and persists a default checking account for a new customer using the factory.
     * The account is initialized with zero balance and marked active by default.
     *
     * @param customer the customer for whom to create the account
     * @return the created Account instance
     */
    public Account createDefaultCheckingAccountFor(Customer customer) {
        if (customer == null || customer.getUserId() == null) {
            throw new IllegalArgumentException("Customer is required to create a default checking account.");
        }

        Account checkingAccount = AccountFactory.createAccount(
                AccountType.CHECKING,
                null, // auto-generate ID
                customer,
                new Date(),
                BigDecimal.ZERO
        );

        createAccount(checkingAccount); // persist it
        return checkingAccount;
    }
    /**
     * Closes the given account if balance is zero. Otherwise, throws an exception.
     *
     * @param account Account to close.
     */
    public void closeAccount(Account account) {
        if (account == null || account.getAccountNumber() == null) {
            throw new IllegalArgumentException("Account is required to close.");
        }

        if (account.getAvailableBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Unable to close account: balance must be zero.");
        }

        account.setActive(false);
        updateAccount(account); // persist status change
        // update user accountList
        Customer customer = account.getCustomer();
        if (customer != null) {
            AccountList accounts = customer.getAccounts();
            if (accounts != null) {
                accounts.remove(account);
            }
        }
        // persist the change
        RepositoryFactory.getInstance(storageMode).getCustomerRepository().updateCustomer(customer);

        System.out.println("Account " + account.getAccountNumber() + " closed successfully.");
    }

    public void autoCloseInactiveCurrencyAccounts() {
        var allAccounts = getAllAccounts();

        NotificationService notificationService = NotificationService.getInstance(storageMode);

        for (Account account : allAccounts) {
            if (account.getAccountType() != AccountType.CURRENCY || !account.isActive()) continue;

            // Get last transaction date or fallback to openedDate
            Date lastActivity = account.getTransactions().getLastActivityDate(); // method from TransactionList
            if (lastActivity == null) {
                lastActivity = account.getOpenedDate();
            }

            LocalDate lastActivityDate = lastActivity.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate oneYearAgo = LocalDate.now().minusYears(1);

            if (lastActivityDate.isBefore(oneYearAgo)) {
                // Conditions met: close account
                account.setActive(false);
                updateAccount(account);

                // Remove from customer's list
                Customer customer = account.getCustomer();
                if (customer != null && customer.getAccounts() != null) {
                    customer.getAccounts().remove(account);
                    RepositoryFactory.getInstance(storageMode).getCustomerRepository().updateCustomer(customer);
                }

                // Optional: notify customer
                notificationService.sendNotification(
                        customer,
                        NotificationType.INFO,
                        "Currency Account Closed",
                        "Your currency account (" + account.getAccountNumber() + ") was automatically closed due to inactivity over 1 year.",
                        customer,
                        account
                );

                System.out.println("Closed inactive currency account: " + account.getAccountNumber());
            }
        }
    }

    public void checkLowBalanceAndNotify() {
        BigDecimal THRESHOLD = new BigDecimal("100.00");
        var allAccounts = getAllAccounts();
        var notificationService = NotificationService.getInstance(storageMode);

        for (Account account : allAccounts) {
            if (!account.isActive()) continue;

            BigDecimal balance = account.getAvailableBalance();
            boolean belowThreshold = balance.compareTo(THRESHOLD) < 0;

            if (belowThreshold && !account.isLowBalanceAlertSent()) {
                // Send notification
                notificationService.sendNotification(
                        account.getCustomer(),
                        NotificationType.INFO,
                        "Low Balance Warning",
                        String.format("Your account (%s) balance has dropped below $%.2f. Current balance: $%.2f",
                                account.getAccountNumber(), THRESHOLD, balance),
                        account.getCustomer(),
                        account
                );
                account.setLowBalanceAlertSent(true);
                updateAccount(account);
            }

            // Reset flag if balance is restored
            if (!belowThreshold && account.isLowBalanceAlertSent()) {
                account.setLowBalanceAlertSent(false);
                updateAccount(account);
            }
        }
    }


}
