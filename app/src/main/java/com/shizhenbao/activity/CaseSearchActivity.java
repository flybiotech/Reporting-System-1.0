package com.shizhenbao.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.OneItem;
import com.util.AlignedTextUtils;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CaseSearchActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editName,editTel,edit01, edit02,startDate,endDate;
    private Button startBtn,bt_left,bt_right;
    private TextView tv;

    private List<String> list2;
    private LoginRegister loginRegister;//登陆注册，获取医生相关信息的类
    private UserManager userManager;
    //判断证件相关的edittext是不是为空
    boolean f1, f2, f3, f4, f5,f6;
    boolean isAdmin=true;
    //存放患者
    private List<User> userCaseList = new ArrayList<>();

    //文字两端对齐
    private TextView tv01,tv02,tv03, tv04;
    private String[] nameTv;

    public static void startAct(Context context) {
        context.startActivity(new Intent(context, CaseSearchActivity.class));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_search);
        initView();
        initTextView();
        //判断用户是否是超级管理员， true 表示是超级管理员，，false 表示是普通医生
        isAdmin= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();

    }

    private void initView() {
        nameTv = new String[]{getString(R.string.patient_name), getString(R.string.case_patient_telephone),getString(R.string.patient_medical_record_number), getString(R.string.patient_social_security_number)};
        editName = (EditText) findViewById(R.id.edit_casesearch_Name);
        editTel = (EditText) findViewById(R.id.edit_casesearch_tel);
        edit01 = (EditText) findViewById(R.id.edit_casesearch_01);//病例号
        edit02 = (EditText) findViewById(R.id.edit_casesearch_02);//社保号码
        startDate = (EditText) findViewById(R.id.edit_casesearch_datestart);//开始时间
        endDate = (EditText) findViewById(R.id.edit_casesearch_dateend);//结束时间
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);

        startBtn = (Button) findViewById(R.id.btn_casesearch_search);
        bt_left = (Button) findViewById(R.id.btn_left);//左边按钮
        bt_right = (Button) findViewById(R.id.btn_right);//右边的按钮
        startBtn.setOnClickListener(this);

        tv = (TextView) findViewById(R.id.title_text);//title
        bt_right.setVisibility(View.GONE);
//        bt_right.setText("");
        bt_left.setVisibility(View.VISIBLE);//设置返回按钮为可视
        bt_left.setOnClickListener(this);
        tv.setText(getString(R.string.patient_select_patients_condition));//改变页面标题3

        loginRegister = new LoginRegister();//登陆注册，获取医生相关信息的类
        userManager = new UserManager();
        list2 = new ArrayList<String>();
    }

    private void initTextView() {
        tv01 = (TextView) findViewById(R.id.tv_casesearch_01);
        tv02 = (TextView) findViewById(R.id.tv_casesearch_02);
        tv03 = (TextView) findViewById(R.id.tv_casesearch_03);
        tv04 = (TextView) findViewById(R.id.tv_casesearch_04);
        TextView[] tvID = {tv01, tv02, tv03, tv04};
        for (int i=0;i<nameTv.length;i++) {
            tvID[i].setText(AlignedTextUtils.justifyString(nameTv[i],4));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month=c.get(Calendar.MONTH);
//        int dayOfMonth=c.get(Calendar.DAY_OF_MONTH);
//        endDate.setText(year+"-"+(month+1)+"-"+dayOfMonth);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.btn_left:
//                finish();
//                break;
            //开始查询
            case R.id.btn_casesearch_search:

//                if (editName.getText().toString().trim().equals("")&&editTel.getText().toString().trim().equals("")
//                        &&edit01.getText().toString().trim().equals("")&&edit02.getText().toString().trim().equals("")
//                        &&startDate.getText().toString().trim().equals("") &&endDate.getText().toString().trim().equals("")) {
//                    Toast.makeText(this, "请输入查询条件", Toast.LENGTH_SHORT).show();
//
//                } else {
                    Toast.makeText(this, getString(R.string.patient_select_patients_message), Toast.LENGTH_SHORT).show();
                    getUserInfo();
                    Intent intent = new Intent(CaseSearchActivity.this, CaseListManagerActivity.class);
                    intent.putExtra("listCase", (Serializable) userCaseList);
                    startActivity(intent);
//                }



                break;

            //开始时间
            case R.id.edit_casesearch_datestart:
                startDate.setText("");
                setDateDialog(startDate);
                break;

            //结束时间
            case R.id.edit_casesearch_dateend:
                endDate.setText("");
                setDateDialog(endDate);
                break;
        }
    }


    private void getUserInfo() {
        list2.clear();
        //姓名
        if (editName.getText().toString().trim().equals("") || editName.getText().toString().trim() == null) {
            f1 = false;
        } else {
            list2.add(editName.getText().toString().trim());
            f1 = true;
        }

        //电话号码
        if (editTel.getText().toString().trim().equals("") || editTel.getText().toString().trim() == null) {
            f2 = false;
        } else {
            list2.add(editTel.getText().toString().trim());
            f2 = true;
        }

        //病历号
        if (edit01.getText().toString().trim().equals("") || edit01.getText().toString().trim() == null) {
            f3 = false;
        } else {
            list2.add(edit01.getText().toString().trim());
            f3 = true;
        }

        //社保号
        if (edit02.getText().toString().trim().equals("") || edit02.getText().toString().trim() == null) {
            f4 = false;
        } else {
            list2.add(edit02.getText().toString().trim());
            f4 = true;
        }

        if (!startDate.getText().toString().equals("") && !endDate.getText().toString().equals("")) {
            list2.add((isDate2Bigger(startDate.getText().toString())-100)+"");
            list2.add((isDate2Bigger(endDate.getText().toString())+100)+"");
//            Logger.e("查询时间 ：开始时间："+isDate2Bigger(startDate.getText().toString())+
//                    "开始时间+100 "+(isDate2Bigger(endDate.getText().toString())+100)+
//                    "   结束时间 ： "+isDate2Bigger(endDate.getText().toString())
//            +"   结束时间+100 "+(isDate2Bigger(endDate.getText().toString())+100));
            f5 = true;
            f6 = true;
        } else {

            f5 = false;
            f6 = false;
        }
        if (isAdmin) {
            //超级用户数据源
          userCaseList = userManager.getUserCaseBySearch(f1, f2, f3, f4,f5, f6,list2);
        } else {
            //普通用户资源
            userCaseList= userManager.getUserCaseBySearch(String.valueOf(new LoginRegister().getDoctorId(OneItem.getOneItem().getName())),f1, f2, f3, f4,f5,f6, list2);
        }

    }


    //设置日期
    public void setDateDialog(final EditText et) {
        Calendar c = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(CaseSearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                et.setText(year+"-"+(month+1)+"-"+dayOfMonth);

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

//        Logger.e( "获取时间 : "+c.get(Calendar.YEAR)+" : "+c.get(Calendar.MONTH)+" ："+c.get(Calendar.DAY_OF_MONTH));
    }


    public static long isDate2Bigger(String str1) {
        if (str1 == null||str1.equals("")) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        try {
            dt1 = sdf.parse(str1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dt1.getTime();
    }

}
