package com.fortisbank.models;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String CustomerID;
    private String FirstName;
    private String LastName;
    private String PINHash;
    private String Email;
    private String PhoneNumber;

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


    @Override
    public String toString() {
        return "Customer{" +
                "CustomerID='" + CustomerID + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", PINHash='" + PINHash + '\'' +
                ", Email='" + Email + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                '}';
    }
}
