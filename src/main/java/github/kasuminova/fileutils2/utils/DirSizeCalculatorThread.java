package github.kasuminova.fileutils2.utils;

import cn.hutool.core.thread.ThreadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class DirSizeCalculatorThread implements Runnable {
    private final File dir;
    private final AtomicLong totalSize;
    private final AtomicLong totalFiles;

    DirSizeCalculatorThread(File dir, AtomicLong totalSize, AtomicLong totalFiles) {
        this.dir = dir;
        this.totalSize = totalSize;
        this.totalFiles = totalFiles;
    }

    public File dir() {
        return dir;
    }

    public AtomicLong totalSize() {
        return totalSize;
    }

    public AtomicLong totalFiles() {
        return totalFiles;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        DirSizeCalculatorThread that = (DirSizeCalculatorThread) obj;
        return Objects.equals(this.dir, that.dir) &&
                Objects.equals(this.totalSize, that.totalSize) &&
                Objects.equals(this.totalFiles, that.totalFiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dir, totalSize, totalFiles);
    }

    @Override
    public String toString() {
        return "DirSizeCalculatorThread[" +
                "dir=" + dir + ", " +
                "totalSize=" + totalSize + ", " +
                "totalFiles=" + totalFiles + ']';
    }

    @Override
    public void run() {
        File[] fileList = dir.listFiles();
        ArrayList<Thread> threadList = new ArrayList<>(4);
        if (fileList != null) {
            for (File value : fileList) {
                if (!value.isDirectory()) {
                    //计算大小
                    totalSize.getAndAdd(value.length());
                    //计算文件
                    totalFiles.getAndIncrement();
                } else {
                    Thread thread = new Thread(new DirSizeCalculatorThread(value, totalSize, totalFiles));
                    threadList.add(thread);
                    thread.start();
                }
            }
        }
        for (Thread thread : threadList) {
            ThreadUtil.waitForDie(thread);
        }
    }
}
