package com.shizhenbao.updataapk;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2017/8/13.
 */

public class FileDownloadManager {

    private static FileDownloadManager instance;

    private DownloadManager mDownloadManager;

    private static String TAG = "TAG_FileDownloadManager";

    private FileDownloadManager() {

    }

    static FileDownloadManager get() {
        if (instance == null) {
            instance = new FileDownloadManager();
        }
        return instance;
    }

    public DownloadManager getDM(Context context) {
        if (mDownloadManager == null){
            mDownloadManager = (DownloadManager) context.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
    }
        return mDownloadManager;
    }

   //最重要的方法，
    long startDownload(UpdaterConfig updaterConfig) {
        // TODO: 2017/8/3 github issue 为什么只有在WIFI情况下才能下载
        // 【在android 7.0小米4 机器上，会报错。在android6.0上乐视1S,也会报错。但在android5.1及以下机器上就没有这个问题】
        // TODO: 2017/8/3 github issue 7.0以上需要Intent.FLAG_GRANT_READ_URI_PERMISSION 权限？
        // TODO: 2017/8/3 实现上层设置文件的下载路径

        //DownlaodManager.Request 用来请求一个下载
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updaterConfig.getFileUrl()));
        //表示下载允许的网络类型，默认在任何情况下都允许下载，有NETWORK_MOBILE、NETWORK_WIFI、NETWORK_BLUETOOTH三种及其组合可供选择。如果只允许wifi下载，而当前网络为3g，则下载会等待。
        request.setAllowedNetworkTypes(updaterConfig.getALLowedNetworkTypes());
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(updaterConfig.isAlloweOverRoaming());

        //能够被MediaScanner 扫描 ,不过这里最好做一个判断
        if (updaterConfig.isCanMediaScanner()) {
            request.allowScanningByMediaScanner();
        }

        //是否显示状态栏下载UI 表示下载进行中和下载完成的通知栏是否显示。默认只显示下载中通知。VISIBILITY_VISIBLE_NOTIFY_COMPLETED表示下载完成后显示通知栏提示。
        // VISIBILITY_HIDDEN表示不显示任何通知栏提示，这个需要在AndroidMainfest中添加权限android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
        //如果把这个注释掉，那么当下载完成之后，通知栏就自动消失
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //点击正在下载的Notification进入下载详情界面，如果设为true则可以看到下载任务的进度，如果设为false，则看不到我们下载的任务
        request.setVisibleInDownloadsUi(updaterConfig.isShowDownloadUI());
//        request.setVisibleInDownloadsUi(false);
        /**
         * 设置文件的保存位置[三种方式]
         * 第一个参数：表示设置下载地址为sd卡的xxx文件夹
         * 第二个参数：文件名为 update.apk
         */
        //第一种
        //file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
//        request.setDestinationInExternalFilesDir(updaterConfig.getContext(), Environment.DIRECTORY_DOWNLOADS,"update.apk");
        //第二种
        //file:///storage/emulated/0/Download/update.apk
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"update.apk");
        ////第三种 自定义文件路径
//        request.setDestinationUri();

        /**
         * 设置一些基本信息
         */
        //设置下载中通知栏提示的标题
        request.setTitle(updaterConfig.getTitle());
        //设置下载中通知栏提示的介绍
        request.setDescription(updaterConfig.getDescription());

        //设置下载文件的mineType 。 设置文件下载的类型
        // 因为下载管理UI中点击某个已下载完成文件及下载完成点击通知栏提示都会根据mimeType去打开文件，所以我们可以利用这个属性。
        request.setMimeType("application/vnd.android.package-archive");
        //添加请求下载的网络连接的http 头，比如“：User-Agent，gzip压缩等
//        request.addRequestHeader(String header, String value);

        //加入下载队列
        long id = getDM(updaterConfig.getContext()).enqueue(request);
        Log.e(TAG, "文件保存的路径的下载id: "+id );
        //这个时候调用这个方法，文件路径为空，因为还没有下载完
        String downloadPath = getDownloadPath(updaterConfig.getContext(), id);
        Log.e(TAG, "文件保存的路径: "+downloadPath );
        //把DownloadId 保存到本地
        UpdaterUtils.saveDownloadId(updaterConfig.getContext(), id);
        return id;
        //long downloadId = mDownloadManager.enqueue(req);
        //Log.d("DownloadManager", downloadId + "");
        //mDownloadManager.openDownloadedFile()
    }


    /**
     * 获取文件保存的路径
     * params downloadId  下载的ID，在整个系统中是独一无二的。此ID用于与此下载相关的调用。
     */
    private String getDownloadPath(Context context, long downloadId) {
        //DownloadManager.Query 用来查询下载信息
        /**
         * 从上面代码可以看出我们主要调用DownloadManager.Query()进行查询。DownloadManager.Query为下载管理对外开放的信息查询类，主要包括以下接口：
         setFilterById(long… ids)根据下载id进行过滤
         setFilterByStatus(int flags)根据下载状态进行过滤
         setOnlyIncludeVisibleInDownloadsUi(boolean value)根据是否在download ui中可见进行过滤。
         orderBy(String column, int direction)根据列进行排序，不过目前仅支持DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP和DownloadManager.COLUMN_TOTAL_SIZE_BYTES排序。
         */
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = getDM(context).query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    //获取文件名称
//                    String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    String path = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                    return path;
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

    //获取保存文件地址
    public Uri getDownloadUri(Context context, long downloadId) {
        return getDM(context).getUriForDownloadedFile(downloadId);
    }




    /**
     * 获取下载状态
     *
     * @return int
     * @see DownloadManager#STATUS_PENDING
     * @see DownloadManager#STATUS_PAUSED
     * @see DownloadManager#STATUS_RUNNING
     * @see DownloadManager#STATUS_SUCCESSFUL
     * @see DownloadManager#STATUS_FAILED
     */

    public int getDownloadStatus(Context context, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = getDM(context).query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                    return status;
                }
            } finally {
                c.close();

            }
        }
        return -1;
    }





























}
