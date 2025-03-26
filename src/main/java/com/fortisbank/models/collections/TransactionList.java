package com.fortisbank.models.collections;

import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.utils.TransactionComparators;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransactionList extends ArrayList<Transaction> {

    public TransactionList() {
        super();
    }

    public TransactionList(Iterable<Transaction> transactions) {
        transactions.forEach(this::add);
    }

    // Sorting methods
    public void sortByDateDescending() {
        this.sort(TransactionComparators.BY_DATE.reversed());  // ✅ Now works correctly
    }

    public void sortByAmountAscending() {
        this.sort(TransactionComparators.BY_AMOUNT);
    }

    public void sortByType() {
        this.sort(TransactionComparators.BY_TYPE);
    }

    // Filtering methods
    public TransactionList filterByMinAmount(BigDecimal minAmount) {
        return this.stream()
                .filter(t -> t.getAmount().compareTo(minAmount) >= 0)
                .collect(Collectors.toCollection(TransactionList::new));
    }

    public TransactionList filterByType(String type) {
        return this.stream()
                .filter(t -> t.getTransactionType().name().equalsIgnoreCase(type))
                .collect(Collectors.toCollection(TransactionList::new));
    }

    public TransactionList filterByDateRange(Date start, Date end) {
        return this.stream()
                .filter(t -> !t.getTransactionDate().before(start) && !t.getTransactionDate().after(end))
                .collect(Collectors.toCollection(TransactionList::new));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TransactionList: ").append(this.size()).append(" transactions\n");
        for (Transaction transaction : this) {
            builder.append("  - ").append(transaction.toString()).append("\n");
        }
        return builder.toString();
    }
}
