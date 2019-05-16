package com.shizhenbao.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.adapter.JianJiSearchAdapter;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.util.AlignedTextUtils;
import com.view.MyToast;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;


public class JianJiSearchActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editName,editTel,edit01, edit02,startDate,endDate;
    private Button startBtn,btn_left,btn_right;
    private TextView tv;

    private List<String> list2;
    private LoginRegister loginRegister;//登陆注册，获取医生相关信息的类
    private UserManager userManager;
    //判断证件相关的edittext是不是为空
    boolean f1, f2, f3, f4, f5,f6;
    boolean isAdmin=true;
    //存放患者
    private List<User> userCaseList = new ArrayList<>();
    int num=0;//判断翻页次数
    double temp;//患者信息所占页数
    private List<Item> list1;//展示数据源
    List<Item> listItem= new ArrayList<Item>();
    private List<User> userlist=new ArrayList<>();
    int usersize=0;//所有未诊断患者的数量
    //文字两端对齐
    private TextView tv01,tv02,tv03, tv04;
    private String[] nameTv;

//    public static void startAct(Context context) {
//        context.startActivity(new Intent(context, CaseSearchActivity.class));
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_jian_ji_search);
        initView();
        initTextView();
        //判断用户是否是超级管理员， true 表示是超级管理员，，false 表示是普通医生
        try {
            isAdmin= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        nameTv = new String[]{getString(R.string.patient_name), getString(R.string.case_patient_telephone), getString(R.string.patient_medical_record_number), getString(R.string.patient_social_security_number)};
        editName = (EditText) findViewById(R.id.edit_casesearch_Name);
        editTel = (EditText) findViewById(R.id.edit_casesearch_tel);
        edit01 = (EditText) findViewById(R.id.edit_casesearch_01);//病例号
        edit02 = (EditText) findViewById(R.id.edit_casesearch_02);//社保号码
        startDate = (EditText)findViewById(R.id.edit_casesearch_datestart);//开始时间
        endDate = (EditText)findViewById(R.id.edit_casesearch_dateend);//结束时间
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);

        startBtn = (Button)findViewById(R.id.btn_casesearch_search);
        btn_left = (Button)findViewById(R.id.btn_left);//左边按钮
        btn_right = (Button)findViewById(R.id.btn_right);//右边的按钮
        startBtn.setOnClickListener(this);

        tv = (TextView)findViewById(R.id.title_text);//title
        btn_right.setVisibility(View.GONE);
