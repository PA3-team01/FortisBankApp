package com.fortisbank.business.services;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.others.NotificationType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Handles customer account opening requests and manager decisions.
 */
public class AccountLoanRequestService {

    private static final Map<StorageMode, AccountLoanRequestService> instances = new EnumMap<>(StorageMode.class);
    private final NotificationService notificationService = NotificationService.getInstance();
    private final AccountService accountService;

    private AccountLoanRequestService(StorageMode storageMode) {
        this.accountService = AccountService.getInstance(storageMode);
    }

    public static synchronized AccountLoanRequestService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, AccountLoanRequestService::new);
    }

    /**
     * Customer submits a request for a new account.
     */
    public void submitAccountRequest(Customer customer, Account requestedAccount, BankManager manager) {
        if (customer == null || requestedAccount == null || manager == null) return;

        requestedAccount.setActive(false); // Will only be activated if accepted

        // Notify manager
        notificationService.sendNotification(manager,
                NotificationType.ACCOUNT_OPENING_REQUEST,
                "New Account Opening Request",
                customer.getFullName() + " requested a " + requestedAccount.getAccountType() + " account.");

        // Notify customer
        notificationService.sendNotification(customer,
                NotificationType.INFO,
                "Request Sent",
                "Your account request was sent to the manager.");
    }

    /**
     * Manager accepts the request and account is created.
     */
    public void acceptAccountRequest(Customer customer, Account account) {
        if (customer == null || account == null) return;

        account.setActive(true);
        accountService.createAccount(account);
        customer.getAccounts().add(account);

        notificationService.sendNotification(customer,
                NotificationType.ACCOUNT_APPROVAL,
                "Account Approved",
                "Your request for a " + account.getAccountType() + " account has been approved.");
    }

    /**
     * Manager rejects the account request.
     */
    public void rejectAccountRequest(Customer customer, String reason) {
        if (customer == null) return;

        notificationService.sendNotification(customer,
                NotificationType.ACCOUNT_REJECTION,
                "Account Request Denied",
                "Your account request was declined. Reason: " + reason);
    }
}
