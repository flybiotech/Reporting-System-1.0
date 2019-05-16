package com.shizhenbao.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.activity.base.BaseActivity;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.constant.DeviceOfSize;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.wifiinfo.WifiHotAdapter;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class WifiSettingActivity extends BaseActivity implements View.OnClickListener {
    private    Button clear,save,btn_right,btn_left,selectBtnHP,selectBtnGetImageSoft;
    private    EditText editName,editPass;
    private    int wifiID;
    private TextView title_text;
    private List<SystemSet> system;
    private LoginRegister lr;
    private WifiHotAdapter adapter;
    private String wifiName = "";
    private int wifiIndex=0;//判断是否 是从FragGetImage 中直接跳过来的
    private double screenInches;//屏幕的尺寸
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setting);
        init();
        Intent intent = getIntent();
         wifiIndex = intent.getIntExtra("wifiIndex", 0);
        if (wifiIndex ==1) {
            selectBtn(1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermission(CreateFileConstant.ASK_CALL_BLUE_PERMISSION)) {
            requestPermission(CreateFileConstant.ASK_CALL_BLUE_CODE, CreateFileConstant.ASK_CALL_BLUE_PERMISSION);
        } else {
            if (wifiIndex == 1) {
                refreshWifiList(getWifiList(this));
            }
        }
    }

    private void init(){
        clear = (Button) findViewById(R.id.btn_wifiSetting_clear01);
        save = (Button) findViewById(R.id.btn_wifiSetting_save01);
        selectBtnHP = (Button) findViewById(R.id.btn_wifisetting_hp);
        selectBtnGetImageSoft = (Button) findViewById(R.id.btn_wifisetting_getImageSoft);
        selectBtnHP.setOnClickListener(this);
        selectBtnGetImageSoft.setOnClickListener(this);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_left = (Button) findViewById(R.id.btn_left);//菜单项左边的按钮
        btn_left.setVisibility(View.VISIBLE);
        btn_left.setOnClickListener(this);
        editName = (EditText) findViewById(R.id.edit_wifiSetting_name01);
        editPass = (EditText) findViewById(R.id.edit_wifiSetting_pass01);
        title_text = (TextView) findViewById(R.id.title_text);

        btn_right.setVisibility(View.INVISIBLE);
        clear.setOnClickListener(this);
        save.setOnClickListener(this);
        screenInches = DeviceOfSize.getScreenSizeOfDevice2(this);
        if (screenInches > 6.0) {
            title_text.setText(getString(R.string.setting_wifi));
        } else {
            title_text.setText(getString(R.string.setting_wifi));
            title_text.setTextSize(15);
        }

        lr = new LoginRegister();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_wifiSetting_clear01:// 清除相关信息
                editName.setText("");
                editPass.setText("");
                Toast.makeText(this,R.string.setting_clear_success, Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_wifiSetting_save01://保存相关信息
                system= DataSupport.findAll(SystemSet.class);
                SystemSet system1 = null;
                if (wifiID==0){
                    if(system.size()==0){//数据为空时创建表
                        system1=new SystemSet();
                        Const.nameHP=(editName.getText().toString().trim());
                        Const.passHP=(editPass.getText().toString().trim());
                        system1.setPrinter_wifi_user(editName.getText().toString().trim());
                        system1.setPrinter_wifi_pwd(editPass.getText().toString().trim());
                    }else {
                        for(int i=0;i<system.size();i++){
                            system1=system.get(0);
                            Const.nameHP=(editName.getText().toString().trim());
                            system1.setPrinter_wifi_user(editName.getText().toString().trim());
                            system1.setPrinter_wifi_pwd(editPass.getText().toString().trim());
                           Const.passHP=(editPass.getText().toString().trim());
                        }
                    }
//                    Const.devModel = null;
                    system1.save();
                }else {

                    if(system.size()==0){//数据为空时创建表
                        system1=new SystemSet();
                        Const.nameShiZhenBao=editName.getText().toString().trim();
                        Const.passShiZhenBao=editPass.getText().toString().trim();
                        system1.setSzb_wifi_user(editName.getText().toString().trim());
                        system1.setSzb_wifi_pwd(editPass.getText().toString().trim());
                    }else {
                        for(int i=0;i<system.size();i++){
                            system1=system.get(0);
                            Const.nameShiZhenBao=editName.getText().toString().trim();
                            Const.passShiZhenBao=editPass.getText().toString().trim();
                            system1.setSzb_wifi_user(editName.getText().toString().trim());
                            system1.setSzb_wifi_pwd(editPass.getText().toString().trim());
                        }
                    }
                    system1.save();
                }


                if (Const.loginCount != 3) {
                    //这段代码是判断 图像获取界面的wifi设置对话框出现的次数
                    Doctor doctor = lr.getDoctor(OneItem.getOneItem().getName());
                    //查找医生登陆的次数
//                int doctorLoginCount = lr.getDoctorLoginCount(OneItem.getOneItem().getName());
                    if (doctor != null) {
                        doctor.setLoginCount(3);
                        doctor.updateAll("dName =? ", OneItem.getOneItem().getName());
                        Const.dialogWifiSetting = false;
                        Const.loginCount = 3;
                    } else {
//                        Const.loginCount = 1;
                    }
                }

                Toast.makeText(this, R.string.setting_picture_save_success, Toast.LENGTH_SHORT).show();

                finish();

                break;
            case R.id.btn_wifisetting_hp: //惠普打印机wifi设置
                selectBtn(0);
                refreshWifiList(getWifiList(this));
                break;
            case R.id.btn_wifisetting_getImageSoft://图像获取软件wifi设置
                selectBtn(1);
                refreshWifiList(getWifiList(this));
                break;
            case R.id.btn_left:
                finish();
                break;
        }
    }


    public void selectBtn(int mState) {
        selectBtnHP.setSelected(false);
        selectBtnGetImageSoft.setSelected(false);
        switch (mState) {
            case 0:
                wifiID = 0;
                editPass.setText(Const.passHP);
                selectBtnHP.setSelected(true);
                break;
            case 1:
                wifiID = 1;
                editPass.setText(Const.passShiZhenBao);
                selectBtnGetImageSoft.setSelected(true);

                break;
        }

    }

    @Override
    public void openGpsPermission() {
        super.openGpsPermission();
        if (wifiIndex == 1) {
            refreshWifiList(getWifiList(this));
        }

    }

    //给listview 列表填充数据 ,将搜索到的wifi显示在listview上
    private void refreshWifiList(List<ScanResult> results) {
        if (results.size() == 0) {
            openGPSSettings();
        } else {
            if (null == adapter) {
                adapter = new WifiHotAdapter(results, this);
            } else {
                adapter.refreshData(results);
            }
            lv_add(results);
        }
    }




    public static final List<ScanResult> getWifiList(Context context){
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        List<ScanResult> wifiList  = wifiManager.getScanResults();
        return wifiList.size()>0? wifiList:new ArrayList();
    }


    private void lv_add(final List<ScanResult> results){
        LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        SelectAdapter selectAdapter=new SelectAdapter(this,list);
//        adapter = new WifiHotAdapter(results, this);
        listView.setAdapter(adapter);
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.setting_wifi_name)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                wifiName = results.get(arg2).SSID;
                editName.setText(wifiName);
                dialog.cancel();
            }
        });
    }


    //跳到GPS设置界面
    private int GPS_REQUEST_CODE = 10;
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
//            initLocation(); //自己写的定位方法
        } else {
            //没有打开则弹出对话框
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.setting_gps))
                    .setMessage(getString(R.string.setting_gps_list))
                    // 拒绝, 退出应用
                    .setNegativeButton(R.string.button_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })

                    .setPositiveButton(R.string.setting_wifi_title,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, GPS_REQUEST_CODE);
                                }
                            })

                    .setCancelable(false)
                    .show();

        }
    }


}
