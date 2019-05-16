package com.shizhenbao.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.activity.AdminManager;
import com.shizhenbao.activity.MainActivity;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.Admin;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.ExceptionManager;
import com.shizhenbao.pop.Path;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.sharePreferencess.SharedPreferenceText;
import com.view.LoadingDialog;
import com.view.MyToast;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 1、这是账户管理工具类，包括查询，保存,超级用户等
 * 2、初始化一些系统属性，包括图片保存初始路径，本地备份路径，服务器备份路径
 */
public class LoginAccountUtils {
    private Context mContext;
    private List<Doctor> doctorList;//登陆过的账户，包括所有信息
    private List <String> accountName;//登陆过的账户名称集合
    private EditText et_account,et_password;
    private CheckBox checkBox;
    private LoginRegister lr;//信息查询类
    private String managerAccount = "shanghaifaluyuan";//管理员账户
    private List<Admin>adminList;//管理员账户集合，最多包含一条信息
    private List<ExceptionManager> exceptionManagerList;//异常处理账户集合，最多包含一条信息
    private List<SystemSet> systemSetList;//系统设置属性集合，最多只有一条信息
    boolean f=false;//判断账户密码是否正确
    private LoadingDialog log; //登陆时的dialog
    List<Path>pathList;//图片采集路径，只有一条信息
    private CreateFileConstant createFileConstant;//创建本地目录
    private SharedPreferenceText sp;
    public LoginAccountUtils(Context mContext,EditText et_account,EditText et_password,CheckBox checkBox){
        this.mContext = mContext;
        this.et_account = et_account;
        this.et_password = et_password;
        lr = new LoginRegister();
        this.checkBox = checkBox;
        log = new LoadingDialog(mContext);
        createFileConstant = new CreateFileConstant();
        sp = new SharedPreferenceText(mContext);
    }
    /**
     * 查询登录过的账户，返回账户名称集合
     */
    public List<String> LoginAccountSelect(){
        doctorList = LitePal.where("loginCount>?","0").find(Doctor.class);
        accountName = new ArrayList<>();
        if(doctorList.size() > 0){
            for(int i = 0;i < doctorList.size();i++){
                accountName.add(doctorList.get(i).getdName());
            }
        }
        return accountName;
    }

