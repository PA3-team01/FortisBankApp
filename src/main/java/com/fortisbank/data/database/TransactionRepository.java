package com.fortisbank.data.database;

import com.fortisbank.data.dal_utils.DatabaseConnection;
import com.fortisbank.data.interfaces.ITransactionRepository;
import com.fortisbank.contracts.exceptions.AccountRepositoryException;
import com.fortisbank.contracts.exceptions.DatabaseConnectionException;
import com.fortisbank.contracts.exceptions.TransactionRepositoryException;
import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.collections.TransactionList;
import com.fortisbank.contracts.models.transactions.Transaction;
import com.fortisbank.contracts.models.transactions.TransactionFactory;
import com.fortisbank.contracts.models.transactions.TransactionType;
import com.fortisbank.contracts.utils.IdGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for managing transactions in the database.
 * Implements the ITransactionRepository interface.
 */
public class TransactionRepository implements ITransactionRepository {
    private static final Logger LOGGER = Logger.getLogger(TransactionRepository.class.getName());
    private static TransactionRepository instance;

    private final DatabaseConnection dbConnection;
    private final AccountRepository accountRepository;

    private TransactionRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.accountRepository = AccountRepository.getInstance();
    }

    public static TransactionRepository getInstance() {
        if (instance == null) {
            synchronized (TransactionRepository.class) {
                if (instance == null) {
                    instance = new TransactionRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public Transaction getTransactionByNumber(String transactionNumber) throws TransactionRepositoryException {
        String query = "SELECT * FROM transactions WHERE TransactionNumber = ?";
        return executeQuery(query, stmt -> stmt.setString(1, transactionNumber), this::mapResultSetToTransaction);
    }

    @Override
    public TransactionList getTransactionsByAccount(String accountId) throws TransactionRepositoryException {
        String query = "SELECT * FROM transactions WHERE SourceAccount = ? OR DestinationAccount = ?";
        return executeQueryList(query, stmt -> {
            stmt.setString(1, accountId);
            stmt.setString(2, accountId);
        });
    }

    @Override
    public TransactionList getAllTransactions() throws TransactionRepositoryException {
        String query = "SELECT * FROM transactions";
        return executeQueryList(query, stmt -> {});
    }

    @Override
    public void insertTransaction(Transaction transaction) throws TransactionRepositoryException {
        String query = "INSERT INTO transactions (TransactionNumber, Description, TransactionDate, TransactionType, Amount, SourceAccount, DestinationAccount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        executeUpdate(query, stmt -> {
            String transactionId = (transaction.getTransactionNumber() != null) ? transaction.getTransactionNumber() : IdGenerator.generateId();
            stmt.setString(1, transactionId);
            stmt.setString(2, transaction.getDescription());
            stmt.setDate(3, new java.sql.Date(transaction.getTransactionDate().getTime()));
            stmt.setString(4, transaction.getTransactionType().name());
            stmt.setBigDecimal(5, transaction.getAmount());
            stmt.setString(6, transaction.getSourceAccount().getAccountNumber());
            stmt.setString(7, (transaction.getDestinationAccount() != null) ? transaction.getDestinationAccount().getAccountNumber() : null);
        });
    }

    @Override
    public void deleteTransaction(String transactionNumber) throws TransactionRepositoryException {
        String query = "DELETE FROM transactions WHERE TransactionNumber = ?";
        executeUpdate(query, stmt -> stmt.setString(1, transactionNumber));
    }

    @Override
    public TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end) throws TransactionRepositoryException {
        String query = "SELECT t.* FROM transactions t " +
                "JOIN accounts a ON t.SourceAccount = a.AccountNumber " +
                "WHERE a.CustomerID = ? AND t.TransactionDate BETWEEN ? AND ?";
        return executeQueryList(query, stmt -> {
            stmt.setString(1, customerID);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
        });
    }

    @Override
    public BigDecimal getBalanceBeforeDate(String customerID, LocalDate start) throws TransactionRepositoryException {
        String query = "SELECT SUM(t.Amount) FROM transactions t " +
                "JOIN accounts a ON t.SourceAccount = a.AccountNumber " +
                "WHERE a.CustomerID = ? AND t.TransactionDate < ?";
        return executeQuery(query, stmt -> {
            stmt.setString(1, customerID);
            stmt.setDate(2, Date.valueOf(start));
        }, rs -> rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO);
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Account sourceAccount = null;
        try {
            sourceAccount = accountRepository.getAccountById(rs.getString("SourceAccount"));
        } catch (AccountRepositoryException e) {
            throw new RuntimeException(e);
        }
        Account destinationAccount = null;
        try {
            destinationAccount = rs.getString("DestinationAccount") != null
                    ? accountRepository.getAccountById(rs.getString("DestinationAccount"))
                    : null;
        } catch (AccountRepositoryException e) {
            throw new RuntimeException(e);
        }
        TransactionType transactionType = TransactionType.valueOf(rs.getString("TransactionType").toUpperCase());
        return (Transaction) TransactionFactory.createTransaction(
                transactionType,
                rs.getString("Description"),
                rs.getDate("TransactionDate"),
                rs.getBigDecimal("Amount"),
                sourceAccount,
                destinationAccount
        );
    }

    private TransactionList executeQueryList(String query, QueryPreparer preparer) throws TransactionRepositoryException {
        var transactions = new TransactionList();
        executeQuery(query, preparer, rs -> {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            return null;
        });
        return transactions;
    }

    private <T> T executeQuery(String query, QueryPreparer preparer, ResultSetMapper<T> mapper) throws TransactionRepositoryException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.prepare(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapper.map(rs);
            }
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: {0}", e.getMessage());
            throw new TransactionRepositoryException("Error executing query: " + query, e);
        }
    }

    private void executeUpdate(String query, QueryPreparer preparer) throws TransactionRepositoryException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.prepare(stmt);
            stmt.executeUpdate();
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, "Error executing update: {0}", e.getMessage());
            throw new TransactionRepositoryException("Error executing update: " + query, e);
        }
    }

    @FunctionalInterface
    private interface QueryPreparer {
        void prepare(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}