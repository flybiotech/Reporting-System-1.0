package com.shizhenbao.constant;

import android.Manifest;
import android.os.Environment;
import android.widget.Toast;

import com.shizhenbao.pop.User;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;

import java.io.File;
import java.util.Calendar;

/**
 * Created by dell on 2017/5/22.
 */

public class CreateFileConstant {
    /**
     * 权限常量相关
     */
    public static CreateFileConstant createFileConstant;
    public static final int WRITE_READ_EXTERNAL_CODE = 0x01;
    public static final String[] WRITE_READ_EXTERNAL_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final int HARDWEAR_CAMERA_CODE = 0x02;
    public static final String[] HARDWEAR_CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};

    public static final int ASK_CALL_BLUE_CODE= 0x03;//权限申请的返回值
    public static final String[] ASK_CALL_BLUE_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};



    public void initCreateDocPath(String name){//创建医生目录
        File file=new File(new Item().getSD()+"/"+OneItem.getOneItem().getGather_path()+"/"+name);
        if(!file.exists()){
            file.mkdirs();
        }
    }
    public void initCreateFLY(){//创建医生目录
        File file=new File(new Item().getSD()+"/AFLY");
        if(!file.exists()){
            file.mkdirs();
        }
    }
    public void initCreateDocLogPath(){//当医生登录后回创建当天的诊断日期目录
        File appDir = new File(Environment.getExternalStorageDirectory(),OneItem.getOneItem().getGather_path()+"/"+ OneItem.getOneItem().getName()+"/"+getCurrentDate());//创建医生诊断信息的文件夹
        if (!appDir.exists()){
            appDir.mkdirs();
        }
    }
    public void initCreateUser(User user, String UserPath){//当病人登记后创建该患者的文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(),user.getGatherPath()+OneItem.getOneItem().getName()+"/"+getCurrentDate()+"/"+getCurrentTime()+"_"+UserPath);
        if (!appDir.exists()){
            appDir.mkdirs();
        }
        user.setGatherPath(Environment.getExternalStorageDirectory()+user.getGatherPath()+OneItem.getOneItem().getName()+"/"+getCurrentDate()+"/"+getCurrentTime()+"_"+UserPath);
        user.save();
    }
    public void initCreateCutPath(User user,String UserPath,String ShowPath){//创建医生剪辑图片的文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(),OneItem.getOneItem().getGather_path()+"/"+OneItem.getOneItem().getName()+"/"+getCurrentDate()+"/"+getCurrentTime()+"_"+UserPath+"/"+ShowPath);
        if (!appDir.exists()){
            appDir.mkdirs();
        }
        user.setCutPath(Environment.getExternalStorageDirectory()+"/"+user.getGatherPath()+"/"+OneItem.getOneItem().getName()+"/"+getCurrentDate()+"/"+getCurrentTime()+"_"+UserPath+"/"+ShowPath);
        user.save();
    }
    //患者登记的时间 格式 2017-5-25
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + (1 + month) + "-" + day;
    }
    //患者等记的时间 格式 2017:5:25
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        return hour + "-" + (1 + minute) + "-" + second;
    }

    //创建单例模式
    public static CreateFileConstant getInstance() {
        if (createFileConstant == null) {
            synchronized (CreateFileConstant.class) {
                if (createFileConstant == null) {
                    createFileConstant = new CreateFileConstant();
                }
            }
        }
        return createFileConstant;
    }

}
