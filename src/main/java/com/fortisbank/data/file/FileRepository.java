package com.fortisbank.data.file;

import java.io.File;
import java.util.List;

/**
 * Abstract class for managing file-based repositories.
 *
 * @param <T> the type of objects to be stored in the repository
 */
public abstract class FileRepository<T> {
    protected final File file;

    /**
     * Constructs a FileRepository with the specified file.
     *
     * @param file the file to be used for storage
     */
    protected FileRepository(File file) {
        this.file = file; // Set this.file to file (file path)
    }

    /**
     * Reads all objects from the file.
     *
     * @return a list of objects read from the file
     */
    protected List<T> readAll() {
        return FileManager.readListFromFile(file); // Call FileManager.readListFromFile() with file as argument
    }

    /**
     * Writes a list of objects to the file.
     *
     * @param list the list of objects to write to the file
     */
    protected void writeAll(List<T> list) {
        FileManager.writeListToFile(file, list); // Call FileManager.writeListToFile() with file and list as arguments
    }
}