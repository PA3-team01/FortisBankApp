package com.fortisbank.business.services.manager;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.users.Customer;

/**
 * Interface for bank manager-related operations.
 */
public interface IBankManagerService {

    /**
     * Creates a new customer.
     *
     * @param firstName the first name of the customer
     * @param lastName the last name of the customer
     * @param email the email of the customer
     * @param phone the phone number of the customer
     * @param hashedPassword the hashed password of the customer
     * @param pinHash the hashed PIN of the customer
     * @return the created customer
     */
    Customer createCustomer(String firstName, String lastName, String email, String phone, String hashedPassword, String pinHash);

    /**
     * Approves an account.
     *
     * @param account the account to be approved
     * @return true if the account is approved
     */
    boolean approveAccount(Account account);

    /**
     * Closes an account.
     *
     * @param account the account to be closed
     * @return true if the account is successfully closed
     */
    boolean closeAccount(Account account);

    /**
     * Deletes a customer.
     *
     * @param customer the customer to be deleted
     * @return true if the customer is successfully deleted
     */
    boolean deleteCustomer(Customer customer);

    /**
     * Generates a customer report.
     */
    void generateCustomerReport();
}