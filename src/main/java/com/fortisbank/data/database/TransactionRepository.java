package com.fortisbank.data.database;

import com.fortisbank.contracts.collections.TransactionList;
import com.fortisbank.contracts.exceptions.AccountRepositoryException;
import com.fortisbank.contracts.exceptions.DatabaseConnectionException;
import com.fortisbank.contracts.exceptions.TransactionRepositoryException;
import com.fortisbank.contracts.models.transactions.Transaction;
import com.fortisbank.data.dal_utils.DatabaseConnection;
import com.fortisbank.data.dto.TransactionDTO;
import com.fortisbank.data.interfaces.ITransactionRepository;
import com.fortisbank.contracts.utils.IdGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionRepository implements ITransactionRepository {

    private static final Logger LOGGER = Logger.getLogger(TransactionRepository.class.getName());
    private static TransactionRepository instance;

    private final DatabaseConnection dbConnection;

    private TransactionRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public static synchronized TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

    @Override
    public Transaction getTransactionByNumber(String transactionId) throws TransactionRepositoryException {
        String query = "SELECT * FROM transactions WHERE transaction_id = ?";
        return executeQuery(query, stmt -> stmt.setString(1, transactionId), rs -> {
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            } else {
                throw new TransactionRepositoryException("Transaction with ID " + transactionId + " not found.");
            }
        });
    }

    @Override
    public TransactionList getTransactionsByAccount(String accountId) throws TransactionRepositoryException {
        String query = "SELECT * FROM transactions WHERE source_account_id = ? OR destination_account_id = ?";
        return executeQueryList(query, stmt -> {
            stmt.setString(1, accountId);
            stmt.setString(2, accountId);
        });
    }

    @Override
    public TransactionList getAllTransactions() throws TransactionRepositoryException {
        return executeQueryList("SELECT * FROM transactions", stmt -> {});
    }

    @Override
    public void insertTransaction(Transaction transaction) throws TransactionRepositoryException {
        String query = "INSERT INTO transactions (transaction_id, transaction_type, transaction_date, amount, description, source_account_id, destination_account_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        TransactionDTO dto = TransactionDTO.fromEntity(transaction);

        executeUpdate(query, stmt -> {
            stmt.setString(1, dto.transactionId() != null ? dto.transactionId() : IdGenerator.generateId());
            stmt.setString(2, dto.transactionType());
            stmt.setDate(3, Date.valueOf(dto.transactionDate()));
            stmt.setBigDecimal(4, dto.amount());
            stmt.setString(5, dto.description());
            stmt.setString(6, dto.sourceAccountId());
            stmt.setString(7, dto.destinationAccountId());
        });
    }

    @Override
    public void deleteTransaction(String transactionId) throws TransactionRepositoryException {
        executeUpdate("DELETE FROM transactions WHERE transaction_id = ?", stmt -> stmt.setString(1, transactionId));
    }

    @Override
    public TransactionList getTransactionsByCustomerAndDateRange(String customerId, LocalDate start, LocalDate end) throws TransactionRepositoryException {
        String query = "SELECT t.* FROM transactions t " +
                "JOIN accounts a ON t.source_account_id = a.account_id " +
                "WHERE a.customer_id = ? AND t.transaction_date BETWEEN ? AND ?";
        return executeQueryList(query, stmt -> {
            stmt.setString(1, customerId);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
        });
    }

    @Override
    public BigDecimal getBalanceBeforeDate(String customerId, LocalDate start) throws TransactionRepositoryException {
        String query = "SELECT SUM(t.amount) FROM transactions t " +
                "JOIN accounts a ON t.source_account_id = a.account_id " +
                "WHERE a.customer_id = ? AND t.transaction_date < ?";
        return executeQuery(query, stmt -> {
            stmt.setString(1, customerId);
            stmt.setDate(2, Date.valueOf(start));
        }, rs -> rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO);
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        TransactionDTO dto = new TransactionDTO(
                rs.getString("transaction_id"),
                rs.getString("transaction_type"),
                rs.getDate("transaction_date").toLocalDate(),
                rs.getBigDecimal("amount"),
                rs.getString("description"),
                rs.getString("source_account_id"),
                rs.getString("destination_account_id")
        );
        return dto.toEntity();
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
            LOGGER.log(Level.SEVERE, "Query failed: {0}", e.getMessage());
            throw new TransactionRepositoryException("Failed query: " + query, e);
        }
    }

    private void executeUpdate(String query, QueryPreparer preparer) throws TransactionRepositoryException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.prepare(stmt);
            stmt.executeUpdate();
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, "Update failed: {0}", e.getMessage());
            throw new TransactionRepositoryException("Failed update: " + query, e);
        }
    }

    @FunctionalInterface
    private interface QueryPreparer {
        void prepare(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException, TransactionRepositoryException;
    }
}
