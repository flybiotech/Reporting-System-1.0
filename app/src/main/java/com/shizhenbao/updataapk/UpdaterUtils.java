package com.shizhenbao.updataapk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;

/**
 * Created by Administrator on 2017/8/13.
 * 下载完成之后，是否按照相关的信息
 */

public class UpdaterUtils {

    private static final String KEY_DOWNLOAD_ID = "downloadId";

    public static void startInstall(Context context, Uri uri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);

    }

    /**
     * 下载的apk和当前程序版本比较
     */

    public static boolean compare(Context context, String path) {
        PackageInfo apkInfo = getApkInfo(context, path);
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
//            if ()
            if (apkInfo.packageName.equals(localPackage)) {
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取apk程序信息 [packageName,versionName...]
     */
    private static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info;
        }
        return null;
    }


    //要启动的intent是否可用
    public static boolean intentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    //系统的下载组件是否可用
    public static boolean checkDownloadState(Context context) {
        int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
        try {
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    ||state==PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    ||state==PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
                    ) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //当下载服务不可用的时候，就调用这个方法，启用下载服务
    public static void showDownloadSetting(Context context) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (UpdaterUtils.intentAvailable(context, intent)) {
            context.startActivity(intent);

        }

    }

    //获取下载的id
    public static long getLocalDownloadId(Context context) {
        return SpUtils.getInstance(context).getLong(KEY_DOWNLOAD_ID, -1L);

    }

     //保存下载的id
    public static void saveDownloadId(Context context, long id) {
        SpUtils.getInstance(context).putLong(KEY_DOWNLOAD_ID, id);
    }













}
