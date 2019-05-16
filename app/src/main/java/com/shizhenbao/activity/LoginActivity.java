package com.shizhenbao.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.activity.base.BaseActivity;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.db.Backup;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.Admin;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.ExceptionManager;
import com.shizhenbao.pop.Path;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.sharePreferencess.SharedPreferenceText;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.util.AlignedTextUtils;
import com.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;


public class LoginActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener {
    private EditText userName;
    private EditText userPass;
    private TextView register;
    private Button login;
    private CheckBox checkBox;
    private LoginRegister lr;
    private TextView tv_LoginName, tv_LoginPass,title_bar;
    private long exitTime = 0;
    //log输出
    String TAG = "MainActivityTAG";
    private List<SystemSet> list;
    private SystemSet systemSet;
    private SharedPreferenceText sp;
    private SharedPreferences sharedPreferences;
    private List<Diacrisis> diacrisisList = DataSupport.findAll(Diacrisis.class);
    private LinearLayout ll_login;
    private LoadingDialog log; //登陆时的dialog
    private RxPermissions rxPermissions;
    boolean f=false;
    private Button down_but;
    private String[] tvDatas;
    private TextView tv_1,tv_2;
    private Backup backup=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        Intent intent=getIntent();
        int temp=intent.getIntExtra("msg",0);
        if(temp==1){
            userName.setText("");
            userPass.setText("");
            checkBox.setChecked(false);
        }
        OneItem.getOneItem().setHospital_name("");
        OneItem.getOneItem().setBackUpPath("");
        OneItem.getOneItem().setBackUpNetPath("");
        OneItem.getOneItem().setGather_path("");
        initSys();
    }

    //登录账户记录
    public void pupopshow() {

        View pupopview=LoginActivity.this.getLayoutInflater().inflate(R.layout.pupop_view, null);
        ListView listview= (ListView) pupopview.findViewById(R.id.lv_docshow);
        listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getloginDoc()));
        final PopupWindow pupopwindow=new PopupWindow(pupopview,300, LinearLayout.LayoutParams.WRAP_CONTENT,true);//设置对话框的大小
        // TODO：更新popupwindow的状态
