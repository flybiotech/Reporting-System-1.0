package com.shizhenbao.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.UI.CustomConfirmDialog;
import com.shizhenbao.adapter.Image1Adapter;
import com.shizhenbao.adapter.Image2Adapter;
import com.shizhenbao.adapter.ImageAdapter;
import com.shizhenbao.adapter.ImageDCAdapter;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.LogUtil;
import com.shizhenbao.util.OneItem;
import com.view.MyToast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener{
    private GridView gv_bingren,gv_yisheng,gv_cusuan,gv_dianyou;
    private List <Item>listitem=null;//病人原图路径集合
    private List <Item>listitem4=null;//病人碘油路径集合
    private List <Item>listitem5=null;//病人醋酸白路径集合
    private List <Item>listitem2=null;//医生编辑过后的图片
//    private Button bt;
    private List<String>list1=new ArrayList<>();//病人原图路径集合
    private List<String>list4=new ArrayList<>();//病人碘油路径集合
    private List<String>list5=new ArrayList<>();//病人醋酸白路径集合
    private List<String>list2=new ArrayList<>();//医生照片路径集合
    private List<String>list3=new ArrayList<>();//选择照片集合
    private List<String>list6=new ArrayList<>();//病人照片选择照片集合
    private Button bt_lef,bt_right;
    private TextView tv_title;
    private String id=null,name="";
    private User user;
    private String gathPath;
    private ImageAdapter adapter;//展示原图的
    private Image1Adapter adapter1;//展示醋酸白的图片
    private Image2Adapter adapter2;//展示碘油的图片
    private ImageDCAdapter adapterDc;//医生编辑过后的图片
    private CustomConfirmDialog customConfirmDialog = null;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        id = intent.getStringExtra("msg");//得到传递过来的病人id值
        name = intent.getStringExtra("msgName");//得到传递过来的病人姓名
        initView();//实例化控件
        if (!id.equals("") && !name.equals("")) {//判断是否有病人信息需要添加
            gathPath=initSelect(id);
            showList();//病人图片数据源展示
            showList1();//医生诊断数据源展示
        }
        initClick();//按钮点击事件

    }
    public String initSelect(String id){

        if(!TextUtils.isEmpty(id)){
            user=new LoginRegister().getUserName(Integer.parseInt(id));
        }
        gathPath=user.getGatherPath();
        return gathPath;
    }
    private void initClick() {
        bt_lef.setOnClickListener(this);//返回按钮点击
//        bt.setOnClickListener(this);//确定按钮点击
        floatingActionButton.setOnClickListener(this);
    }

    private void initView() {
        customConfirmDialog=new CustomConfirmDialog(this);
        gv_bingren= (GridView) findViewById(R.id.gv_yuanshi);
        gv_cusuan= (GridView) findViewById(R.id.gv_cusuan);
        gv_dianyou= (GridView) findViewById(R.id.gv_dianyou);
        gv_yisheng= (GridView) findViewById(R.id.gv_yisheng);
//        bt= (Button) findViewById(R.id.bt);
        floatingActionButton = findViewById(R.id.image_save);
        bt_lef= (Button) findViewById(R.id.btn_left);
        bt_right= (Button) findViewById(R.id.btn_right);
        bt_right.setVisibility(View.VISIBLE);
        bt_right.setText(getString(R.string.patient_show_delete));
        bt_right.setOnClickListener(this);
        tv_title= (TextView) findViewById(R.id.title_text);
        tv_title.setText(getString(R.string.print_iamge_select));//title内容
        bt_lef.setVisibility(View.VISIBLE);//设置返回按钮可见
        if (user == null) {
            user = new User();
        }

    }
    //展示该文件下病人所有的..jpg格式的图片
    public void showList() {
        gathPath=initSelect(id);
        if (gathPath == null) {
            return;
        }
        listitem = new ArrayList<>();
        listitem4 = new ArrayList<>();
        listitem5 = new ArrayList<>();
        if (new Item().getSD() != null) {
            File[] files = new File(gathPath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean a = false;
                    if (name.endsWith(".jpg")) {//所有.jpg格式的文件添加到数组中

                        a = true;
                    }
                    return a;
                }
            });
            if (files!=null&&files.length != 0) {
                //遍历数组动态添加图片到对象中去
                for (File file : files) {
                    if(!file.getName().equals("方位.png")&&file.getName()!="方位.png"){
//                        String image=file.getName().substring(17,file.getName().length());
                        String image=file.getName();
                        if(image.contains("质控图")&&image.contains(".jpg")){
                            Item itemImage=new Item();
                            itemImage.setCb(new CheckBox(this));
                            itemImage.setPath(gathPath+"/"+file.getName());//图片路径
                            listitem.add(itemImage);
                        }else if(image.contains("碘油")&&image.contains(".jpg")){
                            Item itemImage=new Item();
                            itemImage.setCb(new CheckBox(this));
                            itemImage.setPath(gathPath+"/"+file.getName());//图片路径
                            listitem4.add(itemImage);
                        }else if(image.contains("醋酸白")&&image.contains(".jpg")){
                            Item itemImage=new Item();
                            itemImage.setCb(new CheckBox(this));
                            itemImage.setPath(gathPath+"/"+file.getName());//图片路径
                            listitem5.add(itemImage);
                        }
                    }
                }
            }
            adapter = new ImageAdapter(this,listitem);//展示原图的图片
            gv_bingren.setAdapter(adapter);
            adapter1 = new Image1Adapter(this,listitem5);//展示醋酸白的图片
            gv_cusuan.setAdapter(adapter1);
            adapter2 = new Image2Adapter(this,listitem4);//展示碘油的图片
            gv_dianyou.setAdapter(adapter2);
        }
    }
    //展示该文件下医生所有的.png格式的图片
    private void showList1() {
        gathPath=initSelect(id);
        listitem2 = new ArrayList<>();
        if (new Item().getSD() != null) {
            String strtpath = gathPath+"/User_cut";
            if(new File(strtpath).exists()){
//                Toast.makeText(this, "医生路径："+strtpath, Toast.LENGTH_SHORT).show();
                File[] files = new File(strtpath).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        boolean a = false;
                        if (name.endsWith(".png")) {
                            a = true;
                        }
                        return a;
                    }
                });
                if(files!=null&&files.length!=0){
                    for (File file : files) {
                        Item itemImage=new Item();
                        itemImage.setCb(new CheckBox(this));
                        itemImage.setPath(strtpath+"/"+file.getName());
                        listitem2.add(itemImage);
                    }
                }
