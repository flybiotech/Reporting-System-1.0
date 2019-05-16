package com.shizhenbao.util;

import java.io.File;

public class DeleteUtils {
    /**
     * 删除本地文件
     */
    public static boolean deleteLocal(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file1 : files) {
                    deleteLocal(file1);
                }
            }
            file.delete();
            return true;
        }
        return false;
    }

}
