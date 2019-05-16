/*
 * Hewlett-Packard Company
 * All rights reserved.
 *
 * This file, its contents, concepts, methods, behavior, and operation
 * (collectively the "Software") are protected by trade secret, patent,
 * and copyright laws. The use of the Software is governed by a license
 * agreement. Disclosure of the Software to third parties, in any form,
 * in whole or in part, is expressly prohibited except as authorized by
 * the license agreement.
 */

package com.hp.mss.hpprint.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.util.EventMetricsCollector;
import com.hp.mss.hpprint.util.PrintPluginStatusHelper;

/**
 * Created by panini on 1/20/16.
 * 打印机服务插件的类，里面包含了打印机服务插件的相关的信息
 */
public class PrintPlugin {
    /**
     * READY：
     * NOTINSTALLED：未安装状态，表示需要下载安装
     * REQUIREUPDATE：//更新
     * DISABLED：//
     * DOWNLOADING：//正在下载安装
     */
    public enum PluginStatus { READY, NOTINSTALLED, REQUIREUPDATE, DISABLED, DOWNLOADING }

    private String packageName;
    private int  minimumVersion;
    private String playStoreUrl;
    private PluginStatus status;
    private String name;
    private String maker;
    private int icon;
    private Context context;

    /**
     * Represent a print plugin object
     * @param packageName       package name that is used in Google play store 包的名称，使用的是谷歌Play商店
     * @param minimumVersion    minimum version of this plugin that is useful :至少这个插件是有用的版本,有用插件的最低版本
     * @param playStoreUrl      playstore download url  :playstore下载网址
     * @param context           application context
     * @param name              display name  :显示名称
     * @param maker             manufacture of the print plugin 打印插件的制造(商)
     * @param icon              print plugin icon :打印服务插件的图标
     */
    public PrintPlugin(String packageName, int minimumVersion, String playStoreUrl, Context context, String name, String maker, int icon) {
        this.context = context;
        this.packageName = packageName;
        this.minimumVersion = minimumVersion;
        this.playStoreUrl =playStoreUrl;
        this.name = name;
        this.maker = maker;
        this.icon = icon;
        updateStatus();
    }

    /**
     *
     * @return the plugin status
     */
    public PluginStatus getStatus() {
        return status;
    }

    /**
     *
     * @return plugin name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return plugin maker
     */
    public String getMaker() {
        return maker;
    }

    /**
     *
     * @return icon
     */

    public int getIcon() {
        return icon;
    }

    /**
     *
     * @return package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * sent user to Google playstore to download or update the plugin
     */
    public void goToPlayStoreForPlugin() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        EventMetricsCollector.postMetricsToHPServer((Activity) context, EventMetricsCollector.PrintFlowEventTypes.SENT_TO_GOOGLE_PLAY_STORE);
    }

    /**
     * check the plugin status again
     *再次检查插件的状态
     */
    public void updateStatus() {
        final PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0); // This will throw an exception if the package does not exist
            int versionNumber = packageInfo.versionCode;
            if (versionNumber < minimumVersion) {
                //更新状态v
                status = PluginStatus.REQUIREUPDATE;
            } else {
                if (checkPluginEnabled()) {
                    status =  PluginStatus.READY;
                } else {
                    status =  PluginStatus.DISABLED;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            if (status != PluginStatus.DOWNLOADING) {
                status = PluginStatus.NOTINSTALLED;
            }
        }
    }

    /**
     * set plugin status as downloading
     * 将status的状态设置为downloading
     */
    public void setStatusToDownloading() {
        status = PluginStatus.DOWNLOADING;
    }

    /**
     *
     * @return if the installed plugin is enabled
     * 返回的是，如果安装的插件能被启用
     */
    private boolean checkPluginEnabled() {
        //MOPRIA STILL HAS DEFECT - Disabling the print plugin will not disable the running service
        //Using ugly work around specifically for Mopria org.mopria.printplugin/org.mopria.printplugin.MopriaPrintService
        //I'm sorry :(
        //This defect was fixed in Nougat
        String printServiceName = packageName;
        if (packageName == PrintPluginStatusHelper.MOPRIA_PRINT_PLUGIN_PACKAGE_NAME && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            //mopria 大一打印插件服务包名
            printServiceName = PrintPluginStatusHelper.MOPRIA_PRINT_SERVICE_NAME;
        }
        String enabledPrintServices = Settings.Secure.getString(context.getContentResolver(), "enabled_print_services");
        String disabledPrintServices = Settings.Secure.getString(context.getContentResolver(), "disabled_print_services");
        //android M 版本号比5.0高，比6.0低
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            if (null != enabledPrintServices)
                return enabledPrintServices.toLowerCase().contains(printServiceName.toLowerCase());
            else
                return false;
        } else {
            if (null != disabledPrintServices)
                return !disabledPrintServices.toLowerCase().contains(printServiceName.toLowerCase());
            else
                return true;
        }

    }

    /**
     *
     * @return return predefined status icon
     */
    public int getNextActionIcon() {
        switch (this.status) {
            case NOTINSTALLED://未安装，表示可以下载
                return R.drawable.down_arrow;
            case DOWNLOADING://正在现在
                return R.drawable.downloading_arrow;
            case REQUIREUPDATE://更新
                return R.drawable.update;
            case DISABLED://不可用
                return R.drawable.disabled;
            case READY://可用
                return R.drawable.enabled;
            default://默认下载
                return R.drawable.down_arrow;
        }
    }


}