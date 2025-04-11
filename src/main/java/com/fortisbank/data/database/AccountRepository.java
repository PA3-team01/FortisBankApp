package com.fortisbank.data.database;

     import com.fortisbank.data.dal_utils.DatabaseConnection;
     import com.fortisbank.data.interfaces.IAccountRepository;
     import com.fortisbank.contracts.exceptions.AccountRepositoryException;
     import com.fortisbank.contracts.exceptions.DatabaseConnectionException;
     import com.fortisbank.contracts.models.accounts.*;
     import com.fortisbank.contracts.collections.AccountList;
     import com.fortisbank.contracts.models.users.Customer;

     import java.math.BigDecimal;
     import java.sql.*;
     import java.util.Date;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     /**
      * The AccountRepository class is responsible for managing account-related operations
      * such as retrieving, inserting, updating, and deleting accounts from the database.
      */
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
             String query = "SELECT * FROM accounts WHERE AccountID = ?";
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
             String query = "SELECT * FROM accounts WHERE CustomerID = ?";
             return executeQueryList(query, stmt -> stmt.setString(1, customerId));
         }

         @Override
         public AccountList getAllAccounts() throws AccountRepositoryException {
             String query = "SELECT * FROM accounts";
             return executeQueryList(query, stmt -> {});
         }

         @Override
         public void insertAccount(Account account) throws AccountRepositoryException {
             String query = "INSERT INTO accounts (AccountID, CustomerID, AccountType, OpenedDate, AvailableBalance, isActive) VALUES (?, ?, ?, ?, ?, ?)";
             executeUpdate(query, stmt -> {
                 stmt.setString(1, account.getAccountNumber());
                 stmt.setString(2, account.getCustomer().getUserId());
                 stmt.setString(3, account.getAccountType().name());
                 stmt.setDate(4, new java.sql.Date(account.getOpenedDate().getTime()));
                 stmt.setBigDecimal(5, account.getAvailableBalance());
                 stmt.setBoolean(6, account.isActive());
             });
         }

         @Override
         public void updateAccount(Account account) throws AccountRepositoryException {
             String query = "UPDATE accounts SET CustomerID = ?, AccountType = ?, OpenedDate = ?, AvailableBalance = ?, isActive = ? WHERE AccountID = ?";
             executeUpdate(query, stmt -> {
                 stmt.setString(1, account.getCustomer().getUserId());
                 stmt.setString(2, account.getAccountType().name());
                 stmt.setDate(3, new java.sql.Date(account.getOpenedDate().getTime()));
                 stmt.setBigDecimal(4, account.getAvailableBalance());
                 stmt.setBoolean(5, account.isActive());
                 stmt.setString(6, account.getAccountNumber());
             });
         }

         @Override
         public void deleteAccount(String accountId) throws AccountRepositoryException {
             String query = "DELETE FROM accounts WHERE AccountID = ?";
             executeUpdate(query, stmt -> stmt.setString(1, accountId));
         }

         private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
             String accountId = rs.getString("AccountID");
             String customerId = rs.getString("CustomerID");
             AccountType accountType = AccountType.valueOf(rs.getString("AccountType").toUpperCase());
             Date openedDate = rs.getDate("OpenedDate");
             BigDecimal availableBalance = rs.getBigDecimal("AvailableBalance");
             boolean isActive = rs.getBoolean("isActive");

             Customer customer = customerRepository.getCustomerById(customerId);

             switch (accountType) {
                 case CHECKING:
                     return new CheckingAccount(accountId, customer, openedDate, availableBalance);
                 case SAVINGS:
                     return new SavingsAccount(accountId, customer, openedDate, availableBalance, rs.getBigDecimal("AnnualInterestRate"));
                 case CREDIT:
                     return new CreditAccount(accountId, customer, openedDate, rs.getBigDecimal("CreditLimit"), rs.getBigDecimal("InterestRate"));
                 case CURRENCY:
                     String currencyCode = rs.getString("CurrencyType");
                     return new CurrencyAccount(accountId, customer, openedDate, availableBalance, currencyCode);
                 default:
                     throw new IllegalArgumentException("Unknown account type: " + accountType);
             }
         }

         private AccountList executeQueryList(String query, QueryPreparer preparer) throws AccountRepositoryException {
             var accounts = new AccountList();
             executeQuery(query, preparer, rs -> {
                 while (rs.next()) {
                     accounts.add(mapResultSetToAccount(rs));
                 }
                 return null;
             });
             return accounts;
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
                 throw new AccountRepositoryException("Error executing query: " + query, e);
             }
         }

         private void executeUpdate(String query, QueryPreparer preparer) throws AccountRepositoryException {
             try (Connection conn = dbConnection.getConnection();
                  PreparedStatement stmt = conn.prepareStatement(query)) {
                 preparer.prepare(stmt);
                 stmt.executeUpdate();
             } catch (SQLException | DatabaseConnectionException e) {
                 LOGGER.log(Level.SEVERE, "Error executing update: {0}", e.getMessage());
                 throw new AccountRepositoryException("Error executing update: " + query, e);
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