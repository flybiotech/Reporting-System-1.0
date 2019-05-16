package com.shizhenbao.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.OneItem;
import com.view.MyToast;


import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class GuanliyonghuActivity extends AppCompatActivity {
    private ListView lv;
    List<Doctor>doctorList= LitePal.findAll(Doctor.class);
    List<String>list;
    private ArrayAdapter adapter;
    boolean in;
    private TextView textView;
    private Button bt_left,bt_right;
    private Doctor doc;
    private BackupsUtils backupsUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_guanliyonghu);
        initView();
        in=new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin();

        initData();
        initClick();
    }

    private void initView() {
        backupsUtils = new BackupsUtils(this);
        lv= (ListView) findViewById(R.id.lv_yonghu);
        textView= (TextView) findViewById(R.id.title_text);
        textView.setText(getString(R.string.setting_User_management));
        bt_left= (Button) findViewById(R.id.btn_left);
        bt_right= (Button) findViewById(R.id.btn_right);
        bt_left.setVisibility(View.VISIBLE);
        bt_right.setVisibility(View.INVISIBLE);
        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initClick() {
       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, final int i, final long l) {
               doc=new LoginRegister().getDoctor(list.get(i));
               final CharSequence[] items = {getString(R.string.setting_Restore_initial_password),getString(R.string.setting_manager_delete)};//弹出框展示内容
               AlertDialog.Builder builder = new AlertDialog.Builder(GuanliyonghuActivity.this);//声明弹出框
               builder.setItems(items, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int item) {

                       switch (item){
                           case 0://生成pdf报告
                               if(in){//如果为超级用户可以修改
                                    doc.setdPassword("123456");
                                   doc.save();
                                   backupsUtils.initBackUpDoctor(1);
                                   MyToast.showToast(GuanliyonghuActivity.this, getString(R.string.setting_Restore_initial_password_success));
//                                   Toast.makeText(GuanliyonghuActivity.this, R.string.setting_Restore_initial_password_success, Toast.LENGTH_SHORT).show();
                               }else {//普通用户不可以
                                   MyToast.showToast(GuanliyonghuActivity.this, getString(R.string.setting_faild_Jurisdiction));
//                                   Toast.makeText(GuanliyonghuActivity.this, R.string.setting_faild_Jurisdiction, Toast.LENGTH_SHORT).show();
                               }
                               break;
                           case 1://删除所选项
                               if(in){
                                   if(!list.get(i).equals("Admin")){
                                       dialogViewDelete(i);
                                       backupsUtils.initBackUpDoctor(1);
                                   }else {
                                       MyToast.showToast(GuanliyonghuActivity.this, getString(R.string.setting_faild_Admin));
//                                       Toast.makeText(GuanliyonghuActivity.this,R.string.setting_faild_Admin, Toast.LENGTH_SHORT).show();
                                   }
                               }else {
                                   MyToast.showToast(GuanliyonghuActivity.this, getString(R.string.setting_faild_Jurisdiction));
//                                   Toast.makeText(GuanliyonghuActivity.this, R.string.setting_faild_Jurisdiction, Toast.LENGTH_SHORT).show();
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

    private void initData() {
        list=new ArrayList<>();
        for(Doctor doc:doctorList){
            String name=doc.getdName();
            list.add(name);
        }

        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        setListViewHeightBasedOnChildren(lv);
    }
    /**
     * 重新计算ListView的高度
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }



    AlertDialog dialogdelete = null;
    private void dialogViewDelete(final int index) {
        if (dialogdelete == null) {
            dialogdelete = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.print_Notice_title))
                    .setCancelable(false)
                    .setMessage(getString(R.string.setting_delete_user))
                    .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LitePal.deleteAll(Doctor.class,"dName=?",list.get(index));//查询所有符合条件的数据
                            list.remove(index);//删除所选项
                            adapter=new ArrayAdapter(GuanliyonghuActivity.this,android.R.layout.simple_list_item_1,list);
                            adapter.notifyDataSetChanged();//适配器刷新
                            lv.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(lv);

                        }
                    })
                    .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
//                            Logger.e("");
//                            adapter.notifyDataSetChanged();
                        }
                    })
                    .create();
        }
        dialogdelete.show();
    }


}
