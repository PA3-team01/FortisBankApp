package com.fortisbank.models;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;

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
    private AccountList accounts;

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

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        this.CustomerID = customerID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public String getFullName() {
        return FirstName + " " + LastName;
    }

    public String getPINHash() {
        return PINHash;
    }

    public void setPINHash(String PINHash) {
        this.PINHash = PINHash;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.PhoneNumber = phoneNumber;
    }

    public AccountList getAccounts() {return accounts;}
    public void setAccounts(AccountList accounts) {this.accounts = accounts;}
    public BigDecimal getBalance() {
        BigDecimal totalBalance = BigDecimal.ZERO;

        if (accounts != null) {
            for (Account account : accounts) {
                totalBalance = totalBalance.add(account.getAvailableBalance());
            }
        }
        return totalBalance;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "CustomerID='" + CustomerID + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", PINHash='" + PINHash + '\'' +
                ", Email='" + Email + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                 "accounts=" + accounts + '\'' +
                //TODO: Add accounts to toString
                '}';
    }
}