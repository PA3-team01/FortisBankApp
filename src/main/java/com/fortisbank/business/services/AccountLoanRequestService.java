package com.fortisbank.business.services;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;

import java.util.EnumMap;
import java.util.Map;

/**
 * Handles customer account opening requests and manager decisions.
 */
public class AccountLoanRequestService {

    private static final Map<StorageMode, AccountLoanRequestService> instances = new EnumMap<>(StorageMode.class);
    private NotificationService notificationService;
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
        // Check if the requested account type is valid
//        if (accountService.existsByType(customer.getUserId(), requestedAccount.getType())) {
//            throw new IllegalStateException("You already have a " + requestedAccount.getType() + " account.");
//        }
        // save the requested account to the database
        accountService.createAccount(requestedAccount);
        // add the requested account to the customer's account list
        fillCustomerAccountList(customer);
        // Notify manager and customer with rich notification
        notificationService.notifyAccountRequest(manager, customer, requestedAccount);
    }

    /**
     * Manager accepts the request and account is created.
     */
    public void acceptAccountRequest(Customer customer, Account account) {
        if (customer == null || account == null) {
            return;
        }
        fillCustomerAccountList(customer);
        if (customer.getAccounts().contains(account) && !account.isActive()) {
            account.setActive(true);
            accountService.updateAccount(account);
            CustomerService.getInstance(storageMode).updateCustomer(customer);
            notificationService.notifyApproval(customer, account);
        }
    }

    /**
     * Manager rejects the account request.
     */
    public void rejectAccountRequest(Customer customer, String reason, Account rejectedAccount) {
        if (customer == null || rejectedAccount == null) {
            return;
        }
        fillCustomerAccountList(customer);
        if (customer.getAccounts().contains(rejectedAccount)) {
            customer.getAccounts().remove(rejectedAccount);
            CustomerService.getInstance(storageMode).updateCustomer(customer);
            accountService.deleteAccount(rejectedAccount.getAccountNumber());
            notificationService.notifyRejection(customer, reason, rejectedAccount);
        } else {
            notificationService.notifyRejection(customer, "There was a problem with your request. Please fill a new form.", rejectedAccount);
        }
    }

    /**
     * Utility method to fill the customer's account list.
     */
    private void fillCustomerAccountList(Customer customer) {
        AccountList accounts = accountService.getAccountsByCustomerId(customer.getUserId());
        if (accounts == null) accounts = new AccountList();
        customer.setAccounts(accounts);
    }

}