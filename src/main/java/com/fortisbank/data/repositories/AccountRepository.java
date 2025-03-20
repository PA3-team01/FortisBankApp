package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.models.*;
import com.fortisbank.models.accounts.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountRepository implements IAccountRepository {
    private static final Logger LOGGER = Logger.getLogger(AccountRepository.class.getName());

    private final DatabaseConnection dbConnection;
    private final CustomerRepository customerRepository;

    public AccountRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.customerRepository = new CustomerRepository();
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
            LOGGER.log(Level.SEVERE, "Error retrieving account {0}: {1}", new Object[]{accountId, e.getMessage()});
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
            LOGGER.log(Level.SEVERE, "Error retrieving accounts for customer {0}: {1}", new Object[]{customerId, e.getMessage()});
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
            LOGGER.log(Level.SEVERE, "Error retrieving all accounts: {0}", e.getMessage());
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
            stmt.setString(3, account.getAccountType().name()); // Use enum instead of raw string
            stmt.setDate(4, new java.sql.Date(account.getOpenedDate().getTime()));
            stmt.setBigDecimal(5, account.getAvailableBalance());
            stmt.setBoolean(6, account.isActive());

            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Account {0} inserted successfully.", account.getAccountNumber());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting account: {0}", e.getMessage());
        }
    }

    @Override
    public void updateAccount(Account account) {
        String query = "UPDATE accounts SET CustomerID = ?, AccountType = ?, OpenedDate = ?, AvailableBalance = ?, isActive = ? WHERE AccountID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account.getCustomer().getCustomerID());
            stmt.setString(2, account.getAccountType().name()); // Use enum
            stmt.setDate(3, new java.sql.Date(account.getOpenedDate().getTime()));
            stmt.setBigDecimal(4, account.getAvailableBalance());
            stmt.setBoolean(5, account.isActive());
            stmt.setString(6, account.getAccountNumber());

            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Account {0} updated successfully.", account.getAccountNumber());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating account: {0}", e.getMessage());
        }
    }

    @Override
    public void deleteAccount(String accountId) {
        String query = "DELETE FROM accounts WHERE AccountID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountId);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Account {0} deleted successfully.", accountId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting account: {0}", e.getMessage());
        }
    }

    // **Fixed mapResultSetToAccount to use Enum and correct account mapping**
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String accountId = rs.getString("AccountID");
        String customerId = rs.getString("CustomerID");
        AccountType accountType = AccountType.valueOf(rs.getString("AccountType").toUpperCase());
        Date openedDate = rs.getDate("OpenedDate");
        BigDecimal availableBalance = rs.getBigDecimal("AvailableBalance");
        boolean isActive = rs.getBoolean("isActive");

        // Fetch Customer object
        Customer customer = customerRepository.getCustomerById(customerId);

        switch (accountType) {
            case CHECKING:
                return new CheckingAccount(accountId, customer, openedDate, availableBalance, rs.getInt("FreeTransactionLimit"));
            case SAVINGS:
                return new SavingsAccount(accountId, customer, openedDate, availableBalance, rs.getBigDecimal("AnnualInterestRate"));
            case CREDIT:
                return new CreditAccount(accountId, customer, openedDate, availableBalance, rs.getBigDecimal("CreditLimit"), rs.getBigDecimal("InterestRate"));
            case LINE_OF_CREDIT:
                return new LineOfCredit(accountId, customer, openedDate, availableBalance, rs.getBigDecimal("CreditLimit"), rs.getBigDecimal("InterestRate"));
            case CURRENCY:
                return new CurrencyAccount(accountId, customer, openedDate, availableBalance, rs.getString("CurrencyType"), rs.getBigDecimal("ExchangeRate"));
            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }
    }
}
