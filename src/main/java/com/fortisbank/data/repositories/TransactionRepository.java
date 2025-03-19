package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.models.Transaction;
import com.fortisbank.models.Account;
import com.fortisbank.utils.IdGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class TransactionRepository implements ITransactionRepository {
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
            System.err.println("Error retrieving transaction: " + e.getMessage());
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
            System.err.println("Error retrieving transactions for account " + accountId + ": " + e.getMessage());
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
            System.err.println("Error retrieving all transactions: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public void insertTransaction(Transaction transaction) {
        String query = "INSERT INTO transactions (TransactionNumber, Description, TransactionDate, TransactionType, Amount, Fees, SourceAccount, DestinationAccount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String transactionId = (transaction.getTransactionNumber() != null) ? transaction.getTransactionNumber() : IdGenerator.generateId();
            stmt.setString(1, transactionId);
            stmt.setString(2, transaction.getDescription());
            stmt.setDate(3, new java.sql.Date(transaction.getTransactionDate().getTime()));
            stmt.setString(4, transaction.getTransactionType());
            stmt.setBigDecimal(5, transaction.getAmount());
            stmt.setBigDecimal(6, (transaction.getFees() != null) ? transaction.getFees() : BigDecimal.ZERO);
            stmt.setString(7, transaction.getSourceAccount().getAccountNumber());
            stmt.setString(8, (transaction.getDestinationAccount() != null) ? transaction.getDestinationAccount().getAccountNumber() : null);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting transaction: " + e.getMessage());
        }
    }

    @Override
    public void deleteTransaction(String transactionNumber) {
        String query = "DELETE FROM transactions WHERE TransactionNumber = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, transactionNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
        }
    }

    // Helper method to map ResultSet to Transaction Object
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Account sourceAccount = accountRepository.getAccountById(rs.getString("SourceAccount"));
        Account destinationAccount = null;

        if (rs.getString("DestinationAccount") != null) {
            destinationAccount = accountRepository.getAccountById(rs.getString("DestinationAccount"));
        }

        return new Transaction(
                rs.getString("TransactionNumber"),
                rs.getString("Description"),
                rs.getDate("TransactionDate"),
                rs.getString("TransactionType"),
                rs.getBigDecimal("Amount"),
                sourceAccount,
                destinationAccount,
                (rs.getBigDecimal("Fees") != null) ? rs.getBigDecimal("Fees") : BigDecimal.ZERO
        );
    }
}
