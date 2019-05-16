package com.shizhenbao.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.activity.R;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.orhanobut.logger.Logger;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.User;
import com.shizhenbao.printer.HPprinter;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.LogUtil;
import com.shizhenbao.util.OneItem;
import com.util.MyView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PDFPreviewActivity extends AppCompatActivity implements OnPageChangeListener,
        OnLoadCompleteListener, OnDrawListener{

    private PDFView pdfView ;
    private FloatingActionButton btnPrint;
    private String pathName;
    private HPprinter hpPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfpreview);
        pdfView= (PDFView) findViewById(R.id.pdf);

        btnPrint = (FloatingActionButton) findViewById(R.id.printBtn_pdf);
        hpPrint = new HPprinter(this);
        Intent intent1=getIntent();
        pathName= intent1.getStringExtra("msg");
        //跳到打印机的按钮{
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hpPrint.continueButtonPrint(pathName);
                isPkgInstalled(Const.PageHp, pathName);
            }
        });

        displayFromFile(new File(pathName));

    }
    private void displayFromFile( File file ) {
        if (!file.exists()) {
            Toast.makeText(this, R.string.print_Report_Non_existent, Toast.LENGTH_SHORT).show();
            return;
        }
        
        pdfView.fromFile(file)   //设置pdf文件地址
                .defaultPage(1)         //设置默认显示第1页
                .onPageChange(this)     //设置翻页监听
                .onLoad(this)           //设置加载监听
                .onDraw(this)            //绘图监听
                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
                .enableSwipe(true)   //是否允许翻页，默认是允许翻
                // .pages( 2 , 3 , 4 , 5  )  //把2 , 3 , 4 , 5 过滤掉
                .load();
    }
    /**
     * 翻页回调
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {
        Toast.makeText( this , "page= " + page +
                " pageCount= " + pageCount , Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载完成回调
     * @param nbPages  总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
        Toast.makeText( this ,  getString(R.string.print_Report_loading_success)+ nbPages , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }
//打印机
    private void isPkgInstalled(final String pkgName, final String pdfPath) {
        PackageInfo packageInfo = null;//管理已安装app包名
        try {
            packageInfo = getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null)  {//判断是否安装惠普打印机服务插件
            String path = Environment.getExternalStorageDirectory() + "/hp.apk";//打印服务插件本地路径
            try {
                File file = new File(path);
                if (!file.exists()) {
                    copyAPK2SD(path);//将项目中的服务插件复制到本地路径下
                }
                installApk(this,path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            final Doctor doctor_name=new LoginRegister().getDoctor(OneItem.getOneItem().getName());

            if(OneItem.getOneItem().getFile()==null||OneItem.getOneItem().getFile().equals("")){//判断文件不存在
                List<User> userlist= DataSupport.findAll(User.class);
                File f=null;
                if(userlist.size()>0){
                    //当程序刚刚登记第一个用户，还未来得及生成报告  就点击打印报告的按钮时，这个时候是pdf地址是为空的
                    if (userlist.get(userlist.size() - 1).getPdfPath() == null) {
//                        Toast.makeText(PDFPreviewActivity.this, R.string.print_Report_make, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Logger.t("printrt").e("用户的人数： "+userlist.size());
                    for(int i=0;i<userlist.size();i++){

                        f=new File(userlist.get(userlist.size()-1).getPdfPath());
                    }
                    if(getFileSize(f)!=0){
                        if (pdfPath != null&&doctor_name.getEdit_hos_name()!=null) {
                            hpPrint.continueButtonPrint(pdfPath);
                        } else {
                            Toast.makeText(PDFPreviewActivity.this,R.string.print_Report_nothing, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }

            else {

                if(getFileSize(OneItem.getOneItem().getFile())!=0){

                    if (pdfPath != null&&doctor_name.getEdit_hos_name()!=null) {

                        hpPrint.continueButtonPrint(pdfPath);
                    }

                    else {
                        Toast.makeText(this, R.string.print_Report_nothing, Toast.LENGTH_SHORT).show();
                    }


                }
            }
        }
    }


    /**
     * 得到文件大小
     */
    public  long getFileSize(File file){
        long size =0;
        if(file.exists()){
            FileInputStream fis=null;
            try {
                fis=new FileInputStream(file);
                size=fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
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
            Toast.makeText(context, R.string.print_download_faild, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 拷贝assets文件夹的APK插件到SD
     *
     * @param strOutFileName
     * @throws IOException
     */
    private void copyAPK2SD(String strOutFileName) throws IOException {
        LogUtil.createDipPath(strOutFileName);
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myInput =getAssets().open("hp.apk");
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

}
