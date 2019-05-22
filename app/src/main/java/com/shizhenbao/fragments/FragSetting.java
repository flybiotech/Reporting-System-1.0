package com.shizhenbao.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.UI.CustomConfirmDialog;
import com.shizhenbao.activity.GuanliyonghuActivity;
import com.shizhenbao.activity.GuanyuActivity;
import com.shizhenbao.activity.HelpActivity;
import com.shizhenbao.activity.JianjiActivity;
import com.shizhenbao.activity.LoginActivity;
import com.shizhenbao.activity.WIFITestActivity;
import com.shizhenbao.activity.WifiSettingActivity;
import com.shizhenbao.activity.YinjianActivity;
import com.shizhenbao.activity.base.XiugaiPasswordActivity;
import com.shizhenbao.adapter.CommonAdapter;
import com.shizhenbao.adapter.SettingAdapter;
import com.shizhenbao.adapter.ViewHolder;
import com.shizhenbao.connect.DevConnect;
import com.shizhenbao.constant.DeviceOfSize;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.Path;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.service.BackupService;
import com.shizhenbao.service.RecoverService;
import com.shizhenbao.updataapk.Updater;
import com.shizhenbao.updataapk.UpdaterConfig;
import com.shizhenbao.util.AdvancedSettingUtils;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.DeleteDataUtils;
import com.shizhenbao.util.DeleteUtils;
import com.shizhenbao.util.FTP;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.MyProgressDialog;
import com.shizhenbao.util.NetworkUtils;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.RecoveryUtils;
import com.shizhenbao.util.TermAddUtils;
import com.shizhenbao.util.ZipUtil;
import com.util.AlignedTextUtils;
import com.util.FileUtil;
import com.util.SouthUtil;
import com.view.LoadingDialog;
import com.view.MyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;


/**
 * Created by dell on 2017/5/22.
 */

public class FragSetting  extends BaseFragment implements View.OnClickListener,NetworkUtils.NetWorkTestResult,BackupService.UpLoadFileProcess, RecoverService.DownloadResult {

    private double screenInches;//屏幕的尺寸
    private TextView title_text;
    private ListView lv;
    private List<String> list;
    private Button bt_right;
    Doctor doctor=null;
    boolean i=true;
    int mItemLayoutId;//adapter的xml文件的id
    private static final String TAG = "TAG_FragSetting";
    private String localsn="";
    private BackupsUtils backupsUtils;
    private RecoveryUtils recoveryUtils;
    private AdvancedSettingUtils advancedSettingUtils;//高级设置工具
    private DeleteDataUtils deleteDataUtils;//删除工具
    private TermAddUtils termAddUtils;//术语添加工具
    private List<String>print_list;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠

