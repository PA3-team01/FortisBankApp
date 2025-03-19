package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository implements ITransactionRepository {
    private final DatabaseConnection dbConnection;

    public TransactionRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Transaction getTransactionById(String transactionId) {
        String query = "SELECT * FROM transactions WHERE TransactionID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE SourceAccountID = ? OR DestinationAccountID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountId);
            stmt.setString(2, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public void insertTransaction(Transaction transaction) {
        String query = "INSERT INTO transactions (TransactionID, SourceAccountID, DestinationAccountID, TransactionType, Amount, TransactionDate) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, transaction.getTransactionID());
            stmt.setString(2, transaction.getSourceAccountID());
            stmt.setString(3, transaction.getDestinationAccountID());
            stmt.setString(4, transaction.getTransactionType());
            stmt.setBigDecimal(5, transaction.getAmount());
            stmt.setDate(6, Date.valueOf(transaction.getTransactionDate()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTransaction(String transactionId) {
        String query = "DELETE FROM transactions WHERE TransactionID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to map ResultSet to Transaction Object
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getString("TransactionID"),
                rs.getString("SourceAccountID"),
                rs.getString("DestinationAccountID"),
                rs.getString("TransactionType"),
                rs.getBigDecimal("Amount"),
                rs.getDate("TransactionDate").toLocalDate()
        );
    }
}
