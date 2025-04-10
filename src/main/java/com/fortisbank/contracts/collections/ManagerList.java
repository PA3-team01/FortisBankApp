package com.fortisbank.contracts.collections;

 import com.fortisbank.contracts.models.users.BankManager;

 import java.util.ArrayList;
 import java.util.stream.Collectors;

 /**
  * A typed list for managing BankManager objects.
  * Provides filtering and extensibility for manager-specific operations.
  */
 public class ManagerList extends ArrayList<BankManager> {

     // ------------------- Constructors -------------------

     /**
      * Default constructor initializing an empty manager list.
      */
     public ManagerList() {
         super();
     }

     /**
      * Constructor initializing the manager list with a collection of managers.
      *
      * @param managers an iterable collection of managers to add to the list
      */
     public ManagerList(Iterable<BankManager> managers) {
         managers.forEach(this::add);
     }

     // ------------------- Filtering -------------------

     /**
      * Filters the managers whose name contains the specified substring.
      *
      * @param substring the substring to search for
      * @return a filtered manager list
      */
     public ManagerList filterByNameContains(String substring) {
         return this.stream()
                 .filter(m -> m.getFullName().toLowerCase().contains(substring.toLowerCase()))
                 .collect(Collectors.toCollection(ManagerList::new));
     }

     // ------------------- String Representation -------------------

     /**
      * Returns a string representation of the manager list.
      *
      * @return a string containing manager list information
      */
     @Override
     public String toString() {
         return "ManagerList{" + super.toString() + "}";
     }
 }