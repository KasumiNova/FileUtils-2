package github.kasuminova.fileutils2.utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SimpleFileFilter extends FileFilter {
    private final String[] ext;
    private final String[] blackList;
    private final String des;

    /**
     * 一个简易的多文件扩展名过滤器
     * 文件扩展名过滤器
     *
     * @param ext       扩展名数组，如 png,jpg
     * @param blackList 黑名单
     * @param des       扩展描述
     */
    public SimpleFileFilter(String[] ext, String[] blackList, String des) {
        this.ext = ext;
        this.blackList = blackList;
        this.des = des;
    }

    public boolean accept(File file) {
        //如果是文件夹则显示文件夹
        if (file.isDirectory()) return true;
        String fileName = file.getName();
        //黑名单检查
        if (blackList != null) {
            for (String s : blackList) {
                if (fileName.contains(s)) return false;
            }
        }
        for (String extension : ext) {
            if (file.getName().endsWith(String.format(".%s", extension))) return true;
        }
        return false;
    }

    public String getDescription() {
        return des;
    }
}
