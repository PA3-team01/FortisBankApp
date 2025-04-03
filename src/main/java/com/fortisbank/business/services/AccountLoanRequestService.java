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
    private final StorageMode storageMode;

    private AccountLoanRequestService(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.accountService = AccountService.getInstance(storageMode);
        this.notificationService = NotificationService.getInstance(storageMode);
    }

    public static synchronized AccountLoanRequestService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, AccountLoanRequestService::new);
    }

    /**
     * Customer submits a request for a new account.
     */
    public void submitAccountRequest(Customer customer, Account requestedAccount, BankManager manager) {
        if (customer == null || requestedAccount == null || manager == null) return;
        //TODO: check if the customer already has an account of this type


        // save the requested account to the database
        accountService.createAccount(requestedAccount);
        // Notify manager and customer with rich notification
        notificationService.notifyAccountRequest(manager, customer, requestedAccount);


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

        // remove the account from the customer's accounts
        if (customer.getAccounts().contains(rejectedAccount)) {
            customer.getAccounts().remove(rejectedAccount);
        }
        //update the customer in the database
        CustomerService.getInstance(storageMode).updateCustomer(customer);
        // remove the account from the database
        accountService.deleteAccount(rejectedAccount.getAccountNumber());
        // Notify customer with rich notification
        notificationService.notifyRejection(customer, reason, rejectedAccount);
    }
}
