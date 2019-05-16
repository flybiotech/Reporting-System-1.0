package com.shizhenbao.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.Method;

public class SDUtils {

    private Context mContext;
    public SDUtils(Context context){
        this.mContext = context;
    }


    public File getSDFile(){
        String [] AllRoutePath = getAllSdPaths(mContext);
        if(AllRoutePath.length > 1){
            String SDPath = AllRoutePath [1];
            //1、判断sd卡是否可用
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File sdFile = new File(SDPath);
                String sdPath= mContext.getExternalFilesDir(null).getAbsolutePath();
                File file=new File(sdPath);
                boolean backup=true;//判断目录是否创建成功，
                if(!file.exists()){
                    backup= file.mkdirs();
                }
                if(backup){
                    return sdFile;
                }
            }
        }
        return null;
    }

    /**
     * 得到所有的存储路径（内部存储+外部存储）
     *
     * @param context
     * @return
     */
    public String[] getAllSdPaths(Context context) {
        Method mMethodGetPaths = null;
        String[] paths = null;
        //通过调用类的实例mStorageManager的getClass()获取StorageManager类对应的Class对象
        //getMethod("getVolumePaths")返回StorageManager类对应的Class对象的getVolumePaths方法，这里不带参数
        StorageManager mStorageManager = (StorageManager)context
                .getSystemService(context.STORAGE_SERVICE);//storage
        try {
            mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paths;
    }


}
