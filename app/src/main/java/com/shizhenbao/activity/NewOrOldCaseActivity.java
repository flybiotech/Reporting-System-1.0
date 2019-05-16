package com.shizhenbao.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.fragments.fragmentcase.ImageFragment;
import com.shizhenbao.fragments.fragmentcase.PingJiaFragment;
import com.shizhenbao.fragments.fragmentcase.ZhenDuanFragment;
import com.shizhenbao.pop.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewOrOldCaseActivity extends FragmentActivity implements View.OnClickListener{

    private List<Fragment> fragList = new ArrayList<>();
    private ViewPager pager;
    private ImageFragment imageFrag;//显示新旧图片的fragment
    private ZhenDuanFragment zdFrag;//显示新旧诊断信息的fragment
    private PingJiaFragment pjFrag;//显示新旧评价的fragment
    private FragmentPagerAdapter adapter;
//    private List<User> listUser = new ArrayList<User>();
    private TextView tv01,tv02, tv03;
    private Button btn_left;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_neworold_layout);

        initView();
        getPagerList(fragList);
        pager.setCurrentItem(0);
        switchStates(0);

    }


    private void initView() {
        tv01 = (TextView) findViewById(R.id.tv_nor01); //图片比较
        tv02 = (TextView) findViewById(R.id.tv_nor02); //诊断结果比较
        tv03 = (TextView) findViewById(R.id.tv_nor03); //评价结果比较

        tv01.setOnClickListener(this);
        tv02.setOnClickListener(this);
        tv03.setOnClickListener(this);

//        title = (TextView) findViewById(R.id.title_text); //title
        btn_left = (Button) findViewById(R.id.btn_left01);
        btn_left.setOnClickListener(this);

        pager = (ViewPager) findViewById(R.id.nooc_viewpager);
        imageFrag = new ImageFragment();
        zdFrag = new ZhenDuanFragment();
        pjFrag = new PingJiaFragment();
        fragList.add(imageFrag);
        fragList.add(zdFrag);
        fragList.add(pjFrag);
    }


    private void getPagerList(final List<Fragment> list) {
        adapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        };
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchStates(position);
//                Logger.e("ViewPager 当前的位置 : "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    //判断选中了哪一个
    private void switchStates(int state) {
        tv01.setTextColor(Color.BLACK);
        tv02.setTextColor(Color.BLACK);
        tv03.setTextColor(Color.BLACK);
        tv01.setTextSize(18);
        tv02.setTextSize(18);
        tv03.setTextSize(18);

        switch (state) {
            case 0:
                tv01.setTextColor(getResources().getColor(R.color.textcase));
                tv01.setTextSize(20);
                break;

            case 1:
                tv02.setTextColor(getResources().getColor(R.color.textcase));
                tv02.setTextSize(20);
                break;

            case 2:
                tv03.setTextColor(getResources().getColor(R.color.textcase));
                tv03.setTextSize(20);
                break;
                default:
                    break;


        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left01:
                finish();
                break;
            case R.id.tv_nor01://图片比较
                switchStates (0);
                pager.setCurrentItem(0);

                break;

            case R.id.tv_nor02://诊断比较
                switchStates (1);
                pager.setCurrentItem(1);
                break;

            case R.id.tv_nor03://评价比较
                switchStates (2);
                pager.setCurrentItem(2);
                break;
                default:
                    break;

        }
    }



}
