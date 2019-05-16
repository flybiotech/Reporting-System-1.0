package com.application;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.Manager.LruBitmapCache;
import com.Manager.MyContext;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;
import org.litepal.LitePalApplication;


/**
 * Created by gyl1 on 3/31/17.
 */

public class MyApplication extends LitePalApplication {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public static final String TAG = MyApplication.class
            .getSimpleName();
    private static MyApplication mInstance;

    private static Context mContext;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        if (!MyContext.isInitialized()) {
            MyContext.init(getApplicationContext());
        }
        CrashHandler.getInstance().init(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        CrashReport.initCrashReport(getApplicationContext(), "50f7042afa", true);
        mContext = getApplicationContext();
    }
    public static Context getmContext(){
        return mContext;
    }
    public String getCountry() {
        String country = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        return country;
    }

    public static synchronized MyApplication getInstance() {

        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
