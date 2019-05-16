package com.shizhenbao.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.activity.LoginActivity;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.util.OneItem;


public class XiugaiPasswordActivity extends AppCompatActivity {
    Doctor doctor;
    EditText edit_oldPass,edit_newPass,edit_registerPassCover;
    private Button btn_registerSure,btn_registerBack,bt_right,bt_left;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiugai_password);
        initView();
        doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());//根据医生姓名得到对应的医生的信息
        innitData();
    }

    private void innitData() {
        btn_registerSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edit_oldPass.getText().toString().trim())&&!TextUtils.isEmpty(edit_newPass.getText().toString().trim())&&!TextUtils.isEmpty(edit_registerPassCover.getText().toString().trim())){
                    if(edit_oldPass.getText().toString().equals(doctor.getdPassword())){//判断输入密码是否和数据库密码一致
                        if(edit_newPass.getText().toString().equals(edit_registerPassCover.getText().toString())){//判断两次输入密码是否一致
                            if(edit_newPass.getText().toString().trim().length()>=6){//判断输入的密码长度是否大于6.如果大于6，则可以修改，否则会提示重新输入
                                doctor.setdPassword(edit_newPass.getText().toString());//修改对应一生的密码
                                doctor.save();//保存对应的医生信息
                                Toast.makeText(XiugaiPasswordActivity.this, R.string.setting_password_modify_success, Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(XiugaiPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {//输入密码少于6位
                                edit_newPass.setText("");//清空输入框
                                edit_registerPassCover.setText("");//清空输入框
                                edit_newPass.requestFocus();//获取焦点
                                Toast.makeText(XiugaiPasswordActivity.this, R.string.user_register_password_error, Toast.LENGTH_SHORT).show();//提示信息
                            }

                        }else {//两次输入密码不一致
                            edit_newPass.setText("");//清空数据框
                            edit_registerPassCover.setText("");//清空数据框
                            edit_newPass.requestFocus();//获取焦点
                            Toast.makeText(XiugaiPasswordActivity.this, R.string.setting_password_Dissimilarity, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(XiugaiPasswordActivity.this, R.string.setting_password_Original, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(XiugaiPasswordActivity.this, R.string.setting_message_nothing, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        edit_newPass= (EditText) findViewById(R.id.edit_newPass);//新密码
        edit_oldPass= (EditText) findViewById(R.id.edit_oldPass);//旧密码
        edit_registerPassCover= (EditText) findViewById(R.id.edit_registerPassCover);//第二次输入密码
        btn_registerBack= (Button) findViewById(R.id.btn_registerBack);//返回
        btn_registerSure= (Button) findViewById(R.id.btn_registerSure);//确定
        bt_left= (Button) findViewById(R.id.btn_left);
        bt_right= (Button) findViewById(R.id.btn_right);
        tv_title= (TextView) findViewById(R.id.title_text);
        tv_title.setText(getString(R.string.setting_Modify_password));
        bt_right.setVisibility(View.INVISIBLE);//设置为不可见
    }
}
