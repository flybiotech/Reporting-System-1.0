package com.shizhenbao.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.util.SPUtils;
import com.shizhenbao.wifiinfo.WifiAutoConnectManager;
import com.shizhenbao.util.Const;
import com.shizhenbao.wifiinfo.WifiConnectManager;
import com.util.SouthUtil;


//wifi联网测试
public class WIFITestActivity extends AppCompatActivity implements View.OnClickListener,WifiConnectManager.WifiConnectListener {
    private TextView tv01,title_text;
    private Button btnHP,btnSZB,btnStop,btn_left,btn_right;
    WifiManager wifiManager;
    WifiAutoConnectManager wifiAutoConnectManager;

    private String SZB_WIFI_NAME;
    private String SZB_WIFI_PASS;
    private String HP_WIFI_NAME;
    private String HP_WIFI_PASS;

//    private String timeMsg = "";

    //    private WifiInfo mWifiInfo;
    private static String TAG = "TAG_1_";
    //    boolean f=false; //y用在wifi连接上，
//    boolean f1=false;//用在4g网络连接上
    ConnectivityManager mConnectivityManager;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int i=msg.what;
            switch (i) {
                case 1://表示已经连接上了指定的网络

                    tv01.setText(getString(R.string.setting_link_wifi_success));
                    break;

                case 2: //正在连接中
                    int j=msg.arg1;
                    tv01.setText(getString(R.string.setting_link_wifi_test)+j/5.0f*1.0f+" s");

                    break;






                case 3:
                    tv01.setText(getString(R.string.setting_link_wifi_faild));
                    break;
                case 4:
                    tv01.setText(getString(R.string.setting_link_network_stop));
                    break;

                case 7:
                    tv01.setText(getString(R.string.setting_link_test));
                    break;
                default:
                    break;

            }

            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.wifitest);
        init();

    }
    private void init(){
        tv01 = (TextView) findViewById(R.id.tv01_wifiTest);
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText(getString(R.string.setting_Networking_test));
        btnHP = (Button) findViewById(R.id.btn01_wifiTest);
        btnSZB = (Button) findViewById(R.id.btn02_wifiTest);
//        btnG = (Button) findViewById(R.id.btn03_wifiTest);//3g/4g网络测试
        btnStop= (Button) findViewById(R.id.btn04_wifiTest);
        btn_left= (Button) findViewById(R.id.btn_left);//title左右两边的按钮
        btn_right= (Button) findViewById(R.id.btn_right);
        btn_left.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.GONE);
        btn_left.setOnClickListener(this);
        btnHP.setOnClickListener(this);
        btnSZB.setOnClickListener(this);
//        btnG.setOnClickListener(this);
        btnStop.setOnClickListener(this);
//        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        wifiAutoConnectManager = new WifiAutoConnectManager(wifiManager,this);
////        mWifiInfo = wifiManager.getConnectionInfo();
//        mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    @Override
    public void onClick(View v) {
        stopThread();
        switch (v.getId()) {
            case R.id.btn01_wifiTest: //连接惠普打印机


                HP_WIFI_NAME = (String) SPUtils.get(this, Const.HP_WIFI_SSID_KEY, "");
                HP_WIFI_PASS = (String) SPUtils.get(this, Const.HP_WIFI_PASS_KEY, "12345678");
                WifiConnectManager.getInstance().connectWifi(HP_WIFI_NAME, HP_WIFI_PASS, Const.WIFI_TYPE_HP, this);

                break;

            case R.id.btn02_wifiTest://连接主机
                SZB_WIFI_NAME = (String) SPUtils.get(this, Const.SZB_WIFI_SSID_KEY, "");
                SZB_WIFI_PASS = (String) SPUtils.get(this, Const.SZB_WIFI_PASS_KEY, "12345678");
                WifiConnectManager.getInstance().connectWifi(SZB_WIFI_NAME, SZB_WIFI_PASS, Const.WIFI_TYPE_SZB, this);

                break;


            case R.id.btn04_wifiTest: //停止网络测试
                handler.sendEmptyMessage(4);
                break;

            case R.id.btn_left:
                finish();
                break;
            default:
                break;

        }

    }


    private Thread mThread = null;

    private void setConnectTime() {

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while (true) {

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        return;
                    }

                    Message msg2 = new Message();
                    msg2.what=2;
                    msg2.arg1 = i;
                    handler.sendMessage(msg2);
                    i++;

                }


            }
        });

        mThread.start();


    }

    private void stopThread() {
        if (mThread!=null)mThread.interrupt();
    }


    @Override
    public void startWifiConnecting(String type) {
        setConnectTime();
        Log.i(TAG, "startWifiConnecting: 1 ");
    }




    @Override
    public void wifiConnectSuccess(String type) {
        stopThread();
        handler.sendEmptyMessage(1);
        Log.i(TAG, "wifiConnectSuccess: 2");
    }

    @Override
    public void wifiConnectFalid(String type) {
        stopThread();
        handler.sendEmptyMessage(3);
        Log.i(TAG, "wifiConnectFalid:3 ");
    }

    @Override
    public void wifiCycleSearch(String type, boolean isSSID, int count) {
        Log.i(TAG, "wifiCycleSearch: 4  isSSID = "+isSSID+"  , type = "+type);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSSID) {
                    if (type.equals(Const.WIFI_TYPE_HP)) {
                        WifiConnectManager.getInstance().connectWithWpa(HP_WIFI_NAME, HP_WIFI_PASS);
                    }else if (type.equals(Const.WIFI_TYPE_SZB)) {
                        WifiConnectManager.getInstance().connectWithWpa(SZB_WIFI_NAME, SZB_WIFI_PASS);
                    }

                }
            }
        });

    }

    @Override
    public void wifiInputNameEmpty(String type) {
        stopThread();
        handler.sendEmptyMessage(3);
        Log.i(TAG, "wifiInputNameEmpty: 5 ");
    }


    @Override
    protected void onStop() {
        super.onStop();

        WifiConnectManager.getInstance().stopThreadConnectWifi();
        stopThread();
    }
}
