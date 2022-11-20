package github.kasuminova.fileutils2.utils;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.file.FileWriter;
import github.kasuminova.fileutils2.gui.SmoothProgressBar;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;

public class FileUtil {
    public static final int KB = 1024;
    public static final int MB = 1024 * 1024;
    public static final int GB = 1024 * 1024 * 1024;

    /**
     * 生成.json格式文件
     */
    public static void createJsonFile(String jsonString, String filePath, String fileName) throws IORuntimeException {
        createFile(jsonString, filePath, fileName + ".json");
    }

    /**
     * 生成文件
     * @param str 内容
     * @param filePath 路径
     * @param fileName 文件名
     */
    public static void createFile(String str, String filePath, String fileName) throws IORuntimeException {
        //拼接文件完整路径
        File target = new File(filePath + fileName);
        //保证创建一个新文件
        cn.hutool.core.io.FileUtil.touch(target);

        //写入文件
        FileWriter writer = new FileWriter(target);
        writer.write(str);
    }

    /**
     * 复制文件
     */
    public static void copyFile(File src, File dest, ProgressFutureListener progressFutureListener) throws IOException, IORuntimeException {
        if (!src.exists()) throw new NoSuchFileException(src.getAbsolutePath());
        if (!dest.exists()) cn.hutool.core.io.FileUtil.touch(dest);

        RandomAccessFile srcFile = null;
        RandomAccessFile descFile = null;
        try {
            srcFile = new RandomAccessFile(src, "r");
            descFile = new RandomAccessFile(dest, "rw");

            byte[] data = new byte[FileUtil.formatFileSizeInt(src.length())];
            int len;
            long total = 0;

            while ((len = srcFile.read(data)) > 0) {
                descFile.write(data, 0, len);
                total += len;
                if (progressFutureListener != null) progressFutureListener.onProgress(total, len);
            }

            srcFile.close();
            descFile.close();
        } catch (IOException e) {
            if (srcFile != null) srcFile.close();
            if (descFile != null) descFile.close();
        }
    }

    public static void copyFile(File src, File dest) throws IOException, IORuntimeException {
        copyFile(src, dest, null);
    }

    /**
     * 根据传入大小返回合适的 int 大小
     *
     * @param size 文件大小
     * @return 根据大小适应的 int 大小
     */
    public static int formatFileSizeInt(long size) {
        if (size <= KB) {
            return (int) size;
        } else if (size <= MB) {
            return KB * 8;
        } else if (size <= MB * 128) {
            return KB * 64;
        } else if (size <= MB * 512) {
            return MB;
        } else {
            return MB * 8;
        }
    }

    /**
     * 根据传入大小返回合适的格式化文本
     *
     * @param size 文件大小
     * @return Byte 或 KB 或 MB 或 GB
     */
    public static String formatFileSizeToStr(long size) {
        if (size <= KB) {
            return size + " Byte";
        } else if (size <= MB) {
            return String.format("%.2f", (double) size / KB) + " KB";
        } else if (size <= GB) {
            return String.format("%.2f", (double) size / MB) + " MB";
        } else {
            return String.format("%.2f", (double) size / GB) + " GB";
        }
    }

    /**
     * 计算文件夹内容大小
     */
    private static class FileCounter {
        private final AtomicLong totalSize = new AtomicLong(0);
        private final AtomicLong totalFiles = new AtomicLong();

        private long[] getFiles(File dir, SmoothProgressBar statusProgressBar) {
            statusProgressBar.setString("扫描文件夹内容... (0 Byte, 0 文件)");
            Timer timer = new Timer(250, e -> statusProgressBar.setString(
                    String.format("扫描文件夹内容... (%s, %s 文件)",
                            FileUtil.formatFileSizeToStr(totalSize.get()),
                            totalFiles.get())));
            timer.start();

            statusProgressBar.setVisible(true);
            statusProgressBar.setIndeterminate(true);

            new DirSizeCalculatorThread(dir, totalSize, totalFiles).run();

            statusProgressBar.setString(
                    String.format("扫描文件夹内容... (%s, %s 文件)",
                            FileUtil.formatFileSizeToStr(totalSize.get()),
                            totalFiles.get()));
            statusProgressBar.setIndeterminate(false);
            timer.stop();

            return new long[]{totalSize.get(), totalFiles.get()};
        }
    }

    /**
     * <p>
     * 统计目标文件夹内包含的 文件/文件夹 大小.
     * </p>
     *
     * <p>
     * 并将其大小整合在一起至一个变量, 用于轮询线程的查询.
     * </p>
     *
     * <p>
     * size[0] 为总大小
     * </p>
     *
     * <p>
     * size[1] 为总文件数量
     * </p>
     */
    public static long[] getDirSize(File dir, SmoothProgressBar statusProgressBar) {
        return new FileCounter().getFiles(dir, statusProgressBar);
    }
}
