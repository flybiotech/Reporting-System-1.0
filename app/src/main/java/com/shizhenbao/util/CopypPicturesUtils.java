package com.shizhenbao.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.activity.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化一些图片到本地，在生成PDF报告时使用
 */
public class CopypPicturesUtils {
    List<Bitmap>list=new ArrayList<>();
    private Context mContext;
    public CopypPicturesUtils(Context mContext){
        this.mContext = mContext.getApplicationContext();
    }

    /**
     * 将软件中的一些图片复制到本地，如果存在则不进行复制
     */
    public void initCopyPicture(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Resources res = mContext.getResources();
                String routePath = new Item().getSD().getAbsolutePath();
                String blankPath = routePath+"/AFLY/blank.png";
                String ReferencePath = routePath+ "/AFLY/refrence.png";
                String PositionPath = routePath + "/AFLY/position.png";
                if(!new File(blankPath).exists()){
                    BitmapDrawable blank = (BitmapDrawable) res.getDrawable(R.drawable.kb);//app中自带的空白的图片
                    initCopyCurrency(blank,blankPath);
                }
                if(!new File(ReferencePath).exists()){
                    BitmapDrawable refrence = (BitmapDrawable) res.getDrawable(R.drawable.gjt);
                    initCopyCurrency(refrence,ReferencePath);
                }
                if(!new File(PositionPath).exists()){
                    BitmapDrawable position = (BitmapDrawable) res.getDrawable(R.drawable.fwt_1);
                    initCopyCurrency(position,PositionPath);
                }
            }
        }).start();

    }

    private void initCopyCurrency(BitmapDrawable bitmapDrawable,String Path){
        Bitmap bitmap = bitmapDrawable.getBitmap();
        try {
            OutputStream os = new FileOutputStream(Path);//输出流
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);//设置图片的大小
            os.close();//关闭流
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

}
