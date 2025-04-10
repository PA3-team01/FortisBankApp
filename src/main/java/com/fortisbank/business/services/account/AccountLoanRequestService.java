package com.fortisbank.business.services.account;

        import com.fortisbank.business.services.users.customer.CustomerService;
        import com.fortisbank.business.services.notification.NotificationService;
        import com.fortisbank.data.dal_utils.StorageMode;
        import com.fortisbank.contracts.models.accounts.Account;
        import com.fortisbank.contracts.collections.AccountList;
        import com.fortisbank.contracts.models.users.BankManager;
        import com.fortisbank.contracts.models.users.Customer;

        import java.util.EnumMap;
        import java.util.Map;
        import java.util.logging.Level;
        import java.util.logging.Logger;

        /**
         * Handles customer account opening requests and manager decisions.
         */
        public class AccountLoanRequestService {

            private static final Logger LOGGER = Logger.getLogger(AccountLoanRequestService.class.getName());
            private static final Map<StorageMode, AccountLoanRequestService> instances = new EnumMap<>(StorageMode.class);
            private NotificationService notificationService;
            private final AccountService accountService;
            private final StorageMode storageMode;

            private AccountLoanRequestService(StorageMode storageMode) {
                this.storageMode = storageMode;
                this.accountService = AccountService.getInstance(storageMode);
                this.notificationService = NotificationService.getInstance(storageMode);
            }

            /**
             * Returns the singleton instance of AccountLoanRequestService for the given storage mode.
             *
             * @param storageMode the storage mode
             * @return the singleton instance of AccountLoanRequestService
             */
            public static synchronized AccountLoanRequestService getInstance(StorageMode storageMode) {
                return instances.computeIfAbsent(storageMode, AccountLoanRequestService::new);
            }

            /**
             * Customer submits a request for a new account.
             *
             * @param customer the customer submitting the request
             * @param requestedAccount the requested account
             * @param manager the bank manager
             */
            public void submitAccountRequest(Customer customer, Account requestedAccount, BankManager manager) {
                try {
                    if (customer == null || requestedAccount == null || manager == null) {
                        throw new IllegalArgumentException("Customer, requested account, and manager must not be null.");
                    }

                    accountService.createAccount(requestedAccount);
                    fillCustomerAccountList(customer);
                    notificationService.notifyAccountRequest(manager, customer, requestedAccount);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error submitting account request: {0}", e.getMessage());
                    throw new RuntimeException("Failed to submit account request", e);
                }
            }

            /**
             * Manager accepts the request and account is created.
             *
             * @param customer the customer whose request is accepted
             * @param account the account to be created
             */
            public void acceptAccountRequest(Customer customer, Account account) {
                try {
                    if (customer == null || account == null) {
                        throw new IllegalArgumentException("Customer and account must not be null.");
                    }

                    fillCustomerAccountList(customer);
                    if (customer.getAccounts().contains(account) && !account.isActive()) {
                        account.setActive(true);
                        accountService.updateAccount(account);
                        CustomerService.getInstance(storageMode).updateCustomer(customer);
                        notificationService.notifyApproval(customer, account);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error accepting account request: {0}", e.getMessage());
                    throw new RuntimeException("Failed to accept account request", e);
                }
            }

            /**
             * Manager rejects the account request.
             *
             * @param customer the customer whose request is rejected
             * @param reason the reason for rejection
             * @param rejectedAccount the account that was rejected
             */
            public void rejectAccountRequest(Customer customer, String reason, Account rejectedAccount) {
                try {
                    if (customer == null || rejectedAccount == null) {
                        throw new IllegalArgumentException("Customer and rejected account must not be null.");
                    }

                    fillCustomerAccountList(customer);
                    if (customer.getAccounts().contains(rejectedAccount)) {
                        customer.getAccounts().remove(rejectedAccount);
                        CustomerService.getInstance(storageMode).updateCustomer(customer);
                        accountService.deleteAccount(rejectedAccount.getAccountNumber());
                        notificationService.notifyRejection(customer, reason, rejectedAccount);
                    } else {
                        notificationService.notifyRejection(customer, "There was a problem with your request. Please fill a new form.", rejectedAccount);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error rejecting account request: {0}", e.getMessage());
                    throw new RuntimeException("Failed to reject account request", e);
                }
            }

            /**
             * Utility method to fill the customer's account list.
             *
             * @param customer the customer whose account list is to be filled
             */
            private void fillCustomerAccountList(Customer customer) {
                try {
                    AccountList accounts = accountService.getAccountsByCustomerId(customer.getUserId());
                    if (accounts == null) accounts = new AccountList();
                    customer.setAccounts(accounts);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error filling customer account list: {0}", e.getMessage());
                    throw new RuntimeException("Failed to fill customer account list", e);
                }
            }
        }