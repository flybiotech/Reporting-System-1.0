package com.util;

import java.io.Closeable;
import java.io.IOException;

public class CloseableUtils {
    private CloseableUtils() {
    }

    //关闭对象
    public static void closeQuietly(Closeable closeable) {

        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
