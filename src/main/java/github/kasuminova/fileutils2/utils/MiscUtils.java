package github.kasuminova.fileutils2.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MiscUtils {
    /**
     * <p>
     * 将错误打印成字符串。
     * </p>
     * <p>
     * 类似 printStackTrace()。
     * </p>
     *
     * @param e Exception
     * @return 字符串
     */
    public static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static String formatTime(long time) {
        if (time < 1000 * 10) {
            return String.format("%.3fs", (double) time / 1000);
        } else if (time < 1000 * 100) {
            return String.format("%.2fs", (double) time / 1000);
        } else if (time < 1000 * 1000) {
            return String.format("%.1fs", (double) time / 1000);
        } else {
            return String.format("%ss", time / 1000);
        }
    }

    /**
     * 显示弹出式菜单
     * 修复默认方法的几个奇怪的 BUG.
     * @param menu 菜单
     * @param invoker 调用者
     * @param e 鼠标事件
     */
    public static void showPopupMenu(JPopupMenu menu, Component invoker, MouseEvent e) {
        menu.setInvoker(invoker);
        menu.setLocation(e.getLocationOnScreen().x + 5, e.getLocationOnScreen().y + 5);
        menu.setVisible(true);
    }
}
