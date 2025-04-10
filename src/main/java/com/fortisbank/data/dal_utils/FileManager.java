package com.fortisbank.data.dal_utils;

     import java.io.*;
     import java.util.ArrayList;
     import java.util.List;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     @SuppressWarnings("ResultOfMethodCallIgnored")
     public class FileManager {

         private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

         /**
          * Reads a list of objects from a file.
          *
          * @param file the file to read from
          * @param <T> the type of objects in the list
          * @return the list of objects read from the file, or an empty list if the file does not exist or is invalid
          */
         @SuppressWarnings("unchecked")
         public static <T> List<T> readListFromFile(File file) {
             if (!file.exists() || file.length() == 0) {
                 LOGGER.log(Level.WARNING, "File does not exist or is empty: {0}", file.getAbsolutePath());
                 return new ArrayList<>();
             }
             try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                 return (List<T>) ois.readObject();
             } catch (IOException | ClassNotFoundException e) {
                 LOGGER.log(Level.SEVERE, "Error reading list from file: {0}", file.getAbsolutePath());
                 LOGGER.log(Level.SEVERE, e.getMessage(), e);
                 return new ArrayList<>();
             }
         }

         /**
          * Writes a list of objects to a file.
          *
          * @param file the file to write to
          * @param list the list of objects to write
          * @param <T> the type of objects in the list
          */
         public static <T> void writeListToFile(File file, List<T> list) {
             try {
                 if (file.getParentFile() != null) file.getParentFile().mkdirs();
                 try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                     oos.writeObject(list);
                     LOGGER.log(Level.INFO, "List written to file successfully: {0}", file.getAbsolutePath());
                 }
             } catch (IOException e) {
                 LOGGER.log(Level.SEVERE, "Error writing list to file: {0}", file.getAbsolutePath());
                 LOGGER.log(Level.SEVERE, e.getMessage(), e);
             }
         }

         /**
          * Reads a single object from a file. Reads the first object in the file.
          *
          * @param file the file to read from
          * @param <T> the type of the object
          * @return the object read from the file, or null if an error occurs
          */
         @SuppressWarnings("unchecked")
         public static <T> T readObjectFromFile(File file) {
             if (!file.exists() || file.length() == 0) {
                 LOGGER.log(Level.WARNING, "File does not exist or is empty: {0}", file.getAbsolutePath());
                 return null;
             }
             try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                 return (T) ois.readObject();
             } catch (IOException | ClassNotFoundException e) {
                 LOGGER.log(Level.SEVERE, "Error reading object from file: {0}", file.getAbsolutePath());
                 LOGGER.log(Level.SEVERE, e.getMessage(), e);
                 return null;
             }
         }

         /**
          * Writes a single object to a file. Overwrites the file.
          *
          * @param file the file to write to
          * @param object the object to write
          * @param <T> the type of the object
          */
         public static <T> void writeObjectToFile(File file, T object) {
             try {
                 if (file.getParentFile() != null) file.getParentFile().mkdirs();
                 try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                     oos.writeObject(object);
                     LOGGER.log(Level.INFO, "Object written to file successfully: {0}", file.getAbsolutePath());
                 }
             } catch (IOException e) {
                 LOGGER.log(Level.SEVERE, "Error writing object to file: {0}", file.getAbsolutePath());
                 LOGGER.log(Level.SEVERE, e.getMessage(), e);
             }
         }
     }