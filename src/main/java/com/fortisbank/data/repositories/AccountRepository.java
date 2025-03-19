package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.models.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository implements IAccountRepository {
    private final DatabaseConnection dbConnection;
    private final CustomerRepository customerRepository; // Used to fetch Customer object

    public AccountRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.customerRepository = new CustomerRepository(); // Dependency for customer retrieval
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

            stmt.setString(1, account.getAccountNumber());
            stmt.setString(2, account.getCustomer().getCustomerID());
            stmt.setString(3, account.getAccountType());
            stmt.setDate(4, new java.sql.Date(account.getOpenedDate().getTime()));
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

            stmt.setString(1, account.getCustomer().getCustomerID());
            stmt.setString(2, account.getAccountType());
            stmt.setDate(3, new java.sql.Date(account.getOpenedDate().getTime()));
            stmt.setBigDecimal(4, account.getAvailableBalance());
            stmt.setBoolean(5, account.isActive());
            stmt.setString(6, account.getAccountNumber());

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

    // **Fixed mapResultSetToAccount to return the correct subclass**
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String accountId = rs.getString("AccountID");
        String customerId = rs.getString("CustomerID");
        String accountType = rs.getString("AccountType");
        Date openedDate = rs.getDate("OpenedDate");
        BigDecimal availableBalance = rs.getBigDecimal("AvailableBalance");
        boolean isActive = rs.getBoolean("isActive");

        // Fetch Customer object (as it's necessary to add the user object instead of just the ID for serialization)
        Customer customer = customerRepository.getCustomerById(customerId);

        switch (accountType.toUpperCase()) { // TODO: Fix --> check all account types and their constructor parameters
            case "CHECKING":
                return new CheckingAccount(accountId, customer, openedDate, availableBalance, rs.getInt("FreeTransactionLimit"));
            case "SAVINGS":
                return new SavingsAccount(accountId, customer, openedDate, availableBalance, rs.getBigDecimal("AnnualInterestRate"));
            case "CREDIT":
                return new CreditAccount(accountId, customer, openedDate, availableBalance, rs.getBigDecimal("CreditLimit"), rs.getBigDecimal("InterestRate"));
            case "LINE_OF_CREDIT":
                return new LineOfCredit(accountId, customer, openedDate, availableBalance, rs.getBigDecimal("CreditLimit"), rs.getBigDecimal("InterestRate"));
            case "CURRENCY":
                return new CurrencyAccount(accountId, customer, openedDate, availableBalance, rs.getString("CurrencyType"), rs.getBigDecimal("ExchangeRate"));
            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }
    }

}