    /**
     * 展示登陆过的信息
     */
    public void LoginAccountShow(){
        //引入布局
        View showView = LayoutInflater.from(mContext).inflate(R.layout.pupop_view,null);
        //对话框显示的listview
        ListView listViewShow = showView.findViewById(R.id.lv_docshow);
        listViewShow.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,LoginAccountSelect()));
        //初始化对话框
        PopupWindow popupWindowShow = new PopupWindow(showView,300, LinearLayout.LayoutParams.WRAP_CONTENT,true);
        //设置显示位置
        popupWindowShow.showAsDropDown(et_account,0,2);
        popupWindowShow.setOutsideTouchable(true);
        listViewShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String accunt = LoginAccountSelect().get(i);
                Doctor doctor = lr.getDoctor(accunt);
                if(doctor.getdTemp() == 0){
                    et_account.setText(accunt);
                    et_password.setText("");
                    checkBox.setChecked(false);
                }else {
                    et_account.setText(accunt);
                    et_password.setText(doctor.getdPassword());
                    checkBox.setChecked(true);
                }
                popupWindowShow.dismiss();
            }
        });
    }

    /**
     * 初始化管理员账户
     */
    public void initAdministrators(){
        adminList = LitePal.where("controllerName=?",managerAccount).find(Admin.class);
        //当管理员账户为空时，写入数据
        if(adminList.size() == 0){
            Admin admin = new Admin();
            admin.setControllerName(managerAccount);
            admin.setControllerPassword("123456");
            admin.save();
        }
    }
    /**
     * 账户异常处理，防止程序异常时，账户为空的情况
     */
    public void accountException(){
        exceptionManagerList = LitePal.findAll(ExceptionManager.class);
        //每次登录账户，将账户名称信息保存到异常处理账户中，防止异常时账户为空
        if(exceptionManagerList.size() == 0){
            ExceptionManager exceptionManager = new ExceptionManager();
            exceptionManager.setLoginName(et_account.getText().toString().trim());
            exceptionManager.save();
            OneItem.getOneItem().setName(exceptionManager.getLoginName());
        }else {
            for(int i = 0;i < exceptionManagerList.size();i++){
                exceptionManagerList.get(0).setLoginName(et_account.getText().toString().trim());
                exceptionManagerList.get(0).save();
            }
            OneItem.getOneItem().setName(exceptionManagerList.get(0).getLoginName());
        }
    }
    /**
     * 初始化系统设置
     */
    public void initSystemSet(){
        systemSetList = LitePal.findAll(SystemSet.class);
        //数据库为空时，填充数据
        if(systemSetList.size() == 0){
            SystemSet systemSet = new SystemSet();
            systemSet.setBackUpPath("FALUYUAN");//本地备份路径
            systemSet.setBackUpNetPath("/LUFAYUAN/");//ftp备份路径
            systemSet.setGather_path("SZB_save");//采集图片路径
            systemSet.setDialog_time(3);//对话框自动消失时间
            systemSet.save();
            OneItem.getOneItem().setBackUpPath(systemSet.getBackUpPath());
            OneItem.getOneItem().setBackUpNetPath(systemSet.getBackUpNetPath());
            OneItem.getOneItem().setGather_path(systemSet.getGather_path());
            Const.delayTime=systemSet.getDialog_time();
        }else {
            if (systemSetList.get(0).getBackUpPath() == null || "".equals(systemSetList.get(0).getBackUpPath())) {
                systemSetList.get(0).setBackUpPath("FALUYUAN");
            } else if (systemSetList.get(0).getBackUpNetPath() == null || "".equals(systemSetList.get(0).getBackUpNetPath())) {
                systemSetList.get(0).setBackUpNetPath("/LUFAYUAN/");
            }else if(systemSetList.get(0).getGather_path()==null || "".equals(systemSetList.get(0).getGather_path())){
                systemSetList.get(0).setGather_path("SZB_save");
            }else if(systemSetList.get(0).getDialog_time()<0){
                systemSetList.get(0).setDialog_time(3);
            }
            OneItem.getOneItem().setBackUpPath(systemSetList.get(0).getBackUpPath());
            OneItem.getOneItem().setBackUpNetPath(systemSetList.get(0).getBackUpNetPath());
            OneItem.getOneItem().setGather_path(systemSetList.get(0).getGather_path());
            Const.delayTime=systemSetList.get(0).getDialog_time();
            systemSetList.get(0).save();
        }
    }

    /**
     * 开始登录的方法
     */
    public void startLogin(){
        String name = et_account.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if(managerAccount.equals(name)){
            f = lr.loginController(name,password);
        }else {
            f = lr.loginDoctor(name,password);
        }
        if(f){
            log.setMessage(mContext.getString(R.string.bt_dialog_show));
            log.dialogShow();
            new Thread(){
                @Override
                public void run() {
                    int activity = 0;//判断跳转到哪个页面，0代表管理员，1代表普通页面
                    if(managerAccount.equals(name)){
                        activity = 0;
                    }else {
                        initPicturePath();
                        cacheAccount(name);
                        loginNumber();
                        activity = 1;
                    }
                    if(loginResults!=null){
                        loginResults.startActivity(activity,log);
                    }

                }
            }.start();
        }else {
            loginResults.loginFaild(log);
            MyToast.showToast(mContext, mContext.getString(R.string.login_error_show));
//            Toast.makeText(mContext, mContext.getString(R.string.login_error_show), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化图片的保存路径
     */
    private void initPicturePath(){
        pathList = LitePal.findAll(Path.class);
        if(pathList.size() == 0){
            Path p = new Path();
            p.setPicPath("SZB_save");
            p.save();
        }
    }

    /**
     * 1、将登陆账户信息暂时保存下来，推出时自动清除
     * 2、登录医生账户时，开始创建医生账户文件夹,并将账户状态保存下来
     */
    private void cacheAccount(String name){
        //  1
        Doctor doctor = lr.getDoctor(name);
        OneItem.getOneItem().setHospital_name(doctor.getEdit_hos_name());
        OneItem.getOneItem().setHospital_keshi(doctor.getEdit_hos_keshi());
        OneItem.getOneItem().setGather_path(getSD() + "/" +OneItem.getOneItem().getGather_path());
        //  2
        OneItem.getOneItem().setName(name);
        //压缩文件保存位置，主要用于备份
        File docFile = new File(Environment.getExternalStorageDirectory(), OneItem.getOneItem().getBackUpPath());
        if(!docFile.exists()){
            docFile.mkdirs();
        }
        //以日期创建医生目录
        createFileConstant.initCreateDocLogPath();
        //创建初始化部分图片文件时的路径
        createFileConstant.initCreateFLY();
        //判断是否保存账户密码
        if(checkBox.isChecked()){
            sp.setLoginInfo(et_account.getText().toString().trim(), et_password.getText().toString().trim());
            doctor.setdTemp(1);
        }else {
            sp.setLoginInfo(et_account.getText().toString().trim(),"");
            doctor.setdTemp(0);
        }
        doctor.save();
    }

    /**
     * 将用户登录次数记录下来，在获取图像界面使用
     */
    private void loginNumber(){
       systemSetList = LitePal.findAll(SystemSet.class);
       if(systemSetList.size() > 0){
           if(systemSetList.get(0).getThreshold() > 0){
               Const.threshold = systemSetList.get(0).getThreshold();
           }
           if(systemSetList.get(0).getThresholddianyou() > 0){
               Const.thresholddiainyou = systemSetList.get(0).getThresholddianyou();
           }
       }
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


    public interface LoginResults{
        void startActivity(int activity,LoadingDialog loadingDialog);
        void loginFaild(LoadingDialog loadingDialog);
    }
    static LoginResults loginResults;
    public static void setLoginResultsListener(LoginResults loginResultsListener){
        loginResults = loginResultsListener;
    }
}
