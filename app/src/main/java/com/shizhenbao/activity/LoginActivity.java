package com.shizhenbao.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.activity.base.BaseActivity;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.sharePreferencess.SharedPreferenceText;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.CopypPicturesUtils;
import com.shizhenbao.util.DisplayMetricss;
import com.shizhenbao.util.LoginAccountUtils;
import com.shizhenbao.util.MemoryUtils;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SPUtils;
import com.shizhenbao.util.ScreenUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.util.AlignedTextUtils;
import com.util.SouthUtil;
import com.view.LoadingDialog;
import com.view.MyToast;

import org.litepal.LitePal;
import java.util.List;


public class  LoginActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener,LoginAccountUtils.LoginResults {
    private EditText userName;
    private EditText userPass;
    private TextView register;
    private Button login;
    private CheckBox checkBox;
    private TextView tv_LoginName, tv_LoginPass,title_bar;
    //log输出
    String TAG = "MainActivityTAG";
    private SharedPreferenceText sp;
    private SharedPreferences sharedPreferences;
    private List<Diacrisis> diacrisisList = null;
    private LinearLayout ll_login;
    private LoadingDialog log; //登陆时的dialog
    private RxPermissions rxPermissions;
    private Button down_but;
    private String[] tvDatas;
    private TextView tv_1,tv_2;
    private MemoryUtils memoryUtils;//内存操作工具
    private CopypPicturesUtils copypPicturesUtils;//图片复制工具
    private LoginAccountUtils loginAccountUtils;//登录过的账户工具
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_login);
        initView();
        initTextView();
        initGetIntent();
        initToolClass();
        initClick();
        getWidthDP();
    }

    //得到屏幕的最小宽度，以此作为屏幕适配的依据
    private void getWidthDP(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightPixels = ScreenUtils.getScreenHeight(this);
        int widthPixels = ScreenUtils.getScreenWidth(this);
        float density = dm.density;
        float heightDP = heightPixels / density;
        float widthDP = widthPixels / density;
        float smallestWidthDP;
        if(widthDP < heightDP) {
            smallestWidthDP = widthDP;
        }else {
            smallestWidthDP = heightDP;
        }
        int DP = (int)smallestWidthDP;
        SPUtils.put(LoginActivity.this, Const.smallestWidthDP,DP);
        Log.e(TAG+"_aa",smallestWidthDP+"");
    }

    private void initClick() {
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);
        down_but.setOnClickListener(this);
    }

    private void initGetIntent() {
        Intent intent=getIntent();
        int temp=intent.getIntExtra("msg",0);
        if(temp==1){
            userName.setText("");
            userPass.setText("");
            checkBox.setChecked(false);
        }
        OneItem.getOneItem().setHospital_name("");
        OneItem.getOneItem().setBackUpPath("");
        OneItem.getOneItem().setBackUpNetPath("");
        OneItem.getOneItem().setGather_path("");
    }

    /**
     * 初始化工具类及相关方法
     */
    private void initToolClass() {
        copypPicturesUtils = new CopypPicturesUtils(this);
        memoryUtils = new MemoryUtils(this);
        sp = new SharedPreferenceText(this);
        log = new LoadingDialog(this);
        rxPermissions = new RxPermissions(this);
        LoginAccountUtils.setLoginResultsListener(this);
        loginAccountUtils = new LoginAccountUtils(this,userName,userPass,checkBox);
        loginAccountUtils.initAdministrators();
        loginAccountUtils.accountException();
        loginAccountUtils.initSystemSet();
    }

    //字段对齐
    private void initTextView() {
        tvDatas = new String[]{getString(R.string.user_name),getString(R.string.user_password)};
        tv_1= (TextView) findViewById(R.id.tv_loginname);
        tv_2= (TextView) findViewById(R.id.tv_loginpass);
        TextView []textViews={tv_1,tv_2};
        for (int i=0;i<tvDatas.length;i++) {
            if (textViews[i] != null) {
                textViews[i].setText(AlignedTextUtils.justifyString(tvDatas[i], 2 ));
            }
        }
//        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
//        String spName = sharedPreferences.getString("loginName", null);
//        String spPass = sharedPreferences.getString("loginPass", null);
        try {
            String spName = (String) SPUtils.get(this, "loginName", "");
            String spPass =(String)SPUtils.get(this, "loginPass", "");

            tv_LoginName.setText(AlignedTextUtils.justifyString(tvDatas[0],3));
            tv_LoginPass.setText(AlignedTextUtils.justifyString(tvDatas[1],3));
            if (spName == null || spName.equals("") || spPass == null || spPass.equals("")) {
                userName.setText(spName);
                userPass.setText("");
                checkBox.setChecked(false);
            } else {
                userName.setText(spName);
                userPass.setText(spPass);
                checkBox.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        copypPicturesUtils.initCopyPicture();
        diacrisisList = LitePal.findAll(Diacrisis.class);
        if (diacrisisList.size() <= 0) {
            new UserManager(LoginActivity.this).showTermList();
        }

    }

    private void initView() {
        down_but = (Button) findViewById(R.id.down_but);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        ll_login.requestFocus();//获取焦点
        userName = (EditText) findViewById(R.id.edit_loginName);//登陆的姓名
        userPass = (EditText) findViewById(R.id.edit_loginPass);//密码
        login = (Button) findViewById(R.id.login);//登陆按钮
        register = (TextView) findViewById(R.id.tv_register);//注册按钮
        checkBox = (CheckBox) findViewById(R.id.checkBox_login);//记住密码的CheckBox
        tv_LoginName = (TextView) findViewById(R.id.tv_loginname); //用户名
        tv_LoginPass = (TextView) findViewById(R.id.tv_loginpass); //密码
        title_bar= (TextView) findViewById(R.id.title_bar);
        title_bar.setText(getString(R.string.app_name));

    }

    private void ToActivity(Class<?> activity) {
        Intent it = new Intent(LoginActivity.this, activity);
        startActivity(it);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox_login:
                if (isChecked) {
                    sp.setLoginInfo(userName.getText().toString().trim(), userPass.getText().toString().trim());
                } else {
                    sp.setLoginInfo("", "");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: //登陆之后，跳到下一个界面
                RequestPermission();
//                MyToast.showToast(this,"登陆成功");
                break;
            case R.id.tv_register: //点击注册按钮之后，跳到注册界面
                ToActivity(RegisterActicity.class);
                break;
            case R.id.down_but:
                loginAccountUtils.LoginAccountShow();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            RequestPermission();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 请求权限的方法
     */
    private void RequestPermission(){
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        memoryUtils.isEnoughMemory();
//                        copypPicturesUtils.initCopyPicture();
                        loginAccountUtils.startLogin();
                    } else {
//                        SouthUtil.showToast(LoginActivity.this, getString(R.string.bt_login_permission));
                        MyToast.showToast(LoginActivity.this,getString(R.string.bt_login_permission));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (log != null) {
            log.dismiss();
        }
    }

    @Override
    public void doSDCardPermission() {
        super.doSDCardPermission();
        Logger.e("doSDCardPermission  ");
//        login();//申请完权限之后，就开始登陆
    }

    @Override
    public void startActivity(int activity,LoadingDialog loadingDialog) {
        if(loadingDialog!=null){
            loadingDialog.dismiss();
        }
        if(activity == 0){
            ToActivity(AdminManager.class);
        }else {
            ToActivity(MainActivity.class);
        }
        finish();
    }

    @Override
    public void loginFaild(LoadingDialog loadingDialog) {
        if(loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }
}
