package com.shizhenbao.connect;

import android.content.Context;
import android.os.Handler;

import com.shizhenbao.db.Backup;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.Path;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by dell on 2017/8/9.
 */

public class DevConnect {

    private Context mContext;
//    private GetThread searchThread;
    private boolean isSearching;// 判断是否正在搜索
    private String searchMsg;
    private boolean searchInBackground = false;

    private static String TAG = "myCarch_DevConnect";
    public LoadingDialog lod;

    private Handler handle;
    private boolean cancle = false;
    long msTime=-1;
    public DevConnect(Context context,Handler mHandle) {
        mContext = context;
//        lod = new LoadingDialog(mContext,false);
//        ipc = new IPCHandler(mContext);
        this.handle = mHandle;
    }

    public DevConnect(Context context) {
        mContext = context;

    }
    public DevConnect(){

    }



    public void getwifi(){
        lod = LoadingDialog.getInstance(mContext, cancle);
        lod.setMessage("正在尝试连接...");
        lod.dialogShow();
    }


    public void lostwifi(){
        if (lod != null) {
            lod.dismiss();
        }

    }


    //下面是删除本地数据的功能，这个是在删除了视珍宝里面的数据之后，再删除本地的数据

//    ProgressDialog xh_pDialog;//进度提示框

    public void deleteAllData(final Handler handle){
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteLocal();
                deleteLitePal();
//                ipc.deleteLocalDataInfo();
                handle.sendEmptyMessage(6);
            }
        }).start();

    }
    /**
     * 删除数据库数据
     */
    private void deleteLitePal(){
        String localsn=null;
        List<SystemSet> systemSet=DataSupport.findAll(SystemSet.class);
        if(systemSet.size()>0){
            for(int i=0;i<systemSet.size();i++){
                localsn=systemSet.get(0).getLocal_svn();
                Const.sn=localsn;
            }
        }

        DataSupport.deleteAll(Diacrisis.class);
        DataSupport.deleteAll(User.class);
        DataSupport.deleteAll(Doctor.class);
        List<SystemSet>systemSets=DataSupport.findAll(SystemSet.class);
        File file=new File(new Item().getSD()+"/"+systemSet.get(0).getBackUpPath());
        if(file.exists()){
            new Item().deleteFile(file);
        }
        systemSets.get(0).setHospital_name("");
        systemSets.get(0).setGather_path("SZB_save");
        systemSets.get(0).setBackUpPath("FALUYUAN");
        systemSets.get(0).setBackUpNetPath("/LUFAYUAN/");
        systemSets.get(0).save();
        DataSupport.deleteAll(Path.class);

    }
    /**
     * 删除本地数据
     */
    private void deleteLocal(){
        List<SystemSet>setList=DataSupport.findAll(SystemSet.class);
        new Item().deleteFile(new File(new Item().getSD()+"/"+setList.get(0).getGather_path()));
        List<Path>pathList=DataSupport.findAll(Path.class);
        for(int i=0;i<pathList.size();i++){
            new Item().deleteFile(new File(new Item().getSD()+"/"+pathList.get(i).getPicPath()));
        }

    }

    private void deleteData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User>userList=DataSupport.findAll(User.class);
                for(int i=0;i<userList.size();i++){
                    if(msTime-userList.get(i).getRegistDateint()>5184*1000000){
                        DataSupport.deleteAll(User.class,"pId=?",userList.get(i).getpId());//查询所有符合条件的数据
                        File file=new File(userList.get(i).getGatherPath());
                        delFolder(file.getParent());
                    }
                }
//                new FragSetting().initBackups();
                new Backup(mContext);
//                ipc.deleteLocalDataInfo();
                handle.sendEmptyMessage(6);
            }
        }).start();

    }
    public static void delFolder(String folderPath) {
    try {
        delAllFile(folderPath); // 删除完里面所有内容
        String filePath = folderPath;
        filePath = filePath.toString();
        java.io.File myFilePath = new java.io.File(filePath);
        myFilePath.delete(); // 删除空文件夹
         } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 删除指定文件夹下所有文件
// param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
         }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
        if (path.endsWith(File.separator)) {
            temp = new File(path + tempList[i]);
         } else {
                temp = new File(path + File.separator + tempList[i]);
         }
            if (temp.isFile()) {
            temp.delete();
         }
        if (temp.isDirectory()) {
        delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
        delFolder(path + "/" + tempList[i]);// 再删除空文件夹
        flag = true;
         }
        }
         return flag;
}
    public void initData(){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
        Date curDate=new Date(System.currentTimeMillis());
        String dtr=formatter.format(curDate);
        try {
            msTime=formatter.parse(dtr).getTime();
            deleteData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}