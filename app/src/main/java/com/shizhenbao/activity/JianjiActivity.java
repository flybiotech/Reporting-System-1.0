package com.shizhenbao.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.wifiinfo.WifiAutoConnectManager;
import com.shizhenbao.pop.Points;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.DisplayMetricss;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;

import org.litepal.crud.DataSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class JianjiActivity extends Activity implements View.OnClickListener,View.OnTouchListener{
    private Button btn_left, btn_right, refresh, save, album;
    private ImageView imageShow;
    private TextView tv_title;
    //    ConnectWifiDao conWifi;
    List<Points> list;
    List<Item> listItem= new ArrayList<Item>();
    List<Points>listLine;
    private float area=0,length=0;
    private Bitmap bitmap01;
    private Bitmap bitmap02;
    private Canvas canvas;
    private Paint paint;
    private Paint paint2;
    private Paint paint3;
    private Path path;
    private Bitmap baseBitmap;
    private Bitmap bitmap;
    String imagePath = null;
    AlertDialog dialogadd = null;//选择患者对话框
    int num=0;//判断翻页次数
    double temp;//患者信息所占页数
    int usersize=0;//所有未诊断患者的数量
    private List<Item> list1;//展示数据源
    public int i = 0;
//    List<Float> Lengthnum;
    private static final String TAG = "myCarch_JianjiActivity";
    private static final String TAG1 = "TAG1";
    static final String[] PERMISSION = new String[]{
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//读取设备信息
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_NETWORK_STATE

    };
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 3;
    private String id;
    private DisplayMetricss dm;
    WifiAutoConnectManager wifiAutoConnectManager;
    public File appDir=null;
    private User user =null;
    private boolean isSave=false;
    private List<User> userList=null;
    private List<User> userlist=new ArrayList<>();
    private int a=0;//判断是否弹出对话框
    int min=-1;//防止不断重复进入图片展示界面，如果a=1时会重复进入
    private String gatherPath;
    String pathnew;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==-1){
                if(dialogadd.isShowing()){
                    dialogadd.dismiss();
                    Toast.makeText(getContext(), R.string.image_dialog_automatic, Toast.LENGTH_SHORT).show();
                    //判断是否可以对sd卡进行操作
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        user=new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
                        initSelect(OneItem.getOneItem().getUserId());
                        appDir = new File(gatherPath);
                        if (!appDir.exists()) {
                            appDir.mkdirs();
                        }
                        Const.nameJianJi = user.getpName();
                        tv_title.setText(getString(R.string.setting_Picture_editor)+"( "+Const.nameJianJi+" )");
                        getImage();
                    }else {
                        Toast.makeText(JianjiActivity.this, R.string.setting_sd, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jianji);
        init();
            if (pathnew != null) {
                imagePath = pathnew;
            } else {
                imagePath=new Item().getSD()+"/SZB_save/a.jpg";
            }
            Glide.with(this)//图片加载框架
                    .load(imagePath)
                    .crossFade()
                    .into(imageShow);
            displayImage(imagePath);

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent=this.getIntent();
        a= Const.dialogshow;//判断是否选择患者
        pathnew=intent.getStringExtra("msg1");//传递过来的图片路径
        min=Const.imageshow;
        if(a==0){
            selectUser(0);
        }else if (a==1){
            imageListSearch();
        }
        if (pathnew != null) {
            imagePath = pathnew;

        } else {
            imagePath=new Item().getSD()+"/SZB_save/a.jpg";
        }
        tv_title.setText(getString(R.string.setting_Picture_editor)+"( "+Const.nameJianJi+" )");
        Glide.with(this)//图片加载框架
                .load(imagePath)
                .crossFade()
                .into(imageShow);
        displayImage(imagePath);
    }

    private void imageListSearch() {
        try {
            user=new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
            gatherPath=user.getGatherPath()+"/User_cut";
            Logger.e(" 用户的pdf路径 gatherPath ： "+gatherPath);
            appDir = new File(gatherPath);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            Const.nameJianJi = user.getpName();
            if(min==-1){
                getImage();
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }
    private void selectUser(int yeshu) {

            //查找医生名下的患者的个数
            Logger.e("到了select");
            try {
                list1=new ArrayList<>();
                if(userList!=null){
                    userList.clear();
                }

                listItem.clear();
                userlist.clear();
                try {
                    if(OneItem.getOneItem().getName()!=null){
                        if(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()){
                            userList=DataSupport.findAll(User.class);
                        }else {
                            userList= DataSupport.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                        }
                    }else {
                        new UserManager().getExceName();
                        if(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()){
                            userList=DataSupport.findAll(User.class);
                        }else {
                            userList= DataSupport.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                        }
                    }
                }catch (Exception e){
//                    Logger.e("错误剪辑   ....:"+e.getMessage());
                    new UserManager().getExceName();
                    if(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()){
                        userList=DataSupport.findAll(User.class);
                    }else {
                        userList= DataSupport.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                    }
                }
                if (userList.size() > 0) {
                    for (int i = 0; i < userList.size(); i++) {
                        if (userList.get(i).getIs_diag() == 1) {
                            userlist.add(userList.get(i));
                        }
                    }
                    Const.list=userlist;
                    lv_list(yeshu);
//
                } else {
                    Toast.makeText(this, R.string.image_set_patients_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void lv_list(int yeshu){
        list1.clear();
        double userSize=Const.list.size();
        temp = userSize / 5.0;
        Const.usersize=Const.list.size();
        if (temp-yeshu >=1||temp-yeshu==temp) {
            if(temp>=1){
                for (int j = yeshu * 5; j < yeshu * 5 +5; j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name)+":" + userlist.get(j).getpName() + "  " +getString(R.string.patient_age)+ ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userlist.get(j).getTel();
                    item.setpId(userlist.get(j).getpId());
                    item.setpName(zhanshi);
                    list1.add(item);
                }
            }else {
                for (int j = yeshu * 5; j < userlist.size(); j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name)+":" + userlist.get(j).getpName() + "  " +getString(R.string.patient_age)+ ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userlist.get(j).getTel();
                    item.setpId(userlist.get(j).getpId());
                    item.setpName(zhanshi);
                    list1.add(item);
                }
            }

        }else if(temp-yeshu<1){
            for (int j = yeshu * 5; j < userlist.size(); j++) {
                Item item = new Item();
                String zhanshi = getString(R.string.patient_name)+":" + userlist.get(j).getpName() + "  " +getString(R.string.patient_age)+ ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userlist.get(j).getTel();
                item.setpId(userlist.get(j).getpId());
                item.setpName(zhanshi);
                list1.add(item);
            }
        }
        if (list1.size() >=1) {
            lv_additem(list1);
        } else if (list1.size() == 0) {
            return;
        }else {
            id=list1.get(0).getpId();
            OneItem.getOneItem().setUserId(list1.get(0).getpId());
            initSelect(id);
            //判断是否可以对sd卡进行操作
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                user=new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
                appDir = new File(gatherPath);
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                Const.nameJianJi = user.getpName();
                getImage();
            }else {
                Toast.makeText(this, R.string.setting_sd, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void lv_additem(final List<Item>list){
        if (dialogadd != null) {
            if (dialogadd.isShowing()) {
                return;
            }
        }
        LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SelectAdapter selectAdapter=new SelectAdapter(this,list);
        listView.setAdapter(selectAdapter);
        selectAdapter.notifyDataSetChanged();
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        dialogadd=new AlertDialog.Builder(this).setTitle(getString(R.string.image_Select_patients)).setView(linearLayoutMain).setNeutralButton(getString(R.string.button_cancel),null).setPositiveButton(getString(R.string.image_next_page),null).setNegativeButton(getString(R.string.image_Previous_page),null).create();
        dialogadd.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button bt_cancle=dialogadd.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button bt_before=dialogadd.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button bt_next=dialogadd.getButton(AlertDialog.BUTTON_POSITIVE);

                bt_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogadd.dismiss();
                        OneItem.getOneItem().setUserId("");
                        num=0;
                    }
                });
                bt_before.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(temp-num!=temp){
                            if(num>0){
                                num--;
                            }
                            dialogadd.dismiss();
//                            dialogset=1;
                            lv_list(num);
                        }else {
                            Toast.makeText(getContext(), R.string.image_patients_first, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bt_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        num++;
                        if(temp>=num){
                            dialogadd.dismiss();
                            lv_list(num);
                        }else {
                            num--;
                            Toast.makeText(getContext(),R.string.image_patients_last, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialogadd.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialogadd.show();
        if(list.size()==1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = handler.obtainMessage();
                    message.what = -1;
                    int temp = 0;
                    while (temp < Const.delayTime) {
                        try {
                            Thread.sleep(1000);
                            temp++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    id=list.get(0).getpId();
                    OneItem.getOneItem().setUserId(list.get(0).getpId());
                    initSelect(id);
                    handler.sendMessage(message);
                }
            }).start();
        }
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                id=list.get(arg2).getpId();
                OneItem.getOneItem().setUserId(list.get(arg2).getpId());
                initSelect(id);
                //判断是否可以对sd卡进行操作
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    user=new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
                    initSelect(OneItem.getOneItem().getUserId());
                    appDir = new File(gatherPath);
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    Const.nameJianJi = user.getpName();
                    tv_title.setText(getString(R.string.setting_Picture_editor)+"( "+Const.nameJianJi+" )");
                    getImage();
                }else {
                    Toast.makeText(JianjiActivity.this, R.string.setting_sd, Toast.LENGTH_SHORT).show();
                }
                dialogadd.cancel();
            }
        });
    }

    //初始化控件
    private void init() {
        if (listLine == null) {
            listLine=new ArrayList<>();
        }
        Const.nameJianJi="";
        btn_left = (Button) findViewById(R.id.btn_left);//返回按钮，，暂时被隐藏了
        btn_right = (Button) findViewById(R.id.btn_right);//这个是菜单按钮
        btn_right.setText(getString(R.string.image_Select_patients));
        tv_title = (TextView) findViewById(R.id.title_text);//这个是标题栏
        save = (Button) findViewById(R.id.btn_jianji_save01);//保存图片的按钮
        refresh = (Button) findViewById(R.id.btn_jianji_refresh01);//刷新图片的按钮
        album = (Button) findViewById(R.id.btn_jianji_album);//获取需要剪辑的图片的按钮
        imageShow = (ImageView) findViewById(R.id.image_jianji_show); //展示需要剪辑的图片
        imageShow.setBackgroundResource(R.drawable.kb);
        btn_right.setVisibility(View.VISIBLE);
        dm = new DisplayMetricss(this);//获取屏幕的分辨率
        path = new Path();
        tv_title.setText(getString(R.string.setting_Picture_editor)+"( "+" )");
        btn_left.setVisibility(View.VISIBLE);
        list = new ArrayList<Points>();
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        save.setOnClickListener(this);
        refresh.setOnClickListener(this);
        album.setOnClickListener(this);
        imageShow.setOnTouchListener(this);
        imageShow.setBackgroundResource(R.drawable.kb);

        paint = new Paint();
        paint2 = new Paint();
        paint3 = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(5);
        paint3.setColor(Color.WHITE);
        paint3.setAntiAlias(true);
        paint3.setTextSize(35);
        paint3.setStrokeWidth(4);
    }
    public void initSelect(String id){
        if(!id.equals("")||id!=null){
            user=new LoginRegister().getUserName(Integer.parseInt(id));
        }
        gatherPath=user.getGatherPath()+"/User_cut";
    }
    @Override //按钮的监听事件
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_right:
                startActivity(new Intent(this,JianJiSearchActivity.class));
//                finish();
                break;

            case R.id.btn_jianji_refresh01://重绘按钮
                refresh();
                float screen=dm.getDisplayMetricss();
                Log.d(TAG, "屏幕的分辨率screen: "+screen);
                break;
            case R.id.btn_jianji_save01://保存图片的按钮
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                        WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    if(OneItem.getOneItem().getUserId()!=null&&!OneItem.getOneItem().getUserId().equals("")){
                        user=new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
                        initSelect(OneItem.getOneItem().getUserId());
                        appDir = new File(gatherPath);
                        if (!appDir.exists()) {
                            appDir.mkdirs();
                        }
                        getFiles(appDir.toString());
                        if (i>2){
                            Log.d(TAG1, "已经保存的图片的张数 saveBitmap i : "+i);
                            Toast.makeText(this, getString(R.string.setting_edit_no_more),Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            saveBitmap(this);//保存
                            Log.d(TAG, "onClick: 没有数据222");
                        }
                    }else {
                        Toast.makeText(this, R.string.print_patient_select, Toast.LENGTH_SHORT).show();
                    }
                }
                refresh();
                imageShow.setBackgroundResource(R.drawable.kb);
                break;
            case R.id.btn_jianji_album://获取需要剪辑的图片的按钮
                    getImage();
                break;
            case R.id.btn_left:
                OneItem.getOneItem().setJianjiPath("");
                Const.dialogshow=0;
                finish();
                break;
            default:
                break;
        }
    }
    private void getImage(){

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0申请权限
            ActivityCompat.requestPermissions(this, PERMISSION, REQUEST_CODE_ASK_CALL_PHONE);
        } else {
            if (appDir != null&&!appDir.equals("")) {
                getFiles(appDir.toString());

                refresh();
                Intent intent1 = new Intent(getContext(), ImageManegerActivity.class);
                intent1.putExtra("msg",OneItem.getOneItem().getUserId());
                startActivity(intent1);
                finish();

            } else {
                Toast.makeText(this, R.string.setting_file_null, Toast.LENGTH_SHORT).show();
            }
        }
    }

        public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            OneItem.getOneItem().setJianjiPath("");
//            imageShow.setBackgroundResource(R.drawable.kb);
            Log.e(TAG, "onKeyDown: " );
            this.finish();
        }
        return false;
    }

    float startX;
    float startY;
    float endX1;
    float endtY1;
    float linePointX=0;
    float linePointY=0;
    float areaPointX=0;
    float areaPointY=0;

    @Override //触控监听事件
    public boolean onTouch(View v, MotionEvent event) {
        if (bitmap==null){
            return true;
        }
//        if (listLine == null) {
//            listLine=new ArrayList<>();
//        }

        initCanvas();
        switch (event.getAction()&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN://按下第一个手指
                startX = event.getX();
                startY=event.getY();
                endX1=0;
                endtY1=0;
                list.clear();
                list.add(new Points(startX, startY));
                path.moveTo(startX,startY);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (endX1>0&&endtY1>0){
                    path.lineTo(endX1,endtY1);
                    canvas.drawPath(path,paint);
                    startX=endX1;
                    startY=endtY1;
                    list.add(new Points(endX1, endtY1));
                    imageShow.setImageBitmap(baseBitmap);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                endX1 = event.getX();
                endtY1=event.getY();
                canvas.drawPath(path,paint);
                canvas.drawLine(startX,startY,endX1,endtY1,paint2);

                imageShow.setImageBitmap(baseBitmap);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "MotionEvent.ACTION_UP: ");
                path.close();
                canvas.drawPath(path,paint);
                if (list.size()==2) {
                    length = lineLength(list);
                    linePointX=(list.get(0).getX()+list.get(1).getX())/2-20;
                    linePointY=(list.get(0).getY()+list.get(1).getY())/2;
                    Points points=new Points(linePointX,linePointY);
                    points.setI(0);
                    points.setShow(length);
                    points.setX(linePointX);
                    points.setY(linePointY);
                    listLine.add(points);

                } else if (list.size()>2){
                    area = caculate(list);
                    areaPointX=(list.get(0).getX()+list.get(1).getX())/2-20;
                    areaPointY=(list.get(0).getY()+list.get(1).getY())/2;
                    Points points=new Points(areaPointX,areaPointY);
                    points.setI(1);
                    points.setShow(area);
                    points.setX(areaPointX);
                    points.setY(areaPointY);
                    listLine.add(points);

                }
                for(int i=0;i<listLine.size();i++){
                    if(listLine.get(i).getI()==0){
                        canvas.drawText(getString(R.string.setting_length)+"： "+listLine.get(i).getShow(),listLine.get(i).getX(),listLine.get(i).getY(),paint3);
                    }else if(listLine.get(i).getI()==1){
                        canvas.drawText(getString(R.string.setting_acreage)+"： "+listLine.get(i).getShow(),listLine.get(i).getX(),listLine.get(i).getY(),paint3);
                    }
                }
                Log.d(TAG, "最后一个手指抬起来的时候的坐标 mstartX1: "+event.getX()+"-ss--"+endX1+"--mstartY1-"+event.getY()+"  ss:"+endtY1);
                imageShow.setImageBitmap(baseBitmap);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "第一根手指抬起来的时候MotionEvent.ACTION_POINTER_UP: Path  ");
                break;
        }
        return true;
    }


    //canvas paint path的初始化，并且将背景图画在canvas上
    private void initCanvas() {
        isSave=true;
        getScaledImage();
        try {
            baseBitmap = Bitmap.createBitmap(imageShow.getWidth(), imageShow.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
        } catch (Exception e) {
            baseBitmap = Bitmap.createBitmap(imageShow.getWidth(), imageShow.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
        }

        canvas.drawColor(Color.WHITE);
        //这个是背景图
        canvas.drawBitmap(bitmap,left,rigth, paint);
        imageShow.setImageBitmap(baseBitmap);
    }

    float left=0; //bitmap显示的位置
    float rigth=0;
    int cw; //image在view上绘制的真实宽高度
    int ch;


    //获取bitmap显示在ImageView上时的缩放比例
    private void getScaledImage( ){
        //获取imageview的宽和高
        Log.d(TAG, "ImageView width: "+imageShow.getWidth()+"   ImageView Heigth :  "+imageShow.getHeight());

        //获取ImageVIew中Image的真实宽高
        int dw=imageShow.getDrawable().getBounds().width();
        int dh=imageShow.getDrawable().getBounds().height();
        Log.d(TAG, "image 的真实宽：: "+dw+"   image的真实高 ： "+dh);
//
        //获得Imageview中image 的变换矩阵
        Matrix matrix = imageShow.getImageMatrix();
        float[] values = new float[10];
        matrix.getValues(values);
        //image 在绘制过程中的变换矩阵，从中获取x和y方向的缩放系数
        float sx=values[0];
        float sy = values[4];
        Log.d(TAG, "x方向的缩放系数: "+sx+"  y 方向的缩放系数：  "+sy);

        //image在ImageView上绘制的实际高度
        cw=(int )(dw*sx);
        ch = (int) (dh * sy);


        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        left=(imageShow.getWidth()-bitmap.getWidth())/2;
        rigth=(imageShow.getHeight()-bitmap.getHeight())/2;
        Log.d(TAG, "image 在ImageView上绘制的实际高度 x: "+cw+"  y  : "+ch);
//            }
//        });
    }

    //刷新 重绘
    private void  refresh() {
        if (bitmap==null){
//            Toast.makeText(this, "暂时不用刷新", Toast.LENGTH_SHORT).show();
            return;
        }
        if(listLine!=null){
            listLine.clear();
        }else {

        }
        if (baseBitmap!=null){
            baseBitmap = Bitmap.createBitmap(imageShow.getWidth(), imageShow.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap,left,rigth,paint);
            path.reset();
            list.clear();
            length=0;
            area=0;
            imageShow.setImageBitmap(baseBitmap);
//            Toast.makeText(this, "正在刷新", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBitmap( Context context) {
        if(bitmap==null){
            Toast.makeText(context, R.string.print_patient_select, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isSave){
            isSave=false;
        }else {
            Toast.makeText(this, R.string.setting_picture_saved, Toast.LENGTH_SHORT).show();
            return;
        }
        if (baseBitmap == null) {
            Toast.makeText(this, R.string.print_patient_select, Toast.LENGTH_SHORT).show();
            return;
        }

        bitmap02 = Bitmap.createBitmap(baseBitmap, (int)left, (int)rigth,bitmap.getWidth(),bitmap.getHeight() );
        float scalew = bitmap01.getWidth() / bitmap02.getWidth();
        float scaleh = bitmap01.getHeight() / bitmap02.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scalew, scaleh);
        bitmap02 = Bitmap.createBitmap(bitmap02, 0, 0, bitmap02.getWidth(), bitmap02.getHeight(),matrix,true);

        user=new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
        initSelect(OneItem.getOneItem().getUserId());
        appDir = new File(gatherPath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        getFiles(appDir.toString());


        new Thread(new Runnable() {
            @Override
            public void run() {
                //找到文件夹之后，指定文件的名字
                String fileName = System.currentTimeMillis() + ".png";
                File file = new File(appDir, fileName);
                Log.d("saveBitmap", "saveBitmap: " + "--------1-------");
                //将文件转化成输出流

                FileOutputStream steam = null;
                try {
                    steam = new FileOutputStream(file);
                    bitmap02.compress(Bitmap.CompressFormat.PNG, 100, steam);

                    Log.d(TAG1, "run: i  :" + i);
                    Log.d("saveBitmap", "saveBitmap: " + "--------2-------");
                    steam.flush();
                    steam.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
         imageShow.setBackgroundResource(R.drawable.kb);
        Toast.makeText(context, R.string.setting_picture_save_success, Toast.LENGTH_SHORT).show();

    }

    //查找文件夹下图片的数量\

    public void getFiles(String string){
//        Toast.makeText(this, "length:"+string, Toast.LENGTH_SHORT).show();
        File file = new File(string);
        File[] files = file.listFiles();
        i=0;
        for (int j = 0; j < files.length; j++) {
            String name = files[j].getName();
            if (files[j].isDirectory()) {
                String dirPath = files[j].toString().toLowerCase();
                System.out.println(dirPath);
                getFiles(dirPath + "/");
            } else if (files[j].isFile() & name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".bmp") || name.endsWith(".gif") || name.endsWith(".jpeg")) {
                Log.d(TAG1,"FileName===" + files[j].getName());
                i++;
                Log.d(TAG, "getFiles:i "+i);
            }
        }
    }

    //权限的监听事件
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    openAlbum();
                } else {
                    Toast.makeText(this, R.string.setting_permission_closed, Toast.LENGTH_SHORT).show();
                }

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveBitmap(this);
                } else {
                    Toast.makeText(this, R.string.setting_permission_closed, Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    wifiAutoConnectManager.connect(Const.nameHP,Const.passHP,Const.typeWifi);

                }else {
                    Toast.makeText(this,R.string.setting_permission_closed, Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }


    //显示图片
    private void displayImage(String imagePath) {

        if (imagePath != null) {
            list.clear();
            length=0;
            area=0;
            bitmap = BitmapFactory.decodeFile(imagePath);
            bitmap01 = bitmap;
        } else {
            Toast.makeText(this,R.string.setting_picture_show_faild, Toast.LENGTH_SHORT).show();
        }

    }

    ///计算直线的长度
    private float lineLength(List<Points>point){
//        Toast.makeText(this, "集合："+point.size(), Toast.LENGTH_SHORT).show();
        float X=point.get(1).getX()-point.get(0).getX();
        float Y=point.get(1).getY()-point.get(0).getY();
        double length = Math.sqrt(Math.pow(X,2)+Math.pow(Y,2));
        Log.d(TAG, "lineLength: "+point.get(0).getX()+":   : "+point.get(1).getX());
        Log.d(TAG, "lineLength: "+point.get(0).getY()+":   : "+point.get(1).getY());
        return (float)length;
    }

    //计算图形的面积
    private float caculate(List<Points>point){
//        Toast.makeText(this, "集合："+point.size(), Toast.LENGTH_SHORT).show();
        float temp=0;

        for (int i=0;i<list.size();i++){
            if (i<list.size()-1){
                Points p1 = list.get(i);
                Points p2 = list.get(i+1);
                temp += p1.getX() * p2.getY() - p2.getX() * p1.getY();

            }else {
                Points pn = list.get(i);
                Points p0 = list.get(0);
                temp += pn.getX() * p0.getY() - p0.getX() * pn.getY();
            }
        }
        //顺时针画的坐标点是正数，逆时针画的坐标点的得到的面积是负数
        temp=temp/2;
        List<Float> area = getScreenSizeOfDevice();
        temp=(temp/area.get(0))*area.get(1)*2.54f;
        Log.d(TAG, "画图的面积 : "+temp);
        return Math.abs(temp);
    }

    private List<Float> getScreenSizeOfDevice(){
        List<Float> list02 = new ArrayList<Float>();
        Point point = new Point();
        this.getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float area = point.x * point.y;//得到像素面积，
        Log.d(TAG, "屏幕的总像素面积: "+area);
        list02.clear();
        list02.add(area);
        double x = Math.pow(point.x / dm.xdpi, 2);
        double y = Math.pow(point.y/ dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);//得到尺寸大小
        list02.add((float)(x * y));//得到手机屏幕的物理面积
        Log.d(TAG, "屏幕尺寸大小: "+screenInches);
        return list02;
    }
}
