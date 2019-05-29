package com.shizhenbao.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.activity.R;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    private Button bt_left,bt_right;
    private TextView tv;
    private ListView listView;
    private ArrayAdapter adapter;
    private List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_help);
        initView();
    }
    private void initView() {
        bt_left= (Button) findViewById(R.id.btn_left);
        bt_right= (Button) findViewById(R.id.btn_right);
        tv= (TextView) findViewById(R.id.title_text);
        tv.setText(getString(R.string.setting_Common_problem));
        bt_right.setVisibility(View.INVISIBLE);
        bt_left.setVisibility(View.VISIBLE);
        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             finish();
            }
        });
        list = new ArrayList<>();
        list.add(getString(R.string.setting_ordinary_register));
        list.add(getString(R.string.setting_select_patient_message));
//        list.add(getString(R.string.setting_Treasures_of_vision_modify));
//        list.add(getString(R.string.setting_Treasures_of_vision_delete));
        list.add(getString(R.string.setting_Treasures_of_vision_link_faild));
        list.add(getString(R.string.setting_print_attention));
        list.add(getString(R.string.setting_select_case));
        list.add(getString(R.string.setting_Contrast_case));
//        list.add(getString(R.string.setting_removal_used));
//        list.add(getString(R.string.setting_Treasures_of_vision_light));
        list.add(getString(R.string.setting_diagnosis_delete));
//        list.add(getString(R.string.setting_picture_edit));
        listView = (ListView) findViewById(R.id.listview_help_layout);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HelpActivity.this,HelpShowActivity.class);
                intent.putExtra("helpItem", position);
                intent.putExtra("helpTitle", list.get(position));
                startActivity(intent);

            }
        });

    }
}
