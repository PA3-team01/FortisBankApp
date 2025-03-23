package com.fortisbank.data.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

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
    public static <T> T readObjectFromFile(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

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
