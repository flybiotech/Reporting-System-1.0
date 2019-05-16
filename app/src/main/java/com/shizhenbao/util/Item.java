package com.shizhenbao.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.shizhenbao.pop.SystemSet;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 用户 on 2017/4/1.
 * 创建Item对象
 */

public class Item implements Serializable {
    private String pId, pName,tel,age,tv_pResouce,tv_pMarray;
    private List<SystemSet> system;
    private String path;
    private CheckBox cb;
    private String jianjiPath;
    private Context context;
//    ProgressDialog xh_pDialog;//进度提示框
    public Item (Context context){
        this.context=context;
    }
    public Item (){

    }
    public final static String SAVE_APP_NAME = "download.apk";

    public final static String SAVE_APP_LOCATION = "/download";

    public final static String APP_FILE_NAME = "/sdcard" + SAVE_APP_LOCATION + File.separator + SAVE_APP_NAME;

    public static final String DOWNLOAD_APK_ID_PREFS = "download_apk_id_prefs";

    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";

    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getJianjiPath() {
        return jianjiPath;
    }

    public void setJianjiPath(String jianjiPath) {
        this.jianjiPath = jianjiPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public CheckBox getCb() {
        return cb;
    }

    public void setCb(CheckBox cb) {
        this.cb = cb;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getTv_pResouce() {
        return tv_pResouce;
    }

    public void setTv_pResouce(String tv_pResouce) {
        this.tv_pResouce = tv_pResouce;
    }

    public String getTv_pMarray() {
        return tv_pMarray;
    }

    public void setTv_pMarray(String tv_pMarray) {
        this.tv_pMarray = tv_pMarray;
    }

    /**
     * 得到SD卡根目录
     *
     * @return
     */
    public File getSD() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();

        } else {
//            Toast.makeText(this, "SD卡不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public boolean system() {//判断是否创建过数据
        system = DataSupport.findAll(SystemSet.class);
        if (system.size() == 0) {//数据为空时创建表,返回false,否则返回true
            return false;
        } else {
            return true;
        }
    }

    /**
     * 重新计算ListView的高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 删除文件夹所有内容
     */
    public void deleteFile(File file) {

        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            //
        }
    }

    /*
    判断是否安装了视珍宝一代软件，如果没有，则提示安装
     */
    public void isPkgInstalled(final String pkgName) {
        PackageInfo packageInfo = null;//管理已安装app包名
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }

        if (packageInfo == null)  {//判断是否安装视珍宝一代软件
            String path = Environment.getExternalStorageDirectory() + "/SZB_V1.0_size.apk";//视珍宝一带软件
            try {
                File file = new File(path);
                if (!file.exists()) {
//                    Log.e(TAG, "isPkgInstalled:1 !file.exists() " );
                    copyAPK2SD(path);//将项目中的服务插件复制到本地路径下
                }
//                Log.e(TAG, "isPkgInstalled: 2");
                installApk(context,path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {

            intentToSZB("您确定要跳到视珍宝一代图像获取软件吗?",pkgName);

        }
    }


    //安装apk
    private  void installApk(Context context, String fileApk) {
        //    通过隐式意图调用系统安装程序安装APK
        Intent install = new Intent(Intent.ACTION_VIEW);
//        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        //获取下载地址的uri   downloadFileUri: content://downloads/all_downloads/38
//        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Log.e(TAG, "downloadFileUri: "+downloadFileUri);
        if (fileApk != null) {
            File file = new File(fileApk);
            if (Build.VERSION.SDK_INT >= 24) {
                Uri apkUri =
                        FileProvider.getUriForFile(context,context.getPackageName()+ ".fileprovider", file);
//                content://com.qcam.fileprovider/external_files/Download/update.apk
//                Log.e(TAG, "android 7.0 : apkUri "+apkUri );
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivity(install);
            } else {
//                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                //Uri.fromFile(file) : file:///storage/emulated/0/Download/update.apk
//                Log.e(TAG, "Uri.fromFile(file) : "+Uri.fromFile(file) );

                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(install);
            }
        } else {
            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拷贝assets文件夹的视珍宝一代APK插件到SD
     *
     * @param strOutFileName
     * @throws IOException
     */
    private void copyAPK2SD(String strOutFileName) throws IOException {
        LogUtil.createDipPath(strOutFileName);
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myInput = context.getAssets().open("SZB_V1.0_size.apk");
            myOutput = new FileOutputStream(strOutFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            myOutput.flush();
            myInput.close();
            myOutput.close();
        }
    }

    /**
     * 提示跳转至世珍宝一代
     * @param msg
     * @param pkgName
     */
    private void intentToSZB(String msg,final String pkgName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图像获取软件");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = context.getPackageManager().getLaunchIntentForPackage(pkgName);
                context.startActivity(it);
//                isPkgInstalled(pkgName,strUrl,title,infoName);//调到图像获取软件
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

//    public void dialogView(final Context context) {
//
//        AlertDialog dialogView = new AlertDialog.Builder(context)
//                .setTitle("恢复出厂设置")
//                .setCancelable(false)
//                .setMessage("本次清除会清空数据库数据，本地保存数据以及采集到的图像信息。")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        DevConnect devConnect = new DevConnect(context);
//                        devConnect.searchDev();
////                        initShowDia(context,handler);
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .create();
//
//        dialogView.show();
//    }

//    private void initShowDia(Context context, final android.os.Handler handler){
//        xh_pDialog=new ProgressDialog(context);
//        xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        xh_pDialog.setTitle("提示框");
//        xh_pDialog.setCancelable(true);
//        xh_pDialog.setIndeterminate(true);
//        xh_pDialog.setMessage("数据正在清除，请稍等...");
//
//        xh_pDialog.show();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Message mess=handler.obtainMessage();
//                mess.what=6;
//                deleteLocal();
//                deleteLitePal();
//
//                xh_pDialog.cancel();
//                handler.sendMessage(mess);
//            }
//        }).start();
//
//    }
//    /**
//     * 删除数据库数据
//     */
//    private void deleteLitePal(){
//        DataSupport.deleteAll(Diacrisis.class);
//        DataSupport.deleteAll(User.class);
//        DataSupport.deleteAll(Doctor.class);
//        List<SystemSet>systemSets=DataSupport.findAll(SystemSet.class);
//        systemSets.get(0).setHospital_name("");
//        systemSets.get(0).setGather_path("SZB_save");
//        systemSets.get(0).setBackUpPath("PDF");
//        systemSets.get(0).setBackUpNetPath("/LUFAYUAN/");
//        systemSets.get(0).save();
//        DataSupport.deleteAll(Path.class);
//    }
//    /**
//     * 删除本地数据
//     */
//    private void deleteLocal(){
//        List<SystemSet>setList=DataSupport.findAll(SystemSet.class);
//        new Item().deleteFile(new File(new Item().getSD()+"/"+setList.get(0).getGather_path()));
//        List<Path>pathList=DataSupport.findAll(Path.class);
//        for(int i=0;i<pathList.size();i++){
//            new Item().deleteFile(new File(new Item().getSD()+"/"+pathList.get(i).getPicPath()));
//        }
//    }
}