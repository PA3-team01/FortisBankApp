package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.utils.IdGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionRepository implements ITransactionRepository {
    private static final Logger LOGGER = Logger.getLogger(TransactionRepository.class.getName());

    private final DatabaseConnection dbConnection;
    private final AccountRepository accountRepository;

    public TransactionRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.accountRepository = new AccountRepository();
    }

    @Override
    public Transaction getTransactionByNumber(String transactionNumber) {
        String query = "SELECT * FROM transactions WHERE TransactionNumber = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, transactionNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving transaction: {0}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<Transaction> getTransactionsByAccount(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE SourceAccount = ? OR DestinationAccount = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountId);
            stmt.setString(2, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving transactions for account {0}: {1}", new Object[]{accountId, e.getMessage()});
        }
        return transactions;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all transactions: {0}", e.getMessage());
        }
        return transactions;
    }

    @Override
    public void insertTransaction(Transaction transaction) {
        String query = "INSERT INTO transactions (TransactionNumber, Description, TransactionDate, TransactionType, Amount, SourceAccount, DestinationAccount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String transactionId = (transaction.getTransactionNumber() != null) ? transaction.getTransactionNumber() : IdGenerator.generateId();
            stmt.setString(1, transactionId);
            stmt.setString(2, transaction.getDescription());
            stmt.setDate(3, new java.sql.Date(transaction.getTransactionDate().getTime()));
            stmt.setString(4, transaction.getTransactionType().name()); // Enum instead of String
            stmt.setBigDecimal(5, transaction.getAmount());
            stmt.setString(6, transaction.getSourceAccount().getAccountNumber());
            stmt.setString(7, (transaction.getDestinationAccount() != null) ? transaction.getDestinationAccount().getAccountNumber() : null);

            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Transaction {0} inserted successfully.", transactionId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting transaction: {0}", e.getMessage());
        }
    }

    @Override
    public void deleteTransaction(String transactionNumber) {
        String query = "DELETE FROM transactions WHERE TransactionNumber = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, transactionNumber);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Transaction {0} deleted successfully.", transactionNumber);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting transaction: {0}", e.getMessage());
        }
    }

    // Helper method to map ResultSet to Transaction Object
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Account sourceAccount = accountRepository.getAccountById(rs.getString("SourceAccount"));
        Account destinationAccount = null;

        if (rs.getString("DestinationAccount") != null) {
            destinationAccount = accountRepository.getAccountById(rs.getString("DestinationAccount"));
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
}
