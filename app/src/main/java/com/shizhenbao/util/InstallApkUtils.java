package com.shizhenbao.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstallApkUtils {

    private static final String TAG = "TAG_InstallApkUtils";
    private Context mContext;

    public InstallApkUtils(Context context){
        this.mContext = context;
    }

    /**
     * 拷贝assets文件夹的APK插件到SD
     *
     * @param strOutFileName
     * @throws IOException
     */
    public void copyAPK2SD(String strOutFileName) throws IOException {
        LogUtil.createDipPath(strOutFileName);
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myInput = mContext.getAssets().open("hp.apk");
            myOutput = new FileOutputStream(strOutFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (myOutput != null) {
                myOutput.flush();
                myOutput.close();
            }
            if (myInput != null) {
                myInput.close();
            }
        }
    }

    /**
     * 打印机插件安装的通用方法
     */
    public void initInstallAPK(){
        Intent install = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri =
                FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", new File(Environment.getExternalStorageDirectory() + "/hp.apk"));
//                content://com.qcam.fileprovider/external_files/Download/update.apk
        Log.e(TAG, "android 7.0 : apkUri " + apkUri);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        mContext.startActivity(install);
    }

}
