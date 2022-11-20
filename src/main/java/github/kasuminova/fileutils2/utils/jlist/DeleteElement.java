package github.kasuminova.fileutils2.utils.jlist;

import github.kasuminova.fileutils2.configuration.BatchCopyConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DeleteElement implements ActionListener {
    private final JList<String> jList;
    private final List<String> list;
    private final BatchCopyConfig config;

    public DeleteElement(JList<String> jList, List<String> list, BatchCopyConfig config) {
        this.jList = jList;
        this.list = list;
        this.config = config;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        jList.getSelectedValuesList().forEach(list::remove);
        String[] newTargetDirectory = list.toArray(new String[0]);
        jList.setListData(newTargetDirectory);

        if (config != null) config.setTargetDirectory(newTargetDirectory);
    }
}
