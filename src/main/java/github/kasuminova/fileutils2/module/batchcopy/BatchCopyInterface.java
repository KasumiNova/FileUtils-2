package github.kasuminova.fileutils2.module.batchcopy;

import github.kasuminova.fileutils2.gui.SmoothProgressBar;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface BatchCopyInterface {
    AtomicBoolean isWorking();
    String getResourceDirectory();
    List<String> getTargetDirectory();
    SmoothProgressBar getProgressBar();
    void resetProgressBar();
    JLabel getStatusLabel();
}
