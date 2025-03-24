package com.fortisbank.data.file;

import java.io.File;
import java.util.List;

public abstract class FileRepository<T> {
    protected final File file;

    protected FileRepository(File file) {
        this.file = file; // Set this.file to file (file path
    }

    protected List<T> readAll() {
        return FileManager.readListFromFile(file); // Call FileManager.readListFromFile() with file as argument
    }

    protected void writeAll(List<T> list) {
        FileManager.writeListToFile(file, list); // Call FileManager.writeListToFile() with file and list as arguments
    }
}
