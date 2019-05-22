package com.shizhenbao.updataapk;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.activity.R;
import com.view.MyToast;

import java.io.File;

/**
 * Created by Administrator on 2017/8/13.
 */

public class ApkInstallReceiver extends BroadcastReceiver {

    private static String TAG = "TAG_ApkInstallReceiver";

    //        下载完成后，下载管理会发出DownloadManager.ACTION_DOWNLOAD_COMPLETE这个广播，并传递downloadId作为参数。通过接受广播我们可以打开对下载完成的内容进行操作

    //        点击下载中通知栏提示，系统会对下载的应用单独发送Action为DownloadManager.ACTION_NOTIFICATION_CLICKED广播。intent.getData为content://downloads/all_downloads/29669，最后一位为downloadId。
//        如果同时下载多个应用，intent会包含DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS这个key，表示下载的的downloadId数组。这里设计到下载管理通知栏的显示机制
    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            //通过这个返回也可以获取 系统下载器分配的id
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long localDownloadId = UpdaterUtils.getLocalDownloadId(context);
            if (downloadApkId == localDownloadId) {
                installApk(context, downloadApkId);
            }
        }else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)){
            //处理 如果还未完成下载，用户点击Notification ,则调到下载的界面
            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewDownloadIntent);

//            long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
//            //点击通知栏取消下载
//            manager.remove(ids);
//            Toast.makeText(context, "已经取消下载", Toast.LENGTH_SHORT).show();

        }


    }


    //安装apk
    private static void installApk(Context context, long downloadApkId) {
        //    通过隐式意图调用系统安装程序安装APK
        Intent install = new Intent(Intent.ACTION_VIEW);
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //获取下载地址的uri   downloadFileUri: content://downloads/all_downloads/38
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e(TAG, "downloadFileUri: "+downloadFileUri);
        if (downloadFileUri != null) {
            File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    , "update.apk");
            if (Build.VERSION.SDK_INT > 24) {
                Uri apkUri =
                        FileProvider.getUriForFile(context,context.getPackageName()+ ".fileprovider", file);
//                content://com.qcam.fileprovider/external_files/Download/update.apk
                Log.e(TAG, "android 7.0 : apkUri "+apkUri );
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivity(install);
            } else {
//                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                //Uri.fromFile(file) : file:///storage/emulated/0/Download/update.apk
                Log.e(TAG, "Uri.fromFile(file) : "+Uri.fromFile(file) );

                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(install);
            }

        } else {
            MyToast.showToast(context, context.getString(R.string.notification_fail));
//            Toast.makeText(context, context.getString(R.string.notification_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
}
