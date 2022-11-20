package github.kasuminova.fileutils2.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Security {
    /**
     * 检查字符串是否存在非法字符
     *
     * @param container                对话框父窗口
     * @param str                      要检查的对象
     * @param customUnavailableStrings 自定义非法字符列表
     * @return 未通过返回 true, 通过返回 false
     */
    public static boolean stringIsUnsafe(Container container, String str, String[] customUnavailableStrings) {
        //空字符检查
        if (str == null || str.isEmpty()) {
            return true;
        }

        //非法字符检查
        Set<String> unavailableStrList = new HashSet<>(Arrays.asList(
                ":", "*", "?", "<", ">", "|",
                "CON", "AUX",
                "COM1", "COM2", "COM3", "COM4",
                "LPT1", "LPT2", "LPT3",
                "PRN", "NUL"));

        //自定义非法字符
        if (customUnavailableStrings != null) unavailableStrList.addAll(Arrays.asList(customUnavailableStrings));

        if (unavailableStrList.contains(str)) {
            JOptionPane.showMessageDialog(container, "名称包含非法字符.", "错误", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }
}
