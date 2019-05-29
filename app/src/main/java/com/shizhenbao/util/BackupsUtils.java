package com.shizhenbao.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.activity.GuanyuActivity;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.entry.AESUtils3;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.User;
import com.shizhenbao.service.BackupService;
import com.util.FileUtil;
import com.util.SouthUtil;
import com.view.MyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 这是本地备份的工具类，包括数据库数据和本地文件
 */
public class BackupsUtils {
    private Context mContext;
    private NetworkUtils networkUtils;
    private MyProgressDialog myProgressDialog;
    private LinkedList linkedList;//需要上传的文件的集合
    private FTP ftp;
    String ImgfilePath= Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path();//需要压缩的图片文件的路径
    String zipFilePath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath()+"/"+Const.sn+"IMGZIP.zip";//压缩后图片文件保存的路径
    private FileUtil fileUtil;
    private DeleteDataUtils deleteDataUtils;
    public BackupsUtils (Context mContext){
        this.mContext = mContext;
        networkUtils = new NetworkUtils(mContext);
        myProgressDialog = new MyProgressDialog(mContext);
        ftp = new FTP();
        fileUtil = new FileUtil();
        deleteDataUtils = new DeleteDataUtils(mContext);
        sdUtils = new SDUtils(mContext);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myProgressDialog.dialogCancel();
            switch (msg.what){
                case 1:
                    MyToast.showToast(mContext,mContext.getString(R.string.setting_backups_success_message));
                    break;
                case -1:
                    MyToast.showToast(mContext,mContext.getString(R.string.setting_sd_noMemory));
                    break;
            }

        }
    };


    /**
     * 设置报告是否显示诊断结果
     */
    /**
     * 自定义弹出框布局
     */
    public void advancedDialog(List stringList) {
        LinearLayout linearLayoutMain = new LinearLayout(mContext);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(mContext);//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(arrayAdapter);
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.setting_please_change)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(mContext.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long l) {
                switch (arg2) {
                    case 0:
                        SPUtils.put(mContext, Const.display,1);//显示
                        break;
                    case 1:
                        SPUtils.put(mContext, Const.display,-1);//不显示
                        break;
                    default:
                        break;
                }
                MyToast.showToast(mContext,mContext.getString(R.string.image_setting_success));
//                SouthUtil.showToast(mContext,mContext.getString(R.string.image_setting_success));
                dialog.cancel();
            }
        });
    }

    /**
     * 询问备份方式
     */

    public void askBackupDialog(){
//        final CharSequence[] items = {mContext.getString(R.string.setting_ftp_backups), mContext.getString(R.string.setting_local_backups)};//弹出框展示内容
        final CharSequence[] items = { mContext.getString(R.string.setting_local_backups),""};//弹出框展示内容
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);//声明弹出框
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
//                switch (item) {
//                    case 0:
//                        boolean ab=networkUtils.isNetworkAvailable();//判断wifi是否可以连接
//                        if(ab){
//                            networkUtils.getUrl(1);
//                        }else {
//                            MyToast.showToast(mContext,mContext.getString(R.string.setting_network_Unavailability));
////                            Toast.makeText(mContext,mContext.getString(R.string.setting_network_Unavailability), Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case 1:
//                        localBackup();

                Intent intent = new Intent(mContext, BackupService.class);
                mContext.startService(intent);
