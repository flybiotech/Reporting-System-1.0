package com.shizhenbao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.itextpdf.text.DocumentException;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.adapter.SelectAdapter1;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SwipeRefreshView;


import org.litepal.crud.DataSupport;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DengjiYulanActivity extends AppCompatActivity {
    private ListView lv_dengjiyulan;
//    private List<String>list;
//    List<User> list1 = null;//查询该医生所有的病人信息
    private User user=null;
    private TextView tv_title,tv_empty;
    private Button bt_left,bt_right;
    List<User>list2= DataSupport.where("is_diag=?","2").find(User.class);//查询已生成报告的报告
    boolean isAdmin=true;//判断是否为超级用户
    private static List<Item> list;//正序数据源
    private static List<Item> list1;//倒序数据源
    private List<Item> list_show=new ArrayList<>();//展示数据源
    private List<User> userlist=null;
    private SwipeRefreshView mSwipeRefreshView;
    private static int page1;
    private static int page;
    private SelectAdapter1 selectAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dengji_yulan);
        initView();
        Intent intent=getIntent();
        isAdmin= intent.getBooleanExtra("msg",false);//为真时为超级用户，为false时为普通用户
        //查询该医生所有的病人信息
//        list1= DataSupport.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);

        if(isAdmin){
            initDateSUP();//超级用户数据源
        }else {
           initDate();//普通用户数据源
        }
        initClick();
    }

    private void initClick() {
        try {
            lv_dengjiyulan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    try {
                        if (list2.size() == 0) {
                            return;
                        }

                        Intent i = new Intent(DengjiYulanActivity.this, ZhenDuanXiugaiActivity.class);//跳转到修改诊断信息的页面
                        i.putExtra("msg", list_show.get(position).getpId());//将要修改的病人id传递到下个页面
                        startActivity(i);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){

        }
    }
  
    private void initDate() {//普通用户数据源
        list=new ArrayList<>();
        list1=new ArrayList<>();
        userlist=DataSupport.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
        for(int j=0;j<userlist.size();j++){
            if(userlist.get(j).getIs_diag()==2){
                Item item = new Item();
//                String zhanshi = getString(R.string.patient_name)+":" + userlist.get(j).getpName() + "  " +getString(R.string.patient_age)+ ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userlist.get(j).getTel();
                item.setpId(userlist.get(j).getpId());
                item.setpName(userlist.get(j).getpName());
                item.setAge(userlist.get(j).getAge());
                item.setTel(userlist.get(j).getTel());
                item.setTv_pResouce(userlist.get(j).getpSource());
                item.setTv_pMarray(userlist.get(j).getMarry());
                list.add(item);
            }
        }
        for(int i=list.size()-1;i>=0;i--){
            list1.add(list.get(i));
        }
        selectAdapter=new SelectAdapter1(this,list_show);
        lv_dengjiyulan.setAdapter(selectAdapter);
        lv_dengjiyulan.setEmptyView(tv_empty);
        new Item().setListViewHeightBasedOnChildren(lv_dengjiyulan);//重新计算listview每个item的高度
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
    }
    private void initDateSUP() {//超级用户数据源
        list=new ArrayList<>();
        list1=new ArrayList<>();
        userlist=DataSupport.where("is_diag=?","2").find(User.class);
        for (int j = 0; j < userlist.size(); j++) {
            Item item = new Item();
            item.setpId(userlist.get(j).getpId());
            item.setpName(userlist.get(j).getpName());
            item.setAge(userlist.get(j).getAge());
            item.setTel(userlist.get(j).getTel());
            item.setTv_pResouce(userlist.get(j).getpSource());
            item.setTv_pMarray(userlist.get(j).getMarry());
            list.add(item);
        }
        for(int i=list.size()-1;i>=0;i--){
            list1.add(list.get(i));
        }
        selectAdapter=new SelectAdapter1(this,list_show);
        lv_dengjiyulan.setAdapter(selectAdapter);
        new Item().setListViewHeightBasedOnChildren(lv_dengjiyulan);//重新计算listview每个item的高度
        lv_dengjiyulan.setEmptyView(tv_empty);
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
                list_show.clear();
                list_show.addAll(DataResource.getMoreData());
                selectAdapter.notifyDataSetChanged();
                // 加载完数据设置为不加载状态，将加载进度收起来
                mSwipeRefreshView.setLoading(false);
            }
        }, 0);
    }


    private void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list_show.clear();
                list_show.addAll(DataResource.getData());
                selectAdapter.notifyDataSetChanged();
                // 加载完数据设置为不刷新状态，将下拉进度收起来
                if (mSwipeRefreshView.isRefreshing()) {
                    mSwipeRefreshView.setRefreshing(false);
                }
            }
        }, 0);
    }

    public static class DataResource {
        private static List<Item> datas = new ArrayList<>();
        private static List<Item>data_new=new ArrayList<>();
        public static List<Item> getData() {
            datas.clear();
            data_new.clear();
            if(list1.size()>=10 * (page + 1)){
                for (int i = 10 * page; i < 10 * (page + 1); i++) {
                    datas.add(list1.get(i));
                }
            }else if(list1.size()>=10*page&&list1.size()<10*(page+1)){
                for (int i = 10 * page; i <list1.size(); i++) {
                    datas.add(list1.get(i));
                }
            }
            page=page+1;
            return datas;
        }

        public static List<Item> getMoreData() {
            page1 = page1 + 1;
            data_new.clear();
            if(list1.size()>=10 * (page1 + 1)){
                for (int i = 10 * page1; i < 10 * (page1 + 1); i++) {
                    datas.add(list1.get(i));
                }
            }else if(list1.size()>=10*page1&&list1.size()<10*(page1+1)){
                for (int i = 10 * page1; i <list1.size(); i++) {
                    datas.add(list1.get(i));
                }
            }

            return datas;
        }
    }


    private void initView() {
        mSwipeRefreshView= (SwipeRefreshView) findViewById(R.id.srv);
        mSwipeRefreshView.setEnabled(false);
        lv_dengjiyulan= (ListView) findViewById(R.id.lv_dengjiyulan);//listview登记预览
        tv_title= (TextView) findViewById(R.id.title_text);//页面标题
        tv_empty= (TextView) findViewById(R.id.tv_empty);//listview 为空时显示这个view
        bt_left= (Button) findViewById(R.id.btn_left);//
        bt_right= (Button) findViewById(R.id.btn_right);
        bt_right.setVisibility(View.INVISIBLE);//设置为不可见
        bt_left.setVisibility(View.VISIBLE);//设置为可见
        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title.setText(getString(R.string.print_Report_updata));
        page=0;
        page1=0;
    }
}
