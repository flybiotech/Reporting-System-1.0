package com.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.application.MyApplication;
import com.shizhenbao.UI.MyTitleView;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;
import com.southtech.thSDK.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by gyl1 on 12/22/16.
 */

public class SouthUtil {

    public static String ButtonIsAlignRightValue = "ButtonIsAlignRightValue";//默认按钮靠右
    public static String APP = "SouthUtil";
    private static Toast toast;
    private static MyTitleView tvMessage;

    public static void showDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.confirm_util), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void setButtonIsAlignRightValue(Context context, boolean type) {
        SharedPreferences agPreferences = context.getSharedPreferences(SouthUtil.APP, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = agPreferences.edit();
        editor.putBoolean(SouthUtil.ButtonIsAlignRightValue, type);
        editor.commit();
    }

    public static boolean getButtonIsAlignRightValue(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SouthUtil.APP, Activity.MODE_PRIVATE);
        boolean ring_tpe = preferences.getBoolean(SouthUtil.ButtonIsAlignRightValue, true);
        return ring_tpe;

    }

    //toast
    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();

    }


    /**
     * 创建自定义Toast
     */
    private static Toast mToast;

    public static void showToastInfo(Context context, String msg) {
        if (mToast == null) {
            mToast = new Toast(context);
            //设置Toast显示位置，居中，向 X、Y轴偏移量均为0
            mToast.setGravity(Gravity.CENTER, 0, 0);
            //获取自定义视图
            View view = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
            tvMessage = (MyTitleView) view.findViewById(R.id.toast_viewss);
            //设置文本颜色
            tvMessage.setTextColor("#F8F8FF");
            //设置视图
            mToast.setView(view);
            //设置显示时长
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        //设置文本
        tvMessage.setText(msg);
        //显示
        mToast.show();


    }


    public static int convertDpToPixel(float dp, Context context) {
        Resources resources;
        resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }


//dp 转px

    public static float dp2px(float dpValue) {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, Resources.getSystem().getDisplayMetrics());
    }


    //dp 转像素
    public static float getZforCamera(int z) {

        return -z * Resources.getSystem().getDisplayMetrics().density;
    }


    public static String getTimeSSMM(int total) {

        return String.format("%02d:%02d", total / 60, total % 60);
    }

    public static String getTimeYyyymmddhhmmss() {

        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String ctime = formatter.format(new Date());
        return ctime;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            // versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            //  Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static int getRandom() {
        int max = 100000;
        int min = 1;
        Random random = new Random();

        int s = random.nextInt(max) % (max - min + 1) + min;
        Log.e(tag, "random is " + s);
        return s;
    }

    static final String tag = "SouthUtil";


    private static String IMAGENAME = "";
    private static String cusuImage = "";

    public static String saveImageName() {
        String timeStamp = getTimeFileName();
        return "/fly" + timeStamp + "." + IMAGENAME + cusuImage + ".jpg";
    }

    //设置图片的类型。原图，碘油，醋酸白
    public static void setImageType(String imageNames) {
        IMAGENAME = imageNames;
    }

    public static void setCusuImageTime(int minute, int second) {
        if (minute == 0) {
            cusuImage = "";
        }
        cusuImage = "." + second + "." + minute + ".";

    }


    //    videoDir = /storage/emulated/0/SZB_save/Admin/2019-1-4/17-30-34_1_yey
    public static void saveVideo(Context context, String videoDir, String videoType, int videoLength, Handler mHandler) {
        String time = getTimeFileName();
        String videoName = "/fly" + getPathSplitName(videoType) + time + "_" + videoLength + ".mp4";
        File file = new File(videoDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(file.getAbsolutePath(), videoName);
        if (file1.exists()) {
            file1.delete();
        }
        try {
            file1.createNewFile();
        } catch (IOException e) {

        }
        Message msg = new Message();
        msg.what = 1;

        int i = lib.jlocal_StartRec(0, file1.getAbsolutePath());
        if (i == 1) {
            //正在保存视频
            msg.obj = context.getString(R.string.image_pickup_begin);
        } else {
            //视频保存失败
            msg.obj = context.getString(R.string.image_pickup_error);
        }

        if (mHandler != null) {
            mHandler.sendMessage(msg);
        }
    }


    private static String getTimeFileName() {
        final Calendar c = Calendar.getInstance();
        long year = c.get(Calendar.YEAR);
        long month = c.get(Calendar.MONTH) + 1;
        long day = c.get(Calendar.DAY_OF_MONTH);
        long hour = c.get(Calendar.HOUR_OF_DAY);
        long minute = c.get(Calendar.MINUTE);
        long mytimestamp = c.get(Calendar.MILLISECOND);
        String timeStamp = year + "" + month + "" + day + "_" + hour + "_" + minute + "_" + mytimestamp + "";
        return timeStamp;
    }

    private static String getPathSplitName(String filePath) {
//        Logger.e(" 文件名称v：filePath "+filePath);
        String imageCatagory = null;
        try {
            String[] split = filePath.split("[.]");
            if (split.length > 0) {
                imageCatagory = split[0];
                imageCatagory = changeInfo(imageCatagory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageCatagory;
    }

    private static String changeInfo(String filePath) {

        String imageCatagory = MyApplication.getContext().getString(R.string.image_artword);
        switch (filePath) {
            case "yuantu":
                imageCatagory = MyApplication.getContext().getString(R.string.image_artword);
                break;
            case "cusuanbai":
                imageCatagory = MyApplication.getContext().getString(R.string.image_acetic_acid_white);
                break;
            case "dianyou":
                imageCatagory = MyApplication.getContext().getString(R.string.image_Lipiodol);
                break;
            default:
                break;

        }
        return imageCatagory;
    }


}
