package com.fortisbank.models.collections;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.utils.AccountComparators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * List for managing accounts (sorting, filtering).
 */
public class AccountList extends ArrayList<Account> {

    /**
     * Default constructor initializing an empty account list.
     */
    public AccountList() {
        super();
    }

    /**
     * Constructor initializing the account list with a collection of accounts.
     *
     * @param accounts an iterable collection of accounts to add to the list
     */
    public AccountList(Iterable<Account> accounts) {
        accounts.forEach(this::add);
    }

    // Sorting

    /**
     * Sorts the accounts by balance.
     */
    public void sortByBalance() {
        this.sort(AccountComparators.BY_BALANCE);
    }

    /**
     * Sorts the accounts by type (Checking, Savings, etc.).
     */
    public void sortByType() {
        this.sort(AccountComparators.BY_TYPE);
    }

    /**
     * Sorts the accounts by creation date.
     */
    public void sortByCreatedDate() {
        this.sort(AccountComparators.BY_CREATED_DATE);
    }

    /**
     * Sorts the accounts by creation date in descending order.
     */
    public void sortByCreatedDateDescending() {
        this.sort(AccountComparators.BY_CREATED_DATE.reversed());
    }

    // Filtering

    /**
     * Filters the accounts by minimum balance.
     *
     * @param minBalance the minimum balance to filter by
     * @return a filtered account list
     */
    public AccountList filterByMinBalance(BigDecimal minBalance) {
        return this.stream()
                .filter(account -> account.getAvailableBalance().compareTo(minBalance) >= 0)
                .collect(Collectors.toCollection(AccountList::new));
    }

    /**
     * Filters the accounts by type.
     *
     * @param type the type of account to filter by
     * @return a filtered account list
     */
    public AccountList filterByType(String type) {
        return this.stream()
                .filter(account -> account.getAccountType() != null
                        && account.getAccountType().name().equalsIgnoreCase(type))
                .collect(Collectors.toCollection(AccountList::new));
    }

    /**
     * Filters the active accounts.
     *
     * @return an account list with active accounts
     */
    public AccountList filterByActive() {
        return this.stream()
                .filter(Account::isActive)
                .collect(Collectors.toCollection(AccountList::new));
    }

    /**
     * Retrieves all transactions from the accounts.
     *
     * @return a list of transactions
     */
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (Account account : this) {
            transactions.addAll(account.getTransactions());
        }
        return transactions;
    }

    /**
     * Returns a string representation of the account list.
     *
     * @return a string containing account list information
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccountList: ").append(this.size()).append(" accounts\n");
        for (Account account : this) {
            builder.append("  - ").append(account.toString()).append("\n");
        }
        return builder.toString();
    }
}