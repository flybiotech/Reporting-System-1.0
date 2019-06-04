package com.shizhenbao.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activity.ConfigApActivity;
import com.activity.R;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.constant.DeviceOfSize;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.fragments.FragSetting;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;
import com.util.SouthUtil;
import com.view.MyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActicity extends AppCompatActivity implements View.OnClickListener {
    EditText name, pass1, pass2, email,edit_hos_name,edit_hos_keshi;
    Button sure, back;
    LoginRegister lr;
    private TextView tv_title;
    private double screenInches;//屏幕的尺寸
    //再按一次退出程序
    private long exitTime = 0;
    List<Doctor>doctorList=LitePal.findAll(Doctor.class);
    private BackupsUtils backupsUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        screenInches = DeviceOfSize.getScreenSizeOfDevice2(this);
            if(doctorList.size()>0){
                setContentView(R.layout.activity_register);
            }else {
                setContentView(R.layout.admin_register);
            }
        init();
    }

    private void init() {
        lr = new LoginRegister();
        backupsUtils = new BackupsUtils(this);
        name = (EditText) findViewById(R.id.edit_registerName);//姓名
        pass1 = (EditText) findViewById(R.id.edit_registerPass);//密码
        pass2 = (EditText) findViewById(R.id.edit_registerPassCover);//确认密码
        email = (EditText) findViewById(R.id.edit_registerEmail);//邮箱
        tv_title = (TextView) findViewById(R.id.title_bar);
        edit_hos_name= (EditText) findViewById(R.id.edit_hos_name);
        edit_hos_keshi= (EditText) findViewById(R.id.edit_hos_keshi);
        tv_title.setText(getText(R.string.user_register));
        sure = (Button) findViewById(R.id.btn_registerSure);
        back = (Button) findViewById(R.id.btn_registerBack);
        sure.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<SystemSet> systemSet= LitePal.findAll(SystemSet.class);
        for(int i=0;i<systemSet.size();i++){
            String localsn=systemSet.get(0).getLocal_svn();
            Const.sn=localsn;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_registerSure://注册界面的确认按钮

              register();
                break;
            case R.id.btn_registerBack://注册界面的返回按钮
                finish();
                break;
            default:
                break;
        }
    }


    private void register() {


        if(doctorList.size()==0){
            if (TextUtils.isEmpty(name.getText().toString().trim())
                    || TextUtils.isEmpty(pass1.getText().toString().trim())
                    || TextUtils.isEmpty(edit_hos_name.getText().toString().trim())
                    || TextUtils.isEmpty(edit_hos_keshi.getText().toString().trim())) {
                MyToast.showToast(this,getString(R.string.user_register_message_error));
//                SouthUtil.showToast(this,getString(R.string.user_register_message_error));
            } else {
                if(pass1.getText().toString().trim().equals(pass2.getText().toString().trim())){
//                    boolean isEmail = isEmail(email.getText().toString());
//                    if (isEmail){
                        if(pass1.getText().toString().trim().length()>=6){
                            Const.dialogWifiSetting = true;
                            MyToast.showToast(this,getString(R.string.user_register_success));
//                            SouthUtil.showToast(this,getString(R.string.user_register_success));
                            Doctor doctor=new Doctor();
                            List<Doctor> list = LitePal.findAll(Doctor.class);
                            if(list.size()==0){//如果为第一次注册，用户id默认为1
                                doctor.setdId(1);
                            }
                            doctor.setdName(name.getText().toString());
                            doctor.setdEmail(email.getText().toString());
                            doctor.setdPassword(pass1.getText().toString());
                            doctor.setEdit_hos_name(edit_hos_name.getText().toString());
                            doctor.setEdit_hos_keshi(edit_hos_keshi.getText().toString());
                            doctor.setLoginCount(1);
                            doctor.setdAdmin(true);//超级用户
                            doctor.save();
                            new CreateFileConstant().initCreateDocPath(name.getText().toString());//创建医生文件夹
                            finish();
                        }else {
                            pass1.setText("");
                            pass2.setText("");
                            pass1.requestFocus();
                            MyToast.showToast(this,getString(R.string.user_register_password_error));
//                            SouthUtil.showToast(this,getString(R.string.user_register_password_error));
                        }
//                    }else {
//                        email.setText("");
//                        email.requestFocus();
//                        MyToast.showToast(this,getString(R.string.user_register_email_error));
////                        SouthUtil.showToast(this,getString(R.string.user_register_email_error));
//                    }
                }else {
                    MyToast.showToast(this,getString(R.string.user_register_faild_password));
//                    SouthUtil.showToast(this,getString(R.string.user_register_faild_password));
                }
            }
        }else {
            if (TextUtils.isEmpty(name.getText().toString().trim())
                    || TextUtils.isEmpty(pass1.getText().toString().trim())
                    ) {
                MyToast.showToast(this,getString(R.string.user_register_message_error));
//                SouthUtil.showToast(this,getString(R.string.user_register_message_error));
            } else {

                if(!"shanghaifaluyuan".equals(name.getText().toString().trim())) {
                    if (pass1.getText().toString().trim().equals(pass2.getText().toString().trim())) {
//                        boolean isEmail = isEmail(email.getText().toString());
//                        if (isEmail) {
                            int i = lr.addDoctor(name.getText().toString(), pass1.getText().toString(), this.email.getText().toString());
                            if (i == 1) {
                                MyToast.showToast(this,getString(R.string.user_register_faild_account));
//                                SouthUtil.showToast(this,getString(R.string.user_register_faild_account));
                            } else {
                                if (pass1.getText().toString().trim().length() >= 6) {
                                    Const.dialogWifiSetting = true;
                                    MyToast.showToast(this,getString(R.string.user_register_success));
//                                    SouthUtil.showToast(this,getString(R.string.user_register_success));
                                    Doctor doctor = new Doctor();
                                    List<Doctor> list = LitePal.findAll(Doctor.class);
                                    if (list.size() == 0) {//如果为第一次注册，用户id默认为1
                                        doctor.setdId(1);
                                    } else {
                                        doctor.setdId(list.get(list.size() - 1).getdId() + 1);//不是第一次注册，用户id为数据库最后注册用户的id+1
                                    }
                                    doctor.setdName(name.getText().toString());
                                    doctor.setdEmail(email.getText().toString());
                                    doctor.setdPassword(pass1.getText().toString());
                                    doctor.setEdit_hos_name("");
                                    doctor.setEdit_hos_keshi("");
                                    doctor.setLoginCount(1);
                                    if (name.getText().toString().equals("Admin")) {//根据姓名判断是否为超级管理员
                                        doctor.setdAdmin(true);//超级用户
                                    } else {
                                        doctor.setdAdmin(false);//普通用户
                                    }
                                    doctor.save();
                                    new CreateFileConstant().initCreateDocPath(name.getText().toString());//创建医生文件夹
                                    backupsUtils.initBackUpDoctor(1);
                                    finish();
                                } else {
                                    pass1.setText("");
                                    pass2.setText("");
                                    pass1.requestFocus();
                                    MyToast.showToast(this,getString(R.string.user_register_password_error));
//                                    SouthUtil.showToast(this,getString(R.string.user_register_password_error));
                                }
                            }
//                        } else {
//                            email.setText("");
//                            email.requestFocus();
//                            MyToast.showToast(this,getString(R.string.user_register_email_error));
////                            SouthUtil.showToast(this,getString(R.string.user_register_email_error));
//                        }
                    } else {
                        MyToast.showToast(this,getString(R.string.user_register_faild_password));
//                        SouthUtil.showToast(this,getString(R.string.user_register_faild_password));
                    }
                }else {
                    MyToast.showToast(this,getString(R.string.user_register_faild_account));
//                    SouthUtil.showToast(this,getString(R.string.user_register_faild_account));
                    name.setText("");
                    name.requestFocus();
                }
            }
        }
    }

    //判断email格式是否正确
    public boolean isEmail(String email) {
        String str ="^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                MyToast.showToast(this,getString(R.string.quit_program));
//                SouthUtil.showToast(this,getString(R.string.quit_program));
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(RegisterActicity.this.getCurrentFocus().getWindowToken(), 0);
            }
            if(event.getAction() == KeyEvent.ACTION_UP){
                register();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
