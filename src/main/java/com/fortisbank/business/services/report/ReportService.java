package com.fortisbank.business.services.report;

    import com.fortisbank.data.interfaces.IAccountRepository;
    import com.fortisbank.data.interfaces.ICustomerRepository;
    import com.fortisbank.data.interfaces.ITransactionRepository;
    import com.fortisbank.data.dal_utils.RepositoryFactory;
    import com.fortisbank.contracts.models.accounts.Account;
    import com.fortisbank.contracts.models.accounts.AccountType;
    import com.fortisbank.contracts.collections.AccountList;
    import com.fortisbank.contracts.collections.TransactionList;
    import com.fortisbank.contracts.models.reports.BankSummaryReport;
    import com.fortisbank.contracts.models.reports.CustomerStatementReport;
    import com.fortisbank.contracts.models.transactions.Transaction;
    import com.fortisbank.contracts.models.users.Customer;
    import com.fortisbank.business.bll_utils.ReportExporter;

    import java.io.IOException;
    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.YearMonth;
    import java.util.Map;
    import java.util.logging.Level;
    import java.util.logging.Logger;
    import java.util.stream.Collectors;

    /**
     * Service class for generating various reports.
     */
    public class ReportService {

        private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

        private final ICustomerRepository customerRepository;
        private final IAccountRepository accountRepository;
        private final ITransactionRepository transactionRepository;

        /**
         * Constructs a ReportService with the given repository factory.
         *
         * @param factory the repository factory
         */
        public ReportService(RepositoryFactory factory) {
            this.customerRepository = factory.getCustomerRepository();
            this.accountRepository = factory.getAccountRepository();
            this.transactionRepository = factory.getTransactionRepository();
        }

        public CustomerStatementReport generateCustomerStatement(Customer customer, YearMonth month) {
            try {
                LocalDate start = month.atDay(1);
                LocalDate end = month.atEndOfMonth();

                TransactionList transactions = transactionRepository
                        .getTransactionsByCustomerAndDateRange(customer.getUserId(), start, end);

                BigDecimal openingBalance = transactionRepository.getBalanceBeforeDate(customer.getUserId(), start);

                AccountList customerAccounts = accountRepository.getAccountsByCustomerId(customer.getUserId());

                BigDecimal closingBalance = openingBalance;

                for (Transaction t : transactions) {
                    for (Account acc : customerAccounts) {
                        BigDecimal signed = t.getSignedAmountFor(acc);
                        if (signed.compareTo(BigDecimal.ZERO) != 0) {
                            closingBalance = closingBalance.add(signed);
                            break;
                        }
                    }
                }

                return new CustomerStatementReport(customer, transactions, openingBalance, closingBalance, start, end);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error generating customer statement: {0}", e.getMessage());
                throw new RuntimeException("Failed to generate customer statement", e);
            }
        }

        public BankSummaryReport generateBankSummaryReport() {
            try {
                var customers = customerRepository.getAllCustomers();
                var accounts = accountRepository.getAllAccounts();
                var transactions = transactionRepository.getAllTransactions();

                Map<String, Long> accountTypeCounts = accounts.stream()
                        .collect(Collectors.groupingBy(
                                acc -> acc.getAccountType().name(),
                                Collectors.counting()
                        ));

                BigDecimal totalBalance = accounts.stream()
                        .map(Account::getAvailableBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalCreditUsed = accounts.stream()
                        .filter(acc -> acc.getAccountType() == AccountType.CREDIT)
                        .map(Account::getCreditLimit)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalFees = transactions.stream()
                        .filter(t -> t.getTransactionType() == com.fortisbank.contracts.models.transactions.TransactionType.FEE)
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                AccountList lowBalanceAccounts = new AccountList(
                        accounts.stream()
                                .filter(acc -> acc.getAvailableBalance().compareTo(new BigDecimal("50")) < 0)
                                .collect(Collectors.toList())
                );

                return new BankSummaryReport(
                        customers.size(),
                        accounts.size(),
                        accountTypeCounts,
                        totalBalance,
                        totalCreditUsed,
                        totalFees,
                        transactions,
                        lowBalanceAccounts
                );
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error generating bank summary report: {0}", e.getMessage());
                throw new RuntimeException("Failed to generate bank summary report", e);
            }
        }

        public void saveCustomerStatementReportToCSV(CustomerStatementReport report, String filePath) {
            try {
                AccountList customerAccounts = accountRepository.getAccountsByCustomerId(report.getCustomer().getUserId());
                ReportExporter.exportCustomerStatementToCSV(report, filePath, customerAccounts);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error exporting customer report: {0}", e.getMessage());
                throw new RuntimeException("Failed to export customer report", e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error exporting customer report: {0}", e.getMessage());
                throw new RuntimeException("Unexpected error exporting customer report", e);
            }
        }

        public void saveBankSummaryReportToCSV(BankSummaryReport report, String filePath) {
            try {
                ReportExporter.exportBankSummaryToCSV(report, filePath);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error exporting bank report: {0}", e.getMessage());
                throw new RuntimeException("Failed to export bank report", e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error exporting bank report: {0}", e.getMessage());
                throw new RuntimeException("Unexpected error exporting bank report", e);
            }
        }
    }