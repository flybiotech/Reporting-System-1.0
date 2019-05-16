package com.shizhenbao.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.connect.DevConnect;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.util.Const;
import com.shizhenbao.wifiinfo.WifiAutoConnectManager;
import com.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AdminManager extends AppCompatActivity implements View.OnClickListener{
    private Button bt_admin,bt_delete,bt_sn;
    TextView title_bar;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(AdminManager.this,  getString(R.string.setting_data_clear_success_message), Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(AdminManager.this,LoginActivity.class);
            intent.putExtra("msg",1);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager);
        initView();
        initClick();
    }

    private void initClick() {
        bt_sn.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        bt_admin.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_admin:
                List<Doctor>doctorList= DataSupport.where("dName=?","Admin").find(Doctor.class);
                if(doctorList.size()>0){
                    for(int i=0;i<doctorList.size();i++){
                        doctorList.get(0).setdPassword("123456");
                        doctorList.get(0).save();
                    }
                }else {
                    Toast.makeText(AdminManager.this, getString(R.string.super_manager_error), Toast.LENGTH_SHORT).show();
                }
                Intent i=new Intent(AdminManager.this,LoginActivity.class);
                i.putExtra("msg",1);
                startActivity(i);
                Toast.makeText(AdminManager.this, getString(R.string.super_manager_success), Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_sn:

                break;
            case R.id.bt_delete:
                dialogViewDeleteData();
                break;
        }
    }
    private void initView() {
        bt_sn= (Button) findViewById(R.id.bt_sn);
        bt_sn.setVisibility(View.INVISIBLE);
        bt_delete= (Button) findViewById(R.id.bt_delete);
        bt_admin= (Button) findViewById(R.id.bt_admin);
        title_bar= (TextView) findViewById(R.id.title_bar);
        title_bar.setText(getString(R.string.super_manager));
    }
    public void dialogViewDeleteData() {

        final AlertDialog dialogView = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.setting_Restore_factory_settings))
                .setCancelable(false)
                .setMessage(R.string.setting_Restore_factory_settings_message)
                .setPositiveButton(getString(R.string.graffiti_enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DevConnect(AdminManager.this,handler).deleteAllData(handler);
                    }
                })
                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        dialogView.show();
    }


}
