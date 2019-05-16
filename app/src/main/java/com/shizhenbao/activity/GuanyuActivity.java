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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.fragments.FragSetting;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.OneItem;
import com.util.AlignedTextUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

public class GuanyuActivity extends AppCompatActivity {
    private ImageView iv_weweima;
    private TextView tv_banben_name,tv_banben_model,tv_banben_version,tv_banben_all,tv_banben_name_ancer,tv_banben_model_ancer,tv_banben_version_ancer,tv_banben_all_ancer,tv_app_version_hint;
    private String [] tv_banben;
    private TextView tv_title;
    private Button bt_left,bt_right;
    private EditText et_svn;
    boolean i;
    List<SystemSet>system= DataSupport.findAll(SystemSet.class);
    String versionName;//版本号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guanyu);
        initView();
        init();
        iv_weweima.setImageDrawable((ContextCompat.getDrawable(this, R.drawable.erweima)));
        int code=  getVersionCode(this);

//        tv_banben.setText(getString(R.string.setting_app_name)+"  \n"+getString(R.string.setting_Version_specification_model)+"  \n"+getString(R.string.setting_version)+versionName+"  \n"+getString(R.string.setting_version_all));
    }

    private void init() {
        tv_banben=new String[]{getString(R.string.setting_version_message),getString(R.string.setting_app_name),getString(R.string.setting_Version_specification_model),getString(R.string.setting_version),getString(R.string.setting_version_all)};
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
        TextView[] tvData = {tv_app_version_hint,tv_banben_name, tv_banben_model, tv_banben_version, tv_banben_all};
//        if (screenInches > 6.0) {
        for (int i=0;i<tv_banben.length;i++) {
            if (tvData[i] != null) {
                tvData[i].setText(AlignedTextUtils.justifyString(tv_banben[i], 6));
            }
        }
//        }

    }

    private void initView() {
        //判断用户是否是超级管理员， true 表示是超级管理员，，false 表示是普通医生
        i= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();
        iv_weweima= (ImageView) findViewById(R.id.iv_erweima);
        et_svn= (EditText) findViewById(R.id.et_svn);
        tv_title= (TextView) findViewById(R.id.title_text);
        bt_left= (Button) findViewById(R.id.btn_left);
        bt_right= (Button) findViewById(R.id.btn_right);
        bt_right.setVisibility(View.VISIBLE);
        bt_left.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.setting_about_us));
        bt_right.setText(getString(R.string.patient_save));

        et_svn.setText(system.get(0).getLocal_svn());
        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(i){//判断是否为超级用户
            et_svn.setEnabled(true);
        }else {
            et_svn.setEnabled(false);
        }
        bt_right.setOnClickListener(new View.OnClickListener() {//保存输入的sn号
            @Override
            public void onClick(View view) {
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
            }
        });
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
}