//            ImageDCAdapter a = new ImageDCAdapter(this, list);
                adapterDc = new ImageDCAdapter(this,listitem2);
                gv_yisheng.setAdapter(adapterDc);
            }else {
                new File(strtpath).mkdirs();
                showList1();
            }
        }else {

        }
    }

    @Override
    public void onClick(View v) {
        list1=new ArrayList<>();//病人原图路径集合
        list2=new ArrayList<>();//病人原图路径集合
        list4=new ArrayList<>();//病人原图路径集合
        list5=new ArrayList<>();//病人原图路径集合
        list1.clear();
        list2.clear();
        list4.clear();
        list5.clear();
        list1=ImageAdapter.getList();//选择病人原图上传图片集合
        list4=Image1Adapter.getList();//选择病人碘油图片集合
        list5=Image2Adapter.getList();//选择病人醋酸白图片集合
        list2=ImageDCAdapter.getList();//选择医生上传图pain
//        Toast.makeText(this, "a:"+list1.size()+"b:"+list4.size()+"c:"+list5.size(), Toast.LENGTH_SHORT).show();
        switch (v.getId()){
            case R.id.btn_right://右边删除键
                if(id!=null&&!id.equals("")) {
                    if (list1.size() == 0 && list2.size() == 0 && list4.size() == 0 && list5.size() == 0) {
                        MyToast.showToast(this, getString(R.string.print_picture_no_choise));
//                        Toast.makeText(this, getString(R.string.print_picture_no_choise), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (customConfirmDialog == null) {
                        customConfirmDialog = new CustomConfirmDialog(this);
                    }
                    customConfirmDialog.show(getString(R.string.image_manage_delete_title), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < list1.size(); i++) {
                                File file = new File(list1.get(i));
                                file.delete();
                            }
                            for (int i = 0; i < list2.size(); i++) {
                                File file = new File(list2.get(i));
                                file.delete();
                            }
                            for (int i = 0; i < list4.size(); i++) {
                                File file = new File(list4.get(i));
                                file.delete();
                            }
                            for (int i = 0; i < list5.size(); i++) {
                                File file = new File(list5.get(i));
                                file.delete();
                            }
                            ImageAdapter.setList();
                            ImageDCAdapter.setList();
                            Image1Adapter.setList();
                            Image2Adapter.setList();
                            adapter.notifyDataSetChanged();
                            adapterDc.notifyDataSetChanged();
                            adapter1.notifyDataSetChanged();
                            adapter2.notifyDataSetChanged();
                            try {
                                if (customConfirmDialog != null) {
                                    customConfirmDialog.dimessDialog();
                                }

                                showList();
                                showList1();//医生诊断数据源展示
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (customConfirmDialog != null) {
                                customConfirmDialog.dimessDialog();
                            }
                        }
                    });

                }else {
                    MyToast.showToast(this, getString(R.string.print_patient_select));
//                    Toast.makeText(this, getString(R.string.print_patient_select), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btn_left:
                ImageAdapter.setList();
                Image1Adapter.setList();
                Image2Adapter.setList();
                ImageDCAdapter.setList();
//                if(!id.equals(getString(R.string.patient_id))&&!id.equals("")&&id!=null){
//                    list1.clear();//清除数据源
//                    list2.clear();//清除数据源
//                    if(!id.equals(getString(R.string.patient_id))&&!id.equals("")){
//                        user=new LoginRegister().getUserName(Integer.parseInt(id));//根据传递过来的id得到相应的user数据
//                        if(user.getImage()==1||user.getImage()==0){
//                            OneItem.getOneItem().setList(new ArrayList<String>());
//                        }else if(user.getImage()==2){
//                            OneItem.getOneItem().setList(user.getImgPath());
//                        }
//                    }
//                }
                finish();
                break;

            case R.id.image_save:
                OneItem.getOneItem().setD(2);
                list3.clear();
                list6.clear();
                list6.addAll(list1);
                list6.addAll(list4);
                list6.addAll(list5);
                if(!id.equals(getString(R.string.patient_id))&&!id.equals("")) {//如果传递过来的id为空，点击确定直接退出页面
//                    if(list6.size()>3){//判断选择病人图片是否大于三张
//                        Toast.makeText(ImageActivity.this,getString(R.string.print_picture_three), Toast.LENGTH_SHORT).show();
//                    }
//                    else {
                    user=new LoginRegister().getUserName(Integer.parseInt(id));//根据传递过来的id得到相应的user数据
                    list3.addAll(list6);
                    list3.addAll(list2);
                    if(list3.size()==0){//如果没有选择图片则添加空白图片
                        for(int i=0;i<3;i++){
                            list3.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/blank.png");
                        }
                        user.setImgPath(list3);
                        user.setImage(1);
                    }else if(list3.size()>4){
                        MyToast.showToast(ImageActivity.this,getString(R.string.print_picture_three));
//                        Toast.makeText(ImageActivity.this,getString(R.string.print_picture_three), Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        user.setImgPath(list3);
                        user.setImage(2);
                    }
//                    user.setImgPath(list3);
                    user.updateAll("pId=?",id);
//                        user.save();
                    OneItem.getOneItem().setList(list3);//为OneItem类中的list赋值
                    list1.clear();//清除数据源
                    list2.clear();//清除数据源
                    list4.clear();
                    list5.clear();
                    finish();
//                    }
                }else {
                    finish();
                }

                break;
//            case R.id.bt:
//                OneItem.getOneItem().setD(2);
//                list3.clear();
//                list6.clear();
//                list6.addAll(list1);
//                list6.addAll(list4);
//                list6.addAll(list5);
//                if(!id.equals(getString(R.string.patient_id))&&!id.equals("")) {//如果传递过来的id为空，点击确定直接退出页面
////                    if(list6.size()>3){//判断选择病人图片是否大于三张
////                        Toast.makeText(ImageActivity.this,getString(R.string.print_picture_three), Toast.LENGTH_SHORT).show();
////                    }
////                    else {
//                    user=new LoginRegister().getUserName(Integer.parseInt(id));//根据传递过来的id得到相应的user数据
//                    list3.addAll(list6);
//                    list3.addAll(list2);
//                    if(list3.size()==0){//如果没有选择图片则添加空白图片
//                        for(int i=0;i<3;i++){
//                            list3.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/blank.png");
//                        }
//                        user.setImgPath(list3);
//                        user.setImage(1);
//                    }else if(list3.size()>4){
//                        MyToast.showToast(ImageActivity.this,getString(R.string.print_picture_three));
////                        Toast.makeText(ImageActivity.this,getString(R.string.print_picture_three), Toast.LENGTH_SHORT).show();
//                        return;
//                    }else {
//                        user.setImgPath(list3);
//                        user.setImage(2);
//                    }
////                    user.setImgPath(list3);
//                    user.updateAll("pId=?",id);
////                        user.save();
//                    OneItem.getOneItem().setList(list3);//为OneItem类中的list赋值
//                    list1.clear();//清除数据源
//                    list2.clear();//清除数据源
//                    list4.clear();
//                    list5.clear();
//                    finish();
////                    }
//                }else {
//                    finish();
//                }
//
//                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            ImageAdapter.setList();
            Image1Adapter.setList();
            Image2Adapter.setList();
            ImageDCAdapter.setList();
            finish();

            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    private List<String> fileSplitNameList = new ArrayList<>();


}
