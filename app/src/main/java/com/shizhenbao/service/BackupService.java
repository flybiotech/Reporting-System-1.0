package com.shizhenbao.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.litepal.LitePal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.TimeZone;

public class BackupService  extends Service {

    private  FTPClient ftpClient;//FTP连接

    private String strIp = "";//ip地址

    private int intPort = 0;//端口号

    private String user = "";//用户名

    private String password = "";//用户密码

    private long localAllSize, ftpAllSize;

    String backpath = Const.backupPath + OneItem.getOneItem().getBackUpPath() + "/" + Const.sn + "IMGZIP.zip" ;

    private BackupsUtils backupsUtils;
    public BackupService(){
        ftpClient = new FTPClient();
        ftpClient.setDataTimeout(5000);
        ftpClient.setConnectTimeout(5000);
        backupsUtils = new BackupsUtils(Const.context);
        List<SystemSet> systemSets= LitePal.findAll(SystemSet.class);
        if(systemSets.size()>0){
            strIp=systemSets.get(0).getHostName();
            intPort=systemSets.get(0).getServerPort();
            user=systemSets.get(0).getUserName();
            password=systemSets.get(0).getPassword();
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new UploadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        return super.onStartCommand(intent, flags, startId);
    }


    private class UploadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            backupsUtils.initBackUpDoctor(1);
            backupsUtils.initBackUpUser(1);

            String path = Const.backupPath + OneItem.getOneItem().getGather_path() + "/";

            boolean zip =backupsUtils.zipImage(path);

            if(!zip){
                Log.e("aaaa2","22222");
                return null;
            }

            File file = new File(backpath);

            if(!file.exists()){
                Log.e("back","不存在");
                return null;
            }

            boolean isSuccess = ftpLogin();

            if(isSuccess){

                boolean result = uploadFile(file,OneItem.getOneItem().getBackUpNetPath());
                if(result){
                    ftpLogOut();
                    stopSelf();
                }else {
                    ftpLogOut();
                    stopSelf();
                }
            }
            return null;
        }
    }
    /***
     * @上传文件夹
     * @param localDirectory
     *   当地文件夹
     * @param remoteDirectoryPath
     *   Ftp 服务器路径 以目录"/"结束
     * */
    boolean makeDirFlag = false;

    public boolean uploadDirectory(String localDirectory,
                                   String remoteDirectoryPath) {
        File src = new File(localDirectory);

        try {
            initCreateFtpFile(OneItem.getOneItem().getBackUpNetPath());

            initCreateFtpFile(remoteDirectoryPath);

            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            Log.i("TAG_1_ftp", "uploadDirectory: src.getName() = " + src.getName() + ",localDirectory = " + localDirectory);
            //创建服务器文件
            initCreateFtpFile(remoteDirectoryPath);


            //创建复制的目标目录
//            initCreate(localDirectory);
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("is", e.getMessage().toString());
        }
        if (makeDirFlag) {
            File[] allFile = src.listFiles();
            if (allFile == null) {
                return makeDirFlag;
            }
            if (allFile.length > 0)
                for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                    if (!allFile[currentFile].isDirectory()) {
                        String srcName = allFile[currentFile].getPath().toString();
                        boolean upload = uploadFile(new File(srcName), remoteDirectoryPath);
                        if(!upload){
                            return false;
                        }
                    }
                }
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (allFile[currentFile].isDirectory()) {
                    // 递归
                    uploadDirectory(allFile[currentFile].getPath().toString(),
                            remoteDirectoryPath);
//                    initCopy(allFile[currentFile].getPath().toString());
                }
            }
            return makeDirFlag;
        } else {
            ftpLogOut();
            return makeDirFlag;
        }
    }

    /***
     * 上传Ftp文件
     * @param localFile 当地文件
     * @param
     * */
    int num = 0;

    public boolean uploadFile(File localFile, String romotUpLoadePath) {

        Log.i("TAG_1_FTP", "uploadFile: localFile = " + localFile +" , localFile.getName()= "+localFile.getName()+ " , romotUpLoadePath =" + romotUpLoadePath);
        initCreateFtpFile(romotUpLoadePath);
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            Log.e("logging", localFile.getName() + "开始上传....."+inStream+"......"+this.ftpClient);

            ftpClient.enterLocalPassiveMode();

            ftpClient.setSoTimeout(5000);
            success = ftpClient.storeFile(localFile.getName(), inStream);

            //文件上传成功
            if (success == true) {
                ftpClient.sendCommand("pwd");
                num = 0;

                //判断是否上传完整，有时断网后ftpSuccess也为true
                boolean isComplete= initSize(romotUpLoadePath, localFile);
                //如果本地上传的图片和服务器上已上传的文件的大小一致，则说明该文件已完整上传至ftp
                if(isComplete){
                    double percent = ((double) ftpAllSize / localAllSize) * 100;
                    if (upLoadFileProcess != null) {
                        upLoadFileProcess.getUpLoadFileProcessPrecent(percent);
                    }

                    File file = new File(backpath);

                    if(file.exists()){
                        file.delete();
                    }

                    return success;
                }
                return success;
            } else {
                //文件上传失败后，再次进行上传，最多上传4次
                num++;
                if (num <= 3) {
                    uploadFile(localFile, romotUpLoadePath);
                } else {
//                    LogWriteUtils.e("上传失败图片", localFile.getAbsolutePath());
                }

            }
        } catch (FileNotFoundException e) {
            Log.e("logging3",e.getMessage().toString());
            e.printStackTrace();
        } catch (IOException e) {
            num++;
//                LogWriteUtils.e("上传失败图片", localFile.getAbsolutePath());
            //先断开连接
            if(num <= 3){
                ftpLogOut();
                ftpLogin();
                uploadFile(localFile, romotUpLoadePath);
            }else {
                ftpLogOut();
                stopSelf();
                if(upLoadFileProcess != null){
                    upLoadFileProcess.loginOut(false);
                }
            }

            Log.e("logging44","失败"+localFile+num);
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    Log.e("logging4",e.getMessage().toString());
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    //创建服务器文件
    private void initCreateFtpFile(String remoteDirectoryPath) {
        //遍历该服务器某级目录下所有文件的集合
        FTPFile[] ftpFiles = new FTPFile[0];
        try {
            ftpClient.enterLocalPassiveMode();
            ftpFiles = ftpClient.listFiles(remoteDirectoryPath);
            if (ftpFiles.length == 0) {
                makeDirFlag = this.ftpClient.makeDirectory(remoteDirectoryPath);//在ftp服务器上创建文件
            } else {
                makeDirFlag = true;
            }
            if (!makeDirFlag) {
                if (num <= 3) {
                    initCreateFtpFile(remoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //得到服务器文件大小，刚刚上传成功的,如果上传成功，但是该文件的大小没有计入总大小，则重复进行计算
    private boolean initSize(String path, File localFile) {
        FTPFile[] ftpFiles = new FTPFile[0];
        try {
            ftpClient.enterLocalPassiveMode();
            ftpFiles = ftpClient.listFiles(path + localFile.getName());
            if (ftpFiles.length > 0) {

                if(ftpFiles[0].getSize() == localFile.length()){
                    ftpAllSize += ftpFiles[0].getSize();
                    Log.e("ftpsize", localFile.getName() + "上传成功" + ftpAllSize+"|||"+ftpFiles[0].getSize()+"|||"+localFile.length());
                    return true;
                }
//                Log.e("loadSuccess", localFile.getName() + "上传成功" + FTPAllSize);
            } else {
                if (num <= 3) {
                    initSize(path, localFile);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String TAG_uploadservice = "UpLoadService";
    /**
     * @退出关闭服务器链接
     */
    public void ftpLogOut() {
        if(null != this.ftpClient && this.ftpClient.isConnected()){
            try {
                //退出ftp服务器
                boolean outResult = this.ftpClient.logout();
                if(outResult){
                    Log.e(TAG_uploadservice+"success", "成功退出服务器");
                }
            } catch (IOException e) {
                Log.e(TAG_uploadservice+"faild", "退出FTP服务器异常！" + e.getMessage());
                e.printStackTrace();
            }finally {
                try {
                    this.ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return 判断是否登入成功
     */
    public boolean ftpLogin() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        ftpClient.setControlEncoding("utf-8");
        ftpClient.configure(ftpClientConfig);
        try {
            Log.e("faildprort", intPort+"");
            if (this.intPort > 0) {
                ftpClient.connect(this.strIp, this.intPort);
            } else {
                ftpClient.connect(this.strIp);
            }
            // FTP服务器连接回答
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                Log.e("faild", "登录FTP服务失败！");
                return isLogin;
            }
            isLogin = ftpClient.login(this.user, this.password);
            // 设置传输协议
//            this.ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            Log.e("faild", "恭喜" + this.user + "成功登陆FTP服务器" + isLogin);
            if (isLogin) {
                if (ftpLogin != null) {
                    ftpLogin.getFTPLoginResult(true);
                }
            } else {
                if (ftpLogin != null) {
                    ftpLogin.getFTPLoginResult(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("faild", this.user + "登录FTP服务失败！" + e.getMessage() + isLogin);
        }
        ftpClient.setBufferSize(1024 * 2);
        ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }

    /**
     * 下载单个文件，可实现断点下载.
     *
     * @param serverPath
     *            Ftp目录及文件路径
     * @param localPath
     *            本地目录
     *            下载之后的文件名称
     *            监听器
     * @throws IOException
     */

    public void initDown(String serverPath, String localPath){
        File serverFile = new File(serverPath);
        if(serverFile.isDirectory()){
            File [] files = serverFile.listFiles();
            for(int i = 0;i<files.length;i++){
                initDown(serverPath+files[i].getName(),localPath);
            }
        }else {
            try {
                downloadSingleFile(serverPath,localPath,new File(serverPath).getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 下载完成之后关闭连接
        try {
            this.ftpLogOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(upLoadFileProcess != null){
            upLoadFileProcess.getDisConnectResult(true);
        }
    }
    public void downloadSingleFile(String serverPath, String localPath, String fileName) {

        // 打开FTP服务
        try {
            this.ftpLogin();
            if(upLoadFileProcess != null){
                upLoadFileProcess.loginIn(true);
            }
            // 先判断服务器文件是否存在
            FTPFile[] files = ftpClient.listFiles(serverPath);
            if (files.length == 0) {
//                if(upLoadFileProcess != null){
//                    upLoadFileProcess.getFileExist(false);
//                }
                return;
            }else {
//                if(upLoadFileProcess != null){
//                    upLoadFileProcess.getFileExist(true);
//                }
            }
            //创建本地文件夹
            File mkFile = new File(localPath);
            if (!mkFile.exists()) {
                mkFile.mkdirs();
            }

            localPath = localPath + fileName;
            // 接着判断下载的文件是否能断点下载
            long serverSize = files[0].getSize(); // 获取远程文件的长度
            File localFile = new File(localPath);
            long localSize = 0;
            if (localFile.exists()) {
                localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
                if (localSize >= serverSize) {
                    File file = new File(localPath);
                    file.delete();
                }
            }

            // 开始准备下载文件
            OutputStream out = new FileOutputStream(localFile, true);
            ftpClient.setRestartOffset(localSize);
            InputStream input = ftpClient.retrieveFileStream(serverPath);
            byte[] b = new byte[1024];
            int length = 0;
            while ((length = input.read(b)) != -1) {
                out.write(b, 0, length);
            }
            out.flush();
            out.close();
            input.close();

            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
            if (ftpClient.completePendingCommand()) {
//                if(upLoadFileProcess != null){
//                    upLoadFileProcess.getDownloadResult(true);
//                }
            } else {
//                if(upLoadFileProcess != null){
//                    upLoadFileProcess.getDownloadResult(false);
//                }
            }
        } catch (IOException e1) {
            Log.e("ceshi2",e1.getMessage().toString());
            if(upLoadFileProcess !=null){
                upLoadFileProcess.loginIn(false);
            }
            return;
        }
    }

    //得到本地文件的大小
    public void upLoadDir(String localDir, String remoteDir) {
        File file = new File(localDir);
        if (file.exists()) {
            localAllSize = getSize(file.listFiles());
        }
    }

    /**
     * 本地文件大小
     */
    public long getSize(File[] localFile) {

        for (int i = 0; i < localFile.length; i++) {
            if (localFile[i].isFile()) {
                localAllSize += localFile[i].length();
            } else {
                getSize(localFile[i].listFiles());
            }
        }
        return localAllSize;
    }

    public interface FTPLogin {
        void getFTPLoginResult(boolean isLogin);
    }

    static FTPLogin ftpLogin;

    public static void setFtpLoginListenner(FTPLogin ftpLoginListenner) {
        ftpLogin = ftpLoginListenner;
    }

    public interface UpLoadFileProcess {

        void getUpLoadFileProcessPrecent(double percent);
        //        void setRestartService();
        void loginOut(boolean outResult);
        void loginIn(boolean inResult);
//        void getFileExist(boolean isFileExist);//文件是否存在
//        void getDownloadResult(boolean isDownSuccess);//是否下载成功
        void getDisConnectResult(boolean isDisConnect);//是否成功断开连接
//        void getUploadResult(boolean isUpload);//是否上传成功
    }

    static UpLoadFileProcess upLoadFileProcess;

    public static void setUpLoadFileProcessListener(UpLoadFileProcess upLoadFileProcessListener) {
        upLoadFileProcess = upLoadFileProcessListener;
    }

    public static void setUpLoadFileProcessNull() {
        upLoadFileProcess = null;
    }
}
