package com.fortisbank.models.users;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.TransactionList;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class Customer extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String phoneNumber;

    // Not serialized; loaded dynamically at runtime
    private transient AccountList accounts;

    public Customer() {
        super();
        this.role = Role.CUSTOMER;
    }

    public Customer(String userId, String firstName, String lastName, String email,
                    String hashedPassword, String pinHash, String phoneNumber) {
        super(userId, firstName, lastName, email, hashedPassword, pinHash, Role.CUSTOMER);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AccountList getAccounts() {
        return accounts;
    }

    public void setAccounts(AccountList accounts) {
        this.accounts = accounts;
    }

    public BigDecimal getBalance() {
        BigDecimal totalBalance = BigDecimal.ZERO;
        if (accounts != null) {
            for (Account account : accounts) {
                totalBalance = totalBalance.add(account.getAvailableBalance());
            }
        }
        return totalBalance;
    }

    public TransactionList getTransactions() {
        TransactionList all = new TransactionList();
        if (accounts != null) {
            for (Account account : accounts) {
                if (account.getTransactions() != null) {
                    all.addAll(account.getTransactions());
                }
            }
        }
        return all;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "CustomerID='" + userId + '\'' +
                ", FirstName='" + firstName + '\'' +
                ", LastName='" + lastName + '\'' +
                ", Email='" + email + '\'' +
                ", PhoneNumber='" + phoneNumber + '\'' +
                ", Accounts=" + (accounts != null ? accounts.size() + " linked" : "None") +
                '}';
    }
}

