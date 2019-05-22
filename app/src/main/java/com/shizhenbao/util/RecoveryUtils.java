package com.shizhenbao.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.activity.GuanyuActivity;
import com.shizhenbao.activity.ZhenDuanXiugaiActivity;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.service.RecoverService;
import com.view.MyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 数据恢复，包括FTP和本地
 */
public class RecoveryUtils{
    private Context mContext;
    private NetworkUtils networkUtils;
    private MyProgressDialog myProgressDialog ;
    private EditText et_ftpid,et_ftpport,et_ftpname,et_ftppassword;
    private FTP ftp;
    private SDUtils sdUtils;
    private Thread thread;
    private CopyUtils copyUtils;
    String ImgfilePath= Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path();//需要压缩的图片文件的路径
    public RecoveryUtils(Context mContext){
        this.mContext = mContext;
        networkUtils = new NetworkUtils(mContext);
        myProgressDialog = new MyProgressDialog(mContext);
        ftp = new FTP();
        sdUtils = new SDUtils(mContext);
        copyUtils = new CopyUtils();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myProgressDialog.dialogCancel();
            switch (msg.what){
                case 1:
                    MyToast.showToast(mContext,mContext.getString(R.string.setting_recovery_success_message));
                    break;
                case -1:
                    MyToast.showToast(mContext,mContext.getString(R.string.setting_recovery_faild_message));
                    break;
            }
        }
    };
    /**
     *文件恢复，进行询问
     */
    public void inquiryRecoverDialog(){
//        final CharSequence[] items = {mContext.getString(R.string.setting_ftp_recovery), mContext.getString(R.string.setting_local_recovery)};//弹出框展示内容
        final CharSequence[] items = {mContext.getString(R.string.setting_local_recovery), ""};//弹出框展示内容
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);//声明弹出框
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
//                switch (item){
//                    case 0:
//                        boolean ab=networkUtils.isNetworkAvailable();//判断wifi是否可以连接
//                        if(ab){
//                            networkUtils.getUrl(0);
//                        }else {
//                            MyToast.showToast(mContext,mContext.getString(R.string.setting_network_Unavailability));
////                            Toast.makeText(mContext, R.string.setting_network_Unavailability, Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case 1:
                        //先判断本地是否有备份信息
                        File sdFile = sdUtils.getSDFile();
                        if(null == sdFile){
                            MyToast.showToast(mContext,mContext.getString(R.string.settint_no_sd));
                            return;
                        }
                        File backFile = new File(sdFile.getAbsolutePath()+"/Android/data/com.activity/"+Const.sn+ "/");
                        //文件不存在
                        if(!backFile.exists()){
                            MyToast.showToast(mContext,mContext.getString(R.string.setting_recovery_no_data));
//                            Toast.makeText(mContext, R.string.setting_recovery_no_data, Toast.LENGTH_SHORT).show();
                            return;
                        }else {//存在备份文件
                            myProgressDialog.dialogShow(mContext.getString(R.string.setting_recovery_loading));
                            thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int isText = -1;
                                    File []files = backFile.listFiles();
                                    if(files.length > 0){
                                        Message message = handler.obtainMessage();
                                        for(int i =0 ;i<files.length;i++){
                                            String fileName = files[i].getName();
                                            if(fileName.contains("user.txt")){
                                                recoverJson(files[i].getAbsolutePath(),0);
                                            }else if(fileName.contains("doctor.txt")){
                                                recoverJson(files[i].getAbsolutePath(),1);
                                            }else {
                                                copyUtils.copy(files[i].getAbsolutePath(),Environment.getExternalStorageDirectory()+"/SZB_save/");
                                            }
                                        }
                                        message.what = 1;
                                        handler.sendMessage(message);

                                    }
                                }
                            });
                            thread.start();
                        }
//                        break;
//                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }




