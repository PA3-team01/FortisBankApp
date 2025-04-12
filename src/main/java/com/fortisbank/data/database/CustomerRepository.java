package com.fortisbank.data.database;

import com.fortisbank.contracts.collections.CustomerList;
import com.fortisbank.contracts.exceptions.CustomerRepositoryException;
import com.fortisbank.contracts.exceptions.DatabaseConnectionException;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.data.dal_utils.DatabaseConnection;
import com.fortisbank.data.dto.CustomerDTO;
import com.fortisbank.data.interfaces.ICustomerRepository;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        String sql = "SELECT u.*, c.phone_number FROM users u " +
                "JOIN customers c ON u.user_id = c.user_id WHERE u.user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            } else {
                throw new CustomerRepositoryException("Customer with ID " + customerId + " not found.");
            }
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving customer: " + e.getMessage(), e);
            throw new CustomerRepositoryException("Error retrieving customer", e);
        }
    }

    @Override
    public CustomerList getAllCustomers() throws CustomerRepositoryException {
        String sql = "SELECT u.*, c.phone_number FROM users u " +
                "JOIN customers c ON u.user_id = c.user_id WHERE u.role = 'CUSTOMER'";
        CustomerList list = new CustomerList();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }

        } catch (SQLException | DatabaseConnectionException e) {
            throw new CustomerRepositoryException("Error retrieving all customers", e);
        }

        return list;
    }

    @Override
    public void insertCustomer(Customer customer) throws CustomerRepositoryException {
        CustomerDTO dto = CustomerDTO.fromEntity(customer);

        String userSql = "INSERT INTO users (user_id, first_name, last_name, email, hashed_password, pin_hash, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String customerSql = "INSERT INTO customers (user_id, phone_number) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql);
             PreparedStatement custStmt = conn.prepareStatement(customerSql)) {

            userStmt.setString(1, dto.userId());
            userStmt.setString(2, dto.firstName());
            userStmt.setString(3, dto.lastName());
            userStmt.setString(4, dto.email());
            userStmt.setString(5, dto.hashedPassword());
            userStmt.setString(6, dto.pinHash());
            userStmt.setString(7, dto.role());
            userStmt.executeUpdate();

            custStmt.setString(1, dto.userId());
            custStmt.setString(2, dto.phoneNumber());
            custStmt.executeUpdate();

        } catch (SQLException | DatabaseConnectionException e) {
            throw new CustomerRepositoryException("Error inserting customer", e);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws CustomerRepositoryException {
        CustomerDTO dto = CustomerDTO.fromEntity(customer);

        String userSql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, hashed_password = ?, pin_hash = ? " +
                "WHERE user_id = ?";
        String custSql = "UPDATE customers SET phone_number = ? WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql);
             PreparedStatement custStmt = conn.prepareStatement(custSql)) {

            userStmt.setString(1, dto.firstName());
            userStmt.setString(2, dto.lastName());
            userStmt.setString(3, dto.email());
            userStmt.setString(4, dto.hashedPassword());
            userStmt.setString(5, dto.pinHash());
            userStmt.setString(6, dto.userId());
            userStmt.executeUpdate();

            custStmt.setString(1, dto.phoneNumber());
            custStmt.setString(2, dto.userId());
            custStmt.executeUpdate();

        } catch (SQLException | DatabaseConnectionException e) {
            throw new CustomerRepositoryException("Error updating customer", e);
        }
    }

    @Override
    public void deleteCustomer(String customerId) throws CustomerRepositoryException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            stmt.setString(1, customerId);
            stmt.executeUpdate();
        } catch (SQLException | DatabaseConnectionException e) {
            throw new CustomerRepositoryException("Error deleting customer", e);
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        CustomerDTO dto = new CustomerDTO(
                rs.getString("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("hashed_password"),
                rs.getString("pin_hash"),
                rs.getString("role"),
                rs.getString("phone_number")
        );
        return dto.toEntity();
    }
}
