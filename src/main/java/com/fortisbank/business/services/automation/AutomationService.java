package com.fortisbank.business.services.automation;

import com.fortisbank.business.services.transaction.TransactionService;
import com.fortisbank.business.services.account.AccountService;
import com.fortisbank.business.utils.DaemonThread;
import com.fortisbank.data.repositories.StorageMode;

import java.util.concurrent.TimeUnit;

public class AutomationService {

    public static void startAllDaemonTasks(StorageMode storageMode) {

        // 1. Interest Application (Daily)
        new DaemonThread(() -> TransactionService
                .getInstance(storageMode)
                .applyMonthlyInterestToAllCreditAccounts(),
                TimeUnit.DAYS.toMillis(30)).start();

        new DaemonThread(() -> TransactionService
                .getInstance(storageMode)
                .applyAnnualInterestToAllSavingsAccounts(),
                TimeUnit.DAYS.toMillis(365)).start();


        // 2. Auto-close inactive currency accounts (Daily)
        new DaemonThread(() -> AccountService
                .getInstance(storageMode)
                .autoCloseInactiveCurrencyAccounts(),
                TimeUnit.DAYS.toMillis(1)).start();


        // 3. Low Balance Alerts (Every 15 minutes)
        new DaemonThread(() -> AccountService
                .getInstance(storageMode)
                .checkLowBalanceAndNotify(),
                TimeUnit.MINUTES.toMillis(15)).start();


        // 15. Fraud Detection (Hourly)
        new DaemonThread(() -> TransactionService
                .getInstance(storageMode)
                .scanForSuspiciousActivity(),
                TimeUnit.HOURS.toMillis(1)).start();

        // 4. Monthly Statement Generation (Monthly)
        // new DaemonThread(() -> StatementService.generateMonthlyStatements(), TimeUnit.DAYS.toMillis(30)).start();


        // 8. Archive Old Transactions (Monthly)
        // new DaemonThread(() -> ArchiveService.archiveOldTransactions(), TimeUnit.DAYS.toMillis(30)).start();

        // 9. Auto-Approval for Low-Risk Account Requests (Every 5 minutes)
        // new DaemonThread(() -> AccountApprovalService.autoApproveLowRiskRequests(), TimeUnit.MINUTES.toMillis(5)).start();

        // 10. Exchange Rate Updates (Hourly)
        // new DaemonThread(() -> CurrencyService.updateExchangeRates(), TimeUnit.HOURS.toMillis(1)).start();

        // 12. Cleanup Expired/Orphaned Data (Daily)
        // new DaemonThread(() -> CleanupService.removeExpiredOrphanedData(), TimeUnit.DAYS.toMillis(1)).start();

    }
}