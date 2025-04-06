package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.exceptions.CustomerNotFoundException;
import com.fortisbank.models.collections.CustomerList;
import com.fortisbank.models.users.Customer;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import static com.fortisbank.data.repositories.AccountRepository.LOGGER;

/**
 * Repository class for managing customer data in the database.
 * Implements the ICustomerRepository interface.
 */
public class CustomerRepository implements ICustomerRepository {
    private final DatabaseConnection dbConnection;
    private static CustomerRepository instance;

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the database connection.
     */
    private CustomerRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Returns the singleton instance of CustomerRepository.
     * Synchronized to prevent multiple threads from creating multiple instances.
     *
     * @return the singleton instance of CustomerRepository
     */
    public static synchronized CustomerRepository getInstance() {
        if(instance == null){
            instance = new CustomerRepository();
        }
        return instance;
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId the ID of the customer to retrieve
     * @return the customer with the specified ID
     * @throws CustomerNotFoundException if the customer is not found
     */
    @Override
    public Customer getCustomerById(String customerId) {
        String query = "SELECT * FROM customers WHERE CustomerID = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            } else {
                LOGGER.log(Level.WARNING, "Customer with ID {0} not found.", customerId);
                throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving customer {0}: {1}", new Object[]{customerId, e.getMessage()});
            throw new CustomerNotFoundException("Error retrieving customer " + customerId);
        }
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return a list of all customers
     */
    @Override
    public CustomerList getAllCustomers() {
        var customers = new CustomerList();
        String query = "SELECT * FROM customers";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Inserts a new customer into the database.
     *
     * @param customer the customer to insert
     */
    @Override
    public void insertCustomer(Customer customer) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing customer in the database.
     *
     * @param customer the customer to update
     */
    @Override
    public void updateCustomer(Customer customer) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a customer from the database.
     *
     * @param customerId the ID of the customer to delete
     */
    @Override
    public void deleteCustomer(String customerId) {
        String query = "DELETE FROM customers WHERE CustomerID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Maps a ResultSet object to a Customer instance.
     *
     * @param rs the ResultSet object containing customer data retrieved from the database
     * @return a Customer object representing the mapped customer data
     * @throws SQLException if a database access error occurs or if the ResultSet cannot be read
     */
    private Customer mapResultSetToCustomer(@NotNull ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString(rs.findColumn("CustomerID")),
                rs.getString(rs.findColumn("FirstName")),
                rs.getString(rs.findColumn("LastName")),
                rs.getString(rs.findColumn("Email")),
                rs.getString(rs.findColumn("PasswordHash")),
                rs.getString(rs.findColumn("PhoneNumber")),
                rs.getString(rs.findColumn("PINHash"))
        );
    }
}