        screenInches = DeviceOfSize.getScreenSizeOfDevice2(getActivity());
        View view=inflater.inflate(R.layout.frag_layout_setting, container, false);
        init(view);
        if(OneItem.getOneItem().getName()!=null&&!OneItem.getOneItem().getName().equals("")){
            doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
        }else {
            new UserManager().getExceName();
            doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
        }
        if (doctor != null) {
            i=doctor.isdAdmin();
        }
        if(i){//普通用户。默认就是普通用户
            initData();
            initItemClick();
        }else {
            initDataAdmin();//超级用户
            initItemClickAdmin();
        }
        return view;
    }

    @Override
    public void onStart() {//得到本机SN号
        super.onStart();
        List<SystemSet> systemSet=LitePal.findAll(SystemSet.class);
        for(int i=0;i<systemSet.size();i++){
            localsn=systemSet.get(0).getLocal_svn();
            Const.sn=localsn;
        }
        if(Const.context == null){
            Const.context = getContext();
        }
//        WifiReceiver.setWifiConnectionListener(this);
    }
    private void initItemClick() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=null;
                switch (position) {
                    case 0:
                        Const.isSave=true;
                        intent=new Intent(getContext(), LoginActivity.class);//切换用户
                        startActivity(intent);
                        getActivity().finish();
                        break;
                    case 1://用户管理
                        intent=new Intent(getContext(), GuanliyonghuActivity.class);
                        startActivity(intent);
                        break;
                    case 2://修改密码
                        intent=new Intent(getContext(), XiugaiPasswordActivity.class);
                        startActivity(intent);
                        break;
                    case 3://打印机wifi设置，视珍宝wifi设置
                        intent=new Intent(getContext(),WifiSettingActivity.class);
                        intent.putExtra("wifiIndex", 0);
                        startActivity(intent);
                        break;
//                    case 4://wifi联网测试
//                        intent=new Intent(getContext(),WIFITestActivity.class);
//                        startActivity(intent);
//                        break;
                    case 4://添加术语
                        termAddUtils.showTermDialog();
                        break;
                    case 5://高级设置
//                        lv_add();
                        advancedSettingUtils.advancedDialog(advancedSettingUtils.addData());
                        break;
//                    case 7://图片编辑
//                        intent=new Intent(getContext(), JianjiActivity.class);
//                        Const.dialogshow=0;
//                        startActivity(intent);
//                        break;
                    case 6://数据备份
                        backupsUtils.askBackupDialog();
                        break;
                    case 7://数据恢复
                        recoveryUtils.inquiryRecoverDialog();
                        break;
                    case 8:
                        print_list = new ArrayList<>();
                        print_list.add(getString(R.string.setting_print_display));
                        print_list.add(getString(R.string.setting_print_nodisplay));
                        backupsUtils.advancedDialog(print_list);
                        break;
                    case 9://视珍宝硬件参数设置
                        intent=new Intent(getContext(), YinjianActivity.class);
                        startActivity(intent);
                        break;
                    case 10://关于
                        intent=new Intent(getContext(), GuanyuActivity.class);
                        startActivity(intent);
                        break;
                    case 11://帮助
                        intent=new Intent(getContext(), HelpActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void initData() {
        list = new ArrayList<>();
        list.add(getString(R.string.setting_User_switching));
        list.add(getString(R.string.setting_User_management));
        list.add(getString(R.string.setting_Modify_password));
        list.add(getString(R.string.setting_wifi));
//        list.add(getString(R.string.setting_Networking_test));
        list.add(getString(R.string.setting_add_terms));
        list.add(getString(R.string.setting_advanced_setting));
//        list.add(getString(R.string.setting_Picture_editor));
        list.add(getString(R.string.setting_data_backup));
        list.add(getString(R.string.setting_data_recovery));
        list.add(getString(R.string.setting_print_set));
        list.add(getString(R.string.setting_Parameter_configuration));
//        list.add(getString(R.string.setting_removal));
//        list.add(getString(R.string.setting_Version_update));
        list.add(getString(R.string.setting_about_us));
        list.add(getString(R.string.setting_Common_problem));
        list.add("");

        if (screenInches > 6.0) {
            mItemLayoutId = R.layout.listview_item;
        } else {
            mItemLayoutId = R.layout.listview_item_phone;
        }
        /**
         * CommonAdapter 是通用的adapter
         */
        lv.setAdapter(new CommonAdapter<String>(getContext(), list, mItemLayoutId) {
            @Override
            public void convert(ViewHolder helper, String item) {

                helper.setText(R.id.listview_tv_item, item);
//                return null;
            }
        });
        new Item().setListViewHeightBasedOnChildren(lv);
    }
    private void initDataAdmin() {
        list = new ArrayList<>();
        list.add(getString(R.string.setting_User_switching));
        list.add(getString(R.string.setting_Modify_password));
        list.add(getString(R.string.setting_wifi));
        list.add(getString(R.string.setting_Networking_test));
        list.add(getString(R.string.setting_add_terms));
        list.add(getString(R.string.setting_Picture_editor));
        list.add(getString(R.string.setting_data_backup));
        list.add(getString(R.string.setting_print_set));
        list.add(getString(R.string.setting_Parameter_configuration));
//        list.add("蓝牙连接");
//        list.add(getString(R.string.setting_Version_update));
        list.add(getString(R.string.setting_about_us));
        list.add(getString(R.string.setting_Common_problem));
        list.add("");

        SettingAdapter adapter = new SettingAdapter(list,getContext(),screenInches);
        lv.setAdapter(adapter);
        new Item().setListViewHeightBasedOnChildren(lv);
    }

    private void initItemClickAdmin() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=null;

                switch (position) {
                    case 0:
                        Const.isSave=true;
                        intent=new Intent(getContext(), LoginActivity.class);//切换用户
                        startActivity(intent);
                        getActivity().finish();
                        break;
                    case 1://修改密码
                        intent=new Intent(getContext(), XiugaiPasswordActivity.class);
                        startActivity(intent);
                        break;
                    case 2://打印机,视珍宝wifi设置
                        intent=new Intent(getContext(),WifiSettingActivity.class);
                        intent.putExtra("wifiIndex", 0);
                        startActivity(intent);
                        break;
                    case 3://wifi联网测试
                        intent=new Intent(getContext(),WIFITestActivity.class);
                        Const.dialogshow=0;
                        startActivity(intent);
                        break;
                    case 4://添加术语
                        termAddUtils.showTermDialog();
                        break;
                    case 5://图片编辑
                        intent=new Intent(getContext(), JianjiActivity.class);
                        Const.dialogshow=0;
                        startActivity(intent);
                        break;
                    case 6://备份
                        backupsUtils.askBackupDialog();
                        break;
                    case 7:
                        print_list = new ArrayList<>();
                        print_list.add(getString(R.string.setting_print_display));
                        print_list.add(getString(R.string.setting_print_nodisplay));
                        backupsUtils.advancedDialog(print_list);
                        break;
                    case 8://视诊宝硬件参数配置
                        intent=new Intent(getContext(), YinjianActivity.class);
                        startActivity(intent);
                        break;
                    case 9://关于
                        intent=new Intent(getContext(), GuanyuActivity.class);
                        startActivity(intent);
                        break;
                    case 10://帮助
                        intent=new Intent(getContext(), HelpActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void init(View view) {
        myProgressDialog = new MyProgressDialog(getContext());
        OneItem.getOneItem().setTemp(false);
        title_text= (TextView) view.findViewById(R.id.title_text);
        lv = (ListView) view.findViewById(R.id.lv_four);
        bt_right= (Button) view.findViewById(R.id.btn_right);
        title_text.setText(getString(R.string.setting_title)+"("+OneItem.getOneItem().getName()+")");
        bt_right.setVisibility(View.INVISIBLE);
        recoveryUtils = new RecoveryUtils(getContext());
        backupsUtils = new BackupsUtils(getContext());
        NetworkUtils.getNetworkResultListener(this);
        advancedSettingUtils = new AdvancedSettingUtils(getContext());
        deleteDataUtils = new DeleteDataUtils(getContext());
        termAddUtils = new TermAddUtils(getContext());
        BackupService.setUpLoadFileProcessListener(this);
        RecoverService.setDownloadResultListener(this);
    }

    /**
     * 先判断FTP服务器设置是否为空
     */
    public void isFTPNull(int temp){
        List<SystemSet> systemSets = LitePal.findAll(SystemSet.class);
        boolean ftpset = false;
        if(systemSets.size() > 0 ){
            if((systemSets.get(0).getHostName() != null && !systemSets.get(0).getHostName().equals("") )&&( systemSets.get(0).getUserName() != null && !systemSets.get(0).getUserName().equals(""))
                    && (systemSets.get(0).getPassword() != null && !systemSets.get(0).getPassword().equals("")) && (systemSets.get(0).getServerPort()!=0)){
                if(temp == 0){
                    ftpset = showDown();
                    if(ftpset){
                        recoveryUtils.deleteOriginalFile();
                    }
                }else if(temp == 1){
                    ftpset = initSN();
                    if(ftpset){
                        Intent intent = new Intent(getContext(), BackupService.class);
                        getContext().startService(intent);
                    }

                }
            }else {
                recoveryUtils.FTPSetDialog();
            }
        }else {
            recoveryUtils.FTPSetDialog();
        }
    }
    private MyProgressDialog myProgressDialog;
    //判断sn是否为空
    private boolean initSN(){
        myProgressDialog.dialogShow(getContext().getString(R.string.setting_backups_loading));
        if(Const.sn == null || "".equals(Const.sn)){
            Intent intent=new Intent(getActivity(),GuanyuActivity.class);
            getContext().startActivity(intent);
            MyToast.showToast(getContext(),getContext().getString(R.string.setting_local_sn) );
//            Toast.makeText(mContext,mContext.getString(R.string.setting_local_sn) , Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean showDown(){
        myProgressDialog.dialogShow(getContext().getString(R.string.setting_recovery_loading));
        if(Const.sn == null || "".equals(Const.sn)){
            Intent intent=new Intent(getActivity(),GuanyuActivity.class);
            getContext().startActivity(intent);
            MyToast.showToast(getContext(),getContext().getString(R.string.setting_local_sn) );
            return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);

    }

    //网络连接成功
    @Override
    public void NetworkSuccess(int temp) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isFTPNull(temp);
            }
        });

    }


    //网络连接失败
    @Override
    public void NetworkFaild() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.showToast(getContext(),getContext().getString(R.string.setting_network_Unavailability));
            }
        });
    }
    @Override
    public void getUpLoadFileProcessPrecent(double percent) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (percent >= 100) {
                    myProgressDialog.dialogCancel();
                    Const.context = null;
                    MyToast.showToast(getContext(),getString(R.string.setting_backups_success_message));
                }
            }
        });
    }

    @Override
    public void loginOut(boolean outResult) {

    }

    @Override
    public void loginIn(boolean inResult) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!inResult){
                    MyToast.showToast(getContext(),getString(R.string.setting_link_server_faild));
                }
            }
        });
    }

    @Override
    public void getDisConnectResult(boolean isDisConnect) {

    }

    @Override
    public void downloadResult(boolean result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(result){
                    myProgressDialog.dialogCancel();
                    MyToast.showToast(getContext(),getString(R.string.setting_recovery_success_message));
                }else {
                    myProgressDialog.dialogCancel();
                    MyToast.showToast(getContext(),getString(R.string.setting_recovery_faild_message));
                }
            }
        });
    }

    @Override
    public void fileExistence(boolean result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!result){
                    myProgressDialog.dialogCancel();

                    MyToast.showToast(getContext(),getContext().getString(R.string.ftpFileNo));
                }
            }
        });
    }
}