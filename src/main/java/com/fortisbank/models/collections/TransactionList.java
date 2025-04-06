package com.fortisbank.models.collections;

import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.utils.TransactionComparators;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A typed list for managing Transaction objects.
 * Provides sorting and filtering methods for transactions.
 */
public class TransactionList extends ArrayList<Transaction> {

    /**
     * Default constructor initializing an empty transaction list.
     */
    public TransactionList() {
        super();
    }

    /**
     * Constructor initializing the transaction list with a collection of transactions.
     *
     * @param transactions an iterable collection of transactions to add to the list
     */
    public TransactionList(Iterable<Transaction> transactions) {
        transactions.forEach(this::add);
    }

    // Sorting methods

    /**
     * Sorts the transactions by date in descending order.
     */
    public void sortByDateDescending() {
        this.sort(TransactionComparators.BY_DATE.reversed());
    }

    /**
     * Sorts the transactions by amount in ascending order.
     */
    public void sortByAmountAscending() {
        this.sort(TransactionComparators.BY_AMOUNT);
    }

    /**
     * Sorts the transactions by type.
     */
    public void sortByType() {
        this.sort(TransactionComparators.BY_TYPE);
    }

    // Filtering methods

    /**
     * Filters the transactions by minimum amount.
     *
     * @param minAmount the minimum amount to filter by
     * @return a filtered transaction list
     */
    public TransactionList filterByMinAmount(BigDecimal minAmount) {
        return this.stream()
                .filter(t -> t.getAmount().compareTo(minAmount) >= 0)
                .collect(Collectors.toCollection(TransactionList::new));
    }

    /**
     * Filters the transactions by type.
     *
     * @param type the type of transaction to filter by
     * @return a filtered transaction list
     */
    public TransactionList filterByType(String type) {
        return this.stream()
                .filter(t -> t.getTransactionType().name().equalsIgnoreCase(type))
                .collect(Collectors.toCollection(TransactionList::new));
    }

    /**
     * Filters the transactions by date range.
     *
     * @param start the start date
     * @param end the end date
     * @return a filtered transaction list
     */
    public TransactionList filterByDateRange(Date start, Date end) {
        return this.stream()
                .filter(t -> !t.getTransactionDate().before(start) && !t.getTransactionDate().after(end))
                .collect(Collectors.toCollection(TransactionList::new));
    }

    /**
     * Filters the transactions by month.
     *
     * @param targetDate the target date
     * @return a filtered transaction list
     */
    public TransactionList filterByMonth(LocalDate targetDate) {
        int year = targetDate.getYear();
        int month = targetDate.getMonthValue();

        return this.stream()
                .filter(tx -> {
                    LocalDate txDate = tx.getTransactionDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return txDate.getYear() == year && txDate.getMonthValue() == month;
                })
                .collect(Collectors.toCollection(TransactionList::new));
    }

    /**
     * Filters the transactions by types.
     *
     * @param types the types of transactions to filter by
     * @return a filtered transaction list
     */
    public TransactionList filterByTypes(TransactionType... types) {
        List<TransactionType> typeList = List.of(types);

        return this.stream()
                .filter(tx -> typeList.contains(tx.getTransactionType()))
                .collect(Collectors.toCollection(TransactionList::new));
    }

    /**
     * Returns a string representation of the transaction list.
     *
     * @return a string containing transaction list information
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TransactionList: ").append(this.size()).append(" transactions\n");
        for (Transaction transaction : this) {
            builder.append("  - ").append(transaction.toString()).append("\n");
        }
        return builder.toString();
    }

    /**
     * Returns the date of the last recorded transaction.
     *
     * @return the date of the last activity or null if no transactions
     */
    public Date getLastActivityDate() {
        return this.stream()
                .map(Transaction::getTransactionDate)
                .max(Date::compareTo)
                .orElse(null);
    }

}