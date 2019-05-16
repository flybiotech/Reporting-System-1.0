package com.shizhenbao.wifiinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * Created by dell on 2017/10/23.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {

    private NetEvent netEvent;

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//            int netWrokState = NetUtil.getNetWorkState(context);
//            if (netEvent != null){
//                // 接口回传网络状态的类型
//                netEvent.onNetChange(netWrokState);
//            }
//
//        }
    }

    public void setNetEvent(NetEvent netEvent) {
        this.netEvent = netEvent;
    }

}