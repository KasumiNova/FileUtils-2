package github.kasuminova.fileutils2.module.batchcopy;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import github.kasuminova.fileutils2.FileUtils2;
import github.kasuminova.fileutils2.gui.SmoothProgressBar;
import github.kasuminova.fileutils2.utils.FileUtil;
import github.kasuminova.fileutils2.utils.MiscUtils;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

import static github.kasuminova.fileutils2.FileUtils2.logger;

public class BatchCopyExecutor implements ActionListener {
    private final BatchCopyInterface moduleInterface;
    private final List<String> targetDirectoryList;
    private final AtomicLong totalSize = new AtomicLong(0);
    private final AtomicLong completedBytes = new AtomicLong(0);
    private final SmoothProgressBar progressBar;
    private final Timer timer;

    public BatchCopyExecutor(BatchCopyInterface moduleInterface, List<String> targetDirectoryList) {
        this.moduleInterface = moduleInterface;
        this.progressBar = moduleInterface.getProgressBar();
        this.targetDirectoryList = targetDirectoryList;

        timer = new Timer(200, e1 -> {
            long totalSize = BatchCopyExecutor.this.totalSize.get();
            long completedBytes = BatchCopyExecutor.this.completedBytes.get();
            if (totalSize > 0 || completedBytes > 0) {
                int progress = (int) (((double) completedBytes / totalSize) * (100 / targetDirectoryList.size()));
                progressBar.setValue(progress);
                progressBar.setString(StrUtil.format("已完成 {}% - {} / {}",
                        FileUtil.formatFileSizeToStr(completedBytes),
                        FileUtil.formatFileSizeToStr(totalSize),
                        progress));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ThreadUtil.execute(() -> {
            if (moduleInterface.isWorking().get()) {
                JOptionPane.showMessageDialog(FileUtils2.MAIN_FRAME, "当前已有复制任务正在运行。", "注意", JOptionPane.WARNING_MESSAGE);
                return;
            }

            File resourceDirectory = new File(moduleInterface.getResourceDirectory());
            if (!resourceDirectory.exists()) {
                JOptionPane.showMessageDialog(FileUtils2.MAIN_FRAME, "资源文件夹不存在。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            moduleInterface.isWorking().set(true);

            long start = System.currentTimeMillis();

            totalSize.set(FileUtil.getDirSize(resourceDirectory, progressBar)[0]);
            List<FileTask> taskList = FileTaskBuilder.build(resourceDirectory, targetDirectoryList);
            timer.start();

            List<FutureTask<Boolean>> futureTaskList = new ArrayList<>(taskList.size());

            int errorCount = 0;

            for (FileTask fileTask : taskList) {
                FutureTask<Boolean> task = new FutureTask<>(new FileTaskExecutor(fileTask, completedBytes));
                futureTaskList.add(task);
                FileUtils2.GLOBAL_IO_THREAD_POOL.submit(task);
            }

            for (FutureTask<Boolean> task : futureTaskList) {
                try {
                    task.get();
                } catch (Exception ex) {
                    logger.error(ex);
                    errorCount++;
                }
            }

            timer.stop();
            totalSize.set(0);
            completedBytes.set(0);
            moduleInterface.resetProgressBar();

            if (errorCount > 0) {
                JOptionPane.showMessageDialog(FileUtils2.MAIN_FRAME,
                        StrUtil.format(
                                "完成！。\n但是在复制过程中有一些文件出现了问题。\n共有 {} 个错误。\n详情错误请查看终端。",
                                errorCount));
            } else {
                JOptionPane.showMessageDialog(
                        FileUtils2.MAIN_FRAME,
                        StrUtil.format("完成！耗时 {} 秒。",
                                MiscUtils.formatTime(System.currentTimeMillis() - start)),
                        "完成", JOptionPane.INFORMATION_MESSAGE);
            }

            moduleInterface.isWorking().set(false);
        });
    }
}
