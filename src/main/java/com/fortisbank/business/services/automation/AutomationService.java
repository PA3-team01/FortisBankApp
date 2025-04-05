package com.fortisbank.business.services.automation;

public class AutomationService {

    public static void startAllDaemonTasks() {

        // 1. Interest Application (Daily)
        // new DaemonThread(() -> InterestService.applyMonthlyInterest(), TimeUnit.DAYS.toMillis(1)).start();

        // 2. Inactive Currency Account Auto-Closure (Daily)
        // new DaemonThread(() -> AccountService.closeInactiveCurrencyAccounts(), TimeUnit.DAYS.toMillis(1)).start();

        // 3. Chequing Account Monthly Fees (Monthly)
        // new DaemonThread(() -> FeeService.applyChequingFees(), TimeUnit.DAYS.toMillis(30)).start();

        // 4. Monthly Statement Generation (Monthly)
        // new DaemonThread(() -> StatementService.generateMonthlyStatements(), TimeUnit.DAYS.toMillis(30)).start();

        // 5. Low Balance Alerts (Every 15 minutes)
        // new DaemonThread(() -> AlertService.checkLowBalances(), TimeUnit.MINUTES.toMillis(15)).start();

        // 6. Notification Dispatcher (Every 10 seconds)
        // new DaemonThread(() -> NotificationService.dispatchPendingNotifications(), TimeUnit.SECONDS.toMillis(10)).start();

        // 7. Fraud Detection Task (Hourly)
        // new DaemonThread(() -> FraudDetectionService.scanForSuspiciousActivity(), TimeUnit.HOURS.toMillis(1)).start();

        // 8. Archive Old Transactions (Monthly)
        // new DaemonThread(() -> ArchiveService.archiveOldTransactions(), TimeUnit.DAYS.toMillis(30)).start();

        // 9. Auto-Approval for Low-Risk Account Requests (Every 5 minutes)
        // new DaemonThread(() -> AccountApprovalService.autoApproveLowRiskRequests(), TimeUnit.MINUTES.toMillis(5)).start();

        // 10. Exchange Rate Updates (Hourly)
        // new DaemonThread(() -> CurrencyService.updateExchangeRates(), TimeUnit.HOURS.toMillis(1)).start();

        // 11. Account Deletion Eligibility Scan (Daily)
        // new DaemonThread(() -> ClientService.checkAccountClosureEligibility(), TimeUnit.DAYS.toMillis(1)).start();

        // 12. Cleanup Expired/Orphaned Data (Daily)
        // new DaemonThread(() -> CleanupService.removeExpiredOrphanedData(), TimeUnit.DAYS.toMillis(1)).start();

    }
}