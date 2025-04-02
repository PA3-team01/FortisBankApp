package com.fortisbank.business.services;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;

import java.util.EnumMap;
import java.util.Map;

/**
 * Handles customer account opening requests and manager decisions.
 */
public class AccountLoanRequestService {

    private static final Map<StorageMode, AccountLoanRequestService> instances = new EnumMap<>(StorageMode.class);
    private  NotificationService notificationService;
    private final AccountService accountService;

    private AccountLoanRequestService(StorageMode storageMode) {
        this.accountService = AccountService.getInstance(storageMode);
        this.notificationService = NotificationService.getInstance(storageMode);
        // debug
        System.out.println("AccountLoanRequestService initialized with storage mode: " + storageMode);
    }

    public static synchronized AccountLoanRequestService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, AccountLoanRequestService::new);
    }

    /**
     * Customer submits a request for a new account.
     */
    public void submitAccountRequest(Customer customer, Account requestedAccount, BankManager manager) {
        //debug check recieved parameters
        System.out.println("[AccountLoanRequestService]Account request submitted:");
        System.out.println("Customer: " + customer);
        System.out.println("Requested Account: " + requestedAccount);
        System.out.println("Manager: " + manager);
        if (customer == null || requestedAccount == null || manager == null) return;
        //debug
        System.out.println("[AccountLoanRequestService]Account request passed null check");
        // save the requested account to the database
        accountService.createAccount(requestedAccount);

        //debug
        System.out.println("[AccountLoanRequestService]Account request passed account creation");

        // Notify manager and customer with rich notification
        notificationService.notifyAccountRequest(manager, customer, requestedAccount);

        //debug
        System.out.println("[AccountLoanRequestService]Account request passed notification");
    }

    /**
     * Manager accepts the request and account is created.
     */
    public void acceptAccountRequest(Customer customer, Account account) {
        if (customer == null || account == null) return;
        if (customer.getAccounts().contains(account) && !account.isActive()) {
            account.setActive(true);
        } else return;

        notificationService.notifyApproval(customer, account);
    }

    /**
     * Manager rejects the account request.
     */
    public void rejectAccountRequest(Customer customer, String reason, Account rejectedAccount) {
        if (customer == null) return;

        notificationService.notifyRejection(customer, reason, rejectedAccount);
    }
}
