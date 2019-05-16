package com.shizhenbao.util;

import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by fly on 2017/3/23.
 */
//自定义日志
public class LogUtil {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final int level = VERBOSE;

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Logger.t(tag).v(tag,msg);
//            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Logger.t(tag).d(tag,msg);
//            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Logger.t(tag).i(tag,msg);
//            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Logger.t(tag).w(tag,msg);
//            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            Logger.t(tag).e(msg);
//            Log.e(tag, msg);
        }
    }
    /**
     * 根据文件路径 递归创建文件
     *
     * @param file
     */
    public static void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
