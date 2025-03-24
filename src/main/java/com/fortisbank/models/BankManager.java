package com.fortisbank.models;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.accounts.CheckingAccount;
import com.fortisbank.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BankManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String managerID;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final List<Customer> customers;

    public BankManager(String firstName, String lastName, String email) {
        this.managerID = IdGenerator.generateId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.customers = new ArrayList<>();
    }

    public Customer createCustomer(String firstName, String lastName, String pinHash, String email, String phoneNumber) {
        Customer newCustomer = new Customer(IdGenerator.generateId(), firstName, lastName, pinHash, email, phoneNumber);
        customers.add(newCustomer);
        System.out.println("Customer " + newCustomer.getFullName() + " created successfully.");

        CheckingAccount checkingAccount = new CheckingAccount(newCustomer, new BigDecimal("0.00"));
        System.out.println("Checking account created for " + newCustomer.getFullName());

        return newCustomer;
    }

    public boolean approveAccount(Account account) {
        if (account != null && account.getCustomer() != null) {
            System.out.println("Account approved for " + account.getCustomer().getFullName());
            return true;
        }
        return false;
    }

    public boolean closeAccount(Account account) {
        if (account != null && account.isActive()) {
            if (account.getAvailableBalance().compareTo(BigDecimal.ZERO) == 0) {
                account.setActive(false);
                System.out.println("Account " + account.getAccountNumber() + " closed successfully.");
                return true;
            } else {
                System.out.println("Cannot close account: balance is not zero.");
            }
        }
        return false;
    }

    public boolean deleteCustomer(Customer customer) {
        if (customer != null) {
            customers.remove(customer);
            System.out.println("Customer " + customer.getFullName() + " deleted successfully.");
            return true;
        }
        return false;
    }

    public void generateReport() {
        System.out.println("---- Bank Report ----");
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    @Override
    public String toString() {
        return "BankManager{" +
                "managerID='" + managerID + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", customersCount=" + customers.size() +
                '}';
    }
}
