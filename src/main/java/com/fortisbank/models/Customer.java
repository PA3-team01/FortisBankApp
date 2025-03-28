package com.fortisbank.models;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.TransactionList;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String CustomerID;
    private String FirstName;
    private String LastName;
    private String PINHash;
    private String Email;
    private String PhoneNumber;

    // Not serialized; loaded dynamically at runtime
    private transient AccountList accounts;

    public Customer() {
    }

    public Customer(String customerID, String firstName, String lastName, String PINHash, String email, String phoneNumber) {
        this.CustomerID = customerID;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.PINHash = PINHash;
        this.Email = email;
        this.PhoneNumber = phoneNumber;
    }

    // ------------------------- GETTERS -------------------------

    public String getCustomerID() {
        return CustomerID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getFullName() {
        return FirstName + " " + LastName;
    }

    public String getPINHash() {
        return PINHash;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public AccountList getAccounts() {
        return accounts;
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


    // ------------------------- SETTERS -------------------------

    public void setCustomerID(String customerID) {
        this.CustomerID = customerID;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public void setPINHash(String PINHash) {
        this.PINHash = PINHash;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.PhoneNumber = phoneNumber;
    }

    public void setAccounts(AccountList accounts) {
        this.accounts = accounts;
    }

    // ------------------------- TO STRING -------------------------

    @Override
    public String toString() {
        return "Customer{" +
                "CustomerID='" + CustomerID + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Email='" + Email + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Accounts=" + (accounts != null ? accounts.size() + " linked" : "None") +
                '}';
    }
}