//                        break;
//                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * 本地备份
     */

    public void localBackup(){

        if(null != Const.sn && !Const.sn.equals("")){
            String SDSize = initSDMemoryAvailable();
            if(SDSize == null){
//                MyToast.showToast(mContext,mContext.getString(R.string.setting_data_backupoast));
                if(backupResult != null){
                    backupResult.sdResult(0);
                }
                return;
            }
//            myProgressDialog.dialogShow(mContext.getString(R.string.setting_backups_loading));


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Message message = handler.obtainMessage();
                    String backupFileSize = fileUtil.getAutoFilesSize();
                    String BackupFileSize_new = "1";
                    String SDsize_new = "1";
                    if(backupFileSize.contains("吉")){
                        BackupFileSize_new=backupFileSize.substring(0,backupFileSize.indexOf("吉"));
                        SDsize_new=SDSize.substring(0,SDSize.indexOf("吉"));
                    }else {
                        BackupFileSize_new=backupFileSize.substring(0,backupFileSize.indexOf("G"));
                        SDsize_new=SDSize.substring(0,SDSize.indexOf("G"));
                    }
                    if(Double.parseDouble(SDsize_new) - Double.parseDouble(BackupFileSize_new) < 0.1){
//                        message.what = -1;
                        if(backupResult != null){
                            backupResult.sdResult(1);
                        }
                    }else {
                        int fileresilt = initCopyToSD();
                        boolean docResult = initBackUpDoctor(2);
                        boolean userResult = initBackUpUser(2);

                        Log.e("backupUtils",fileresilt + "" +  docResult + userResult + "aa");

                        if(fileresilt == 0 && docResult && userResult){
                            if(backupResult != null){
                                backupResult.backuoResult(true);
                            }
                        }else {
                            if(backupResult != null){
                                backupResult.backuoResult(false);
                            }
                        }
//                        message.what = 1;
                    }
//                    handler.sendMessage(message);
//                }
//            }).start();
        }else {

            if(backupResult != null){
                backupResult.snResult();
            }

        }
    }

    /**
     * 将图片保存文件夹压缩
     */
    public boolean zipImage(String path){
        try {
            zipFilePath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath()+File.separator+Const.sn+"IMGZIP.zip";
            File file = new File(zipFilePath);
            if(!file.exists()){
                file.getParentFile().mkdirs();
            }

            ZipUtil.zip(path,zipFilePath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     * 传入原始路径和目标路径，开始复制
     */
    public int  initCopyFile(String fromFile,String toFile){
        //要复制的文件目录
        File[]currentFiles;
        File root = new File(fromFile);
        //判断文件是否存在
        if(!root.exists()){
            return -1;
        }
        //如果存在则获取当前目录下的所有文件，填充数组
        currentFiles = root.listFiles();
        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if(!targetDir.exists()){
            targetDir.mkdirs();
        }
        //遍历要复制的全部文件
        for(int i=0;i<currentFiles.length;i++){
            if(currentFiles[i].isDirectory()){//如果当前项为子目录，进行递归
                initCopyFile(currentFiles[i].getPath()+"/",toFile+currentFiles[i].getName()+"/");
            }else {//如果当前项为文件则进行文件拷贝
                initCopySingleFile(currentFiles[i].getPath(),toFile+currentFiles[i].getName());
            }
        }
        return 0;
    }
    /**
     * 拷贝单个文件
     */
    public int initCopySingleFile(String fromFile,String toFile){
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int d;
            while((d = fosfrom.read(bt))>0){
                fosto.write(bt,0,d);
            }
            fosfrom.close();
            fosto.close();
            return 0;
        } catch (Exception e) {
//            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 得到所有的存储路径（内部存储+外部存储）
     *
     * @param context
     * @return
     */
    public static String[] getAllSdPaths(Context context) {
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
    /**
     * 得到SD的可用内存
     */
    public String initSDMemoryAvailable() {
        String[] AllRoutePath = getAllSdPaths(mContext);
        if (AllRoutePath.length > 1) {
            String SDPath = AllRoutePath[1];
            File file = new File(SDPath);
            long blockSize = 0;
            long avaiSize = 0;
            //1、判断sd卡是否可用
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //sd卡可用
                StatFs statFs = new StatFs(file.getPath());
                blockSize = statFs.getBlockSize();
                avaiSize = statFs.getAvailableBlocks();
            } else {
                MyToast.showToast(mContext,"SD卡不存在");
//                Toast.makeText(mContext, "SD卡不存在", Toast.LENGTH_SHORT).show();
            }
            return Formatter.formatFileSize(mContext,blockSize*avaiSize);
        }
        return null;
    }

    /**
     * 备份医生数据库
     */
    public boolean  initBackUpDoctor(int type){
        List<Doctor> backupsList = LitePal.findAll(Doctor.class);
        JSONObject allDataDoctor = new JSONObject();
        JSONArray array=new JSONArray();
        for (int i = 0; i < backupsList.size(); i++) {//患者数据库
            JSONObject object = new JSONObject();
            int dId=backupsList.get(i).getdId();
            String dName=backupsList.get(i).getdName();
            String dPassword=backupsList.get(i).getdPassword();
            String dEmail=backupsList.get(i).getdEmail();
            boolean dAdmin=backupsList.get(i).isdAdmin();
            int loginCount=backupsList.get(i).getLoginCount();
            int dTemp=backupsList.get(i).getdTemp();
            String edit_hos_name=backupsList.get(i).getEdit_hos_name();
            String edit_hos_keshi=backupsList.get(i).getEdit_hos_keshi();
            try {
                object.put("dId", dId);
                object.put("dName", dName);
                object.put("dPassword", dPassword);
                object.put("dEmail", dEmail);
                object.put("dAdmin", dAdmin);
                object.put("loginCount", loginCount);
                object.put("dTemp", dTemp);
                object.put("edit_hos_name",edit_hos_name);
                object.put("edit_hos_keshi",edit_hos_keshi);
            }catch (Exception e){

                e.printStackTrace();
                return false;
            }
            array.put(object);
        }
        try {
            allDataDoctor.put("persondata", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File file = null;
        if(type == 1){
            file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path() + File.separator + Const.sn+"doctor.txt");
        }else if(type ==2){
            file = new File(sdFile.getAbsolutePath()+"/Android/data/com.flybiotech.reportsystem1/"+Const.sn+ "/" +"doctor.txt");
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            String a= AESUtils3.encrypt(allDataDoctor.toString(),"fly123456");
            out.print(a);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return true;
    }

    /**
     * 备份患者数据库,1代表路径为本地，2代表路径为SD
     */
    public boolean initBackUpUser(int type){
        List<User> backupsList = LitePal.findAll(User.class);
        JSONObject allData = new JSONObject();
        JSONArray array=new JSONArray();
        for (int i = 0; i < backupsList.size(); i++) {
            JSONObject object = new JSONObject();
            String pId = backupsList.get(i).getpId();//患者id
            String pName = backupsList.get(i).getpName();//患者姓名
            String tel = backupsList.get(i).getTel();//患者电话
            String age = backupsList.get(i).getAge();//患者年龄
            String idNumber = backupsList.get(i).getIdNumber();//患者身份证
            String sSNumber = backupsList.get(i).getsSNumber();//社保号码
            String caseNumbe = backupsList.get(i).getCaseNumbe();//病历号
            String gatherPath=backupsList.get(i).getGatherPath();//采集图片路径
            String pdfPath=backupsList.get(i).getPdfPath();//pdf保存路径
            String hCG = backupsList.get(i).gethCG();//
            String work = backupsList.get(i).getWork();
            String pregnantCount = backupsList.get(i).getPregnantCount();
            String childCount = backupsList.get(i).getChildCount();
            String abortionCount = backupsList.get(i).getAbortionCount();
            String sexPartnerCount = backupsList.get(i).getSexPartnerCount();
            String smokeTime = backupsList.get(i).getSmokeTime();
            String checkNotes = backupsList.get(i).getCheckNotes();
            String bloodType = backupsList.get(i).getBloodType();
            String birthControlMode = backupsList.get(i).getBirthControlMode();
            String pSource = backupsList.get(i).getpSource();
            String marry = backupsList.get(i).getMarry();
            String advice = backupsList.get(i).getAdvice();
            String Hpv_dna = backupsList.get(i).getHpv_dna();
            String symptom = backupsList.get(i).getSymptom();
            String bingbian = backupsList.get(i).getBingbian();
            String yindaojin = backupsList.get(i).getYindaojin();
            String nizhen = backupsList.get(i).getNizhen();
            String zhuyishixiang = backupsList.get(i).getZhuyishixiang();
            String bianjie = backupsList.get(i).getBianjie();
            String handle=backupsList.get(i).getHandle();
            String color = backupsList.get(i).getColor();
            String xueguna = backupsList.get(i).getXueguna();
            String dianranse = backupsList.get(i).getDianranse();
            String cusuan = backupsList.get(i).getCusuan();
            String registDate=backupsList.get(i).getRegistDate();
            String checkDate=backupsList.get(i).getCheckDate();
            int operId = backupsList.get(i).getOperId();
            List<String> imgPath = backupsList.get(i).getImgPath();
            boolean bResult = backupsList.get(i).isbResult();
            int is_diag = backupsList.get(i).getIs_diag();
            int image=backupsList.get(i).getImage();
            String cutPath=backupsList.get(i).getCutPath();
            long registDateint=backupsList.get(i).getRegistDateint();
            int surfacenum=backupsList.get(i).getSurfacenum();
            int colornnum=backupsList.get(i).getColornnum();
            int vesselnum=backupsList.get(i).getVesselnum();
            int stainnum=backupsList.get(i).getStainnum();
            try {
                object.put("pId", pId);
                object.put("pName", pName);
                object.put("tel", tel);
                object.put("age", age);
                object.put("idNumber", idNumber);
                object.put("sSNumber", sSNumber);
                object.put("caseNumbe", caseNumbe);
                object.put("hCG", hCG);
                object.put("work", work);
                object.put("pregnantCount", pregnantCount);
                object.put("childCount", childCount);
                object.put("abortionCount", abortionCount);
                object.put("sexPartnerCount", sexPartnerCount);
                object.put("smokeTime", smokeTime);
                object.put("checkNotes", checkNotes);
                object.put("bloodType", bloodType);
                object.put("birthControlMode", birthControlMode);
                object.put("pSource", pSource);
                object.put("marry", marry);
                object.put("advice", advice);
                object.put("Hpv_dna", Hpv_dna);
                object.put("symptom", symptom);
                object.put("bingbian", bingbian);
                object.put("yindaojin", yindaojin);
                object.put("nizhen", nizhen);
                object.put("zhuyishixiang", zhuyishixiang);
                object.put("bianjie", bianjie);
                object.put("handle",handle);
                object.put("color", color);
                object.put("xueguna", xueguna);
                object.put("dianranse", dianranse);
                object.put("cusuan", cusuan);
                object.put("registDate",registDate);
                object.put("checkDate",checkDate);
                object.put("operId", operId);
                object.put("imgPath", imgPath);
                object.put("bResult", bResult);
                object.put("is_diag", is_diag);
                object.put("gatherPath",gatherPath);
                object.put("pdfPath",pdfPath);
                object.put("image",image);
                object.put("cutPath",cutPath);
                object.put("registDateint",registDateint);
                object.put("surfacenum",surfacenum);
                object.put("colornnum",colornnum);
                object.put("vesselnum",vesselnum);
                object.put("stainnum",stainnum);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(object);
        }
        try {
            allData.put("persondata", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File file = null;
        if(type ==2){
            file = new File(sdFile.getAbsolutePath()+"/Android/data/com.flybiotech.reportsystem1/"+Const.sn + "/"+"user.txt");
        }else {
            file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path() + File.separator + Const.sn+"user.txt");
        }

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            String a= AESUtils3.encrypt(allData.toString(),"fly123456");
            out.print(a);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return true;
    }

    private File sdFile = null;
    private SDUtils sdUtils;
    public int initCopyToSD(){
        sdFile = sdUtils.getSDFile();
        int result = initCopyFile(new Item().getSD()+"/"+ OneItem.getOneItem().getGather_path(),sdFile.getAbsolutePath()+"/Android/data/com.flybiotech.reportsystem1/"+Const.sn+ "/" + OneItem.getOneItem().getGather_path()+"/");
        return result;
    }


    public interface BackupResult{
        void backuoResult(boolean result);
        void sdResult(int temp);//0代表无SD，1代表SD卡内存不足
        void snResult();

    }
    static BackupResult backupResult;

    public static void setBackupResult(BackupResult backupResultListener){
        backupResult = backupResultListener;
    }
}
