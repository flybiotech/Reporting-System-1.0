package com.shizhenbao.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import android.widget.Toast;

import com.view.MyToast;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 手机内存操作工具类
 */
public class MemoryUtils {
    private Context mContext;

    public MemoryUtils(Context mContext){
        this.mContext = mContext;
    }
    /**
     * 判断可用内存是否足够，当可用内存小于20%时，提示清理内存
     * @return
     */
    public  void isEnoughMemory(){
        String TotalMemory=getSDMemory();//手机总内存
        String AvailableMemory=getSDMemoryky();//手机可用内存
        String memory_new="1";
        String memory1_new="1";
        //小米手机返回字节内容比较特殊，一般为“吉字节”
        if(TotalMemory.contains("吉")){
            memory_new=TotalMemory.substring(0,TotalMemory.indexOf("吉"));
            memory1_new=AvailableMemory.substring(0,AvailableMemory.indexOf("吉"));
        }else if(TotalMemory.contains("G")){//普通手机返回内容
            memory_new=TotalMemory.substring(0,TotalMemory.indexOf("G"));
            memory1_new=AvailableMemory.substring(0,AvailableMemory.indexOf("G"));
        }
        if(Double.parseDouble(memory1_new)/Double.parseDouble(memory_new)<0.2){
            MyToast.showToast(mContext,"内存已不足，请进行备份");
//            Toast.makeText(mContext, "内存已不足，请进行备份", Toast.LENGTH_SHORT).show();
        }
    }

        /*
        得到手机总内存大小
        */
    public String getSDMemory(){
        File file= Environment.getDataDirectory();
        StatFs statFs=new StatFs(file.getPath());
        long blockSize=statFs.getBlockSize();
        long totalBlocks=statFs.getBlockCount();
        return Formatter.formatFileSize(mContext,blockSize*totalBlocks);
    }
        /*
        得到手机可用内存
        */
    public String getSDMemoryky(){
        File file=Environment.getDataDirectory();
        StatFs statFs=new StatFs(file.getPath());
        long blockSize=statFs.getBlockSize();
        long avai=statFs.getAvailableBlocks();
        return Formatter.formatFileSize(mContext,blockSize*avai);
    }

    /**
     * 判断手机是否安装SD卡
     */
    public void initSDJudge(){

    }
}
