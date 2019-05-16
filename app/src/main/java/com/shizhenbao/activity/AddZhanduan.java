package com.shizhenbao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.activity.R;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class AddZhanduan extends AppCompatActivity {
    private ListView lv_add_zhanduan;
    private Button bt_left,bt_right;
    private List<User> userList=null;
    private SelectAdapter adapter=null;
    private List<Item> list;
    private List<User> userlist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_zhanduan);
        initView();
        chaxun();
        initClick();
    }

    private void initClick() {
        lv_add_zhanduan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //6.0尺寸以下的设备，跳到这个界面
                    OneItem.getOneItem().setId(list.get(i).getpId());
                    OneItem.getOneItem().setB(false);//如果B为真，登记跳转到诊断，如果为false,展示病人信息跳转过去
                    OneItem.getOneItem().setC(true);//判断诊断页面是否可以输入，为true时可以输入，否则不可以输入
                    Intent intent=new Intent(AddZhanduan.this,MainActivity.class);//声明Intent
                    intent.putExtra("canshu",3);//传递参数3到主页面，从主页面跳转到诊断页面
                    startActivity(intent);//页面跳转
                    finish();
            }
        });
    }

    private void initView() {
        lv_add_zhanduan= (ListView) findViewById(R.id.lv_add_zhenduan);
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
    private void chaxun(){
        list=new ArrayList<>();
        userList= DataSupport.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
        for(int i=0;i<userList.size();i++){
            if(userList.get(i).getIs_diag()==1){
                userlist.add(userList.get(i));
            }
        }
        for(int j=0;j<userlist.size();j++){
            Item item=new Item();
            item.setpId(userlist.get(j).getpId());
            item.setpName(userlist.get(j).getpName());
            list.add(item);
        }
        adapter=new SelectAdapter(AddZhanduan.this,list);
        lv_add_zhanduan.setAdapter(adapter);
        new GuanliyonghuActivity().setListViewHeightBasedOnChildren(lv_add_zhanduan);
    }
}
