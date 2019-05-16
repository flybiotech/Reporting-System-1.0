package com.shizhenbao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.UI.CustomConfirmDialog;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.adapter.SelectAdapter1;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.fragments.fragmentJianji.NOTYETFragment;
import com.shizhenbao.fragments.fragmentJianji.YETFragment;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SwipeRefreshView;
import com.view.MyToast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity {
    private ListView lv_select;
    private static List<Item>list1=new ArrayList<>();
    private TextView tv,tv_empty;
    private Button bt_right,bt_left;
    private SwipeRefreshView mSwipeRefreshView;
    private static int page;
    private static int page1;
    List<User>listAllUser=new ArrayList<>();//查询数据库中所有不为空的User，展示到listview
//    private LoginRegister loginRegister;//登陆注册，获取医生相关信息的类
    private UserManager userManager;
//    private List<String>stringList=new ArrayList<>();
    private CustomConfirmDialog customConfirmDialog = null;
    SelectAdapter1 adapter=null;
    private List<Item>itemList=new ArrayList<>();//翻页展示数据源
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_select_layout);
        userManager = new UserManager(SelectActivity.this);
            //从搜索界面获取到的患者信息
            list1 = userManager.getUserDate();
            init();
            adapter=new SelectAdapter1(SelectActivity.this,itemList);
            lv_select.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            MyToast.dissmissToast();
            new Item().setListViewHeightBasedOnChildren(lv_select);//重新计算listview每个item的高度
            lv_select.setEmptyView(tv_empty);//当listview 没有数据时，就显示这个view
            bt_left.setOnClickListener(new View.OnClickListener() {//点击返回
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 不能在onCreate中设置，这个表示当前是刷新状态，如果一进来就是刷新状态，SwipeRefreshLayout会屏蔽掉下拉事件
        //swipeRefreshLayout.setRefreshing(true);

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
        //对查询出来的数据进行操作的方法
        initClick();
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
                itemList.clear();
                itemList.addAll(DataResource.getMoreData());
                adapter.notifyDataSetChanged();
                // 加载完数据设置为不加载状态，将加载进度收起来
                mSwipeRefreshView.setLoading(false);
            }
        }, 2000);
    }


    private void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                itemList.clear();
                itemList.addAll(DataResource.getData());
                adapter.notifyDataSetChanged();
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

    private void init() {
        page=0;
        page1=0;
        mSwipeRefreshView= (SwipeRefreshView) findViewById(R.id.srv);
        mSwipeRefreshView.setEnabled(false);
        lv_select= (ListView) findViewById(R.id.listView_selectActivity);
        //菜单选项
        tv= (TextView) findViewById(R.id.title_text);//title
        tv_empty= (TextView) findViewById(R.id.tv_empty);//title
        bt_left= (Button) findViewById(R.id.btn_left);//左边按钮
        bt_right= (Button) findViewById(R.id.btn_right);//右边的按钮
        bt_right.setVisibility(View.GONE);
        bt_right.setText(R.string.patient_show_delete_all);
        bt_right.setVisibility(View.VISIBLE);
        bt_left.setVisibility(View.VISIBLE);//设置返回按钮为可视
        tv.setText(R.string.patient_show_title);//改变页面标题
        customConfirmDialog = new CustomConfirmDialog(this);
        //UserManager类中有一个方法，当调用这个方法时，会将查询出来的患者的信息保存在OneItem中
        for(int i=0;i<list1.size();i++){
            listAllUser.add(new LoginRegister().getUserName(Integer.parseInt(list1.get(i).getpId())));
        }
        bt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               deleteAll();
            }
        });

    }
    private void deleteAll(){
        customConfirmDialog.show(getString(R.string.patient_show_delete_all_prompt), new View.OnClickListener() {
            @Override
            public void onClick(View v) {//确定删除
                if(OneItem.getOneItem().getName()!=null&&!OneItem.getOneItem().getName().equals("")){
                    Doctor doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
                    if(!doctor.isdAdmin()){
                        LitePal.deleteAll(User.class,"operId=? and is_diag=?",doctor.getdId()+"",1+"");//查询所有符合条件的数据
                    }else {
                        LitePal.deleteAll(User.class,"is_diag=?",1+"");
                    }
                    list1.clear();//删除所选项
                    SelectAdapter adapter=new SelectAdapter(SelectActivity.this,list1);
                    adapter.notifyDataSetChanged();//适配器刷新
                    lv_select.setAdapter(adapter);
                    new Item().setListViewHeightBasedOnChildren(lv_select);//重新计算listview每个item的高度
                }
                customConfirmDialog.dimessDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//不删除
                customConfirmDialog.dimessDialog();
            }
        });
    }
    private void initClick() {
        lv_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                final CharSequence[] items = {getString(R.string.patient_show_updata), getString(R.string.patient_show_delete),getString(R.string.patient_show_get_image)};//弹出框展示内容
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectActivity.this);//声明弹出框
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item){
                            case 0://查询修改
                                int  b;
                                b=listAllUser.get(position).getIs_diag();//判断bResult是否为true，若为true已经生成报告，false则未生成报告

                                if (b==2){
                                    dialog.dismiss();
                                    MyToast.showToast(SelectActivity.this, getString(R.string.patient_show_updata_error));
//                                    Toast.makeText(SelectActivity.this, R.string.patient_show_updata_error, Toast.LENGTH_SHORT).show();
                                }else {
                                    Intent intent=new Intent(SelectActivity.this,ModifyActivity.class);//声明Intent
                                    intent.putExtra("second",list1.get(position).getpId());//intent传递listview中item的数据
                                    intent.putExtra("b",b);
                                    startActivity(intent);//页面跳转
                                    finish();
                                }
                                break;
                            case 1://删除所选项
                                customConfirmDialog.show(getString(R.string.patient_show_delete_sure), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {//确定删除
                                        int c;
                                        c=listAllUser.get(position).getIs_diag();//判断bResult是否为true，若为true已经生成报告，false则未生成报告
                                        if(c==1){
                                            LitePal.deleteAll(User.class,"pId=?",list1.get(position).getpId());//查询所有符合条件的数据
                                            list1.remove(position);//删除所选项
                                            SelectAdapter1 adapter=new SelectAdapter1(SelectActivity.this,list1);
                                            adapter.notifyDataSetChanged();//适配器刷新
                                            lv_select.setAdapter(adapter);
                                            new Item().setListViewHeightBasedOnChildren(lv_select);//重新计算listview每个item的高度
                                        }
                                        customConfirmDialog.dimessDialog();
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {//不删除
                                        customConfirmDialog.dimessDialog();
                                    }
                                });
                                break;
                            case 2:
                                int d;
                                d=listAllUser.get(position).getIs_diag();
                                if(d==1){
                                    OneItem.getOneItem().setIntImage(0);
                                    OneItem.getOneItem().setImageId(Integer.parseInt(list1.get(position).getpId()));
                                    Intent intent=new Intent(SelectActivity.this,MainActivity.class);
                                    intent.putExtra("canshu",1);
                                    startActivity(intent);
                                    finish();
                                }
                                break;
                            default:
                                break;

                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