////本地数据库数据恢复//////////////////////////////////
    /**
     * 恢复患者数据库数据,temp==0时代表患者，temp==1时代表医生
     */
    public boolean recoverJson(String path,int temp){
        boolean recoverLitepal = false;
        String Data = "";
        StringBuffer result=new StringBuffer();
        FileInputStream fis = null;
        BufferedReader br = null;
        try {
            fis=new FileInputStream(path);
            br=new BufferedReader(new InputStreamReader(fis));
            String line="";
            while ((line=br.readLine())!=null){
                result.append(line);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                return false;
            }
        }
        Data = result.toString();
        if(temp == 0){
            recoverLitepal = patientJson(Data);
        }else {
            recoverLitepal = doctorJson(Data);
        }
//        myProgressDialog.dialogCancel();
        return recoverLitepal;
    }

    /**
     * 解析患者json文件
     */
    private boolean patientJson(String userData){
        try {
            List<User> userList= LitePal.findAll(User.class);
            JSONObject jsonObject=new JSONObject(userData);
            JSONArray array=jsonObject.getJSONArray("persondata");
            if(userList.size()>0){
                LitePal.deleteAll(User.class);
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if(obj.getInt("is_diag")==2){
                    User user = new User();
                    user.setpId(obj.getString("pId"));
                    user.setpName(obj.getString("pName"));
                    user.setTel(obj.getString("tel"));
                    user.setAge(obj.getString("age"));
                    user.setRegistDateint(obj.getLong("registDateint"));
                    user.setIdNumber(obj.getString("idNumber"));
                    user.setsSNumber(obj.getString("sSNumber"));
                    user.setCaseNumbe(obj.getString("caseNumbe"));
                    user.sethCG(obj.getString("hCG"));
                    user.setWork(obj.getString("work"));
                    user.setPregnantCount(obj.getString("pregnantCount"));
                    user.setChildCount(obj.getString("childCount"));
                    user.setAbortionCount(obj.getString("abortionCount"));
                    user.setSexPartnerCount(obj.getString("sexPartnerCount"));
                    user.setSmokeTime(obj.getString("smokeTime"));
                    user.setCheckNotes(obj.getString("checkNotes"));
                    user.setBloodType(obj.getString("bloodType"));
                    user.setBirthControlMode(obj.getString("birthControlMode"));
                    user.setpSource(obj.getString("pSource"));
                    user.setMarry(obj.getString("marry"));
                    user.setAdvice(obj.getString("advice"));
                    user.setHpv_dna(obj.getString("Hpv_dna"));
                    user.setSymptom(obj.getString("symptom"));
                    user.setBingbian(obj.getString("bingbian"));
                    user.setYindaojin(obj.getString("yindaojin"));
                    user.setNizhen(obj.getString("nizhen"));
                    user.setZhuyishixiang(obj.getString("zhuyishixiang"));
                    user.setBianjie(obj.getString("bianjie"));
                    user.setHandle(obj.getString("handle"));
                    user.setColor(obj.getString("color"));
                    user.setXueguna(obj.getString("xueguna"));
                    user.setDianranse(obj.getString("dianranse"));
                    user.setCusuan(obj.getString("cusuan"));
                    user.setRegistDate(obj.getString("registDate"));
                    user.setCheckDate(obj.getString("checkDate"));
                    user.setOperId(obj.getInt("operId"));
//                          user.setImgPath(obj.get("imgPath"));
                    user.setImage(obj.getInt("image"));
                    user.setCutPath(obj.getString("cutPath"));
                    user.setRegistDateint(obj.getLong("registDateint"));
                    user.setbResult(obj.getBoolean("bResult"));
                    user.setIs_diag(obj.getInt("is_diag"));
                    user.setGatherPath(obj.getString("gatherPath"));
                    user.setPdfPath(obj.getString("pdfPath"));
                    user.setSurfacenum(obj.getInt("surfacenum"));
                    user.setColornnum(obj.getInt("colornnum"));
                    user.setVesselnum(obj.getInt("vesselnum"));
                    user.setStainnum(obj.getInt("stainnum"));
                    user.save();
                }else {
                    User user = new User();
                    user.setpId(obj.getString("pId"));
                    user.setpName(obj.getString("pName"));
                    user.setTel(obj.getString("tel"));
                    user.setAge(obj.getString("age"));
//                                        user.setCheckDate(obj.getString("checkDate"));
                    user.setRegistDateint(obj.getLong("registDateint"));
                    user.setIdNumber(obj.getString("idNumber"));
                    user.setsSNumber(obj.getString("sSNumber"));
                    user.setCaseNumbe(obj.getString("caseNumbe"));
                    user.sethCG(obj.getString("hCG"));
                    user.setWork(obj.getString("work"));
                    user.setImage(obj.getInt("image"));
                    user.setCutPath(obj.getString("cutPath"));
//                                        user.setRegistDateint(obj.getLong("registDateint"));
                    user.setRegistDate(obj.getString("registDate"));
                    user.setPregnantCount(obj.getString("pregnantCount"));
                    user.setChildCount(obj.getString("childCount"));
                    user.setAbortionCount(obj.getString("abortionCount"));
                    user.setSexPartnerCount(obj.getString("sexPartnerCount"));
                    user.setSmokeTime(obj.getString("smokeTime"));
                    user.setCheckNotes(obj.getString("checkNotes"));
                    user.setBloodType(obj.getString("bloodType"));
                    user.setBirthControlMode(obj.getString("birthControlMode"));
                    user.setpSource(obj.getString("pSource"));
                    user.setMarry(obj.getString("marry"));
                    user.setOperId(obj.getInt("operId"));
                    user.setGatherPath(obj.getString("gatherPath"));
                    user.setIs_diag(obj.getInt("is_diag"));
                    user.save();
                }
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
    /**
     * 解析医生数据json
     */
    private boolean doctorJson(String doctorData){
        try {
            //如果医生数据库有数据，则先删除原有数据
            List<Doctor>doctorlist=LitePal.findAll(Doctor.class);
            if(doctorlist.size()>0){
                LitePal.deleteAll(Doctor.class);
            }
            JSONObject jsonObject=new JSONObject(doctorData);
            JSONArray array=jsonObject.getJSONArray("persondata");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Doctor doctor=new Doctor();
                doctor.setdId(obj.getInt("dId"));
                doctor.setdName(obj.getString("dName"));
                doctor.setdPassword(obj.getString("dPassword"));
                doctor.setdEmail(obj.getString("dEmail"));
                doctor.setdAdmin(obj.getBoolean("dAdmin"));
                doctor.setLoginCount(obj.getInt("loginCount"));
                doctor.setdTemp(obj.getInt("dTemp"));
                doctor.setEdit_hos_name(obj.getString("edit_hos_name"));
                doctor.setEdit_hos_keshi(obj.getString("edit_hos_keshi"));
                doctor.save();
            }
        } catch (JSONException e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 如果ftp服务器为设置，则提示设置
     */
    public void FTPSetDialog(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.ftplink,null);
        et_ftpid = view.findViewById(R.id.et_ftpip);
        et_ftpport= (EditText) view.findViewById(R.id.et_ftpport);
        et_ftpname= (EditText) view.findViewById(R.id.et_ftpname);
        et_ftppassword= (EditText) view.findViewById(R.id.et_ftppassword);
        List<SystemSet>systemSets = LitePal.findAll(SystemSet.class);
        if(systemSets.size() > 0){
            et_ftpid.setText(systemSets.get(0).getHostName());
            et_ftpport.setText(systemSets.get(0).getServerPort()+"");
            et_ftpname.setText(systemSets.get(0).getUserName());
            et_ftppassword.setText(systemSets.get(0).getPassword());
        }
        LinearLayout linearLayoutMain = new LinearLayout(mContext);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
        linearLayoutMain.addView(view);
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.setting_ftp_server_title)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(mContext.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        return;
                    }
                }).setPositiveButton(mContext.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(et_ftpid.getText().toString().trim())//判断信息是否为空
                                &&!TextUtils.isEmpty(et_ftpname.getText().toString().trim())
                                &&!TextUtils.isEmpty(et_ftppassword.getText().toString().trim())
                                &&!TextUtils.isEmpty(et_ftpport.getText().toString().trim())
                        ){
                            List<SystemSet>setList=LitePal.findAll(SystemSet.class);
                            if(setList.size()==0){
                                SystemSet systemSet=new SystemSet();
                                systemSet.setHostName(et_ftpid.getText().toString().trim());
                                systemSet.setServerPort(Integer.parseInt(et_ftpport.getText().toString().trim()));
                                systemSet.setUserName(et_ftpname.getText().toString().trim());
                                systemSet.setPassword(et_ftppassword.getText().toString().trim());
                                systemSet.save();
                            }else {
                                for(int j=0;j<setList.size();j++){
                                    setList.get(0).setHostName(et_ftpid.getText().toString().trim());
                                    setList.get(0).setServerPort(Integer.parseInt(et_ftpport.getText().toString().trim()));
                                    setList.get(0).setUserName(et_ftpname.getText().toString().trim());
                                    setList.get(0).setPassword(et_ftppassword.getText().toString().trim());
                                }
                                setList.get(0).save();
                            }
                            dialogInterface.cancel();
                            MyToast.showToast(mContext,mContext.getString(R.string.setting_ftp_server_success));
//                            Toast.makeText(mContext,mContext.getString(R.string.setting_ftp_server_success) , Toast.LENGTH_SHORT).show();
                        }else{
                            MyToast.showToast(mContext,mContext.getString(R.string.setting_ftp_server_error));
//                            Toast.makeText(mContext,mContext.getString(R.string.setting_ftp_server_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();
        if(!dialog.isShowing()){
            dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
            dialog.show();
        }
    }
    /**
     * ftp数据恢复时，需要先删除本地原有的文件，防止数据错乱
     */
    private File zipFile,userFile,docFile;
    public void deleteOriginalFile(){
        File imageSave = new File(ImgfilePath);
        if(!imageSave.exists()){
            imageSave.mkdirs();
        }
        Intent intent = new Intent(mContext, RecoverService.class);
        mContext.startService(intent);
    }
    public boolean unZip(String path){
        File file = new File(path);
        if(!file.exists()){
            return false;
        }

        return ZipUtil.unZip(path,Const.backupPath + OneItem.getOneItem().getGather_path() + "/");
    }
    /**
     * 得到SD卡根目录
     *
     * @return
     */
    public File getSD() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();

        } else {
//            Toast.makeText(this, "SD卡不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}

