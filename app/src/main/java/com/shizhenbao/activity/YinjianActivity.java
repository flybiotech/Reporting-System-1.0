package com.shizhenbao.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.activity.R;


public class YinjianActivity extends AppCompatActivity {
    private TextView tv_title;
    Button bt_left,bt_right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinjian);
        tv_title= (TextView) findViewById(R.id.title_text);
        bt_left= (Button) findViewById(R.id.btn_left);
        bt_right= (Button) findViewById(R.id.btn_right);
        tv_title.setText(getString(R.string.setting_Parameter_configuration));
        bt_left.setVisibility(View.VISIBLE);
        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bt_right.setVisibility(View.INVISIBLE);
    }
}
