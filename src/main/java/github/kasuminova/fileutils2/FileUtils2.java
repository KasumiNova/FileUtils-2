package github.kasuminova.fileutils2;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.log.Log;
import github.kasuminova.fileutils2.configuration.BatchCopyConfig;
import github.kasuminova.fileutils2.configuration.ConfigManager;
import github.kasuminova.fileutils2.gui.SwingThemeLoader;
import github.kasuminova.fileutils2.module.batchcopy.BatchCopy;
import github.kasuminova.fileutils2.utils.CustomThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FileUtils2 {
    static {
        SwingThemeLoader.init();
    }

    public static final ThreadPoolExecutor GLOBAL_IO_THREAD_POOL = ExecutorBuilder.create()
            .setCorePoolSize(Runtime.getRuntime().availableProcessors())
            .setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2)
            .setKeepAliveTime(30, TimeUnit.SECONDS)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setThreadFactory(CustomThreadFactory.create("IO Thread - {}"))
            .build();
    public static final Log logger = Log.get("FileUtils 2");
    public static final JFrame MAIN_FRAME = new JFrame("FileUtils 2 - 文件批量操作");
    public static final JPanel MAIN_PANEL = (JPanel) MAIN_FRAME.getContentPane();
    public static final JTabbedPane MAIN_TABBED_PANE = new JTabbedPane(SwingConstants.TOP);
    private static final BatchCopyConfig DEFAULT_BATCH_COPY_CONFIG = new BatchCopyConfig();

    private static void init() {
        MAIN_PANEL.setLayout(new BorderLayout());
        MAIN_PANEL.add(MAIN_TABBED_PANE, BorderLayout.CENTER);

        try {
            ConfigManager.loadBatchCopyConfigFromFile("./FileUtils2/", "config", DEFAULT_BATCH_COPY_CONFIG);
        } catch (Exception e) {
            logger.error("无法载入主配置文件。", e);
        }
        MAIN_TABBED_PANE.add(BatchCopy.create("./FileUtils2/", "config"), "模块：批量复制");

        MAIN_FRAME.pack();
        MAIN_FRAME.setResizable(false);
        MAIN_FRAME.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MAIN_FRAME.setLocationRelativeTo(null);
        MAIN_FRAME.setVisible(true);
    }

    public static void main(String[] args) {
        init();
    }
}
