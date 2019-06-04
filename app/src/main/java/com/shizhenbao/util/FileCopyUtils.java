package com.shizhenbao.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopyUtils {

    private Context mContext;

    private long localFileSize = 0;

    private Thread thread;

    private String fromPath = "";

    private String toPath = "";

    private boolean isCopySuccess = false;
    public FileCopyUtils(Context context){
        this.mContext = context;
    }

    /**
     * 判断本地文件大小
     */
    public boolean getSize(File file){
        if(file.exists()){
            if(file.isDirectory()){
                File [] files = file.listFiles();
                for(int i = 0; i<files.length;i++){
                    if(files[i].isFile()){
                        localFileSize += files[i].length();
                    }else {
                        getSize(files[i]);
                    }
                }
            }
            if(localFileSize > 0){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }


    private boolean isExists = false;
    public void selectFile(){
        File file = new File(Const.originalPath);
        String patientPath = (String) SPUtils.get(mContext, Const.patientKey, "");
        Log.e("getImage1",patientPath);
        isExists = getSize(file);
        if(null != patientPath && !patientPath.equals("")){
            if(isExists){
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(getSize(new File(Const.originalPath + "/质控图/PHOTOS"))){
                            isCopySuccess = copyFile(patientPath + "/",1);
                        }
                        if (getSize(new File(Const.originalPath + "/醋酸白/PHOTOS"))) {
                            isCopySuccess = copyFile(patientPath+ "/",2);
                        }
                        if (getSize(new File(Const.originalPath + "/碘油/PHOTOS"))) {
                            isCopySuccess = copyFile(patientPath+ "/",3);
                        }
                        if(getSize(new File(Const.originalPath + "/质控图/VIDEOS"))){
                            isCopySuccess = copyFile(patientPath + "/",4);
                        }
                        if (getSize(new File(Const.originalPath + "/醋酸白/VIDEOS"))) {
                            isCopySuccess = copyFile(patientPath+ "/",5);
                        }
                        if (getSize(new File(Const.originalPath + "/碘油/VIDEOS"))) {
                            isCopySuccess = copyFile(patientPath+ "/",6);
                        }
                        //复制完成后，开始删除
                        if(Const.originalPath != null && isCopySuccess){
                            File orFile = new File(Const.originalPath);
                            deleteFile(orFile);
                        }
                        if(isCopySuccess){
                            if(fileCopyListener != null){
                                fileCopyListener.fileCopyResult(true);
                            }
                        }else {
                            if(fileCopyListener != null){
                                fileCopyListener.fileCopyResult(false);
                            }
                        }
                        thread.interrupt();
                    }
                });
                thread.start();
            }
        }
    }

    public boolean copyFile(String path,int temp){
        if(temp == 1){
            fromPath = Const.originalPath + "/质控图/PHOTOS/";
        }
        if(temp == 2){
            fromPath = Const.originalPath + "/醋酸白/PHOTOS/";
        }
        if(temp == 3){
            fromPath = Const.originalPath + "/碘油/PHOTOS/";
        }
        if(temp == 4){
            fromPath = Const.originalPath + "/质控图/VIDEOS/";
        }
        if(temp == 5){
            fromPath = Const.originalPath + "/醋酸白/VIDEOS/";
        }
        if(temp == 6){
            fromPath = Const.originalPath + "/碘油/VIDEOS/";
        }
        boolean isSuccess = cicleCoyeFile(fromPath,path);
        return isSuccess;
    }

    /**
     * 复制所有的数据，主要判断文件是否存在，循环便历初始文件夹
     */
    public boolean cicleCoyeFile(String fromFile,String toFile){
        //要复制的文件目录
        File[] fromList;
        File file = new File(fromFile);
        //判断文件是否存在
        if (!file.exists()) {
            return false;
        }
        //如果存在则获取当前目录下的所有文件，填充数组
        fromList = file.listFiles();
        //目标目录
        File toList = new File(toFile);
        //创建目录
        if (!toList.exists()) {
            toList.mkdirs();
        }
        //遍历要复制的全部文件
        for (int i = 0; i < fromList.length; i++) {
            if (fromList[i].isDirectory()) {//如果当前项为子目录，进行递归
                cicleCoyeFile(fromList[i].getPath() + "/", toFile + "/" + fromList[i].getName() + "/");
            } else {//如果当前项为文件则进行拷贝
                singleCopyFile(fromList[i].getPath(), toFile + fromList[i].getName());
            }
        }
        return true;
    }

    /**
     * 拷贝单个文件
     */
    private boolean singleCopyFile(String fromFile, String toFile){
        try {
            InputStream inputStream = new FileInputStream(fromFile);
            OutputStream outputStream = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int d;
            while ((d = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, d);
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除文件夹
     */
    private static boolean deleteFile(File file){
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
            return true;
        }
        return false;
    }

    public interface FileCopyListener{
        void fileCopyResult(boolean result);
        void fileStartCopy();
    }

    static FileCopyListener fileCopyListener;

    public static void setFileCopyListener(FileCopyListener fileCopy){
        fileCopyListener = fileCopy;
    }
}
