package com.shizhenbao.db;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.shizhenbao.wifiinfo.WifiAutoConnectManager;


/**
 * Created by fly on 2017/4/6.
 */

public class AlertDialogDao {
    private WifiAutoConnectManager wifiAutoConnectManager;
    WifiManager wifiManager;
    public AlertDialogDao(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiAutoConnectManager = new WifiAutoConnectManager(wifiManager,context);

    }

//    public void showAlertDialog(final Context context, final String name, final String pass, final int type) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setTitle("未连接到惠普打印机的wifi");
//        dialog.setMessage("您要连接到惠普打印机吗？");
//        dialog.setCancelable(false);
//        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                wifiAutoConnectManager.connect(name, pass, type);
//
//            }
//        });
//        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(context, "暂时不连接", Toast.LENGTH_SHORT).show();
//            }
//        });
//        dialog.show();
//    }

}
