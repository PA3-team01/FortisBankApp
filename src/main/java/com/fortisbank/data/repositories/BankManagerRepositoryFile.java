package com.fortisbank.data.repositories;

     import com.fortisbank.data.file.FileRepository;
     import com.fortisbank.exceptions.BankManagerRepositoryException;
     import com.fortisbank.models.collections.ManagerList;
     import com.fortisbank.models.users.BankManager;

     import java.io.File;
     import java.util.List;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     /**
      * Repository class for managing bank manager data stored in a file.
      * Extends the FileRepository class and implements the IBankManagerRepository interface.
      */
     public class BankManagerRepositoryFile extends FileRepository<BankManager> implements IBankManagerRepository {

         private static final Logger LOGGER = Logger.getLogger(BankManagerRepositoryFile.class.getName());
         private static final File file = new File("data/managers.ser"); // File to store manager data
         private static BankManagerRepositoryFile instance; // Singleton instance

         private BankManagerRepositoryFile() {
             super(file);
         }

         public static synchronized BankManagerRepositoryFile getInstance() {
             if (instance == null) {
                 instance = new BankManagerRepositoryFile();
             }
             return instance;
         }

         @Override
         public BankManager getManagerById(String id) throws BankManagerRepositoryException {
             return executeQuery(managers -> managers.stream()
                     .filter(m -> m.getUserId().equals(id))
                     .findFirst()
                     .orElse(null), "Error retrieving manager with ID: " + id);
         }

         @Override
         public void insertManager(BankManager manager) throws BankManagerRepositoryException {
             executeUpdate(managers -> managers.add(manager), "Error inserting manager");
         }

         @Override
         public void updateManager(BankManager manager) throws BankManagerRepositoryException {
             executeUpdate(managers -> {
                 for (int i = 0; i < managers.size(); i++) {
                     if (managers.get(i).getUserId().equals(manager.getUserId())) {
                         managers.set(i, manager);
                         return;
                     }
                 }
             }, "Error updating manager");
         }

         @Override
         public void deleteManager(String id) throws BankManagerRepositoryException {
             executeUpdate(managers -> managers.removeIf(m -> m.getUserId().equals(id)), "Error deleting manager with ID: " + id);
         }

         @Override
         public ManagerList getAllManagers() throws BankManagerRepositoryException {
             return executeQuery(ManagerList::new, "Error retrieving all managers");
         }

         private <T> T executeQuery(QueryFunction<List<BankManager>, T> function, String errorMessage) throws BankManagerRepositoryException {
             try {
                 List<BankManager> managers = readAll();
                 return function.apply(managers);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, errorMessage, e);
                 throw new BankManagerRepositoryException(errorMessage, e);
             }
         }

         private void executeUpdate(UpdateFunction<List<BankManager>> function, String errorMessage) throws BankManagerRepositoryException {
             try {
                 List<BankManager> managers = readAll();
                 function.apply(managers);
                 writeAll(managers);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, errorMessage, e);
                 throw new BankManagerRepositoryException(errorMessage, e);
             }
         }

         @FunctionalInterface
         private interface QueryFunction<T, R> {
             R apply(T t) throws Exception;
         }

         @FunctionalInterface
         private interface UpdateFunction<T> {
             void apply(T t) throws Exception;
         }
     }