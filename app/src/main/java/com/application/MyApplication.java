package com.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.Manager.MyContext;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.litepal.LitePalApplication;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.Volley;

/**
 * Created by gyl1 on 3/31/17.
 */

public class MyApplication extends LitePalApplication {
//    private RequestQueue mRequestQueue;
//    private ImageLoader mImageLoader;
    public static final String TAG = MyApplication.class
            .getSimpleName();
    private static MyApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        if (!MyContext.isInitialized()) {
            MyContext.init(getApplicationContext());
        }
        CrashHandler.getInstance().init(this);
        Logger.addLogAdapter(new AndroidLogAdapter());



    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);

    }

    public String getCountry() {
        String country = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        return country;
    }

    public static synchronized MyApplication getInstance() {

        return mInstance;
    }
//    public RequestQueue getRequestQueue() {
//        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//        }
//        return mRequestQueue;
//    }
//
//    public ImageLoader getImageLoader() {
//        getRequestQueue();
//        if (mImageLoader == null) {
//            mImageLoader = new ImageLoader(this.mRequestQueue,
//                    new LruBitmapCache());
//        }
//        return this.mImageLoader;
//    }
//
//    public <T> void addToRequestQueue(Request<T> req, String tag) {
//        // set the default tag if tag is empty
//        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//        getRequestQueue().add(req);
//    }
//
//    public <T> void addToRequestQueue(Request<T> req) {
//        req.setTag(TAG);
//        getRequestQueue().add(req);
//    }
//
//    public void cancelPendingRequests(Object tag) {
//        if (mRequestQueue != null) {
//            mRequestQueue.cancelAll(tag);
//        }
//    }
}
