package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.models.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository implements IAccountRepository {
    private final DatabaseConnection dbConnection;

    public AccountRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Account getAccountById(String accountId) {
        String query = "SELECT * FROM accounts WHERE AccountID = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> getAccountsByCustomerId(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts WHERE CustomerID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public void insertAccount(Account account) {
        String query = "INSERT INTO accounts (AccountID, CustomerID, AccountType, OpenedDate, AvailableBalance, isActive) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account.getAccountID());
            stmt.setString(2, account.getCustomerID());
            stmt.setString(3, account.getAccountType());
            stmt.setDate(4, Date.valueOf(account.getOpenedDate()));
            stmt.setBigDecimal(5, account.getAvailableBalance());
            stmt.setBoolean(6, account.isActive());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAccount(Account account) {
        String query = "UPDATE accounts SET CustomerID = ?, AccountType = ?, OpenedDate = ?, AvailableBalance = ?, isActive = ? WHERE AccountID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account.getCustomerID());
            stmt.setString(2, account.getAccountType());
            stmt.setDate(3, Date.valueOf(account.getOpenedDate()));
            stmt.setBigDecimal(4, account.getAvailableBalance());
            stmt.setBoolean(5, account.isActive());
            stmt.setString(6, account.getAccountID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAccount(String accountId) {
        String query = "DELETE FROM accounts WHERE AccountID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to map ResultSet to Account Object
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getString("AccountID"),
                rs.getString("CustomerID"),
                rs.getString("AccountType"),
                rs.getDate("OpenedDate").toLocalDate(),
                rs.getBigDecimal("AvailableBalance"),
                rs.getBoolean("isActive")
        );
    }
}
