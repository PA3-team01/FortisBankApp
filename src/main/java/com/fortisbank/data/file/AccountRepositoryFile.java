package com.fortisbank.data.file;

        import com.fortisbank.data.interfaces.IAccountRepository;
        import com.fortisbank.contracts.exceptions.AccountRepositoryException;
        import com.fortisbank.contracts.models.accounts.Account;
        import com.fortisbank.contracts.collections.AccountList;

        import java.io.File;
        import java.util.List;
        import java.util.logging.Level;
        import java.util.logging.Logger;

        /**
         * Repository class for managing account data stored in a file.
         * Extends the FileRepository class and implements the IAccountRepository interface.
         */
        public class AccountRepositoryFile extends FileRepository<Account> implements IAccountRepository {
            private static final Logger LOGGER = Logger.getLogger(AccountRepositoryFile.class.getName());
            private static final File file = new File("data/accounts.ser"); // File to store account data
            private static AccountRepositoryFile instance; // Singleton instance

            /**
             * Private constructor to prevent direct instantiation.
             * Initializes the repository with the specified file.
             */
            private AccountRepositoryFile() {
                super(file);
            }

            /**
             * Returns the singleton instance of AccountRepositoryFile.
             * Synchronized to prevent multiple threads from creating multiple instances.
             *
             * @return the singleton instance of AccountRepositoryFile
             */
            public static synchronized AccountRepositoryFile getInstance() {
                if (instance == null) {
                    instance = new AccountRepositoryFile();
                }
                return instance;
            }

            @Override
            public Account getAccountById(String accountId) throws AccountRepositoryException {
                return executeQuery(accounts -> accounts.stream()
                        .filter(a -> a.getAccountNumber().equals(accountId))
                        .findFirst()
                        .orElse(null), "Error retrieving account with ID: " + accountId);
            }

            @Override
            public AccountList getAccountsByCustomerId(String customerId) throws AccountRepositoryException {
                return executeQuery(accounts -> {
                    AccountList result = new AccountList();
                    accounts.stream()
                            .filter(a -> a.getCustomer() != null && customerId.equals(a.getCustomer().getUserId()))
                            .forEach(result::add);
                    return result;
                }, "Error retrieving accounts for customer ID: " + customerId);
            }

            @Override
            public AccountList getAllAccounts() throws AccountRepositoryException {
                return executeQuery(AccountList::new, "Error retrieving all accounts");
            }

            @Override
            public void insertAccount(Account account) throws AccountRepositoryException {
                executeUpdate(accounts -> accounts.add(account), "Error inserting account");
            }

            @Override
            public void updateAccount(Account account) throws AccountRepositoryException {
                executeUpdate(accounts -> {
                    for (int i = 0; i < accounts.size(); i++) {
                        if (accounts.get(i).getAccountNumber().equals(account.getAccountNumber())) {
                            accounts.set(i, account);
                            return;
                        }
                    }
                }, "Error updating account");
            }

            @Override
            public void deleteAccount(String accountId) throws AccountRepositoryException {
                executeUpdate(accounts -> accounts.removeIf(a -> a.getAccountNumber().equals(accountId)), "Error deleting account with ID: " + accountId);
            }

            private <T> T executeQuery(QueryFunction<List<Account>, T> function, String errorMessage) throws AccountRepositoryException {
                try {
                    List<Account> accounts = readAll();
                    return function.apply(accounts);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, errorMessage, e);
                    throw new AccountRepositoryException(errorMessage, e);
                }
            }

            private void executeUpdate(UpdateFunction<List<Account>> function, String errorMessage) throws AccountRepositoryException {
                try {
                    List<Account> accounts = readAll();
                    function.apply(accounts);
                    writeAll(accounts);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, errorMessage, e);
                    throw new AccountRepositoryException(errorMessage, e);
                }
            }

            @FunctionalInterface
            private interface QueryFunction<T, R> {
                R apply(T t) throws Exception;
            }

            @FunctionalInterface
            private interface UpdateFunction<T> {
                void apply(T t) throws Exception;
            }
        }