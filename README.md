# Fortis Bank `System`

## Updates March 20:
Refactored Transaction System and Updated Related Repositories

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
