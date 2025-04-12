package com.fortisbank.data.database;

import com.fortisbank.contracts.collections.AccountList;
import com.fortisbank.contracts.exceptions.AccountRepositoryException;
import com.fortisbank.contracts.exceptions.DatabaseConnectionException;
import com.fortisbank.contracts.models.accounts.*;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.data.dal_utils.DatabaseConnection;
import com.fortisbank.data.dto.AccountDTO;
import com.fortisbank.data.interfaces.IAccountRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountRepository implements IAccountRepository {
    private static final Logger LOGGER = Logger.getLogger(AccountRepository.class.getName());
    private static AccountRepository instance;

    private final DatabaseConnection dbConnection;
    private final CustomerRepository customerRepository;

    private AccountRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.customerRepository = CustomerRepository.getInstance();
    }

    public static synchronized AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    @Override
    public Account getAccountById(String accountId) throws AccountRepositoryException {
        String query = "SELECT * FROM accounts WHERE account_id = ?";
        return executeQuery(query, stmt -> stmt.setString(1, accountId), rs -> {
            if (rs.next()) {
                return mapResultSetToAccount(rs);
            } else {
                throw new AccountRepositoryException("Account with ID " + accountId + " not found.");
            }
        });
    }

    @Override
    public AccountList getAccountsByCustomerId(String customerId) throws AccountRepositoryException {
        String query = "SELECT * FROM accounts WHERE customer_id = ?";
        return executeQueryList(query, stmt -> stmt.setString(1, customerId));
    }

    @Override
    public AccountList getAllAccounts() throws AccountRepositoryException {
        return executeQueryList("SELECT * FROM accounts", stmt -> {});
    }

    @Override
    public void insertAccount(Account account) throws AccountRepositoryException {
        String query = "INSERT INTO accounts (account_id, customer_id, account_type, opened_date, is_active, available_balance, credit_limit) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        executeUpdate(query, stmt -> {
            stmt.setString(1, account.getAccountNumber());
            stmt.setString(2, account.getCustomer().getUserId());
            stmt.setString(3, account.getAccountType().name());
            stmt.setDate(4, new java.sql.Date(account.getOpenedDate().getTime()));
            stmt.setBoolean(5, account.isActive());
            stmt.setBigDecimal(6, account.getAvailableBalance());
            stmt.setBigDecimal(7, account instanceof CreditAccount credit ? credit.getCreditLimit() : null);
        });
    }

    @Override
    public void updateAccount(Account account) throws AccountRepositoryException {
        String query = "UPDATE accounts SET customer_id = ?, account_type = ?, opened_date = ?, is_active = ?, available_balance = ?, credit_limit = ? " +
                "WHERE account_id = ?";

        executeUpdate(query, stmt -> {
            stmt.setString(1, account.getCustomer().getUserId());
            stmt.setString(2, account.getAccountType().name());
            stmt.setDate(3, new java.sql.Date(account.getOpenedDate().getTime()));
            stmt.setBoolean(4, account.isActive());
            stmt.setBigDecimal(5, account.getAvailableBalance());
            stmt.setBigDecimal(6, account instanceof CreditAccount credit ? credit.getCreditLimit() : null);
            stmt.setString(7, account.getAccountNumber());
        });
    }

    @Override
    public void deleteAccount(String accountId) throws AccountRepositoryException {
        executeUpdate("DELETE FROM accounts WHERE account_id = ?", stmt -> stmt.setString(1, accountId));
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException, AccountRepositoryException {
        AccountDTO dto = new AccountDTO(
                rs.getString("account_id"),
                rs.getString("customer_id"),
                rs.getString("account_type"),
                rs.getDate("opened_date").toLocalDate(),
                rs.getInt("is_active") == 1,
                rs.getBigDecimal("available_balance"),
                rs.getBigDecimal("credit_limit"),
                rs.getBigDecimal("interest_rate"),
                rs.getString("currency_code")
        );

        Customer customer = customerRepository.getCustomerById(dto.customerId());
        return dto.toEntity(customer);
    }

    private AccountList executeQueryList(String query, QueryPreparer preparer) throws AccountRepositoryException {
        var list = new AccountList();
        executeQuery(query, preparer, rs -> {
            while (rs.next()) {
                list.add(mapResultSetToAccount(rs));
            }
            return null;
        });
        return list;
    }

    private <T> T executeQuery(String query, QueryPreparer preparer, ResultSetMapper<T> mapper) throws AccountRepositoryException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.prepare(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapper.map(rs);
            }
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: {0}", e.getMessage());
            throw new AccountRepositoryException("Query failed: " + query, e);
        }
    }

    private void executeUpdate(String query, QueryPreparer preparer) throws AccountRepositoryException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.prepare(stmt);
            stmt.executeUpdate();
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, "Error executing update: {0}", e.getMessage());
            throw new AccountRepositoryException("Update failed: " + query, e);
        }
    }

    @FunctionalInterface
    private interface QueryPreparer {
        void prepare(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException, AccountRepositoryException;
    }
}
