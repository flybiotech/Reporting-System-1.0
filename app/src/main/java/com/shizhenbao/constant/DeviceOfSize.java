package com.shizhenbao.constant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

/**
 * Created by dell on 2017/6/16.
 */

public class DeviceOfSize  {


    public static double getScreenSizeOfDevice2(Activity activity) {

        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        double x = Math.pow(point.x/ dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }


    public static void getMetric(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 屏幕宽度（像素）
        int height= dm.heightPixels; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width/density);//屏幕宽度(dp)
        int screenHeight = (int)(height/density);//屏幕高度(dp)
        Logger.e("Android设备的相关信息 "+"  屏幕宽度(像素) = "+width+"   屏幕高度(像素) = "+height+
                "  屏幕宽度(dp) = "+screenWidth+"  屏幕高度 = "+screenHeight);
        Log.e("PRETTY_LOGGER", "getMetric: "+ "  屏幕密度(density) = " +density +"  屏幕密度DPI = "+densityDpi );
    }

}
