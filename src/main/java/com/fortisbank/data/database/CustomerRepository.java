package com.fortisbank.data.database;

     import com.fortisbank.data.dal_utils.DatabaseConnection;
     import com.fortisbank.data.interfaces.ICustomerRepository;
     import com.fortisbank.contracts.exceptions.CustomerRepositoryException;
     import com.fortisbank.contracts.exceptions.DatabaseConnectionException;
     import com.fortisbank.contracts.collections.CustomerList;
     import com.fortisbank.contracts.models.users.Customer;
     import org.jetbrains.annotations.NotNull;

     import java.sql.Connection;
     import java.sql.PreparedStatement;
     import java.sql.ResultSet;
     import java.sql.SQLException;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     /**
      * Repository class for managing customer data in the database.
      * Implements the ICustomerRepository interface.
      */
     public class CustomerRepository implements ICustomerRepository {
         private static final Logger LOGGER = Logger.getLogger(CustomerRepository.class.getName());
         private final DatabaseConnection dbConnection;
         private static CustomerRepository instance;

         private CustomerRepository() {
             this.dbConnection = DatabaseConnection.getInstance();
         }

         public static synchronized CustomerRepository getInstance() {
             if (instance == null) {
                 instance = new CustomerRepository();
             }
             return instance;
         }

         @Override
         public Customer getCustomerById(String customerId) throws CustomerRepositoryException {
             String query = "SELECT * FROM customers WHERE CustomerID = ?";
             try (Connection conn = dbConnection.getConnection();
                  PreparedStatement stmt = conn.prepareStatement(query)) {

                 stmt.setString(1, customerId);
                 ResultSet rs = stmt.executeQuery();

                 if (rs.next()) {
                     return mapResultSetToCustomer(rs);
                 } else {
                     LOGGER.log(Level.WARNING, "Customer with ID {0} not found.", customerId);
                     throw new CustomerRepositoryException("Customer with ID " + customerId + " not found.");
                 }
             } catch (SQLException | DatabaseConnectionException e) {
                 LOGGER.log(Level.SEVERE, "Error retrieving customer {0}: {1}", new Object[]{customerId, e.getMessage()});
                 throw new CustomerRepositoryException("Error retrieving customer " + customerId, e);
             }
         }

         @Override
         public CustomerList getAllCustomers() throws CustomerRepositoryException {
             var customers = new CustomerList();
             String query = "SELECT * FROM customers";

             try (Connection conn = dbConnection.getConnection();
                  PreparedStatement stmt = conn.prepareStatement(query);
                  ResultSet rs = stmt.executeQuery()) {

                 while (rs.next()) {
                     customers.add(mapResultSetToCustomer(rs));
                 }
             } catch (SQLException | DatabaseConnectionException e) {
                 LOGGER.log(Level.SEVERE, "Error retrieving all customers: {0}", e.getMessage());
                 throw new CustomerRepositoryException("Error retrieving all customers", e);
             }
             return customers;
         }

         @Override
         public void insertCustomer(Customer customer) throws CustomerRepositoryException {
             String query = "INSERT INTO customers (CustomerID, FirstName, LastName, Email, PhoneNumber, PINHash) VALUES (?, ?, ?, ?, ?, ?)";

             try (Connection conn = dbConnection.getConnection();
                  PreparedStatement stmt = conn.prepareStatement(query)) {

                 stmt.setString(1, customer.getUserId());
                 stmt.setString(2, customer.getFirstName());
                 stmt.setString(3, customer.getLastName());
                 stmt.setString(4, customer.getEmail());
                 stmt.setString(5, customer.getPhoneNumber());
                 stmt.setString(6, customer.getPINHash());

                 stmt.executeUpdate();
             } catch (SQLException | DatabaseConnectionException e) {
                 LOGGER.log(Level.SEVERE, "Error inserting customer: {0}", e.getMessage());
                 throw new CustomerRepositoryException("Error inserting customer", e);
             }
         }

         @Override
         public void updateCustomer(Customer customer) throws CustomerRepositoryException {
             String query = "UPDATE customers SET FirstName = ?, LastName = ?, Email = ?, PhoneNumber = ?, PINHash = ? WHERE CustomerID = ?";

             try (Connection conn = dbConnection.getConnection();
                  PreparedStatement stmt = conn.prepareStatement(query)) {

                 stmt.setString(1, customer.getFirstName());
                 stmt.setString(2, customer.getLastName());
                 stmt.setString(3, customer.getEmail());
                 stmt.setString(4, customer.getPhoneNumber());
                 stmt.setString(5, customer.getPINHash());
                 stmt.setString(6, customer.getUserId());

                 stmt.executeUpdate();
             } catch (SQLException | DatabaseConnectionException e) {
                 LOGGER.log(Level.SEVERE, "Error updating customer: {0}", e.getMessage());
                 throw new CustomerRepositoryException("Error updating customer", e);
             }
         }

         @Override
         public void deleteCustomer(String customerId) throws CustomerRepositoryException {
             String query = "DELETE FROM customers WHERE CustomerID = ?";

             try (Connection conn = dbConnection.getConnection();
                  PreparedStatement stmt = conn.prepareStatement(query)) {

                 stmt.setString(1, customerId);
                 stmt.executeUpdate();
             } catch (SQLException | DatabaseConnectionException e) {
                 LOGGER.log(Level.SEVERE, "Error deleting customer with ID {0}: {1}", new Object[]{customerId, e.getMessage()});
                 throw new CustomerRepositoryException("Error deleting customer with ID " + customerId, e);
             }
         }

         private Customer mapResultSetToCustomer(@NotNull ResultSet rs) throws SQLException {
             return new Customer(
                     rs.getString("CustomerID"),
                     rs.getString("FirstName"),
                     rs.getString("LastName"),
                     rs.getString("Email"),
                     rs.getString("PasswordHash"),
                     rs.getString("PhoneNumber"),
                     rs.getString("PINHash")
             );
         }
     }