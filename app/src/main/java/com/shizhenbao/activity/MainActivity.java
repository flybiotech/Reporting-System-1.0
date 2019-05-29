package com.shizhenbao.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.fragments.FragCaseManager;
import com.shizhenbao.fragments.FragGetImage;
import com.shizhenbao.fragments.FragPatient;
import com.shizhenbao.fragments.FragPrinter;
import com.shizhenbao.fragments.FragSetting;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.CopypPicturesUtils;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.LogUtil;
import com.shizhenbao.util.OneItem;
import com.view.MyToast;


import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private TextView patientInfo,getImageInfo,printerInfo,casemanager,settingInfo;
//    private FragmentManager manager;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragList = new ArrayList<Fragment>();
    private int mState=-1;
    private static String TAG = "TAG_MainActivity";
    //再按一次退出程序
    private long exitTime = 0;

    List<Doctor> doctorList;
    private CopypPicturesUtils copypPicturesUtils;//图片复制工具
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_home_layout);
        doctorList= LitePal.findAll(Doctor.class);
        if(new Item().system()){
            SystemSet s=new LoginRegister().getSystem();
            OneItem.getOneItem().setHospital_name(s.getHospital_name());
            OneItem.getOneItem().setGather_path(s.getGather_path());
            OneItem.getOneItem().setPrinter_wifi_user(s.getPrinter_wifi_user());
            OneItem.getOneItem().setPrinter_wifi_pwd(s.getPrinter_wifi_pwd());
            OneItem.getOneItem().setSzb_wifi_user(s.getSzb_wifi_user());
            OneItem.getOneItem().setSzb_wifi_pwd(s.getSzb_wifi_pwd());
        }else {
            MyToast.showToast(this, getString(R.string.setting_hospital_name));
//            Toast.makeText(this, getString(R.string.setting_hospital_name), Toast.LENGTH_SHORT).show();
        }
        init();
        getPagerAdapters(mFragList);
        getIntentData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntentData();
    }
    private void getIntentData(){
        Intent intent=getIntent();
        int canshu=  intent.getIntExtra("canshu",0);//默认为登记页面
        if(canshu==0){
            switchState(0);//跳到患者信息fragment
            mViewPager.setCurrentItem(0);
        }else if(canshu==3){
            switchState(3);//跳到系统中心fragment
            mViewPager.setCurrentItem(3);
        }else if(canshu==2){//调到打印报告fragment
            switchState(2);
            mViewPager.setCurrentItem(2);
        } else if (canshu == 1) {//跳到图像获取界面/
            switchState(1);
            mViewPager.setCurrentItem(1);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();

        copypPicturesUtils.initCopyPicture();
    }
    private void setKeyguardEnable(boolean enable) {
        //disable
        if (!enable) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            return;
        }
        //enable
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }
    private void init() {
        copypPicturesUtils = new CopypPicturesUtils(this);
        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);
        patientInfo = (TextView) findViewById(R.id.textView_home_patientInfo);//患者信息
        getImageInfo = (TextView) findViewById(R.id.textView_home_getImageInfo);//获取图片信息
        printerInfo = (TextView) findViewById(R.id.textView_home_printerInfo);//打印报告
        casemanager = (TextView) findViewById(R.id.textView_home_casemanagerInfo);//病例管理
        settingInfo = (TextView) findViewById(R.id.textView_home_settingInfo);//系统设置
        patientInfo.setOnClickListener(this);
        getImageInfo.setOnClickListener(this);
        printerInfo.setOnClickListener(this);
        casemanager.setOnClickListener(this);
        settingInfo.setOnClickListener(this);
        mFragList.clear();
        FragPatient frag1 = new FragPatient();
        FragGetImage frag2 = new FragGetImage();
        FragPrinter frag3 = new FragPrinter();
        FragCaseManager frag4 = new FragCaseManager();
        FragSetting frag5 = new FragSetting();
        mFragList.add(frag1);
        mFragList.add(frag2);
        mFragList.add(frag3);
        mFragList.add(frag4);
        mFragList.add(frag5);
    }

    private void getPagerAdapters(final List<Fragment>list) {
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return list.size();
            }
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_home_patientInfo:
                switchState(0);
                mViewPager.setCurrentItem(0);
                break;

            case R.id.textView_home_getImageInfo:
                switchState(1);
                mViewPager.setCurrentItem(1);
                break;

            case R.id.textView_home_printerInfo:
                switchState(2);
                mViewPager.setCurrentItem(2);
                break;

            case R.id.textView_home_casemanagerInfo:
                switchState(3);
                mViewPager.setCurrentItem(3);
                break;

            case R.id.textView_home_settingInfo:
                switchState(4);
                mViewPager.setCurrentItem(4);
                break;
            default:
                break;
        }

    }

    //判断选中了哪个Fragment
    private void switchState(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        patientInfo.setTextColor(Color.BLACK);
        getImageInfo.setTextColor(Color.BLACK);
        printerInfo.setTextColor(Color.BLACK);
        casemanager.setTextColor(Color.BLACK);
        settingInfo.setTextColor(Color.BLACK);

        patientInfo.setSelected(false);
        getImageInfo.setSelected(false);
        printerInfo.setSelected(false);
        casemanager.setSelected(false);
        settingInfo.setSelected(false);

//        Const.wifiMark = true;//标记wifi广播的发送


        switch (state) {
            case 0:
                patientInfo.setTextColor(R.color.lightRed);
                patientInfo.setSelected(true);
                //判断是不是从其他三个fragment 跳到 FragGetImage的
                Const.isIntent = true;
                OneItem.getOneItem().setViewPagerCount(0); //记录当前 是第几个 fragment
                break;
            case 1:
                getImageInfo.setTextColor(R.color.lightRed);
                getImageInfo.setSelected(true);
                break;
            case 2:
                printerInfo.setTextColor(R.color.lightRed);
                printerInfo.setSelected(true);
                Const.isIntent = true;
                OneItem.getOneItem().setViewPagerCount(2); //记录当前 是第几个 fragment
                break;
            case 3:
                casemanager.setTextColor(R.color.lightRed);
                casemanager.setSelected(true);
                Const.isIntent = true;
                OneItem.getOneItem().setViewPagerCount(3); //记录当前 是第几个 fragment
                break;

            case 4:
                settingInfo.setTextColor(R.color.lightRed);
                settingInfo.setSelected(true);
                Const.isIntent = true;
                OneItem.getOneItem().setViewPagerCount(4); //记录当前 是第几个 fragment
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

                if ((System.currentTimeMillis() - exitTime) > 1000) {
                    MyToast.showToast(this, getString(R.string.quit_program));
//                    Toast.makeText(this, R.string.quit_program, Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;

        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Const.dialogWifiSetting = true;
    }
}
