package com.fortisbank.data.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileManager {

    @SuppressWarnings("unchecked")
    public static <T> List<T> readListFromFile(File file) { // Read a list of objects from a file
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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

    // Optional extras:

    // Read a single object from a file (*** reads the first object in the file ***)
    @SuppressWarnings("unchecked")
    public static <T> T readObjectFromFile(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Write a single object to a file (*** overwrites the file ***)
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
