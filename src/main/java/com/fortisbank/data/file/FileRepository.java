package com.fortisbank.data.file;

import java.io.File;
import java.util.List;

public abstract class FileRepository<T> {
    protected final File file;

    protected FileRepository(File file) {
        this.file = file;
    }

    protected List<T> readAll() {
        return FileManager.readListFromFile(file);
    }

    protected void writeAll(List<T> list) {
        FileManager.writeListToFile(file, list);
    }
}
