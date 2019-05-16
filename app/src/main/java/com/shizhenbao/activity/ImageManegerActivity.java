package com.shizhenbao.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.shizhenbao.UI.CustomConfirmDialog;
import com.shizhenbao.adapter.ImageManagerAdapter;
import com.activity.R;
import com.shizhenbao.fragments.fragmentJianji.NOTYETFragment;
import com.shizhenbao.fragments.fragmentJianji.YETFragment;
import com.shizhenbao.util.Const;

import java.util.ArrayList;
import java.util.List;


public class ImageManegerActivity extends FragmentActivity implements View.OnClickListener{
    private TextView tv_no,tv_yes;
    private ViewPager vp_jianji;
    private List<Fragment> fragment=new ArrayList<>();
    private FragmentPagerAdapter adapter;
    private TextView title_bar;
    private Button bt_left,bt_right;
    private CustomConfirmDialog customConfirmDialog = null;
//    private List<String>list1=new ArrayList<>();//病人照片路径集合
//    private ImageManagerAdapter imaadapter;
//    private int checkIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_image_maneger);
        initView();
        switchStates(0);
        vp_jianji.setCurrentItem(0);
    }

    private void initView() {
        tv_no= (TextView) findViewById(R.id.tv_no);
        tv_yes= (TextView) findViewById(R.id.tv_yes);
        vp_jianji= (ViewPager) findViewById(R.id.vp_jianji);
        title_bar= (TextView) findViewById(R.id.title_text);
        bt_left= (Button) findViewById(R.id.btn_left);
        bt_right= (Button) findViewById(R.id.btn_right);
        bt_left.setVisibility(View.VISIBLE);
        title_bar.setText(getString(R.string.setting_picture_manage)+"("+Const.nameJianJi+")");
        bt_right.setVisibility(View.VISIBLE);
        bt_right.setText(getString(R.string.patient_show_delete));
        fragment.add(new NOTYETFragment());
        fragment.add(new YETFragment());
        initPager(fragment);
        bt_right.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        tv_no.setOnClickListener(this);
        tv_yes.setOnClickListener(this);
        customConfirmDialog = new CustomConfirmDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }

    private void initPager(final List<Fragment> fragment){
        adapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragment.get(position);
            }

            @Override
            public int getCount() {
                return fragment.size();
            }
        };
        vp_jianji.setAdapter(adapter);
        vp_jianji.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchStates(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    //判断选中了哪一个
    private void switchStates(int state) {
        tv_no.setTextColor(Color.BLACK);
        tv_yes.setTextColor(Color.BLACK);
        tv_yes.setTextSize(18);
        tv_no.setTextSize(18);
        switch (state) {
            case 0:
               tv_no.setTextColor(getResources().getColor(R.color.textcase));
                tv_no.setTextSize(20);
                break;
            case 1:
                tv_yes.setTextColor(getResources().getColor(R.color.textcase));
                tv_yes.setTextSize(20);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_left:
                Intent intent=new Intent(ImageManegerActivity.this,JianjiActivity.class);
                Const.dialogshow=0;
                startActivity(intent);
                finish();
                break;
            case R.id.tv_no:
                switchStates(0);
                vp_jianji.setCurrentItem(0);
                break;
            case R.id.tv_yes:
                switchStates(1);
                vp_jianji.setCurrentItem(1);
                break;
            case R.id.btn_right://删除按钮
                customConfirmDialog.show(getString(R.string.image_manage_delete_title),getString(R.string.image_manage_delete_ok),getString(R.string.image_manage_delete_no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//确定删除

                        try {
                            if (Const.isVisible == 1) {
                                NOTYETFragment f=(NOTYETFragment)(fragment.get(0));
                                f.deleteImage();
                            } else if (Const.isVisible == 2) {
                                YETFragment yf = (YETFragment) fragment.get(1);
                                yf.deleteImage();
                            }
                            customConfirmDialog.dimessDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//不删除
                        customConfirmDialog.dimessDialog();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
