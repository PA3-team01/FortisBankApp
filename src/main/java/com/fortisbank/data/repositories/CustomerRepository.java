package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.models.Customer;
import com.fortisbank.models.collections.CustomerList;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomerRepository implements ICustomerRepository {
    private final DatabaseConnection dbConnection;
    private static CustomerRepository instance;

    private CustomerRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public static CustomerRepository getInstance() {
        if(instance == null){
            instance = new CustomerRepository();
        }
        return instance;
    }

    @Override
    public Customer getCustomerById(String customerId) {
        String query = "SELECT * FROM customers WHERE CustomerID = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {
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

    @Override
    public void insertCustomer(Customer customer) {
        String query = "INSERT INTO customers (CustomerID, FirstName, LastName, Email, PhoneNumber, PINHash) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getCustomerID());
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
            stmt.setString(6, customer.getCustomerID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private Customer mapResultSetToCustomer(@NotNull ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString(rs.findColumn("CustomerID")),
                rs.getString(rs.findColumn("FirstName")),
                rs.getString(rs.findColumn("LastName")),
                rs.getString(rs.findColumn("Email")),
                rs.getString(rs.findColumn("PhoneNumber")),
                rs.getString(rs.findColumn("PINHash"))
        );
    }
}
