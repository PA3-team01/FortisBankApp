package com.fortisbank.data.repositories;

    import com.fortisbank.data.file.FileRepository;
    import com.fortisbank.exceptions.TransactionRepositoryException;
    import com.fortisbank.models.accounts.Account;
    import com.fortisbank.models.collections.TransactionList;
    import com.fortisbank.models.transactions.Transaction;

    import java.io.File;
    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.ZoneId;
    import java.util.List;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * Repository class for managing transactions in a file.
     * Implements the ITransactionRepository interface.
     */
    public class TransactionRepositoryFile extends FileRepository<Transaction> implements ITransactionRepository {
        private static final Logger LOGGER = Logger.getLogger(TransactionRepositoryFile.class.getName());
        private static final File file = new File("data/transactions.ser");
        private static TransactionRepositoryFile instance;

        private TransactionRepositoryFile() {
            super(file);
        }

        public static synchronized TransactionRepositoryFile getInstance() {
            if (instance == null) {
                instance = new TransactionRepositoryFile();
            }
            return instance;
        }

        @Override
        public Transaction getTransactionByNumber(String transactionNumber) throws TransactionRepositoryException {
            try {
                return readAllTransactions().stream()
                        .filter(t -> t.getTransactionNumber().equals(transactionNumber))
                        .findFirst()
                        .orElse(null);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error retrieving transaction by number: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error retrieving transaction by number: " + transactionNumber, e);
            }
        }

        @Override
        public TransactionList getTransactionsByAccount(String accountId) throws TransactionRepositoryException {
            try {
                return readAllTransactions().stream()
                        .filter(t -> {
                            Account sourceAccount = t.getSourceAccount();
                            return (sourceAccount != null && sourceAccount.getAccountNumber().equals(accountId))
                                    || (t.getDestinationAccount() != null
                                    && t.getDestinationAccount().getAccountNumber().equals(accountId));
                        })
                        .collect(TransactionList::new, TransactionList::add, TransactionList::addAll);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error retrieving transactions by account: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error retrieving transactions by account: " + accountId, e);
            }
        }

        @Override
        public TransactionList getAllTransactions() throws TransactionRepositoryException {
            try {
                return new TransactionList(readAllTransactions());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error retrieving all transactions: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error retrieving all transactions", e);
            }
        }

        @Override
        public void insertTransaction(Transaction transaction) throws TransactionRepositoryException {
            try {
                var transactions = readAllTransactions();
                transactions.add(transaction);
                writeAllTransactions(transactions);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error inserting transaction: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error inserting transaction", e);
            }
        }

        @Override
        public void deleteTransaction(String transactionNumber) throws TransactionRepositoryException {
            try {
                var transactions = readAllTransactions();
                transactions.removeIf(t -> t.getTransactionNumber().equals(transactionNumber));
                writeAllTransactions(transactions);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting transaction: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error deleting transaction: " + transactionNumber, e);
            }
        }

        @Override
        public TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end) throws TransactionRepositoryException {
            ZoneId zone = ZoneId.systemDefault();
            try {
                return readAllTransactions().stream()
                        .filter(t -> {
                            Account sourceAccount = t.getSourceAccount();
                            if (sourceAccount == null) {
                                return false;
                            }
                            String transactionCustomerID = sourceAccount.getCustomer().getUserId();
                            LocalDate transactionDate = t.getTransactionDate().toInstant().atZone(zone).toLocalDate();
                            return transactionCustomerID.equals(customerID) &&
                                    (!transactionDate.isBefore(start) && !transactionDate.isAfter(end));
                        })
                        .collect(TransactionList::new, TransactionList::add, TransactionList::addAll);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error retrieving transactions by customer and date range: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error retrieving transactions by customer and date range", e);
            }
        }

        @Override
        public BigDecimal getBalanceBeforeDate(String customerID, LocalDate start) throws TransactionRepositoryException {
            ZoneId zone = ZoneId.systemDefault();
            try {
                return readAllTransactions().stream()
                        .filter(t -> {
                            String transactionCustomerID = t.getSourceAccount().getCustomer().getUserId();
                            LocalDate transactionDate = t.getTransactionDate().toInstant().atZone(zone).toLocalDate();
                            return transactionCustomerID.equals(customerID) && transactionDate.isBefore(start);
                        })
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error retrieving balance before date: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error retrieving balance before date", e);
            }
        }

        private List<Transaction> readAllTransactions() throws TransactionRepositoryException {
            try {
                return readAll();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error reading transactions from file: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error reading transactions from file", e);
            }
        }

        private void writeAllTransactions(List<Transaction> transactions) throws TransactionRepositoryException {
            try {
                writeAll(transactions);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error writing transactions to file: {0}", e.getMessage());
                throw new TransactionRepositoryException("Error writing transactions to file", e);
            }
        }
    }