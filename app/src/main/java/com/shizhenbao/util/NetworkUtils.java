package com.shizhenbao.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.activity.R;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private Context mContext;
    private static URL url;//请求的url地址
    private static int state=-1;//网络请求返回值
    private static HttpURLConnection urlConnection;
    private MyProgressDialog myProgressDialog;
    public NetworkUtils(Context mContext){
        this.mContext = mContext;
        myProgressDialog = new MyProgressDialog(mContext);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myProgressDialog.dialogCancel();
        }
    };
    /**
     * 网络是否连接
     */
    public boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager)mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        // 去进行判断网络是否连接
        return (info != null && info.isAvailable());
    }

    /**
     * 是否可以进行网络访问
     */
    public void getUrl(int temp){//测试网络是否可用与上网,temp=0是备份，temp=1是恢复
        myProgressDialog.dialogShow(mContext.getString(R.string.setting_link_test));
        new Thread(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {

                    url=new URL(Const.NetworkUrl);
                    urlConnection= (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000);//设置连接超时时间
                    state=urlConnection.getResponseCode();
                    if(state==302){
                        if(netWorkTestResult !=null){
                            netWorkTestResult.NetworkSuccess(temp);
                        }
                        return;
                    }else {
                        if(netWorkTestResult !=null){
                            netWorkTestResult.NetworkFaild();
                        }
                    }

                } catch (Exception e) {
                    if(netWorkTestResult !=null){
                        netWorkTestResult.NetworkFaild();
                    }
                    return;
                }finally {
                    {
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }

    public interface NetWorkTestResult{
        void NetworkSuccess(int temp);
        void NetworkFaild();
    }
    static NetWorkTestResult netWorkTestResult;
    public static void getNetworkResultListener(NetWorkTestResult NetworkResultListener){
        netWorkTestResult = NetworkResultListener;
    }
}
