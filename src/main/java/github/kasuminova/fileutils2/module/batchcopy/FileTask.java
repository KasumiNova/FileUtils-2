package github.kasuminova.fileutils2.module.batchcopy;

import java.io.File;

public class FileTask {
    public final File src;
    public final File dest;

    public FileTask(File src, File dest) {
        this.src = src;
        this.dest = dest;
    }
}
