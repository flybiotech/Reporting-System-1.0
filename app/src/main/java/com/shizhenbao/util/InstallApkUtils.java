package com.shizhenbao.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.activity.R;
import com.view.MyToast;

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
     * 判断某个apk是否已安装
     */
    public PackageInfo isInstallApk(String pkgName){
        PackageInfo packageInfo = null;//管理已安装app包名
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo;
    }



    /**
     * 拷贝assets文件夹的APK插件到SD
     *
     * @throws IOException
     */
    public boolean copyAPK2SD(String apkName) throws IOException {
        File file = null;
        String filePath = null;
        if(apkName.equals(Const.mopriaApkName)){
            filePath = Environment.getExternalStorageDirectory() + "/" + Const.mopriaApkName;//打印服务插件本地路径
        }else {
            filePath = Environment.getExternalStorageDirectory() + "/com.stub.StubApp.apk";//打印服务插件本地路径

        }
        file = new File(filePath);
        if(file.exists()){
            return true;
        }
        LogUtil.createDipPath(filePath);
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myInput = mContext.getAssets().open(apkName);
            myOutput = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally {
            if (myOutput != null) {
                myOutput.flush();
                myOutput.close();
            }
            if (myInput != null) {
                myInput.close();
            }
        }
        return true;
    }

    /**
     * 根据不同android版本选择不同的安装方式
     */
    public void installApk(Activity activity,String apkPath){
        if (apkPath != null) {
            File file = new File(apkPath);
            if (Build.VERSION.SDK_INT >= 24) {//android 7.0以上
                initInstallAPK(file.getName());
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//Android 8.0以上，增加了一个未知来源安装的权限
                if(!mContext.getPackageManager().canRequestPackageInstalls()){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    activity.startActivityForResult(intent,1);
                }else {
                    initInstallAPK(file.getName());
                }
            } else{//android 6.0以下直接安装
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                mContext.startActivity(install);
            }
        } else {
            MyToast.showToast(mContext,mContext.getString(R.string.print_download_faild));
//            SouthUtil.showToast(getActivity(), getActivity().getString(R.string.print_download_faild));
        }
    }

    /**
     * 打印机插件安装的通用方法
     */
    public void initInstallAPK(String apkName){
        Intent install = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri =
                FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", new File(Environment.getExternalStorageDirectory() + "/" + apkName));
//                content://com.qcam.fileprovider/external_files/Download/update.apk
        Log.e(TAG, "android 7.0 : apkUri " + apkUri);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        mContext.startActivity(install);
    }

}
