package github.kasuminova.fileutils2.module.batchcopy;

import cn.hutool.core.util.StrUtil;
import github.kasuminova.fileutils2.utils.FileUtil;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

public class FileTaskExecutor implements Callable<Boolean> {
    private final FileTask task;
    private final AtomicLong completedBytesTotal;

    public FileTaskExecutor(FileTask task, AtomicLong completedBytesTotal) {
        this.task = task;
        this.completedBytesTotal = completedBytesTotal;
    }

    @Override
    public Boolean call() throws IOException {
        if (task.dest.exists()) {
            if (!task.dest.delete()) {
                throw new IOException(StrUtil.format("Could Not Delete File {}", task.dest.getPath()));
            }
        }
        FileUtil.copyFile(task.src, task.dest, (completedBytes, bytesWritten) -> completedBytesTotal.addAndGet(bytesWritten));

        return true;
    }
}
