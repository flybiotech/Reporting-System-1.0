package com.shizhenbao.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

/**惠普打印机下载工具
 * Created by zhangbin on 2017/4/25.
 */

public class UpdateAppManager {
    @SuppressWarnings("unused")

    private static final String TAG = "UpdateAppManager";

    /**

     * 下载Apk, 并设置Apk地址,

     * 默认位置: /storage/sdcard0/Download

     *

     * @param context    上下文

     * @param downLoadUrl 下载地址

     * @param infoName   通知名称

     * @param description  通知描述

     */

    @SuppressWarnings("unused")

    public static void downloadApk(Context context, String downLoadUrl, String description, String infoName) {

        String appUrl = downLoadUrl;

        if (appUrl == null || appUrl.isEmpty()) {

            return;

        }

        appUrl = appUrl.trim(); // 去掉首尾空格

        if (!appUrl.startsWith("http")) {

            appUrl = "http://" + appUrl; // 添加Http信息

        }

        DownloadManager.Request request;

        try {

            request = new DownloadManager.Request(Uri.parse(appUrl));

        } catch (Exception e) {

            e.printStackTrace();

            return;

        }

        request.setTitle(infoName);

        request.setDescription(description);

        //在通知栏显示下载进度

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            request.allowScanningByMediaScanner();

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        }

        //sdcard目录下的download文件夹

        request.setDestinationInExternalPublicDir(Item.SAVE_APP_LOCATION, Item.SAVE_APP_NAME);

        Context appContext = context.getApplicationContext();

        DownloadManager manager = (DownloadManager) appContext.getSystemService(Context.DOWNLOAD_SERVICE);

        manager.enqueue(request);

    }

}
