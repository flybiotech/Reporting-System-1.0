package com.shizhenbao.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.activity.base.BaseActivity;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.fragments.FragPrinter;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SwipeRefreshView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class XuanzePDFActivity extends BaseActivity {
    private ListView lv;
    private static List<String> list=new ArrayList<>();
    private String path = new Item().getSD()+"/SZB_save/"+ OneItem.getOneItem().getName();
    private TextView tv,tv_empty;
    private Button bt_right, bt_left;
    private List<User>  user;
    private Doctor doctor;
    private List<String>userlist=new ArrayList<>();
    private static List<String>userlistPdfName=new ArrayList<>();//倒序数据源
    private static List<String>userlistPdfName1=new ArrayList<>();//正序数据源
    private File[] files;
    boolean i;
    private List<File>pathList;
    private SwipeRefreshView mSwipeRefreshView;
    private static int page1;
    private static int page;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xuanze_pdf);
        try{
            if(OneItem.getOneItem().getName()!=null||!OneItem.getOneItem().getName().equals("")){
                i= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();
            }else {
                new UserManager().getExceName();
                i= new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();
            }
        }catch (Exception e){
            new UserManager().getExceName();
        }
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            userlistPdfName1.clear();
            userlistPdfName.clear();
            list.clear();
            boolean f = hasPermission(CreateFileConstant.WRITE_READ_EXTERNAL_PERMISSION);
            if (f) {
            } else {
                requestPermission(CreateFileConstant.WRITE_READ_EXTERNAL_CODE, CreateFileConstant.WRITE_READ_EXTERNAL_PERMISSION);
            }
            doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
            if(doctor.isdAdmin()){//超级用户查询数据库所有的数据
                user=DataSupport.findAll(User.class);
            }else {
                user= DataSupport.where("operId=?",String.valueOf(doctor.getdId())).find(User.class);//普通用户查询自己名下的数据库
            }
            for(User u:user){
                if (u.getPdfPath() != null&&!u.getPdfPath().equals("")) {//保证程序的健壮性 ,有时候u的值会为空。必须要排除 u=null 的情况
                    String[] userPathName = u.getPdfPath().split("/");
                    Log.d("TAG1", " u.getPdfPath():----- "+u.getPdfPath());
                    if(new FragPrinter().getFileSize(new File(u.getPdfPath()))!=0){
                        userlist.add(u.getPdfPath());
                    }
                    Log.e("报告路径",u.getPdfPath());
                    Log.e("报告数组长度",userPathName.length+"");
                    if (userPathName.length >= 3) {//保证程序的健壮性
                        userlistPdfName1.add(userPathName[userPathName.length-1]);
                    }
                }
            }
            for(int j=userlistPdfName1.size()-1;j>=0;j--){
                userlistPdfName.add(userlistPdfName1.get(j));
            }
            lv.setEmptyView(tv_empty);
            adapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1,list);
            lv.setAdapter(adapter);
            new Item().setListViewHeightBasedOnChildren(lv);

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
            initClick();
        }catch (Exception e){

        }
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
                list.clear();
                list.addAll(DataResource.getMoreData());
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
                list.clear();
                list.addAll(DataResource.getData());
                adapter.notifyDataSetChanged();
                // 加载完数据设置为不刷新状态，将下拉进度收起来
                if (mSwipeRefreshView.isRefreshing()) {
                    mSwipeRefreshView.setRefreshing(false);
                }
            }
        }, 0);
    }

    public static class DataResource {
        private static List<String> datas = new ArrayList<>();
        private static List<String>data_new=new ArrayList<>();
        public static List<String> getData() {
            datas.clear();
            data_new.clear();
            if(userlistPdfName.size()>=10 * (page + 1)){
                for (int i = 10 * page; i < 10 * (page + 1); i++) {
                    datas.add(userlistPdfName.get(i));
                }
            }else if(userlistPdfName.size()>=10*page&&userlistPdfName.size()<10*(page+1)){
                for (int i = 10 * page; i <userlistPdfName.size(); i++) {
                    datas.add(userlistPdfName.get(i));
                }
            }
//            page=page+1;
            return datas;
        }

        public static List<String> getMoreData() {
            page1 = page1 + 1;
            data_new.clear();
            if(userlistPdfName.size()>=10 * (page1 + 1)){
                for (int i = 10 * page1; i < 10 * (page1 + 1); i++) {
                    datas.add(userlistPdfName.get(i));
                }
            }else if(userlistPdfName.size()>=10*page1&&userlistPdfName.size()<10*(page1+1)){
                for (int i = 10 * page1; i <userlistPdfName.size(); i++) {
                    datas.add(userlistPdfName.get(i));
                }
            }

            return datas;
        }
    }
    private void initClick() {

        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path=userlistPdfName.get(position).toString();
                int p=path.lastIndexOf("-");
                int o=path.lastIndexOf("_");
//                Toast.makeText(XuanzePDFActivity.this, "a:"+p+"b:"+o, Toast.LENGTH_SHORT).show();
                String pin=path.substring(p+1,o);
//                Toast.makeText(XuanzePDFActivity.this, "pin:"+pin, Toast.LENGTH_SHORT).show();
                List<User> u=DataSupport.where("pId=?",pin).find(User.class);
                for(int i=0;i<u.size();i++){
                    initPDFPreview(u.get(i).getPdfPath());
                }
            }
        });
    }

    private void initView() {
//        userlistPdfName.clear();
        mSwipeRefreshView= (SwipeRefreshView) findViewById(R.id.srv);
        mSwipeRefreshView.setEnabled(false);
        lv = (ListView) findViewById(R.id.lv);
        tv_empty = (TextView) findViewById(R.id.tv_empty);//listview 为空时显示这个view
        tv = (TextView) findViewById(R.id.title_text);
        bt_right = (Button) findViewById(R.id.btn_right);
        bt_left = (Button) findViewById(R.id.btn_left);
        bt_left.setVisibility(View.VISIBLE);
        bt_right.setVisibility(View.GONE);
        tv.setText(getString(R.string.print_Report_Preview));
//        list = new ArrayList<>();
        page1=0;
        page=0;
    }

    //调用自带阅读器
    private void initPDFPreview(String path1) {
        if (path1.equals("")||path1==null) {
            Toast.makeText(this, R.string.print_Report_Non_existent, Toast.LENGTH_SHORT).show();
            return;
        }
        OneItem.getOneItem().setFile(new File(path1));
        Intent intent = new Intent(XuanzePDFActivity.this, PDFPreviewActivity.class);
        intent.putExtra("msg", path1);
        startActivity(intent);
        finish();
    }

    @Override  //处理申请完权限之后，的事情
    public void doSDCardPermission() {
    }

}
