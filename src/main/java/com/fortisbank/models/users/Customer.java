package com.fortisbank.models.users;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.TransactionList;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Class representing a customer.
 */
public class Customer extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The phone number of the customer.
     */
    private String phoneNumber;

    /**
     * List of accounts associated with the customer.
     * Not serialized; loaded dynamically at runtime.
     */
    private transient AccountList accounts;

    /**
     * Default constructor initializing a customer with default values.
     */
    public Customer() {
        super();
        this.role = Role.CUSTOMER;
    }

    /**
     * Unique identifier for the customer.
     */
    String customerId = UUID.randomUUID().toString();

    /**
     * Constructor initializing a customer with specified values.
     *
     * @param userId the user ID of the customer
     * @param firstName the first name of the customer
     * @param lastName the last name of the customer
     * @param email the email of the customer
     * @param phoneNumber the phone number of the customer
     * @param hashedPassword the hashed password of the customer
     * @param pinHash the hashed PIN of the customer
     */
    public Customer(String userId, String firstName, String lastName, String email, String phoneNumber,
                    String hashedPassword, String pinHash) {
        super(userId, firstName, lastName, email, hashedPassword, pinHash, Role.CUSTOMER);
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the phone number of the customer.
     *
     * @return the phone number of the customer
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the customer.
     *
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the list of accounts associated with the customer.
     *
     * @return the list of accounts
     */
    public AccountList getAccounts() {
        return accounts;
    }

    /**
     * Sets the list of accounts associated with the customer.
     *
     * @param accounts the list of accounts to set
     */
    public void setAccounts(AccountList accounts) {
        this.accounts = accounts;
    }

    /**
     * Returns the total balance of all accounts associated with the customer.
     *
     * @return the total balance
     */
    public BigDecimal getBalance() {
        BigDecimal totalBalance = BigDecimal.ZERO;
        if (accounts != null) {
            for (Account account : accounts) {
                totalBalance = totalBalance.add(account.getAvailableBalance());
            }
        }
        return totalBalance;
    }

    /**
     * Returns the list of transactions for all accounts associated with the customer.
     *
     * @return the list of transactions
     */
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

    /**
     * Returns a string representation of the customer.
     *
     * @return a string representation of the customer
     */
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