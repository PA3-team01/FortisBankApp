package com.fortisbank.business.services.automation;

import com.fortisbank.business.services.transaction.TransactionService;
import com.fortisbank.business.services.account.AccountService;
import com.fortisbank.business.bll_utils.DaemonThread;
import com.fortisbank.data.dal_utils.StorageMode;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutomationService {
    private static final Logger LOGGER = Logger.getLogger(AutomationService.class.getName());

    /**
     * Starts all daemon tasks for the given storage mode.
     *
     * @param storageMode the storage mode
     */
    public static void startAllDaemonTasks(StorageMode storageMode) {

        // 1. Interest Application (Daily)
        new DaemonThread(() -> {
            try {
                TransactionService.getInstance(storageMode).applyMonthlyInterestToAllCreditAccounts();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error applying monthly interest to credit accounts: {0}", e.getMessage());
            }
        }, TimeUnit.DAYS.toMillis(30)).start();

        new DaemonThread(() -> {
            try {
                TransactionService.getInstance(storageMode).applyAnnualInterestToAllSavingsAccounts();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error applying annual interest to savings accounts: {0}", e.getMessage());
            }
        }, TimeUnit.DAYS.toMillis(365)).start();

        // 2. Auto-close inactive currency accounts (Daily)
        new DaemonThread(() -> {
            try {
                AccountService.getInstance(storageMode).autoCloseInactiveCurrencyAccounts();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error auto-closing inactive currency accounts: {0}", e.getMessage());
            }
        }, TimeUnit.DAYS.toMillis(1)).start();

        // 3. Low Balance Alerts (Every 15 minutes)
        new DaemonThread(() -> {
            try {
                AccountService.getInstance(storageMode).checkLowBalanceAndNotify();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error checking low balance and notifying: {0}", e.getMessage());
            }
        }, TimeUnit.MINUTES.toMillis(15)).start();

        // 4. Fraud Detection (Hourly)
        new DaemonThread(() -> {
            try {
                TransactionService.getInstance(storageMode).scanForSuspiciousActivity();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error scanning for suspicious activity: {0}", e.getMessage());
            }
        }, TimeUnit.HOURS.toMillis(1)).start();

        // Uncomment and implement the following tasks as needed:
        // 5. Monthly Statement Generation (Monthly)
        // new DaemonThread(() -> {
        //     try {
        //         StatementService.generateMonthlyStatements();
        //     } catch (Exception e) {
        //         LOGGER.log(Level.SEVERE, "Error generating monthly statements: {0}", e.getMessage());
        //     }
        // }, TimeUnit.DAYS.toMillis(30)).start();

        // 6. Archive Old Transactions (Monthly)
        // new DaemonThread(() -> {
        //     try {
        //         ArchiveService.archiveOldTransactions();
        //     } catch (Exception e) {
        //         LOGGER.log(Level.SEVERE, "Error archiving old transactions: {0}", e.getMessage());
        //     }
        // }, TimeUnit.DAYS.toMillis(30)).start();

        // 7. Auto-Approval for Low-Risk Account Requests (Every 5 minutes)
        // new DaemonThread(() -> {
        //     try {
        //         AccountApprovalService.autoApproveLowRiskRequests();
        //     } catch (Exception e) {
        //         LOGGER.log(Level.SEVERE, "Error auto-approving low-risk account requests: {0}", e.getMessage());
        //     }
        // }, TimeUnit.MINUTES.toMillis(5)).start();

        // 8. Exchange Rate Updates (Hourly)
        // new DaemonThread(() -> {
        //     try {
        //         CurrencyService.updateExchangeRates();
        //     } catch (Exception e) {
        //         LOGGER.log(Level.SEVERE, "Error updating exchange rates: {0}", e.getMessage());
        //     }
        // }, TimeUnit.HOURS.toMillis(1)).start();

        // 9. Cleanup Expired/Orphaned Data (Daily)
        // new DaemonThread(() -> {
        //     try {
        //         CleanupService.removeExpiredOrphanedData();
        //     } catch (Exception e) {
        //         LOGGER.log(Level.SEVERE, "Error cleaning up expired/orphaned data: {0}", e.getMessage());
        //     }
        // }, TimeUnit.DAYS.toMillis(1)).start();
    }
}