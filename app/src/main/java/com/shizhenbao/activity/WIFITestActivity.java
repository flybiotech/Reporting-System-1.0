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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.wifiinfo.WifiAutoConnectManager;
import com.shizhenbao.util.Const;


//wifi联网测试
public class WIFITestActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv01,title_text;
    private Button btnHP,btnSZB,btnG,btnStop,btn_left,btn_right;
    WifiManager wifiManager;
    WifiAutoConnectManager wifiAutoConnectManager;
    private WifiInfo mWifiInfo;
    private static String TAG = "TAG003";
    boolean f=false; //y用在wifi连接上，
    boolean f1=false;//用在4g网络连接上
    ConnectivityManager mConnectivityManager;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int i=msg.what;
            switch (i) {
                case 1://表示已经连接上了指定的网络
                    Log.d(TAG, "WIFITestActivit  jiushi111111");
                    tv01.setText(getString(R.string.setting_link_wifi_success));
                    break;

                case 2:
                    Log.d(TAG, "WIFITestActivity :  ggafdsgasdfga");
                    int j=msg.arg1;
                    tv01.setText(getString(R.string.setting_link_wifi_test)+j/5*1.0f+" s");

                    break;

                case 3:
                    tv01.setText(getString(R.string.setting_link_wifi_faild));
                    break;
                case 4:
                    tv01.setText(getString(R.string.setting_link_network_stop));
                    break;
                case 5:
                    tv01.setText(getString(R.string.setting_link_network_success));
                    break;
                case 6:
                    tv01.setText(getString(R.string.setting_link_network_faild));
                    break;

                case 7:
                    tv01.setText(getString(R.string.setting_link_test));
                    break;

            }

            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiAutoConnectManager = new WifiAutoConnectManager(wifiManager,this);
        mWifiInfo = wifiManager.getConnectionInfo();
        mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn01_wifiTest: //连接惠普打印机
                handlerMessagess(7);

                if (!f) {
                    wifiAutoConnectManager.connect(Const.nameHP, Const.passHP, Const.typeWifi);
                    f = true;
                    getWifiConnect(Const.nameHP);
                } else {
                    Toast.makeText(this,R.string.setting_link_wifi_testting, Toast.LENGTH_SHORT).show();
                } 
               
                f1 = false;
                
                break;

            case R.id.btn02_wifiTest://连接世珍宝
                handlerMessagess(7);
                if (!f) {
                    wifiAutoConnectManager.connect( Const.nameShiZhenBao, Const.passShiZhenBao,Const.typeWifi);//连接视珍宝的wifi
                    f = true;
                    getWifiConnect(Const.nameShiZhenBao);
                } else {
                    Toast.makeText(this, R.string.setting_link_wifi_testting, Toast.LENGTH_SHORT).show();
                }

                f1 = false;

                break;

            case R.id.btn03_wifiTest://关闭wifi，连接3g/4g网络
//                handlerMessagess(7);
//                if (wifiManager.isWifiEnabled()) {
//                wifiManager.setWifiEnabled(false);
//            }
//
//                f = false;
//                f1 = true;
////                toggleMobileData(this,false);
//                getMobileConnect();
//                Toast.makeText(this, "正在等待3g/4g的网络连接", Toast.LENGTH_SHORT).show();
                break;
            
            case R.id.btn04_wifiTest: //停止网络测试
                f=false;
                f1 = false;
                Message msg = new Message();
                msg.what=4;
                handler.sendMessage(msg);
                break;

            case R.id.btn_left:
                finish();
                break;
           
        }

    }

    public void   getWifiConnect(final String string) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while (f) {
                    try {
                        Message msg2 = new Message();
                        msg2.what=2;
                        msg2.arg1 = i;
                        handler.sendMessage(msg2);
                        WifiInfo mWifiInfo=null;
                        NetworkInfo info= mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (info.isConnected()) {
                            mWifiInfo = wifiManager.getConnectionInfo();
                            String ssid = mWifiInfo.getSSID();
                            if (ssid != null && ssid.equals("\""+string+"\"")) {
                                Message msg = new Message();
                                msg.what=1;
                                handler.sendMessage(msg);
                                f = false;
                            }
                        }
                        if (i > 100) {
                            Message msg = new Message();
                            msg.what = 3;
                            handler.sendMessage(msg);
                            f = false;
                        }
                        i++;
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        }

        private void getMobileConnect(){
            boolean isPad = isPad(this);
            if (isPad) {
                handlerMessagess(6);
                return;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int i=0;
                        while (f1){
                            NetworkInfo.State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                            if(NetworkInfo.State.CONNECTED==state){
                                Log.d(TAG, "GPRS网络已连接");
                                Message msg = new Message();
                                msg.what = 5;
                                handler.sendMessage(msg);
                                f = false;
                            }
                            if (i > 100) {
                                Message msg = new Message();
                                msg.what = 6;
                                handler.sendMessage(msg);
                                f1 = false;
                            }
                            i++;
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();

            }


        }

    //判断当前设备能否打电话，是否有打电话的功能
    public static boolean isPad(Activity activity) {
        TelephonyManager telephony = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return true;
        }else {
            return false;
        }
    }
    private void handlerMessagess(int msg1) {
        Message msg = new Message();
        msg.what = msg1;
        handler.sendMessage(msg);
    }
}
