package com.shizhenbao.util;

/**
 * Created by Administrator on 2017/6/24.
 */

public class ImgFun {

    static {
        System.loadLibrary("ImgFun");
    }
    public static native int blurDetect(int []buf,int w,int h,int yu);
}