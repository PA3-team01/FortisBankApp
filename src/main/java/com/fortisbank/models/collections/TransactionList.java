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

public class TransactionList extends ArrayList<Transaction> {

    public TransactionList() {
        super();
    }

    public TransactionList(Iterable<Transaction> transactions) {
        transactions.forEach(this::add);
    }

    // Sorting methods

    /**
     * Trie les transactions par date decroissante
     */
    public void sortByDateDescending() {
        this.sort(TransactionComparators.BY_DATE.reversed());  // ✅ Now works correctly
    }

    /**
     * Trie les transactions par montant croissant
     */
    public void sortByAmountAscending() {
        this.sort(TransactionComparators.BY_AMOUNT);
    }

    /**
     * Trie les transactions par type
     */
    public void sortByType() {
        this.sort(TransactionComparators.BY_TYPE);
    }

    // Filtering methods

    /**
     * Filtre les transactions par montant minimum
     * @param minAmount Solde minimum
     * @return Transaction List filtrer
     */
    public TransactionList filterByMinAmount(BigDecimal minAmount) {
        return this.stream()
                .filter(t -> t.getAmount().compareTo(minAmount) >= 0)
                .collect(Collectors.toCollection(TransactionList::new));
    }

    /**
     * Filtre les transactions par type
     * @param type Type de transaction
     * @return Transaction List filtrer
     */
    public TransactionList filterByType(String type) {
        return this.stream()
                .filter(t -> t.getTransactionType().name().equalsIgnoreCase(type))
                .collect(Collectors.toCollection(TransactionList::new));
    }

    /**
     * Filtre les transactions par date range
     * @param start Debut
     * @param end Fin
     * @return Transaction List filtrer
     */
    public TransactionList filterByDateRange(Date start, Date end) {
        return this.stream()
                .filter(t -> !t.getTransactionDate().before(start) && !t.getTransactionDate().after(end))
                .collect(Collectors.toCollection(TransactionList::new));
    }

    /**
     * Filtre les transactions par mois
     * @param targetDate Date cible
     * @return Transaction List filtrer
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
     * Filtre les transactions par types
     * @param types Types de transactions
     * @return Transaction List filtrer
     */
    public TransactionList filterByTypes(TransactionType... types) {
        List<TransactionType> typeList = List.of(types);

        return this.stream()
                .filter(tx -> typeList.contains(tx.getTransactionType()))
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
