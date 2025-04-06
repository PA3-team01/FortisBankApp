package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.utils.IdGenerator;

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

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the database connection and account repository.
     */
    private TransactionRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.accountRepository = AccountRepository.getInstance();
    }

    /**
     * Returns the singleton instance of TransactionRepository.
     *
     * @return the singleton instance
     */
    public static TransactionRepository getInstance() {
        if(instance == null){
            instance = new TransactionRepository();
        }
        return instance;
    }

    /**
     * Retrieves a transaction by its number.
     *
     * @param transactionNumber the number of the transaction to retrieve
     * @return the transaction with the specified number, or null if not found
     */
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

    /**
     * Retrieves all transactions associated with a specific account ID.
     *
     * @param accountId the ID of the account whose transactions to retrieve
     * @return a list of transactions associated with the specified account ID
     */
    @Override
    public TransactionList getTransactionsByAccount(String accountId) {
        var transactions = new TransactionList();
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

    /**
     * Retrieves all transactions.
     *
     * @return a list of all transactions
     */
    @Override
    public TransactionList getAllTransactions() {
        var transactions = new TransactionList();
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

    /**
     * Inserts a new transaction into the database.
     *
     * @param transaction the transaction to insert
     */
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
            stmt.setString(4, transaction.getTransactionType().name()); // return string representation of enum
            stmt.setBigDecimal(5, transaction.getAmount());
            stmt.setString(6, transaction.getSourceAccount().getAccountNumber());
            stmt.setString(7, (transaction.getDestinationAccount() != null) ? transaction.getDestinationAccount().getAccountNumber() : null);

            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Transaction {0} inserted successfully.", transactionId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting transaction: {0}", e.getMessage());
        }
    }

    /**
     * Deletes a transaction by its number.
     *
     * @param transactionNumber the number of the transaction to delete
     */
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

    /**
     * Retrieves transactions for a specific customer within a date range.
     *
     * @param customerID the ID of the customer whose transactions to retrieve
     * @param start the start date of the date range
     * @param end the end date of the date range
     * @return a list of transactions for the specified customer within the date range
     */
    @Override
    public TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end) {
        var transactions = new TransactionList();
        String query = "SELECT t.* FROM transactions t " +
                "JOIN accounts a ON t.SourceAccount = a.AccountNumber " +
                "WHERE a.CustomerID = ? AND t.TransactionDate BETWEEN ? AND ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerID);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving transactions for customer {0}: {1}", new Object[]{customerID, e.getMessage()});
        }
        return transactions;
    }

    /**
     * Retrieves the balance for a specific customer before a given date.
     *
     * @param customerID the ID of the customer whose balance to retrieve
     * @param start the date before which to calculate the balance
     * @return the balance for the specified customer before the given date
     */
    @Override
    public BigDecimal getBalanceBeforeDate(String customerID, LocalDate start) {

        String query = "SELECT SUM(t.Amount) FROM transactions t " +
                "JOIN accounts a ON t.SourceAccount = a.AccountNumber " +
                "WHERE a.CustomerID = ? AND t.TransactionDate < ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerID);
            stmt.setDate(2, Date.valueOf(start));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving balance for customer {0}: {1}", new Object[]{customerID, e.getMessage()});
        }
        return BigDecimal.ZERO;
    }

    /**
     * Helper method to map a ResultSet to a Transaction object.
     *
     * @param rs the ResultSet to map
     * @return the mapped Transaction object
     * @throws SQLException if an SQL error occurs
     */
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