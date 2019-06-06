package com.shizhenbao.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;
import com.util.AlignedTextUtils;
import com.view.MyToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "TAG4";
    private List<String> list2;
    private LoginRegister loginRegister;//登陆注册，获取医生相关信息的类
  private UserManager userManager;
    //判断证件相关的edittext是不是为空
    boolean f1, f2, f3, f4, f5;
    boolean isAdmin=true;
    private TextView tv_name,tv_ID,tv_CaseNumbe,tv_data,tv_SSNumber;
    //社保号         病例号            社保号            护照号
    private EditText edit_IdNumber, edit_CaseNumbe, edit_SSNumber, edit_Name;
    private TextView tv,textView_date;
    private Button bt_right, bt_left, btn_search;
    private String []tv_candtion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_search_layout);
        init();
        //判断用户是否是超级管理员， true 表示是超级管理员，，false 表示是普通医生
        try{
            if(OneItem.getOneItem().getName()!=null&&!OneItem.getOneItem().getName().equals("")){
                isAdmin= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();
            }else {
                new UserManager().getExceName();
                isAdmin= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();
            }
        }catch (Exception e){
            new UserManager().getExceName();
            isAdmin= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();
        }
        //点击返回
        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
//        lv_select = (ListView) findViewById(R.id.listView_selectActivity);
        tv_candtion=new String[]{getString(R.string.patient_name),getString(R.string.patient_ID),getString(R.string.patient_medical_record_number),getString(R.string.patient_social_security_number),getString(R.string.patient_select_data)};
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_ID= (TextView) findViewById(R.id.tv_ID);
        tv_CaseNumbe= (TextView) findViewById(R.id.tv_CaseNumbe);
        tv_SSNumber= (TextView) findViewById(R.id.tv_SSNumber);
        tv_data= (TextView) findViewById(R.id.tv_data);
        //菜单选项
        tv = (TextView) findViewById(R.id.title_text);//title
        bt_left = (Button) findViewById(R.id.btn_left);//左边按钮
        bt_right = (Button) findViewById(R.id.btn_right);//右边的按钮
        btn_search = (Button) findViewById(R.id.btn_search_search);//搜索按钮

        edit_IdNumber = (EditText) findViewById(R.id.editText_search_IdNumber);
        edit_IdNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
        edit_CaseNumbe = (EditText) findViewById(R.id.editText_search_CaseNumbe);
        edit_CaseNumbe.setRawInputType(Configuration.KEYBOARD_QWERTY);
        edit_SSNumber = (EditText) findViewById(R.id.editText_search_SSNumber);
        edit_SSNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
        edit_Name = (EditText) findViewById(R.id.editText_search_Name);
        textView_date = (TextView) findViewById(R.id.textView_search_Date);

        textView_date.setOnClickListener(this);
        bt_right.setVisibility(View.GONE);
//        bt_right.setText("");
        bt_left.setVisibility(View.VISIBLE);//设置返回按钮为可视
        tv.setText(getString(R.string.patient_select_patients_condition));//改变页面标题
        loginRegister = new LoginRegister();//登陆注册，获取医生相关信息的类
        userManager = new UserManager();
        btn_search.setOnClickListener(this);
        list2 = new ArrayList<String>();
        TextView[] tvData ={tv_name,tv_ID,tv_CaseNumbe,tv_SSNumber,tv_data};
        for (int i=0;i<tvData.length;i++) {
            if (tv_candtion[i] != null) {
                tvData[i].setText(AlignedTextUtils.justifyString(tv_candtion[i], 4));
            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_search:
                MyToast.showToast(this,getString(R.string.patient_select_patients_message));
//                Toast.makeText(this, R.string.patient_select_patients_message, Toast.LENGTH_SHORT).show();
                getUserInfo();
                Intent intent = new Intent(SearchActivity.this, SelectActivity.class);
                startActivity(intent);
                break;
            //获取日期
            case R.id.textView_search_Date:
                textView_date.setText("");
                setDateDialog();
                break;
                default:
                    break;
        }

    }

    private void getUserInfo() {
        list2.clear();
        if (edit_IdNumber.getText().toString().equals("") || edit_IdNumber.getText().toString() == null) {
            f1 = false;
        } else {
            list2.add(edit_IdNumber.getText().toString());
            f1 = true;
        }

        if (edit_CaseNumbe.getText().toString().equals("") || edit_CaseNumbe.getText().toString() == null) {
            f2 = false;
        } else {
            list2.add(edit_CaseNumbe.getText().toString());
            f2 = true;
        }

        if (edit_SSNumber.getText().toString().equals("") || edit_SSNumber.getText().toString() == null) {
            f3 = false;
        } else {
            list2.add(edit_SSNumber.getText().toString());
            f3 = true;
        }

        if (edit_Name.getText().toString().equals("") || edit_Name.getText().toString() == null) {
            f4 = false;
        } else {
            list2.add(edit_Name.getText().toString());
            f4 = true;
        }

        if (textView_date.getText().toString().equals("") || textView_date.getText().toString() == null) {
            f5 = false;
        } else {
            list2.add(textView_date.getText().toString());
            f5 = true;
        }
        if (isAdmin) {
            //超级用户数据源
            userManager.getUserBySearch(f1, f2, f3, f4, f5, list2);
        } else {
            //普通用户资源
            userManager.getUserBySearch(String.valueOf(new LoginRegister().getDoctorId(OneItem.getOneItem().getName())),f1, f2, f3, f4, f5, list2, Const.offSetCount);
        }

    }


    //设置日期
    public void setDateDialog() {
        Calendar c = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    textView_date.setText(year+"-"+(month+1)+"-"+dayOfMonth);

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

        Log.d(TAG, "获取时间 : "+c.get(Calendar.YEAR)+" : "+c.get(Calendar.MONTH)+" "+c.get(Calendar.DAY_OF_MONTH));
    }


}
