package com.shizhenbao.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.application.MyApplication;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.DeleteUtils;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.RecoveryUtils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.TimeZone;

public class RecoverService extends Service {

    private FTPClient ftpClient;//FTP连接

    private String strIp = "";//ip地址

    private int intPort = 0;//端口号

    private String user = "";//用户名

    private String password = "";//用户密码

    public String userFtpPath = Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path() + File.separator +Const.sn+"user.txt";
    public String doctorFtpPath = Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path() + File.separator + Const.sn+"doctor.txt";
    public String savePath = Const.backupPath + "SZB_save/";
    boolean recoverUser = false;
    boolean recoverDoctor = false;
    boolean unzip = false;
    /**
     * FTP当前目录.
     */
    private String currentPath = "";
    private RecoveryUtils recoveryUtils;
    public RecoverService(){
        ftpClient = new FTPClient();
        ftpClient.setDataTimeout(5000);
        ftpClient.setConnectTimeout(5000);
        recoveryUtils = new RecoveryUtils(MyApplication.getmContext());
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
        new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        return super.onStartCommand(intent, flags, startId);
    }

    private class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            boolean isSuccess = ftpLogin();

            if(isSuccess){
                try {

                    File savefile = new File(savePath);

                    if(savefile.exists()){
                        DeleteUtils.deleteLocal(savefile);
                    }

                    boolean downResult =  downloadSingleFile(OneItem.getOneItem().getBackUpNetPath()+Const.sn + "IMGZIP.zip",Const.backupPath + OneItem.getOneItem().getBackUpPath() + "/","SZB_save.zip");

                    if(downResult){
                        //解压压缩包
                        unzip = recoveryUtils.unZip(Const.backupPath + OneItem.getOneItem().getBackUpPath() + "/" + "SZB_save.zip");

                        if(unzip){

                            recoverUser = recoveryUtils.recoverJson(userFtpPath,0);

                            recoverDoctor = recoveryUtils.recoverJson(doctorFtpPath,1);

                            if(recoverDoctor && recoverUser){
                                Log.e("recover","44444444");
                                if(downloadResult != null){
                                    downloadResult.downloadResult(true);
                                }
                                File file = new File(Const.backupPath + OneItem.getOneItem().getBackUpPath() + "/" + "SZB_save.zip");
                                if(file.exists()){
                                    file.delete();
                                }
                            }else {
                                Log.e("recover","5555555");
                                if(downloadResult != null){
                                    downloadResult.downloadResult(false);
                                }
                            }

                        }else {
                            if(downloadResult != null){
                                Log.e("recover","6666666");
                                downloadResult.downloadResult(false);
                            }
                        }

                        Log.e(TAG_uploadservice+"result" ,"true");
                    }else {
                        if(downloadResult != null){
                            Log.e("recover","77777777777");
                            downloadResult.downloadResult(false);
                        }

                        Log.e(TAG_uploadservice+"result" ,"false");
                    }
                    ftpLogOut();
                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }




    /**
     * 下载.
     * @param remotePath FTP目录
     * @param fileName 文件名
     * @param localPath 本地目录
     * @return Result
     * @throws IOException
     */
    public boolean  indownload(String remotePath, String fileName, String localPath) {
        boolean flag = false;
        // 初始化FTP当前目录
        currentPath = remotePath;

        // 更改FTP目录
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remotePath);
            // 得到FTP当前目录下所有文件
            FTPFile[] ftpFiles = ftpClient.listFiles();
            // 循环遍历
            for (FTPFile ftpFile : ftpFiles) {
                // 找到需要下载的文件
                if (ftpFile.getName().equals(fileName)) {
                    // 创建本地目录
                    File file = new File(localPath);
                    if (ftpFile.isDirectory()) {
                        // 下载多个文件
                        flag = downloadMany(file);
                    } else {
                        // 下载当个文件
//                        flag = downloadSingle(file, ftpFile);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 下载单个文件，可实现断点下载.
     *
     * @param serverPath
     *            Ftp目录及文件路径
     * @param localPath
     *            本地目录
     * @param fileName
     *            下载之后的文件名称
     *            监听器
     * @throws IOException
     */


    public boolean downloadSingleFile(String serverPath, String localPath, String fileName) {
        // 打开FTP服务
        try {
            // 先判断服务器文件是否存在
            FTPFile[] files = ftpClient.listFiles(serverPath);
            if (files.length == 0) {
                Log.e("recover","00000000");
                if(downloadResult != null){
                    downloadResult.fileExistence(false);
                }
                return false;
            }else {
                Log.e("recover1","11111111");
                if(downloadResult != null){
                    downloadResult.fileExistence(true);
                }
            }
            //创建本地文件夹
            File mkFile = new File(localPath);
            if (!mkFile.exists()) {
                mkFile.mkdirs();
            }

            localPath = localPath + fileName;
            // 接着判断下载的文件是否能断点下载
            File localFile = new File(localPath);
            // 开始准备下载文件
            ftpClient.enterLocalPassiveMode();
            OutputStream out = new FileOutputStream(localFile, true);
            InputStream input = ftpClient.retrieveFileStream(serverPath);
            byte[] b = new byte[1024];
            int length = 0;
            while ((length = input.read(b)) != -1) {
                out.write(b, 0, length);
            }
            Log.e("recover","99999999");
            out.flush();
            out.close();
            input.close();
            Log.e("recover","888888888");
            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
//            if (ftpClient.completePendingCommand()) {
//                Log.e("recover","333333333333");
//
//            } else {
//                Log.e("recover","2222222222");
//                if(downloadResult != null){
//                    downloadResult.downloadResult(false);
//                }
//            }
        } catch (IOException e1) {
            Log.e("ceshi2",e1.getMessage().toString());
            return false;
        }
        return true;
    }



    /**
     * 下载单个文件.
     * @param localFile 本地目录
//     * @param ftpFile FTP目录
     * @return true下载成功, false下载失败
     * @throws IOException
     */
    private boolean downloadSingle(File localFile) {
        boolean flag = false;
            try {
                // 创建输出流
                OutputStream outputStream = new FileOutputStream(localFile);
                // 下载单个文件
                flag = ftpClient.retrieveFile(localFile.getName(), outputStream);
                // 关闭文件流
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                Log.e("reser",e.getMessage().toString());
                e.printStackTrace();
            }

        return flag;
    }
    /**
     * FTP根目录.
     */
    public static final String REMOTE_PATH = "\\";
    /**
     * 下载多个文件.
     * @param localFile 本地目录
     * @return true下载成功, false下载失败
     * @throws IOException
     */
    private boolean downloadMany(File localFile) throws IOException {
        boolean flag = true;
        // FTP当前目录
//        if (!currentPath.equals(REMOTE_PATH)) {
//            currentPath = currentPath + REMOTE_PATH + localFile.getName();
//        } else {
//            currentPath = currentPath + localFile.getName();
//        }
        currentPath = currentPath + "/" + localFile.getName() ;
        // 创建本地文件夹
        localFile.mkdir();
        // 更改FTP当前目录
        ftpClient.changeWorkingDirectory(currentPath);
        // 得到FTP当前目录下所有文件
        FTPFile[] ftpFiles = ftpClient.listFiles();
        // 循环遍历
        for (FTPFile ftpFile : ftpFiles) {
            if(!ftpFile.getName().equals(".") && !ftpFile.getName().equals("..")){
                // 创建文件
                File file = new File(localFile.getPath() + "/" + ftpFile.getName());

                if (ftpFile.isDirectory()) {
                    // 下载多个文件
                    flag = downloadMany(file);
                } else {
                    // 下载单个文件
//                    flag = downloadSingle(file, ftpFile);
                }
            }

        }
        return flag;
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
//                if (downloadResult != null) {
//                    downloadResult.ftplogin(true);
//                }
            } else {
//                if (downloadResult != null) {
//                    downloadResult.ftplogin(false);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("faild", this.user + "登录FTP服务失败！" + e.getMessage() + isLogin);
        }
        ftpClient.setBufferSize(1024 * 2);
        ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }

   public interface DownloadResult{
//        void ftplogin(boolean result);
//        void ftploginOut(boolean result);
        void downloadResult(boolean result);
        void fileExistence(boolean result);
   }

   static DownloadResult downloadResult;
    public static void setDownloadResultListener(DownloadResult downloadResultListener){
        downloadResult = downloadResultListener;
    }
}
