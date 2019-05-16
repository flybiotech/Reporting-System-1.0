package com.shizhenbao.db;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import android.widget.Toast;

import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbin on 2017/11/13.
 */

public class Backup {
    List<String>list_Directory1=new ArrayList<>();//相应日期下子文件集合
    private Context context;
    private String localsn;
    public Backup(Context context){
        this.context=context;
    }
    public Backup(Context context,String localsn){
        this.context=context;
        this.localsn=localsn;
    }

    public int copy(String fromFile,String toFile){
        //要复制的文件目录
        File[]currentFiles;
        File root=new File(fromFile);
        //判断文件是否存在
        if(!root.exists()){
            return -1;
        }
        //如果存在则获取当前目录下的所有文件，填充数组
        currentFiles=root.listFiles();
        //目标目录
        File targetDir=new File(toFile);
        //创建目录
        if(!targetDir.exists()){
            targetDir.mkdirs();
        }
        //遍历要复制的全部文件
        for(int i=0;i<currentFiles.length;i++){
            if(currentFiles[i].isDirectory()){//如果当前项为子目录，进行递归
                copy(currentFiles[i].getPath()+"/",toFile+currentFiles[i].getName()+"/");
            }else {//如果当前项为文件则进行文件拷贝
                CopySdcardFile(currentFiles[i].getPath(),toFile+currentFiles[i].getName());
            }
        }
        return 0;
    }
    //文件拷贝
    public int CopySdcardFile(String fromFile,String toFile){
        try {
            InputStream fosfrom=new FileInputStream(fromFile);
            OutputStream fosto=new FileOutputStream(toFile);
            byte bt[]=new byte[1024];
            int d;
            while((d=fosfrom.read(bt))>0){
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
    public void getSD(){
        String[] strAllPath =getAllSdPaths(context);
//        String moblieStorgePath = strAllPath[0];// 手机
        if (strAllPath.length>1) {
            String sDStorgePath = strAllPath[1];// sd卡
            //1、判断sd卡是否可用
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                //sd卡可用
                File path=new File(sDStorgePath);//sd卡下面的a.txt文件  参数 前面 是目录 后面是文件
                copy(new Item().getSD()+"/"+ OneItem.getOneItem().getGather_path(),path.getAbsolutePath()+"/"+ OneItem.getOneItem().getGather_path()+"/");
            }
        }else {
            Toast.makeText(context, "没有SD卡", Toast.LENGTH_SHORT).show();
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
    /*
    搜索符合备份条件的文件
     */
    public void back_up(){
        list_Directory.clear();
        list_Directory1.clear();
        selectData(new Item().getSD()+"/"+ OneItem.getOneItem().getGather_path(),0);
        for(int i=0;i<list_Directory.size();i++){
            selectData(new Item().getSD()+"/"+ OneItem.getOneItem().getGather_path()+"/"+list_Directory.get(i),1);
//                            Toast.makeText(getContext(), "ll:"+list_Directory1.get(i)+",22:"+list_Directory1.size()+",33:"+list_Directory.size(), Toast.LENGTH_SHORT).show();

        }
        long time=0;
        for(int j=0;j<list_Directory1.size();j++){
            time= new UserManager().isDate2Bigger(list_Directory1.get(j));
        }
        Toast.makeText(context, "时间："+time, Toast.LENGTH_SHORT).show();
    }

    List<String> list_Directory=new ArrayList<>();//一级目录文件夹名称

    public void selectData(String path, final int temp) {

        if (new Item().getSD() != null) {
            File[] files = new File(path).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean a = false;
                    if(dir.isDirectory()){
                        if(temp==0){
                            list_Directory.add(name);
                        }else if(temp==1){
                            list_Directory1.add(name);
                        }
                        a=true;
                    }
                    return a;
                }
            });
        }
    }

    /*
   得到手机总内存大小
    */
    public String getSDMemory(){
        File file=Environment.getDataDirectory();
        StatFs statFs=new StatFs(file.getPath());
        long blockSize=statFs.getBlockSize();
        long totalBlocks=statFs.getBlockCount();
        return Formatter.formatFileSize(context,blockSize*totalBlocks);
    }
    /*
    得到手机可用内存
     */
    public String getSDMemoryky(){
        File file=Environment.getDataDirectory();
        StatFs statFs=new StatFs(file.getPath());
        long blockSize=statFs.getBlockSize();
        long avai=statFs.getAvailableBlocks();
        return Formatter.formatFileSize(context,blockSize*avai);
    }
    /*
    得到SD卡可用的内存
     */
    public String getSDMemorysd(){
//        File file=Environment.getExternalStorageDirectory();
        String[] strAllPath =new Backup(context).getAllSdPaths(context);

        if (strAllPath.length > 1) {
//            String moblieStorgePath = strAllPath[0];// 手机
            String sDStorgePath = strAllPath[1];// sd卡
            File file=new File(sDStorgePath);
            long blockSize=0;
            long avai=0;
            //1、判断sd卡是否可用
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                //sd卡可用
                StatFs statFs=new StatFs(file.getPath());
                blockSize=statFs.getBlockSize();
                avai=statFs.getAvailableBlocks();
            }else {
                Toast.makeText(context, "SD卡不存在", Toast.LENGTH_SHORT).show();
            }

            return Formatter.formatFileSize(context,blockSize*avai);
        }
        return null;

    }
    /*
    得到要备份的文件大小
     */
    public String getBackupFileSize(){
        String filepath=new Item().getSD()+"/"+ OneItem.getOneItem().getGather_path();
        File file=new File(filepath);
        StatFs statFs=new StatFs(file.getPath());
        long blockSize=statFs.getBlockSize();
        long totalBlocks=statFs.getBlockCount();
        return Formatter.formatFileSize(context,blockSize*totalBlocks);
    }
    public static String getAutoFilesSize(){
        String filepath=new Item().getSD()+"/"+ OneItem.getOneItem().getGather_path();
        File file=new File(filepath);
        long blockSize=0;
        try {
            if(file.isDirectory()){
                blockSize=getFileSizes(file);
            }else {
                blockSize=getFileSize(file);
            }

        }catch (Exception e){

        }
        return FormetFileSize(blockSize);
    }
    /*
    获取指定文件大小
     */
    private static long getFileSize(File file) throws Exception {
        long size=0;
        if(file.exists()){
            FileInputStream fis=null;
            fis=new FileInputStream(file);
            size=fis.available();
        }else {
            file.createNewFile();
        }
        return size;
    }

    /*
    获取指定文件夹
     */
    private static long getFileSizes(File f) throws Exception {
        long size=0;
        File filest[]=f.listFiles();
        for(int i=0;i<filest.length;i++){
            if(filest[i].isDirectory()){
                size=size+getFileSizes(filest[i]);
            }else {
                size=size+getFileSize(filest[i]);
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
//        if(fileS<1024){
//            fileSizeString=df.format((double)fileS)+"B";
//        }else
// if(fileS<1048576){
//            fileSizeString=df.format((double)fileS/1024)+"KB";
//        }else if(fileS<1073741824){
//            fileSizeString=df.format((double) fileS/1048576)+"MB";
//        }else {
        fileSizeString=df.format((double)fileS/1073741824)+"GB";
//        }
        return fileSizeString;
    }

    public void initBackDoctor(){
        List<Doctor> backupsList = DataSupport.findAll(Doctor.class);
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

            }
            array.put(object);
        }
        try {
            allDataDoctor.put("persondata", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + Const.sn+"doctor.txt");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            out.print(allDataDoctor.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void initBackups() {//将数据库数据通过JSON保存为txt文件
        List<User> backupsList = DataSupport.findAll(User.class);
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
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + Const.sn+"user.txt");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            out.print(allData.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