//        bt_right.setText("");
        btn_left.setVisibility(View.VISIBLE);//设置返回按钮为可视
        btn_left.setOnClickListener(this);
        tv.setText(getString(R.string.image_Select_patients));//改变页面标题3

        loginRegister = new LoginRegister();//登陆注册，获取医生相关信息的类
        userManager = new UserManager();
        list2 = new ArrayList<String>();
    }

    private void initTextView() {
        tv01 = (TextView)findViewById(R.id.tv_casesearch_01);
        tv02 = (TextView)findViewById(R.id.tv_casesearch_02);
        tv03 = (TextView)findViewById(R.id.tv_casesearch_03);
        tv04 = (TextView)findViewById(R.id.tv_casesearch_04);
        TextView[] tvID = {tv01, tv02, tv03, tv04};
        for (int i=0;i<nameTv.length;i++) {
            tvID[i].setText(AlignedTextUtils.justifyString(nameTv[i],4));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int dayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        endDate.setText(year+"-"+(month+1)+"-"+dayOfMonth);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_left:
               finish();
                break;
            //开始查询
            case R.id.btn_casesearch_search:

//                    Toast.makeText(this, "正在查询患者信息", Toast.LENGTH_SHORT).show();
                    getUserInfo();
                if (userCaseList.size() > 0) {
//                    lv_add(userCaseList);
                    lv_list(0);
                } else {
                    MyToast.showToast(this, getString(R.string.case_patient_nothing));
//                    Toast.makeText(this, getString(R.string.case_patient_nothing), Toast.LENGTH_SHORT).show();
                }

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
            default:
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
//                    +"   结束时间+100 "+(isDate2Bigger(endDate.getText().toString())+100));
            f5 = true;
            f6 = true;
        } else {

            f5 = false;
            f6 = false;
        }
        if (isAdmin) {
            //超级用户数据源
            userCaseList = userManager.getUserCaseBySearch(f1, f2, f3, f4,f5, f6,list2);
//            lv_list(0);
            Const.list=userCaseList;
        } else {
            //普通用户资源
            userCaseList= userManager.getUserCaseBySearch(String.valueOf(new LoginRegister().getDoctorId(OneItem.getOneItem().getName())),f1, f2, f3, f4,f5,f6, list2);
//            lv_list(0);
            Const.list=userCaseList;
        }

    }


    //设置日期
    public void setDateDialog(final EditText et) {
        Calendar c = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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
        if (dt1 == null) {
            return 0;
        }
        return dt1.getTime();
    }


    AlertDialog dialog = null;
    public void lv_list(int yeshu){
        list1=new ArrayList<>();
        list1.clear();

        double userSize= Const.list.size();
        temp = userSize / 5.0;
        Const.usersize=Const.list.size();
        if (temp-yeshu >=1||temp-yeshu==temp) {
            if(temp>=1){
                for (int j = yeshu * 5; j < yeshu * 5 +5; j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name)+":" + userCaseList.get(j).getpName() + "  " + getString(R.string.patient_age)+":" + userCaseList.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userCaseList.get(j).getTel();
                    item.setpId(userCaseList.get(j).getpId());
                    item.setpName(zhanshi);
                    list1.add(item);
                }
            }else {
                for (int j = yeshu * 5; j < userCaseList.size(); j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name)+":" + userCaseList.get(j).getpName() + "  " + getString(R.string.patient_age)+":" + userCaseList.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userCaseList.get(j).getTel();
                    item.setpId(userCaseList.get(j).getpId());
                    item.setpName(zhanshi);
                    list1.add(item);
                }
            }

        }else if(temp-yeshu<1){
            for (int j = yeshu * 5; j < userCaseList.size(); j++) {
                Item item = new Item();
                String zhanshi = getString(R.string.patient_name)+":" + userCaseList.get(j).getpName() + "  " + getString(R.string.patient_age)+":" + userCaseList.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userCaseList.get(j).getTel();
                item.setpId(userCaseList.get(j).getpId());
                item.setpName(zhanshi);
                list1.add(item);
            }
        }
        if (list1.size() >=1) {
            lv_additem(list1);
        } else if (list1.size() == 0) {
//            Toast.makeText(getContext(), "请先登记一个患者信息 存在的患者都已经生成PDF了", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void lv_additem(final List<Item>list){
        if (dialog != null) {
            if (dialog.isShowing()) {
                return;
            }
        }
        LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SelectAdapter selectAdapter=new SelectAdapter(this,list);
        listView.setAdapter(selectAdapter);
        selectAdapter.notifyDataSetChanged();
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        dialog=new AlertDialog.Builder(this).setTitle(getString(R.string.image_Select_patients)).setView(linearLayoutMain).setNeutralButton(getString(R.string.button_cancel),null).setPositiveButton(getString(R.string.image_next_page),null).setNegativeButton(getString(R.string.image_Previous_page),null).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button bt_cancle=dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button bt_before=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button bt_next=dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                bt_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        num=0;
                    }
                });
                bt_before.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(temp-num!=temp){
                            if(num>0){
                                num--;
                            }
                            dialog.dismiss();
//                            dialogset=1;
                            lv_list(num);
                        }else {
                            MyToast.showToast(getContext(), getString(R.string.image_patients_first));
//                            Toast.makeText(getContext(), getString(R.string.image_patients_first), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bt_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        num++;
                        if(temp>num){
                            dialog.dismiss();
                            lv_list(num);
                        }else {
                            num--;
                            MyToast.showToast(getContext(), getString(R.string.image_patients_last));
//                            Toast.makeText(getContext(), getString(R.string.image_patients_last), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();

        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                OneItem.getOneItem().setUserId(list.get(arg2).getpId());
                Intent intent = new Intent(JianJiSearchActivity.this,JianjiActivity.class);
                Const.imageshow=-1;
                Const.dialogshow=1;
                startActivity(intent);
                finish();
                dialog.cancel();
            }
        });
    }
    private void lv_add(final List<User>list){
        if (list.size() == 0) {
            return;
        }
        LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        JianJiSearchAdapter selectAdapter=new JianJiSearchAdapter(this,list);
        listView.setAdapter(selectAdapter);
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.image_Select_patients)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                OneItem.getOneItem().setUserId(list.get(arg2).getpId());
                Intent intent = new Intent(JianJiSearchActivity.this,JianjiActivity.class);
                intent.putExtra("msg", 1);
//                OneItem.getOneItem().setTemp(true);
                startActivity(intent);
                finish();
                dialog.cancel();
            }
        });
    }
}
