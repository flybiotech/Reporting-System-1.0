package com.shizhenbao.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.adapter.CaseManagerAdapter;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.SwipeRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CaseListManagerActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvName,tvTel, tvCheckTime;
    private TextView title,tvContrast,tv_empty;//标题
    private ListView listView;
    private Button btn_left,btn_right;
    private CaseManagerAdapter adapter;
    static ArrayList<User> listCase = new ArrayList<User>();
    static ArrayList<User> listCase1 = new ArrayList<User>();
    ArrayList<User> listSelect = new ArrayList<User>();
    private SwipeRefreshView mSwipeRefreshView;
    private static int page;
    private static int page1;
    private List<User>userList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_list_manager);
        initView();
        showListView();
    }

    private void initView() {
        listCase1 =(ArrayList<User> ) getIntent().getSerializableExtra("listCase");
        listCase.clear();
//        Logger.e( "人数：" + listCase.size());
        try{
            for(int i=listCase1.size()-1;i>=0;i--){
                listCase.add(listCase1.get(i));
            }
        }catch (Exception e){
            Log.e("图片异常",e.getMessage());
        }

        mSwipeRefreshView= (SwipeRefreshView) findViewById(R.id.srv);
        mSwipeRefreshView.setEnabled(false);
        tvName = (TextView) findViewById(R.id.tv_caselist_name);//姓名
        tvTel = (TextView) findViewById(R.id.tv_caselist_tel);//电话号码
        tv_empty = (TextView) findViewById(R.id.tv_empty);//当listvie为空时，暂无数据
        tvCheckTime = (TextView) findViewById(R.id.tv_caselist_checktime);//检查时间
        tvContrast = (TextView) findViewById(R.id.textview_contrast);//两次结果对比
//        tvContrast.setText("两次结果比对     ");
        listView = (ListView) findViewById(R.id.list_caselist_01);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.INVISIBLE);
        btn_left=(Button) findViewById(R.id.btn_left);
        btn_left.setVisibility(View.VISIBLE);
        btn_left.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title_text);
        title.setText(getString(R.string.case_patient_list));
        page=0;
        page1=0;
    }

    private List<Boolean> ischeck = new ArrayList<>();
    private void showListView() {
        ischeck.clear();
        if (listCase.size() > 0) {
            for (int i = 0; i < listCase.size(); i++) {
                ischeck.add(false);
            }
        } else {
            ischeck.add(false);
        }
        adapter = new CaseManagerAdapter(this,userList,ischeck);
        listView.setAdapter(adapter);
        listView.setEmptyView(tv_empty);
        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        mSwipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        mSwipeRefreshView.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_blue_bright, R.color.colorPrimaryDark,
                android.R.color.holo_orange_dark, android.R.color.holo_red_dark, android.R.color.holo_purple);

        mSwipeRefreshView.setItemCount(10);

        // 手动调用,通知系统去测量
        mSwipeRefreshView.measure(0, 0);
        mSwipeRefreshView.setRefreshing(true);
        initEvent();
        initData();
//        listView.setEmptyView();
//        adapter.setOnSwitchClickListner(this);
    }
    private void initEvent() {

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });


        // 设置下拉加载更多
        mSwipeRefreshView.setOnLoadMoreListener(new SwipeRefreshView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
    }

    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                userList.clear();
                userList.addAll(DataResource.getMoreData());
//                showListView();
                adapter.notifyDataSetChanged();
                // 加载完数据设置为不加载状态，将加载进度收起来
                mSwipeRefreshView.setLoading(false);
            }
        }, 0);
    }


    private void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                userList.clear();
                userList.addAll(DataResource.getData());
                adapter.notifyDataSetChanged();
                // 加载完数据设置为不刷新状态，将下拉进度收起来
                if (mSwipeRefreshView.isRefreshing()) {
                    mSwipeRefreshView.setRefreshing(false);
                }
            }
        }, 0);
    }

    public static class DataResource {
        private static List<User> datas = new ArrayList<>();
        private static List<User>data_new=new ArrayList<>();
        public static List<User> getData() {
            datas.clear();
            data_new.clear();
            if(listCase.size()>=10 * (page + 1)){
                for (int i = 10 * page; i < 10 * (page + 1); i++) {
                    datas.add(listCase.get(i));
                }
            }else if(listCase.size()>=10*page&&listCase.size()<10*(page+1)){
                for (int i = 10 * page; i <listCase.size(); i++) {
                    datas.add(listCase.get(i));
                }
            }

            page=page+1;
            return datas;
        }

        public static List<User> getMoreData() {
            page1 = page1 + 1;
            data_new.clear();
            if(listCase.size()>=10 * (page1 + 1)){
                for (int i = 10 * page1; i < 10 * (page1 + 1); i++) {
                    datas.add(listCase.get(i));
                }
            }else if(listCase.size()>=10*page1&&listCase.size()<10*(page1+1)){
                for (int i = 10 * page1; i <listCase.size(); i++) {
                    datas.add(listCase.get(i));
                }
            }

            return datas;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (listCase != null) {
            if (listCase.size() > 0) {
                tvContrast.setText(getString(R.string.case_patient_contrast_tip));
            } else {
                tvContrast.setText(getString(R.string.case_patient_nothing));
                tvContrast.setText("");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right://标题栏右边的按钮

                break;
            case R.id.btn_left://标题栏左边的按钮
                finish();
                break;
        }
    }


}
