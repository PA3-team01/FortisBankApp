package com.fortisbank.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

    public abstract class Account {

        private String accountNumber;
        private String accountType;
        private Date openedDate;
        private BigDecimal availableBalance;
        private List<Transaction> transactions;
        private BigDecimal transactionFees;
        private boolean isActive; // BOOL FOR ACCOUNT STATUS ACTIVE/CLOSE

        // CONSTRUCTOR

        public Account(String accountNumber, String accountType, Date openedDate, BigDecimal initialBalance) {
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.openedDate = openedDate;
            this.availableBalance = initialBalance;
            this.transactions = new ArrayList<>();
            this.transactionFees = BigDecimal.ZERO; // INITIAL TRANSACTION FEE = 0
            this.isActive = true; // DEFAULT TRUE
        }

        // Get = set
        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public Date getOpenedDate() {
            return openedDate;
        }

        public void setOpenedDate(Date openedDate) {
            this.openedDate = openedDate;
        }

        public BigDecimal getAvailableBalance() {
            return availableBalance;
        }

        public void setAvailableBalance(BigDecimal availableBalance) {
            this.availableBalance = availableBalance;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        public BigDecimal getTransactionFees() {
            return transactionFees;
        }

        public void setTransactionFees(BigDecimal transactionFees) {
            this.transactionFees = transactionFees;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }



       //RETURN THE AVAILABLE SOLD

        public BigDecimal viewBalance() {
            return availableBalance;
        }

        // ADD THE TRANSACTION TO THE LIST OF TRANSACTION OF THE ACCOUNT

        public void addTransaction(Transaction transaction) {
            this.transactions.add(transaction);
        }

        // FEES

        public void applyFees(BigDecimal fees) {
            if (availableBalance.compareTo(fees) >= 0) {
                availableBalance = availableBalance.subtract(fees);
                transactionFees = transactionFees.add(fees);
                // TO DO = FEES VERIFICATION POSSIBLE ERROR WHEN DOING OTHER CLASS(Transaction)?

                addTransaction(new Transaction("FEE", fees)); // Add a transaction for the fees
            }
            else {
                throw new IllegalArgumentException("Insufficient balance to apply fees.");
            }
        }

        //ACCOUNT CLOSURE

        public void closeAccount() {
            if (availableBalance.compareTo(BigDecimal.ZERO) == 0) {
                isActive = false;
                System.out.println("Account " + accountNumber + " have been closed.");
            }
            else {
                throw new IllegalStateException("Unable to close the account: the balance is not zero.");
            }
        }

        //ABSTRACT METHODE FOR ACCOUNT SPECIFIC OPERATIONS

        public abstract void deposit(BigDecimal amount);
        public abstract void withdraw(BigDecimal amount, BigDecimal fees);
        public abstract void transfer(Account targetAccount, BigDecimal amount);
    }