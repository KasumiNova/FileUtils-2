package github.kasuminova.fileutils2.module.batchcopy;

import github.kasuminova.fileutils2.configuration.BatchCopyConfig;
import github.kasuminova.fileutils2.configuration.ConfigManager;
import github.kasuminova.fileutils2.gui.SmoothProgressBar;
import github.kasuminova.fileutils2.gui.layoutmanager.VFlowLayout;
import github.kasuminova.fileutils2.utils.ModernColors;
import github.kasuminova.fileutils2.utils.SwingUtils;
import github.kasuminova.fileutils2.utils.jlist.AddNewElement;
import github.kasuminova.fileutils2.utils.jlist.DeleteElement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static github.kasuminova.fileutils2.FileUtils2.logger;

public class BatchCopy extends JPanel {
    private static final Dimension JLIST_SIZE = new Dimension(280, 355);
    private final JLabel statusLabel = new JLabel("状态：就绪", JLabel.LEFT);
    private final JTextField resourceDirectoryTextField = new JTextField();
    private final JList<String> targetDirectoryJList = new JList<>();
    private final List<String> targetDirectoryList = new ArrayList<>(0);
    private final JList<String> targetDirectoryTmpJList = new JList<>();
    private final List<String> targetDirectoryTmpList = new ArrayList<>(0);
    private final JButton startBtn = new JButton("开始复制（默认配置）");
    private final JButton startTmpBtn = new JButton("开始复制（临时配置）");
    private final JButton saveConfig = new JButton("保存配置");
    private final SmoothProgressBar progressBar = new SmoothProgressBar(100, 200);
    private final AtomicBoolean isWorking = new AtomicBoolean();
    private final BatchCopyConfig config;
    private final String configPath;
    private final String configName;
    private BatchCopyInterface moduleInterface;

    //变量初始化
    {
        statusLabel.setForeground(ModernColors.BLUE);
        statusLabel.setBorder(new EmptyBorder(2,0, 0,0));
        progressBar.setBorder(new EmptyBorder(0,15,0,0));
        progressBar.setStringPainted(true);
        progressBar.setString("就绪");
        saveConfig.addActionListener(e -> saveConfig());
    }

    BatchCopy(String configPath, String configName) {
        config = new BatchCopyConfig();
        this.configPath = configPath;
        this.configName = configName;
        try {
            ConfigManager.loadBatchCopyConfigFromFile(configPath, configName, config);
        } catch (Exception e) {
            logger.error("无法载入主配置文件。", e);
        }
        setLayout(new VFlowLayout());

        applyConfig();
        loadInterface();

        add(loadResourceDirectoryTextField());

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(createTargetDirectoryList(), BorderLayout.WEST);
        listPanel.add(createTargetDirectoryTmpList(), BorderLayout.EAST);

        add(listPanel);
        add(saveConfig);
        add(loadStatusPanel());
    }

    public static BatchCopy create(String configPath, String configName) {
        return new BatchCopy(configPath, configName);
    }

    private void applyConfig() {
        resourceDirectoryTextField.setText(config.getResourceDirectory());
        targetDirectoryList.addAll(Arrays.asList(config.getTargetDirectory()));
        targetDirectoryJList.setListData(targetDirectoryList.toArray(new String[0]));
    }

    private void saveConfig() {
        config.setTargetDirectory(targetDirectoryList.toArray(new String[0]))
              .setResourceDirectory(resourceDirectoryTextField.getText());
        ConfigManager.saveConfigToFile(configPath, configName, config);
    }

    private void loadInterface() {
        moduleInterface = new BatchCopyInterface() {
            @Override
            public AtomicBoolean isWorking() {
                return isWorking;
            }

            @Override
            public String getResourceDirectory() {
                return BatchCopy.this.config.getResourceDirectory();
            }

            @Override
            public List<String> getTargetDirectory() {
                return BatchCopy.this.targetDirectoryList;
            }

            @Override
            public SmoothProgressBar getProgressBar() {
                return progressBar;
            }

            @Override
            public void resetProgressBar() {
                progressBar.setValue(0);
                progressBar.setString("无任务");
            }

            @Override
            public JLabel getStatusLabel() {
                return statusLabel;
            }
        };
    }

