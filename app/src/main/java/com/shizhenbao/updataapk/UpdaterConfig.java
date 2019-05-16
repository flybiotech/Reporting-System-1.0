package com.shizhenbao.updataapk;

import android.content.Context;

/**
 * Created by Administrator on 2017/8/13.
 */

public class UpdaterConfig {

    private boolean mIsLog;
    private String mTitle; // 设置一些基本显示信息 设置下载中通知栏提示的标题
    private String mDescription;// 设置下载中通知栏提示的介绍
    private String mDownloadPath;
    private String mFileUrl; //DownloadManager.Request用来请求一个下载的地址
    private String mFilename;
    private boolean mIsShowDownloadUI = true; //点击正在下载的Notification进入下载详情界面，如果设为true则可以看到下载任务的进度，如果设为false，则看不到我们下载的任务
    private int mNotificationVisibility;
    private boolean mCanMediaScanner; //能够被MediaScanner扫描
    private boolean mAllowedOverRoaming;  //移动网络是否允许下载   移动网络情况下是否允许漫游。
    private int mAllowedNetworkTypes = ~0;// //表示下载允许的网络类型，默认在任何网络下都允许下载。有NETWORK_MOBILE、NETWORK_WIFI、NETWORK_BLUETOOTH三种及其组合可供选择。如果只允许wifi下载，而当前网络为3g，则下载会等待。
    private Context mContext;

    private UpdaterConfig(Context context) {
        this.mContext = context;
    }

    public boolean isIsLog() {
        return mIsLog;
    }

    public String getFileUrl() {
        return mFileUrl;
    }

    public void setFileUrl(String mFileUrl) {
        this.mFileUrl = mFileUrl;
    }

    public void setIsLog(boolean mIsLog) {
        this.mIsLog = mIsLog;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getDownloadPath() {
        return mDownloadPath;
    }

    public void setDownloadPath(String mDownloadPath) {
        this.mDownloadPath = mDownloadPath;
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String mFilename) {
        this.mFilename = mFilename;
    }

    public boolean isShowDownloadUI() {
        return mIsShowDownloadUI;
    }

    public void setShowDownloadUI(boolean mIsShowDownloadUI) {
        this.mIsShowDownloadUI = mIsShowDownloadUI;
    }

    public int getNotificationVisibility() {
        return mNotificationVisibility;
    }

    public void setNotificationVisibility(int mNotificationVisibility) {
        this.mNotificationVisibility = mNotificationVisibility;
    }

    public boolean isCanMediaScanner() {
        return mCanMediaScanner;
    }

    public void setCanMediaScanner(boolean mCanMediaScanner) {
        this.mCanMediaScanner = mCanMediaScanner;
    }

    public boolean isAlloweOverRoaming() {
        return mAllowedOverRoaming;
    }

    public void setAlloweOverRoaming(boolean mAlloweOverRoaming) {
        this.mAllowedOverRoaming = mAlloweOverRoaming;
    }

    public int getALLowedNetworkTypes() {
        return mAllowedNetworkTypes;
    }

    public void setALLowedNetworkTypes(int mALLowedNetworkTypes) {
        this.mAllowedNetworkTypes = mALLowedNetworkTypes;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }


    //新建Builder 对象
    public static class Builder{

        UpdaterConfig updaterConfig;

        public Builder(Context context) {
            updaterConfig = new UpdaterConfig(context);
        }

        public Builder setIsLog(boolean mIsLog) {
            updaterConfig.setIsLog(mIsLog);
            return this;
        }

        //设置通知栏提示的标题
        public Builder setTitle(String title) {
            updaterConfig.setTitle(title);
            return this;
        }

        public Builder setDescription(String description) {
            updaterConfig.setDescription(description);
            return this;
        }

        //设置文件下载路径
        public Builder setDownloadPath(String downloadPath) {
            updaterConfig.setDownloadPath(downloadPath);
            return this;
        }

        //设置下载的文件名称
        public Builder setFilename(String filename) {
            updaterConfig.setFilename(filename);
            return this;
        }

        //文件网络地址
        public Builder setFileUrl(String url) {
            updaterConfig.setFileUrl(url);
            return this;
        }


        public Builder setIsShowDownloadUI(boolean isShowDownloadUI) {
            updaterConfig.setShowDownloadUI(isShowDownloadUI);
            return this;
        }

        public Builder setNotificationVisibility(int notificationVisibility) {
            updaterConfig.mNotificationVisibility = notificationVisibility;
            return this;
        }

        /**
         * 能否被 MediaScanner 扫描
         *request.allowScanningByMediaScanner(); 表示允许MediaScanner扫描到这个文件，默认不允许。
         * @param canMediaScanner
         * @return
         */
        public Builder setCanMediaScanner(boolean canMediaScanner) {
            updaterConfig.mCanMediaScanner = canMediaScanner;
            return this;
        }

        /**
         * 移动网络是否允许下载
         *
         * @param allowedOverRoaming
         * @return
         */
        public Builder setAllowedOverRoaming(boolean allowedOverRoaming) {
            updaterConfig.mAllowedOverRoaming = allowedOverRoaming;
            return this;
        }

        public Builder setContext(Context context) {
            updaterConfig.mContext = context;
            return this;

        }

        /**
         * By default, all network types are allowed
         *
         * @param allowedNetworkTypes
         * @param allowedNetworkTypes # NETWORK_MOBILE
         * @param  allowedNetworkTypes # NETWORK_WIFI
         */
        public Builder setAllowedNetworkTypes(int allowedNetworkTypes) {
            updaterConfig.mAllowedNetworkTypes= allowedNetworkTypes;
            return this;
        }

        public UpdaterConfig build() {
            return updaterConfig;
        }
    }

    public interface AllowedNetworkType {
        /**
         * Bit flag for {@link android.app.DownloadManager.Request#NETWORK_MOBILE}
         */
        int NETWORK_MOBILE = 1 << 0;

        /**
         * Bit flag for {@link android.app.DownloadManager.Request#NETWORK_WIFI}
         */
        int NETWORK_WIFI = 1 << 1;
    }





























}
