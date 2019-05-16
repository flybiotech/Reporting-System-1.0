package com.shizhenbao.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.constant.DeviceOfSize;
import com.shizhenbao.db.Backup;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.fragments.FragSetting;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

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
    List<Doctor>doctorList=DataSupport.findAll(Doctor.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        List<SystemSet> systemSet= DataSupport.findAll(SystemSet.class);
        for(int i=0;i<systemSet.size();i++){
            String localsn=systemSet.get(0).getLocal_svn();
            Const.sn=localsn;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_registerSure://注册界面的确认按钮
                if(doctorList.size()==0){
                    if (TextUtils.isEmpty(name.getText().toString().trim())
                            || TextUtils.isEmpty(pass1.getText().toString().trim())
                            || TextUtils.isEmpty(email.getText().toString().trim())
                            || TextUtils.isEmpty(edit_hos_name.getText().toString().trim())
                            || TextUtils.isEmpty(edit_hos_keshi.getText().toString().trim())) {
                        Toast.makeText(this,R.string.user_register_message_error, Toast.LENGTH_SHORT).show();
                    } else {
                            if(pass1.getText().toString().trim().equals(pass2.getText().toString().trim())){
                                boolean isEmail = isEmail(email.getText().toString());
                                if (isEmail){
                                    if(pass1.getText().toString().trim().length()>=6){
                                        Const.dialogWifiSetting = true;
                                        Toast.makeText(this, R.string.user_register_success, Toast.LENGTH_SHORT).show();
                                        Doctor doctor=new Doctor();
                                        List<Doctor> list = DataSupport.findAll(Doctor.class);
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
//                                        new Backup(this,"111").initBackDoctor();
//                                        try {
//                                            boolean existsfile=initDoctorLitepal();
//                                            if(existsfile){
//                                                new Backup(this,Const.sn).initBackDoctor();
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
                                        finish();
                                    }else {
                                        pass1.setText("");
                                        pass2.setText("");
                                        pass1.requestFocus();
                                        Toast.makeText(this, R.string.user_register_password_error, Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    email.setText("");
                                    email.requestFocus();
                                    Toast.makeText(this, R.string.user_register_email_error, Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(this, R.string.user_register_faild_password, Toast.LENGTH_SHORT).show();
                            }
                    }
                }else {
                    if (TextUtils.isEmpty(name.getText().toString().trim())
                            || TextUtils.isEmpty(pass1.getText().toString().trim())
                            || TextUtils.isEmpty(email.getText().toString().trim())
                            ) {
                        Toast.makeText(this,R.string.user_register_message_error, Toast.LENGTH_SHORT).show();
                    } else {
                        if(!name.getText().toString().trim().equals("上海法路源")&&name.getText().toString().trim()!="上海法路源") {
                            if (pass1.getText().toString().trim().equals(pass2.getText().toString().trim())) {
                                boolean isEmail = isEmail(email.getText().toString());
                                if (isEmail) {
                                    int i = lr.addDoctor(name.getText().toString(), pass1.getText().toString(), this.email.getText().toString());
                                    if (i == 1) {
                                        Toast.makeText(this, R.string.user_register_faild_account, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (pass1.getText().toString().trim().length() >= 6) {
                                            Const.dialogWifiSetting = true;
                                            Toast.makeText(this, R.string.user_register_success, Toast.LENGTH_SHORT).show();
                                            Doctor doctor = new Doctor();
                                            List<Doctor> list = DataSupport.findAll(Doctor.class);
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
                                            new Backup(this,Const.sn).initBackDoctor();
                                            finish();
                                        } else {
                                            pass1.setText("");
                                            pass2.setText("");
                                            pass1.requestFocus();
                                            Toast.makeText(this, R.string.user_register_password_error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    email.setText("");
                                    email.requestFocus();
                                    Toast.makeText(this, R.string.user_register_email_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this,R.string.user_register_faild_password, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(this, R.string.user_register_faild_account, Toast.LENGTH_SHORT).show();
                            name.setText("");
                            name.requestFocus();
                        }
                    }
                }
                break;
            case R.id.btn_registerBack://注册界面的返回按钮
                finish();
                break;
            default:
                break;
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
                Toast.makeText(this,R.string.quit_program, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    private boolean initDoctorLitepal() throws JSONException {
        File file=new File(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + Const.sn+"doctor.txt");
        if(!file.exists()){
            return true;
        }
        String litepal=new FragSetting().getFileFromSD(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + Const.sn+"doctor.txt");
        JSONObject jsonObject=new JSONObject(litepal);
        JSONArray array=jsonObject.getJSONArray("persondata");
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
           if(obj.getString("dName").equals("Admin")){
               return false;
           }
    }
    return true;
    }
}
