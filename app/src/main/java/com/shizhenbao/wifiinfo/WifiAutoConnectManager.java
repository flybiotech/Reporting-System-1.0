package com.shizhenbao.wifiinfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.shizhenbao.util.Const;
import com.shizhenbao.util.LogUtil;
import com.shizhenbao.util.SPUtils;

import java.util.List;

/**
 * Created by 用户 on 2017/5/3.
 */

public class WifiAutoConnectManager {
    private static final String TAG = "TAG_WifiAutoConnectManager";
    private ConnectWifiIsRepeatListener connectWifiIsRepeatListener;
    private WifiManager wifiManager;
    private Context mContext;
    NetworkInfo.State wifi = null;
    private String msgSSID = "";
    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
//    public enum WifiCipherType {
//        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
//    }
    private ConnectivityManager conMan;

    // 构造函数
    public WifiAutoConnectManager(WifiManager wifiManager, Context context) {

        this.wifiManager = wifiManager;
        mContext = context;
        conMan = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    // 提供一个外部接口，传入要连接的无线网
    public void connect(String ssid, String password, int type) {

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        msgSSID = ssid;
        String ssid1 = wifiInfo.getSSID();
        if (!ssid1.equals("")&&ssid1.equals("\"" + ssid + "\"") && NetworkInfo.State.CONNECTED == wifi) {
            Const.wifiRepeat = true;
        } else {
            Const.wifiRepeat = false;
            Thread thread = new Thread(new ConnectRunnable(ssid, password, getCipherType(mContext, ssid)));
            thread.start();
        }
//        LogUtil.e(TAG," Const.wifiRepeat = "+ Const.wifiRepeat+"  password = "+password);

    }

    int mPriority = 10;

    // 查看以前是否也配置过这个网络
    @Nullable //这个表示可以传入空值
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs == null) {
            return null;
        }

        for (int i = 0; i < existingConfigs.size(); i++) {
            //关闭所有的可以链接的wifi
            wifiManager.disableNetwork(existingConfigs.get(i).networkId);
            wifiManager.removeNetwork(existingConfigs.get(i).networkId);
            wifiManager.saveConfiguration();

        }

        for (WifiConfiguration existingConfig : existingConfigs) {
//             LogUtil.e(TAG,"isExsits ssid = "+existingConfig.SSID);
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }


    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             int Type) {
//        mPriority++;
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.priority = 0;
        config.SSID = "\"" + SSID + "\"";
        LogUtil.e(TAG,"SSID = "+SSID+" Password =  "+Password+"    Type = " + Type);
        if (Type == 1) { //没有密码的情况
//            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//
        }
        // wep
        if (Type == 2) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
//            config.priority = priority+20;
        }


        // wpa
        if (Type == 3) {  //WIFICIPHER_WPA WPA加密的情况
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
//            config.priority = priority+20;

        }
        return config;
    }

    // 打开wifi功能
    private boolean openWifi() {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    // 关闭WIFI
    private void closeWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }



    class ConnectRunnable implements Runnable {
        private String ssid;

        private String password;

        private int type;


        public ConnectRunnable(String ssid, String password, int type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;

        }

        @Override
        public void run() {
            // 打开wifi
            openWifi();
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句

            while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                try {
                    // 为了避免程序一直while循环，让它睡个100毫秒检测……
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                }
            }

            WifiConfiguration tempConfig = isExsits(ssid);
            if (tempConfig != null) {
//                LogUtil.e(TAG,"tempConfig != null");
                boolean b = wifiManager.enableNetwork(tempConfig.networkId,
                        true);
                wifiManager.saveConfiguration();
                wifiManager.reconnect();

//                boolean connected = false;
                if (!b) {
                    wifiManager.reconnect();
                }

            } else {
                LogUtil.e(TAG,"tempConfig == null ");
                WifiConfiguration wifiConfig = createWifiInfo(ssid, password,
                        type);
                if (wifiConfig == null) {
                    return;
                }
                int netID = wifiManager.addNetwork(wifiConfig);
//                wifiManager.setWifiEnabled(true);
                wifiManager.enableNetwork(netID, true);
                wifiManager.saveConfiguration();
                wifiManager.reconnect();
            }
            //只有连接视珍宝的WiFi时，才去做重复查询WiFi是否连接成功
            LogUtil.e("TAG_","msgSSID = "+msgSSID+" , szb_wifi = "+SPUtils.get(mContext,Const.SZB_WIFI_SSID_KEY,"-1"));
            if (msgSSID.equals((String) SPUtils.get(mContext,Const.SZB_WIFI_SSID_KEY,"-1"))) {
                try {
                    Thread.sleep(10*1000);
                    connectWifiIsRepeatListener.onRepeatConnect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    // 获取ssid的加密方式

    public static int getCipherType(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        List<ScanResult> list = wifiManager.getScanResults();

        for (ScanResult scResult : list) {

            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;

                if (!TextUtils.isEmpty(capabilities)) {

                    if (capabilities.contains("WPA")
                            || capabilities.contains("wpa")) {
                        return 3;//WifiCipherType.WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP")
                            || capabilities.contains("wep")) {
                        return 2;//WifiCipherType.WIFICIPHER_WEP;
                    } else {
                        return 1;//WifiCipherType.WIFICIPHER_NOPASS;
                    }
                }
            }
        }
        return 3;// WifiCipherType.WIFICIPHER_INVALID;
    }



    public void setConnectWifiIsRepeatListener(ConnectWifiIsRepeatListener listener) {
        connectWifiIsRepeatListener = listener;
    }

    public interface ConnectWifiIsRepeatListener{
        void onRepeatConnect();

    }


}
