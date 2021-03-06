package com.shizhenbao.connect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.Path;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.sharePreferencess.SharedPreferenceText;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.SPUtils;
import com.view.LoadingDialog;

import org.litepal.LitePal;

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

    public LoadingDialog lod;
    private Handler handle;
    private boolean cancle = false;
    long msTime=-1;
    private SharedPreferenceText sharedPreferenceText;
    public DevConnect(Context context,Handler mHandle) {
        mContext = context;
        this.handle = mHandle;
        sharedPreferenceText = new SharedPreferenceText(mContext);
    }

    public void lostwifi(){
        if (lod != null) {
            lod.dismiss();
        }

    }

    //下面是删除本地数据的功能，这个是在删除了视珍宝里面的数据之后，再删除本地的数据
    public void deleteAllData(final Handler handle){
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteLocal();
                deleteLitePal();
//                ipcHandler.deleteLocalDataInfo();
                handle.sendEmptyMessage(6);
            }
        }).start();

    }
    /**
     * 删除数据库数据
     */
    private void deleteLitePal(){
        String localsn=null;
        List<SystemSet> systemSet=LitePal.findAll(SystemSet.class);
        if(systemSet.size()>0){
            for(int i=0;i<systemSet.size();i++){
                localsn=systemSet.get(0).getLocal_svn();
                Const.sn=localsn;
            }
        }

        LitePal.deleteAll(Diacrisis.class);
        LitePal.deleteAll(User.class);
        LitePal.deleteAll(Doctor.class);
        List<SystemSet>systemSets=LitePal.findAll(SystemSet.class);
        File file=new File(new Item().getSD()+"/"+systemSet.get(0).getBackUpPath());
        if(file.exists()){
            new Item().deleteFile(file);
        }
        systemSets.get(0).setHospital_name("");
        systemSets.get(0).setGather_path("SZB_save_1");
        systemSets.get(0).setBackUpPath("FALUYUAN");
        systemSets.get(0).setBackUpNetPath("/LUFAYUAN/");
        systemSets.get(0).save();
        LitePal.deleteAll(Path.class);



    }
    /**
     * 删除本地数据
     */
    private void deleteLocal(){
        try {
            SPUtils.put(mContext,"loginName", "");
            SPUtils.put(mContext,"loginPass", "");
            List<SystemSet>setList=LitePal.findAll(SystemSet.class);
            new Item().deleteFile(new File(new Item().getSD()+"/"+setList.get(0).getGather_path()));
            List<Path>pathList=LitePal.findAll(Path.class);
            for(int i=0;i<pathList.size();i++){
                new Item().deleteFile(new File(new Item().getSD()+"/"+pathList.get(i).getPicPath()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User>userList=LitePal.findAll(User.class);
                for(int i=0;i<userList.size();i++){
                    if(msTime-userList.get(i).getRegistDateint()>5184*1000000){
                        LitePal.deleteAll(User.class,"pId=?",userList.get(i).getpId());//查询所有符合条件的数据
                        File file=new File(userList.get(i).getGatherPath());
                        delFolder(file.getParent());
                    }
                }
                handle.sendEmptyMessage(6);
            }
        }).start();

    }
    public static void delFolder(String folderPath) {
    try {
        delAllFile(folderPath); // 删除完里面所有内容
        String filePath = folderPath;
        filePath = filePath.toString();
        File myFilePath = new File(filePath);
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