package com.fortisbank.data.repositories;

     import com.fortisbank.exceptions.CustomerRepositoryException;
     import com.fortisbank.models.collections.CustomerList;
     import com.fortisbank.models.users.Customer;

     /**
      * Interface for customer repository operations.
      * Provides methods to manage customer data.
      */
     public interface ICustomerRepository {

         /**
          * Retrieves a customer by their ID.
          *
          * @param customerId the ID of the customer to retrieve
          * @return the customer with the specified ID
          * @throws CustomerRepositoryException if an error occurs while retrieving the customer
          */
         Customer getCustomerById(String customerId) throws CustomerRepositoryException;

         /**
          * Retrieves all customers.
          *
          * @return a list of all customers
          * @throws CustomerRepositoryException if an error occurs while retrieving the customers
          */
         CustomerList getAllCustomers() throws CustomerRepositoryException;

         /**
          * Inserts a new customer.
          *
          * @param customer the customer to insert
          * @throws CustomerRepositoryException if an error occurs while inserting the customer
          */
         void insertCustomer(Customer customer) throws CustomerRepositoryException;

         /**
          * Updates an existing customer.
          *
          * @param customer the customer to update
          * @throws CustomerRepositoryException if an error occurs while updating the customer
          */
         void updateCustomer(Customer customer) throws CustomerRepositoryException;

         /**
          * Deletes a customer by their ID.
          *
          * @param customerId the ID of the customer to delete
          * @throws CustomerRepositoryException if an error occurs while deleting the customer
          */
         void deleteCustomer(String customerId) throws CustomerRepositoryException;
     }