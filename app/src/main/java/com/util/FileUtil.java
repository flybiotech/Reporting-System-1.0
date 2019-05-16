package com.util;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;

import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;

/**
 * 包含对操作文件及访问的SD卡一些方法
 * @author gyl
 *
 */
public class FileUtil {
    private final static String TAG = "FileUtil";


    public static boolean checkSD() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    /**
     * 获取SD卡根目录路径
     * @return 表示根目录路径字符串（末尾不带“/”，如"/sdcard"),若SD卡未安装，则返回null
     */
    public static String getSDRootPath() {
        if(!checkSD()) {
            return null;
        } else {
            return Environment.getExternalStorageDirectory().getPath();
        }
    }

    /**
     * 创建目录
     * @param dirPath 目录路径（末尾不带“/”，如"/sdcard"）
     * @return true - 创建成功，false - 创建失败
     */
    public static boolean makeDir(String dirPath) {
        boolean isSuccessful = true;
        File dir = new File(dirPath);
        if(!dir.exists()) {
            isSuccessful = dir.mkdirs();
        }
        return isSuccessful;
    }

    /**
     * 以jpg格式保存Bitmap
     * @param filePath 完整的文件路径（含路径、扩展名）
     * @param bitmap
     * @return true - 保存成功， false - 保存失败
     */
    public static boolean saveBitmapInJPG(String filePath, Bitmap bitmap) {
        File f = new File(filePath);
        if(f.exists()) {
            return true;
        }
        try {
            f.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    static class CompratorByLastModified implements Comparator<File> {
//        public int compare(File f1, File f2) {
//            long diff = f1.lastModified() - f2.lastModified();
//            if (diff > 0) {
//                return -1;
//            } else if (diff == 0) {
//                return 0;
//            } else {
//                return 1;
//            }
//        }
//    }

    /**
     * 获取图片缩略图
     * @param path 图片的完整路径
     * @param width 缩略图宽，单位：像素
     * @param height 缩略图高，单位：像素
     * @return 图片缩略图的Bitmap，若出现异常，返回null
     */
    public static Bitmap getThumbnail(String path, int width, int height) {
        Bitmap bitmap = null;
        Bitmap thumbnail = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeFile(path, options);
            thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width, height);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return thumbnail;
    }



    /**
     * 删除指定路径下的所有文件
     * @param path 指定的路径
     */
    public static void delFiles(String path) {
        File file = new File(path);
        if(!file.exists()) { return; }
        File[] files = file.listFiles();
        for(int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    public static boolean isFileExist(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        if(file.exists()) {

            return true;
        }
        else{
            return false;
        }

    }

    /**
     * 获取文件大小（包括文件夹）
     * @return
     */
    public static String getAutoFilesSize(){
        String filepath=new Item().getSD()+"/"+ OneItem.getOneItem().getGather_path();
        File file=new File(filepath);
        long blockSize=0;
        try {
            if(file.isDirectory()){
                blockSize=getFile(file);
            }else {
                blockSize=getFileSize(file);
            }

        }catch (Exception e){

        }
        return FormetFileSize(blockSize);
    }
    /**
     * 获取本地单个文件的大小
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    /*
 转换文件大小
  */
    private static String FormetFileSize(long fileS){
        DecimalFormat df=new DecimalFormat("#.00");
        String fileSizeString="";
        String wrongSize="0GB";
        if(fileS==0){
            return wrongSize;
        }
        fileSizeString=df.format((double)fileS/1073741824)+"GB";
        return fileSizeString;
    }
    /*
   获取指定文件夹
    */
    private static long getFile(File f) throws Exception {
        long size=0;
        File filest[]=f.listFiles();
        for(int i=0;i<filest.length;i++){
            if(filest[i].isDirectory()){
                size=size+getFile(filest[i]);
            }else {
                size=size+getFileSize(filest[i]);
            }
        }
        return size;
    }
}
