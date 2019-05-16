package com.shizhenbao.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.SPUtils;
import com.shizhenbao.wifiinfo.WifiHotAdapter;
import com.util.SouthUtil;
import com.view.MyToast;
import com.view.PasswordCharSequence;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WifiSettingActivity extends BaseActivity implements View.OnClickListener {
    private Button clear, save, btn_right, btn_left, selectBtnHP, selectBtnGetImageSoft;
    private EditText editName, editPass;
    private int wifiID;
    private TextView title_text;
    private List<SystemSet> system;
    private LoginRegister lr;
    private WifiHotAdapter adapter;
    private String wifiName = "";
    private int wifiIndex = 0;//判断是否 是从FragGetImage 中直接跳过来的 1 ：表示是从FragGetImage 那边跳过来的
    private double screenInches;//屏幕的尺寸
    private List<ScanResult> results = new ArrayList<ScanResult>();
    private String ssid = "";
    private String pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_wifi_setting);
        init();
        Intent intent = getIntent();
        wifiIndex = intent.getIntExtra("wifiIndex", 0);
        if (wifiIndex == 1) {//表示从FragmentImage跳转过来的
            SPUtils.put(this, Const.LOGINCOUNT, "1");
            Const.FIRSTSHOW = true;
            selectBtn(1);
        }
        initEditFormat();
    }

    private void initEditFormat() {
        editPass.setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence charSequence, View view) {
                return new PasswordCharSequence(charSequence);
            }

            @Override
            public void onFocusChanged(View view, CharSequence charSequence, boolean b, int i, Rect rect) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!hasPermission(CreateFileConstant.ASK_CALL_BLUE_PERMISSION)) {
            requestPermission(CreateFileConstant.ASK_CALL_BLUE_CODE, CreateFileConstant.ASK_CALL_BLUE_PERMISSION);
        } else {
            if (wifiIndex == 1) {
                refreshWifiList(this);
            } else {
                selectBtn(1);
                ssid=(String) SPUtils.get(this, Const.SZB_WIFI_SSID_KEY,  "");
                pass=(String) SPUtils.get(this, Const.SZB_WIFI_PASS_KEY,  "");
                editName.setText(ssid);
                editPass.setText(pass);
            }
        }


    }

    private void init() {
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
        switch (v.getId()) {
            case R.id.btn_wifiSetting_clear01:// 清除相关信息
                editName.setText("");
                editPass.setText("");
                MyToast.showToast(this,getString(R.string.setting_clear_success));
//                SouthUtil.showToast(this,getString(R.string.setting_clear_success));
                break;

            case R.id.btn_wifiSetting_save01://保存相关信息
//                system= DataSupport.findAll(SystemSet.class);
//                SystemSet system1 = null;
                if (wifiID == 0) {
                    SPUtils.put(this, Const.HP_WIFI_SSID_KEY, editName.getText().toString().trim() + "");
                    SPUtils.put(this, Const.HP_WIFI_PASS_KEY, editPass.getText().toString().trim() + "");

                } else {
                    SPUtils.put(this, Const.SZB_WIFI_SSID_KEY, editName.getText().toString().trim() + "");
                    SPUtils.put(this, Const.SZB_WIFI_PASS_KEY, editPass.getText().toString().trim() + "");
                }
                MyToast.showToast(this,getString(R.string.setting_picture_save_success));
//                SouthUtil.showToast(this, getString(R.string.setting_picture_save_success));
                finish();

                break;
            case R.id.btn_wifisetting_hp: //惠普打印机wifi设置
                selectBtn(0);
                refreshWifiList(this);
                break;
            case R.id.btn_wifisetting_getImageSoft://图像获取软件wifi设置
                selectBtn(1);
                refreshWifiList(this);
                break;
            case R.id.btn_left:
                finish();
                break;
            default:
                break;


        }
    }


    public void selectBtn(int mState) {
        selectBtnHP.setSelected(false);
        selectBtnGetImageSoft.setSelected(false);
        switch (mState) {
            case 0:
                wifiID = 0;

                editName.setText((String) SPUtils.get(this, Const.HP_WIFI_SSID_KEY, ""));
                editPass.setText((String) SPUtils.get(this, Const.HP_WIFI_PASS_KEY, "12345678"));
                selectBtnHP.setSelected(true);
                break;
            case 1:
                wifiID = 1;
                editName.setText((String) SPUtils.get(this, Const.SZB_WIFI_SSID_KEY, ""));
                editPass.setText((String) SPUtils.get(this, Const.SZB_WIFI_PASS_KEY, "12345678"));
                selectBtnGetImageSoft.setSelected(true);
                break;
            default:
                break;
        }

    }

    @Override
    public void openGpsPermission() {
        super.openGpsPermission();
        if (wifiIndex == 1) {
//            refreshWifiList(getWifiList(this));
            refreshWifiList(this);
        }

    }


    //给listview 列表填充数据 ,将搜索到的wifi显示在listview上
    private void refreshWifiList(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        List<ScanResult> scan = wifiManager.getScanResults();
        //如果没有搜索到wifi，就开启GPS
        if (scan.size() == 0) {
            openGPSSettings();
        } else {
            if (results == null) {
                results = new ArrayList<ScanResult>();
            }
            results.clear();

            Observable.from(scan)
                    .filter(new Func1<ScanResult, Boolean>() {
                        @Override
                        public Boolean call(ScanResult scanResult) {
                            return scanResult.SSID.contains(Const.HP_SSID_FILTER) || scanResult.SSID.contains(Const.SZB_SSID_FILTER);
                        }
                    }).map(new Func1<ScanResult, ScanResult>() {
                @Override
                public ScanResult call(ScanResult scanResult) {
                    return scanResult;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ScanResult>() {
                        @Override
                        public void onCompleted() {
                            if (null == adapter) {
                                adapter = new WifiHotAdapter(results, WifiSettingActivity.this);
                            } else {
                                adapter.refreshData(results);
                            }
                            lv_add(results);

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(ScanResult scanResult) {
                            results.add(scanResult);
                        }
                    });
        }
    }


    private void lv_add(final List<ScanResult> results) {
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
