package com.shizhenbao.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.adapter.JianjiAdapter;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.view.MyToast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class JianjiPreviewActivity extends AppCompatActivity {
    private GridView gv;
    private Button bt_rush;
    private List<Item>list;
    String id;
    private User user;
    String gathPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_jianji_preview);
        gv= (GridView) findViewById(R.id.gv);
        bt_rush= (Button) findViewById(R.id.flush);
        Intent intent=getIntent();
        id=intent.getStringExtra("msg");
        initSelect(id);
        initDAta();
        iniTClick();
    }
    public void initSelect(String id){
        if(!TextUtils.isEmpty(id)){
            user=new LoginRegister().getUserName(Integer.parseInt(id));
        }
        gathPath=user.getGatherPath();

    }
    private void initView(View view){

    }
    private void iniTClick() {
        gv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //6.0尺寸以下的设备，跳到这个界面
                    OneItem.getOneItem().setJianjiPath(list.get(i).getJianjiPath());//将选择的图片路径保存在实体类中
                    Intent intent=new Intent(JianjiPreviewActivity.this,JianjiActivity.class);
                    intent.putExtra("msg",1);
                    startActivity(intent);//页面跳转
                    finish();
            }
        });
        bt_rush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyToast.showToast(JianjiPreviewActivity.this, getString(R.string.setting_picture_preview));
//                Toast.makeText(JianjiPreviewActivity.this, getString(R.string.setting_picture_preview), Toast.LENGTH_SHORT).show();
                initDAta();
            }
        });
    }

    //展示该文件下病人所有的.png格式的图片

    private void initDAta() {
        list=new ArrayList<>();
        if (getSD() != null) {
            File[] files = new File(gathPath).listFiles(new FilenameFilter() {//得到该路径下所有的文件
                @Override
                public boolean accept(File dir, String name) {
                    boolean a = false;
                    if (name.endsWith(".jpg")) {//判断该文件是否属于图片
                        a = true;
                    }
                    return a;
                }
            });

            for (File file : files) {
                Item item = new Item();
                item.setJianjiPath(gathPath+"/" + file.getName());//将图片路径添加到实体类的list集合中
                list.add(item);
            }

            JianjiAdapter adapter = new JianjiAdapter(this, list);
            gv.setAdapter(adapter);
        }
    }
    /**
     * 得到SD卡根目录
     * @return
     */
    public File getSD(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return Environment.getExternalStorageDirectory();

        }else {
            return null;
        }
    }
}
