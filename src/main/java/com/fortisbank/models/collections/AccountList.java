package com.fortisbank.models.collections;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.utils.AccountComparators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Liste pour la gestion des comptes (sorting, filtering)
 */
public class AccountList extends ArrayList<Account> {

    public AccountList() {
        super();
    }

    public AccountList(Iterable<Account> accounts) {
        accounts.forEach(this::add);
    }

    //Sorting

    /**
     * Trie les comptes par solde
     */
    public void sortByBalance() {
        this.sort(AccountComparators.BY_BALANCE);
    }

    /**
     * Trie les comptes par type (Checking, Savings, etc.)
     */
    public void sortByType() {
        this.sort(AccountComparators.BY_TYPE);
    }

    /**
     * Trie les comptes par date de creation
     */
    public void sortByCreatedDate() {
        this.sort(AccountComparators.BY_CREATED_DATE);
    }

    /**
     * Trie les compte par date de creation decroissante
     */
    public void sortByCreatedDateDescending() {
        this.sort(AccountComparators.BY_CREATED_DATE.reversed());
    }

    //Filtering

    /**
     * Filtre les comptes par minimum balance
     * @param minBalance Solde minimum
     * @return Account List filtrer
     */
    public AccountList filterByMinBalance(BigDecimal minBalance) {
        return this.stream()
                .filter(account -> account.getAvailableBalance().compareTo(minBalance) >= 0)
                .collect(Collectors.toCollection(AccountList::new));
    }

    /**
     * Filtre les comptes par type
     * @param type Type de compte
     * @return Account List filtrer
     */
    public AccountList filterByType(String type) {
        return this.stream()
                .filter(account -> account.getAccountType() != null
                        && account.getAccountType().name().equalsIgnoreCase(type))
                .collect(Collectors.toCollection(AccountList::new));
    }

    /**
     * Filtre les comptes actifs
     * @return Account List avec les comptes actifs
     */
    public AccountList filterByActive() {
        return this.stream()
                .filter(Account::isActive)
                .collect(Collectors.toCollection(AccountList::new));
    }

    /**
     * Recupere toutes les transaction de les comptes
     * @return Liste des transactions
     */
    //All transactions from accounts
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (Account account : this) {
            transactions.addAll(account.getTransactions());
        }
        return transactions;
    }

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
