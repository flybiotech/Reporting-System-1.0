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

package com.hp.mss.hpprint.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Toast;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.activity.PrintPluginManagerActivity;
import com.hp.mss.hpprint.activity.PrintPreview;
import com.hp.mss.hpprint.adapter.HPPrintDocumentAdapter;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;

import java.util.HashMap;

/**
 * In order to print, you need to call the print(Activity) method in this class. It automatically creates
 * the print preview activity for KitKat devices. It also helps you install/detect print plugins.
 * <p>
 * You will need to set the printJobData in order to invoke the print method.
 */
public class PrintUtil {

    private static final String HAS_METRICS_LISTENER = "has_metrics_listener";
    private static final int START_PREVIEW_ACTIVITY_REQUEST = 100;
    private static final String TAG = "PrintUtil";

    private static PrintJobData printJobData;

    private static HashMap<String, String> appSpecificMetrics;

    protected static PrintMetricsListener metricsListener;
    public static boolean is4x5media;
    public static boolean doNotEncryptDeviceId = false;
    public static boolean uniqueDeviceIdPerApp = true;
    public static HashMap customData = new HashMap();
    public static final PrintAttributes.MediaSize mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "5 x 7", 5000, 7000);

    /**
     * Set this to false to disable plugin helper dialogs.
     */
    public static boolean sendPrintMetrics = true;

    /**
     * Call to start the HP Print SDK print flow.
     * @param activity The calling activity.
     * @param metrics any metrics related data in key/value hash format
     */
    public static void print(Activity activity, HashMap<String,String> metrics){
        appSpecificMetrics = metrics;
        print(activity);
    }

    public static boolean hasPrintJob() {
        if (printJobData == null) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Call to start the HP Print SDK print flow.  : 调用启动HP打印SDK打印流程。
     * @param activity The callin(调用)g activity.
     */
    public static void print(Activity activity){
        metricsListener = null;

        if(printJobData == null){
            Log.e(TAG, "Please set PrintJobData first");
            //这个是被我自己给注释掉的
//            Toast.makeText(activity.getApplicationContext(), TAG + ": " + R.string.set_print_job_data, Toast.LENGTH_LONG).show();
            Toast.makeText(activity.getApplicationContext(), TAG + ":请选择一个要打印的内容 " , Toast.LENGTH_LONG).show();
            return;
        }

        EventMetricsCollector.postMetricsToHPServer(
                activity,
                EventMetricsCollector.PrintFlowEventTypes.ENTERED_PRINT_SDK);

        if (checkIfActivityImplementsInterface(activity, PrintUtil.PrintMetricsListener.class)) {
            metricsListener = (PrintMetricsListener) activity;
        }

        /**
         * 这段的作用是，判断惠普提供的打印机插件 有没有启动或者安装
         * needHelpToInstallPlugin(activity)：判断是否需要跳转到下载插件的activity
         */
        if ( needHelpToInstallPlugin(activity) ) {
            Intent intent = new Intent(activity, PrintPluginManagerActivity.class);
            activity.startActivity(intent);
        } else {
            //显示需要打印的图片或者PDF  ，还显示选择打印机的设置， 和打印按钮
            readyToPrint(activity);

        }
//        readyToPrint(activity);
    }

    /**
     * 这个是关于打印的数据的
     * @param activity
     */
    public static void readyToPrint(Activity activity) {
        if(printJobData == null) {
            Log.e(TAG, "Please set PrintJobData first");
            Toast.makeText(activity.getApplicationContext(), TAG + ": " + R.string.set_print_job_data, Toast.LENGTH_LONG).show();
            return;
        }

//        createPrintJob(activity);
        //这个是预览要打印的图片或 者PDF ,而且直接展示了 插件的选择和设置  LOLLIPOP:是android 5.0版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP || printJobData.containsPDFItem()) {
            Log.d(TAG, "readyToPrint: android 5.0以上");
            //这个打印界面更加人性化，更加好
            createPrintJob(activity);
        } else {
            Log.d(TAG, "readyToPrint: android 5.0一下 或者不是pdf");
            //这个界面是比较老的界面。5.0 官方指定的打印界面，UI不可自定义
            startPrintPreviewActivity(activity);
        }
//        startPrintPreviewActivity(activity);
    }

    /**
     * Directly create the android PrintJob. This should not be needed except for special circumstances.
     * Please use the {@link #print(Activity)} method.
     * @param activity The calling activity.
     */
    public static void createPrintJob(final Activity activity) {
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter adapter = new HPPrintDocumentAdapter(activity, printJobData, false);
        //这个类很重要。主要是显示选择要打印的图片或PDF 而且还有打印机服务插件的选择
        //对一个打印服务来说，这个类代表一个打印任务

        final PrintJob androidPrintJob = printManager.print(printJobData.getJobName(), adapter, printJobData.getPrintDialogOptions());
        PItem.getOneItem().setPrintJob(androidPrintJob);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //想惠普服务器发送相关信息
                EventMetricsCollector.postMetricsToHPServer(activity, EventMetricsCollector.PrintFlowEventTypes.SENT_TO_PRINT_DIALOG);
                PrintMetricsCollector collector = new PrintMetricsCollector(activity, androidPrintJob, appSpecificMetrics);
                collector.start();
//                activity.finish();
            }
        });
    }

    /**
     * Sets the printJobData.
     * //要使用的打印作业数据对象
     * @param printJobData The print job data object that you want to use.
     */
    public static void setPrintJobData(PrintJobData printJobData){
       PrintUtil.printJobData = printJobData;
    }

    /**
     * Gets the printJobData.
     * @return The printJobData that is set using {@link #setPrintJobData(PrintJobData)} otherwise it will return null.
     */
    public static PrintJobData getPrintJobData(){
        return printJobData;
    }

    /**
     * This interface exists in order to pass print metrics back to the calling activity.
     * In order to receive print metrics, you must implement this interface in your activity that calls {@link #print(Activity)}.
     */
    public interface PrintMetricsListener {
        /**
         * This method, when implemented allows you to access data in the PrintMetricsData class.
         * @param printMetricsData The print metrics data.
         */
        void onPrintMetricsDataPosted(PrintMetricsData printMetricsData);
    }

    private static void startPrintPreviewActivity(Activity activity) {
        Intent intent = new Intent(activity, PrintPreview.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean has_metrics_listener = (metricsListener != null) ? true : false;
        intent.putExtra(HAS_METRICS_LISTENER, has_metrics_listener);

        activity.startActivityForResult(intent, START_PREVIEW_ACTIVITY_REQUEST);
        EventMetricsCollector.postMetricsToHPServer(activity, EventMetricsCollector.PrintFlowEventTypes.OPENED_PREVIEW);
    }

    private static boolean checkIfActivityImplementsInterface(Activity theActivity, Class theInterface){
        for (Class i : theActivity.getClass().getInterfaces())
            if (i.toString().equals(theInterface.toString()))
                return true;
        return false;
    }

    //判断插件是否已经安装好了，如果已经安装好了， 就不需要跳转到下载插件的activity了
    private static boolean needHelpToInstallPlugin(Activity activity) {
        boolean needHelp = true;
        //如果已经安装好了插件，则不需要再安装插件，就返回false，
        PrintPluginStatusHelper pluginStatusHelper = PrintPluginStatusHelper.getInstance(activity);
        if (pluginStatusHelper.readyToPrint())
            needHelp = false;
        return needHelp;
    }
}
