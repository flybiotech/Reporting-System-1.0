package com.shizhenbao.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintJob;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.activity.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.hp.mss.hpprint.util.PItem;
import com.orhanobut.logger.Logger;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.User;
import com.shizhenbao.printer.HPprinter;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.InstallApkUtils;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SPUtils;
import com.shizhenbao.wifiinfo.WifiConnectManager;
import com.util.FileUtil;
import com.util.SouthUtil;
import com.view.MyToast;


import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFPreviewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener, WifiConnectManager.WifiConnectListener {


    private PDFView pdfView;
    private FloatingActionButton btnPrint;
    private String pathName;
    private HPprinter hpPrint;
    private PrintJob printJob;
    private InstallApkUtils installApkUtils;
    private FileUtil fileUtil;

    private String HP_WIFI_SSID = "";
    private String HP_WIFI_PASS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_pdfpreview);
        initView();

        hpPrint = new HPprinter(this);
        Intent intent1 = getIntent();
        pathName = intent1.getStringExtra("msg");
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

    private void initView() {
        pdfView = (PDFView) findViewById(R.id.pdfView);
        btnPrint = (FloatingActionButton) findViewById(R.id.printBtn_pdf);
        installApkUtils = new InstallApkUtils(this);
        fileUtil = new FileUtil();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiConnect();
        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
        if (!flag) {
            printJob = PItem.getOneItem().getPrintJob();
            if (printJob != null) {
                finish();
            }
        }
    }

    private void wifiConnect() {
        HP_WIFI_SSID = (String) SPUtils.get(this, Const.HP_WIFI_SSID_KEY, "");
        HP_WIFI_PASS = (String) SPUtils.get(this, Const.HP_WIFI_PASS_KEY, "");
        Log.e("TAG_1", "wifiConnect: ssid = "+ HP_WIFI_SSID +" ,pass = "+HP_WIFI_PASS+" , WIFI_TYPE_HP = "+Const.WIFI_TYPE_HP );
        WifiConnectManager.getInstance().connectWifi(HP_WIFI_SSID, HP_WIFI_PASS, Const.WIFI_TYPE_HP_PDF, this);
    }




    private void displayFromFile(File file ) {
        if (!file.exists()) {
            MyToast.showToast(this,getString(R.string.print_Report_Non_existent));
//            SouthUtil.showToast(this,getString(R.string.print_Report_Non_existent));
            return;
        }

        pdfView.fromFile(file)   //设置pdf文件地址
                .defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();
    }

    /**
     * 翻页回调
     *
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {
        MyToast.showToast(this,"page= " + page + " pageCount= " + pageCount);
//        SouthUtil.showToast(this,"page= " + page + " pageCount= " + pageCount);
    }

    /**
     * 加载完成回调
     *
     * @param nbPages 总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
        MyToast.showToast(this,getString(R.string.print_Report_loading_success));
//        SouthUtil.showToast(this,getString(R.string.print_Report_loading_success));
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
        if (packageInfo == null) {//判断是否安装惠普打印机服务插件
            String path = Environment.getExternalStorageDirectory() + "/hp.apk";//打印服务插件本地路径
            try {
                File file = new File(path);
                if (!file.exists()) {
                    installApkUtils.copyAPK2SD(path);//将项目中的服务插件复制到本地路径下
                }
                installApk(this, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            final Doctor doctor_name = new LoginRegister().getDoctor(OneItem.getOneItem().getName());

            if (OneItem.getOneItem().getFile() == null) {//判断文件不存在
                List<User> userlist = LitePal.findAll(User.class);
                File f = null;
                if (userlist.size() > 0) {
                    //当程序刚刚登记第一个用户，还未来得及生成报告  就点击打印报告的按钮时，这个时候是pdf地址是为空的
                    if (userlist.get(userlist.size() - 1).getPdfPath() == null) {
//                        Toast.makeText(PDFPreviewActivity.this, R.string.print_Report_make, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Logger.t("printrt").e("用户的人数： " + userlist.size());
                    for (int i = 0; i < userlist.size(); i++) {

                        f = new File(userlist.get(userlist.size() - 1).getPdfPath());
                    }
                    if (fileUtil.getFileSize(f) != 0) {
                        if (pdfPath != null && doctor_name.getEdit_hos_name() != null) {
                            hpPrint.continueButtonPrint(pdfPath);

                        } else {
                            MyToast.showToast(this,getString(R.string.print_Report_nothing));
//                            SouthUtil.showToast(this,getString(R.string.print_Report_nothing));
                        }
                    }
                }

            } else {

                if (fileUtil.getFileSize(OneItem.getOneItem().getFile()) != 0) {

                    if (pdfPath != null && doctor_name.getEdit_hos_name() != null) {

                        hpPrint.continueButtonPrint(pdfPath);
                    } else {
                        MyToast.showToast(this,getString(R.string.print_Report_nothing));
//                        SouthUtil.showToast(this,getString(R.string.print_Report_nothing));
                    }


                }
            }
        }
    }

    //根据android 版本选择不同的安装方式
    private void installApk(Context context, String fileApk) {

        if (fileApk != null) {
            File file = new File(fileApk);
            if (Build.VERSION.SDK_INT >= 24) {//android 7.0以上
                installApkUtils.initInstallAPK();
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//Android 8.0以上，增加了一个未知来源安装的权限
                if(!getPackageManager().canRequestPackageInstalls()){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivityForResult(intent,1);
                }else {
                    installApkUtils.initInstallAPK();
                }
            } else{//android 6.0以下直接安装
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(install);
            }
        } else {
            MyToast.showToast(this,getString(R.string.print_download_faild));
//            SouthUtil.showToast(this, getString(R.string.print_download_faild));
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    @Override
    public void startWifiConnecting(String type) {

    }

    @Override
    public void wifiConnectSuccess(String type) {

    }

    @Override
    public void wifiConnectFalid(String type) {

    }

    @Override
    public void wifiCycleSearch(String type, boolean isSSID, int count) {

        if (type.equals(Const.WIFI_TYPE_HP_PDF)) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isSSID) {
                        WifiConnectManager.getInstance().connectWithWpa(HP_WIFI_SSID, HP_WIFI_PASS);
                    } else {
                        MyToast.showToast(PDFPreviewActivity.this,getString(R.string.wifiFailMsgprint));
//                        SouthUtil.showToast(PDFPreviewActivity.this, getString(R.string.wifiFailMsgprint));
                    }
                }
            });


        }



    }

    @Override
    public void wifiInputNameEmpty(String type) {

    }
}