//        pupopwindow.update(100,100);
        // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
        pupopwindow.showAsDropDown(userName, 0, 2);
        pupopwindow.setOutsideTouchable(true);//设置对话框外可点击
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Doctor doctor = lr.getDoctor(getloginDoc().get(i));
                if(doctor.getdTemp()==0){
                    userName.setText(getloginDoc().get(i));
                    userPass.setText("");
                    checkBox.setChecked(false);
                }else {
                    userName.setText(getloginDoc().get(i));
                    userPass.setText(doctor.getdPassword());
                    checkBox.setChecked(true);
                }
                pupopwindow.dismiss();
            }
        });
    }

    private List<String>getloginDoc(){//查询登录过得账户
        List<Doctor>loginDoc=DataSupport.where("loginCount>?","0").find(Doctor.class);
        List<String>login=new ArrayList<>();
        if(loginDoc.size()>0){
            for(int i=0;i<loginDoc.size();i++){
                login.add(loginDoc.get(i).getdName());
            }
        }
        return login;
    }
    private void initSys() {
        List<Admin>admins=DataSupport.where("controllerName=?","上海法路源").find(Admin.class);//隐藏账户数据库
        Admin admin=null;
        if(admins.size()==0){
            admin=new Admin();
            admin.setControllerName("上海法路源");
            admin.setControllerPassword("123456");
            admin.save();
        }
        List<ExceptionManager>exceptionManagerList=DataSupport.findAll(ExceptionManager.class);//账户异常处理数据库
        ExceptionManager exceptionManager;
        if(exceptionManagerList.size()==0){
            exceptionManager=new ExceptionManager();
            exceptionManager.setLoginName(userName.getText().toString().trim());
            exceptionManager.save();
            OneItem.getOneItem().setName(exceptionManager.getLoginName());
        }else {
            for(int i=0;i<exceptionManagerList.size();i++){
                exceptionManagerList.get(0).setLoginName(userName.getText().toString().trim());
                exceptionManagerList.get(0).save();
            }
            OneItem.getOneItem().setName(exceptionManagerList.get(0).getLoginName());
        }
        List<SystemSet> system = DataSupport.findAll(SystemSet.class);//系统数据设置数据库
        SystemSet systemSet1;
        if (system.size() == 0) {//数据为空时创建表
            systemSet1=new SystemSet();
            systemSet1.setBackUpPath("FALUYUAN");
            systemSet1.setBackUpNetPath("/LUFAYUAN/");
            systemSet1.setGather_path("FLY_SZB");
            systemSet1.setDialog_time(3);
            systemSet1.save();
            OneItem.getOneItem().setBackUpPath(systemSet1.getBackUpPath());
            OneItem.getOneItem().setBackUpNetPath(systemSet1.getBackUpNetPath());
            OneItem.getOneItem().setGather_path(systemSet1.getGather_path());
            Const.delayTime=systemSet1.getDialog_time();
        } else {
            if (system.get(0).getBackUpPath() == null || "".equals(system.get(0).getBackUpPath())) {
                system.get(0).setBackUpPath("FALUYUAN");
            } else if (system.get(0).getBackUpNetPath() == null || "".equals(system.get(0).getBackUpNetPath())) {
                system.get(0).setBackUpNetPath("/LUFAYUAN/");
            }else if(system.get(0).getGather_path()==null || "".equals(system.get(0).getGather_path())){
                system.get(0).setGather_path("FLY_SZB");
            }else if(system.get(0).getDialog_time()<0){
                system.get(0).setDialog_time(3);
            }
            OneItem.getOneItem().setBackUpPath(system.get(0).getBackUpPath());
            OneItem.getOneItem().setBackUpNetPath(system.get(0).getBackUpNetPath());
            OneItem.getOneItem().setGather_path(system.get(0).getGather_path());
            Const.delayTime=system.get(0).getDialog_time();
            system.get(0).save();
        }
    }

    private void init() {
        backup=new Backup(this);
        tvDatas = new String[]{getString(R.string.user_name),getString(R.string.user_password)};
        tv_1= (TextView) findViewById(R.id.tv_loginname);
        tv_2= (TextView) findViewById(R.id.tv_loginpass);
        TextView []textViews={tv_1,tv_2};
        for (int i=0;i<tvDatas.length;i++) {
            if (textViews[i] != null) {
                textViews[i].setText(AlignedTextUtils.justifyString(tvDatas[i], 2 ));
            }

        }
        lr = new LoginRegister();
        down_but = (Button) findViewById(R.id.down_but);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        ll_login.requestFocus();//获取焦点
        userName = (EditText) findViewById(R.id.edit_loginName);//登陆的姓名
        userPass = (EditText) findViewById(R.id.edit_loginPass);//密码
        login = (Button) findViewById(R.id.login);//登陆按钮
        login.setOnClickListener(this);
        register = (TextView) findViewById(R.id.tv_register);//注册按钮
        register.setOnClickListener(this);
        checkBox = (CheckBox) findViewById(R.id.checkBox_login);//记住密码的CheckBox
        checkBox.setOnCheckedChangeListener(this);
        sp = new SharedPreferenceText(this);
        down_but.setOnClickListener(this);
        tv_LoginName = (TextView) findViewById(R.id.tv_loginname); //用户名
        tv_LoginPass = (TextView) findViewById(R.id.tv_loginpass); //密码
        title_bar= (TextView) findViewById(R.id.title_bar);
        title_bar.setText(getString(R.string.app_name));
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String spName = sharedPreferences.getString("loginName", null);
        String spPass = sharedPreferences.getString("loginPass", null);
//        TextView tvId[] = {tv_LoginName,tv_LoginPass};
        tv_LoginName.setText(AlignedTextUtils.justifyString(tvDatas[0],3));
        tv_LoginPass.setText(AlignedTextUtils.justifyString(tvDatas[1],3));
        log = new LoadingDialog(this);
        rxPermissions = new RxPermissions(this);

        if (spName == null || spName.equals("") || spName == null || spPass.equals("")) {
            userName.setText(spName);
            userPass.setText("");
            checkBox.setChecked(false);
        } else {
            userName.setText(spName);
            userPass.setText(spPass);
            checkBox.setChecked(true);
        }
    }

    private void ToActivity(Class<?> activity) {
        Intent it = new Intent(LoginActivity.this, activity);
        startActivity(it);
    }

    public void addImage() {
        Resources res = getResources();//添加空白图片
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.kb);//app中自带的空白的图片
        Bitmap img = d.getBitmap();
        String fn = "kb.png";//设置缓存到本地图片的名称
        String path = new Item().getSD() + "/AFLY/" + fn;//声明保存的路径
        try {
            OutputStream os = new FileOutputStream(path);//输出流
            img.compress(Bitmap.CompressFormat.PNG, 100, os);//设置图片的大小
            os.close();//关闭流
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

    public void addImage1() {
        Resources res = getResources();//添加空白图片
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.gjt);//app中自带的空白的图片
        Bitmap img = d.getBitmap();
        String fn = "qq.png";//设置缓存到本地图片的名称
        String path = new Item().getSD() + "/AFLY/" + fn;//声明保存的路径
        try {
            OutputStream os = new FileOutputStream(path);//输出流
            img.compress(Bitmap.CompressFormat.PNG, 100, os);//设置图片的大小
            os.close();//关闭流
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

    public void addImage2() {
        Resources res = getResources();//添加空白图片
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.fwt_1);//app中自带的空白的图片
        Bitmap img = d.getBitmap();
        String fn = "fwt.png";//设置缓存到本地图片的名称
        String path = new Item().getSD() + "/AFLY/" + fn;//声明保存的路径
        try {
            OutputStream os = new FileOutputStream(path);//输出流
            img.compress(Bitmap.CompressFormat.PNG, 100, os);//设置图片的大小
            os.close();//关闭流
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox_login:
                if (isChecked) {
                    sp.setLoginInfo(userName.getText().toString().trim(), userPass.getText().toString().trim());

                } else {
                    sp.setLoginInfo(" ", " ");
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: //登陆之后，跳到下一个界面
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    login();
                                } else {
                                    Toast.makeText(LoginActivity.this, R.string.bt_login_permission, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


//                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
//                        , Manifest.permission.CAMERA).subscribe(new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean aBoolean) {
//                        if (aBoolean) {
//                            login();
//                        } else {
//                            Toast.makeText(LoginActivity.this, R.string.bt_login_permission, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

                break;
            case R.id.tv_register: //点击注册按钮之后，跳到注册界面
                ToActivity(RegisterActicity.class);
//                pupopshow();
                break;
            case R.id.down_but:
                pupopshow();
                break;
        }
    }


    //登陆
    private void login() {
        String memory=backup.getSDMemory();//手机总内存
        String memoty1=backup.getSDMemoryky();//手机可用内存
        String memory_new=memory.substring(0,memory.indexOf("G"));
        String memory1_new=memoty1.substring(0,memoty1.indexOf("G"));
//        String memory2=backup.getAutoFilesSize();
//        Toast.makeText(this, "文件大小:"+memory2, Toast.LENGTH_SHORT).show();
        if(Double.parseDouble(memory1_new)/Double.parseDouble(memory_new)<0.2){
            Toast.makeText(this, "内存已不足，请进行备份", Toast.LENGTH_SHORT).show();
        }
        if(userName.getText().toString().trim().equals("上海法路源")||userName.getText().toString().trim()=="上海法路源"){
            f = lr.loginController(userName.getText().toString().trim(), userPass.getText().toString().trim());//判断用户名和密码是否满足条件
        }else{
            f = lr.loginDoctor(userName.getText().toString().trim(), userPass.getText().toString().trim());//判断用户名和密码是否满足条件
        }
        if (f) {
            log.setMessage(getString(R.string.bt_dialog_show));
            log.dialogShow();
            new Thread() {
                @Override
                public void run() {
                    if(userName.getText().toString().trim().equals("上海法路源")||userName.getText().toString().trim()=="上海法路源"){
                        ToActivity(AdminManager.class);
                    }else {
                        List<Path>pathList=DataSupport.findAll(Path.class);//保存所有的图片保存路径
                        if(pathList.size()==0){
                            Path path=new Path();
                            path.setPicPath("SZB_save");
                            path.save();
                        }
                        Doctor doctor = lr.getDoctor(userName.getText().toString().trim());
//                        doctor.setLoginCount(doctor.getLoginCount()+1);

                        if (diacrisisList.size() <= 0) {
                            new UserManager(LoginActivity.this).showTermList();
                        }
                        OneItem.getOneItem().setB(true);
                        OneItem.getOneItem().setHospital_name(doctor.getEdit_hos_name());
                        OneItem.getOneItem().setHospital_keshi(doctor.getEdit_hos_keshi());
//                        bianhao();
                        list = DataSupport.findAll(SystemSet.class);
                        if (list.size() != 0) {
                            for (int i = 0; i < list.size(); i++) {
                                systemSet = list.get(0);
                                if (systemSet.getSzb_wifi_user() != null || "".equals(systemSet.getSzb_wifi_user())) {
                                    Const.nameShiZhenBao = (systemSet.getSzb_wifi_user());
                                } else {
                                    Const.nameShiZhenBao = "IPCAM_AP_80003016";
                                }
                                if (systemSet.getSzb_wifi_pwd() != null || "".equals(systemSet.getSzb_wifi_pwd())) {
                                    Const.passShiZhenBao = (systemSet.getSzb_wifi_pwd());
                                } else {
                                    Const.passShiZhenBao = "12345678";
                                }
                                if (systemSet.getPrinter_wifi_user() != null || "".equals(systemSet.getPrinter_wifi_user())) {
                                    Const.nameHP = (systemSet.getPrinter_wifi_user());
                                } else {
                                    Const.nameHP = "DIRECT-A5-HP DeskJet 3630 series";
                                }
                                if (systemSet.getPrinter_wifi_pwd() != null || "".equals(systemSet.getPrinter_wifi_pwd())) {
                                    Const.passHP = (systemSet.getPrinter_wifi_pwd());
                                } else {
                                    Const.passHP = "12345678";
                                }
                                OneItem.getOneItem().setGather_path(new Item().getSD() + "/"+OneItem.getOneItem().getGather_path());
                            }
                        } else {
                            Const.nameHP = "DIRECT-A5-HP DeskJet 3630 series";
                            Const.passHP = "12345678";
                            Const.nameShiZhenBao = "IPCAM_AP_80003016";
                            Const.passShiZhenBao = "12345678";
                            OneItem.getOneItem().setGather_path(new Item().getSD() +  "/"+OneItem.getOneItem().getGather_path());
                        }
                        if (f) {
                            OneItem.getOneItem().setName(userName.getText().toString().trim());
                            File appDir = new File(Environment.getExternalStorageDirectory(), OneItem.getOneItem().getBackUpPath());//压缩文件存储文件夹
                            if (!appDir.exists()) {
                                appDir.mkdirs();
                            }
                            //创建医生诊断信息的文件夹
                            new CreateFileConstant().initCreateDocLogPath();
                            new CreateFileConstant().initCreateFLY();//默认图片文件夹
                            addImage();
                            addImage1();
                            addImage2();
                            //获得屏幕的尺寸
                            //登陆时，将账号和密码保存到本地
                            if (checkBox.isChecked()) {
                                sp.setLoginInfo(userName.getText().toString().trim(), userPass.getText().toString().trim());
                                doctor.setdTemp(1);
                            } else {
                                sp.setLoginInfo(userName.getText().toString().trim(),"");
                                doctor.setdTemp(0);
                            }
                            doctor.save();
                            //创建医生诊断信息的文件夹
                            new CreateFileConstant().initCreateDocLogPath();
                            new CreateFileConstant().initCreateFLY();
                            addImage();
                            addImage1();
                            addImage2();

                            //将用户登录的次数保存到Const 中 ,在图像获取界面有用
                            Const.loginCount = lr.getDoctorLoginCount(userName.getText().toString().trim());
//                            Logger.e("登录的次数 Const.loginCount = "+Const.loginCount +" ");
                            List<SystemSet> system= DataSupport.findAll(SystemSet.class);
                            if (system.size() > 0) {
                                if (system.get(0).getThreshold() > 0) {
                                    Const.threshold=system.get(0).getThreshold();
                                }
                                if (system.get(0).getThresholddianyou()>0) {
                                    Const.thresholddiainyou=system.get(0).getThresholddianyou();
                                }
                            }
                            //跳到这个界面
                            ToActivity(MainActivity.class);
                            finish();
                        }
                    }
                }
            }.start();
        } else {
            Toast.makeText(this, R.string.login_error_show, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.CAMERA)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                login();
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.bt_login_permission, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
//                    , Manifest.permission.CAMERA).subscribe(new Action1<Boolean>() {
//                @Override
//                public void call(Boolean aBoolean) {
//                    if (aBoolean) {
//                        login();
//                    } else {
//                        Toast.makeText(LoginActivity.this, R.string.bt_login_permission, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (log != null) {
            log.dismiss();
        }

    }

    @Override
    public void doSDCardPermission() {
        super.doSDCardPermission();
        login();//申请完权限之后，就开始登陆
    }
}
