package com.shizhenbao.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.bumptech.glide.Glide;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.fragments.FragSetting;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.OneItem;
import com.util.AlignedTextUtils;


import org.litepal.LitePal;

import java.util.List;

public class GuanyuActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView iv_weweima,iv_guanwang;
    private TextView tv_banben_name,tv_banben_model,tv_banben_version,tv_banben_all,tv_banben_name_ancer,tv_banben_model_ancer,tv_banben_version_ancer,tv_banben_all_ancer,tv_app_version_hint;
    private String [] tv_banben;
    private TextView tv_title;
    private Button bt_left,bt_right;
    private EditText et_svn;
    boolean i;
    List<SystemSet>system= LitePal.findAll(SystemSet.class);
    String versionName;//版本号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_guanyu);
        initView();
        init();
        initDrawable();
        initClick();
        getVersionCode(this);
    }

    private void initClick() {
        bt_left.setOnClickListener(this);
        bt_right.setOnClickListener(this);
    }

    private void initDrawable() {
        Glide.with(GuanyuActivity.this)
                .load(R.drawable.erweima)
                .into(iv_weweima);
        Glide.with(GuanyuActivity.this)
                .load(R.drawable.guanwnag)
                .into(iv_guanwang);
    }

    private void init() {
        tv_banben=new String[]{getString(R.string.setting_version_message),getString(R.string.setting_app_name),getString(R.string.setting_Version_specification_model),getString(R.string.setting_version),getString(R.string.setting_version_all)};

        TextView[] tvData = {tv_app_version_hint,tv_banben_name, tv_banben_model, tv_banben_version, tv_banben_all};
        for (int i=0;i<tv_banben.length;i++) {
            if (tvData[i] != null) {
                tvData[i].setText(AlignedTextUtils.justifyString(tv_banben[i], 6));
            }
        }
    }

    private void initView() {
        //判断用户是否是超级管理员， true 表示是超级管理员，，false 表示是普通医生
        i= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();
        iv_weweima= (ImageView) findViewById(R.id.iv_erweima);
        iv_guanwang = findViewById(R.id.iv_guanwang);
        et_svn= (EditText) findViewById(R.id.et_svn);
        tv_title= (TextView) findViewById(R.id.title_text);
        bt_left= (Button) findViewById(R.id.btn_left);
        bt_right= (Button) findViewById(R.id.btn_right);
        bt_right.setVisibility(View.VISIBLE);
        bt_left.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.setting_about_us));
        bt_right.setText(getString(R.string.patient_save));
        versionName=getVersionName(this);
        tv_banben_name= (TextView) findViewById(R.id.tv_banben_name);
        tv_app_version_hint= (TextView) findViewById(R.id.tv_app_version_hint);
        TextPaint tp=tv_app_version_hint.getPaint();
        tp.setFakeBoldText(true);
        tv_banben_model= (TextView) findViewById(R.id.tv_banben_model);
        tv_banben_version= (TextView) findViewById(R.id.tv_banben_version);
        tv_banben_all= (TextView) findViewById(R.id.tv_banben_all);
        tv_banben_name_ancer= (TextView) findViewById(R.id.tv_banben_name_ancer);
        tv_banben_name_ancer.setText(getString(R.string.setting_app_name1));
        tv_banben_model_ancer= (TextView) findViewById(R.id.tv_banben_model_ancer);
        tv_banben_model_ancer.setText(getString(R.string.setting_Version_specification_model_ancer));
        tv_banben_version_ancer= (TextView) findViewById(R.id.tv_banben_version_ancer);
        tv_banben_version_ancer.setText(getString(R.string.setting_version_ancer)+versionName);
        tv_banben_all_ancer= (TextView) findViewById(R.id.tv_banben_all_ancer);
        tv_banben_all_ancer.setText(getString(R.string.setting_version_all_ancer));
        et_svn.setText(system.get(0).getLocal_svn());
        if(i){//判断是否为超级用户
            et_svn.setEnabled(true);
        }else {
            et_svn.setEnabled(false);
        }

    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                SystemSet system1 = null;
                if(system.size()==0){//数据为空时创建表
                    system1=new SystemSet();
                    system1.setLocal_svn(et_svn.getText().toString().trim());
                }else {
                    for(int i=0;i<system.size();i++){
                        system1=system.get(0);
                        system1.setLocal_svn(et_svn.getText().toString().trim());
                    }
                }
                system1.save();
                finish();
                break;
                default:break;
        }
    }
}
