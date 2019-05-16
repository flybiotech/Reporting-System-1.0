package com.shizhenbao.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.UI.ImagePathView;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.DisplayMetricss;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SPUtils;
import com.shizhenbao.wifiinfo.WifiAutoConnectManager;
import com.view.MyToast;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class JianjiActivity extends Activity implements View.OnClickListener, ImagePathView.OnSaveImageListener {
    private Button btn_left, btn_right, refresh, save, album;

    private ImagePathView imageShow;
    private TextView tv_title;

    private List<Item> listItem = new ArrayList<Item>();
    private String imagePath = null;
    private AlertDialog dialogadd = null;//选择患者对话框
    private int num = 0;//判断翻页次数
    private double temp;//患者信息所占页数
    private int usersize = 0;//所有未诊断患者的数量
    private List<Item> list1;//展示数据源
    public int i = 0;

    private static final String TAG = "TAG_11JianjiActivity";

    private final String[] PERMISSION = new String[]{
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//读取设备信息
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_NETWORK_STATE

    };
    public final static int REQUEST_CODE_ASK_CALL_PHONE = 3;
    private String id;
    private DisplayMetricss dm;
    private WifiAutoConnectManager wifiAutoConnectManager;
    public File appDir = null;
    private User user = null;

    private List<User> userList1 = null;
    private List<User> userList2 = new ArrayList<>();
    private int a = 0;//判断是否弹出对话框
    int min = -1;//防止不断重复进入图片展示界面，如果a=1时会重复进入
    private String gatherPath;//图片采集路径
    private String pathnew;
    private double ratioPoint;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {
                if (dialogadd.isShowing()) {
                    dialogadd.dismiss();
                    Toast.makeText(getContext(), R.string.image_dialog_automatic, Toast.LENGTH_SHORT).show();
                    //判断是否可以对sd卡进行操作
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        user = new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
                        initSelect(OneItem.getOneItem().getUserId());
                        appDir = new File(gatherPath);
                        if (!appDir.exists()) {
                            appDir.mkdirs();
                        }
                        Const.nameJianJi = user.getpName();
                        tv_title.setText(getString(R.string.setting_Picture_editor) + "( " + Const.nameJianJi + " )");
                        getImage();
                    } else {
                        Toast.makeText(JianjiActivity.this, R.string.setting_sd, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_jianji);
        init();

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        Intent intent = this.getIntent();
        a = Const.dialogshow;//判断是否选择患者
        pathnew = intent.getStringExtra("msg1");//传递过来的图片路径
        min = Const.imageshow;
        if (a == 0) {
            selectUser(0);
        } else if (a == 1) {
            imageListSearch();
        }

        if (pathnew != null) {
            imagePath = pathnew;

        } else {
            imagePath = new Item().getSD() + "/SZB_save/a.jpg";
        }
        tv_title.setText(getString(R.string.setting_Picture_editor) + "( " + Const.nameJianJi + " )");

    }


    //初始化控件
    private void init() {

        Const.nameJianJi = "";
        btn_left = (Button) findViewById(R.id.btn_left);//返回按钮，，暂时被隐藏了
        btn_right = (Button) findViewById(R.id.btn_right);//这个是菜单按钮
        btn_right.setText(getString(R.string.image_Select_patients));
        tv_title = (TextView) findViewById(R.id.title_text);//这个是标题栏
        save = (Button) findViewById(R.id.btn_jianji_save01);//保存图片的按钮
        refresh = (Button) findViewById(R.id.btn_jianji_refresh01);//刷新图片的按钮
        album = (Button) findViewById(R.id.btn_jianji_album);//获取需要剪辑的图片的按钮
        imageShow = findViewById(R.id.image_jianji_show); //展示需要剪辑的图片
        btn_right.setVisibility(View.VISIBLE);
        dm = new DisplayMetricss(this);//获取屏幕的分辨率
        tv_title.setText(getString(R.string.setting_Picture_editor) + "( " + " )");
        btn_left.setVisibility(View.VISIBLE);

        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        save.setOnClickListener(this);
        refresh.setOnClickListener(this);
        album.setOnClickListener(this);
        imageShow.setOnSaveImageListener(this);

        ratioPoint = getScrInchPer();

        imageShow.post(new Runnable() {
            @Override
            public void run() {
                imageShow.setBitmapPath(imagePath, ratioPoint);
//                displayImage(imagePath);
            }
        });
    }


    private void imageListSearch() {

        try {
            user = new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
            gatherPath = user.getGatherPath() + "/User_cut";
            appDir = new File(gatherPath);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            Const.nameJianJi = user.getpName();
            if (min == -1) {
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
            list1 = new ArrayList<>();
            if (userList1 != null) {
                userList1.clear();
            }

            listItem.clear();
            userList2.clear();
            try {
                if (OneItem.getOneItem().getName() != null) {
                    if (new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()) {
                        userList1 = LitePal.findAll(User.class);
                    } else {
                        userList1 = LitePal.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                    }
                } else {
                    new UserManager().getExceName();
                    if (new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()) {
                        userList1 = LitePal.findAll(User.class);
                    } else {
                        userList1 = LitePal.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                    }
                }
            } catch (Exception e) {
//                    Logger.e("错误剪辑   ....:"+e.getMessage());
                new UserManager().getExceName();
                if (new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()) {
                    userList1 = LitePal.findAll(User.class);
                } else {
                    userList1 = LitePal.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                }
            }
            if (userList1.size() > 0) {
                for (int i = 0; i < userList1.size(); i++) {
                    if (userList1.get(i).getIs_diag() == 1) {
                        userList2.add(userList1.get(i));
                    }
                }
                Const.list = userList2;
                lv_list(yeshu);
            } else {
                MyToast.showToast(this, getString(R.string.image_set_patients_error));
//                    Toast.makeText(this, R.string.image_set_patients_error, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lv_list(int yeshu) {
        list1.clear();
        double userSize = Const.list.size();
        temp = userSize / 5.0;
        Const.usersize = Const.list.size();
        if (temp - yeshu >= 1 || temp - yeshu == temp) {
            if (temp >= 1) {
                for (int j = yeshu * 5; j < yeshu * 5 + 5; j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name) + ":" + userList2.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userList2.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userList2.get(j).getTel();
                    item.setpId(userList2.get(j).getpId());
                    item.setpName(zhanshi);
                    list1.add(item);
                }
            } else {
                for (int j = yeshu * 5; j < userList2.size(); j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name) + ":" + userList2.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userList2.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userList2.get(j).getTel();
                    item.setpId(userList2.get(j).getpId());
                    item.setpName(zhanshi);
                    list1.add(item);
                }
            }

        } else if (temp - yeshu < 1) {
            for (int j = yeshu * 5; j < userList2.size(); j++) {
                Item item = new Item();
                String zhanshi = getString(R.string.patient_name) + ":" + userList2.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userList2.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userList2.get(j).getTel();
                item.setpId(userList2.get(j).getpId());
                item.setpName(zhanshi);
                list1.add(item);
            }
        }
        if (list1.size() >= 1) {
            lv_additem(list1);
        } else if (list1.size() == 0) {
            return;
        } else {
            id = list1.get(0).getpId();
            OneItem.getOneItem().setUserId(list1.get(0).getpId());
            initSelect(id);
            //判断是否可以对sd卡进行操作
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                user = new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
                appDir = new File(gatherPath);
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                Const.nameJianJi = user.getpName();
                getImage();
            } else {
                MyToast.showToast(this, getString(R.string.setting_sd));
//                Toast.makeText(this, R.string.setting_sd, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void lv_additem(final List<Item> list) {
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
        SelectAdapter selectAdapter = new SelectAdapter(this, list);
        listView.setAdapter(selectAdapter);
        selectAdapter.notifyDataSetChanged();
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        dialogadd = new AlertDialog.Builder(this).setTitle(getString(R.string.image_Select_patients)).setView(linearLayoutMain).setNeutralButton(getString(R.string.button_cancel), null).setPositiveButton(getString(R.string.image_next_page), null).setNegativeButton(getString(R.string.image_Previous_page), null).create();
        dialogadd.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button bt_cancle = dialogadd.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button bt_before = dialogadd.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button bt_next = dialogadd.getButton(AlertDialog.BUTTON_POSITIVE);

                bt_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogadd.dismiss();
                        OneItem.getOneItem().setUserId("");
                        num = 0;
                    }
                });
                bt_before.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (temp - num != temp) {
                            if (num > 0) {
                                num--;
                            }
                            dialogadd.dismiss();
                            lv_list(num);
                        } else {
                            MyToast.showToast(getContext(), getString(R.string.image_patients_first));
                        }
                    }
                });
                bt_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        num++;
                        if (temp >= num) {
                            dialogadd.dismiss();
                            lv_list(num);
                        } else {
                            num--;
                            MyToast.showToast(getContext(), getString(R.string.image_patients_last));
                        }
                    }
                });
            }
        });

        dialogadd.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialogadd.show();
        if (list.size() == 1) {
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
                    id = list.get(0).getpId();
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
                id = list.get(arg2).getpId();
                OneItem.getOneItem().setUserId(list.get(arg2).getpId());
                initSelect(id);
                //判断是否可以对sd卡进行操作
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    user = new LoginRegister().getUserName(Integer.parseInt(OneItem.getOneItem().getUserId()));
                    initSelect(OneItem.getOneItem().getUserId());
                    appDir = new File(gatherPath);
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    Const.nameJianJi = user.getpName();
                    tv_title.setText(getString(R.string.setting_Picture_editor) + "( " + Const.nameJianJi + " )");
                    getImage();
                } else {
                    MyToast.showToast(JianjiActivity.this, getString(R.string.setting_sd));
                }
                dialogadd.cancel();
            }
        });
    }


    public void initSelect(String id) {
        try {
            if (!TextUtils.isEmpty(id)) {
                user = new LoginRegister().getUserName(Integer.parseInt(id));
            }
            gatherPath = user.getGatherPath() + "/User_cut";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override //按钮的监听事件
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_right:
                startActivity(new Intent(this, JianJiSearchActivity.class));
                break;

            case R.id.btn_jianji_refresh01://重绘按钮
                if (imageShow != null) {
                    imageShow.reFresh();
                }

                break;
            case R.id.btn_jianji_save01://保存图片的按钮
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                        WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    if (imageShow != null) {
                        imageShow.saveBitmap(gatherPath);
                    }
                }

                break;
            case R.id.btn_jianji_album://获取需要剪辑的图片的按钮
                getImage();
                break;
            case R.id.btn_left:
                OneItem.getOneItem().setJianjiPath("");
                Const.dialogshow = 0;
                finish();
                break;
            default:
                break;
        }
    }

    private void getImage() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0申请权限
            ActivityCompat.requestPermissions(this, PERMISSION, REQUEST_CODE_ASK_CALL_PHONE);
        } else {
            if (appDir != null) {
//                getFiles(appDir.toString());
                Intent intent1 = new Intent(getContext(), ImageManegerActivity.class);
                intent1.putExtra("msg", OneItem.getOneItem().getUserId());
                startActivity(intent1);
                finish();

            } else {
                MyToast.showToast(JianjiActivity.this, getString(R.string.setting_file_null));
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            OneItem.getOneItem().setJianjiPath("");
//            imageShow.setBackgroundResource(R.drawable.kb);
            this.finish();
        }
        return false;
    }


    //权限的监听事件
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    openAlbum();
                } else {
                    MyToast.showToast(JianjiActivity.this, getString(R.string.setting_permission_closed));
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (imageShow != null) {
                        imageShow.saveBitmap(gatherPath);
                    }
                } else {
                    MyToast.showToast(JianjiActivity.this, getString(R.string.setting_permission_closed));
                }
                break;

            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    wifiAutoConnectManager.connect((String) SPUtils.get(this, Const.HP_WIFI_SSID_KEY, ""), (String) SPUtils.get(this, Const.HP_WIFI_PASS_KEY, "12345678"), Const.typeWifi);


                } else {
                    MyToast.showToast(JianjiActivity.this, getString(R.string.setting_permission_closed));
                }

                break;
            default:
                break;
        }
    }


    //得到像素与英寸的比例
    private float getScrInchPer() {
        Point point = new Point();
        this.getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float area = point.x * point.y;//得到像素面积，
        Log.i(TAG, "屏幕的总像素面积: " + area + "  ,point.x = " + point.x + " , point.y = " + point.y);
        float x = (float) Math.pow(point.x / dm.xdpi, 1);
        float y = (float) Math.pow(point.y / dm.ydpi, 1);
        float per = Math.max(x / point.x, y / point.y);
        Log.i(TAG, "getScrInchPer: per = " + per);
        return per;
    }

    @Override
    public void onSaveImageSuccess() {
        MyToast.showToast(JianjiActivity.this, getString(R.string.setting_picture_save_success));
    }

    @Override
    public void onSaveImageFailed(int code) {
        Log.i(TAG, "onSaveImageFailed: code = " + code);

        switch (code) {
            case 0:

                break;
            case 1:
                MyToast.showToast(JianjiActivity.this, getString(R.string.setting_edit_no_more));
                break;

            case 2://图片正在保存的过程中，

                break;

            case 3://该图片已经被保存过了，不能再保存了
                MyToast.showToast(JianjiActivity.this, getString(R.string.setting_picture_saved));
                break;

            case 4:
                MyToast.showToast(JianjiActivity.this, getString(R.string.print_patient_select));
                break;

            default:
                break;

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
