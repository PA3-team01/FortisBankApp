package com.fortisbank.contracts.collections;

import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.utils.CustomerComparators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
* List for managing customers.
*/
public class CustomerList extends ArrayList<Customer> {

 // ------------------- Constructors -------------------

 /**
  * Default constructor initializing an empty customer list.
  */
 public CustomerList() {
     super();
 }

 /**
  * Constructor initializing the customer list with a collection of customers.
  *
  * @param customers an iterable collection of customers to add to the list
  */
 public CustomerList(Iterable<Customer> customers) {
     customers.forEach(this::add);
 }

 // ------------------- Sorting Methods -------------------

 /**
  * Sorts the customers by name.
  */
 public void sortByName() {
     this.sort(CustomerComparators.BY_NAME);
 }

 /**
  * Sorts the customers by balance in descending order.
  */
 public void sortByBalanceDescending() {
     this.sort(CustomerComparators.BY_BALANCE.reversed());
 }

 /**
  * Sorts the customers by transaction count in ascending order.
  */
 public void sortByTransactionCountAscending() {
     this.sort(CustomerComparators.BY_TRANSACTION_COUNT);
 }

 /**
  * Sorts the customers by transaction count in descending order.
  */
 public void sortByTransactionCountDescending() {
     this.sort(CustomerComparators.BY_TRANSACTION_COUNT.reversed());
 }

 // ------------------- Filtering Methods -------------------

 /**
  * Filters the customers by minimum balance.
  *
  * @param minBalance the minimum balance
  * @return a filtered customer list
  */
 public CustomerList filterByMinBalance(double minBalance) {
     return this.stream()
             .filter(c -> c.getBalance().compareTo(BigDecimal.valueOf(minBalance)) >= 0)
             .collect(Collectors.toCollection(CustomerList::new));
 }

 /**
  * Filters the customers whose name contains the specified substring.
  *
  * @param substring the substring to search for
  * @return a filtered customer list
  */
 public CustomerList filterByNameContains(String substring) {
     return this.stream()
             .filter(c -> c.getFullName().toLowerCase().contains(substring.toLowerCase()))
             .collect(Collectors.toCollection(CustomerList::new));
 }

 // ------------------- String Representation -------------------

 /**
  * Returns a string representation of the customer list.
  *
  * @return a string containing customer list information
  */
 @Override
 public String toString() {
     return "CustomerList{" + super.toString() + "}";
 }
}