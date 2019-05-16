package com.shizhenbao.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by 用户 on 2017/4/28.
 */

public class DisplayMetricss {

    private Context context;

    public DisplayMetricss(Context context){
        this.context=context;
    }

    //获取屏幕的分辨率
    public float getDisplayMetricss(){
//        DisplayMetricss displayMetrics = new DisplayMetricss(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
         float screenWidth  = dm.widthPixels;      // 屏幕宽（像素，如：480px）
         float screenHeight = dm.heightPixels;     // 屏幕高（像素，如：800px）
         return  screenWidth;
    }
}
