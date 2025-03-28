package com.fortisbank.data.repositories;

        import java.util.EnumMap;
        import java.util.Map;

        public class RepositoryFactory {

            /**
             * The RepositoryFactory class is designed to provide a single instance of repository objects
             * based on the specified storage mode (FILE or DATABASE). It uses the Singleton pattern to
             * ensure that only one instance of RepositoryFactory exists per StorageMode.
             *
             * - The `instances` map holds the RepositoryFactory instances, keyed by StorageMode.
             * - The constructor is private to prevent direct instantiation.
             * - The `getInstance` method returns the existing instance for the given StorageMode or creates a new one if it doesn't exist.
             * - The `getCustomerRepository`, `getAccountRepository`, and `getTransactionRepository` methods return the appropriate repository
             *   instance based on the current storage mode.
             */
            private static final Map<StorageMode, RepositoryFactory> instances = new EnumMap<>(StorageMode.class);
            private final StorageMode mode;

            private RepositoryFactory(StorageMode mode) {
                this.mode = mode;
            }

            public static synchronized RepositoryFactory getInstance(StorageMode mode) {
                return instances.computeIfAbsent(mode, RepositoryFactory::new);
            }

            public ICustomerRepository getCustomerRepository() {
                return switch (mode) {
                    case FILE -> CustomerRepositoryFile.getInstance();
                    case DATABASE -> CustomerRepository.getInstance();
                };
            }

            public IAccountRepository getAccountRepository() {
                return switch (mode) {
                    case FILE -> AccountRepositoryFile.getInstance();
                    case DATABASE -> AccountRepository.getInstance();
                };
            }

            public ITransactionRepository getTransactionRepository() {
                return switch (mode) {
                    case FILE -> TransactionRepositoryFile.getInstance();
                    case DATABASE -> TransactionRepository.getInstance();
                };
            }
        }