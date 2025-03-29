package com.fortisbank.models.users;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.accounts.CheckingAccount;
import com.fortisbank.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BankManager extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Customer> customers;

    public BankManager(String firstName, String lastName, String email, String hashedPassword, String pinHash) {
        this.userId = IdGenerator.generateId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.PINHash = pinHash;
        this.role = Role.MANAGER;
        this.customers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "BankManager{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", customersCount=" + customers.size() +
                '}';
    }
}
