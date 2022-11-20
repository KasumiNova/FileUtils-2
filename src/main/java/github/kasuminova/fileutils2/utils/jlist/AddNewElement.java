package github.kasuminova.fileutils2.utils.jlist;

import github.kasuminova.fileutils2.FileUtils2;
import github.kasuminova.fileutils2.configuration.BatchCopyConfig;
import github.kasuminova.fileutils2.utils.Security;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AddNewElement implements ActionListener {
    private final JList<String> jList;
    private final List<String> list;
    private final BatchCopyConfig config;
    private final boolean autoUpdateConfig;
    public AddNewElement(JList<String> jList, List<String> list, BatchCopyConfig config, boolean autoUpdateConfig) {
        this.jList = jList;
        this.list = list;
        this.config = config;
        this.autoUpdateConfig = autoUpdateConfig;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String element = JOptionPane.showInputDialog(FileUtils2.MAIN_FRAME,
                "请输入目标文件夹：（如 “Dir0”）");
        if (element != null && !element.isEmpty()) {
            if (Security.stringIsUnsafe(FileUtils2.MAIN_FRAME, element, new String[]{config.getResourceDirectory()})) {
                return;
            }
            list.add(element);
            String[] newTargetDirectory = list.toArray(new String[0]);
            jList.setListData(newTargetDirectory);

            if (autoUpdateConfig) config.setTargetDirectory(newTargetDirectory);
        }
    }
}
