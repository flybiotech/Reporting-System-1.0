package com.shizhenbao.wifiinfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.shizhenbao.util.Const;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by 用户 on 2017/5/3.
 */

public class WifiAutoConnectManager {
    private static final String TAG = "TAG_WifiAutoConnectManager";

    private WifiManager wifiManager;
    private Context mContext;
    NetworkInfo.State wifi = null;
    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
//    public enum WifiCipherType {
//        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
//    }
    private ConnectivityManager conMan;
    // 构造函数
    public WifiAutoConnectManager(WifiManager wifiManager, Context context) {

        this.wifiManager = wifiManager;
        mContext = context;
        conMan= (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    // 提供一个外部接口，传入要连接的无线网
    public void connect(String ssid, String password, int type) {

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
         wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();

        String ssid1 = wifiInfo.getSSID();
//        Logger.e("ssid1 :"+ssid1+ "  ssid: "+ssid+" wifi 状态："+wifi);
        if (ssid1.equals("\"" + ssid + "\"")&& NetworkInfo.State.CONNECTED==wifi) {
            Const.wifiRepeat = true;
        } else {
            Const.wifiRepeat = false;
            Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
            thread.start();
        }

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

//            boolean b = wifiManager.removeNetwork(existingConfigs.get(i).networkId);
//            Log.e(TAG, "删除已经保存的wifi: "+b );
            wifiManager.disableNetwork(existingConfigs.get(i).networkId);

        }
//        Log.e(TAG,"  existingConfig.priority：  " + mPriority);

        for (WifiConfiguration existingConfig : existingConfigs) {
//            Log.e(TAG, "isExsits: "+existingConfig.SSID+"/n" );

            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration  createWifiInfo(String SSID, String Password,
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
        if (Type == 1) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // wep
        if (Type ==2) {
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

        private int  type;

//        private int mPriority;//wifi 的权限

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
//                    Log.d("1111", "run: 正在打开wifi");
                } catch (InterruptedException ie) {
//                    Log.e(TAG, ie.toString());
                }
            }

            WifiConfiguration tempConfig = isExsits(ssid);

            if (tempConfig != null) {
//                wifiManager.setWifiEnabled(true);
//                Log.e(TAG, "tempConfig. "+" tempConfig.SSID  : "+tempConfig.SSID+"   networkId: "+tempConfig.networkId );
                boolean b = wifiManager.enableNetwork(tempConfig.networkId,
                        true);

                boolean connected = false;
                if (!b) {
                     connected = wifiManager.reconnect();
                }

//                Log.e(TAG, "save enableNetwork status enable=" + b+" connected 1  "+connected);

            }
            else {
//                Log.e(TAG, "run: 但没有保存wifi信息时，就创新新的链接" );
                WifiConfiguration wifiConfig = createWifiInfo(ssid, password,
                        type);
//                wifiConfig.priority = mPriority;
//                Log.e(TAG,"create  SSID ; "+wifiConfig.SSID+ "wifi_priority 1: "+wifiConfig.priority );
                if (wifiConfig == null) {
//                    Log.e(TAG, "wifiConfig is null!");
                    return;
                }
                int netID = wifiManager.addNetwork(wifiConfig);
                boolean enabled = wifiManager.enableNetwork(netID, true);
//                Log.e(TAG, "create enableNetwork status enable=" + enabled);
                boolean connected = wifiManager.reconnect();
//                Log.e(TAG, "create enableNetwork connected=" + connected);
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

    public static int  getCipherType(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        List<ScanResult> list = wifiManager.getScanResults();

        for (ScanResult scResult : list) {

            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                // Log.i("hefeng","capabilities=" + capabilities);

                if (!TextUtils.isEmpty(capabilities)) {

                    if (capabilities.contains("WPA")
                            || capabilities.contains("wpa")) {
                        Log.i("hefeng", "wpa");
                        return  3;//WifiCipherType.WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP")
                            || capabilities.contains("wep")) {
                        Log.i("hefeng", "wep");
                        return  2;//WifiCipherType.WIFICIPHER_WEP;
                    } else {
                        Log.i("hefeng", "no");
                        return   1;//WifiCipherType.WIFICIPHER_NOPASS;
                    }
                }
            }
        }
        return  0;// WifiCipherType.WIFICIPHER_INVALID;
    }


    /**
     * 通过反射出不同版本的connect方法来连接Wifi
     *
     * @author jiangping.li
     * @param netId
     * @return
     * @since MT 1.0
     *
     */
//    private Method connectWifiByReflectMethod(int netId) {
//        Method connectMethod = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//
//            System.out.println("connectWifiByReflectMethod road 1");
//            // 反射方法： connect(int, listener) , 4.2 <= phone‘s android version
//            for (Method methodSub : wifiManager.getClass().getDeclaredMethods()) {
//                if ("connect".equalsIgnoreCase(methodSub.getName())) {
//                    Class<?>[] types = methodSub.getParameterTypes();
//                    if (types != null && types.length > 0) {
//                        if ("int".equalsIgnoreCase(types[0].getName())) {
//                            connectMethod = methodSub;
//                        }
//                    }
//                }
//            }
//            if (connectMethod != null) {
//                try {
//                    connectMethod.invoke(wifiManager, netId, null);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    System.out.println("connectWifiByReflectMethod Android "
//                            + Build.VERSION.SDK_INT + " error!");
//
//                    return null;
//                }
//            }
//        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
//            // 反射方法: connect(Channel c, int networkId, ActionListener listener)
//            // 暂时不处理4.1的情况 , 4.1 == phone‘s android version
//            System.out.println("connectWifiByReflectMethod road 2");
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
//                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            System.out.println("connectWifiByReflectMethod road 3");
//            // 反射方法：connectNetwork(int networkId) ,
//            // 4.0 <= phone‘s android version < 4.1
//            for (Method methodSub : wifiManager.getClass()
//                    .getDeclaredMethods()) {
//                if ("connectNetwork".equalsIgnoreCase(methodSub.getName())) {
//                    Class<?>[] types = methodSub.getParameterTypes();
//                    if (types != null && types.length > 0) {
//                        if ("int".equalsIgnoreCase(types[0].getName())) {
//                            connectMethod = methodSub;
//                        }
//                    }
//                }
//            }
//            if (connectMethod != null) {
//                try {
//                    connectMethod.invoke(wifiManager, netId);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    System.out.println("connectWifiByReflectMethod Android "
//                            + Build.VERSION.SDK_INT + " error!");
//                    return null;
//                }
//            }
//        } else {
//            // < android 4.0
//            return null;
//        }
//        return connectMethod;
//    }
//
//    public String getCurrentWifiname() {
//        try {
//            if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){  //wifi已经打开
//                WifiInfo info = wifiManager.getConnectionInfo();
//                String wifiId = info != null ? info.getSSID() : null;
//
//                return wifiId;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



}
