package com.fortisbank.data.file;

     import com.fortisbank.data.interfaces.ICustomerRepository;
     import com.fortisbank.contracts.exceptions.CustomerRepositoryException;
     import com.fortisbank.contracts.collections.CustomerList;
     import com.fortisbank.contracts.models.users.Customer;

     import java.io.File;
     import java.util.List;

     /**
      * Repository class for managing customer data stored in a file.
      * Extends the FileRepository class and implements the ICustomerRepository interface.
      */
     public class CustomerRepositoryFile extends FileRepository<Customer> implements ICustomerRepository {

         private static final File file = new File("data/customers.ser"); // File to store customer data
         private static CustomerRepositoryFile instance; // Singleton instance

         /**
          * Private constructor to prevent direct instantiation.
          * Initializes the repository with the specified file.
          */
         private CustomerRepositoryFile() {
             super(file);
         }

         /**
          * Returns the singleton instance of CustomerRepositoryFile.
          * Synchronized to prevent multiple threads from creating multiple instances.
          *
          * @return the singleton instance of CustomerRepositoryFile
          */
         public static synchronized CustomerRepositoryFile getInstance() {
             if (instance == null) {
                 instance = new CustomerRepositoryFile();
             }
             return instance;
         }

         /**
          * Retrieves a customer by their ID.
          *
          * @param id the ID of the customer to retrieve
          * @return the customer with the specified ID
          * @throws CustomerRepositoryException if an error occurs while retrieving the customer
          */
         @Override
         public Customer getCustomerById(String id) throws CustomerRepositoryException {
             try {
                 return readAll().stream()
                         .filter(c -> c.getUserId().equals(id))
                         .findFirst()
                         .orElseThrow(() -> new CustomerRepositoryException("Customer with ID " + id + " not found."));
             } catch (Exception e) {
                 throw new CustomerRepositoryException("Error retrieving customer with ID: " + id, e);
             }
         }

         /**
          * Inserts a new customer into the file.
          *
          * @param customer the customer to insert
          * @throws CustomerRepositoryException if an error occurs while inserting the customer
          */
         @Override
         public void insertCustomer(Customer customer) throws CustomerRepositoryException {
             try {
                 List<Customer> customers = readAll();
                 customers.add(customer);
                 writeAll(customers);
             } catch (Exception e) {
                 throw new CustomerRepositoryException("Error inserting customer", e);
             }
         }

         /**
          * Updates an existing customer in the file.
          *
          * @param customer the customer to update
          * @throws CustomerRepositoryException if an error occurs while updating the customer
          */
         @Override
         public void updateCustomer(Customer customer) throws CustomerRepositoryException {
             try {
                 var customers = readAll();
                 boolean updated = false;
                 for (int i = 0; i < customers.size(); i++) {
                     if (customers.get(i).getUserId().equals(customer.getUserId())) {
                         customers.set(i, customer);
                         updated = true;
                         break;
                     }
                 }
                 if (!updated) {
                     throw new CustomerRepositoryException("Customer with ID " + customer.getUserId() + " not found for update.");
                 }
                 writeAll(customers);
             } catch (Exception e) {
                 throw new CustomerRepositoryException("Error updating customer", e);
             }
         }

         /**
          * Deletes a customer from the file.
          *
          * @param id the ID of the customer to delete
          * @throws CustomerRepositoryException if an error occurs while deleting the customer
          */
         @Override
         public void deleteCustomer(String id) throws CustomerRepositoryException {
             try {
                 var customers = readAll();
                 boolean removed = customers.removeIf(c -> c.getUserId().equals(id));
                 if (!removed) {
                     throw new CustomerRepositoryException("Customer with ID " + id + " not found for deletion.");
                 }
                 writeAll(customers);
             } catch (Exception e) {
                 throw new CustomerRepositoryException("Error deleting customer with ID: " + id, e);
             }
         }

         /**
          * Retrieves all customers from the file.
          *
          * @return a list of all customers
          * @throws CustomerRepositoryException if an error occurs while retrieving the customers
          */
         @Override
         public CustomerList getAllCustomers() throws CustomerRepositoryException {
             try {
                 return new CustomerList(readAll());
             } catch (Exception e) {
                 throw new CustomerRepositoryException("Error retrieving all customers", e);
             }
         }
     }