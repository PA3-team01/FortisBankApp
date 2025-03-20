# Fortis Bank `System`

## Updates March 20:

### Refactored Transaction System and Updated Related Repositories

- Modularized `Transaction` by introducing an `abstract class` and `TransactionInterface`.
- Replaced `String transactionType` with `TransactionType` Enum for better type safety.
- Created specialized transaction subclasses: `DepositTransaction`, `WithdrawalTransaction`, `TransferTransaction`, and `FeeTransaction`.
- Implemented `TransactionFactory` to handle dynamic transaction creation.
- Removed `fees` field from `Transaction` as fees are now separate transactions.
- Updated `Account` model:
  - Replaced `String accountType` with `AccountType` Enum.
  - Removed `transactionFees` field (now handled via `FeeTransaction`).
  - Updated `applyFees()` to use `TransactionFactory` for creating fee transactions.
- Refactored `TransactionRepository`:
  - Updated SQL queries to use `TransactionType` Enum.
  - Replaced direct instantiation with `TransactionFactory`.
  - Improved logging with `Logger`.
- Refactored `AccountRepository`:
  - Updated `mapResultSetToAccount()` to instantiate correct account subclasses.
  - Standardized SQL queries and improved error handling.

This refactoring improves modularity, maintainability, and ensures consistency across the banking system.

![image](https://github.com/user-attachments/assets/6a5efe75-3826-45d3-956d-2bd6fbbaed4a)

---

### Refactored Account System with Transaction Integration and Currency Support

- Implemented `AccountInterface` to enforce core account operations.
- Updated `Account` abstract class to:
  - Use transactions for all deposits, withdrawals, and transfers.
  - Ensure all balance modifications are logged with `TransactionFactory`.
  - Standardize fee applications using `applyFees()`.
  - Introduce `closeAccount()` validation to prevent closure with a non-zero balance.

- Refactored all account subclasses:
  - `CheckingAccount`: Now enforces free transaction limits and applies fees after the threshold.
  - `SavingsAccount`: Supports automatic interest application.
  - `CreditAccount`: Implements credit limits and interest application.
  - `CurrencyAccount`: Now integrates with `CurrencyType` for exchange rates.
  - All account types now use `super.deposit()` and `super.withdraw()` to ensure transaction logging.

- Introduced `CurrencyType`:
  - Manages exchange rates with a singleton pattern.
  - Supports adding, updating, and removing currency values dynamically.
  - Ensures accuracy in foreign currency deposits and withdrawals.

- Updated `CurrencyAccount` to:
  - Use `CurrencyType` for real-time exchange rate conversion.
  - Implement currency-specific deposit and withdrawal functionality.
  - Track last activity to enable auto-closing inactive accounts.

- Improved modularity by integrating `AccountFactory`:
  - Supports dynamic account creation based on `AccountType`.
  - Ensures correct parameters are used for each account type.

- Refactored `AccountRepository`:
  - Now uses `AccountFactory` to instantiate accounts.
  - Improved transaction tracking with `recordTransaction()` and `getTransactionsForAccount()`.
  - Integrated `CurrencyType` for accurate exchange rate retrieval in `CurrencyAccount`.

- Updated `IAccountRepository`:
  - Added `recordTransaction(Transaction transaction)` to store transactions related to accounts.
  - Added `getTransactionsForAccount(String accountId)` to retrieve an account’s transaction history.
  - Ensures all repositories support transaction logging and retrieval.

- Improved logging in `AccountRepository` for better debugging.
- Added updated PlantUML class diagram to reflect new architecture.

This update ensures transaction-driven operations for all accounts, enables accurate currency exchange handling, and improves system maintainability.
![image](https://github.com/user-attachments/assets/3dde4cf0-17c2-4093-9e00-952093fc627c)

