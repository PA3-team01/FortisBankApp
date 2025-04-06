package com.fortisbank.data.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileManager {

    /**
     * Reads a list of objects from a file.
     *
     * @param file the file to read from
     * @param <T> the type of objects in the list
     * @return the list of objects read from the file, or an empty list if the file does not exist
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> readListFromFile(File file) {
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}