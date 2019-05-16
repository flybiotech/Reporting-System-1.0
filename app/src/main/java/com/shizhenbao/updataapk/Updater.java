package com.shizhenbao.updataapk;

import android.app.DownloadManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import javax.security.auth.login.LoginException;

/**
 * Created by Administrator on 2017/8/13.
 * 调用这个类，就可以进行彻底的跳转
 */

public class Updater {

    /**
     * FileDownloadManager.getDownloadStatus 如果没有找到会返回-1
     */
    private static final int STATUS_UN_FIND = -1;

    private static Updater instance;

    private static String TAG = "TAG_Updater";

    public synchronized static Updater getInstance() {
        if (instance == null) {
            instance = new Updater();
        }
        return instance;
    }


    //这个方法即使下载更新版本的方法pub
    public void download(UpdaterConfig updaterConfig) {
        //系统的下载组件是否可用
        if (!UpdaterUtils.checkDownloadState(updaterConfig.getContext())) {
            Toast.makeText(updaterConfig.getContext(),"下载服务不可用，请您启用", Toast.LENGTH_SHORT).show();
            //当下载服务不可用的时候，就调用这个方法，启用下载服务
            UpdaterUtils.showDownloadSetting(updaterConfig.getContext());
            return;
        }
        long downloadId = UpdaterUtils.getLocalDownloadId(updaterConfig.getContext());
        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.get();
            int status = fdm.getDownloadStatus(updaterConfig.getContext(), downloadId);
            switch (status) {

                //下载成功
                case DownloadManager.STATUS_SUCCESSFUL:
                    //获取保存文件地址
                    Uri uri = fdm.getDownloadUri(updaterConfig.getContext(), downloadId);
                    if (uri != null) {
                        //本地的版本大于当前的版本，就直接安装
                        if (UpdaterUtils.compare(updaterConfig.getContext(), uri.getPath())) {
                            UpdaterUtils.startInstall(updaterConfig.getContext(), uri);
                            return;
                        } else {
                            //从FileDownloadManager中移除这个任务
                            fdm.getDM(updaterConfig.getContext()).remove(downloadId);
                        }
                    }
                    //重新下载
                    startDownload(updaterConfig);
                    break;

                //下载失败
                case DownloadManager.STATUS_FAILED:
                    startDownload(updaterConfig);
                    break;
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(updaterConfig.getContext(), "downloadId=" + downloadId + " ,status = STATUS_RUNNING", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    Toast.makeText(updaterConfig.getContext(), "downloadId=" + downloadId + " ,status = STATUS_PENDING", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PAUSED:
                    Toast.makeText(updaterConfig.getContext(), "downloadId=" + downloadId + " ,status = STATUS_PAUSED", Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_UN_FIND:
                    Toast.makeText(updaterConfig.getContext(), "downloadId=" + downloadId + " ,status = STATUS_UN_FIND", Toast.LENGTH_SHORT).show();
                    startDownload(updaterConfig);
                    break;
                default:
                    Toast.makeText(updaterConfig.getContext(), "downloadId=" + downloadId + " ,status = " + status, Toast.LENGTH_SHORT).show();

                    break;
            }
        } else {
            startDownload(updaterConfig);
        }

    }

    //下载更新版本的apk
    private void startDownload(UpdaterConfig updaterConfig) {
        long id = FileDownloadManager.get().startDownload(updaterConfig);
        Log.e(TAG, "startDownload: downloadId :  "+id );
    }

}
