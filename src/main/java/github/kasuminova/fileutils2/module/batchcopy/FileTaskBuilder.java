package github.kasuminova.fileutils2.module.batchcopy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static github.kasuminova.fileutils2.FileUtils2.logger;

public class FileTaskBuilder {
    /**
     * 以资源文件夹为主，构建复制任务列表
     * @param srcDir 资源文件夹
     * @param destDirPathList 目标文件夹
     * @return 任务列表
     */
    public static List<FileTask> build(File srcDir, List<String> destDirPathList) {
        List<FileTask> taskList = new ArrayList<>(destDirPathList.size());
        List<File> srcFiles = scanFiles(srcDir);

        destDirPathList.forEach(destDirPath -> {
            StringBuilder destPath = new StringBuilder(16);
            File destDir = new File(destDirPath);
            srcFiles.forEach(file -> {
                destPath.setLength(0);
                File destFile = new File(
                        destPath.append(destDir.getPath())
                                .append("/")
                                .append(file.getPath().substring(srcDir.getPath().length()))
                                .toString());

                //防止自我复制
                try {
                    if (!file.getCanonicalPath().equals(destFile.getCanonicalPath())) {
                        taskList.add(new FileTask(file, destFile));
                    }
                } catch (IOException e) {
                    logger.error(e);
                }
            });
        });

        return taskList;
    }

    /**
     * 递归寻找目标文件夹内的所有文件
     * @param srcDir 要寻找的文件夹
     * @return 文件夹内所有文件
     */
    public static List<File> scanFiles(File srcDir) {
        File[] files = srcDir.listFiles();
        if (files != null) {
            List<File> fileList = new ArrayList<>(files.length);

            for (File file : files) {
                if (file.isHidden() || !file.canRead()) continue;

                if (file.isDirectory()) {
                    fileList.addAll(scanFiles(file));
                } else {
                    fileList.add(file);
                }
            }

            return fileList;
        }

        return new ArrayList<>(0);
    }
}