    private Box loadResourceDirectoryTextField() {
        Box box = Box.createHorizontalBox();
        box.add(new JLabel("资源文件夹路径："));
        box.add(resourceDirectoryTextField);
        box.setBorder(new EmptyBorder(5,5,5,5));

        return box;
    }

    private JPanel createTargetDirectoryList() {
        JPanel targetDirectoryPanel = new JPanel(new BorderLayout());

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem addNewElement = new JMenuItem("添加新的目标文件夹");
        addNewElement.addActionListener(new AddNewElement(targetDirectoryJList, targetDirectoryList, config, true));
        popupMenu.add(addNewElement);

        JMenuItem deleteElement = new JMenuItem("删除选中的目标文件夹");
        deleteElement.addActionListener(new DeleteElement(targetDirectoryJList, targetDirectoryList, config));
        popupMenu.add(deleteElement);

        popupMenu.addSeparator();

        JMenuItem copyLeftToRight = new JMenuItem("复制选中的内容至临时配置");
        copyLeftToRight.addActionListener(e -> {
            targetDirectoryTmpList.addAll(targetDirectoryJList.getSelectedValuesList());
            targetDirectoryTmpJList.setListData(targetDirectoryTmpList.toArray(new String[0]));
        });
        popupMenu.add(copyLeftToRight);

        JPanel listPanel = new JPanel();
        listPanel.add(targetDirectoryJList);
        JScrollPane listPanelScrollPane = new JScrollPane(
                listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        listPanelScrollPane.setBorder(new TitledBorder("目标文件夹（默认配置）"));
        listPanelScrollPane.setPreferredSize(JLIST_SIZE);

        SwingUtils.PopupMenuAdapter adapter = new SwingUtils.PopupMenuAdapter(popupMenu, targetDirectoryPanel);
        listPanelScrollPane.addMouseListener(adapter);
        targetDirectoryJList.addMouseListener(adapter);

        targetDirectoryPanel.add(listPanelScrollPane, BorderLayout.CENTER);

        startBtn.addActionListener(new BatchCopyExecutor(moduleInterface, targetDirectoryList));
        targetDirectoryPanel.add(startBtn, BorderLayout.SOUTH);

        return targetDirectoryPanel;
    }

    private JPanel createTargetDirectoryTmpList() {
        JPanel targetDirectoryPanel = new JPanel(new BorderLayout());

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem addNewElement = new JMenuItem("添加新的目标文件夹");
        addNewElement.addActionListener(new AddNewElement(targetDirectoryTmpJList, targetDirectoryTmpList, config, false));
        popupMenu.add(addNewElement);

        JMenuItem deleteElement = new JMenuItem("删除选中的目标文件夹");
        deleteElement.addActionListener(new DeleteElement(targetDirectoryTmpJList, targetDirectoryTmpList, null));
        popupMenu.add(deleteElement);

        JPanel listPanel = new JPanel();
        listPanel.add(targetDirectoryTmpJList);
        JScrollPane listPanelScrollPane = new JScrollPane(
                listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        listPanelScrollPane.setBorder(new TitledBorder("目标文件夹（临时配置）"));
        listPanelScrollPane.setPreferredSize(JLIST_SIZE);

        SwingUtils.PopupMenuAdapter adapter = new SwingUtils.PopupMenuAdapter(popupMenu, targetDirectoryPanel);
        listPanelScrollPane.addMouseListener(adapter);
        targetDirectoryTmpJList.addMouseListener(adapter);

        targetDirectoryPanel.add(listPanelScrollPane, BorderLayout.CENTER);

        startTmpBtn.addActionListener(new BatchCopyExecutor(moduleInterface, targetDirectoryTmpList));
        targetDirectoryPanel.add(startTmpBtn, BorderLayout.SOUTH);

        return targetDirectoryPanel;
    }

    private JPanel loadStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.CENTER);

        return statusPanel;
    }
}
